package br.com.agilizeware.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.agilizeware.enums.FilterOperatorEnum;

/**
 * 
 * Nome: SpecificationBuilder.java Propósito:
 * <p>
 * Specificação da query a ser gerada pelo JPA.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * @version: 1.7
 * 
 *           Registro de Manutenção: 27/03/2014 17:41:18 - Autor: Tiago de
 *           Almeida Lopes - Responsável: Thiago Monteiro - Criação.
 */
public final class SpecificationBuilder<E> {
	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param filterParams
	 * @return
	 */
	public static <E> AbstractSpecification<E> and(
			final List<AbstractSpecification<E>> filterParams) {
		return new AbstractSpecification<E>() {
			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> personRoot, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				if (filterParams == null || filterParams.size() == 0) {
					// no params, do nothing
					return null;
				}

				Predicate firstPredicate = null;

				for (AbstractSpecification<E> filter : filterParams) {
					Predicate currentPredicate = filter.toPredicate(context,
							personRoot, query, cb);
					if (firstPredicate == null) {
						firstPredicate = currentPredicate;
					} else {
						firstPredicate = cb.and(firstPredicate,
								currentPredicate);
					}
				}

				return firstPredicate;
			}
		};
	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param filterParam
	 * @return
	 */
	public static <E> AbstractSpecification<E> not(
			final AbstractSpecification<E> filterParam) {
		return new AbstractSpecification<E>() {
			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> personRoot, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				if (filterParam == null) {
					// no params, do nothing
					return null;
				}
				return cb.not(filterParam.toPredicate(context, personRoot,
						query, cb));
			}
		};
	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param filterParams
	 * @return
	 */
	public static <E> AbstractSpecification<E> or(
			final List<AbstractSpecification<E>> filterParams) {
		return new AbstractSpecification<E>() {
			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> personRoot, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				if (filterParams == null || filterParams.size() == 0) {
					// no params, do nothing
					return null;
				}

				Predicate firstPredicate = null;

				for (AbstractSpecification<E> filter : filterParams) {
					Predicate currentPredicate = filter.toPredicate(context,
							personRoot, query, cb);
					if (firstPredicate == null) {
						firstPredicate = currentPredicate;
					} else {
						firstPredicate = cb
								.or(firstPredicate, currentPredicate);
					}
				}

				return firstPredicate;
			}
		};
	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param searchTerm
	 * @param param
	 * @return
	 */
	public static <E> AbstractSpecification<E> like(final String searchTerm,
			final String param) {

		return new AbstractSpecification<E>() {

			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> personRoot, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				if (searchTerm == null || searchTerm.length() == 0) {
					// no params, do nothing
					return null;
				}
				String likePattern = getLikePattern(searchTerm);
				return cb.like(cb.lower(personRoot.<String> get(param)),
						likePattern);
			}

			private String getLikePattern(final String searchTerm) {
				StringBuilder pattern = new StringBuilder();
				pattern.append(searchTerm.toLowerCase());
				pattern.append("%");
				return pattern.toString();
			}
		};

	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param field
	 * @param value
	 * @param operator
	 * @return
	 */
	public static <E> AbstractSpecification<E> filter(final String field,
			final Object value, final FilterOperatorEnum operator) {
		return new AbstractSpecification<E>() {
			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (field == null) {
					return null;
				}

				Path<?> path = SpecificationHelper.getRelationPath(context,
						root, field);
				return SpecificationHelper.makeSubCondition(path, cb, value,
						operator);
			}
		};
	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static <E> AbstractSpecification<E> filter(final String field,
			final Object value) {
		return new AbstractSpecification<E>() {
			@Override
			public Predicate toPredicate(SpecificationContext context,
					Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (field == null) {
					return null;
				}

				Path<?> path = SpecificationHelper.getRelationPath(context,
						root, field);

				return SpecificationHelper.makeSubCondition(path, cb, value,
						getDefaultFilterOperator(value));
			}
		};
	}

	/**
	 * 
	 * Defines default operator for filtering in case no operator was specified.
	 * By default it is :
	 * 
	 * EQUAL for single object IN for collection object
	 * 
	 * @param value
	 * @return
	 */
	private static FilterOperatorEnum getDefaultFilterOperator(Object value) {
		boolean isList = isList(value);
		return isList ? FilterOperatorEnum.IN : FilterOperatorEnum.EQ;
	}

	/**
	 * 
	 * Introduzir aqui os comentários necessários para o método.
	 * 
	 * @see insira o 'see" aqui.
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isList(Object value) {
		return value instanceof Object[] || value instanceof Collection;
	}

}
