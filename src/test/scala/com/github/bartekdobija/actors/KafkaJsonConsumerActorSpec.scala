package com.github.bartekdobija.actors

import akka.actor.{ActorRef, PoisonPill}
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.concurrent.duration._

object KafkaJsonConsumerActorSpec {
  case class Log(ts: String, `type`: String, data: String)
}

class KafkaJsonConsumerActorSpec extends ActorSpec with EmbeddedKafka {

  private var actor: ActorRef = _
  private val consumerConfig = Map(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )
  private val topic = getClass.getSimpleName
  private val groupId = getClass.getSimpleName
  private val bootstrap = "localhost:6001"

  classOf[KafkaJsonConsumerActor[_]].getSimpleName must {
    "consume JSON events" in {

      val event = "{\"ts\": 12345, \"type\": \"com.github.bartekdobija.Log\", \"data\": \"hello world\"}"

      withRunningKafka {
        createCustomTopic(topic)

        actor = system.actorOf(KafkaJsonConsumerActor.props[KafkaJsonConsumerActorSpec.Log](topic, bootstrap, groupId, consumerConfig))
        actor ! Subscribe
        expectMsg(Subscribed)

        publishStringMessageToKafka(topic, event)

        expectMsgClass(5 seconds, classOf[Record])

        actor ! PoisonPill
      }

    }
  }

}
