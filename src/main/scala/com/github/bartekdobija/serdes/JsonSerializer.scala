package com.github.bartekdobija.serdes

import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer extends Serializer[Any] {

  protected val om = new ObjectMapper() with ScalaObjectMapper
  om.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Any): Array[Byte] = {
    if (data == null) return null
    try {
      om.writeValueAsBytes(data)
    } catch {
      case e: Exception =>
        throw new SerializationException("Error serializing JSON message", e)
    }
  }

  override def close(): Unit = {}

}
