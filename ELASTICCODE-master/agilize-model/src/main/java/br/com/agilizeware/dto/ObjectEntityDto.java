package br.com.agilizeware.dto;

import java.util.List;

import br.com.agilizeware.model.Entity;
import br.com.agilizeware.model.FieldValue;

public class ObjectEntityDto extends DtoAb {
	
	private static final long serialVersionUID = 4462979709637121150L;
	
	private Entity mainEntity;
	private List<FieldValue> singleAttributes;
	private List<ObjectEntityDto> compositeEntities;
	
	public ObjectEntityDto() {
		super();
	}

	public Entity getMainEntity() {
		return mainEntity;
	}

	public void setMainEntity(Entity mainEntity) {
		this.mainEntity = mainEntity;
	}

	public List<FieldValue> getSingleAttributes() {
		return singleAttributes;
	}

	public void setSingleAttributes(List<FieldValue> singleAttributes) {
		this.singleAttributes = singleAttributes;
	}

	public List<ObjectEntityDto> getCompositeEntities() {
		return compositeEntities;
	}

	public void setCompositeEntities(List<ObjectEntityDto> compositeEntities) {
		this.compositeEntities = compositeEntities;
	}
	
	public String getNmEntity() {
		if(mainEntity != null) {
			return mainEntity.getName();
		}
		return null;
	}
}
