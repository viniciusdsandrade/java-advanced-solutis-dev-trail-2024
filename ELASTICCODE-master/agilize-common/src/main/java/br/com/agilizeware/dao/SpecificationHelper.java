package br.com.agilizeware.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.util.DateUtil;

/**
 * 
* Nome: SpecificationHelper.java
* Propósito: <p> Helper para auxilop na criação das condições esistentes na especificação de query.</p>
* @author  Gestum / LMS <BR/>
* Equipe: Gestum - Software -São Paulo <BR>
* @version: 1.7
*
* Registro de Manutenção: 27/03/2014 17:43:40
*	           - Autor: Tiago de Almeida Lopes
*	           - Responsável: Thiago Monteiro
*	           - Criação.
 */
public final class SpecificationHelper {

    /**
     * Parses input filter fields and depending on it type makes appropriate
     * criteria builder method calls
     *
     * @param cb - criteria builder
     * @return - predicate with filter. For instance "where budget=1"
     * or "where budget in (1,2,3,4)" or "where budget is null"
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
	protected static Predicate makeSubCondition(Path<?> path, CriteriaBuilder cb, Object value, FilterOperatorEnum operator) {
        Predicate predicate = null;

        switch (operator) {

           case EQ :
               if (value == null) {
                   predicate = cb.isNull(path);
               } else {
                   predicate = cb.equal(path, value);
               }
               break;



            case IN :
                Collection values = null;
                // Assume that value is collection or array
                if (value instanceof Collection) {
                    values = (Collection) value;
                } else if (value instanceof Object[]) {
                    values = Arrays.asList((Object[]) value);
                }

                //todo fix
                if (values == null) {
                    System.out.println("Error");
                    return null;
                }

                boolean containsNull = values.contains(null);
                if (containsNull) {
                    predicate = cb.or(path.in(values), cb.isNull(path));
                } else {
                    predicate = path.in(values);
                }
                break;


            //TODO escape all chars???
            case LIKE :
                //TODO fix next
                // Assume that value is string
                predicate = cb.like(path.as(String.class), value.toString());
                break;


            case CONTAINS :
                // Assume that value is string
                predicate = cb.like(path.as(String.class), "%" + value.toString() + "%");
                break;

            case STARTSWITH :
                // Assume that value is string
                predicate = cb.like(path.as(String.class), value.toString() + "%");
                break;

            case ENDSWITH :
                // Assume that value is string
                predicate = cb.like(path.as(String.class), "%" + value.toString());
                break;

            case ISEMPTY :
                PluralAttribute attr = (PluralAttribute) ((Join) path).getAttribute();
                predicate = cb.isEmpty(((Join) path).getParent().get(attr));
                break;


            case ISNOTEMPTY :
                attr = (PluralAttribute) ((Join) path).getAttribute();
                predicate = cb.isNotEmpty(((Join) path).getParent().get(attr));
                break;
                
            case BETWEEN:
            	
            	Date secondDate = DateUtil.highDateTime((Date)value);
            	Date firstDate = DateUtil.lowDateTime((Date)value);
            	
                predicate = cb.between(path.as(Date.class), firstDate, secondDate);
                break;

            default :

                //todo fix assuming
                // Assume that value implements Comparable interface
                Expression<? extends Comparable> pathCmp = (Expression<? extends Comparable>) path;
                Comparable valueCmp = (Comparable) value;

                switch (operator) {
                    case GE :
                        predicate = cb.greaterThanOrEqualTo(pathCmp, valueCmp);
                        break;
                    case LE :
                        predicate = cb.lessThanOrEqualTo(pathCmp, valueCmp);
                        break;
                    case GT :
                        predicate = cb.greaterThan(pathCmp, valueCmp);
                        break;
                    case LT :
                        predicate = cb.lessThan(pathCmp, valueCmp);
                        break;
                }
                break;

        }

        if (predicate == null) {
            System.out.println("Unable to create specification for filter.");
        }

        return predicate;
    }

    /**
     * For now supports only join by id.
     *
     * @param filterField - filter field.
     * @return path
     */
    @SuppressWarnings("rawtypes")
	protected static Path<?> getRelationPath(final SpecificationContext context, Root<?> personRoot, String filterField) {
        Path<?> path = personRoot;
        From lastJoin = personRoot;
        String filterFieldPath = "";
        for (String subField : filterField.split("\\.")) {
            filterFieldPath += filterFieldPath.length() == 0 ? subField : ("." + subField);
            Path pathFromCache = context.relationPathCache.get(filterFieldPath);
            if (pathFromCache != null) {
                path = pathFromCache;
                From joinFromCache = context.relationJoinCache.get(filterFieldPath);
                if (joinFromCache != null) {
                    lastJoin = joinFromCache;
                }
            } else {
                path = path.<String>get(subField);
                // support of ONE_TO_MANY SUPPORT
                if (isPathJoin(path)) {
                    lastJoin = lastJoin.join(subField, JoinType.LEFT);
                    path = lastJoin;
                }
                context.relationPathCache.put(filterFieldPath, path);
                context.relationJoinCache.put(filterFieldPath, lastJoin);
            }
        }
        //if MANY_TO_ONE relation than must have id attribute to make join
        if (isPathJoin(path)) {
            path = path.get("id");
            filterFieldPath += ".id";
            context.relationPathCache.put(filterFieldPath, path);
            context.relationJoinCache.put(filterFieldPath, lastJoin);
        }
        return path;
    }

    /**
     * 
     * Introduzir aqui os comentários necessários para o método.
     * @see insira o 'see" aqui.
     * 
     * @param path
     * @return
     */
    @SuppressWarnings("rawtypes")
	private static boolean isPathJoin(Path<?> path) {
        //todo how to avoid this?
        /*
        if (path instanceof PluralAttributePath) {
            return true;
        }
        */
        if (Collection.class.isAssignableFrom(path.getJavaType()) || path.getJavaType().isArray()) {
            return true;
        }
        Attribute.PersistentAttributeType persistentAttributeType = null;
        if (path.getModel() instanceof SingularAttribute) {
            persistentAttributeType = ((SingularAttribute) path.getModel()).getPersistentAttributeType();
        }
        if (persistentAttributeType == Attribute.PersistentAttributeType.MANY_TO_MANY ||
            persistentAttributeType == Attribute.PersistentAttributeType.MANY_TO_ONE ||
            persistentAttributeType == Attribute.PersistentAttributeType.ONE_TO_MANY ||
            persistentAttributeType == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            return true;
        }
        return false;
    }

}
