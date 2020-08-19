package elka.achlebos.view

import elka.achlebos.model.client.Client
import elka.achlebos.model.client.ClientsManager
import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.viewmodel.AddressSpaceFragmentModel
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import tornadofx.*

class AddressSpaceFragment : Fragment() {
    private val model: AddressSpaceFragmentModel by inject()

    private val connectedServers: ObservableList<Client> = ClientsManager.connected
    private val selectedServer = SimpleObjectProperty<Client>()

    private var serverTreeVBox: VBox by singleAssign()
    private val alreadyGeneratedTreesMap = mutableMapOf<Client, TreeView<AddressSpaceComponent>>()
    private lateinit var actuallyDisplayedClient: Client

    init {
        selectedServer.onChange {
            it?.also {
                val treeView = alreadyGeneratedTreesMap.computeIfAbsent(it) { client ->
                    actuallyDisplayedClient = client
                    generateTreeFor(client)
                }
                serverTreeVBox.replaceChildren(treeView)
            }
        }
    }

    override fun onDock() {
        super.onDock()
        setWidthAsPartOfParentWidth()
    }

    override val root = vbox {
        label("Connected servers")
        combobox(selectedServer, connectedServers)

        vbox {
            serverTreeVBox = this

            useMaxHeight = true
        }
    }

    private fun generateTreeFor(client: Client): TreeView<AddressSpaceComponent> {
        val root: AddressSpaceComponent = client.rootCatalogue
        return treeview(TreeItem(root)) {
            populate {
                model.discoverCatalogueContent(it.value, actuallyDisplayedClient.opcUaClient)
            }

            cellFormat {
                val prefix = if (it is AddressSpaceCatalogue) "[Cat]" else "[Node]"
                text = "$prefix ${it.name}"
            }

            onUserSelect {
                println("$it")
            }

            fitToParentHeight()
        }
    }

    private fun setWidthAsPartOfParentWidth() {
        val prefPart = 0.2
        val parentWindowWidthProperty = currentWindow?.widthProperty()?.multiply(prefPart)
        root.prefWidthProperty().bind(parentWindowWidthProperty)
    }
}