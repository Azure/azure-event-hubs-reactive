// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.scaladsl

import java.time.Instant

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl._
import com.microsoft.azure.reactiveeventhubs.config.{Configuration, IConfiguration}
import com.microsoft.azure.reactiveeventhubs.{Logger, EventHubsMessage, SourceOptions, StreamManager}

import scala.language.postfixOps

object EventHub {
  def apply(): EventHub = new EventHub()

  def apply(config: IConfiguration): EventHub = new EventHub(config)
}

/** Provides a streaming source to retrieve messages from Azure Event Hubs
  *
  * TODO: Provide ClearCheckpoints() method to clear the state
  */
class EventHub(config: IConfiguration)
  extends Logger {

  def this() = this(Configuration())

  private[this] val streamManager = new StreamManager

  private[this] def allPartitions = Some(0 until config.connect.eventHubPartitions)

  private[this] def fromStart = Some(List.fill[String](config.connect.eventHubPartitions)(EventHubPartition.OffsetStartOfStream))

  /** Stop the stream
    */
  def close(): Unit = streamManager.close()

  /** Stream returning all the messages from all the configured partitions.
    * If checkpointing the stream starts from the last position saved, otherwise
    * it starts from the beginning.
    *
    * @return A source of Event Hub messages
    */
  def source(): Source[EventHubsMessage, NotUsed] = source(SourceOptions().allPartitions.fromStart)

  /** Stream returning all the messages from all the requested partitions.
    * If checkpointing the stream starts from the last position saved, otherwise
    * it starts from the beginning.
    *
    * @param partitions Partitions to process
    *
    * @return A source of Event Hubs messages
    */
  def source(partitions: Seq[Int]): Source[EventHubsMessage, NotUsed] = source(SourceOptions().partitions(partitions))

  /** Stream returning all the messages starting from the given time, from all
    * the configured partitions.
    *
    * @param startTime Starting position expressed in time
    *
    * @return A source of Event Hubs messages
    */
  def source(startTime: Instant): Source[EventHubsMessage, NotUsed] = source(SourceOptions().fromTime(startTime))

  /** Stream returning all the messages, from the given starting point, optionally with
    * checkpointing
    *
    * @return A source of Event Hubs messages
    */
  def source(options: SourceOptions): Source[EventHubsMessage, NotUsed] = {

    val partitions: Seq[Int] = options.getPartitions(config.connect)

    val graph = GraphDSL.create() {
      implicit b ⇒
        import GraphDSL.Implicits._

        val merge = b.add(Merge[EventHubsMessage](partitions.size))

        for (partition ← partitions) {
          val graph = EventHubPartition(config, partition).source(options).via(streamManager)
          val source = Source.fromGraph(graph).async
          source ~> merge
        }

        SourceShape(merge.out)
    }

    Source.fromGraph(graph)
  }
}
