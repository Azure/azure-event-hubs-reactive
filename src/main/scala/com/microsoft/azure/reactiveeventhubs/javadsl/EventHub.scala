// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.javadsl

import java.time.Instant

import akka.NotUsed
import akka.stream.javadsl.{Source ⇒ JavaSource}
import com.microsoft.azure.reactiveeventhubs.config.{Configuration, IConfiguration}
import com.microsoft.azure.reactiveeventhubs.scaladsl.{EventHub => EventHubDsl}
import com.microsoft.azure.reactiveeventhubs.{EventHubsMessage, SourceOptions}

/** Provides a streaming source to retrieve messages from Azure Event Hub
  */
class EventHub(config: IConfiguration) {

  // Parameterless ctor
  def this() = this(Configuration())

  private lazy val eventHub = EventHubDsl(config)

  /** Stop the stream
    */
  def close(): Unit = eventHub.close()

  /** Stream returning all the messages since the beginning, from all the
    * configured partitions.
    *
    * @return A source of Event hub messages
    */
  def source(): JavaSource[EventHubsMessage, NotUsed] = new JavaSource(eventHub.source())

  /** Stream returning all the messages from all the requested partitions.
    * If checkpointing the stream starts from the last position saved, otherwise
    * it starts from the beginning.
    *
    * @param partitions Partitions to process
    *
    * @return A source of Event hub messages
    */
  def source(partitions: java.util.List[java.lang.Integer]): JavaSource[EventHubsMessage, NotUsed] = {
    new JavaSource(eventHub.source(SourceOptions().partitions(partitions)))
  }

  /** Stream returning all the messages starting from the given time, from all
    * the configured partitions.
    *
    * @param startTime Starting position expressed in time
    *
    * @return A source of Event hub messages
    */
  def source(startTime: Instant): JavaSource[EventHubsMessage, NotUsed] = {
    new JavaSource(eventHub.source(startTime))
  }

  /** Stream events using the requested options
    *
    * @param options Set of streaming options
    *
    * @return A source of Event hub messages
    */
  def source(options: SourceOptions): JavaSource[EventHubsMessage, NotUsed] = {
    new JavaSource(eventHub.source(options))
  }
}
