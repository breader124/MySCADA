package elka.achlebos.view

import elka.achlebos.model.client.Client
import elka.achlebos.model.client.ClientsManager
import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import org.eclipse.milo.opcua.stack.core.NamespaceTable
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription
import tornadofx.*

class AddressSpaceFragment : Fragment() {

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

    override val root = vbox {
        label("Connected servers")
        combobox(selectedServer, connectedServers)

        vbox {
            serverTreeVBox = this

            useMaxHeight = true
        }
    }

    override fun onDock() {
        super.onDock()
        setWidthAsPartOfParentWidth()
    }


    private fun generateTreeFor(client: Client): TreeView<AddressSpaceComponent> {
        val root: AddressSpaceComponent = client.rootCatalogue
        return treeview(TreeItem(root)) {
            populate {
                discoverCatalogueContent(it.value)
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

    private fun discoverCatalogueContent(component: AddressSpaceComponent): ObservableList<AddressSpaceComponent>? {
        runAsync {
            val browseResult: BrowseResult
            if (component is AddressSpaceCatalogue) {
                browseResult = component.browse().get()
            } else {
                return@runAsync
            }

            val references: List<ReferenceDescription>? = browseResult.references?.asList()
            references?.forEach { reference ->
                val nodeClass = reference.nodeClass
                val nodeIdOpt = reference.nodeId.local(NamespaceTable())
                nodeIdOpt.ifPresent { nodeId ->
                    if (nodeClass == NodeClass.Object) {
                        val discoveredCatalogue = AddressSpaceCatalogue(nodeId, actuallyDisplayedClient.opcUaClient)
                        component.add(discoveredCatalogue)
                    } else if (nodeClass == NodeClass.Variable) {
                        val discoveredNode = AddressSpaceNode(nodeId, actuallyDisplayedClient.opcUaClient)
                        component.add(discoveredNode)
                    }
                }
            }
        }

        return component.items
    }
}