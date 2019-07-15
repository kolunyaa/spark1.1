package com.zerniuk

import java.io.File

import com.zerniuk.service.{FileService, HiveService}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object SparkApp extends App {

  System.setSecurityManager(null)

  Logger.getLogger("org").setLevel(Level.ERROR)

  val warehouseLocation = new File("spark-warehouse").getAbsolutePath

  val spark = SparkSession.builder()
    .master("local")
    .appName("Spark Hive")
    .config("spark.sql.warehouse.dir", warehouseLocation)
    .enableHiveSupport()
    .getOrCreate()

  spark.sqlContext.setConf("hive.exec.dynamic.partition", "true")
  spark.sqlContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")

  spark.udf.register("get_file_name", (path: String) => extractBatch(path))

  def extractBatch(path: String) = path.split("/").last.split("\\.").head

  val hiveService = new HiveService(spark)
  val fileService = new FileService

  hiveService.dropTable
  hiveService.init

  thread {
    fileService.addNewFiles
  }

  while (fileService.newFiles.nonEmpty) {
    hiveService.processNewData
    sleep(3)
  }

  spark.sql("select round(sum(unit_price),2) as price_sum, country from retail group by country order by round(sum(unit_price),2) desc").show()
  spark.sql("select country, count(*) as count from retail group by country order by count(*) desc").show()
  spark.sql("select country, quantity, unit_price, round((quantity * unit_price), 2) as total_price from retail").show()

  spark.stop()
}
