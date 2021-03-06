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

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class AllMessagesAreDelivered extends FeatureSpec with GivenWhenThen {

  // TODO: we should use tags
  if (!sys.env.contains("TRAVIS_PULL_REQUEST") || sys.env("TRAVIS_PULL_REQUEST") == "false") {

    info("As a client of Azure Event hub")
    info("I want to be able to receive all device messages")
    info("So I can process them all")

    // A label shared by all the messages, to filter out data sent by other tests
    val testRunId: String = s"[${this.getClass.getName}-" + java.util.UUID.randomUUID().toString + "]"

    val counter = actorSystem.actorOf(Props[Counter], this.getClass.getName + "Counter")
    counter ! "reset"

    def readCounter: Long = {
      Await.result(counter.ask("get")(5 seconds), 5 seconds).asInstanceOf[Long]
    }

    Feature("All Event hub messages are delivered") {

      Scenario("Application wants to retrieve all Event hub messages") {

        // How many seconds we allow the test to wait for messages from the stream
        val TestTimeout = 60 seconds
        val DevicesCount = 5
        val MessagesPerDevice = 3
        val expectedMessageCount = DevicesCount * MessagesPerDevice

        // Create devices
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
          }
          for (deviceNumber ← 0 until DevicesCount) devices(deviceNumber).waitConfirmation()
        }

        for (deviceNumber ← 0 until DevicesCount) devices(deviceNumber).disconnect()

        log.info("Messages sent: {}", expectedMessageCount)

        When("A client application processes messages from the stream")
        counter ! "reset"
        val count = Sink.foreach[EventHubsMessage] {
          m ⇒ counter ! "inc"
        }

        messages
          .filter(m ⇒ m.contentAsString contains testRunId)
          .runWith(count)

        Then("Then the client application receives all the messages sent")
        var time = TestTimeout.toMillis.toInt
        val pause = time / 10
        var actualMessageCount = readCounter
        while (time > 0 && actualMessageCount < expectedMessageCount) {
          Thread.sleep(pause)
          time -= pause
          actualMessageCount = readCounter
          log.info("Messages received so far: {} of {} [Time left {} secs]", actualMessageCount, expectedMessageCount, time / 1000)
        }

        hub.close()

        assert(actualMessageCount == expectedMessageCount,
          s"Expecting ${expectedMessageCount} messages but received ${actualMessageCount}")
      }
    }
  }
}
