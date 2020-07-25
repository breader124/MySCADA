package elka.achlebos.view

import elka.achlebos.model.connection.Connection
import elka.achlebos.viewmodel.ConnectionCreationViewModel
import tornadofx.*

class ConnectionCreationView : View("New connection") {
    private val model: ConnectionCreationViewModel by inject()
    private val serverUri: String by param()

    init {
        model.item = Connection(serverUri)
        runAsync {
            model.discover()
        }
    }

    override val root = form {
        listview(model.discoveredEndpoints).selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            model.selectedEndpoint.value = newValue
        }

        fieldset {
            field("Certificate path") {
                textfield(model.certificatePath)
            }

            field("Password") {
                passwordfield(model.password)
            }
        }

        button("Connect").action {
            model.connect()
        }
    }
}