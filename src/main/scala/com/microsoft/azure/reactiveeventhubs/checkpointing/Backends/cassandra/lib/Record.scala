// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.checkpointing.Backends.cassandra.lib

/** Trait to be implemented by records to be stored into Cassandra
  */
private[reactiveeventhubs] trait ToCassandra {
  def toJsonValues: String
}
