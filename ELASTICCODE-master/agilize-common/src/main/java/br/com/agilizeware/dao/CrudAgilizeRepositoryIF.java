package br.com.agilizeware.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudAgilizeRepositoryIF<T, ID extends Serializable> extends
		CrudRepository<T, ID>, JpaSpecificationExecutor<T> {

}
