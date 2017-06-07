:: Populate the following environment variables, and execute this file before
:: running the samples.
::
:: For more information about where to find these values, more information here:
::
:: * https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-create
::
::
:: Example:
::
:: SET EVENTHUB_NAME="my-event-hub"
::
:: SET EVENTHUB_ENDPOINT="sb://your-namespace.servicebus.windows.net/"
::
:: SET EVENTHUB_PARTITIONS=4
::
:: SET EVENTHUB_ACCESS_POLICY="service"
::
:: SET EVENTHUB_ACCESS_KEY="..."
::
:: SET STREAMING_CHECKPOINT_ACCOUNT = 'myazurestorage'
::
:: SET STREAMING_CHECKPOINT_KEY = "..."
::

SET EVENTHUB_NAME = ""

SET EVENTHUB_ENDPOINT = ""

SET EVENTHUB_PARTITIONS = ""

SET EVENTHUB_ACCESS_POLICY = ""

SET EVENTHUB_ACCESS_KEY = ""

:: When using checkpoints stored in Azure Blob, this is the Azure Storage Account name
SET STREAMING_CHECKPOINT_ACCOUNT = ""

:: When using checkpoints stored in Azure Blob, this is the Azure Storage Account secret key
SET STREAMING_CHECKPOINT_KEY = ""
