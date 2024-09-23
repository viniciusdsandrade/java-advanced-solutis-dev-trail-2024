package br.com.agilizeware.file.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.model.File;


@Component("fileDaoIf")
public class FileDaoImpl extends DaoAB<File, Long> {
	
	@Autowired
	public FileDaoImpl(FileDaoRepository repository) {
		super(File.class, repository);
    }

	@Override
	public File save(File entity) {
		// TODO Auto-generated method stub
		File f = super.save(entity);
		f.setPathLogical(f.getPathLogical() + f.getId());
		return super.save(f);
	}
	
	

}
