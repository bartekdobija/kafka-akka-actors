package com.github.bartekdobija.serdes

import com.github.bartekdobija.serdes.JsonSerializerSpec.Log
import org.scalatest.WordSpec

object JsonSerializerSpec {
  case class Log(a:String, b: Double)
}

class JsonSerializerSpec extends WordSpec {

  "JsonSerializer" must {

    "serializer into byte array" in {

      val data = Log("ab",12.3)
      val jdata = "{\"a\":\"ab\",\"b\":12.3}"

      val x = new JsonSerializer().serialize("t", data)

      assertResult(jdata)(String.valueOf(x.map(_.toChar)))
      assert(x.length == 19)

    }

  }

}
