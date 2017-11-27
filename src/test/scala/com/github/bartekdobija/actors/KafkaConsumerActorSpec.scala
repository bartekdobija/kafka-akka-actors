package com.github.bartekdobija.actors

import akka.actor.{ActorRef, PoisonPill}
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer}

import scala.concurrent.duration._

class KafkaConsumerActorSpec extends ActorSpec with EmbeddedKafka {

  private var actor: ActorRef = _
  private val consumerConfig = Map(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )
  private val topic = getClass.getSimpleName
  private val groupId = getClass.getSimpleName
  private val bootstrap = "localhost:6001"


  classOf[KafkaConsumerActor[_, _]].getSimpleName must {

    "consume messages" in {
      withRunningKafka {

        createCustomTopic(topic)

        actor = system.actorOf(KafkaConsumerActor.props[Long, String](topic, bootstrap, groupId, new LongDeserializer, new StringDeserializer, consumerConfig))

        actor ! Subscribe
        expectMsg(Subscribed)

        publishStringMessageToKafka(topic, "{\"foo\":\"bar\"}")
        publishStringMessageToKafka(topic, "{\"a\":\"b\"}")

        expectMsgClass(5 seconds, classOf[Record])
        expectMsgClass(classOf[Record])

        actor ! PoisonPill

      }
    }
  }
}
