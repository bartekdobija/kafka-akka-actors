package com.github.bartekdobija.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.manub.embeddedkafka.EmbeddedKafka
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

private[actors] abstract class ActorSpec
  extends TestKit(ActorSystem("TestActorSystem"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with EmbeddedKafka {

  val bootstrap = "localhost:6001"

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}
