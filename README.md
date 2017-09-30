[![Maven Central][maven-badge]][maven-url]
[![Build][build-badge]][build-url]
[![Issues][issues-badge]][issues-url]

# Reactive Event Hubs

Reactive Event Hubs is an Akka Stream library that can be used **to read
events** from
[Azure Event Hubs](https://azure.microsoft.com/en-us/services/event-hubs/),
via a **reactive stream** with **asynchronous back pressure**.
Azure Event Hubs is a service used to scale telemetry ingestion from websites,
apps, and any streams of data.

The library can be used both in Java and Scala, providing a fluent DSL for both
programming languages, similarly to the approach used by Akka.

The following is a simple example showing how to use the library in Scala. A
stream of incoming telemetry data is read, parsed and converted to a
`Temperature` object, and then filtered based on the temperature value:

```scala
EventHub().source()
    .to(console)
    .run()
```

and the equivalent code in Java:

```java
TypeReference<Temperature> type = new TypeReference<Temperature>() {};

new EventHub().source()
    .to(console())
    .run(streamMaterializer);
```


#### Streaming from Event hubs to _any_

An interesting example is reading telemetry data from Azure Event Hubs, and
sending it to a Kafka topic, so that it can be consumed by other services
downstream:

```scala
...
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer

case class KafkaProducer(bootstrapServer: String)(implicit val system: ActorSystem) {

  protected val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServer)

  def getSink() = Producer.plainSink(producerSettings)

  def packageMessage(elem: String, topic: String): ProducerRecord[Array[Byte], String] = {
    new ProducerRecord[Array[Byte], String](topic, elem)
  }
}
```

```scala
val kafkaProducer = KafkaProducer(bootstrapServer)

EventHub().source()
    .runWith(kafkaProducer.getSink())
```

## Source options

### Event hub partitions

The library supports reading from a subset of
[partitions](https://azure.microsoft.com/en-us/documentation/articles/event-hubs-overview),
to enable the development of distributed applications. Consider for instance the
scenario of a client application deployed to multiple nodes, where each node
processes independently a subset of the incoming telemetry.

```scala
val p1 = 0
val p2 = 3

EventHub().source(Seq(p1, p2))
    .to(console)
    .run()
```

### Starting point

Unless specified, the stream starts from the beginning of the data present in
each partition. It's possible to start the stream from a given date and time
too:

```scala
val start = java.time.Instant.now()

EventHub().source(start)
    .to(console)
    .run()
```

### Multiple options

`EventHub().source()` provides a quick API to specify the start time or the
partitions. To specify more options, you can use the `SourceOptions` class,
combining multiple settings:

```scala
val options = SourceOptions()
  .partitions(0,2,3)
  .fromTime(java.time.Instant.now())
  .withRuntimeInfo()
  .saveOffsets()

EventHub().source(options)
    .to(console)
    .run()
```

### Stream processing restart - saving the current position

The library provides a mechanism to restart the stream from a recent
*checkpoint*, to be resilient to restarts and crashes.
*Checkpoints* are saved automatically, with a configured frequency, on a storage
provided. For instance, the stream position can be saved every 30 seconds and/or
every 500 messages (these values are configurable), in a table in Cassandra or
using Azure blobs.

Currently the position is saved in a concurrent thread, delayed by time and/or
count, depending on the configuration settings. Given the current implementation
it's possible that the position saved is ahead of your processing logic. While
it's possible to mitigate the risk via the configuration settings,
**at-least-once** cannot be guaranteed.

We are working to support **at-least-once** semantic in the next few months,
providing more control on the checkpointing logic.

For more information about the checkpointing feature,
[please read here](checkpointing.md).


### Event Hub configuration

By default Reactive Event Hubs uses an `application.conf` configuration file to
fetch the parameters required to connect to Azure Event Hubs. The connection and
authentication values to use, can be found in the
[Azure Portal](https://portal.azure.com):

Properties required to receive telemetry:

* **eventHubName**:
* **eventHubEndpoint**:
* **eventHubPartitions**:
* **accessPolicy**:
* **accessKey**:

The values should be stored in your `application.conf` resource (or equivalent).
Optionally you can reference environment settings if you prefer, for example to
hide sensitive data.

```
reactive-eventhubs {

  connection {
    eventHubName        = "<Event Hub name>"
    eventHubEndpoint    = "<Event Hub endpoint>"
    eventHubPartitions  = <the number of partitions in your Event Hub>
    accessPolicy   = "<access policy name>"
    accessKey      = "<access policy key>"
  }

  [... other settings...]
}
````

Example using environment settings:

```
reactive-eventhubs {

  connection {
    eventHubName        = ${?EVENTHUB_NAME}
    eventHubEndpoint    = ${?EVENTHUB_ENDPOINT}
    eventHubPartitions  = ${?EVENTHUB_PARTITIONS}
    accessPolicy   = ${?EVENTHUB_ACCESS_POLICY}
    accessKey      = ${?EVENTHUB_ACCESS_KEY}
  }

  [... other settings...]
}
````

Note that the library will automatically use these exact environment variables,
unless overridden in your configuration file (all the default settings are
stored in [reference.conf](src/main/resources/reference.conf)).

Although using a configuration file is the preferred approach, it's also
possible to inject a different configuration at runtime, providing an object
implementing the `IConfiguration` interface.

The logging level can be managed overriding Akka configuration, for example:

```
akka {
  # Options: OFF, ERROR, WARNING, INFO, DEBUG
  loglevel = "WARNING"
}
```

There are other settings, to tune performance and connection details:

* **streaming.consumerGroup**: the
  [consumer group](https://azure.microsoft.com/en-us/documentation/articles/event-hubs-overview)
  used during the connection
* **streaming.receiverBatchSize**: the number of messages retrieved on each call
  to Azure Event Hubs. The default (and maximum) value is 999.
* **streaming.receiverTimeout**: timeout applied to calls while retrieving
  messages. The default value is 3 seconds.
* **streaming.retrieveRuntimeInfo**: when enabled, the messages returned by
  `EventHub.Source` will contain some runtime information about the last message
  in each partition. You can use this information to calculate how many
  telemetry events remain to process.

The complete configuration reference (and default values) is available in
[reference.conf](src/main/resources/reference.conf).

## Samples

The project includes several demos in Java and Scala, showing some of the use
cases and how the Reactive Event Hubs API works. All the demos require an
instance of Azure Event Hubs, with some telemetry to stream.

1. **DisplayMessages** [Java]: how to stream Azure Event Hubs telemetry within
   a Java application
1. **AllMessagesFromBeginning** [Scala]: simple example streaming all the events
   in the hub.
1. **OnlyRecentMessages** [Scala]: stream all the events, starting from the
   current time.
1. **OnlyTwoPartitions** [Scala]: shows how to stream events from a subset of
   partitions.
1. **MultipleDestinations** [Scala]: shows how to read once and deliver events
   to multiple destinations.
1. **CloseStream** [Scala]: show how to close the stream
1. **Throughput** [Scala]: stream all events and display statistics about the
   throughput.
1. **Throttling** [Scala]: throttle the incoming stream to a defined speed of
   events/second.
1. **StoreOffsetsWhileStreaming** [Scala]: demonstrates how the stream can be
   restarted without losing its position. The current position is stored in a
   Cassandra table (we suggest to run a docker container for the purpose of the
   demo, e.g. `docker run -ip 9042:9042 --rm cassandra`).
1. **StartFromStoredOffsetsButDontWriteNewOffsets** [Scala]: shows how to use
   the saved checkpoints to start streaming from a known position, without
   changing the value in the storage. If the storage doesn't contain
   checkpoints, the stream starts from the beginning.
1. **StartFromStoredOffsetsIfAvailableOrByTimeOtherwise** [Scala]: similar to
   the previous demo, with a fallback datetime when the storage doesn't contain
   checkpoints.
1. **StreamIncludingRuntimeInformation** [Scala]: shows how runtime information
   works.

When ready, you should either edit the `application.conf` configuration files
([scala](samples-scala/src/main/resources/application.conf) and
[java](samples-java/src/main/resources/application.conf))
with your credentials, or set the corresponding environment variables.
Follow the instructions described in the previous section on how to set the
correct values.

The root folder includes also a script showing how to set the environment
variables in [Linux/MacOS](setup-env-vars.sh) and [Windows](setup-env-vars.bat).

The demos can be executed using the scripts included in the root folder
(`run_<language>_samples.sh` and `run_<language>_samples.cmd`):

* [`run_scala_samples.sh`](run_scala_samples.sh): execute Scala demos
* [`run_java_samples.sh`](run_java_samples.sh): execute Java demos

## Future work

* support at-least-once when checkpointing
* use EventHub SDK async APIs

# Contributing

## Contribution license Agreement

If you want/plan to contribute, we ask you to sign a
[CLA](https://cla.microsoft.com/) (Contribution license Agreement). A friendly
bot will remind you about it when you submit a pull-request.

## Code style

If you are sending a pull request, please check the code style with IntelliJ
IDEA, importing the settings from
[`Codestyle.IntelliJ.xml`](codestyle.intellij.xml).

## Running the tests

You can use the included `build.sh` script to execute all the unit and
functional tests in the suite. The functional tests require an existing Azure
Event Hub resource, that yous should setup. For the tests to connect to your
Hub, configure your environment using the `setup-env-vars.*` scripts
mentioned above in this page.


[maven-badge]: https://img.shields.io/maven-central/v/com.microsoft.azure/reactive-event-hubs_2.12.svg
[maven-url]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22reactive-event-hubs_2.12%22
[build-badge]: https://img.shields.io/travis/Azure/reactive-event-hubs-java.svg
[build-url]: https://travis-ci.org/Azure/reactive-event-hubs-java
[issues-badge]: https://img.shields.io/github/issues/azure/reactive-event-hubs-java.svg?style=flat-square
[issues-url]: https://github.com/azure/reactive-event-hubs-java/issues
