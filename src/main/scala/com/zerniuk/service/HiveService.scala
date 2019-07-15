package com.zerniuk.service

import com.zerniuk.SparkApp
import org.apache.spark.sql.functions.{callUDF, input_file_name}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.Try

class HiveService(spark: SparkSession) extends Serializable {

  import spark.implicits._

  def init = {
    val df = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("data/retail/init/2010-12-01.csv")

    enrichWithFileName(df)
      .write
      .partitionBy("batch_id")
      .format("hive")
      .saveAsTable("retail")
  }

  def processNewData = {
    val inputFiles = spark.read.format("csv").load("data/retail/by-day/*.csv").inputFiles.toSeq
    val currentBatches = getPartitionsNames
    val newFiles = inputFiles.filter { path =>
      val batch = SparkApp.extractBatch(path)
      !currentBatches.contains(batch)
    }
    println(s"New files size : ${newFiles.size}")
    if (newFiles.nonEmpty) {
      val df = spark.read.format("csv")
        .option("header", "true")
        .option("inferSchema", "true")
        .load(newFiles: _*)

      enrichWithFileName(df)
        .write
        .format("hive")
        .insertInto("retail")

    }
  }

  def getPartitionsNames = {

    Try(spark.sql("SHOW PARTITIONS retail")
      .select("partition").map(x => x.getString(0))
      .collect()
      .map(_.split("=").last).toSeq).toOption.getOrElse(Seq.empty)
  }

  def dropTable = {
    spark.sql("DROP TABLE IF EXISTS retail")
  }

  private def enrichWithFileName(df: DataFrame) = {
    df.withColumn("batch_id", callUDF("get_file_name", input_file_name()))
  }
}
