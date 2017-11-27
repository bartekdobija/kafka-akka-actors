package com.github.bartekdobija.actors

import akka.actor.ActorRef
import com.github.bartekdobija.actors.KafkaJsonProducerSpec.Log
import com.github.bartekdobija.actors.KafkaProducerActor.Message

object KafkaJsonProducerSpec {
  case class Log(a: Int, b: String)
}

class KafkaJsonProducerSpec extends ActorSpec {

  private var actor: ActorRef = _
  private val topic = getClass.getSimpleName

  classOf[KafkaJsonProducerActor].getSimpleName must {

    "produce JSON messages" in {
      withRunningKafka {
        createCustomTopic(topic)
        actor = system.actorOf(KafkaJsonProducerActor.props(bootstrap))

        actor ! Message(topic, Log(1,"a"))
        assertResult("{\"a\":1,\"b\":\"a\"}")(consumeFirstStringMessageFrom(topic))

      }
    }

  }

}
