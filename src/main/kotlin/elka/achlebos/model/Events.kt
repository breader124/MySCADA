package elka.achlebos.model

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import tornadofx.*

class ConnectionCreatedEvent(val client: OpcUaClient): FXEvent()

class ConnectionRefusedEvent: FXEvent()

class CertificateCreatedEvent(val certificateName: String, val appUri: String): FXEvent()

class CertificateCreationErrorEvent : FXEvent()