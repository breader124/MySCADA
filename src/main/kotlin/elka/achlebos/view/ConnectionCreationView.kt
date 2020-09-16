package elka.achlebos.view

import elka.achlebos.model.connection.Connection
import elka.achlebos.viewmodel.ConnectionCreationViewModel
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class ConnectionCreationView : View("New connection") {
    private val model: ConnectionCreationViewModel by inject()

    private val discoverButtonActive = SimpleBooleanProperty(false)
    private val connectButtonActive = SimpleBooleanProperty(false)

    override val root = form {
        fieldset {
            textfield(model.serverUri) {
                setOnKeyReleased {
                    discoverButtonActive.value = model.serverUri.value.isNotEmpty()
                }
            }

            button("Discover endpoints") {
                enableWhen(discoverButtonActive)

                action {
                    model.clearDiscoveredEndpoints()
                    runAsync {
                        model.item = Connection(model.serverUri.value)
                        model.discover()
                    } ui {
                        model.discoveredEndpoints.addAll(it)
                    } fail {
                        model.handleDiscoveryException(it)
                    }
                }
            }
        }

        listview(model.discoveredEndpoints) {
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                // possible bug
                model.selectedEndpoint.value = newValue
                log.info("${model.selectedEndpoint.value}")
                connectButtonActive.value = isServerChosenAndPasswordProvided()
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
                passwordfield(model.password) {
                    setOnKeyTyped {
                        connectButtonActive.value = isServerChosenAndPasswordProvided()
                    }
                }
            }
        }

        button("Connect") {
            enableWhen(connectButtonActive)

            action {
                runAsync {
                    model.connect()
                    resetState()
                } fail {
                    model.handleConnectException(it)
                }
                close()
            }
        }
    }

    private fun isServerChosenAndPasswordProvided(): Boolean {
        return model.selectedEndpoint.value != null && model.password.value.isNotEmpty()
    }

    private fun resetState() {
        model.serverUri.value = ""
        model.password.value = ""
        model.selectedEndpoint.value = null
        model.clearDiscoveredEndpoints()

        discoverButtonActive.value = false
        connectButtonActive.value = false
    }
}