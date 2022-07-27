# KEDODataLake


The major goals of the KEDO Data Lake fall into three distinct bins:
* developing a smart object service that delivers to a set of logical objects in the DOA space with PIDs and Knowledge Graph, while managing the raw data in the Data Lake, and
* studying and identifying the benefits of this layered architecture particularly in its potential to build trust at the local level.
* applied a Machine Learning approach to ingest and align the knowledge graph while migrating KEDO Objects between different KEDO Data Lakes

## Installation Guide

### Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. GraphDB 9.8
4. MongoDB service 4.4.6
5. Neo4j 4.4.10 (https://github.com/neo4j/neo4j)

### Service Dependence:

1. Handle service v9.3.0

### Building the Source

Check out source codes:

```
git clone https://github.com/luoyu357/KEDODataLake.git
```

Edit the `application.properties` file under `src/main/resources` and set your port for the service

```
vi KEDODataLake/src/main/resources/application.properties

server.port = 8082
spring.servlet.multipart.max-file-size=100MB

spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

Edit the `config.properties` file under `src/main/java/iu/edu/kedo` and set the information for KEDO Data Lake, Handle System, GraphDB, and MongoDB for service

```
vi KEDODataLake/src/main/java/iu/edu/kedo/config.properties

handle.restful.api.url = http://35.83.244.177:8000/api/handles/20.500.12033/
handle.prefix = 20.500.12033
private.key.file = /Users/luoyu/Desktop/ec2/handle/admpriv.bin
private.key.file.password =
handle.admin.identifier = 0.NA/20.500.12033
kedo.service = http://localhost:8082/
lake = /Users/luoyu/Desktop/YuLuo/lake/
graphdb = http://localhost:7200/repositories/1
mongo = localhost:27017
```


Edit the `href` field in `.html` files under `KEDODataLake/src/main/resources/templates` and update the proper service address

```
vi xxxx.html
```

Build and run the KEDO Data Lake

```
mvn spring-boot:run
```

## Pages

* `/home`: navigate to the `upload`, `query`, `update`, and `download` page
* `/upload`: users could type the project name and upload a list of raw data files
* `/query`: users could use PID/IRI to query the PID entity, PID Table and Knowledge.
* `/update`: users could use this page to update the provenance between two PIDs, and upload the Insight information
* `/downloadPage`: users could use KEDO Object PID to download the kEDO Object Packet. The Packet includes the RDF of knowledge and raw data files


## Components

1. `src` folder
  * KEDO Data Model
  2. KEDO Data Lake
  3. KEDO Learner
2. `images` folder
  1. KEDO Data Model Images
  2. KEDO Data Lake Images
  3. KEDO Learner Images
  4. KEDO Learner Assessment Images
  5. Appendix
3. `Python` folder
  1. code of TransE and KEDO Learner in python format







# Release History

* 0.1 1st release 2021.7.7
* 0.2 2nd release 2022.7.26
