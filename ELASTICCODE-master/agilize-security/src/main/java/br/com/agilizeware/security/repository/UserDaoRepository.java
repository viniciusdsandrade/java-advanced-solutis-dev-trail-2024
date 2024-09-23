package br.com.agilizeware.security.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Profile;
import br.com.agilizeware.model.User;

public interface UserDaoRepository extends CrudAgilizeRepositoryIF<User, Long> {
	
	@Query(" select us from User us where us.username = :username")
	User loadUserByUsername(@Param("username") String username);
	
	@Query(" select us from User us where us.superAdm = true and us.enabled = true and us.credentialsNonExpired = true and us.accountNonLocked = true and us.accountNonExpired = true and (us.dtRemove is null OR us.dtRemove > :dtAtual) ")
	List<User> findUsersAdm(@Param("dtAtual") Date dtAtual);
	
	@Query(" select p from Profile p where p.username = :login")
	Profile loadUserProfileByLogin(@Param("login") String login);
	
	@Query(" select us from User us where (us.dtRemove is null OR us.dtRemove > :dtAtual) and us.accountNonExpired = true and us.accountNonLocked = true and us.enabled = true and us.credentialsNonExpired = true ")
	List<User> findAll(@Param("dtAtual") Date dtAtual);
}