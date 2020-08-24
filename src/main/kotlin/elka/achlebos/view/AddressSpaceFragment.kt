package elka.achlebos.view

import elka.achlebos.model.client.Client
import elka.achlebos.model.client.ClientsManager
import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.viewmodel.AddressSpaceFragmentModel
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.util.logging.Logger

class AddressSpaceFragment : Fragment() {
    companion object {
        private val logger: Logger = Logger.getLogger(AddressSpaceFragment::class.simpleName)
    }

    private val model: AddressSpaceFragmentModel by inject()
    private var serverTreeBorderPane: BorderPane by singleAssign()
    private var noConnectedServerLabel: Label by singleAssign()
    private val noChosenServerLabel = label("There is no chosen server")

    private val connectedServers: ObservableList<Client> = ClientsManager.connected
    private val selectedServer = SimpleObjectProperty<Client?>()

    private val alreadyGeneratedTreesMap = mutableMapOf<Client, TreeView<AddressSpaceComponent>>()
    private lateinit var currentlyDisplayedClient: Client

    init {
        selectedServer.onChange {
            it?.also {
                currentlyDisplayedClient = it

                logger.info(currentlyDisplayedClient.toString())

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
                action {
                    performDisconnection()
                }

                fitToParentWidth()
            }
        }
    }

    private fun generateTreeFor(client: Client): TreeView<AddressSpaceComponent> {
        val root: AddressSpaceComponent = client.rootCatalogue
        return treeview(TreeItem(root)) {
            populate {
                if (connectedServers.contains(currentlyDisplayedClient)) {
                    model.discoverCatalogueContent(it.value, currentlyDisplayedClient.opcUaClient)
                } else {
                    null
                }
            }

            cellFormat {
                val prefix = if (it is AddressSpaceCatalogue) "[Cat]" else "[Node]"
                text = "$prefix ${it.name}"
            }

            onUserSelect {
                logger.info("$it")
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
        val currentlyConnectedServers = connectedServers.filter { it !== currentlyDisplayedClient }

        model.disconnect(currentlyDisplayedClient)
        if (currentlyConnectedServers.isEmpty()) {
            serverTreeBorderPane.center = noConnectedServerLabel
        } else {
            alreadyGeneratedTreesMap.remove(currentlyDisplayedClient)
            selectedServer.value = null
        }
        ClientsManager.removeClient(currentlyDisplayedClient)
    }
}