// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs

import com.microsoft.azure.eventhubs.EventHubClient
import com.microsoft.azure.reactiveeventhubs.config.IConnectConfiguration
import com.microsoft.azure.servicebus.ConnectionStringBuilder

private case class EventHubStorage(config: IConnectConfiguration) extends Logger {

  // TODO: Manage transient errors e.g. timeouts
  // EventHubClient.createFromConnectionString(connString)
  //   .get(config.receiverTimeout, TimeUnit.MILLISECONDS)
  def createClient(): EventHubClient = {
    log.info("Creating EventHub client to {}", config.iotHubName)
    EventHubClient.createFromConnectionStringSync(buildConnString())
  }

  private[this] def buildConnString() =
    new ConnectionStringBuilder(
      config.iotHubNamespace,
      config.iotHubName,
      config.accessPolicy,
      config.accessKey).toString
}
