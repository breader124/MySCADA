package elka.achlebos.model.data

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem
import org.eclipse.milo.opcua.stack.core.AttributeId
import org.eclipse.milo.opcua.stack.core.Identifiers
import org.eclipse.milo.opcua.stack.core.types.builtin.*
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned
import org.eclipse.milo.opcua.stack.core.types.enumerated.*
import org.eclipse.milo.opcua.stack.core.types.structured.*
import java.util.concurrent.CompletableFuture

abstract class AddressSpaceComponent(protected open val nodeId: NodeId,
                                     protected val client: OpcUaClient) {
    open fun add(component: AddressSpaceComponent) {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun remove(component: AddressSpaceComponent) {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun browse(): CompletableFuture<BrowseResult> {
        throw UnsupportedOperationException("Cannot browse")
    }

    open fun readValue(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun writeValue(value: Number): CompletableFuture<StatusCode> {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun subscribe(timeInterval: Double,
                       onItemCreated: (UaMonitoredItem, Int) -> Unit): CompletableFuture<List<UaMonitoredItem>> {
        throw UnsupportedOperationException("Cannot subscribe")
    }
}

class AddressSpaceNode(nodeId: NodeId, client: OpcUaClient) : AddressSpaceComponent(nodeId, client) {
    override fun readValue(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readValue()
    }

    override fun writeValue(value: Number): CompletableFuture<StatusCode> {
        val variant = Variant(value)
        val dataValue = DataValue(variant)
        return client.writeValue(nodeId, dataValue)
    }

    override fun subscribe(timeInterval: Double,
                           onItemCreated: (UaMonitoredItem, Int) -> Unit): CompletableFuture<List<UaMonitoredItem>> {
        val subscription = client.subscriptionManager.createSubscription(timeInterval)
        return subscription.thenCompose {
            val readValue = ReadValueId(
                    nodeId,
                    AttributeId.Value.uid(),
                    null,
                    QualifiedName.NULL_VALUE
            )
            val clientHandle = it.nextClientHandle()
            val params = MonitoringParameters(
                    clientHandle,
                    timeInterval,
                    null,
                    Unsigned.uint(100),
                    true
            )
            val request = MonitoredItemCreateRequest(
                    readValue,
                    MonitoringMode.Reporting,
                    params
            )
            it.createMonitoredItems(
                    TimestampsToReturn.Both,
                    arrayListOf(request),
                    onItemCreated
            )
        }
    }
}

class AddressSpaceCatalogue(node: NodeId, client: OpcUaClient) : AddressSpaceComponent(node, client) {
    private val items: MutableList<AddressSpaceComponent> = mutableListOf()

    override fun add(component: AddressSpaceComponent) {
        items.add(component)
    }

    override fun remove(component: AddressSpaceComponent) {
        items.remove(component)
    }

    override fun browse(): CompletableFuture<BrowseResult> {
        val browseDescription = BrowseDescription(
                nodeId,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                Unsigned.uint(NodeClass.Object.value.or(NodeClass.Variable.value)),
                Unsigned.uint(BrowseResultMask.All.value)
        )
        return client.browse(browseDescription)
    }
}
