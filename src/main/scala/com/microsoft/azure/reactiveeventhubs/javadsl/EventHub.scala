// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.javadsl

import java.time.Instant

import akka.NotUsed
import akka.stream.javadsl.{Source ⇒ JavaSource}
import com.microsoft.azure.reactiveeventhubs.config.{Configuration, IConfiguration}
import com.microsoft.azure.reactiveeventhubs.scaladsl.{EventHub ⇒ IoTHubScalaDSL}
import com.microsoft.azure.reactiveeventhubs.{MessageFromDevice, SourceOptions}

/** Provides a streaming source to retrieve messages from Azure IoT Hub
  */
class EventHub(config: IConfiguration) {

  // Parameterless ctor
  def this() = this(Configuration())

  private lazy val iotHub = IoTHubScalaDSL(config)

  /** Stop the stream
    */
  def close(): Unit = iotHub.close()

  /** Stream returning all the messages since the beginning, from all the
    * configured partitions.
    *
    * @return A source of IoT messages
    */
  def source(): JavaSource[MessageFromDevice, NotUsed] = new JavaSource(iotHub.source())

  /** Stream returning all the messages from all the requested partitions.
    * If checkpointing the stream starts from the last position saved, otherwise
    * it starts from the beginning.
    *
    * @param partitions Partitions to process
    *
    * @return A source of IoT messages
    */
  def source(partitions: java.util.List[java.lang.Integer]): JavaSource[MessageFromDevice, NotUsed] = {
    new JavaSource(iotHub.source(SourceOptions().partitions(partitions)))
  }

  /** Stream returning all the messages starting from the given time, from all
    * the configured partitions.
    *
    * @param startTime Starting position expressed in time
    *
    * @return A source of IoT messages
    */
  def source(startTime: Instant): JavaSource[MessageFromDevice, NotUsed] = {
    new JavaSource(iotHub.source(startTime))
  }

  /** Stream events using the requested options
    *
    * @param options Set of streaming options
    *
    * @return A source of IoT messages
    */
  def source(options: SourceOptions): JavaSource[MessageFromDevice, NotUsed] = {
    new JavaSource(iotHub.source(options))
  }
}
