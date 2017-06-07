// Copyright (c) Microsoft. All rights reserved.

package OSN.Demo.More

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubMessage
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._

object Console {

  def apply() = Sink.foreach[EventHubMessage] {

    m ⇒ println(
      s"${m.received} "
        + s" - ${m.contentAsString}")

  }
}

object Storage {

  def apply() = Sink.foreach[EventHubMessage] {

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
