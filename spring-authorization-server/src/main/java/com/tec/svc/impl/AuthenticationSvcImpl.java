package com.tec.svc.impl;

import com.tec.entity.LoginAttempt;
import com.tec.entity.OAuthUser;
import com.tec.repository.LoginAttemptRepository;
import com.tec.repository.OAuthUserRepository;
import com.tec.svc.EncryptionSvc;
import com.tec.svc.AuthenticationSvc;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * https://medium.com/takeaway-tech/wiring-your-own-authentication-with-springbootoauth2-a636b7420714
 */
@Slf4j
@Service
public class AuthenticationSvcImpl implements AuthenticationSvc {

    @Autowired
    private transient EncryptionSvc encryptionSvc;

    @Autowired
    private transient OAuthUserRepository oAuthUserRepository;

    @Autowired
    private transient LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private transient HttpServletRequest request;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();

        if(loginAttemptRepository.countAttempts(name, LoginAttemptRepository.MAX_MINUTES) >= LoginAttemptRepository.MAX_ATTEMPTS){
            throw new LockedException(name + " is locked to due to too many failed attempts");
        }

        final Optional<OAuthUser> opt = oAuthUserRepository.findByUsername(name);
        if(!opt.isPresent() || !opt.get().isEnabled()){
            insertLoginAttempt(name, request);
            throw new BadCredentialsException("user not found " + name);
        }

        final String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();
        return getAuthentication(opt.get(), password, request);
    }

    /**
     * save the login attempt
     * @param userName the user name attempting to login
     */
    @Transactional
    protected void insertLoginAttempt(final String userName, final HttpServletRequest request) {
        //TODO why is this getting double tapped???
        if(request.getAttribute(LOGGED_BAD_KEY) == null) {
            final LoginAttempt la = new LoginAttempt();
            la.setUserName(userName);

            final String xfHeader = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
            if (xfHeader == null) {
                la.setIpAddress(request.getRemoteAddr());
            } else {
                la.setIpAddress(xfHeader.split(",")[0]);
            }
            loginAttemptRepository.save(la);
            request.setAttribute(LOGGED_BAD_KEY, Boolean.TRUE);
        }
    }

    @Override
    public Authentication getAuthentication(final OAuthUser oAuthUser, final String password, final HttpServletRequest request) throws BadCredentialsException {
        Authentication auth = null;
            //use strong encryption
        if (StringUtils.isNoneBlank(oAuthUser.getEnhancedPwd()) && encryptionSvc.matches(password, oAuthUser.getEnhancedPwd())) {
            auth = new UsernamePasswordAuthenticationToken(oAuthUser.getUsername(), oAuthUser.getEnhancedPwd(), oAuthUser.getAuthorities());
        } else if (oAuthUser.getPassword().equals(password)) {
            final String hashed = encryptionSvc.hashPassword(password);
            oAuthUser.setEnhancedPwd(hashed);
            oAuthUserRepository.save(oAuthUser);
            auth = new UsernamePasswordAuthenticationToken(oAuthUser.getUsername(), hashed, oAuthUser.getAuthorities());
        } else {
            insertLoginAttempt(oAuthUser.getUsername(), request);
            throw new BadCredentialsException("user not found " + oAuthUser.getUsername());
        }
        loginAttemptRepository.clearUserName(oAuthUser.getUsername());
        return auth;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
