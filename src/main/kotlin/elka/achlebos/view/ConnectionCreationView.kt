package elka.achlebos.view

import elka.achlebos.model.connection.Connection
import elka.achlebos.viewmodel.ConnectionCreationViewModel
import tornadofx.*

class ConnectionCreationView : View("New connection") {
    private val model: ConnectionCreationViewModel by inject()

    override val root = form {
        fieldset {
            textfield(model.serverUri)
            button("Discover endpoints").action {
                model.clearDiscoveredEndpoints()
                runAsync {
                    model.item = Connection(model.serverUri.value)
                    model.discover()
                } ui {
                    model.discoveredEndpoints.addAll(it)
                }
            }
        }

        listview(model.discoveredEndpoints) {
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                model.selectedEndpoint.value = newValue
            }

            cellFormat {
                graphic = cache {
                    form {
                        fieldset {
                            field("Server URI") {
                                label(it.endpointUrl)
                            }
                            field("Security mode") {
                                label(it.securityMode.toString())
                            }
                            field("Security policy URI") {
                                label(it.securityPolicyUri)
                            }
                        }
                    }
                }
            }
        }

        fieldset {
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