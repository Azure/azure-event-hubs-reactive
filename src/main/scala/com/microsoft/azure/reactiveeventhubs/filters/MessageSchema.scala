// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.filters

import com.microsoft.azure.reactiveeventhubs.MessageFromDevice

object MessageSchema {
  def apply(messageSchema: String)(m: MessageFromDevice) = new MessageSchema(messageSchema).filter(m)
}

/** Filter by message type
  *
  * @param messageSchema Message type
  */
class MessageSchema(val messageSchema: String) {
  def filter(m: MessageFromDevice): Boolean = m.messageSchema == messageSchema
}






