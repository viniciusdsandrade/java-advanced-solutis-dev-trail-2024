package br.com.agilizeware.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Entity;
import br.com.agilizeware.model.EntityApplication;

public interface EntityApplicationDaoRepository extends CrudAgilizeRepositoryIF<EntityApplication, Long> {

	@Query(" select distinct ea.entity from EntityApplication ea where ea.application.id = :idApplication ")
	List<Entity> findEntitiesForApplication(@Param("idApplication") Long idApplication);
	
	@Query(" select ea from EntityApplication ea where ea.entity.id = :idEntity ")
	List<EntityApplication> findEntityApplicationForEntity(@Param("idEntity") Long idEntity);
}
