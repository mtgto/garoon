name := "garoon"

organization := "net.mtgto"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.sisioh" %% "scala-dddbase-core" % "0.1.24",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
  "org.specs2" %% "specs2" % "2.3.3" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-feature", "-encoding", "UTF8")

scalacOptions in Test ++= Seq("-Yrangepos")

fork := true

