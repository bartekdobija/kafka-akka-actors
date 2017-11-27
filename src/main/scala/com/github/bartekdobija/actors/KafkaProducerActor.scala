package com.github.bartekdobija.actors

import java.util.Properties

import akka.actor.{Actor, ActorLogging, Props}
import com.github.bartekdobija.actors.KafkaProducerActor.Message
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}

object KafkaProducerActor {
  val NAME: String = getClass.getName

  case class Message(topic: String, value: Any)

  def props[K, V](bootstrap: String, config: Map[String, AnyRef] = Map.empty, keySerializer: Serializer[_] = new StringSerializer, valueSerializer: Serializer[_] = new StringSerializer): Props = Props(new KafkaProducerActor[K, V](bootstrap, config, keySerializer, valueSerializer))
}

class KafkaProducerActor[K, V](private val bootstrap: String, private val config: Map[String, AnyRef] = Map.empty, private val keySerializer: Serializer[_] = new StringSerializer, private val valueSerializer: Serializer[_] = new StringSerializer) extends Actor with ActorLogging {

  protected var prod: Producer[K, V] = _

  override def preStart(): Unit = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getClass)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getClass)
    config.foreach{ case (k,v) => props.put(k,v)}
    prod = new KafkaProducer[K, V](props)
  }

  override def receive: Receive = {
    case Message(t: String, d: V) => prod.send(new ProducerRecord[K, V](t, d))
    case _ => log.warning("unknown event")
  }
}
