// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.checkpointing.Backends

import com.microsoft.azure.reactiveeventhubs.Logger
import com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra.lib.Connection
import com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra.{CheckpointRecord, CheckpointsTableSchema}
import com.microsoft.azure.reactiveeventhubs.checkpointing.ICPConfiguration
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHubPartition
import org.json4s.JsonAST

/** Storage logic to write checkpoints to a Cassandra table
  */
private[reactiveeventhubs] class CassandraTable(cpconfig: ICPConfiguration) extends CheckpointBackend with Logger {

  val schema     = new CheckpointsTableSchema(checkpointNamespace(cpconfig), "checkpoints")
  val connection = Connection(cpconfig.cassandraCluster, cpconfig.cassandraReplicationFactor, cpconfig.cassandraAuth, schema)
  val table      = connection.getTable[CheckpointRecord]()

  connection.createKeyspaceIfNotExists()
  connection.createTableIfNotExists()

  /** Read the offset of the last record processed for the given partition
    *
    * @param partition Event hub partition number
    *
    * @return Offset of the last record (already) processed
    */
  override def readOffset(partition: Int): String = {
    val result: JsonAST.JObject = table.select(s"partition = ${partition}")

    if (result.values("partition").asInstanceOf[BigInt] < 0) {
      EventHubPartition.OffsetCheckpointNotFound
    } else {
      result.values("offset").asInstanceOf[String]
    }
  }

  /** Store the offset for the given Event hub partition
    *
    * @param partition Event hub partition number
    * @param offset    Event hub partition offset
    */
  override def writeOffset(partition: Int, offset: String): Unit = {
    table.updateRow(CheckpointRecord(partition, offset))
  }
}
