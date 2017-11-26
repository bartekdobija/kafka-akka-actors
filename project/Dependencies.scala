import sbt._

object Dependencies {

  lazy val scalaTestVersion = "3.0.1"
  lazy val akkaActorVersion = "2.5.7"
  lazy val kafkaVersion = "0.11.0.1"
  lazy val jacksonVersion = "2.9.2"

  val baseDeps = Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVersion % Provided,

    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion % Provided,

    "com.typesafe.akka" %% "akka-actor" % akkaActorVersion % Provided,

    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaActorVersion % Test,
    "net.manub" %% "scalatest-embedded-kafka" % "1.0.0" % Test
  )

}
