// Copyright (c) Microsoft. All rights reserved.

package C_Throughput

import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.scaladsl._

import scala.concurrent.duration._
import scala.language.postfixOps

/** Measure the streaming throughput and show how many messages are left
  */
object Demo extends App {

  val showStatsEvery = 1 second

  // Messages throughput monitoring sink
  val monitor = Sink.foreach[EventHubsMessage] {
    m ⇒ {
      Monitoring.total += 1

      val partition = m.runtimeInfo.partitionInfo.partitionNumber.get
      Monitoring.totals(partition) += 1
      Monitoring.remain(partition) = if (m.runtimeInfo.partitionInfo.lastSequenceNumber.isEmpty) 0
                                     else m.runtimeInfo.partitionInfo.lastSequenceNumber.get - m.sequenceNumber
    }
  }

  // Start processing the stream
  EventHub().source
    .to(monitor)
    .run()

  // Print statistics at some interval
  Monitoring.printStatisticsWithFrequency(showStatsEvery)
}
