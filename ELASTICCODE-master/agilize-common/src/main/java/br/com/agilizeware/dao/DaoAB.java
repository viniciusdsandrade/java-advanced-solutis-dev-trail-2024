package br.com.agilizeware.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableFilterParam;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.SortDTO;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.enums.PageableTypePredicateEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.EntityIf;
import br.com.agilizeware.util.Functions;
import br.com.agilizeware.util.Util;


public abstract class DaoAB<E extends EntityIf, ID extends Serializable>  {

	protected static final int NUMBER_OF_ROWS_PER_PAGE = 10;
	
	private static final Logger log = LogManager.getLogger(DaoAB.class);
	
	protected E entity;
	protected ID id;
	protected CrudAgilizeRepositoryIF<E, ID> repositorio;
	
	@PersistenceContext
	protected EntityManager em;
	
	public DaoAB(CrudRepository<E, Serializable> pRepostory) {
		super();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DaoAB(Class<E> entity, CrudAgilizeRepositoryIF pRepostory) {
		super();
		try {
			this.entity = entity.newInstance();
			this.repositorio = pRepostory;
		} catch (InstantiationException iae) {
			log.error("ERRO:", iae);
		} catch (IllegalAccessException e) {
			log.error("ERRO:",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public E findOne(Serializable id) {
		
		E entity = ((CrudRepository<E, Serializable>) repositorio).findOne(id);
		if (entity == null) {
			throw new AgilizeException(HttpStatus.NO_CONTENT.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, id.toString(), "Abstrato");
		}
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		List<E> ret = null;
		Iterable<E> it = ((CrudRepository<E, Serializable>) repositorio).findAll();
		if(it != null && it.iterator().hasNext()) {
			 ret = new ArrayList<>(1);
			 it.forEach(ret::add);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public void delete(Serializable id) {
		((CrudRepository<E, Serializable>) repositorio).delete(id);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(isolation=Isolation.READ_COMMITTED, propagation=Propagation.MANDATORY, rollbackFor=Exception.class)
	public E save(E entity) {
		((EntityIf)entity).setDtCreate(Util.obterDataHoraAtual());
		return ((CrudRepository<E, Serializable>) repositorio).save(entity);
	}
	
	public CrudAgilizeRepositoryIF<E, ID> getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(CrudAgilizeRepositoryIF<E, ID> repositorio) {
		this.repositorio = repositorio;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageableListDTO<E> findPageable(PageableFilterDTO pageableFiltersDTO) {

		boolean predicateTypeNull = true;
		if (pageableFiltersDTO.getParamsFilter() != null) {
			for (PageableFilterParam pageableFilterParam : pageableFiltersDTO
					.getParamsFilter()) {
				if (pageableFilterParam.getPredicateType() != null) {
					predicateTypeNull = false;
					break;
				}
			}
		}
		if (predicateTypeNull == false) {
			PageableListDTO<E> pageableList = findPageable2(pageableFiltersDTO);
			return pageableList;
		}

		AbstractSpecification specificationParam = null;
		Sort sorting = null;
		Boolean isSorting = false;
		String sortName = "id";
		if (pageableFiltersDTO.getParamsFilter() != null) {
			for (PageableFilterParam pageableFilterParam : pageableFiltersDTO
					.getParamsFilter()) {

				boolean isDate = false;
				boolean isEnum = false;
				Field field = null;
				Enum objEnum = null;
				try {
					field = entity.getClass().getDeclaredField(
							pageableFilterParam.getParam());
					if (field.getType().isEnum()) {
						isEnum = true;
					}
					if (field.getType().equals(java.util.Date.class)) {
						isDate = true;
					}
				} catch (NoSuchFieldException nse) { 
					log.error(
							"Erro na determinacao da classe do parametro informado para filtro",
							nse);
				} catch (SecurityException se) {
					log.error(
							"Erro na determinacao da classe do parametro informado para filtro",
							se);
				}

				if (isEnum) {
					objEnum = Functions.getEnumByOrdinal(Integer
							.valueOf(pageableFilterParam.getValueParam()),
							field.getType());
				}

				if (isEnum) {
					specificationParam = SpecificationBuilder.filter(
							pageableFilterParam.getParam(), objEnum);
				} else {

					if (isDate) {
						Date dtValue = new Date();
						
						/*if(Util.isNotNull(pageableFilterParam.getValueParam()) && 
								FilterOperatorEnum.BETWEEN.equals(pageableFilterParam.getFilterOperator())) {
							if(pageableFilterParam.getValueParam().indexOf(";") > 0) {*/
						if(Util.isNotNull(pageableFilterParam.getValueParam()) && 
								FilterOperatorEnum.BETWEEN.equals(pageableFilterParam.getFilterOperator()) &&
								pageableFilterParam.getValueParam().indexOf(";") > 0) {
							
								String[] dates = pageableFilterParam.getValueParam().split(";");
								Date dtIni = Util.parseToDate(dates[0]);
								Date dtFim = Util.parseToDate(dates[1]);
								List<AbstractSpecification<EntityIf>> lista = new ArrayList<AbstractSpecification<EntityIf>>();
								lista.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), dtIni, FilterOperatorEnum.GE));
								lista.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), dtFim, FilterOperatorEnum.LE));
								specificationParam = SpecificationBuilder.and(lista);
							//}
							/*else {
								Date dt = Util.parseToDate(pageableFilterParam.getValueParam());
								List<AbstractSpecification<EntityIf>> lista = new ArrayList<AbstractSpecification<EntityIf>>();
								lista.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), dt, pageableFilterParam.getFilterOperator()));
								specificationParam = SpecificationBuilder.and(lista);
							}*/
						}
						else if (!(pageableFilterParam.getValueParam() == null || 
								pageableFilterParam.getValueParam().toLowerCase().equals("notnull"))) {
							try {
								dtValue = new SimpleDateFormat("yyyyMMdd")
										.parse("19640520");
							} catch (ParseException e1) {
							}
							String dt = pageableFilterParam.getValueParam();
							try {
								if (dt != null) {
									dt = dt.replace("Z", "");
									dt = dt.replace("T", " ");
									SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss.000");
									try {
										dtValue = sdf.parse(dt);
									} catch (ParseException e) {
									}
								}
							} catch (Exception ee) {
							}
						}
						else if (pageableFilterParam.getValueParam() == null) {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), null);
							List<AbstractSpecification<EntityIf>> lista = new ArrayList<AbstractSpecification<EntityIf>>();
							lista.add(specificationParam);
							specificationParam = SpecificationBuilder.or(lista);
						} 
						else if (pageableFilterParam.getValueParam().toLowerCase().equals("notnull")) {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), dtValue);
							specificationParam = SpecificationBuilder
									.not(specificationParam);
						} 
						else {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), dtValue,
									FilterOperatorEnum.EQ);
						}
						
					} else { // nao e' data nem enum

						if (pageableFilterParam.getValueParam() == null) {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), null);

							List<AbstractSpecification<EntityIf>> lista = new ArrayList<AbstractSpecification<EntityIf>>();
							lista.add(specificationParam);
							specificationParam = SpecificationBuilder.or(lista);
						} else {
							specificationParam = SpecificationBuilder.like(
									pageableFilterParam.getValueParam(),
									pageableFilterParam.getParam());
						}
					}
				}
			}
		}

		isSorting = true;
		Field fieldSort = null;

		if (pageableFiltersDTO.getSorts() != null) {
			sortName = pageableFiltersDTO.getSorts().get(0).getName();
			Boolean sortOrd = pageableFiltersDTO.getSorts().get(0).getAsc();
			if (sortOrd == null) {
				sortOrd = true;
			}
			if (sortOrd) {
				sorting = sortByLastPropertyAsc(sortName);
			} else {
				sorting = sortByLastPropertyDesc(sortName);
			}
		} else {
			try {
				fieldSort = entity.getClass().getDeclaredField("name");
			} catch (NoSuchFieldException nse) {
				try {
					fieldSort = entity.getClass().getSuperclass()
							.getDeclaredField("name");
				} catch (NoSuchFieldException nse2) {
				} catch (SecurityException e) {}
			} catch (SecurityException e2) {
				try {
					fieldSort = entity.getClass().getSuperclass()
							.getDeclaredField("name");
				} catch (NoSuchFieldException nse) {
				} catch (SecurityException e) {}
			}
			if (fieldSort != null) {
				sorting = sortByLastPropertyAsc("name");
			} else {
				sorting = sortByLastPropertyAsc(sortName);
			}
		}

		Pageable pageableSpec = null;

		if (isSorting) {
			pageableSpec = constructPageSpecification(
					pageableFiltersDTO.getPage(), sorting,
					pageableFiltersDTO.getRowsPerPage());
		} else {
			pageableSpec = constructPageSpecificationWithoutSort(
					pageableFiltersDTO.getPage(),
					pageableFiltersDTO.getRowsPerPage());
		}

		Page requestedPage = this.repositorio.findAll(specificationParam,
				pageableSpec);

		pageableFiltersDTO.setTotalRows((int) requestedPage.getTotalElements());

		PageableListDTO<E> pageableListDTO = new PageableListDTO<E>();
		Iterable<E> lista = new PageImpl(requestedPage.getContent()).getContent();
		pageableListDTO.setList(lista);
		pageableListDTO.setPageableFilterDTO(pageableFiltersDTO);
		return pageableListDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public PageableListDTO<E> findPageable2(PageableFilterDTO pageableFiltersDTO) {
		Date dtValue = null;
		try {
			dtValue = new SimpleDateFormat("yyyyMMdd").parse("19640520");
		} catch (ParseException e1) {
		}

		AbstractSpecification<E> specificationParam = null;
		AbstractSpecification<E> specificationRestrictsParam = null;
		List<AbstractSpecification<E>> specificationsFiltersParams = new ArrayList<AbstractSpecification<E>>();
		List<AbstractSpecification<E>> specificationsFiltersParamsExecutor = new ArrayList<AbstractSpecification<E>>();
		Iterable<E> entities = null;
		Map<PageableTypePredicateEnum, List<AbstractSpecification<E>>> specificationsRestrictsFiltersParams = new LinkedHashMap<PageableTypePredicateEnum, List<AbstractSpecification<E>>>();
		Set<PageableTypePredicateEnum> keysPredicatesRetrictsFiltersParams = null;
		LinkedHashSet<PageableFilterParam> params = pageableFiltersDTO
				.getParamsFilter();
		List<AbstractSpecification<E>> specificationsOR = new ArrayList<AbstractSpecification<E>>();

		String lastParamName = null;
		String sortName = "id";

		for (PageableFilterParam pageableFilterParam : params) {

			boolean isDate = false;
			boolean isEnum = false;
			Enum objEnum = null;

			Field field = Util.getField(entity, pageableFilterParam.getParam());

			if (field.getType().isEnum()) {
				isEnum = true;
			}
			if (field.getType().equals(java.util.Date.class)) {
				isDate = true;
			}

			if (isEnum && Util.isNotNull(pageableFilterParam.getValueParam())) {
				objEnum = Functions.getEnumByOrdinal(
						Integer.valueOf(pageableFilterParam.getValueParam()),
						field.getType());
			}

			if (isDate) {
				
				if (!(pageableFilterParam.getValueParam() == null || 
						pageableFilterParam.getValueParam().toLowerCase().equals("notnull"))) {
					
			          String dt = pageableFilterParam.getValueParam();
			          try {
			            if (dt != null) {
//			              dt = dt.replace("Z", "");
//			              dt = dt.replace("T", " ");
//			              SimpleDateFormat sdf = new SimpleDateFormat(
//			                  "yyyy-MM-dd HH:mm:ss.000");
			              //try {
//			                dtValue = sdf.parse(dt);
			                dtValue = Util.parseToDate(dt);
			              /*} catch (ParseException e) {
			              }*/
			            }
			          } catch (Exception ee) {
			          }
			        }
			}
			
			if(Util.isNotNull(pageableFilterParam.getValueParam()) && 
					FilterOperatorEnum.BETWEEN.equals(pageableFilterParam.getFilterOperator()) &&
					pageableFilterParam.getValueParam().indexOf(";") > 0) {
				
					String[] dates = pageableFilterParam.getValueParam().split(";");
					Date dtIni = Util.parseToDate(dates[0]);
					Date dtFim = Util.parseToDate(dates[1]);
					List<AbstractSpecification<E>> lista = new ArrayList<AbstractSpecification<E>>();
					lista.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), dtIni, FilterOperatorEnum.GE));
					lista.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), dtFim, FilterOperatorEnum.LE));
					specificationParam = SpecificationBuilder.and(lista);
			}

			else if (PageableTypePredicateEnum.FILTER.equals(pageableFilterParam.getPredicateType())) {
				if (isEnum) {
					specificationParam = SpecificationBuilder.filter(
							pageableFilterParam.getParam(), objEnum,
							FilterOperatorEnum.EQ);
				} else {
					if (isDate) {
						if (pageableFilterParam.getValueParam() == null) {
							List<AbstractSpecification<E>> lista = new ArrayList<AbstractSpecification<E>>();
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), null);
							lista.add(specificationParam);
							specificationParam = SpecificationBuilder.or(lista);
						} else if (pageableFilterParam.getValueParam()
								.toLowerCase().equals("notnull")) {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), dtValue);
							specificationParam = SpecificationBuilder
									.not(specificationParam);
						} else {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), dtValue,
									pageableFilterParam.getFilterOperator());
						}
					} else { // nao e' data nem enum
						// ValueParam == NULL
						if (pageableFilterParam.getValueParam() == null) {
							List<AbstractSpecification<E>> lista = new ArrayList<AbstractSpecification<E>>();
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), null);
							lista.add(specificationParam);
							specificationParam = SpecificationBuilder.or(lista);
						} // ValueParam == notnull
						else if (pageableFilterParam.getValueParam()
								.toLowerCase().equals("notnull")) {
							specificationParam = SpecificationBuilder.filter(
									pageableFilterParam.getParam(), 999999999);
							specificationParam = SpecificationBuilder
									.not(specificationParam);
						} // ValueParam == algum valor
						else {
							// FilterOperator nao informado : assume LIKE
							if (pageableFilterParam.getFilterOperator() == null) {
								specificationParam = SpecificationBuilder
										.filter(pageableFilterParam.getParam(),
												"%"
														+ pageableFilterParam
																.getValueParam()
														+ "%", // valor
																// informado
																// "entre"
																// wildcards
												FilterOperatorEnum.LIKE);
							} // FilterOperator informado : assume o informado
								// (ex: EQ)
							else {
								specificationParam = SpecificationBuilder
										.filter(pageableFilterParam.getParam(),
												(pageableFilterParam
														.getFilterOperator()
														.equals(FilterOperatorEnum.LIKE) ? "%"
														+ pageableFilterParam
																.getValueParam()
														+ "%"
														: pageableFilterParam
																.getValueParam()),
												pageableFilterParam
														.getFilterOperator());
							}
						}
					}
				}
			}
			else if(PageableTypePredicateEnum.OR.equals(pageableFilterParam.getPredicateType())) {
				specificationsOR.add(SpecificationBuilder.filter(pageableFilterParam.getParam(), 
						(pageableFilterParam.getFilterOperator().equals(FilterOperatorEnum.LIKE) ? 
								"%" + pageableFilterParam.getValueParam() + "%" : pageableFilterParam.getValueParam()),
						pageableFilterParam.getFilterOperator()));
			}
			
			if(Util.isNotNull(specificationParam)) {
				specificationsFiltersParams.add(specificationParam);
			}

			lastParamName = pageableFilterParam.getParam();
		}
		
		if(Util.isListNotNull(specificationsOR)) {
			specificationsFiltersParams.add(SpecificationBuilder.or(specificationsOR));
		}
		

		Sort sorting = null;
		boolean isSorting = true;
		Field fieldSort = null;

		if (pageableFiltersDTO.getSorts() != null) {
			List<String> sortNameList = new ArrayList<String>();
			for (SortDTO sortDto : pageableFiltersDTO.getSorts()) {
				sortNameList.add(sortDto.getName());
			}
			sorting = new Sort(Sort.Direction.ASC, sortNameList);
			Iterator ordIt = sorting.iterator();
			int iOrd = 0;
			List<Order> orderList = new ArrayList<Order>();
			while (ordIt.hasNext()) {
				Order ord = (Order) ordIt.next();
				if (pageableFiltersDTO.getSorts().get(iOrd).getAsc() == null
						|| pageableFiltersDTO.getSorts().get(iOrd).getAsc()
								.equals(false)) {
					ord = new Order(Sort.Direction.DESC, pageableFiltersDTO
							.getSorts().get(iOrd).getName());
				}
				orderList.add(ord);
				iOrd++;
			}
			sorting = new Sort(orderList);
		} else {
			try {
				fieldSort = entity.getClass().getDeclaredField("name");
			} catch (NoSuchFieldException nse) {
				try {
					fieldSort = entity.getClass().getSuperclass()
							.getDeclaredField("name");
				} catch (NoSuchFieldException nse2) {
				} catch (SecurityException e) {}
			} catch (SecurityException e2) {
				try {
					fieldSort = entity.getClass().getSuperclass()
							.getDeclaredField("name");
				} catch (NoSuchFieldException nse2) {
				} catch (SecurityException e) {}
			}
			if (fieldSort != null) {
				sorting = sortByLastPropertyAsc("name");
			} else {
				sorting = sortByLastPropertyAsc(sortName);
			}
		}
		/*
		 * private Sort sortByLastPropertyAsc(String property) { return new
		 * Sort(Sort.Direction.ASC, property); }
		 * 
		 * private Sort sortByLastPropertyDesc(String property) { return new
		 * Sort(Sort.Direction.DESC, property); }
		 */
		Pageable pageableSpec = null;

		if (isSorting) {
			pageableSpec = constructPageSpecification(
					pageableFiltersDTO.getPage(), sorting,
					pageableFiltersDTO.getRowsPerPage());
		} else {
			pageableSpec = constructPageSpecificationWithoutSort(
					pageableFiltersDTO.getPage(),
					pageableFiltersDTO.getRowsPerPage());
		}

		Page requestedPage = null;

		// amarrar todos criterios com um "AND" ???
		specificationRestrictsParam = SpecificationBuilder
				.and(specificationsFiltersParams);

		requestedPage = this.repositorio.findAll(specificationRestrictsParam,
				pageableSpec);

		pageableFiltersDTO.setTotalRows((int) requestedPage.getTotalElements());

		PageableListDTO<E> pageableListDTO = new PageableListDTO<E>();
		pageableListDTO.setList(new PageImpl(requestedPage.getContent()).getContent());
		pageableListDTO.setPageableFilterDTO(pageableFiltersDTO);
		return pageableListDTO;
	}
	
	public Sort sortByLastPropertyAsc(String property) {
		return new Sort(Sort.Direction.ASC, property);
	}

	private Sort sortByLastPropertyDesc(String property) {
		return new Sort(Sort.Direction.DESC, property);
	}
	
	public Pageable constructPageSpecificationWithoutSort(int pageIndex,
			Integer rowsPerPage) {
		if (rowsPerPage == null) {
			rowsPerPage = NUMBER_OF_ROWS_PER_PAGE;
		}
		Pageable pageSpecification = new PageRequest(pageIndex, rowsPerPage);
		return pageSpecification;
	}
	
	public Pageable constructPageSpecification(int pageIndex, Sort sort,
			Integer rowsPerPage) {
		if (rowsPerPage == null) {
			rowsPerPage = NUMBER_OF_ROWS_PER_PAGE;
		}
		Pageable pageSpecification = new PageRequest(pageIndex, rowsPerPage,
				sort);
		return pageSpecification;
	}

}