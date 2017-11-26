package com.github.bartekdobija.serdes

import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer extends Serializer[JsonNode] {

  private val objectMapper = new ObjectMapper()

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: JsonNode): Array[Byte] = {
    if (data == null) return null
    try {
      objectMapper.writeValueAsBytes(data)
    } catch {
      case e: Exception =>
        throw new SerializationException("Error serializing JSON message", e)
    }
  }

  override def close(): Unit = {}

}
