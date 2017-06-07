// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.checkpointing.Backends

import com.microsoft.azure.reactiveeventhubs.checkpointing.ICPConfiguration

trait CheckpointBackend {

  def checkpointNamespace(cpconfig: ICPConfiguration): String = cpconfig.storageNamespace

  /** Read the offset of the last record processed for the given partition
    *
    * @param partition Event hub partition number
    *
    * @return Offset of the last record (already) processed
    */
  def readOffset(partition: Int): String

  /** Store the offset for the given Event hub partition
    *
    * @param partition Event hub partition number
    * @param offset    Event hub partition offset
    */
  def writeOffset(partition: Int, offset: String): Unit
}
