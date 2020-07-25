package elka.achlebos.view

import elka.achlebos.model.connection.Connection
import elka.achlebos.viewmodel.ConnectionCreationViewModel
import javafx.stage.FileChooser
import tornadofx.*

class ConnectionCreationView : View("New connection") {
    private val model: ConnectionCreationViewModel by inject()

    override val root = form {
        fieldset {
            textfield(model.serverUri)
            button("Discover endpoints").action {
                runAsync {
                    model.item = Connection(model.serverUri.value)
                    model.discover()
                }
            }
        }

        listview(model.discoveredEndpoints).selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            model.selectedEndpoint.value = newValue
        }

        fieldset {
            field("Certificate path") {
                button("Choose certificate").action {
                    val f = chooseFile(
                            title = "Choose certificate to delete",
                            filters = arrayOf(
                                    FileChooser.ExtensionFilter("Certificates", "*.msc")
                            ),
                            mode = FileChooserMode.Single
                    )
                    f.firstOrNull()?.also {
                        model.certificatePath.value = it.absolutePath
                    }
                }
                text(model.certificatePath)
            }

            field("Password") {
                passwordfield(model.password)
            }
        }

        button("Connect").action {
            runAsync {
                model.connect()
            }
            close()
        }
    }
}