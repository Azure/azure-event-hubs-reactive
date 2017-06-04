// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.More

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.MessageFromDevice
import com.microsoft.azure.reactiveeventhubs.filters.Device
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.filters.MessageSchema

object Console {

  def apply() = Sink.foreach[MessageFromDevice] {

    m ⇒ println(
      s"${m.received} - ${m.deviceId} - ${m.messageSchema}"
        + s" - ${m.contentAsString}")

  }
}

object Storage {

  def apply() = Sink.foreach[MessageFromDevice] {

    m ⇒ {
      /* ... write to storage ... */
    }

  }
}

object Demo extends App {

  EventHub()

    .source(java.time.Instant.now()) // <===

    .filter(MessageSchema("temperature")) // <===

    .filter(Device("device1000")) // <===

    .alsoTo(Storage()) // <===

    .to(Console())

    .run()
}
