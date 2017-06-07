// Copyright (c) Microsoft. All rights reserved.

package it.helpers

import java.nio.file.{Files, Paths}

import com.microsoft.azure.eventhubs.EventHubClient
import com.typesafe.config.{Config, ConfigFactory}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.reflect.io.File

/* Test configuration settings */
object Configuration {

  private[this] val confConnPath      = "eventhub-react.connection."
  private[this] val confStreamingPath = "eventhub-react.streaming."

  private[this] val conf: Config = ConfigFactory.load()

  // Read-only settings
  val eventHubNamespace : String = conf.getString(confConnPath + "namespace")
  val eventHubName      : String = conf.getString(confConnPath + "name")
  val eventHubPartitions: Int    = conf.getInt(confConnPath + "partitions")
  val accessPolicy      : String = conf.getString(confConnPath + "accessPolicy")
  val accessKey         : String = conf.getString(confConnPath + "accessKey")

  // Tests can override these
  var receiverConsumerGroup: String = EventHubClient.DEFAULT_CONSUMER_GROUP_NAME
  var receiverTimeout      : Long   = conf.getDuration(confStreamingPath + "receiverTimeout").toMillis
  var receiverBatchSize    : Int    = conf.getInt(confStreamingPath + "receiverBatchSize")
}
