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
import java.util.concurrent.ExecutionException

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
                .whenComplete{ _, _ -> log.info("Completed discovering endpoints") }
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

        when (exc) {
            is ExecutionException -> {
                alert(AlertType.ERROR, "Connection cannot be established")
                log.severe(exc.localizedMessage)
            }
            else -> log.severe(exc.localizedMessage)
        }
    }

    fun handleConnectException(exc: Throwable) {
        log.severe(exc.localizedMessage)
        when (exc) {
            is CertificateLoadingException -> alert(AlertType.ERROR, "Incorrect password")
            else -> alert(AlertType.ERROR, "Connection refused")
        }
    }
}

