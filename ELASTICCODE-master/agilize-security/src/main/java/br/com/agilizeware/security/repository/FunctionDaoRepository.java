package br.com.agilizeware.security.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Function;

public interface FunctionDaoRepository extends CrudAgilizeRepositoryIF<Function, Long> {
	
	@Query(" select f from Function f where f.name = :name ")
	Function findByName(@Param("name") String name);
}
