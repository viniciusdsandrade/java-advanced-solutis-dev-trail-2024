package br.com.agilizeware.asynchronous.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.asynchronous.repository.QueueDaoImpl;
import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.EmailFrameEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.OrderQueueEnum;
import br.com.agilizeware.enums.QueueEnum;
import br.com.agilizeware.enums.TemplateStandardEmailEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Queue;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/queue")
public class QueueServiceRest extends ServiceRestEntityAb<Queue, Long> {
	
	@Autowired
	private QueueDaoImpl queueDaoIf;
	
	@Override
	protected DaoAB<Queue, Long> definirDao() {
		return queueDaoIf;
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public RestResultDto<Queue> start(@RequestBody Queue queue, HttpServletRequest request) {
		log.info("Solicitação de Serviço Assincrono");
		
		RestResultDto<Queue> result = new RestResultDto<Queue>();
		List<AgilizeException> errors = validateQueue(queue);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));

			log.error("Erros de Validação == ");
			log.error(result.getStrAgilizeExceptionError());
			
			return result;
		}
		
		if(!Util.isNotNull(queue.getPriority())) {
			queue.setPriority(OrderQueueEnum.MINIMAL);
		}
		if(!Util.isNotNull(queue.getHistoric())) {
			queue.setHistoric(false);
		}
		queue.setDtCreate(Util.obterDataHoraAtual());
		if(Util.isMapNotNull(queue.getMapParameters())) {
			try {
				queue.setjSonParameters(RestResultDto.getMapper().writeValueAsString(queue.getMapParameters()));
			}
			catch(IOException ioe) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe)));

				log.error("Erro de IO == ", ioe);
				
				return result;
			}
			
		}
		queue = queueDaoIf.save(queue);
		result.setSuccess(true);
		result.setData(queue);
		
		log.info("--- FIM da Solicitação de Serviço Assincrono");
		
		return result;
	}

	private List<AgilizeException> validateQueue(Queue queue) {
		
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(queue == null) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.record"));
		}
		else {
			
			if(!Util.isNotNull(queue.getIdUserCreate())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.user.create"));
			}
			if(!Util.isNotNull(queue.getApplication()) || !Util.isNotNull(queue.getApplication().getId())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.application"));
			}
			
			if(!Util.isNotNull(queue.getQueue())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.queue"));
			}
			else if(QueueEnum.EXTERNAL.equals(queue.getQueue()) && !Util.isNotNull(queue.getUrl())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.url"));
			}
			else if(QueueEnum.MAIL.equals(queue.getQueue())) {
				try {
					arrays.addAll(validateMailProperties(queue));
				}
				catch(IOException ioe) {
					arrays.add(new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe));
				}
				
			}
		}
		return arrays;
	}
	
	private List<AgilizeException> validateMailProperties(Queue queue) throws IOException {

		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);

		//Convertendo o Json recebido em Map //new ObjectMapper()
		Map<String, Object> record = queue.getMapParameters();
		
		//Validando os dados do Email encaminhado
		if(record == null || record.isEmpty()) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.email"));
		}
		else {
			
			if(!Util.isNotNull(record.get(EmailFrameEnum.ADDRESS.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.mail.to"));
			}
			else if (record.get(EmailFrameEnum.ADDRESS.getName()).toString().length() > EmailFrameEnum.ADDRESS.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.mail.to", 
						EmailFrameEnum.ADDRESS.getLength().toString()));
			}
			
			if (Util.isNotNull(record.get(EmailFrameEnum.SUBJECT.getName())) && 
					record.get(EmailFrameEnum.SUBJECT.getName()).toString().length() > EmailFrameEnum.SUBJECT.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.mail.name", 
						EmailFrameEnum.SUBJECT.getLength().toString()));
			}
			
			if (!Util.isNotNull(record.get(EmailFrameEnum.TEMPLATE_STANDARD.getName())) && 
					!Util.isNotNull(record.get(EmailFrameEnum.TEMPLATE_EXTERNAL.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.mail.template"));
			}
			
			if (Util.isNotNull(record.get(EmailFrameEnum.TEMPLATE_STANDARD.getName()))) {
				if(!Util.isNotNull(Util.onlyNumbers(record.get(EmailFrameEnum.TEMPLATE_STANDARD.getName()).toString()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.mail.template"));
				}
				else {
					try {
						TemplateStandardEmailEnum.findByCode(Integer.valueOf(Util.onlyNumbers(record.get(EmailFrameEnum.TEMPLATE_STANDARD.getName()).toString())));
					}
					catch(ArrayIndexOutOfBoundsException arr) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.mail.template"));
					}
				}
			}
			
			if (Util.isNotNull(record.get(EmailFrameEnum.TEMPLATE_EXTERNAL.getName())) && 
					!Util.isNotNull(Util.onlyNumbers(record.get(EmailFrameEnum.TEMPLATE_EXTERNAL.getName()).toString()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.mail.template"));
			}
			
			if (Util.isNotNull(record.get(EmailFrameEnum.ATTACHMENT.getName())) && 
					!Util.isNotNull(Util.onlyNumbers(record.get(EmailFrameEnum.ATTACHMENT.getName()).toString()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.mail.attachment"));
			}
		}
		
		return arrays;
	}
	
}
