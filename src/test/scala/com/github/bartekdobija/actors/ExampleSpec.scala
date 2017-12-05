package com.github.bartekdobija.actors

import com.github.bartekdobija.actors.ExampleSpec.SomeEvent
import com.github.bartekdobija.actors.KafkaConsumerActor.{Record, Subscribe, Subscribed}
import com.github.bartekdobija.actors.KafkaProducerActor.Message
import com.github.bartekdobija.serdes.JsonSerializer
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{Serializer, StringDeserializer, StringSerializer}

import scala.concurrent.duration._

object ExampleSpec {
  case class SomeEvent(a: Int, b: String)
}

class ExampleSpec extends ActorSpec {

  private implicit val js: Serializer[Any] = new JsonSerializer
  private implicit val s: Serializer[String] = new StringSerializer
  private val jsonTopic = "json-example"
  private val stringTopic = "string-example"
  private val jsonGroupId = "jgid"
  private val groupId = "gid"
  private val config = Map(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )

  override def beforeAll: Unit = {
    EmbeddedKafka.start()

    createCustomTopic(jsonTopic)
    createCustomTopic(stringTopic)
    publishToKafka[Any](jsonTopic, SomeEvent(1,"a"))
    publishToKafka(stringTopic, "message")
  }

  override def afterAll: Unit = {
    EmbeddedKafka.stop()
  }

  classOf[ExampleSpec].getSimpleName must {

    "demonstrate consumer's API (JSON)" in {
      val props = KafkaJsonConsumerActor.props[SomeEvent](jsonTopic, bootstrap, jsonGroupId, config)
      val actor = system.actorOf(props, "json-consumer")

      actor ! Subscribe

      // confirmed subscription
      expectMsg(Subscribed)

      // expect Record(value: Any) messages from the consumer
      expectMsg(5 seconds, Record(SomeEvent(1,"a")))

    }

    "demonstrate consumer's API (Generic)" in {

      val props = KafkaConsumerActor.props[Long, String](
        stringTopic,
        bootstrap,
        groupId,
        valueDeserializer = new StringDeserializer,
        configProps = config
      )
      val actor = system.actorOf(props, "consumer")

      actor ! Subscribe

      // confirmed subscription
      expectMsg(Subscribed)

      // expect Record(value: Any) from the consumer
      expectMsg(5 second, Record("message"))
    }

    "demonstrate producer's API (JSON)" in {
      val actor = system.actorOf(KafkaJsonProducerActor.props(bootstrap))

      actor ! Message("topicName", SomeEvent(1, "a"))
    }

    "demonstrate producer's API (Generic)" in {
      val actor = system.actorOf(KafkaProducerActor.props[Long, String](bootstrap))

      actor ! Message("topicName", "message".getBytes)
    }

  }

}
