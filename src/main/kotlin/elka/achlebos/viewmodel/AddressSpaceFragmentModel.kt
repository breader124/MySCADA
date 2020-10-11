package elka.achlebos.viewmodel

import elka.achlebos.model.SubscriptionRemoveRequestEvent
import elka.achlebos.model.data.*
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import javafx.collections.ObservableList
import javafx.scene.control.Alert.AlertType
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem
import org.eclipse.milo.opcua.stack.core.NamespaceTable
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription
import tornadofx.*
import java.util.*
import kotlin.random.Random

open class AddressSpaceFragmentModel : ViewModel() {

    val activeSubscriptions: MutableMap<UUID, AddressSpaceComponent> = mutableMapOf()

    init {
        subscribe<SubscriptionRemoveRequestEvent> {
            runAsync {
                unsubscribe(it.queueNum)
            } success {
                log.info("Successfully unsubscribed for UUID: $it")
            } fail {
                alert(AlertType.ERROR, "Error occurred trying to unsubscribe", it.message)
            }
        }
    }

    fun discoverCatalogueContent(component: AddressSpaceComponent,
                                 currentClient: OpcUaClient): ObservableList<AddressSpaceComponent>? {
        val browseResult: BrowseResult
        if (component is AddressSpaceCatalogue) {
            try {
                browseResult = component.browse().get()
            } catch (exc: Exception) {
                log.severe(exc.localizedMessage)
                return component.items
            }
        } else {
            return component.items
        }

        val references: List<ReferenceDescription>? = browseResult.references?.asList()
        references?.forEach { extendTreeByNewlyDiscoveredComponent(it, component, currentClient) }

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

    fun subscribe(component: AddressSpaceComponent): UUID {
        val subscriptionUUID = UUID.randomUUID()
        val onItemCreated = { monitoredItem: UaMonitoredItem, id: Int ->
            DataDispatcher.allocateNewQueue(subscriptionUUID)
            monitoredItem.setValueConsumer { _, data: DataValue ->
                DataDispatcher.addDataToQueue(subscriptionUUID, data.value.value)
            }
        }

        val createdMonitoredItems = component.subscribe(subscriptionUUID, onItemCreated)
                .exceptionally {
                    log.severe("Encountered problem creating subscription for ${component.name}")
                    throw it
                }
                .get()
        createdMonitoredItems.forEach { item -> SubscriptionManager.registerMonitoredItem(subscriptionUUID, item) }
        activeSubscriptions[subscriptionUUID] = component

        log.info("Successfully created monitored item for: ${component.name}")

        return subscriptionUUID
    }

    fun unsubscribe(uuid: UUID) {
        activeSubscriptions[uuid]?.unsubscribe(uuid)?.get()
        activeSubscriptions.remove(uuid)
        DataDispatcher.removeQueue(uuid)
    }

    fun disconnect(server: Server) {
        server.disconnect()
                .whenComplete { _, _ -> log.info("Successfully disconnected from $server") }
                .exceptionally {
                    log.severe(it.localizedMessage)
                    throw it
                }
                .get()
    }

    fun handleDiscoveringCatalogueContentException() {
        log.info("Handling discovering catalogue content exception")
    }

    fun handleSubscribeException() {
        log.info("Handling subscribe exception")
        alert(AlertType.ERROR, "Error creating subscription")
    }

    fun handleDisconnectingException() {
        log.info("Handling disconnecting exception")
        alert(AlertType.ERROR, "Error trying to disconnect")
    }

    fun updateServerManagerState(server: Server) {
        ServerManager.removeServer(server)
        log.info("Removed $server from ServerManager")
    }
}