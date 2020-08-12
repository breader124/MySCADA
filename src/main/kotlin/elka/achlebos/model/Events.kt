package elka.achlebos.model

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import tornadofx.*

class ConnectionCreatedEvent(val opcUaClient: OpcUaClient): FXEvent()