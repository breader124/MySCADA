package elka.achlebos.viewmodel

import elka.achlebos.model.connection.Connection
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import tornadofx.*
import java.nio.file.Paths

class ConnectionCreationViewModel : ItemViewModel<Connection>() {
    var discoveredEndpoints: ObservableList<EndpointDescription> = observableListOf()

    val selectedEndpoint = SimpleObjectProperty<EndpointDescription>()
    val certificatePath = SimpleStringProperty()
    val password = SimpleStringProperty()

    fun discover() {
        val discovered = item.discoverEndpoints().get()
        discoveredEndpoints.addAll(discovered)
    }

    fun connect() {
        item.connectUsingX509Cert(
                selectedEndpoint.value,
                Paths.get(certificatePath.value),
                password.value
        )
    }
}