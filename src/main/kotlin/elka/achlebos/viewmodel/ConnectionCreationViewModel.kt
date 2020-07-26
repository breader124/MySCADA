package elka.achlebos.viewmodel

import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.ConnectionRefusedEvent
import elka.achlebos.model.connection.Connection
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.UaClient
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import tornadofx.*
import java.nio.file.Paths

class ConnectionCreationViewModel : ItemViewModel<Connection>() {
    var discoveredEndpoints: ObservableList<EndpointDescription> = observableListOf()

    val serverUri = SimpleStringProperty()
    val selectedEndpoint = SimpleObjectProperty<EndpointDescription>()
    val certificatePath = SimpleStringProperty()
    val password = SimpleStringProperty()

    fun discover() {
        discoveredEndpoints.removeAll()
        val nowDiscovered = item.discoverEndpoints().get()
        discoveredEndpoints.addAll(nowDiscovered)
    }

    fun connect() {
        try {
            item.connectUsingX509Cert(
                    selectedEndpoint.value,
                    Paths.get(certificatePath.value),
                    password.value
            ).whenComplete { client: UaClient?, _: Throwable? ->
                fire(ConnectionCreatedEvent(client as OpcUaClient))
            }.exceptionally {
                throw it
            }.get()
        } catch (exc: Exception) {
            exc.printStackTrace()
            fire(ConnectionRefusedEvent())
        }
    }
}