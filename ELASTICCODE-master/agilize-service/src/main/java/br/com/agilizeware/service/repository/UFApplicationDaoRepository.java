package br.com.agilizeware.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.EntityApplication;
import br.com.agilizeware.model.UF;

public interface UFApplicationDaoRepository extends CrudAgilizeRepositoryIF<UF, Long> {

	@Query("select u from UF u")
	List<UF> findAllUF();
	
}
