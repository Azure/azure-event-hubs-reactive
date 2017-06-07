// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.Checkpoints

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.SourceOptions

object Console {

  def apply() = Sink.foreach[EventHubsMessage] {
    m ⇒ println(s"enqueued-time: ${m.received}, offset: ${m.offset}, payload: ${m.contentAsString}")
  }
}

object Demo extends App {

  EventHub()
    .source(SourceOptions().saveOffsets()) // <===
    .to(Console())
    .run()
}
