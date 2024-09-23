package br.com.agilizeware.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.model.FieldValue;


@Component("fieldValueDaoIf")
public class FieldValueDaoImpl extends DaoAB<FieldValue, Long> {
	
	@Autowired
	public FieldValueDaoImpl(FieldValueDaoRepository repository) {
		super(FieldValue.class, repository);
    }

}
