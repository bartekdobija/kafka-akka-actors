package com.github.bartekdobija.serdes

import com.github.bartekdobija.serdes.JsonSerializerSpec.Log
import org.scalatest.WordSpec

object JsonSerializerSpec {
  case class Log(a: String, b: Seq[Seq[String]])
}

class JsonSerializerSpec extends WordSpec {

  private val serializer = new JsonSerializer()

  private val log = Log("a", Seq(Seq("a"), Seq("b")))
  private val jlog = "{\"a\":\"a\",\"b\":[[\"a\"],[\"b\"]]}"

  "JsonSerializer" must {

    "serializer into byte array" in {
      assertResult(jlog){
        String.valueOf(serializer.serialize("t", log).map(_.toChar))
      }
    }

  }

}
