import sbt._

object Dependencies {

  lazy val scalaTestVersion = "3.0.1"
  lazy val akkaActorVersion = "2.5.7"
  lazy val kafkaVersion = "0.11.0.1"
  lazy val jacksonVersion = "2.9.2"

  val baseDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

    "org.apache.kafka" % "kafka-clients" % kafkaVersion % Provided,

    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion % Provided,

    "com.typesafe.akka" %% "akka-actor" % akkaActorVersion % Provided,

    "net.manub" %% "scalatest-embedded-kafka" % "1.0.0" % Test
  )

}
