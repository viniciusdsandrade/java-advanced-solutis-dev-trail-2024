package br.com.agilizeware.dto;

import java.util.LinkedHashSet;
import java.util.List;

public class PageableFilterDTO extends DtoAb {

	private static final long serialVersionUID = 2203872366288813541L;

	public PageableFilterDTO() {
	}

	private LinkedHashSet<PageableFilterParam> paramsFilter;
	private int page;
	private Boolean isWithLimitPerPage;
	private Integer rowsPerPage;
	private Integer totalRows;
	private List<SortDTO> sorts;

	public LinkedHashSet<PageableFilterParam> getParamsFilter() {
		return paramsFilter;
	}

	public void setParamsFilter(
			LinkedHashSet<PageableFilterParam> paramsFilter) {
		this.paramsFilter = paramsFilter;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Integer getRowsPerPage() {
		return rowsPerPage != null ? rowsPerPage : 10;
	}

	public void setRowsPerPage(Integer rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public List<SortDTO> getSorts() {
		return sorts;
	}

	public void setSorts(List<SortDTO> sorts) {
		this.sorts = sorts;
	}
	
	public String getNmEntity() {
		return null;
	}

	public Boolean getWithLimitPerPage() {
		return isWithLimitPerPage != null ? isWithLimitPerPage : true;
	}

	public void setWithLimitPerPage(Boolean isWithLimitPerPage) {
		this.isWithLimitPerPage = isWithLimitPerPage;
	}
	
	
}
