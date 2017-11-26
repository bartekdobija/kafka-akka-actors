package com.github.bartekdobija.actors

import akka.actor.{ActorRef, PoisonPill}
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer}
import org.codehaus.jackson.map.ObjectMapper

import scala.concurrent.duration._

class KafkaConsumerActorSpec extends ActorSpec with EmbeddedKafka {

  private var actor: ActorRef = _
  private val objectMapper = new ObjectMapper()


  classOf[KafkaConsumerActor[_, _]].getSimpleName must {

    "consume messages" in {
      withRunningKafka {

        createCustomTopic("scraped", Map.empty[String, String], 1, 1)

        actor = system.actorOf(KafkaConsumerActor.props[Long, String]("scraped", "gid", "localhost:6001", new LongDeserializer, new StringDeserializer))

        actor ! Subscribe
        expectMsg(Subscribed)

        publishStringMessageToKafka("scraped", "{\"foo\":\"bar\"}")
        publishStringMessageToKafka("scraped", "{\"a\":\"b\"}")

        expectMsgClass(14 seconds, classOf[Record])
        expectMsgClass(classOf[Record])

        actor ! PoisonPill

      }
    }
  }
}
