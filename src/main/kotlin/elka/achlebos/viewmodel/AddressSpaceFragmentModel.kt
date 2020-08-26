package elka.achlebos.viewmodel

import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.stack.core.NamespaceTable
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription
import tornadofx.*
import java.util.logging.Logger

class AddressSpaceFragmentModel : ViewModel() {
    companion object {
        private val logger: Logger = Logger.getLogger(AddressSpaceFragmentModel::class.simpleName)
    }

    fun discoverCatalogueContent(component: AddressSpaceComponent,
                                 currentClient: OpcUaClient): ObservableList<AddressSpaceComponent>? {
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
                val nodeName = reference.displayName.text ?: ""
                val nodeIdOpt = reference.nodeId.local(NamespaceTable())
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

    fun readValue(component: AddressSpaceComponent): Variant = component.readValue().get().value

    fun disconnect(server: Server) {
        runAsync {
            logger.info("To disconnect: $server")

            server.disconnect().get()

            logger.info("Disconnected $server")
        }
    }

    fun updateServerManagerState(server: Server) {
        ServerManager.removeServer(server)
    }
}