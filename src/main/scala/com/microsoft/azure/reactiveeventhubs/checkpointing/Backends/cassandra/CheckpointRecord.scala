// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra

import com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra.lib.ToCassandra
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

private[reactiveeventhubs] case class CheckpointRecord(partition: Int, offset: String)
  extends ToCassandra {

  /** Convert record to JSON
    *
    * @return JSON string
    */
  override def toJsonValues: String = {
    val now = java.time.Instant.now.toString
    val json = ("partition" -> partition) ~ ("offset" -> offset) ~ ("lastUpdate" -> now)
    compact(render(json))
  }
}
