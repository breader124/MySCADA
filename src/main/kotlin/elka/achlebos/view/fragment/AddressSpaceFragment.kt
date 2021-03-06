package elka.achlebos.view.fragment

import elka.achlebos.model.EstablishingConnectionStarted
import elka.achlebos.model.EstablishingConnectionStopped
import elka.achlebos.model.SubscriptionCreatedEvent
import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.dialog.ReadCatalogueDialog
import elka.achlebos.view.dialog.ReadNodeDialog
import elka.achlebos.view.dialog.WriteDialog
import elka.achlebos.viewmodel.AddressSpaceFragmentModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.lang.Exception

class AddressSpaceFragment : Fragment() {

    private val model: AddressSpaceFragmentModel by inject()
    private var serverTreeBorderPane: BorderPane by singleAssign()

    private var noConnectedServerLabel: Label by singleAssign()
    private val noChosenServerLabel = label("There is no chosen server")
    private val connectingInProgressLabel = label("Connecting to chosen server in progress...")
    private var previousLabel: Node? = null

    private val connectedServers: ObservableList<Server> = ServerManager.CONNECTED
    private val selectedServer = SimpleObjectProperty<Server?>()
    private val selectedComponent = SimpleObjectProperty<AddressSpaceComponent?>()

    private val alreadyGeneratedTreesMap = mutableMapOf<Server, TreeView<AddressSpaceComponent>>()
    private lateinit var currentlyDisplayedServer: Server

    private val disconnectButtonInactive: SimpleBooleanProperty = SimpleBooleanProperty(true)
    private val nodeOptionsInactive: SimpleBooleanProperty = SimpleBooleanProperty()

    init {
        selectedServer.onChange {
            disconnectButtonInactive.value = it == null
            it?.also {
                currentlyDisplayedServer = it
                val treeView = alreadyGeneratedTreesMap.computeIfAbsent(it) { client ->
                    generateTreeFor(client)
                }
                serverTreeBorderPane.center = treeView
            }
        }

        connectedServers.onChange {
            if (selectedServer.value == null) {
                if (connectedServers.isNotEmpty()) {
                    serverTreeBorderPane.center = noChosenServerLabel
                } else {
                    serverTreeBorderPane.center = noConnectedServerLabel
                }
            }
        }

        subscribe<EstablishingConnectionStarted> {
            previousLabel = serverTreeBorderPane.center
            serverTreeBorderPane.center = connectingInProgressLabel
        }

        subscribe<EstablishingConnectionStopped> {
            if (connectedServers.isNotEmpty() && previousLabel == noConnectedServerLabel) {
                serverTreeBorderPane.center = noChosenServerLabel
            } else {
                serverTreeBorderPane.center = previousLabel
            }
        }
    }

    override fun onDock() {
        super.onDock()
        setWidthAsPartOfParentWidth()
    }

    override val root = borderpane {
        serverTreeBorderPane = this

        top {
            label("Connected servers")
            combobox(selectedServer, connectedServers) {
                fitToParentWidth()
            }
        }

        center {
            noConnectedServerLabel = label("There is no connected server")
        }

        bottom {
            button("Disconnect") {
                disableWhen {
                    disconnectButtonInactive
                }

                action {
                    runAsync {
                        model.disconnect(currentlyDisplayedServer)
                    } success {
                        deleteDisconnectedItemsFromFragment()
                    } fail {
                        model.handleDisconnectingException()
                    }
                }

                fitToParentWidth()
            }
        }

        style {
            borderColor += box(c("#a1a1a1"))
        }
    }

    private fun generateTreeFor(server: Server): TreeView<AddressSpaceComponent> {
        val root: AddressSpaceComponent = server.rootCatalogue
        return treeview(TreeItem(root)) {
            populate {
                if (connectedServers.contains(currentlyDisplayedServer)) {
                    try {
                        runAsync {
                            model.discoverCatalogueContent(it.value, currentlyDisplayedServer.opcUaClient)
                        }
                        it.value.items
                    } catch (exc: Exception) {
                        model.handleDiscoveringCatalogueContentException()
                        null
                    }
                } else {
                    null
                }
            }

            cellFormat {
                val prefix = if (it is AddressSpaceCatalogue) "[Cat]" else "[Node]"
                text = "$prefix ${it.name}"
            }

            contextmenu {
                item("Read...") {
                    action {
                        when (selectedComponent.value) {
                            is AddressSpaceNode -> {
                                val paramMappings = mapOf(ReadNodeDialog::component to selectedComponent.value)
                                find<ReadNodeDialog>(paramMappings).openWindow()
                            }
                            is AddressSpaceComponent -> {
                                val paramMappings = mapOf(ReadCatalogueDialog::component to selectedComponent.value)
                                find<ReadCatalogueDialog>(paramMappings).openWindow()
                            }
                        }
                    }
                }

                item("Write... (only numerical value)") {
                    disableWhen(nodeOptionsInactive)

                    action {
                        selectedComponent.value?.also {
                            if (it is AddressSpaceNode) {
                                val mappings = mapOf(WriteDialog::node to it)
                                find<WriteDialog>(mappings).openWindow()
                            }
                        }
                    }
                }

                item("Subscribe...") {
                    disableWhen(nodeOptionsInactive)

                    action {
                        val componentName = selectedComponent.value?.name ?: ""
                        selectedComponent.value?.also {
                            runAsync {
                                model.subscribe(it)
                            } success {
                                fire(SubscriptionCreatedEvent(it, componentName))
                            } fail {
                                model.handleSubscribeException()
                            }
                        }
                    }
                }
            }

            onUserSelect {
                selectedComponent.value = it
                nodeOptionsInactive.value = it !is AddressSpaceNode
            }

            fitToParentHeight()
        }
    }

    private fun setWidthAsPartOfParentWidth() {
        val prefPart = 0.2
        val parentWindowWidthProperty = currentWindow?.widthProperty()?.multiply(prefPart)
        root.prefWidthProperty().bind(parentWindowWidthProperty)
    }

    private fun deleteDisconnectedItemsFromFragment() {
        val currentlyConnectedServers = connectedServers.filter { it !== currentlyDisplayedServer }
        if (currentlyConnectedServers.isEmpty()) {
            serverTreeBorderPane.center = noConnectedServerLabel
        } else {
            alreadyGeneratedTreesMap.remove(currentlyDisplayedServer)
        }
        selectedServer.value = null
        model.updateServerManagerState(currentlyDisplayedServer)
    }
}