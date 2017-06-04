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
:: SET EVENTHUB_ENDPOINT="sb://iothub-ns-myioth-75186-9fb862f912.servicebus.windows.net/"
::
:: SET EVENTHUB_PARTITIONS=4
::
:: SET EVENTHUB_ACCESS_POLICY="service"
::
:: SET EVENTHUB_ACCESS_KEY="1Ab23456C78d+E9fOgH1234ijklMNo5P//Q6rStuwX7="
::
:: SET EVENTHUB_ACCESS_HOSTNAME="my-event-hub.azure-devices.net"
::
:: SET STREAMING_CHECKPOINT_ACCOUNT = 'myazurestorage'
::
:: SET STREAMING_CHECKPOINT_KEY = "A0BcDef1gHIJKlmn23o8PQrStUvWxyzAbc4dEFG5HOIJklMnopqR+StuVwxYzJjxsU6vnDeNTv7Ipqs8MaBcDE=="
::

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ `Event Hub-compatible name`
SET EVENTHUB_NAME = ""

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ `Event Hub-compatible endpoint`
SET EVENTHUB_ENDPOINT = ""

:: see: Endpoints ⇒ Messaging ⇒ Events ⇒ Partitions
SET EVENTHUB_PARTITIONS = ""

:: see: Shared access policies, we suggest to use `service` here
SET EVENTHUB_ACCESS_POLICY = ""

:: see: Shared access policies ⇒ key name ⇒ Primary key
SET EVENTHUB_ACCESS_KEY = ""

:: see: Shared access policies ⇒ key name ⇒ Connection string ⇒ HostName
SET EVENTHUB_ACCESS_HOSTNAME = ""

:: When using checkpoints stored in Azure Blob, this is the Azure Storage Account name
SET STREAMING_CHECKPOINT_ACCOUNT = ""

:: When using checkpoints stored in Azure Blob, this is the Azure Storage Account secret key
SET STREAMING_CHECKPOINT_KEY = ""

