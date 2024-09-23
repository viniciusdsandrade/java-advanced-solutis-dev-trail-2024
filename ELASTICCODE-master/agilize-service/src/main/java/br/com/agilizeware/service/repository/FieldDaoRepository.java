package br.com.agilizeware.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Field;

public interface FieldDaoRepository extends CrudAgilizeRepositoryIF<Field, Long> {

	@Query(" select f from Field f where f.entity.id = :idEntity ")
	List<Field> findFieldsForEntity(@Param("idEntity") Long idEntity);
}
