package br.com.agilizeware.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableFilterParam;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.Pojo;
import br.com.agilizeware.dto.SortDTO;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;


public abstract class MongoDaoAB {

	protected static final Logger log = LogManager.getLogger(MongoDaoAB.class);
	
	//@Autowired
	protected MongoDbFactory mongoDbFactory;
	
	public MongoDaoAB(MongoDbFactory factory) {
		super();
		this.mongoDbFactory = factory;
	}
	
	protected Jongo getJongo() {
		return new Jongo(mongoDbFactory.getDb());
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public Pojo save(Pojo doc) {
		doc.setId(Util.generateUuid());
		DBObject dbo = (DBObject)JSON.parse(doc.getjSon()); //(new GsonJsonParser()). 
		dbo.put(Pojo.NM_ID, doc.getId()); 
		if(Util.isNotNull(doc.getSoundex())) {
			dbo.put(Pojo.NM_SOUNDEX, doc.getSoundex());
		}
		if(Util.isNotNull(doc.getNmEntity())) {
			dbo.put(Pojo.NM_ENTITY, doc.getNmEntity());
		}
		MongoCollection mc = getJongo().getCollection(doc.getCollection());
		mc.save(dbo);
		return doc;
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void delete(String id, String collection) {
		MongoCollection mc = getJongo().getCollection(collection);
		mc.remove("{"+Pojo.NM_ID+":#}", id);
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void update(String id, String collection, Map<String, Object> updates) {
		
		StringBuffer sbParams = new StringBuffer("");
		Object[] valueParams = null;
		if(updates != null && !updates.isEmpty()) {
			sbParams.append("{$set: {");
			valueParams = new Object[updates.size()+1];
			DBObject dbo = null;
			int i=0;
			for(String key : updates.keySet()) {
				sbParams.append(key+":#, ");
				if(updates.get(key) instanceof Pojo) {
					dbo = (DBObject)JSON.parse(((Pojo)updates.get(key)).getjSon());
					valueParams[i] = dbo;
				}
				else {
					valueParams[i] = updates.get(key);
				}
				i++;
			}
			sbParams.append("_dtUpdate:#}}");
			valueParams[updates.size()] = Util.obterDataHoraAtual().getTime();
		}
		else {
			valueParams = new Object[1];
			sbParams.append("{$set: {_dtUpdate:#}}");
			valueParams[0] = Util.obterDataHoraAtual().getTime();
		}
		MongoCollection mc = getJongo().getCollection(collection);
		mc.update("{"+Pojo.NM_ID+":#}", id).with(sbParams.toString(), valueParams);
	}
	
	
	public Pojo findOne(String id, String collection) {
		MongoCollection mc = getJongo().getCollection(collection);
		DBObject dbo = mc.findOne("{"+Pojo.NM_ID+":#}", id).as(DBObject.class);
		if (!Util.isNotNull(dbo)) {
			throw new AgilizeException(HttpStatus.NO_CONTENT.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, id, collection);
		}
		
		return getPojo(dbo);
	}

	private Pojo getPojo(DBObject dbo) {
		Pojo ret = new Pojo();
		if(Util.isNotNull(dbo)) {
			ret.setjSon(JSON.serialize(dbo));
			ret.setId(dbo.get(Pojo.NM_ID).toString());
			if(Util.isNotNull(dbo.get(Pojo.NM_SOUNDEX))) {
				ret.setSoundex(dbo.get(Pojo.NM_SOUNDEX).toString());
			}
			if(Util.isNotNull(dbo.get(Pojo.NM_ENTITY))) {
				ret.setNmEntity(dbo.get(Pojo.NM_ENTITY).toString());
			}
		}
		return ret;
	}
	
	public PageableListDTO<Pojo> findPageable(String collection, PageableFilterDTO pageableFiltersDTO) {
		
		StringBuffer sbSort = new StringBuffer("");
		int i=0;
		if (Util.isListNotNull(pageableFiltersDTO.getSorts())) {
			sbSort.append("{");
			for(SortDTO sortDto : pageableFiltersDTO.getSorts()) {
				sbSort.append(sortDto.getName()+": ").append(sortDto.getAsc() ? "1" : "-1")
					.append(i<(pageableFiltersDTO.getSorts().size()-1) ? ", " : "");
				i++;
			}
		}
		if(!Util.isNotNull(sbSort.toString())) {
			sbSort.append("{"+Pojo.NM_ID+": 1");
		}
		sbSort.append("}");
		
		StringBuffer sbFilter = new StringBuffer("");
		Object[] valueParams = null;
		if(pageableFiltersDTO.getParamsFilter() != null && !pageableFiltersDTO.getParamsFilter().isEmpty()) {
			
			valueParams = new Object[pageableFiltersDTO.getParamsFilter().size()];
			sbFilter.append("{");
			i=0;
			for (PageableFilterParam filter : pageableFiltersDTO.getParamsFilter()) {
				
				valueParams[i] = filter.getValueParamMongoDb();
				if(FilterOperatorEnum.EQ.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":#").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.GT.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":{$gt:#}").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.GE.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":{$ge:#}").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.LE.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":{$le:#}").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.LT.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":{$lt:#}").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.IN.equals(filter.getFilterOperator())) {
					sbFilter.append(filter.getParam()+":{$in:#}").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				else if(FilterOperatorEnum.PHONETIC.equals(filter.getFilterOperator())) {
					sbFilter.append(Pojo.NM_SOUNDEX+":#").append(i<(pageableFiltersDTO.getParamsFilter().size()-1) ? ", " : "");
				}
				i++;
			}
			sbFilter.append("}");
		}
		
		MongoCollection mc = getJongo().getCollection(collection);
		Find f;
		if(Util.isNotNull(sbFilter.toString())) {
			f = mc.find(sbFilter.toString(), valueParams).sort(sbSort.toString());
		}
		else {
			f = mc.find().sort(sbSort.toString());
		}
		
		PageableListDTO<Pojo> pageableListDTO = new PageableListDTO<Pojo>(); 
		pageableListDTO.setPageableFilterDTO(pageableFiltersDTO);
		
		MongoCursor<DBObject> cursor;
		if(pageableFiltersDTO.getWithLimitPerPage()) {  
			cursor = f.skip(pageableFiltersDTO.getPage()).limit(pageableFiltersDTO.getRowsPerPage()).as(DBObject.class);
		}
		else {
			cursor = f.as(DBObject.class);
		}
		List<Pojo> listRet = new ArrayList<Pojo>(1);
		if(Util.isNotNull(cursor)) {
			while(cursor.hasNext()) {
				listRet.add(getPojo(cursor.next()));
			}
		}
		pageableListDTO.setList(listRet);
		
		return pageableListDTO;
	}
}