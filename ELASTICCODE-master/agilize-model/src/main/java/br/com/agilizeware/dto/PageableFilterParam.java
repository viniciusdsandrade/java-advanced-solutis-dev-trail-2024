package br.com.agilizeware.dto;

import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.enums.PageableTypePredicateEnum;

public class PageableFilterParam extends DtoAb {

	private static final long serialVersionUID = -1883781932235127736L;

	private String param;
	private String valueParam;
	private Object valueParamMongoDb;
	private FilterOperatorEnum filterOperator;
	private PageableTypePredicateEnum predicateType;
	private PageableTypePredicateEnum predicateRestricBindingTypeParam;
	
	
	public PageableFilterParam() {
	}

	public PageableFilterParam(String param, FilterOperatorEnum filterOperator, String valueParam) {
		super();
		this.param = param;
		this.valueParam = valueParam;
		this.valueParamMongoDb = valueParam;
		this.filterOperator = filterOperator;
		this.predicateType = PageableTypePredicateEnum.FILTER;
	}
	
	public PageableFilterParam(String param, FilterOperatorEnum filterOperator, Object valueParamMongoDb) {
		super();
		this.param = param;
		this.filterOperator = filterOperator;
		this.valueParamMongoDb = valueParamMongoDb;
		this.predicateType = PageableTypePredicateEnum.FILTER;
	}
	
	public PageableFilterParam(String param, FilterOperatorEnum filterOperator, String valueParam, PageableTypePredicateEnum predicateType) {
		super();
		this.param = param;
		this.valueParam = valueParam;
		this.valueParamMongoDb = valueParam;
		this.filterOperator = filterOperator;
		this.predicateType = predicateType;
	}

	public PageableTypePredicateEnum getPredicateType() {
		return predicateType;
	}

	public void setPredicateType(PageableTypePredicateEnum predicateType) {
		this.predicateType = predicateType;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValueParam() {
		return valueParam;
	}

	public void setValueParam(String valueParam) {
		this.valueParam = valueParam;
	}

	public PageableTypePredicateEnum getPredicateRestricBindingTypeParam() {
		return predicateRestricBindingTypeParam;
	}

	public void setPredicateRestricBindingTypeParam(PageableTypePredicateEnum predicateRestricBindingTypeParam) {
		this.predicateRestricBindingTypeParam = predicateRestricBindingTypeParam;
	}

	public FilterOperatorEnum getFilterOperator() {
		return filterOperator;
	}

	public void setFilterOperator(FilterOperatorEnum filterOperator) {
		this.filterOperator = filterOperator;
	}
	
	public Object getValueParamMongoDb() {
		return valueParamMongoDb;
	}

	public void setValueParamMongoDb(Object valueParamMongoDb) {
		this.valueParamMongoDb = valueParamMongoDb;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PageableFilterParam that = (PageableFilterParam) o;

		return param.equals(that.param);

	}

	@Override
	public int hashCode() {
		return param.hashCode();
	}
// /**
	// * @return the isTypeEnum
	// */
	// public Boolean getIsTypeEnum() {
	// return isTypeEnum;
	// }
	//
	// /**
	// * @param isTypeEnum the isTypeEnum to set
	// */
	// public void setIsTypeEnum(Boolean isTypeEnum) {
	// this.isTypeEnum = isTypeEnum;
	// }
	
	public String getNmEntity() {
		return null;
	}

}
