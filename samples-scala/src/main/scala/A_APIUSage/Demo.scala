// Copyright (c) Microsoft. All rights reserved.

package A_APIUSage

import java.time.Instant

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.scaladsl._
import com.microsoft.azure.reactiveeventhubs.{EventHubMessage, SourceOptions}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

/** Stream all messages from beginning
  */
object AllMessagesFromBeginning extends App {

  println("Streaming all the messages")

  val messages = EventHub().source()

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Stream recent messages
  */
object OnlyRecentMessages extends App {

  println("Streaming recent messages")

  val messages = EventHub().source(java.time.Instant.now())

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Stream only from partitions 0 and 3
  */
object OnlyTwoPartitions extends App {

  val Partition1 = 0
  val Partition2 = 3

  println(s"Streaming messages from partitions ${Partition1} and ${Partition2}")

  val messages = EventHub().source(Seq(Partition1, Partition2))

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Stream and save the position while streaming
  */
object StoreOffsetsWhileStreaming extends App {

  println(s"Streaming messages and save offsets while streaming")

  val messages = EventHub().source(SourceOptions().saveOffsets())

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Streaming messages from a saved position, without updating the position stored
  */
object StartFromStoredOffsetsButDontWriteNewOffsets extends App {

  println(s"Streaming messages from a saved position, without updating the position stored")

  val messages = EventHub().source(SourceOptions().fromSavedOffsets())

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Streaming messages from a saved position, without updating the position stored.
  * If there is no position saved, start from one hour in the past.
  */
object StartFromStoredOffsetsIfAvailableOrByTimeOtherwise extends App {

  println(s"Streaming messages from a saved position, without updating the position stored. If there is no position saved, start from one hour in the past.")

  val messages = EventHub().source(SourceOptions().fromSavedOffsets(Instant.now().minusSeconds(3600)))

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

object StreamIncludingRuntimeInformation extends App {

  println(s"Stream messages and print how many messages are left in each partition.")

  val messages = EventHub().source(SourceOptions().fromStart().withRuntimeInfo())

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"Partition ${m.runtimeInfo.partitionInfo.partitionNumber.get}: " +
      s"${m.runtimeInfo.partitionInfo.lastSequenceNumber.get - m.sequenceNumber} messages left to stream")
  }

  messages

    .to(console)

    .run()
}

object MultipleStreamingOptionsAndSyntaxSugar extends App {

  println(s"Streaming messages and save position")

  val options = SourceOptions()
    .partitions(0, 2, 3)
    .fromOffsets("614", "64365", "123512")
    .saveOffsets()

  val messages = EventHub().source(options)

  val console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages

    .to(console)

    .run()
}

/** Stream to 2 different consoles
  */
object MultipleDestinations extends App {

  println("Streaming to two different consoles")

  val messages = EventHub().source(java.time.Instant.now())

  val console1 = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"Temperature console: ${m.received} - ${m.contentAsString}")
  }

  val console2 = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"Humidity console: ${m.received} - ${m.contentAsString}")
  }

  messages

    .alsoTo(console1)

    .to(console2)

    .run()
}

/** Show how to close the stream, terminating the connections to Azure IoT hub
  */
object CloseStream extends App {

  println("Streaming all the messages, will stop in 5 seconds")

  implicit val system = akka.actor.ActorSystem("system")

  system.scheduler.scheduleOnce(5 seconds) {
    hub.close()
  }

  val hub      = EventHub()
  val messages = hub.source()

  var console = Sink.foreach[EventHubMessage] {
    m ⇒ println(s"${m.received} - ${m.contentAsString}")
  }

  messages.to(console).run()
}
