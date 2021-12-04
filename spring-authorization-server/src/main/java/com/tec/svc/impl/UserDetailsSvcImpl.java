package com.tec.svc.impl;

import com.tec.entity.OAuthUser;
import com.tec.repository.OAuthUserRepository;
import com.tec.svc.UserDetailsSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsSvcImpl implements UserDetailsSvc {

    @Autowired
    private transient OAuthUserRepository oAuthUserRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<OAuthUser> opt = oAuthUserRepository.findByUsername(username);
        if(!opt.isPresent() || !opt.get().isEnabled()){
            throw new UsernameNotFoundException("user not found " + username);
        }
        return opt.get();
    }
}
