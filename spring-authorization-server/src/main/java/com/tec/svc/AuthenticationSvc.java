package com.tec.svc;

import com.tec.entity.OAuthUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * this is needed to allow for transactional processing
 */
public interface AuthenticationSvc extends AuthenticationProvider, AuthenticationManager {

    //request attribute key
    String LOGGED_BAD_KEY = "login_attempt_processed";

    /**
     * expose processing for testing
     * @param oAuthUser the user information
     * @param password the password
     * @param request the current request object
     * @return the auth record
     * @throws BadCredentialsException
     */
    Authentication getAuthentication(final OAuthUser oAuthUser, final String password, HttpServletRequest request) throws BadCredentialsException;
}
