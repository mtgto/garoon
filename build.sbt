name := "garoon"

organization := "net.mtgto"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.apache.axis2" % "axis2" % "1.6.2",
  "org.apache.axis2" % "axis2-java2wsdl" % "1.6.2",
  "org.apache.ws.commons.axiom" % "axiom-api" % "1.2.14",
  "org.apache.ws.commons.axiom" % "axiom-impl" % "1.2.14",
  "org.apache.ws.commons.schema" % "XmlSchema" % "1.4.7",
  "org.sisioh" %% "scala-dddbase-core" % "0.1.29",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
  "org.specs2" %% "specs2-core" % "2.4.15" % "test",
  "org.specs2" %% "specs2-mock" % "2.4.15" % "test"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-feature", "-encoding", "UTF8")

scalacOptions in Test ++= Seq("-Yrangepos")

publishTo := Some(Resolver.file("file", new File("maven/")))
