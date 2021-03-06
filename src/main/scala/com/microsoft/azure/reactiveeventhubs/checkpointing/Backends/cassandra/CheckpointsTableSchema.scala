// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra

import com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra.lib.{Column, ColumnType, TableSchema}

/** Schema of the table containing the checkpoints
  */
private[reactiveeventhubs] class CheckpointsTableSchema(keySpace: String, tableName: String) extends TableSchema {

  // Container name
  override val keyspace: String = keySpace

  // Table name
  override val name: String = tableName

  // Columns
  override val columns: Seq[Column] = Seq(
    Column("partition", ColumnType.Int, true),
    Column("offset", ColumnType.String),
    Column("lastUpdate", ColumnType.Timestamp)
  )
}
