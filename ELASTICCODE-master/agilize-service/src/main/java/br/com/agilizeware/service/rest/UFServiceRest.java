package br.com.agilizeware.service.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.model.UF;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.service.repository.UFDaoImpl;

@RestController
@RequestMapping("/uf")
@Service
public class UFServiceRest extends ServiceRestEntityAb<UF, Long> {
	
	@Autowired
	private UFDaoImpl ufDaoIf;
	
	
	@Override
	protected DaoAB<UF, Long> definirDao() {
		return ufDaoIf;
	}
	

	@RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public RestResultDto<List<UF>> findAllUF() {
		
		List<UF> entities = new ArrayList<UF>(1);
		entities.addAll(ufDaoIf.findAllUF());
		RestResultDto<List<UF>> result = new RestResultDto<List<UF>>();
		result.setSuccess(true);
		result.setData(entities);
		
		return result;
	}
	

	
	
}