import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1.1",
  organization := "com.github.bartekdobija",
  scalaVersion := "2.12.2"
)

// Docker configuration
lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "kafka-akka-actors",
    libraryDependencies ++= baseDeps
  )

fork in Test := true
