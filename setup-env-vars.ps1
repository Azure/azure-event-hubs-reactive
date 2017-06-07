#  Populate the following environment variables, and execute this file before
#  running the samples.
#
#  For more information about where to find these values, more information here:
#
#  * https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-create
#
#
#  Example:
#
#  $env:EVENTHUB_NAME="my-event-hub"
#
#  $env:EVENTHUB_ENDPOINT="sb://your-namespace.servicebus.windows.net/"
#
#  $env:EVENTHUB_PARTITIONS=4
#
#  $env:ACCESS_POLICY="service"
#
#  $env:ACCESS_KEY="..."
#
#  $env:STREAMING_CHECKPOINT_ACCOUNT = 'myazurestorage'
#
#  $env:STREAMING_CHECKPOINT_KEY = "..."
#

$env:EVENTHUB_NAME = ''

$env:EVENTHUB_ENDPOINT = ''

$env:EVENTHUB_PARTITIONS = ''

$env:EVENTHUB_ACCESS_POLICY = ''

$env:EVENTHUB_ACCESS_KEY = ''

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account name
$env:STREAMING_CHECKPOINT_ACCOUNT = ''

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account secret key
$env:STREAMING_CHECKPOINT_KEY = ''
