package elka.achlebos.viewmodel

import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.connection.Connection
import elka.achlebos.view.ConnectionCreationView
import elka.achlebos.view.popups.ConnectionRefusedDialog
import elka.achlebos.view.popups.TimeoutExceptionDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.UaClient
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ExecutionException
import java.util.logging.Logger

class ConnectionCreationViewModel : ItemViewModel<Connection>() {
    companion object {
        val logger: Logger = Logger.getLogger(ConnectionCreationViewModel::class.simpleName)
    }

    var discoveredEndpoints: ObservableList<EndpointDescription> = observableListOf()

    val serverUri = SimpleStringProperty()
    val selectedEndpoint = SimpleObjectProperty<EndpointDescription>()
    val password = SimpleStringProperty()

    private val keyStorePath: Path = Paths.get("keyStore.jks")

    fun clearDiscoveredEndpoints() {
        discoveredEndpoints.clear()
    }

    fun discover(): List<EndpointDescription> {
        return item
                .discoverEndpoints()
                .exceptionally { throw it }
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
        }.exceptionally {
            throw it
        }.get()
    }

    fun handleDiscoveryException(exc: Throwable) {
        when (exc) {
            is ExecutionException -> find<TimeoutExceptionDialog>().openWindow()
            else -> logger.severe(exc.localizedMessage)
        }
    }

    fun handleConnectException(exc: Throwable) {
        find<ConnectionRefusedDialog>().openWindow()
    }
}

