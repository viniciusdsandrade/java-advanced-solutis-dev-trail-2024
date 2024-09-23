package br.com.agilizeware.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;

/**
 * 
 * Nome: PageableListDTO.java Propósito:
 * <p>
 * Encapsula os dados do paginador quando estiver paginando na lista de
 * resultado diretamente na paginação no banco ou em cache.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * @version: 1.7
 *
 *           Registro de Manutenção: 19/01/2015 08:27:24 - Autor: Tiago de
 *           Almeida Lopes - Responsável: Regis Neves - Criação
 */
public class PageableListDTO<DTO> extends DtoAb {

	/**
	 * Introduzir aqui os comentários necessários para o campo.
	 */
	private static final long serialVersionUID = 2674451009610071375L;

	/**
	 * Atributo pageableFilterDTO.
	 */
	private PageableFilterDTO pageableFilterDTO;

	/**
	 * Atributo list.
	 */
	private Iterable<DTO> list;

	/**
	 * Atributo totalRowsInCachePageable.
	 */
	private Long totalRowsInCachePageable;

	/**
	 * Construtor padrão da classe.
	 */
	public PageableListDTO() {
		super();
	}

	/**
	 * @return Retorna o valor do campo 'pageableFilterDTO'.
	 */
	public PageableFilterDTO getPageableFilterDTO() {
		return pageableFilterDTO;
	}

	/**
	 * @return Retorna o valor do campo 'list'.
	 */
	public Iterable<DTO> getList() {
		return list;
	}

	/**
	 * @param list
	 *            - O valor do campo 'list' a determinar.
	 */
	public void setList(Iterable<DTO> list) {
		this.list = list;
	}

	/**
	 * @param pageableFilterDTO
	 *            - O valor do campo 'pageableFilterDTO' a determinar.
	 */
	public void setPageableFilterDTO(PageableFilterDTO pageableFilterDTO) {
		this.pageableFilterDTO = pageableFilterDTO;
	}

	/**
	 * @return Retorna o valor do campo 'totalRowsInCachePageable'.
	 */
	public Long getTotalRowsInCachePageable() {
		if(totalRowsInCachePageable == null) {
			if(list != null) {
				return list.spliterator().getExactSizeIfKnown();
			}
			return 0L;
		}
		return totalRowsInCachePageable;
	}

	/**
	 * @param totalRowsInCachePageable
	 *            - O valor do campo 'totalRowsInCachePageable' a determinar.
	 */
	public void setTotalRowsInCachePageable(Long totalRowsInCachePageable) {
		this.totalRowsInCachePageable = totalRowsInCachePageable;
	}
	
	public String getNmEntity() {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public List<HashMap> getJsonList() {
		List<HashMap> ret = new ArrayList<HashMap>(1);
		if(this.list != null && list.iterator() != null && list.iterator().hasNext()) {
			Iterator it = list.iterator();
			while(it.hasNext()) {
				Object obj = it.next();
				if(obj instanceof Pojo) {
					Pojo p = (Pojo)obj;
					try {
						ret.add(RestResultDto.getMapper().readValue(p.getjSon(), new TypeReference<HashMap>() {}));
					}catch(IOException ioe) {
						throw new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe);
					}
				}
			}
		}
		return ret;
	}
}
