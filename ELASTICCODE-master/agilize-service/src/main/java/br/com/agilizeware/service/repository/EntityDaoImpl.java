package br.com.agilizeware.service.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.agilizeware.dao.AbstractSpecification;
import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dao.SpecificationBuilder;
import br.com.agilizeware.dao.SpecificationContext;
import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableFilterParam;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.Entity;
import br.com.agilizeware.model.EntityApplication;
import br.com.agilizeware.model.Field;
import br.com.agilizeware.model.FieldValue;
import br.com.agilizeware.model.Relationship;
import br.com.agilizeware.util.Util;

@Component("entityDaoIf")
public class EntityDaoImpl extends DaoAB<Entity, Long> { //implements EntityDaoIf {

	@Autowired
	private FieldDaoRepository fieldRepository;
	@Autowired
	private FieldValueDaoRepository fieldValueRepository;
	@Autowired
	private RelationshipDaoRepository relationshipDaoRepository;
	@Autowired
	private EntityApplicationDaoRepository eaDaoRepository;

	@Autowired
	public EntityDaoImpl(EntityDaoRepository repository) {
		super(Entity.class, repository);
	}

    /*@Autowired
    public EntityDaoImpl(FieldDaoRepository fieldRepository, 
    		FieldValueDaoRepository fieldValueRepository, RelationshipDaoRepository relationshipDaoRepository) {
        this.fieldRepository = fieldRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.relationshipDaoRepository = relationshipDaoRepository;
    }*/
	
	/*@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public Entity save(Entity entity) {
    	return getRepositorio().save(entity);
    }*/
    
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
    public Field saveField(Field field) {
    	return fieldRepository.save(field);
    }
    
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public FieldValue saveFieldValue(FieldValue fieldValue) {
		return fieldValueRepository.save(fieldValue);
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public Relationship saveRelationship(Relationship relationship) {
		return relationshipDaoRepository.save(relationship);
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void deleteAllFieldsForEntity(Long idEntity) {
		List<Field> fields = fieldRepository.findFieldsForEntity(idEntity);
		if(Util.isListNotNull(fields)) {
			for(Field field : fields) {
				fieldRepository.delete(field);
			}
		}
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void deleteApplicationsForEntity(Long idEntity) {
		
		List<EntityApplication> eas = eaDaoRepository.findEntityApplicationForEntity(idEntity);
		if(Util.isListNotNull(eas)) {
			for(EntityApplication ea : eas) {
				eaDaoRepository.delete(ea);
			}
		}
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void saveEntityApplication(Long idEntity, Long idApplication, Long idUser) {
		
		EntityApplication entity = new EntityApplication();
		entity.setApplication(new Application());
		entity.getApplication().setId(idApplication);
		entity.setEntity(new Entity());
		entity.getEntity().setId(idEntity);
		entity.setDtCreate(Util.obterDataHoraAtual());
		entity.setIdUserCreate(idUser);
		
		eaDaoRepository.save(entity);
	}
	
	public List<Entity> findEntitiesForApplication(Long idApplication) {
		return eaDaoRepository.findEntitiesForApplication(idApplication);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageableListDTO<Entity> findPageable(PageableFilterDTO pageableFiltersDTO) {
		// TODO Auto-generated method stub
		AbstractSpecification<Entity> specificationRestrictsParam = null;
		List specificationsFiltersParams = new ArrayList<AbstractSpecification<Entity>>(1);
		if(pageableFiltersDTO.getParamsFilter() != null) {
			for(PageableFilterParam filter : pageableFiltersDTO.getParamsFilter()) {
				if(filter.getParam().equals("applications")) {
					specificationsFiltersParams.add(filterApplications(filter.getValueParam().toString()));
				}
				else {
					specificationsFiltersParams.add(SpecificationBuilder.filter(filter.getParam(), filter.getValueParam()));
				}
			}
		}
		
		Pageable pageableSpec = super.constructPageSpecification(pageableFiltersDTO.getPage(), 
				super.sortByLastPropertyAsc("name"), pageableFiltersDTO.getRowsPerPage());
		
		Page requestedPage = null;

		// amarrar todos criterios com um "AND" ???
		specificationRestrictsParam = SpecificationBuilder.and(specificationsFiltersParams);

		requestedPage = this.repositorio.findAll(specificationRestrictsParam,
				pageableSpec);

		pageableFiltersDTO.setTotalRows((int) requestedPage.getTotalElements());

		PageableListDTO<Entity> pageableListDTO = new PageableListDTO<Entity>();
		pageableListDTO.setList(new PageImpl(requestedPage.getContent()).getContent());
		pageableListDTO.setPageableFilterDTO(pageableFiltersDTO);
		return pageableListDTO;
	}
	
	private AbstractSpecification<Entity> filterApplications(final String ids) {
        
		return new AbstractSpecification<Entity>() {

            @Override
            public Predicate toPredicate(SpecificationContext context, Root<Entity> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {   
            	
            	Join<Entity, EntityApplication> joinEntities = root.join("applications", JoinType.INNER);
            	Join<EntityApplication, Application> joinApp = joinEntities.join("application", JoinType.INNER);
            	return joinApp.get("id").in(ids);
            }
        };
    }
	
	
}
