import AssemblyKeys._

name := "spark-ml"

scalaVersion := "2.10.3"

version := "1.2"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.1.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

assemblySettings

assemblyOption in assembly ~= { _.copy(includeScala = false) }

test in assembly := {}
