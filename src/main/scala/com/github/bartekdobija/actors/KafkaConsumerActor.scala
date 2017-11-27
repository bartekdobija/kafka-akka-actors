package com.github.bartekdobija.actors

import java.util.Properties

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.bartekdobija.actors.KafkaConsumerActor._
import org.apache.kafka.clients.consumer.{
  Consumer,
  ConsumerConfig,
  ConsumerRecords,
  KafkaConsumer
}
import org.apache.kafka.common.serialization.{
  Deserializer,
  LongDeserializer,
  StringDeserializer
}

import scala.collection.mutable
import scala.concurrent.duration._

object KafkaConsumerActor {
  case object Subscribe
  case object Subscribed
  case object Unsubscribe
  case object Unsubscribed
  case object Consume
  case class Record(value: Any)

  val CONSUMER_POLL_TIMEOUT: Int = 0
  val SCHEDULE_ONE_DELAY: FiniteDuration = 50 milliseconds
  val NAME: String = getClass.getSimpleName

  def props[K, V](topic: String,
                  bootstrap: String,
                  groupId: String = getClass.getSimpleName,
                  keyDeserializer: Deserializer[_] = new LongDeserializer,
                  valueDeserializer: Deserializer[_] = new StringDeserializer,
                  configProps: Map[String, AnyRef] = Map.empty,
                  schemaRegistry: String = ""): Props =
    Props(classOf[KafkaConsumerActor[K, V]],
          topic,
          bootstrap,
          groupId,
          keyDeserializer,
          valueDeserializer,
          configProps,
          schemaRegistry)
}

class KafkaConsumerActor[K, V](
    private val topic: String,
    private val bootstrap: String,
    private val groupId: String = getClass.getSimpleName,
    private val keyDeserializer: Deserializer[_] = new LongDeserializer,
    private val valueDeserializer: Deserializer[_] = new StringDeserializer,
    private val configProps: Map[String, AnyRef] = Map.empty,
    private val schemaRegistry: String = "")
    extends Actor
    with ActorLogging {

  protected val subscribed: mutable.Map[Int, ActorRef] = mutable.Map.empty
  private var consumer: Consumer[K, V] = _

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"restarting actor ${getClass.getName}")
    this.preStart()
  }

  override def preStart(): Unit = {
    super.preStart()

    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
              keyDeserializer.getClass)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
              valueDeserializer.getClass)
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConsumerActor.NAME)
    props.put("schema.registry.url", schemaRegistry)

    // add extra configuration properties
    configProps.foreach { case (k, v) => props.put(k, v) }

    import collection.JavaConverters._
    consumer = new KafkaConsumer(props)
    consumer.subscribe(Seq(topic).asJava)

    // start consuming
    context.system.scheduler.scheduleOnce(SCHEDULE_ONE_DELAY) {
      self ! Consume
    }(context.system.dispatcher)
  }

  override def receive: Receive = {
    case Subscribe =>
      subscribe(sender)
      sender() ! Subscribed
    case Unsubscribe =>
      unsubscribe(sender)
      sender() ! Unsubscribed
    case Consume =>
      dispatchRecords(consumer.poll(CONSUMER_POLL_TIMEOUT))
      self ! Consume

    case _ => log.warning("unknown event")
  }

  protected def dispatchRecords(value: ConsumerRecords[K, V]): Unit =
    subscribed.foreach {
      case (_, sub) =>
        value.records(topic).forEach { record =>
          sub ! Record(record.value())
        }
    }

  private[this] def subscribe(ref: ActorRef): Unit =
    subscribed += (ref.hashCode -> ref)

  private[this] def unsubscribe(ref: ActorRef): Unit =
    subscribed -= ref.hashCode
}
