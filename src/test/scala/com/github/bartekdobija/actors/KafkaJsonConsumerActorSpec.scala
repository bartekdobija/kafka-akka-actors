package com.github.bartekdobija.actors

import akka.actor.{ActorRef, PoisonPill}
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import com.github.bartekdobija.actors.KafkaJsonConsumerActorSpec.Log
import com.github.bartekdobija.serdes.JsonSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serializer

import scala.concurrent.duration._

object KafkaJsonConsumerActorSpec {
  case class Log(a: String, b: Seq[Seq[String]])
}

class KafkaJsonConsumerActorSpec extends ActorSpec {

  private var actor: ActorRef = _
  private val consumerConfig = Map(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )
  private val topic = getClass.getSimpleName
  private val groupId = getClass.getSimpleName

  implicit val ser: Serializer[Any] = new JsonSerializer()
  private val event = Log("a", Seq(Seq("a"), Seq("b")))

  classOf[KafkaJsonConsumerActor[_]].getSimpleName must {
    "consume JSON events" in {
      withRunningKafka {
        createCustomTopic(topic)

        actor = system.actorOf(KafkaJsonConsumerActor.props[Log](topic, bootstrap, groupId, consumerConfig))
        actor ! Subscribe
        expectMsg(Subscribed)

        publishToKafka[Any](topic, event)
        expectMsgClass(5 seconds, classOf[Record])

        actor ! PoisonPill
      }

    }
  }

}
