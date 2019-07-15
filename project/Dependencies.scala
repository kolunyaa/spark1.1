import sbt._

object Dependencies {
  val spark_version = "2.4.3"
  val spark_core = "org.apache.spark" %% "spark-core" % spark_version force()
  val sql = "org.apache.spark" %% "spark-sql" % spark_version force()
  val spark_hive = "org.apache.spark" %% "spark-hive" % spark_version force()

  val spark = Seq(spark_core, sql, spark_hive)
}
