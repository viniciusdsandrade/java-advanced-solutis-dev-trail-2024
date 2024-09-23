package br.com.agilizeware.security.repository;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.model.Profile;
import br.com.agilizeware.model.User;
import br.com.agilizeware.model.UserAccess;

public interface UserDaoIf extends UserDetailsService {

	User loadUserByUsername(String username);
	UserAccess saveUserAccess(UserAccess ua);
	List<User> findUsersAdm();
	User retrieveUserLogged();
	PageableListDTO<User> findPageable(PageableFilterDTO pageableFiltersDTO);
	User save(User user);
	
	Profile findProfileByUserName(String login);
	List<User> findAll();
	void createUser(String userId, Profile profile);
	void delete(Long id);
}
