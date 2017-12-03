package com.github.bartekdobija.actors

import akka.actor.Props
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.bartekdobija.actors.KafkaConsumerActor.Record
import com.github.bartekdobija.serdes.JsonDeserializer
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import org.apache.kafka.common.serialization.LongDeserializer

import scala.reflect.ClassTag
import scala.reflect._

object KafkaJsonConsumerActor {
  val NAME: String = getClass.getSimpleName

  def props[T: ClassTag](topic: String,
                         bootstrap: String,
                         groupId: String = NAME,
                         config: Map[String, AnyRef] = Map.empty): Props =
    Props(new KafkaJsonConsumerActor[T](topic, bootstrap, groupId, config))
}

class KafkaJsonConsumerActor[T: ClassTag](topic: String,
                                          bootstrap: String,
                                          groupId: String,
                                          config: Map[String, AnyRef] =
                                            Map.empty)
    extends KafkaConsumerActor[Long, JsonNode](topic,
                                               bootstrap,
                                               groupId,
                                               new LongDeserializer,
                                               new JsonDeserializer,
                                               config) {

  protected val om = new ObjectMapper() with ScalaObjectMapper
  om.registerModule(DefaultScalaModule)

  override protected def dispatchRecords(
      value: ConsumerRecords[Long, JsonNode]): Unit = {
    val ct = classTag[T].runtimeClass
    subscribed.foreach {
      case (_, sub) =>
        value.records(topic).forEach {
          record: ConsumerRecord[Long, JsonNode] =>
            sub ! Record(om.treeToValue(record.value(), ct))
        }
    }
  }

}
