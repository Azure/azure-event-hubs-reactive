// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.Checkpoints

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.MessageFromDevice
import com.microsoft.azure.reactiveeventhubs.filters.Device
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.SourceOptions
import com.microsoft.azure.reactiveeventhubs.filters.MessageSchema

object Console {

  def apply() = Sink.foreach[MessageFromDevice] {

    m ⇒ println(
      s"${m.received} - ${m.deviceId} - ${m.messageSchema}"
        + s" - ${m.contentAsString}")
  }
}

object Demo extends App {

  EventHub()

    .source(SourceOptions().saveOffsets()) // <===

    .filter(MessageSchema("temperature"))

    .filter(Device("device1000"))

    .to(Console())

    .run()
}
