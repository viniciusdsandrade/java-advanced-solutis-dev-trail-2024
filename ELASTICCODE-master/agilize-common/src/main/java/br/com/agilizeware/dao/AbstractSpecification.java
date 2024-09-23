package br.com.agilizeware.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * 
* Nome: AbstractSpecification.java
* Propósito: <p> Abstract specification for context supported specifications.
* Also it uses distinct to remove duplications. </p>
* @author  Gestum / LMS <BR/>
* Equipe: Gestum - Software -São Paulo <BR>
* @version: 1.7
*
* Registro de Manutenção: 27/03/2014 17:40:41
*	           - Autor: Tiago de Almeida Lopes
*	           - Responsável: Thiago Monteiro
*	           - Criação.
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

	
	/**
	 * 
	 * Método Sobreescrito - Comentários: 
	 * 
	 * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
	 */
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.distinct(true);
        return toPredicate(new SpecificationContext(), root, query, cb);
    }

    /**
     * 
     * Introduzir aqui os comentários necessários para o método.
     * @see insira o 'see" aqui.
     * 
     * @param context
     * @param root
     * @param query
     * @param cb
     * @return
     */
    public abstract Predicate toPredicate(SpecificationContext context, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

}
