// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.Simple

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.MessageFromDevice
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._

object Console {

  def apply() = Sink.foreach[MessageFromDevice] {

    m ⇒ println(
      s"${m.received} - ${m.deviceId} - ${m.messageSchema}"
        + s" - ${m.contentAsString}")

  }
}

object Demo extends App {

  EventHub()

    .source()

    .to(Console())

    .run()
}
