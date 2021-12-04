package com.tec.repository;

import com.tec.entity.OAuthUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository()
public interface OAuthUserRepository extends CrudRepository<OAuthUser, Long> {

    Optional<OAuthUser> findByUsername(String username);
}