package com.github.bartekdobija.serdes

import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class JsonDeserializer extends Deserializer[JsonNode] {

  private[this] val objectMapper: ObjectMapper = new ObjectMapper()

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, bytes: Array[Byte]): JsonNode = {
    if (bytes == null) return null
    try {
      objectMapper.readTree(bytes)
    } catch {
      case e: Exception => throw new SerializationException(e)
    }
  }

  override def close(): Unit = {}

}
