package br.com.agilizeware.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Application;

public interface ApplicationDaoRepository extends CrudAgilizeRepositoryIF<Application, Long> {
	
	@Query(" select a from Application a where a.name = :name ")
	Application findCompleteByName(@Param("name") String name);
	
	/*@Query(" select a from Application a where a.active = :active ")
	List<Application> findApplications(@Param("active") Boolean active);*/
	
	@Query(" select a.id, a.name from Application a where a.active = :active ")
	List<Object[]> findApplications(@Param("active") Boolean active);
}
