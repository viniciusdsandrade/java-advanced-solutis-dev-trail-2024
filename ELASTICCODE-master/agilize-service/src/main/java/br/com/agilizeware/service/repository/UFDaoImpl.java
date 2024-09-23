package br.com.agilizeware.service.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.model.UF;

@Component("ufDaoIf")
public class UFDaoImpl extends DaoAB<UF, Long> { //implements EntityDaoIf {
	
	@Autowired
	private UFApplicationDaoRepository ufDaoRepository;
	
	
	@Autowired
	public UFDaoImpl(UFDaoRepository repository) {
		super(UF.class, repository);
	}

	public List<UF> findAllUF() {
		return ufDaoRepository.findAllUF();
	}

	
	
}
