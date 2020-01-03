name := "AkkaCluster"

version := "0.1"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.+"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion, // to use classic Akka Cluster
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion, // to use typed Akka Cluster
  "com.typesafe.akka" %% "akka-http"   % "10.+",
  "io.spray" %% "spray-json" % "1.3.5", // to make ProcessorResponse as json format
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11", // to make ProcessorResponse as json format
  "org.slf4j" % "slf4j-api" % "1.7.+", // required if to use log.info(...) in ActorLogging
  "org.slf4j" % "slf4j-simple" % "1.7.+", // required if to use log.info(...) in ActorLogging
  "org.scalatest" %% "scalatest" % "3.+" % Test,
  "org.scalacheck" %% "scalacheck" % "1.+" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
)

// to build docker image (other build.sbt properties)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

mainClass in Compile := Some("akka_cluster.Server")
dockerBaseImage := "java:8-jre-alpine"
version in Docker := "latest"
dockerExposedPorts := Seq(8000)
dockerRepository := Some("starmap")
// run "sbt docker:publishLocal" to build the image
//   it will create a local image under REPOSITORY "starmap/akkacluster"   