package elka.achlebos.model

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import tornadofx.*
import java.time.Period

class ConnectionCreatedEvent(val client: OpcUaClient): FXEvent()

class ConnectionRefusedEvent: FXEvent()

class CertificateCreatedEvent(val certificateName: String, val appUri: String, val validityPeriod: Period): FXEvent()

class CertificateCreationErrorEvent : FXEvent()

class CertificateRemovedEvent : FXEvent()