package com.github.bartekdobija

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.scalatest.FlatSpec

case class Log(ts: Long, `type`: String, data: String)

class SandboxSpec extends FlatSpec {

  private val om = new ObjectMapper() with ScalaObjectMapper
  om.registerModule(DefaultScalaModule)

  "Sandbox" should "act as an experimentation area" in {

    val data = "{\"ts\": 12345, \"type\": \"com.github.bartekdobija.Log\", \"data\": \"a\"}"
    val o = om.readValue(data, classOf[Log])

    assertResult(12345)(o.ts)
    assertResult("a")(o.data)

  }

}
