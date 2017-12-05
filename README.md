# Kafka Akka Actors

Lightweight implementation of consumer/producer Akka Actors.

(early version)

API Example can be found in the [ExampleSpec](src/test/scala/com/github/bartekdobija/actors/ExampleSpec.scala) test

Usage (SBT Deps):
```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.bartekdobija" % "kafka-akka-actors" % "{Tag}"

// select your preferred jackson version
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.+"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.+"
```

Consumer (Scala):
```scala
case class SomeEvent(a: Int, b: String)

// JSON consumer
val actor = system.actorOf(KafkaJsonConsumerActor.props[SomeEvent](topic, bootstrap, groupId))

// or generic consumer
val actor = system.actorOf(KafkaConsumerActor.props[Long, String](topic, bootstrap, groupId))

actor ! Subscribe

// in parent actor
override def receive: Receive = {
  case Record(e: SomeEvent) => ...
}
```

Producer (Scala):
```scala
case class SomeEvent(a: Int, b: String)

// JSON Producer
val actor = system.actorOf(KafkaJsonProducerActor.props(bootstrap))

// or generic producer
val actor = system.actorOf(KafkaProducerActor.props[Long, String](bootstrap))

// for JSON Producer
actor ! Message(topic, SomeEvent(1,"a"))

// for generic
actor ! Message(topic, "event")
```