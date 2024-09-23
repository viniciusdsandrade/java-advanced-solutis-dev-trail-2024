package br.com.agilizeware.security.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.agilizeware.model.Function;
import br.com.agilizeware.model.User;
import br.com.agilizeware.security.repository.SecurityDaoImpl;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@Component
public class PrepareInitContainer implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private AppPropertiesService app;
	@Autowired
	private UserDaoIf userDao;
	@Autowired
	private SecurityDaoImpl securityDaoIf;
	
	//public static final String NAME_GROUP_ADM = "Grupo de Administradores";
	
	
	/**
	   * This event is executed as late as conceivably possible to indicate that 
	   * the application is ready to service requests.
	   */
	  @Override
	  @Transactional
	  public void onApplicationEvent(final ApplicationReadyEvent event) {
			System.out.println("---- Inicio PrepareInitContainer -----");
			initializeRolesForSuperAdm();
			System.out.println("---- Fim PrepareInitContainer -----");
	  }

	private void initializeRolesForSuperAdm() {
		
		//Verificando se o papel padrão dos administradores existem
		String nameFuncAdm = app.getPropertyString("name.group.adm");
		Function f = securityDaoIf.saveIfNotExistsFunction(nameFuncAdm, null);
		
		//Obtendo a relação de superAdms
		List<User> usersSuperAdm = userDao.findUsersAdm();
		if(Util.isListNotNull(usersSuperAdm)) {
			for(User us : usersSuperAdm) {
				//salvando as regras que nao existem para o Papel de SuperAdm
				securityDaoIf.saveRolesIfNotExistsForUser(us.getId(), f.getId());
				//salvando o Papel de SuperAdm para o User 
				securityDaoIf.saveFunctionForUserIfNotExist(us.getId(), f.getId());
			}
		}
    }
}
