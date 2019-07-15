import Dependencies._


name := "spark1.1"

version := "0.1"

scalaVersion := "2.12.8"

name := "Spark Hive"


ThisBuild / name := "Spark1"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1"
lazy val root = (project in file("."))
  .settings(
    name := "Spark1.1",
    libraryDependencies ++= spark,
    mainClass in Compile := Some("com.zerniuk.SparkApp")

  )