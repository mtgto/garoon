name := "garoon"

organization := "net.mtgto"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.apache.axis2" % "axis2" % "1.6.2",
  "org.apache.axis2" % "axis2-java2wsdl" % "1.6.2",
  "org.apache.ws.commons.axiom" % "axiom-api" % "1.2.14",
  "org.apache.ws.commons.axiom" % "axiom-impl" % "1.2.14",
  "org.apache.ws.commons.schema" % "XmlSchema" % "1.4.7",
  "org.sisioh" %% "scala-dddbase-core" % "0.1.24",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
  "org.specs2" %% "specs2" % "2.3.3" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-feature", "-encoding", "UTF8")

scalacOptions in Test ++= Seq("-Yrangepos")

fork := true

