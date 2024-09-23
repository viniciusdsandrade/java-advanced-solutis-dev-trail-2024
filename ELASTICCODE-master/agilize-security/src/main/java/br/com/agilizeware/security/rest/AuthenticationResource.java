package br.com.agilizeware.security.rest;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.model.Profile;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/api/session")
public class AuthenticationResource {
	
    /*@Autowired
    private AuthenticationManager authenticationManager;*/
	@Autowired
	private UserDaoIf userDaoIf;

    @RequestMapping(method = RequestMethod.GET)
    public Profile session(Principal user) {
    	if(Util.isNotNull(user) && Util.isNotNull(user.getName())) {
    		return userDaoIf.findProfileByUserName(user.getName());
    	}
    	return new Profile();
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
