package br.com.agilizeware.security.rest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableFilterParam;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.dto.SortDTO;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.enums.PageableTypePredicateEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.User;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.security.component.AuthUtil;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.TokenUtils;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/agilize/login")
public class UserServiceRest extends ServiceRestEntityAb<User, Long> {
	
	@Autowired
	private UserDaoIf userDaoIf;
	@Autowired
	private AuthUtil authUtil;
	
	@Override
	protected DaoAB<User, Long> definirDao() {
		return null;
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	@ResponseBody
	public RestResultDto<User> authenticate(@RequestBody User user, HttpServletRequest request) {
		User us = authUtil.authenticate(user, null);
		
		RestResultDto<User> res = new RestResultDto<User>();
		res.setSuccess(true);
		res.setData(us);
		return res;
	}
	
	@RequestMapping(value = "/validateToken", method = RequestMethod.POST)
	@ResponseBody
	public RestResultDto<User> validateToken(@RequestBody String authToken, HttpServletRequest request) {
		
		UserDetails userDetails = null;
		String userName = TokenUtils.getUserNameFromToken(authToken);
		if (userName != null) {
			userDetails = this.userDaoIf.loadUserByUsername(userName);
			if(userDetails != null) {
				if (TokenUtils.validateToken(authToken, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource()
							.buildDetails((HttpServletRequest) request));
					SecurityContextHolder.getContext().setAuthentication(
							authentication);
				}
			}
			else {
				throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
			}
		}
		
		if(userDetails == null || !(userDetails instanceof User)) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
		}
		
		User userlogged = (User)userDetails;
		userlogged.setToken(TokenUtils.createToken(userlogged));
		userlogged.setPassword(null);
		
		RestResultDto<User> res = new RestResultDto<User>();
		res.setSuccess(true);
		res.setData(userlogged);
		return res;
	}
	
	//@Override
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<User> save(@RequestBody User record) {
		
		RestResultDto<User> result = new RestResultDto<User>();
		
		//Validando o Usuário a ser salvo
		List<AgilizeException> errors = validateUser(record);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		/*
		Verificando se já existe usuário cadastrado com este cpf/Email. Só pode haver um unico usuário com o mesmo cpf && email.
		Se houver dois usuários,  um com o mesmo Cpf e outro com o mesmo email informado, deve-se invalidar o cadastro e informar que já existe
		a informação no BD.
		*/
		PageableFilterDTO filter = new PageableFilterDTO();
		filter.setSorts(new ArrayList<SortDTO>(1));
		filter.getSorts().add(new SortDTO("id", true));
		filter.setWithLimitPerPage(false);
		filter.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
		filter.getParamsFilter().add(new PageableFilterParam("cpf", 
				FilterOperatorEnum.EQ, record.getCpf(), PageableTypePredicateEnum.OR));
		filter.getParamsFilter().add(new PageableFilterParam("email", 
				FilterOperatorEnum.EQ, record.getEmail(), PageableTypePredicateEnum.OR));
		filter.getParamsFilter().add(new PageableFilterParam("username", 
				FilterOperatorEnum.EQ, record.getUsername(), PageableTypePredicateEnum.OR));
		filter.setRowsPerPage(10000000);
		
		PageableListDTO<User> pages = userDaoIf.findPageable(filter);
		//int count = 0;
		if(Util.isNotNull(pages) && Util.isNotNull(pages.getList()) && pages.getList().iterator().hasNext()) {
			/*Iterator<User> it = pages.getList().iterator();
			User user = null;	
			while(it.hasNext()) {
				count++;
				user = it.next();
			}
			
			if(count == 1) {
				record.setId(user.getId());
			}
			else {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_SAME_USER)));
				return result;
			}*/
			
			//Não permitir o cadastro de alguem que já exista na BD
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_SAME_USER)));
			return result;
		}
		
		String newPassw = Util.decodePassword(record.getPassword());
		newPassw = Util.getPasswordEncoder().encode(newPassw);
		record.setPassword(newPassw);
		
		userDaoIf.save(record);
		User ret = authUtil.authenticate(record, newPassw);
		result.setSuccess(true);
		result.setData(ret);
		return result;
	}
	
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public RestResultDto<Long> delete(@PathVariable("id") Long id) {
		
		if(!Util.isNotNull(id)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		userDaoIf.delete(id);
		RestResultDto<Long> result = new RestResultDto<Long>();
		result.setSuccess(true);
		result.setData(id);
		return result;
	}
	
	private List<AgilizeException> validateUser(User record) {
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(record == null) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.user"));
		}
		else {
			
			if(!Util.isNotNull(record.getName())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.name"));
			}
			if(!Util.isNotNull(record.getCpf())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.cpf"));
			}
			if(!Util.isNotNull(record.getEmail())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.email"));
			}
			if(!Util.isNotNull(record.getPassword())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.password"));
			}
			if(!Util.isNotNull(record.getUsername())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.username"));
			}
			if(!Util.isNotNull(record.getDevice())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.device"));
			}
		}
		return arrays;
	}
	
}
