package br.com.agilizeware.asynchronous;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import br.com.agilizeware.asynchronous.component.QueueService;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulledConfig {
	
	private static final Logger log = LogManager.getLogger(SchedulledConfig.class);
	
	@Autowired
	private QueueService queueService;

	@Scheduled(fixedDelay=300000)
	public void executeAllQueues() {
		
		log.info("** INI SchedulledConfig | executeAllQueues **");
		try {
			queueService.executeQueues();
		}
		catch(Throwable th) {
			log.error("ERROR");
			log.error(th.getMessage(), th);
		}
		log.info("--- FIM SchedulledConfig | executeAllQueues ---");
	}
}
