// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.reactiveeventhubs

import java.time.Instant

import com.microsoft.azure.reactiveeventhubs.config.IConnectConfiguration
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHubPartition

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object SourceOptions {
  def apply() = new SourceOptions
}

/** Source streaming options
  *
  * TODO: differentiate checkpoint type: automatic (concurrent) or manual (user at-least-once)
  */
class SourceOptions() {

  private[this] var _allPartitions          : Boolean             = true
  private[this] var _partitions             : Option[Seq[Int]]    = None
  private[this] var _isFromStart            : Boolean             = true
  private[this] var _isFromOffsets          : Boolean             = false
  private[this] var _isFromTime             : Boolean             = false
  private[this] var _isFromCheckpoint       : Boolean             = false
  private[this] var _startTime              : Option[Instant]     = None
  private[this] var _startTimeOnNoCheckpoint: Option[Instant]     = None
  private[this] var _startOffsets           : Option[Seq[String]] = None
  private[this] var _isSaveOffsets          : Boolean             = false
  private[this] var _isWithRuntimeInfo      : Boolean             = false

  /** Set the options to retrieve events from all the Event Hubs partitions
    *
    * @return Current instance
    */
  def allPartitions(): SourceOptions = {
    _allPartitions = true
    _partitions = None
    this
  }

  /** Define from which Event Hubs partitions to retrieve events
    *
    * @param values List of partitions
    *
    * @return Current instance
    */
  def partitions[X: ClassTag](values: Int*): SourceOptions = {
    _partitions = Some(values)
    _allPartitions = false
    this
  }

  /** Define from which Event Hubs partitions to retrieve events
    *
    * @param values List of partitions
    *
    * @return Current instance
    */
  def partitions(values: Seq[Int]): SourceOptions = partitions(values: _ *)

  /** Define from which Event Hubs partitions to retrieve events
    *
    * @param values List of partitions
    *
    * @return Current instance
    */
  def partitions(values: java.util.List[java.lang.Integer]): SourceOptions = partitions(values.asScala.map(_.intValue()))

  /** Define from which Event Hubs partitions to retrieve events
    *
    * @param values List of Event Hubs partitions
    *
    * @return Current instance
    */
  def partitions(values: Array[Int]): SourceOptions = partitions(values.toSeq)

  /** Set the options to retrieve events from the beginning of the stream
    *
    * @return Current instance
    */
  def fromStart(): SourceOptions = {
    _isFromStart = true
    _isFromOffsets = false
    _isFromTime = false
    _isFromCheckpoint = false
    _startTime = None
    _startTimeOnNoCheckpoint = None
    _startOffsets = None
    this
  }

  /** Set the options to retrieve events from a specific time
    *
    * @param value Start time
    *
    * @return Current instance
    */
  def fromTime(value: Instant): SourceOptions = {
    _startTime = Some(value)
    _isFromStart = false
    _isFromOffsets = false
    _isFromTime = true
    _isFromCheckpoint = false
    _startTimeOnNoCheckpoint = None
    _startOffsets = None
    this
  }

  /** Set the options to start streaming for the specified offsets
    *
    * @param values List of offsets
    *
    * @return Current instance
    */
  def fromOffsets[X: ClassTag](values: String*): SourceOptions = {
    _startOffsets = Some(values)
    _isFromStart = false
    _isFromOffsets = true
    _isFromTime = false
    _isFromCheckpoint = false
    _startTime = None
    _startTimeOnNoCheckpoint = None
    this
  }

  /** Set the options to start streaming for the specified offsets
    *
    * @param values List of offsets
    *
    * @return Current instance
    */
  def fromOffsets(values: Seq[String]): SourceOptions = fromOffsets(values: _ *)

  /** Set the options to start streaming for the specified offsets
    *
    * @param values List of offsets
    *
    * @return Current instance
    */
  def fromOffsets(values: java.util.List[java.lang.String]): SourceOptions = fromOffsets(values.asScala)

  /** Set the options to start streaming for the specified offsets
    *
    * @param values List of offsets
    *
    * @return Current instance
    */
  def fromOffsets(values: Array[java.lang.String]): SourceOptions = fromOffsets(values.toSeq)

  /** Set the options to start streaming for the saved offsets
    *
    * @param startTimeIfMissing start time
    *
    * @return Current instance
    */
  def fromSavedOffsets(startTimeIfMissing: Instant = Instant.MIN): SourceOptions = {
    _isFromStart = false
    _isFromOffsets = false
    _isFromTime = false
    _isFromCheckpoint = true
    _startOffsets = None
    _startTime = None
    _startTimeOnNoCheckpoint = Some(startTimeIfMissing)
    this
  }

  /** Set the options to store the stream offset
    *
    * @return Current instance
    */
  def saveOffsets(): SourceOptions = {
    _isSaveOffsets = true
    this
  }

  /** Set the options to include Event Hubs runtime information in the stream
    *
    * @return Current instance
    */
  def withRuntimeInfo(): SourceOptions = {
    _isWithRuntimeInfo = true
    this
  }

  private[reactiveeventhubs] def getStartTime: Option[Instant] = _startTime

  private[reactiveeventhubs] def getStartTimeOnNoCheckpoint: Option[Instant] = _startTimeOnNoCheckpoint

  private[reactiveeventhubs] def getStartOffsets(config: IConnectConfiguration): Seq[String] = {
    if (!_isFromOffsets)
      List.fill[String](config.eventHubPartitions)(EventHubPartition.OffsetStartOfStream)
    else {
      if (_startOffsets.get.size != config.eventHubPartitions)
        throw new RuntimeException(s"The number of stream offsets [${_startOffsets.get.size}] " +
          s"differs from the number of partitions [${config.eventHubPartitions}]")

      _startOffsets.get
    }
  }

  private[reactiveeventhubs] def getPartitions(config: IConnectConfiguration): Seq[Int] = {
    if (_allPartitions) 0 until config.eventHubPartitions
    else _partitions.get
  }

  private[reactiveeventhubs] def isFromStart: Boolean = _isFromStart

  private[reactiveeventhubs] def isFromOffsets: Boolean = _isFromOffsets

  private[reactiveeventhubs] def isFromTime: Boolean = _isFromTime

  private[reactiveeventhubs] def isFromSavedOffsets: Boolean = _isFromCheckpoint

  private[reactiveeventhubs] def isSaveOffsets: Boolean = _isSaveOffsets

  private[reactiveeventhubs] def isWithRuntimeInfo: Boolean = _isWithRuntimeInfo
}
