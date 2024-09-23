package br.com.agilizeware.asynchronous.component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.com.agilizeware.asynchronous.repository.QueueDaoImpl;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.EmailFrameEnum;
import br.com.agilizeware.enums.QueueEnum;
import br.com.agilizeware.enums.TemplateStandardEmailEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.Queue;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@Service
public class QueueService {
	
	private static final Logger log = LogManager.getLogger(QueueService.class);
	private static final String JSON_EXCEPTION = "JsonProcessingException GERADO AO TENTAR CONVERTER A SEGUINTE EXCEÇÃO EM STRING: ";

	@Autowired
	private AppPropertiesService app;
	@Autowired
	private QueueDaoImpl queueDaoIf;
	@Autowired
    private JavaMailSender javaMailSender;
	@Autowired
	private VelocityEngine velocityEngine;

	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public void executeQueues() {
		
		log.info("**** INICIO executeQueues ******");
		
		List<Queue> queues = queueDaoIf.findAllUnexecuted();
		for (Queue queue : queues) {
			
			log.info("Processamento da fila = "+queue.getId());

			queue.setDtExecution(Util.obterDataHoraAtual());
			try {
				
				if(QueueEnum.MAIL.equals(queue.getQueue())) {
					queue = sendEmail(queue);
				}
				else if(QueueEnum.EXTERNAL.equals(queue.getQueue())) {
					queue = callExternalServices(queue);
				}
				
				queue.setExecuted(true);
				queue.setError(null);
			}
			catch(Exception e) {
				
				log.error("Erro = "+e.getMessage(), e);
				
				queue.setCount(queue.getCount()+1);
				if(queue.getCount() == 3) {
					queue.setExecuted(true);
				}
				try {
					queue.setError(RestResultDto.getStrException(e));
				}
				catch(AgilizeException age) {
					queue.setError(JSON_EXCEPTION+e.getMessage());
				}
			}
			
			if(!queue.getHistoric() && queue.getExecuted() && !Util.isNotNull(queue.getError())) {
				queueDaoIf.delete(queue.getId());
			}
			else {
				queueDaoIf.save(queue);
			}
		}

		log.info("**** FIM executeQueues ******");
	}
	
	private Queue sendEmail(Queue queue) throws IOException {

		log.info("---- INICIO DE ENVIO DE EMAIL PARA queue.id = "+queue.getId()+" ----");
		Map<String, Object> mapa = queue.getMapParameters();
		sendConfirmationEmail(mapa);
		log.info("---- FIM DE ENVIO DE EMAIL PARA queue.id = "+queue.getId()+" ----");
		
		return queue;
	}
	
	private Queue callExternalServices(Queue queue) {
		log.info("++++ INICIO DE ENVIO DE CHAMADA EXTERNA PARA queue.id = "+queue.getId()+" ++++");

		String url = queue.getUrl();
		Util.getResult(Util.accessRestService(url, 2, null, RestResultDto.class, queue.getApplication().getPassword(), queue.getMapParameters()));
		
		log.info("++++ FIM DE ENVIO DE CHAMADA EXTERNA PARA queue.id = "+queue.getId()+" ++++");
		
		return queue;
	}
	
	private void sendConfirmationEmail(Map<String, Object> mapJson) {
        
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(mapJson.get(EmailFrameEnum.ADDRESS.getName()).toString());
                message.setFrom(app.getPropertyString("spring.mail.username")); 
                if(Util.isNotNull(mapJson.get(EmailFrameEnum.SUBJECT.getName()))) {
                	message.setSubject(mapJson.get(EmailFrameEnum.SUBJECT.getName()).toString());
                }
                if(Util.isNotNull(mapJson.get(EmailFrameEnum.ATTACHMENT.getName()))) {
                	Long idAttach = Long.valueOf(Util.onlyNumbers(
        					mapJson.get(EmailFrameEnum.ATTACHMENT.getName()).toString()));
        			String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
        			Application appFileServer = Util.getApplication(nmFileServer, ServiceAuthenticationFilter.KEY);
        			String urlFile = appFileServer.getHost() + app.getPropertyString("path.file.by.id") + idAttach;
        			Object obj = Util.accessRestService(urlFile, 5, null, RestResultDto.class, appFileServer.getPassword(), null);
        			if(Util.isNotNull(obj)) {
        				java.io.File file = (java.io.File)obj;
        				message.addAttachment(file.getName(), file);
        			}
                }
                
                String text = prepareRegistrationEmailText(mapJson);
                message.setText(text, true);
            }
        };
        this.javaMailSender.send(preparator);
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String prepareRegistrationEmailText(Map<String, Object> mapJson) {
	    
		VelocityContext context = new VelocityContext();
	    String strArqTemplate = null;
		if (Util.isNotNull(mapJson.get(EmailFrameEnum.TEMPLATE_STANDARD.getName()))) {
			strArqTemplate = TemplateStandardEmailEnum.findByCode(Integer.valueOf(Util.onlyNumbers(
					mapJson.get(EmailFrameEnum.TEMPLATE_STANDARD.getName()).toString()))).getLabel();
		}
		else {
			Long idExternalTemp = Long.valueOf(Util.onlyNumbers(
					mapJson.get(EmailFrameEnum.TEMPLATE_EXTERNAL.getName()).toString()));
			String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
			Application appFileServer = Util.getApplication(nmFileServer, ServiceAuthenticationFilter.KEY);
			String urlFile = appFileServer.getHost() + app.getPropertyString("path.file.by.id") + idExternalTemp;
			Object obj = Util.accessRestService(urlFile, 5, null, RestResultDto.class, appFileServer.getPassword(), null);
			if(Util.isNotNull(obj)) {
				java.io.File file = (java.io.File)obj;
				strArqTemplate = file.getName();
			}
		}
	    
		if (Util.isNotNull(mapJson.get(EmailFrameEnum.PARAMETERS.getName()))) {
			Map<String, Object> mapa = (Collections.synchronizedMap((LinkedHashMap)mapJson.get(EmailFrameEnum.PARAMETERS.getName())));
			if(mapa != null) {
				for(String key : mapa.keySet()) {
					context.put(key, mapa.get(key));
				}
			}
		}
		
	    StringWriter stringWriter = new StringWriter();
	    velocityEngine.mergeTemplate(strArqTemplate, app.getPropertyString("spring.mail.default-encoding"), 
	    		context, stringWriter);
	    String text = stringWriter.toString();
	    return text;
	}
	
}
