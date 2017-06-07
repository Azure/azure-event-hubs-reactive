// Copyright (c) Microsoft. All rights reserved.

package it.helpers

import com.microsoft.azure.eventhubs

class Publisher (publisherId: String) extends Logger {

  private var ready      = true
  private val waitOnSend = 20000
  private val waitUnit   = 50

//  private[this] class EventCallback extends IotHubEventCallback {
//    override def execute(status: IotHubStatusCode, context: scala.Any): Unit = {
//      ready = true
//      val i = context.asInstanceOf[Int]
//      log.debug("{}: Message {} status {}", deviceId, i, status.name())
//
//      // Sleep to avoid being throttled
//      Thread.sleep(50)
//    }
//  }

  // Load device credentials
  private[this] lazy val credentials = Configuration.deviceCredentials(publisherId)

  // Prepare connection string for this device
  private[this] lazy val connString = DeviceConnectionString.build(
    Configuration.iotHubName, credentials.deviceId, credentials.primaryKey)

  // Prepare client to send messages
  private[this] lazy val client = {
    log.info("Opening connection for device '{}'", publisherId)
    //new DeviceClient(connString, IotHubClientProtocol.AMQPS)
  }

  def sendMessage(text: String, sequenceNumber: Int): Unit = {

    if (!ready) {
      waitConfirmation()
      if (!ready) throw new RuntimeException(s"Device '${publisherId}', the client is busy")
    }

    ready = false

    // Open internally checks if it is already connected
    //client.open()

    log.debug("Device '{}' sending '{}'", publisherId, text)
    //val message = new Message(text)
    //client.sendEventAsync(message, new EventCallback(), sequenceNumber)
  }

  def waitConfirmation(): Unit = {

    log.debug("Device '{}' waiting for confirmation...", publisherId)

    var wait = waitOnSend
    if (!ready) while (wait > 0 && !ready) {
      Thread.sleep(waitUnit)
      wait -= waitUnit
    }

    if (!ready) log.debug("Device '{}', confirmation not received", publisherId)
  }

  def disconnect(): Unit = {
    //client.close()
    log.debug("Device '{}' disconnected", publisherId)
  }
}