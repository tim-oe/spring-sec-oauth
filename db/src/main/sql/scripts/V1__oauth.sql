CREATE TABLE users (
                       id int(20) UNSIGNED NOT NULL AUTO_INCREMENT,
                       email varchar(128) NOT NULL COMMENT 'Login username',
                       password varchar(32) DEFAULT NULL COMMENT 'raw pwd used for poc',
                       enhanced_pwd varchar(60) DEFAULT NULL COMMENT 'new pwd column for stronger encryption',
                       is_active bit(1) DEFAULT b'1',
                       created_on timestamp NOT NULL DEFAULT current_timestamp(),
                       last_updated timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                       PRIMARY KEY (id),
                       UNIQUE KEY email_UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='application user data';

CREATE TABLE login_attempt (
                               id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               user_name varchar(128) NOT NULL comment 'the user name trying to log in',
                               ip_addr varchar(64) NOT NULL comment 'the client ip address',
                               is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                               created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY user_name_KEY (user_name),
                               KEY created_on_KEY (created_on)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='for tracking login attempt data';

-- oauth server schema
-- see https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java
CREATE TABLE oauth2_registered_client (
                                          id varchar(100) NOT NULL,
                                          client_id varchar(100) NOT NULL,
                                          client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          client_secret varchar(200) DEFAULT NULL,
                                          client_secret_expires_at timestamp,
                                          client_name varchar(200) NOT NULL,
                                          client_authentication_methods varchar(1000) NOT NULL,
                                          authorization_grant_types varchar(1000) NOT NULL,
                                          redirect_uris varchar(1000) DEFAULT NULL,
                                          scopes varchar(1000) NOT NULL,
                                          client_settings varchar(2000) NOT NULL,
                                          token_settings varchar(2000) NOT NULL,
                                          PRIMARY KEY (id),
                                          KEY client_id_KEY (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='spring oauth2';


insert into oauth2_registered_client(id,client_id,
                                     client_id_issued_at,
                                     client_secret,
                                     client_secret_expires_at,
                                     client_name,
                                     client_authentication_methods,
                                     authorization_grant_types,
                                     redirect_uris,
                                     scopes,
                                     client_settings,
                                     token_settings)
values
       ('8d548ae3-66ee-4d94-bbcc-4174f82688b6',
        'test-client',
        CURRENT_TIMESTAMP(),
        '{noop}secret',
        DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 1 YEAR),
        '8d548ae3-66ee-4d94-bbcc-4174f82688b6',
        'client_secret_basic',
        'refresh_token,authorization_code',
        'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/test-client-oidc',
        'test.read,openid',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}');

CREATE TABLE oauth2_authorization (
                                      id varchar(100) NOT NULL,
                                      registered_client_id varchar(100) NOT NULL,
                                      principal_name varchar(200) NOT NULL,
                                      authorization_grant_type varchar(100) NOT NULL,
                                      attributes varchar(4000) DEFAULT NULL,
                                      state varchar(500) DEFAULT NULL,
                                      authorization_code_value blob DEFAULT NULL,
                                      authorization_code_issued_at timestamp,
                                      authorization_code_expires_at timestamp,
                                      authorization_code_metadata varchar(2000) DEFAULT NULL,
                                      access_token_value blob DEFAULT NULL,
                                      access_token_issued_at timestamp,
                                      access_token_expires_at timestamp,
                                      access_token_metadata varchar(2000) DEFAULT NULL,
                                      access_token_type varchar(100) DEFAULT NULL,
                                      access_token_scopes varchar(1000) DEFAULT NULL,
                                      oidc_id_token_value blob DEFAULT NULL,
                                      oidc_id_token_issued_at timestamp,
                                      oidc_id_token_expires_at timestamp,
                                      oidc_id_token_metadata varchar(2000) DEFAULT NULL,
                                      refresh_token_value blob DEFAULT NULL,
                                      refresh_token_issued_at timestamp,
                                      refresh_token_expires_at timestamp,
                                      refresh_token_metadata varchar(2000) DEFAULT NULL,
                                      PRIMARY KEY (id),
                                      KEY principal_name_KEY (principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='spring oauth2';

CREATE TABLE oauth2_authorization_consent (
                                              registered_client_id varchar(100) NOT NULL,
                                              principal_name varchar(200) NOT NULL,
                                              authorities varchar(1000) NOT NULL,
                                              PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='spring oauth2';
