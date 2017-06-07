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
#  export EVENTHUB_NAME="my-event-hub"
#
#  export EVENTHUB_ENDPOINT="sb://your-namespace.servicebus.windows.net/"
#
#  export EVENTHUB_PARTITIONS=4
#
#  export EVENTHUB_ACCESS_POLICY="service"
#
#  export EVENTHUB_ACCESS_KEY="..."
#
#  export STREAMING_CHECKPOINT_ACCOUNT = 'myazurestorage'
#
#  export STREAMING_CHECKPOINT_KEY = "..."
#

export EVENTHUB_NAME=""

export EVENTHUB_ENDPOINT=""

export EVENTHUB_PARTITIONS=""

export EVENTHUB_ACCESS_POLICY=""

export EVENTHUB_ACCESS_KEY=""

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account name
export STREAMING_CHECKPOINT_ACCOUNT=""

# When using checkpoints stored in Azure Blob, this is the Azure Storage Account secret key
export STREAMING_CHECKPOINT_KEY=""
