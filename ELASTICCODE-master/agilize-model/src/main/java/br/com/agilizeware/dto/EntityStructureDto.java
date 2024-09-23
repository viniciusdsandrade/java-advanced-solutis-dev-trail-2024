package br.com.agilizeware.dto;

import java.util.List;

import br.com.agilizeware.model.Entity;
import br.com.agilizeware.model.Field;
import br.com.agilizeware.model.Relationship;

public class EntityStructureDto extends DtoAb {
	
	private static final long serialVersionUID = -5989430034606286677L;
	
	private Entity mainEntity;
	private List<Field> singleAttributes;
	private List<Relationship> compositeEntities;
	
	public EntityStructureDto() {
		super();
	}

	public Entity getMainEntity() {
		return mainEntity;
	}

	public void setMainEntity(Entity mainEntity) {
		this.mainEntity = mainEntity;
	}

	public List<Field> getSingleAttributes() {
		return singleAttributes;
	}

	public void setSingleAttributes(List<Field> singleAttributes) {
		this.singleAttributes = singleAttributes;
	}

	public List<Relationship> getCompositeEntities() {
		return compositeEntities;
	}

	public void setCompositeEntities(List<Relationship> compositeEntities) {
		this.compositeEntities = compositeEntities;
	}
	
	public String getNmEntity() {
		if(mainEntity != null) {
			return mainEntity.getName();
		}
		return null;
	}
}
