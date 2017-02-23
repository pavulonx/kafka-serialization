import sbt.Keys._
import sbt._
import sbt.impl.GroupArtifactID

object Dependencies {

  def libraryVersion(groupArtifactID: GroupArtifactID, version211: String, version212: String) = Def.setting(scalaBinaryVersion.value match {
    case "2.11" => groupArtifactID % version211
    case "2.12" => groupArtifactID % version212
  })

  object Akka {

    private val version211 = "2.3.16"
    private val version212 = "2.4.16"

    val actor = libraryVersion("com.typesafe.akka" %% s"akka-actor", version211, version212)
    val testKit = libraryVersion("com.typesafe.akka" %% s"akka-testkit", version211, version212)

  }

  object Typesafe {

    private val version = "1.3.1"

    val config = "com.typesafe" % "config" % version
  }

  object slf4j {

    private val version = "1.7.22"

    val api = "org.slf4j" % "slf4j-api" % version
    val log4jOverSlf4j = "org.slf4j" % "log4j-over-slf4j" % version
    val jclOverSlf4j = "org.slf4j" % "jcl-over-slf4j" % version
    val nop = "org.slf4j" % "slf4j-nop" % version
  }

  object log4j {

    private val version = "2.7"

    val log4jToSlf4j = "org.apache.logging.log4j" % "log4j-to-slf4j" % version
  }

  object logback {

    private val version = "1.1.8"

    val core = "ch.qos.logback" % "logback-core" % version
    val classic = "ch.qos.logback" % "logback-classic" % version
  }

  object scalaTest {

    private val version = "3.0.1"

    val scalaTest = "org.scalatest" %% "scalatest" % version

  }

  object scalaCheck {

    private val version = "1.13.4"

    val scalaCheck = "org.scalacheck" %% "scalacheck" % version

  }

  object Specs2 {

    private val version211 = "3.7"
    private val version212 = "2.4.17"

    val core = libraryVersion("org.specs2" %% "specs2-core", version211, version212)
    val mock = libraryVersion("org.specs2" %% "specs2-mock", version211, version212)

  }

  object scalaMock {

    private val version = "3.4.2"

    val scalaTestSupport = "org.scalamock" %% "scalamock-scalatest-support" % version
  }

  object kafka {

    private val version = "0.10.0.1"

    val avroSerializer = "io.confluent" % "kafka-avro-serializer" % "3.1.1" exclude("org.slf4j", "slf4j-log4j12")
    val client = "org.apache.kafka" % "kafka-clients" % version exclude("org.slf4j", "slf4j-log4j12")
  }

  object Circe {

    private val version = "0.6.1"

    val core = "io.circe" %% "circe-core" % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser = "io.circe" %% "circe-parser" % version
  }

  object Avro4s {

    private val version = "1.6.3"

    val core = "com.sksamuel.avro4s" %% "avro4s-core" % version
    val macros = "com.sksamuel.avro4s" %% "avro4s-macros" % version
    val json = "com.sksamuel.avro4s" %% "avro4s-json" % version
  }

  object Json4s {

    private val version211 = "3.3.0"
    private val version212 = "3.5.0"

    val core = libraryVersion("org.json4s" %% "json4s-core", version211, version212)
    val native = libraryVersion("org.json4s" %% "json4s-native", version211, version212)

  }

  object Wiremock {

    private val version = "2.4.1"

    val wiremock = "com.github.tomakehurst" % "wiremock" % version

  }

  val l = libraryDependencies

  val core = l ++= Seq(kafka.client)

  val json4s = l ++= Seq(Json4s.core.value, Json4s.native.value)

  val avro4s = l ++= Seq(Avro4s.core, kafka.avroSerializer)

  val circe = l ++= Seq(Circe.core, Circe.parser, Circe.generic % Test)

  val client = l ++= Seq(Akka.actor.value % Provided, Typesafe.config % Provided, Specs2.core.value % Test, Specs2.mock.value % Test)

  val testkit = l ++= Seq(Akka.testKit.value, scalaTest.scalaTest, scalaCheck.scalaCheck, Typesafe.config, scalaMock.scalaTestSupport, logback.classic, Wiremock.wiremock)

}
