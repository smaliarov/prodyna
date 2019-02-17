# PRODYNA Technical Test

### Assignment

Prepare and demonstrate a small project solution with the following functional and non-functional requirements.
- Implement a REST service called "PersonService", which offers CRUD (Create, Read, Update,
  Delete) for an entity of type "Person"
- A person has a unique ID and a name which is non-null and has a length of 3 to 20 characters
- Use Java EE 8 as base technology
- The service shall run in an application server like WildFly
- The service shall persist the entities in a database like MySQL or MongoDB
- Use a dependency and build management tool like Maven or Gradle

### Solution

Implemented PersonService using Spring Boot.

But I was also considering https://microprofile.io/ as an alternative. I choose Spring Boot because I have production experience with it.

##### Why Spring Boot?
- Speed of development
- Popular framework for Java development in general and Microservices in particular
- Easy to extend.

### Specifics of implementation
- Runs as a standalone application using an embedded Tomcat (easier to demo)
- Maven build generates both jar and a war file that can be deployed to an application server like WildFly
- Uses embedded H2 database, can be easily switched to use MySQL or any other DB supported by Spring JPA

### Additional questions
- How could authentication and authorization be added to the service?

The short answer would be: Spring Security. The longer answer would depend on the way service  is going to be used. 

- How could performance measurement (per method, count, average execution time,…) be added to the service?

It really depends on what and why you want to measure. I would start with using JMX and something like Java Mission Control.

If you need something more specific, I would continue with Micrometer (https://micrometer.io/)

- What would be a good pattern to implement the following requirement to achieve a "separation of concern&quot;? Every time a Person is Created, Updated or Deleted, a message is sent to a JMS queue

See PersonChangeListener

- What would be a good pattern to mitigate the following problem?  Normally a remote client would call the create method of the REST service to add a person and the ID of the person would be created in the database. The ID or the person with the ID would be returned to the client. In case there is a problem and the client did not receive a response with the ID, the client does not know if the person was created in the database

A client should try again. The create method has been created in such a way that an attempt to create a person with the same name does not result in an error, it updates the person.

- What should be considered when building a Java client for this server?

The service comes with Swagger (https://swagger.io), I would start with that.

- Explain a scenario with replication/clustering

The service is stateless, it can be easily used in a cluster. If you want some kind of caching/read-only copies though, it would be more complicated.