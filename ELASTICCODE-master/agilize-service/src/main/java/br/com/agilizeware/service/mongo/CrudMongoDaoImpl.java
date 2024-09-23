package br.com.agilizeware.service.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.MongoDaoAB;

@Component("crudMongoDaoIf")
public class CrudMongoDaoImpl extends MongoDaoAB {

	@Autowired
	public CrudMongoDaoImpl(MongoDbFactory mongoDbFactory) {
		super(mongoDbFactory);
	}
}
