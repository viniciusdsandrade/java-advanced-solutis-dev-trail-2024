package br.com.agilizeware.asynchronous.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.model.Queue;

@Component("queueDaoIf")
public class QueueDaoImpl extends DaoAB<Queue, Long> { 

	@Autowired
	public QueueDaoImpl(QueueDaoRepository queueDaoRepository) {
		super(Queue.class, queueDaoRepository);
	}

	public List<Queue> findAllUnexecuted() {
		return ((QueueDaoRepository)getRepositorio()).findAllUnexecuted();
	}
	
}
