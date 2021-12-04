### DB schema patch scripts and sample data 

### patch script process 
 - sql/scripts contains release patch scripts
 - sql/data contains sample data scripts
### overall usage
- utilizes [flyway](https://flywaydb.org/documentation/gradle/)
- assumes working knowledge of [gradle](https://docs.gradle.org/current/userguide/userguide.html)
- assumes working knowledge of [gradle build environment](https://docs.gradle.org/current/userguide/build_environment.html)
- db settings for gradle.properties 
    - default location is ~/.gradle
    - reads from gradle.properties located in $USER_GRADLE_HOME
    - the following properties are required (values can be derived from legacy .Outboundengine.properties if preset)
        - db_host=
        - db_port=
        - db_name=
        - db_username=
        - db_password=
- custom tasks Tasks
    - dbReset
        - will drop and recreate schema using patch files
        - install triggers, stored procedures, views
        - load sample data

### Database instances
the instances are run via docker containers
- MariaDB 10.4.x

starting instances
- gradle mariadbComposeUp

stopping instances
- gradle mariadbComposeDown
