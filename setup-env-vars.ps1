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
#  $env:EVENTHUB_ENDPOINT="sb://iothub-ns-myioth-75186-9fb862f912.servicebus.windows.net/"
#
#  $env:EVENTHUB_PARTITIONS=4
#
#  $env:ACCESS_POLICY="service"
#
#  $env:ACCESS_KEY="1Ab23456C78d+E9fOgH1234ijklMNo5P//Q6rStuwX7="
#
#  $env:ACCESS_HOSTNAME="my-event-hub.azure-devices.net"
#
#  $env:STREAMING_CHECKPOINT_ACCOUNT = 'myazurestorage'
#
#  $env:STREAMING_CHECKPOINT_KEY = "A0BcDef1gHIJKlmn23o8PQrStUvWxyzAbc4dEFG5HOIJklMnopqR+StuVwxYzJjxsU6vnDeNTv7Ipqs8MaBcDE=="
#

# see: Endpoints ⇒ Messaging ⇒ Events ⇒ `Event Hub-compatible name`
$env:EVENTHUB_NAME = ''

# see: Endpoints ⇒ Messaging ⇒ Events ⇒ `Event Hub-compatible endpoint`
$env:EVENTHUB_ENDPOINT = ''

# see: Endpoints ⇒ Messaging ⇒ Events ⇒ Partitions
$env:EVENTHUB_PARTITIONS = ''

# see: Shared access policies, we suggest to use `service` here
$env:EVENTHUB_ACCESS_POLICY = ''

# see: Shared access policies ⇒ key name ⇒ Primary key
$env:EVENTHUB_ACCESS_KEY = ''

# see: Shared access policies ⇒ key name ⇒ Connection string ⇒ HostName
$env:EVENTHUB_ACCESS_HOSTNAME = ''

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account name
$env:STREAMING_CHECKPOINT_ACCOUNT = ''

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account secret key
$env:STREAMING_CHECKPOINT_KEY = ''
