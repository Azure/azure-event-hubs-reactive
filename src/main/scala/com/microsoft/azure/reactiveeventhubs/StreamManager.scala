// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

private[reactiveeventhubs] class StreamManager
  extends GraphStage[FlowShape[EventHubMessage, EventHubMessage]] {

  private[this] val in          = Inlet[EventHubMessage]("StreamCanceller.Flow.in")
  private[this] val out         = Outlet[EventHubMessage]("StreamCanceller.Flow.out")
  private[this] var closeSignal = false

  override val shape = FlowShape.of(in, out)

  def close(): Unit = closeSignal = true

  override def createLogic(attr: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val message: EventHubMessage = grab(in)
          push(out, message)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (closeSignal) {
            cancel(in)
          } else {
            pull(in)
          }
        }
      })
    }
  }
}
