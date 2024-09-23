/*package br.com.agilizeware.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import br.com.agilizeware.dao.MongoDaoAB;
import br.com.agilizeware.dto.DtoAb;
import br.com.agilizeware.dto.isobrou.ProductDto;

public class EntityMongoDb extends MongoDaoAB<DtoAb> {

	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Autowired
	public EntityMongoDb(Class<DtoAb> doc, MongoTemplate mongoTemplate) {
		super(doc, mongoTemplate);
	}
	
	public MongoDaoAB<ProductDto> getMongoDaoProduct() {
		
		MongoDaoAB<ProductDto> m = this(ProductDto.class, mongoTemplate); 
		
		return new MongoDaoAB<ProductDto>(null, mongoTemplate);
	}

	
	public void filter() {
		
		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		
		Query searchUserQuery = new Query(Criteria.where("username").is("mkyong"));

		// find the saved user again.
		User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
		System.out.println("2. find - savedUser : " + savedUser);

		
	}
	
}
*/