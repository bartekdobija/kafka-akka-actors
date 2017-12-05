package com.github.bartekdobija.actors

import akka.actor.Props
import com.github.bartekdobija.actors.KafkaProducerActor.Message
import com.github.bartekdobija.serdes.JsonSerializer
import org.apache.kafka.clients.producer.ProducerRecord

object KafkaJsonProducerActor {
  val NAME: String = getClass.getName

  def props(bootstrap: String,
            config: Map[String, AnyRef] = Map.empty): Props =
    Props(classOf[KafkaJsonProducerActor], bootstrap, config)
}

class KafkaJsonProducerActor(bootstrap: String, config: Map[String, AnyRef])
    extends KafkaProducerActor[Array[Byte], AnyRef](bootstrap,
                                                    config,
                                                    valueSerializer =
                                                      new JsonSerializer) {
  override def receive: Receive = {
    case Message(t: String, d: AnyRef) =>
      prod.send(new ProducerRecord[Array[Byte], AnyRef](t, d))
    case _ => log.warning("unknown event")
  }
}
