// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs.filters

import com.microsoft.azure.reactiveeventhubs.EventHubsMessage

/** Set of filters to ignore Event hub traffic
  *
  */
private[reactiveeventhubs] object Ignore {

  /** Ignore the keep alive signal injected by MessageFromDeviceSource
    *
    * @return True if the message must be processed
    */
  def ignore = (m: EventHubsMessage) ⇒ m.contentAsString == "ignore"
}
