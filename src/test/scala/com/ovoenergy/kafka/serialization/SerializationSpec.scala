package com.ovoenergy.kafka.serialization

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import com.ovoenergy.UnitSpec
import Serialization._
import Serialization.Implicits._
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

class SerializationSpec extends UnitSpec {

  import StandardCharsets._

  val stringSerializer: Serializer[String] = (s: String) => s.getBytes(UTF_8)
  val stringDeserializer: Deserializer[String] = (data: Array[Byte]) => new String(data, UTF_8)

  val intSerializer: Serializer[Int] = {(i: Int) =>
    ByteBuffer.allocate(4).putInt(i).array()
  }

  val intDeserializer: Deserializer[Int] = {(data: Array[Byte]) =>
    ByteBuffer.wrap(data).getInt
  }


  "Serialization" when {

    "deserializing" should {

      "add the magic byte to the serialized data" in {
        serializerWithMagicByte(Format.Json, stringSerializer).serialize("test", "Test")(0) should be(Format.toByte(Format.Json))
      }

      "demultiplex the magic byte correctly" in {

        val serializer = serializerWithMagicByte(Format.Json, stringSerializer)
        val deserializer: Deserializer[String] = deserializerWithMagicByteDemultiplexer(
          Format.Json -> deserializerWithFirstByteDropping(stringDeserializer),
          Format.AvroBinaryWithSchema -> deserializerWithFirstByteDropping({ data: Array[Byte] => new String(data.map(b => (b + 1).asInstanceOf[Byte]), UTF_8) }) // change the byte value
        )

        val expectedString = "TestString"
        val deserialized = deserializer.deserialize("test-topic", serializer.serialize("test-topic", expectedString))

        deserialized shouldBe expectedString
      }

      "skip the magic byte" in {

        val expectedBytes = "test string".getBytes(UTF_8)
        val deserializer = deserializerWithFirstByteDropping { data: Array[Byte] => data }

        deserializer.deserialize("test-topic", Array(12: Byte) ++ expectedBytes).deep shouldBe expectedBytes.deep
      }

      "demultiplex the topic correctly" in {

        val stringTopic = "string-topic"
        val intTopic = "int-topic"

        // This code is nasty, but in production no one is going to have a consumer with two unrelated types.
        val deserializer = deserializerWithTopicDemultiplexer(
          TopicMatcher.equalsTo(stringTopic)->stringDeserializer.asInstanceOf[Deserializer[Any]],
          TopicMatcher.equalsTo(intTopic)->intDeserializer.asInstanceOf[Deserializer[Any]]
        )

        val expectedInt = 34

        val deserialized = deserializer.deserialize(intTopic, intSerializer.serialize("Does not matter", expectedInt))

        deserialized shouldBe Some(expectedInt)
      }

    }
  }


}