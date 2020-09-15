package elka.achlebos.model

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import tornadofx.*

class ConnectionCreatedEvent(val name: String, val opcUaClient: OpcUaClient): FXEvent()

class SubscriptionCreatedEvent(val queueNum: Int): FXEvent()