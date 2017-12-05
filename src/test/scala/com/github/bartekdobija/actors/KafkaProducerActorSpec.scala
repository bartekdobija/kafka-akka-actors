package com.github.bartekdobija.actors

import akka.actor.ActorRef
import com.github.bartekdobija.actors.KafkaProducerActor.Message

class KafkaProducerActorSpec extends ActorSpec {

  private var actor: ActorRef = _
  private val topic = getClass.getSimpleName

  classOf[KafkaConsumerActor[_, _]].getSimpleName must {

    "produce messages" in {
      withRunningKafka {
        createCustomTopic(topic)
        actor = system.actorOf(KafkaProducerActor.props[Long, String](bootstrap))

        val m = "test"
        actor ! Message(topic, m.getBytes())
        assertResult(m)(consumeFirstStringMessageFrom(topic))

      }
    }
  }

}
