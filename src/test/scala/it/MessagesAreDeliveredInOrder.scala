// Copyright (c) Microsoft. All rights reserved.

package it

import java.time.Instant

import akka.actor.Props
import akka.pattern.ask
import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.SourceOptions
import it.helpers.{Counter, Publisher}
import org.scalatest._

import scala.collection.parallel.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class MessagesAreDeliveredInOrder extends FeatureSpec with GivenWhenThen {

  // TODO: we should use tags
  if (!sys.env.contains("TRAVIS_PULL_REQUEST") || sys.env("TRAVIS_PULL_REQUEST") == "false") {

    info("As a client of Azure Event hub")
    info("I want to receive the messages in order")
    info("So I can process them in order")

    // A label shared by all the messages, to filter out data sent by other tests
    val testRunId: String = s"[${this.getClass.getName}-" + java.util.UUID.randomUUID().toString + "]"

    val counter = actorSystem.actorOf(Props[Counter], this.getClass.getName + "Counter")
    counter ! "reset"

    def readCounter: Long = {
      Await.result(counter.ask("get")(5 seconds), 5 seconds).asInstanceOf[Long]
    }

    Feature("Event hub messages are delivered in order") {

      // Note: messages are sent in parallel to obtain some level of mix in the
      // storage, so do not refactor, i.e. don't do one device at a time.
      Scenario("Customer needs to process Event hub messages in the right order") {

        // How many seconds we allow the test to wait for messages from the stream
        val TestTimeout = 120 seconds
        val DevicesCount = 25
        val MessagesPerDevice = 100
        val expectedMessageCount = DevicesCount * MessagesPerDevice

        // Initialize device objects
        val devices = new collection.mutable.ListMap[Int, Publisher]()
        for (deviceNumber ← 0 until DevicesCount) devices(deviceNumber) = new Publisher("device" + (10000 + deviceNumber))

        // We'll use this as the streaming start date
        val startTime = Instant.now().minusSeconds(30)
        log.info("Test run: {}, Start time: {}", testRunId, startTime)

        Given("An Event hub is configured")
        val hub = EventHub()
        val messages = hub.source(SourceOptions().fromTime(startTime))

        And(s"${DevicesCount} devices have sent ${MessagesPerDevice} messages each")
        for (msgNumber ← 1 to MessagesPerDevice) {
          for (deviceNumber ← 0 until DevicesCount) {
            devices(deviceNumber).sendMessage(testRunId, msgNumber)
            // temporary workaround for issue 995
            if (msgNumber == 1) devices(deviceNumber).waitConfirmation()
          }

          for (deviceNumber ← 0 until DevicesCount) devices(deviceNumber).waitConfirmation()
        }
        for (deviceNumber ← 0 until DevicesCount) devices(deviceNumber).disconnect()
        log.info("Messages sent: {}", expectedMessageCount)

        When("A client application processes messages from the stream")

        Then("Then the client receives all the messages ordered within each device")
        counter ! "reset"
        val cursors = new mutable.ParHashMap[String, Long]
        val verifier = Sink.foreach[EventHubsMessage] {
          m ⇒ {
            counter ! "inc"
            log.debug("seq: {} ", m.sequenceNumber)
          }
        }

        messages
          .filter(m ⇒ m.contentAsString contains (testRunId))
          .runWith(verifier)

        // Wait till all messages have been verified
        var time = TestTimeout.toMillis.toInt
        val pause = time / 12
        var actualMessageCount = readCounter
        while (time > 0 && actualMessageCount < expectedMessageCount) {
          Thread.sleep(pause)
          time -= pause
          actualMessageCount = readCounter
          log.info("Messages received so far: {} of {} [Time left {} secs]", actualMessageCount, expectedMessageCount, time / 1000)
        }

        log.info("Stopping stream")
        hub.close()

        log.info("actual messages {}", actualMessageCount)

        assert(
          actualMessageCount == expectedMessageCount,
          s"Expecting ${expectedMessageCount} messages but received ${actualMessageCount}")
      }
    }
  }
}
