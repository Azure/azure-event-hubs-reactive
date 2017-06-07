// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.Simple

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._

object Console {

  def apply() = Sink.foreach[EventHubsMessage] {

    m ⇒ println(
      s"${m.received} "
        + s" - ${m.contentAsString}")

  }
}

object Demo extends App {

  EventHub()

    .source()

    .to(Console())

    .run()
}
