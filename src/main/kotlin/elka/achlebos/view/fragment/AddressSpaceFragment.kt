package elka.achlebos.view.fragment

import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.ReadCatalogueDialog
import elka.achlebos.view.ReadNodeDialog
import elka.achlebos.view.WriteDialog
import elka.achlebos.viewmodel.AddressSpaceFragmentModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import tornadofx.*

class AddressSpaceFragment : Fragment() {

    private val model: AddressSpaceFragmentModel by inject()
    private var serverTreeBorderPane: BorderPane by singleAssign()
    private var noConnectedServerLabel: Label by singleAssign()
    private val noChosenServerLabel = label("There is no chosen server")

    private val connectedServers: ObservableList<Server> = ServerManager.CONNECTED
    private val selectedServer = SimpleObjectProperty<Server?>()
    private val selectedComponent = SimpleObjectProperty<AddressSpaceComponent?>()

    private val alreadyGeneratedTreesMap = mutableMapOf<Server, TreeView<AddressSpaceComponent>>()
    private lateinit var currentlyDisplayedServer: Server

    private val disconnectButtonInactive: SimpleBooleanProperty = SimpleBooleanProperty(true)

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
                    performDisconnection()
                }

                fitToParentWidth()
            }
        }
    }

    private fun generateTreeFor(server: Server): TreeView<AddressSpaceComponent> {
        val root: AddressSpaceComponent = server.rootCatalogue
        return treeview(TreeItem(root)) {
            populate {
                if (connectedServers.contains(currentlyDisplayedServer)) {
                    model.discoverCatalogueContent(it.value, currentlyDisplayedServer.opcUaClient)
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

                item("Write...") {
                    action {
                        find<WriteDialog>().openWindow()
                    }
                }
            }

            onUserSelect {
                selectedComponent.value = it
            }

            fitToParentHeight()
        }
    }

    private fun setWidthAsPartOfParentWidth() {
        val prefPart = 0.2
        val parentWindowWidthProperty = currentWindow?.widthProperty()?.multiply(prefPart)
        root.prefWidthProperty().bind(parentWindowWidthProperty)
    }

    private fun performDisconnection() {
        val currentlyConnectedServers = connectedServers.filter { it !== currentlyDisplayedServer }

        model.disconnect(currentlyDisplayedServer)
        if (currentlyConnectedServers.isEmpty()) {
            serverTreeBorderPane.center = noConnectedServerLabel
        } else {
            alreadyGeneratedTreesMap.remove(currentlyDisplayedServer)
        }
        selectedServer.value = null
        model.updateServerManagerState(currentlyDisplayedServer)
    }
}