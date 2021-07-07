# KEDODataLake
* This is a prototype 


The major goals of the KEDO Data Lake fall into three distinct bins:
* developing a smart object service that delivers to a set of logical objects in the DOA space with PIDs and Knowledge Graph, while managing the raw data in the Data Lake, and 
* studying and identifying the benefits of this layered architecture particularly in its potential to build trust at the local level.

## Installation Guide

### Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. GraphDB 9.8
4. Handle service
5. MongoDB service



### Building the Source

Check out source codes:

```
git clonehttps://github.com/luoyu357/KEDODataLake.git
```

Edit the `application.properties` file under `src/main/resources` and set your port for the service

```
vi LIDO/src/main/resources/application.properties
```

Edit the `config.properties` file under `src/main/java/id/edu/kedo` and set the information for Handle System, Cordra, and temporal folder for service

```
vi LIDO/src/main/java/iu/edu/kedo/config.properties
```


Edit the `href` field in `.html` files under `src/main/resources/templates` and set the proper service address

```
vi xxxx.html
```

Build and run the Airbox Data Lake

```
mvn spring-boot:run
```

## Operations

* `/home`: navigate to the `upload`, `query`, `update`, and `download` page
* `/upload`: users could type the project name and upload a list of raw data files
* `/query`: users could use PID/IRI to query the PID entity, PID Table and Knowledge.
* `/update`: users could use this page to update the provenance between two PIDs, and upload the Insight information
* `/downloadPage`: users could use KEDO Object PID to download the kEDO Object Packet. The Packet includes the RDF of knowledge and raw data files


# Release History

* 0.1 1st release 2021.7.7
