// Copyright (c) Microsoft. All rights reserved.

package DisplayMessages;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage;
import com.microsoft.azure.reactiveeventhubs.SourceOptions;
import com.microsoft.azure.reactiveeventhubs.javadsl.EventHub;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import static java.lang.System.out;

/**
 * Retrieve messages from Event hub and display the data in the console
 */
public class Main extends ReactiveStreamingApp
{
    public static void main(String args[])
    {
        // Source retrieving messages from two Event hub partitions (e.g. partition 0 and 3)
        int[] partitions = {0, 2};
        SourceOptions options = new SourceOptions().partitions(partitions);
        Source<EventHubsMessage, NotUsed> messagesFromTwoPartitions1 = new EventHub().source(options);

        // Same, but different syntax using one of the shortcuts
        Source<EventHubsMessage, NotUsed> messagesFromTwoPartitions2 = new EventHub().source(Arrays.asList(0, 3));

        // Source retrieving from all Event hub partitions for the past 24 hours
        Source<EventHubsMessage, NotUsed> messages = new EventHub().source(Instant.now().minus(1, ChronoUnit.DAYS));

        messages
                .to(console())
                .run(streamMaterializer);
    }

    public static Sink<EventHubsMessage, CompletionStage<Done>> console()
    {
        return Sink.foreach(m ->
        {
            out.println("Seq #: " + m.sequenceNumber() + ", offset: " + m.offset());
        });
    }
}
