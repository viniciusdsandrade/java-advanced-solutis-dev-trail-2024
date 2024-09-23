package br.com.agilizeware.asynchronous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Queue;

public interface QueueDaoRepository extends CrudAgilizeRepositoryIF<Queue, Long> {
	
	@Query(" select q from Queue q where q.executed = false order by q.priority ")
	List<Queue> findAllUnexecuted();
}
