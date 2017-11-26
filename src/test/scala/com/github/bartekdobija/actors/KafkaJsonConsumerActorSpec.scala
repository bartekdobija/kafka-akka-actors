package com.github.bartekdobija.actors

import akka.actor.{ActorRef, PoisonPill}
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import net.manub.embeddedkafka.EmbeddedKafka
import org.codehaus.jackson.map.ObjectMapper

import scala.concurrent.duration._

object KafkaJsonConsumerActorSpec {
  class Log {
    @JsonProperty("ts") var timestamp: Long = _
    @JsonProperty("type") var `type`: String = _
    @JsonProperty("data") var data: String = _
  }
}

class KafkaJsonConsumerActorSpec extends ActorSpec with EmbeddedKafka {

  private var actor: ActorRef = _
  private val objectMapper = new ObjectMapper()

  classOf[KafkaJsonConsumerActor[_]].getSimpleName must {
    "consume JSON logs" in {

      withRunningKafka {
        createCustomTopic("logs", Map.empty[String, String], 1, 1)

        actor = system.actorOf(KafkaJsonConsumerActor.props[KafkaJsonConsumerActorSpec.Log]("logs", "gid", "localhost:6001"))
        actor ! Subscribe
        expectMsg(Subscribed)

        publishStringMessageToKafka("logs", "{\"ts\": 12345, \"type\": \"com.github.bartekdobija.Log\", \"data\": \"hello world\"}")

        expectMsgClass(4 seconds, classOf[Record])

        actor ! PoisonPill
      }

    }
  }

}
