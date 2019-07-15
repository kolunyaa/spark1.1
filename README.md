## Practical project #1.1

### Project requirements

 1. datasource in minio/s3 marked with batch ids of some nature (possibly the same data but under different batches)
 2. spark batch appliction could determine what it needs to process from bucket path without explicit file names
 3. each new processed batch is written to minio/s3/etc and registered as new partition of Hive table
 4. Hive table is persisted and available for queries from Hive


### Dependencies

     Name         | Version
     ------------ | -------------
     Scala        | 2.12.8
     Sbt          | 1.2.8
     Spark SQL    | 2.4.3
     
### Project description

This project demonstrates the simple data processing using Apache Spark with Apache Hive

App flow:

Main class `com.zerniuk.SparkApp.scala` 

Users who do not have an existing Hive deployment can still enable Hive support. <br/>
When not configured by the hive-site.xml, the context automatically creates metastore_db in the current directory  <br/>
and creates a directory configured by spark.sql.warehouse.dir, which defaults to the directory spark-warehouse  <br/>
in the current directory that the Spark application is started.  <br/>

data/*
contains csv files with retail data.
File name template is `yyyy-mm-dd.csv` <br/>
Each file contains daily records.

`init` folder contains file for `retail` table init.<br/>
`by-day` - the source folder. Spark will check this folder for files to process.<br/>
`new` - contains files that will be added to `by-day` folder to simulate new data incoming.<br/>

SparkApp init stage creates `retail` table enriches it with `batch_id` column which is extracted from filename, <br/>
`retail` table is partitioned by `batch_id` column. <br/>

`
 thread {
    fileService.addNewFiles
  }`
  
  Creates a job in a separate thread which simulates new data incoming.
  Files from `new` folder copied to `by-day` with some delay.
  
  Spark checks `by-day` folder for new files and writes data to hive as a separate partition.
  
  `spark-warehouse` folder will be created at the root of app.
 
  Data is stored in separated folders for each batch.
  
  In this case we can query `retail` table as Hive Table.
  
### How to run
* Go to the root of the application and run `sbt compile run`

