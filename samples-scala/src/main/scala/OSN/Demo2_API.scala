// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.More

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._

object Console {

  def apply() = Sink.foreach[EventHubsMessage] {
    m ⇒ println(s"enqueued-time: ${m.received}, offset: ${m.offset}, payload: ${m.contentAsString}")
  }
}

object Storage {

  def apply() = Sink.foreach[EventHubsMessage] {
    m ⇒ {
      /* ... write to storage ... */
    }
  }
}

object Demo extends App {

  EventHub()
    .source(java.time.Instant.now()) // <===
    .alsoTo(Storage()) // <===
    .to(Console())
    .run()
}
