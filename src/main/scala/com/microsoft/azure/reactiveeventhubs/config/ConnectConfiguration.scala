// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.config

import com.typesafe.config.{Config, ConfigFactory}

trait IConnectConfiguration {

  // Event Hub namespace
  val eventHubNamespace: String

  // Event Hub name
  val eventHubName: String

  // Event hub partitions number
  val eventHubPartitions: Int

  // Event hub access policy name
  val accessPolicy: String

  // Event hub access policy key
  val accessKey: String
}

object ConnectConfiguration {

  def apply(): IConnectConfiguration = new ConnectConfiguration()

  def apply(configData: Config): IConnectConfiguration = new ConnectConfiguration(configData)
}

/** Hold settings to connect to Event Hub
  */
class ConnectConfiguration(configData: Config) extends IConnectConfiguration {

  // Parameterless ctor
  def this() = this(ConfigFactory.load)

  private[this] val confConnPath = "eventhub-react.connection."

  lazy val eventHubNamespace  = getNamespaceFromEndpoint(configData.getString(confConnPath + "eventHubEndpoint"))
  lazy val eventHubName       = configData.getString(confConnPath + "eventHubName")
  lazy val eventHubPartitions = configData.getInt(confConnPath + "eventHubPartitions")
  lazy val accessPolicy     = configData.getString(confConnPath + "accessPolicy")
  lazy val accessKey        = configData.getString(confConnPath + "accessKey")

  /** Extract namespace from endpoint string
    *
    * @param endpoint Endpoint string
    *
    * @return namespace
    */
  private[this] def getNamespaceFromEndpoint(endpoint: String): String = {
    endpoint.replaceFirst(".*://", "").replaceFirst("\\..*", "")
  }
}
