// Copyright (c) Microsoft. All rights reserved.

package it

import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import org.scalatest._

class EventHubReactSimpleAPI extends FeatureSpec with GivenWhenThen {

  info("As a client of Azure Event hub")
  info("I want to be able to receive messages as a stream")
  info("So I can process them asynchronously and at scale")

  Feature("Event Hub React API") {

    Scenario("Developer wants to retrieve Event hub messages") {

      Given("An Event hub is configured")
      val hub = EventHub()

      When("A developer wants to fetch messages from Azure Event hub")
      val messagesFromAllPartitions: Source[EventHubsMessage, NotUsed] = hub.source()
      val messagesFromNowOn: Source[EventHubsMessage, NotUsed] = hub.source(Instant.now())

      Then("The messages are presented as a stream")
      messagesFromAllPartitions.to(Sink.ignore)
      messagesFromNowOn.to(Sink.ignore)

      hub.close()
    }
  }
}
