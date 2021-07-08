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
4. MongoDB service 4.4.6

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

## Operation

### Create

In the `upload` page, users could upload the project into the KEDO Data Lake. After the project is uploaded, the KEDO service will register PIDs for KEDO Object, KEDO knowledge Graph, Raw Data Files and RO-Crate. For example:

```
http://35.83.244.177:8000/api/handles/20.500.12033/1de5b1d2-19a1-4383-a26c-10e50d64bcb7	RO PID
http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8	RO PID
http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b	File PID
http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7	File PID
http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5	KEDO Object PID
http://35.83.244.177:8000/api/handles/20.500.12033/04679445-4fac-419b-9aac-5bbdb885c9a4	KEDO KG PID
http://35.83.244.177:8000/api/handles/20.500.12033/b38e474a-f575-4500-8840-44e5a03aa627	KEDO Type PID

```

### Query

In the `query` page, users could use PIDs to find several kinds of information

1. query the PID Kernel Information

```
Pure PID : http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5

quotationOf	None
dateCreation	08/07/2021 05:10:15
digitalObjectLocation	http://localhost:8082/queryPage?query=local&knowledge=http://www.kedo.com/11582a73-dd31-41c7-bc78-ae8ab08cdba5
revisionOf	None
alternateOf	None
etag	KEDO Object PID
wasDerivedFrom	None
lastModified	08/07/2021 05:10:15
primarySourceOf	None
specializationOf	None
```

2. query the PID table

```
PID Table: http://35.83.244.177:8000/api/handles/20.500.12033/5940e9f7-9db0-4fd4-9c2a-83e2906405c5

http://35.83.244.177:8000/api/handles/20.500.12033/1de5b1d2-19a1-4383-a26c-10e50d64bcb7	RO-Crate PID
http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8	RO-Crate PID
http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b	File PID
http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7	File PID
http://35.83.244.177:8000/api/handles/20.500.12033/04679445-4fac-419b-9aac-5bbdb885c9a4	KEDO KG PID
http://35.83.244.177:8000/api/handles/20.500.12033/b38e474a-f575-4500-8840-44e5a03aa627	KEDO Type PID


PID Table: http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7

http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8	RO-Crate PID
http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b	Share

```

3. query the knowledge

```
Local Query (use PID or IRI): http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8

<http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "altitude" ;
      <http://www.entity.com/field#name>
              "barometric_altitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "m" .


Traversal: PID Graph (use any PID) http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7

<http://35.83.244.177:8000/api/handles/20.500.12033/1de5b1d2-19a1-4383-a26c-10e50d64bcb7>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "RO-Crate PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/queryPage?query=local&knowledge=http://www.ro.com/1de5b1d2-19a1-4383-a26c-10e50d64bcb7" ;
      <http://www.entity.com/field#etag>
              "RO-Crate PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> ;
      <http://www.openarchives.org/ore/terms#describes>
              <http://www.ro.com/1de5b1d2-19a1-4383-a26c-10e50d64bcb7> .

<http://35.83.244.177:8000/api/handles/20.500.12033/b38e474a-f575-4500-8840-44e5a03aa627>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "KEDO Type PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/queryPage?query=local&knowledge=http://www.kedo.com/b38e474a-f575-4500-8840-44e5a03aa627" ;
      <http://www.entity.com/field#etag>
              "KEDO Type PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> ;
      <http://www.openarchives.org/ore/terms#describes>
              <http://www.kedo.com/b38e474a-f575-4500-8840-44e5a03aa627> .

<http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "File PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/download?path=/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight1.nc" ;
      <http://www.entity.com/field#etag>
              "File PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> .

<http://35.83.244.177:8000/api/handles/20.500.12033/04679445-4fac-419b-9aac-5bbdb885c9a4>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "KEDO KG PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/queryPage?query=local&knowledge=http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4" ;
      <http://www.entity.com/field#etag>
              "KEDO KG PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> ;
      <http://www.openarchives.org/ore/terms#describes>
              <http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4> .

<http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "RO-Crate PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/queryPage?query=local&knowledge=http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8" ;
      <http://www.entity.com/field#etag>
              "RO-Crate PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> ;
      <http://www.openarchives.org/ore/terms#describes>
              <http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8> .

<http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "File PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/download?path=/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight2.nc" ;
      <http://www.entity.com/field#etag>
              "File PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#aggregates>
              <http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5> .

<http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5>
      a       "PID" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "KEDO Object PID" ;
      <http://www.entity.com/field#alternateOf>
              "None" ;
      <http://www.entity.com/field#dateCreation>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#digitalObjectLocation>
              "http://localhost:8082/queryPage?query=local&knowledge=http://www.kedo.com/11582a73-dd31-41c7-bc78-ae8ab08cdba5" ;
      <http://www.entity.com/field#etag>
              "KEDO Object PID" ;
      <http://www.entity.com/field#lastModified>
              "08/07/2021 05:10:15" ;
      <http://www.entity.com/field#primarySourceOf>
              "None" ;
      <http://www.entity.com/field#quotationOf>
              "None" ;
      <http://www.entity.com/field#revisionOf>
              "None" ;
      <http://www.entity.com/field#specializationOf>
              "None" ;
      <http://www.entity.com/field#wasDerivedFrom>
              "None" ;
      <http://www.openarchives.org/ore/terms#describes>
              <http://www.kedo.com/11582a73-dd31-41c7-bc78-ae8ab08cdba5> .



Traversal: RO-Crate with Feature (use RO-Crate PID) http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8

<http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "altitude" ;
      <http://www.entity.com/field#name>
              "barometric_altitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "m" .

<http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "UTC_time" ;
      <http://www.entity.com/field#name>
              "time" ;
      <http://www.entity.com/field#type>
              "String" ;
      <http://www.entity.com/field#unit>
              "none" .

<http://www.feature.com/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "Attritbue" ;
      <http://www.entity.com/field#NCO>
              "4.7.2" ;
      <http://www.entity.com/field#_NCProperties>
              "version=1|netcdflibversion=4.6.0|hdf5libversion=1.10.0" ;
      <http://www.entity.com/field#history>
              "Fri Jul 10 21:09:17 2020: ncks -g mozaic_flight_2012030321335035_descent test_hgroups.nc 2.nc" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_aircraft>
              "3" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_arr>
              "TLV" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_dep>
              "FRA" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_flight>
              "2012030321335035" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_level>
              "calibrated" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_link>
              "http://www.iagos.fr/extract" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_mission>
              "mozaic" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_phase>
              "descent" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_arr>
              "2012-03-04 01:05:08" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_dep>
              "2012-03-03 09:33:50" ;
      <http://www.openarchives.org/ore/terms#isDescribedBy>
              <http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca> ;
      <http://www.openarchives.org/ore/terms#isReferencedBy>
              <http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8> ;
      <http://www.openarchives.org/ore/terms#specializationOf>
              <http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a> , <http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8> , <http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe> , <http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef> , <http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61> , <http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698> .

<http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "RO-Crate" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "RO-Crate" ;
      <http://www.entity.com/field#checksum>
              "b5088f62ac88c76beab839ac2937e6d3" ;
      <http://www.entity.com/field#contentSize>
              "15256" ;
      <http://www.entity.com/field#fileFormat>
              "nc" ;
      <http://www.entity.com/field#filePID>
              "http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7" ;
      <http://www.entity.com/field#localPath>
              "/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight2.nc" ;
      <http://www.entity.com/field#name>
              "flight2.nc" ;
      <http://www.entity.com/field#pid>
              "http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8" ;
      <http://www.openarchives.org/ore/terms#isPartOf>
              <http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4> .

<http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "O3" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_ozone_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .

<http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "air_press" ;
      <http://www.entity.com/field#name>
              "air_pressure" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "Pa" .

<http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "lon" ;
      <http://www.entity.com/field#standard_name>
              "longitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#units>
              "degree_east" .

<http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "CO" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_carbon_monoxide_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .


Traversal: Feature (use File PID) http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7

<http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "lat" ;
      <http://www.entity.com/field#standard_name>
              "latitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#units>
              "degree_north" .

<http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "altitude" ;
      <http://www.entity.com/field#name>
              "barometric_altitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "m" .

<http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "UTC_time" ;
      <http://www.entity.com/field#name>
              "time" ;
      <http://www.entity.com/field#type>
              "String" ;
      <http://www.entity.com/field#unit>
              "none" .

<http://www.feature.com/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "Attritbue" ;
      <http://www.entity.com/field#NCO>
              "4.7.2" ;
      <http://www.entity.com/field#_NCProperties>
              "version=1|netcdflibversion=4.6.0|hdf5libversion=1.10.0" ;
      <http://www.entity.com/field#history>
              "Fri Jul 10 21:09:17 2020: ncks -g mozaic_flight_2012030321335035_descent test_hgroups.nc 2.nc" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_aircraft>
              "3" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_arr>
              "TLV" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_dep>
              "FRA" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_flight>
              "2012030321335035" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_level>
              "calibrated" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_link>
              "http://www.iagos.fr/extract" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_mission>
              "mozaic" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_phase>
              "descent" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_arr>
              "2012-03-04 01:05:08" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_dep>
              "2012-03-03 09:33:50" ;
      <http://www.openarchives.org/ore/terms#isDescribedBy>
              <http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca> ;
      <http://www.openarchives.org/ore/terms#isReferencedBy>
              <http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8> ;
      <http://www.openarchives.org/ore/terms#specializationOf>
              <http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a> , <http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8> , <http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe> , <http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef> , <http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61> , <http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698> .

<http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "O3" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_ozone_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .

<http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "air_press" ;
      <http://www.entity.com/field#name>
              "air_pressure" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "Pa" .

<http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "lon" ;
      <http://www.entity.com/field#standard_name>
              "longitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#units>
              "degree_east" .

<http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "CO" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_carbon_monoxide_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .


Traversal: entire Knowledge Graph (use KEDO KG PID)  http://35.83.244.177:8000/api/handles/20.500.12033/11582a73-dd31-41c7-bc78-ae8ab08cdba5

<http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "altitude" ;
      <http://www.entity.com/field#name>
              "barometric_altitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "m" .

<http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "UTC_time" ;
      <http://www.entity.com/field#name>
              "time" ;
      <http://www.entity.com/field#type>
              "String" ;
      <http://www.entity.com/field#unit>
              "none" .

<http://www.feature.com/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "Attritbue" ;
      <http://www.entity.com/field#NCO>
              "4.7.2" ;
      <http://www.entity.com/field#_NCProperties>
              "version=1|netcdflibversion=4.6.0|hdf5libversion=1.10.0" ;
      <http://www.entity.com/field#history>
              "Fri Jul 10 21:09:17 2020: ncks -g mozaic_flight_2012030321335035_descent test_hgroups.nc 2.nc" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_aircraft>
              "3" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_arr>
              "TLV" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_airport_dep>
              "FRA" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_flight>
              "2012030321335035" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_level>
              "calibrated" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_link>
              "http://www.iagos.fr/extract" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_mission>
              "mozaic" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_phase>
              "descent" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_arr>
              "2012-03-04 01:05:08" ;
      <http://www.entity.com/field#mozaic_flight_2012030321335035_descent_time_dep>
              "2012-03-03 09:33:50" ;
      <http://www.openarchives.org/ore/terms#isDescribedBy>
              <http://www.feature.com/015d93f3-baa4-45c2-ba59-3fa56903bcca> ;
      <http://www.openarchives.org/ore/terms#isReferencedBy>
              <http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8> ;
      <http://www.openarchives.org/ore/terms#specializationOf>
              <http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a> , <http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8> , <http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe> , <http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef> , <http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61> , <http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698> .

<http://www.ro.com/1de5b1d2-19a1-4383-a26c-10e50d64bcb7>
      a       "RO-Crate" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "RO-Crate" ;
      <http://www.entity.com/field#checksum>
              "99e68013a692bfadfdce6698b1da5210" ;
      <http://www.entity.com/field#contentSize>
              "13875" ;
      <http://www.entity.com/field#fileFormat>
              "nc" ;
      <http://www.entity.com/field#filePID>
              "http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b" ;
      <http://www.entity.com/field#localPath>
              "/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight1.nc" ;
      <http://www.entity.com/field#name>
              "flight1.nc" ;
      <http://www.entity.com/field#pid>
              "http://35.83.244.177:8000/api/handles/20.500.12033/1de5b1d2-19a1-4383-a26c-10e50d64bcb7" ;
      <http://www.openarchives.org/ore/terms#isPartOf>
              <http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4> .

<http://www.feature.com/1de5b1d2-19a1-4383-a26c-10e50d64bcb7>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "Attritbue" ;
      <http://www.entity.com/field#NCO>
              "4.7.2" ;
      <http://www.entity.com/field#_NCProperties>
              "version=1|netcdflibversion=4.6.0|hdf5libversion=1.10.0" ;
      <http://www.entity.com/field#history>
              "Fri Jul 10 21:09:04 2020: ncks -g mozaic_flight_2012030403540535_ascent test_hgroups.nc 1.nc" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_aircraft>
              "3" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_airport_arr>
              "FRA" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_airport_dep>
              "TLV" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_flight>
              "2012030403540535" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_level>
              "calibrated" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_link>
              "http://www.iagos.fr/extract" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_mission>
              "mozaic" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_phase>
              "ascent" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_time_arr>
              "2012-03-04 08:01:44" ;
      <http://www.entity.com/field#mozaic_flight_2012030403540535_ascent_time_dep>
              "2012-03-04 03:54:05" ;
      <http://www.openarchives.org/ore/terms#isDescribedBy>
              <http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a> , <http://www.feature.com/5f0ce69a-a445-4845-8fa5-063a17ea98e8> , <http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe> , <http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61> , <http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef> , <http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698> ;
      <http://www.openarchives.org/ore/terms#isReferencedBy>
              <http://www.ro.com/1de5b1d2-19a1-4383-a26c-10e50d64bcb7> .

<http://www.ro.com/a80b290a-9f3c-4042-ab82-935b848554a8>
      a       "RO-Crate" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "RO-Crate" ;
      <http://www.entity.com/field#checksum>
              "b5088f62ac88c76beab839ac2937e6d3" ;
      <http://www.entity.com/field#contentSize>
              "15256" ;
      <http://www.entity.com/field#fileFormat>
              "nc" ;
      <http://www.entity.com/field#filePID>
              "http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7" ;
      <http://www.entity.com/field#localPath>
              "/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight2.nc" ;
      <http://www.entity.com/field#name>
              "flight2.nc" ;
      <http://www.entity.com/field#pid>
              "http://35.83.244.177:8000/api/handles/20.500.12033/a80b290a-9f3c-4042-ab82-935b848554a8" ;
      <http://www.openarchives.org/ore/terms#isPartOf>
              <http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4> .

<http://www.kedo.com/b38e474a-f575-4500-8840-44e5a03aa627>
      a       "KEDO Type" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "KEDO Type" ;
      <http://www.entity.com/field#pid>
              "http://35.83.244.177:8000/api/handles/20.500.12033/b38e474a-f575-4500-8840-44e5a03aa627" ;
      <http://www.entity.com/field#repository>
              "D2I" ;
      <http://www.entity.com/field#sizelevel>
              "Bytes" .

<http://www.feature.com/e9dbbcae-fd7a-4828-95a1-33478ac261fe>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "O3" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_ozone_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .

<http://www.kedo.com/04679445-4fac-419b-9aac-5bbdb885c9a4>
      a       "KEDO KG" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "KEDO KG" ;
      <http://www.entity.com/field#pid>
              "http://35.83.244.177:8000/api/handles/20.500.12033/04679445-4fac-419b-9aac-5bbdb885c9a4" ;
      <http://www.openarchives.org/ore/terms#isDescribedBy>
              <http://www.kedo.com/b38e474a-f575-4500-8840-44e5a03aa627> .

<http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "CO" ;
      <http://www.entity.com/field#name>
              "mole_fraction_of_carbon_monoxide_in_air" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "ppb" .

<http://www.feature.com/358ed0c8-6267-422c-8ecd-d02d00c9e71a>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "lat" ;
      <http://www.entity.com/field#standard_name>
              "latitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#units>
              "degree_north" .

<http://www.feature.com/c9b0a1bb-d54c-451b-8a3a-0fe51c1c9c61>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "air_press" ;
      <http://www.entity.com/field#name>
              "air_pressure" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#unit>
              "Pa" .

<http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef>
      a       "Feature" ;
      <http://www.w3.org/2000/01/rdf-schema#label>
              "lon" ;
      <http://www.entity.com/field#standard_name>
              "longitude" ;
      <http://www.entity.com/field#type>
              "double" ;
      <http://www.entity.com/field#units>
              "degree_east" .


Neighborhood Query: Provenance (use any File PID) http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7

quotationOf	None
alternateOf	None
revisionOf	None
wasDerivedFrom	None
primarySourceOf	None
specializationOf	None


Global Analytics: Shared Features (use any Feature (variable) IRI) http://www.feature.com/f62fd12d-117f-4a68-9478-bfd5b2406698

http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b
http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7
```

### Update
1. users could update the provenance information between two PIDs

```
target PID: http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b
provenance: specializationOf
object PID: http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7

quotationOf	None
dateCreation	08/07/2021 05:10:15
digitalObjectLocation	http://localhost:8082/download?path=/Users/luoyu/Desktop/YuLuo/lake/demo133b2b9a-df77-4937-98ba-94a7e01de567/flight1.nc
revisionOf	None
alternateOf	None
etag	File PID
wasDerivedFrom	None
lastModified	08/07/2021 05:35:04
primarySourceOf	None
specializationOf	http://35.83.244.177:8000/api/handles/20.500.12033/8bc08b7b-ba6d-4fa9-83f7-173804954ca7
```

2. users could upload the Insight information

```
File PID:	http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b
Workflow PID:	http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b
Feature (variable) IRI:	http://www.feature.com/f3d3ecab-bace-41a9-982f-15bb40b52bef
Description:	This is .....
Running Command:	run ...
Result:	result ...

Insight PID: http://35.83.244.177:8000/api/handles/20.500.12033/f0008a0a-f413-4a61-98c5-45d96f0f36ec

PID table: http://35.83.244.177:8000/api/handles/20.500.12033/77bef895-c9c7-4a33-8667-7cde59b0104b

http://35.83.244.177:8000/api/handles/20.500.12033/1de5b1d2-19a1-4383-a26c-10e50d64bcb7	RO-Crate PID
http://35.83.244.177:8000/api/handles/20.500.12033/f0008a0a-f413-4a61-98c5-45d96f0f36ec	Insight PID

```

### Download
1. users could use any PID to download the related object
2. Users could download the KEDO Packet in the `downloadPage`. The KEDO Packet includes the `raw data files` and a `rdf.ttl`





# Release History

* 0.1 1st release 2021.7.7
