package elka.achlebos.model

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import tornadofx.*
import java.util.*

class ConnectionCreatedEvent(val name: String, val opcUaClient: OpcUaClient): FXEvent()

class SubscriptionCreatedEvent(val queueNum: UUID, val componentName: String): FXEvent()

class SubscriptionRemoveRequestEvent(val queueNum: UUID): FXEvent()

class EstablishingConnectionStarted : FXEvent()

class EstablishingConnectionStopped : FXEvent()