package br.com.agilizeware.security.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.Function;
import br.com.agilizeware.util.Util;

@Component("securityDaoIf")
public class SecurityDaoImpl extends DaoAB<Function, Long> { //implements SecurityDaoIf {

	@Autowired
	private ApplicationDaoRepository applicationDaoRepository;
	
	/*@PersistenceContext(unitName="entityManagerFactory")
	private EntityManager em;*/
	
	/*@Autowired
	public SecurityDaoImpl(FunctionDaoRepository functionDaoRepository, ApplicationDaoRepository applicationDaoRepository) {
        this.functionDaoRepository = functionDaoRepository;
        this.applicationDaoRepository = applicationDaoRepository;
    }*/
	
	@Autowired
	public SecurityDaoImpl(FunctionDaoRepository functionDaoRepository) {
		super(Function.class, functionDaoRepository);
	}
	
	@SuppressWarnings("rawtypes")
	public List findRolesForUser(Long idUser) {
		
		StringBuffer strQuerie = new StringBuffer("");
		strQuerie.append(" select r.id, r.name ");
		strQuerie.append(" from role r ");
		strQuerie.append(" where 1=1 ");
		strQuerie.append(" and exists (select 1 from user_role ur ");
		strQuerie.append("     where ur.fk_role = r.id ");
		strQuerie.append("     and ur.fk_user = :user) ");
		strQuerie.append(" or exists (select 1 from ");
		strQuerie.append(" 	user_function uf inner join function_feature ff on ff.fk_function = uf.fk_function ");
		strQuerie.append(" 	where ff.fk_role = r.id ");
		strQuerie.append("     and uf.fk_user = :user) ");
		strQuerie.append(" or exists (select 1 from ");
		strQuerie.append(" 	user_function uf inner join function_feature ff on ff.fk_function = uf.fk_function ");
		strQuerie.append("     inner join feature_role fr on fr.fk_feature = ff.fk_feature ");
		strQuerie.append(" 	   where fr.fk_role = r.id ");
		strQuerie.append("     and uf.fk_user = :user) ");
		strQuerie.append(" or exists (select 1 from "); 
		strQuerie.append(" 	user_group ug inner join function_group fg on fg.fk_groupment = ug.fk_groupment ");
		strQuerie.append(" 	inner join function_feature ff on ff.fk_function = fg.fk_function ");
		strQuerie.append("     where ff.fk_role = r.id ");
		strQuerie.append("     and ug.fk_user = :user) ");
		strQuerie.append(" or exists (select 1 from ");
		strQuerie.append(" 	user_group ug inner join function_group fg on fg.fk_groupment = ug.fk_groupment ");
		strQuerie.append(" 	inner join function_feature ff on ff.fk_function = fg.fk_function ");
		strQuerie.append("     inner join feature_role fr on fr.fk_feature = ff.fk_feature ");
		strQuerie.append(" 	where fr.fk_role = r.id ");
		strQuerie.append("     and ug.fk_user = :user) ");
		strQuerie.append(" group by r.id ");
		strQuerie.append(" order by r.name ");
		
		Query querie = em.createNativeQuery(strQuerie.toString());
		querie.setParameter("user", idUser);
		
		return querie.getResultList();
	}
	
	public Function saveIfNotExistsFunction(String name, Long idUserCreate) {
		Function f = ((FunctionDaoRepository)getRepositorio()).findByName(name);
		if(Util.isNotNull(f) && Util.isNotNull(f.getId())) {
			return f;
		}
		
		StringBuffer strQuerie = new StringBuffer("");
		strQuerie.append(" insert into function (name) value (:name) ");
		Query querie = em.createNativeQuery(strQuerie.toString());
		querie.setParameter("name", name);
		
		querie.executeUpdate();
		
		return ((FunctionDaoRepository)getRepositorio()).findByName(name);
	}
	
	public void saveRolesIfNotExistsForUser(Long idUser, Long idFunction) {
		
		StringBuffer strQuerie = new StringBuffer("");
		strQuerie.append(" insert into function_feature ");
		strQuerie.append(" (fk_function, fk_feature, fk_role, dt_creation, fk_user_creation) ");
		strQuerie.append(" (select :function, null, r.id, :dtAtual, null ");
		strQuerie.append(" from role r ");
		strQuerie.append(" where 1=1 ");
		strQuerie.append(" and not exists (select 1 from "); 
		strQuerie.append(" 	user_function uf inner join function_feature ff on ff.fk_function = uf.fk_function ");
		strQuerie.append(" 	where ff.fk_role = r.id ");
		strQuerie.append("    and uf.fk_user = :user) ");
		strQuerie.append(" and not exists (select 1 from "); 
		strQuerie.append(" 	user_function uf inner join function_feature ff on ff.fk_function = uf.fk_function ");
		strQuerie.append("     inner join feature_role fr on fr.fk_feature = ff.fk_feature ");
		strQuerie.append(" 	where fr.fk_role = r.id ");
		strQuerie.append("     and uf.fk_user = :user) ");
		strQuerie.append(" group by r.id) ");
		
		Query querie = em.createNativeQuery(strQuerie.toString());
		querie.setParameter("user", idUser);
		querie.setParameter("function", idFunction);
		querie.setParameter("dtAtual", new java.sql.Date(Util.obterDataHoraAtual().getTime()));
		
		querie.executeUpdate();
	}
	
	@SuppressWarnings("rawtypes")
	public void saveFunctionForUserIfNotExist(Long idUser, Long idFunction) {
		
		StringBuffer strQuerie = new StringBuffer("");
		strQuerie.append(" select uf.fk_user from user_function uf ");
		strQuerie.append(" where uf.fk_user = :user ");
		strQuerie.append(" and uf.fk_function = :function ");
		
		Query querie = em.createNativeQuery(strQuerie.toString());
		querie.setParameter("user", idUser);
		querie.setParameter("function", idFunction);
		
		List result = querie.getResultList();
		if(!Util.isListNotNull(result)) {
			strQuerie = new StringBuffer("");
			strQuerie.append(" insert into user_function ");
			strQuerie.append(" (fk_user, fk_function, dt_creation, fk_user_creation) ");
			strQuerie.append(" values ");
			strQuerie.append(" (:user, :function, :dtAtual, null) ");
			
			Query querieIns = em.createNativeQuery(strQuerie.toString());
			querieIns.setParameter("user", idUser);
			querieIns.setParameter("function", idFunction);
			querieIns.setParameter("dtAtual", new java.sql.Date(Util.obterDataHoraAtual().getTime()));
			
			querieIns.executeUpdate();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Collection<SimpleGrantedAuthority> findRolesForPathApplication(String appName, String path, Integer httpMethod) {
		
		StringBuffer strQuerie = new StringBuffer("");
		strQuerie.append(" select r.id, r.name ");
		strQuerie.append(" from role r ");
		strQuerie.append(" inner join path_role pr on pr.fk_role = r.id ");
		strQuerie.append(" inner join application_path ap on ap.fk_application = pr.fk_application_path ");
		strQuerie.append(" inner join application app on app.id = ap.fk_application ");
		strQuerie.append(" where 1=1 ");
		strQuerie.append(" and upper(app.name) = :appName ");
		strQuerie.append(" and upper(ap.path) like (:path) ");
		strQuerie.append(" and ap.http_method = :httpMethod ");
		
		Query querie = em.createNativeQuery(strQuerie.toString());
		querie.setParameter("appName", appName);
		querie.setParameter("path", path+"%");
		querie.setParameter("httpMethod", httpMethod);
		
		List<Object[]> results = querie.getResultList();
		
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(1);
		authorities = Collections.synchronizedCollection(authorities);
		if(Util.isListNotNull(results)) {
			for(Object[] obj : results) {
				authorities.add(new SimpleGrantedAuthority(obj[1].toString()));
			}
			
		}
		Collections.unmodifiableCollection(authorities);
		
		return authorities;
	}
	
	public Application findCompleteApplicationByName(String name) {
		return applicationDaoRepository.findCompleteByName(name);
	}
	
	/**
	 * Método que retornará a lista de aplicações ativas.
	 * OBS: Não expor todo o objeto ao cliente
	 * @param active
	 * @return
	 */
	public List<Application> findApplications(Boolean active) {
		//return applicationDaoRepository.findApplications(active);
		
		List<Application> ret = new ArrayList<Application>(1);
		List<Object[]> apps = applicationDaoRepository.findApplications(active);
		if(Util.isListNotNull(apps)) {
			for(Object[] objs : apps) {
				Application app = new Application();
				app.setId(Long.valueOf(objs[0].toString()));
				app.setName(objs[1].toString());
				ret.add(app);
			}
		}
		return ret;
	}

}
