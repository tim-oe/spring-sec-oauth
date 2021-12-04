## Spring Security OAuth Authorization Server

### Relevant information:

This module demonstrates OAuth authorization flow using Spring Authorization Server, Spring OAuth Resource Server and
Spring OAuth Client. It uses the JDBC backed services described [here](https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java)

- requires database
  - see [DB](db/README.md) subproject for details 

- Run the Authorization Server from the `spring-authorization-server` module
    - IMPORTANT: Modify the `/etc/hosts` file and add the entry `127.0.0.1 auth-server`
- Run the Resource Server from `resource-server` module
- Run the client from `client-server` module
- Go to `http://127.0.0.1:8080/articles`
    - Enter the credentials `admin@example.net/admin`
- The module uses the new OAuth stack with Java 11

### Relevant Articles:

- [Spring Security OAuth Authorization Server](https://www.baeldung.com/spring-security-oauth-auth-server)
