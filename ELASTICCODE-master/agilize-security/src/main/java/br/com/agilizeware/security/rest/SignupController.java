package br.com.agilizeware.security.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

import br.com.agilizeware.enums.TypeDeviceEnum;
import br.com.agilizeware.model.User;
import br.com.agilizeware.security.component.AuthUtil;
import br.com.agilizeware.util.Util;

@Controller
public class SignupController {

	@Autowired
	private AuthUtil authUtil;
	
    private final ProviderSignInUtils signInUtils;

    @Autowired
    public SignupController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository connectionRepository) {
        signInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
    }

    @RequestMapping(value = "/signup")
    public RedirectView signup(WebRequest request) {
        Connection<?> connection = signInUtils.getConnectionFromSession(request);
        TypeDeviceEnum typeDevice = Util.getTypeDeviceAtUserAgent(request.getHeader("user-agent"));
        if (connection != null) {
        	User us = authUtil.authenticProfile(connection, typeDevice);
            signInUtils.doPostSignUp(connection.getDisplayName(), request);
            String url = "/#/signin/logged/"+us.getToken();
            return new RedirectView(url, true);
        }
        return new RedirectView("/", true);
    }
}