package br.com.agilizeware.service.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.EntityStructureDto;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.FieldEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Entity;
import br.com.agilizeware.model.EntityApplication;
import br.com.agilizeware.model.Field;
import br.com.agilizeware.model.Relationship;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.service.repository.EntityDaoImpl;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/entity")
@Service
public class EntityServiceRest extends ServiceRestEntityAb<Entity, Long> {
	
	@Autowired
	private EntityDaoImpl entityDaoIf;
	
	
	@Override
	protected DaoAB<Entity, Long> definirDao() {
		return entityDaoIf;
	}
	
	@RequestMapping(value = "/aplications", method = RequestMethod.GET)
    @ResponseBody
    public RestResultDto<Set<Entity>> findByApplications(@RequestParam(required = true, name="parameters") String parameters) {
		Set<Entity> entities = new HashSet<Entity>(1);
		if(Util.isNotNull(parameters)) {
			String[] listId = parameters.split(";");
			for (String id : listId) {
				if(Util.isNotNull(Util.onlyNumbers(id))) {
					entities.addAll(entityDaoIf.findEntitiesForApplication(new Long(Util.onlyNumbers(id))));
				}
			}
		}
		RestResultDto<Set<Entity>> result = new RestResultDto<Set<Entity>>();
		result.setSuccess(true);
		result.setData(entities);
		return result;
	}
	
	@Override	
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Entity> save(@RequestBody Entity record) {
		return saveUpdate(record, 1);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Entity> updateEntity(@PathVariable("id") Long id, @RequestBody Entity record) {
		return saveUpdate(record, 2);
	}
	
	private RestResultDto<Entity> saveUpdate(Entity record, int operation) {
		RestResultDto<Entity> result = new RestResultDto<Entity>();
		List<AgilizeException> errors = validateRequiredEntityFields(record, operation);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		if(operation == 1) {
			record.setDtCreate(Util.obterDataHoraAtual());
		}
		
		List<EntityApplication> apps = new ArrayList<EntityApplication>(record.getApplications());
		record.setApplications(null);
		Entity entSave = entityDaoIf.save(record);
		
		if(operation == 2) {
			entityDaoIf.deleteAllFieldsForEntity(entSave.getId());
			entityDaoIf.deleteApplicationsForEntity(entSave.getId());
		}
		
		for(Field field : record.getFields()) {
			field.setEntity(entSave);
			field.setDtCreate(Util.obterDataHoraAtual());
			entityDaoIf.saveField(field);
		}
		
		for(EntityApplication entApp : apps) {
			entityDaoIf.saveEntityApplication(entSave.getId(), entApp.getApplication().getId(), entSave.getIdUserCreate());
		}
		
		result.setSuccess(true);
		result.setData(entSave);
		return result;
	}
	
	/*@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public RestResultDto delete(@PathVariable("id") Long id) {
		
		if(!Util.isNotNull(id)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		entityDaoIf.deleteApplicationsForEntity(id);
		entityDaoIf.delete(id);
		RestResultDto result = new RestResultDto();
		result.setSuccess(true);
		result.setData(id);
		return result;
	}*/
	
	private List<AgilizeException> validateField(List<Field> fields, int operation) {
		List<AgilizeException> errors = new ArrayList<AgilizeException>(1);
		
		if(!Util.isListNotNull(fields)) {
			errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "list.entity.field.title"));
		}
		else {
			
			for(Field field : fields) {
				
				/*if(operation == 2 && !Util.isNotNull(field.getId())) {
					errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk"));
				}*/
				
				if(!Util.isNotNull(field.getTypeField())) {
					errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.field.type"));
				}
				else {
					if(FieldEnum.RELATIONSHIP.equals(field.getTypeField())) {
						if(!Util.isNotNull(field.getTypeRelation())) {
							errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.relation.type"));
						}
						if(!Util.isNotNull(field.getEntityRef()) || !Util.isNotNull(field.getEntityRef().getId())) {
							errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.entity.ref"));
						}
					}
				}
				if(!Util.isNotNull(field.getName())) {
					errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.field.name"));
				}
				if(!Util.isNotNull(field.getIdUserCreate())) {
					errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.user.create"));
				}
			}
		}
		
		return errors;
	}
	
	private List<AgilizeException> validateRequiredEntityFields(Entity record, int operation) {
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(record == null) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "page.entity.save.title"));
		}
		else {
			
			if(operation == 2 && !Util.isNotNull(record.getId())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk"));
			}
			
			if(!Util.isNotNull(record.getName())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.name"));
			}
			if(!Util.isNotNull(record.getDescription())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.description"));
			}
			if(!Util.isNotNull(record.getIdUserCreate())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.user.create"));
			}
			if(record.getApplications() == null || record.getApplications().isEmpty()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.application"));
			}
			
			arrays.addAll(validateField(record.getFields(), operation));
		}
		return arrays;
	}
	
	
	
	
	
	
	
	
	
	/*@RequestMapping(method = RequestMethod.POST)
    @ResponseBody*/
    public void saveStructure(@RequestBody EntityStructureDto record) {
		
		validateRequiredFields(record, 1);
		
		Entity entSave = entityDaoIf.save(record.getMainEntity());
		
		for(Field field : record.getMainEntity().getFields()) {
			field.setEntity(entSave);
			entityDaoIf.saveField(field);
		}
		
		if(Util.isListNotNull(record.getCompositeEntities())) {
			for(Relationship relation : record.getCompositeEntities()) {
				entityDaoIf.save(relation.getChildren());
				entityDaoIf.saveRelationship(relation);
			}
		}
	}
	
	private void validateRequiredFields(EntityStructureDto record, int operation) {
		
		List<AgilizeException> errors = new ArrayList<AgilizeException>(1);
		if(!Util.isNotNull(record) || !Util.isNotNull(record.getMainEntity())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.record");
		}
		
		//operation == 1 --> Save
		if(operation == 1) {
			
			record.getMainEntity().setFields(record.getSingleAttributes());
			errors.addAll(validateRequiredEntityFields(record.getMainEntity(), operation));
			
			if(Util.isListNotNull(record.getCompositeEntities())) {
				for(Relationship relation : record.getCompositeEntities()) {
					relation.setFather(record.getMainEntity());
					if(!Util.isNotNull(relation.getTypeRelation())) {
						errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.relation.type"));
					}
					if(!Util.isNotNull(relation.getChildren())) {
						errors.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EMPTY_LIST, "lbl.relation.children"));
					}
					else {
						errors.addAll(validateRequiredEntityFields(relation.getChildren(), operation));
					}
				}
			}
		}
		
		if(Util.isListNotNull(errors)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors);
		}
	}
	
	
}