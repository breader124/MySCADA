package elka.achlebos.viewmodel

import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.popup.ErrorCreatingSubscription
import elka.achlebos.view.popup.ErrorTryingToDisconnect
import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem
import org.eclipse.milo.opcua.stack.core.NamespaceTable
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue
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
                } catch (exc: Exception) {
                    log.severe("Error browsing catalogue content: ${component.name}")

//                    TODO("handle TimeoutException instead of suppressing it")
                    return@runAsync
                }
            } else {
                return@runAsync
            }

            val references: List<ReferenceDescription>? = browseResult.references?.asList()
            references?.forEach { extendTreeByNewlyDiscoveredComponent(it, component, currentClient) }
        }

        return component.items
    }

    private fun extendTreeByNewlyDiscoveredComponent(
            reference: ReferenceDescription,
            component: AddressSpaceComponent,
            currentClient: OpcUaClient
    ) {
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

    fun subscribe(component: AddressSpaceComponent) {
        val onItemCreated = { monitoredItem: UaMonitoredItem, id: Int ->
            monitoredItem.setValueConsumer { _, data: DataValue ->
                println("${monitoredItem.hashCode()}: ${data.value.value}")
            }
        }
        component.subscribe(1000.0, onItemCreated)
                .exceptionally {
                    log.severe("Encountered problem creating subscription for ${component.name}")
                    throw it
                }
                .get()

        log.info("Successfully created monitored item for: ${component.name}")
    }

    fun disconnect(server: Server) {
        runAsync {
            server.disconnect()
                    .whenComplete { _, _ -> log.info("Successfully disconnected from $server") }
                    .exceptionally {
                        log.severe(it.localizedMessage)
                        throw it
                    }
                    .get()
        }
    }

    fun handleDiscoveringCatalogueContentException() {
        log.info("Handling discovering catalogue content exception")
    }

    fun handleSubscribeException() {
        log.info("Handling subscribe exception")
        find<ErrorCreatingSubscription>().openWindow()
    }

    fun handleDisconnectingException() {
        log.info("Handling disconnecting exception")
        find<ErrorTryingToDisconnect>().openWindow()
    }

    fun updateServerManagerState(server: Server) {
        ServerManager.removeServer(server)

        log.info("Removed $server from ServerManager")
    }
}