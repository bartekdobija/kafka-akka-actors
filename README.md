# Kafka Akka Actors

Lightweight implementation of consumer/producer Akka Actors.

(early version)

Usage (SBT):
```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.bartekdobija" % "kafka-akka-actors" % "{Tag}"

// select your preferred jackson version
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.+"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.+"
```