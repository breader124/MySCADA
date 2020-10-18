package elka.achlebos.viewmodel

import elka.achlebos.model.CertificateLoadingException
import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.connection.Connection
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.control.Alert.AlertType
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.UaClient
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class ConnectionCreationViewModel : ItemViewModel<Connection>() {

    var discoveredEndpoints: ObservableList<EndpointDescription> = observableListOf()

    val serverUri = SimpleStringProperty("")
    val selectedEndpoint = SimpleObjectProperty<EndpointDescription>()
    val password = SimpleStringProperty("")

    private val keyStorePath: Path = Paths.get("keyStore.jks")

    fun clearDiscoveredEndpoints() {
        discoveredEndpoints.clear()

        log.info("Cleared previously discovered endpoints")
    }

    fun discover(): List<EndpointDescription> {
        return item
                .discoverEndpoints()
                .whenComplete { _, _ -> log.info("Completed discovering endpoints") }
                .exceptionally {
                    log.severe("Encountered problem discovering endpoints")
                    throw it
                }
                .get()
    }

    fun connect() {
        item.connectUsingX509Cert(
                selectedEndpoint.value,
                keyStorePath,
                password.value
        ).whenComplete { client: UaClient?, _: Throwable? ->
            val name = "[${selectedEndpoint.value.securityMode}] ${selectedEndpoint.value.endpointUrl}"
            fire(ConnectionCreatedEvent(name, client as OpcUaClient))

            log.info("Successfully connected with: $name")
        }.exceptionally {
            val name = "[${selectedEndpoint.value.securityMode}] ${selectedEndpoint.value.endpointUrl}"

            log.severe("Encountered problem connecting with $name")

            throw it
        }.get()
    }

    fun handleDiscoveryException(exc: Throwable) {
        log.info("Handling discovery exception")
        exc.message?.also {
            when {
                it.contains("Connection refused") -> {
                    alert(AlertType.ERROR, "Connection refused")
                }
                it.contains("timed out") -> {
                    alert(AlertType.ERROR, "Connection timed out waiting for response from host")
                }
                it.contains("unsupported protocol") -> {
                    alert(AlertType.ERROR, "Protocol you wanted to use for establishing connection is unsupported")
                }
                it.contains("closed") -> {
                    alert(AlertType.ERROR, "Network error occurred, connection is closed. Please try again")
                }
            }
        }
        log.severe(exc.localizedMessage)
    }

    fun handleConnectException(exc: Throwable) {
        log.info("Handling connect exception")
        when (exc) {
            is CertificateLoadingException -> alert(AlertType.ERROR, "Incorrect password")
            else -> alert(AlertType.ERROR, "Connection refused")
        }
        log.severe(exc.localizedMessage)
    }
}

