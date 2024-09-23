package br.com.agilizeware.service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.dao.MongoDaoAB;
import br.com.agilizeware.rest.ServiceRestMongoAb;
import br.com.agilizeware.service.mongo.CrudMongoDaoImpl;

@RestController
@RequestMapping("/crud/mongo")
@Service
public class CrudMongoServiceRest extends ServiceRestMongoAb {
	
	@Autowired
	private CrudMongoDaoImpl crudMongoDaoImpl;
	
	@Override
	protected MongoDaoAB definirMongoDao() {
		return crudMongoDaoImpl;
	}
}