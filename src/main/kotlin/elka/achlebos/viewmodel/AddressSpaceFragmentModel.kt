package elka.achlebos.viewmodel

import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.popup.TimeoutExceptionDialog
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.stack.core.NamespaceTable
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription
import tornadofx.*

class AddressSpaceFragmentModel : ViewModel() {
    fun discoverCatalogueContent(component: AddressSpaceComponent,
                                 currentClient: OpcUaClient): ObservableList<AddressSpaceComponent>? {
        runAsync {
            val browseResult: BrowseResult
            if (component is AddressSpaceCatalogue) {
                try {
                    browseResult = component.browse().get()
                } catch(exc: Exception) {
                    // TODO("handle timeout exceptions during discovering content of catalogue")
                    return@runAsync
                }
            } else {
                return@runAsync
            }

            val references: List<ReferenceDescription>? = browseResult.references?.asList()
            references?.forEach { reference ->
                val nodeClass = reference.nodeClass
                val nodeName = reference.displayName.text ?: ""
                val emptyRemoteNamespaceTable = NamespaceTable()
                val nodeIdOpt = reference.nodeId.local(emptyRemoteNamespaceTable)
                nodeIdOpt.ifPresent { nodeId ->
                    if (nodeClass == NodeClass.Object) {
                        val discoveredCatalogue = AddressSpaceCatalogue(nodeId, nodeName, currentClient)
                        component.add(discoveredCatalogue)
                    } else if (nodeClass == NodeClass.Variable) {
                        val discoveredNode = AddressSpaceNode(nodeId, nodeName, currentClient)
                        component.add(discoveredNode)
                    }
                }
            }
        }

        return component.items
    }

    fun disconnect(server: Server) {
        runAsync {
            server.disconnect().get()
        }
    }

    fun updateServerManagerState(server: Server) {
        ServerManager.removeServer(server)
    }
}