package elka.achlebos.model.data

import javafx.collections.ObservableList
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem
import org.eclipse.milo.opcua.stack.core.AttributeId
import org.eclipse.milo.opcua.stack.core.Identifiers
import org.eclipse.milo.opcua.stack.core.types.builtin.*
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned
import org.eclipse.milo.opcua.stack.core.types.enumerated.*
import org.eclipse.milo.opcua.stack.core.types.structured.*
import tornadofx.*
import java.util.concurrent.CompletableFuture

abstract class AddressSpaceComponent(open val nodeId: NodeId,
                                     val name: String,
                                     protected val client: OpcUaClient) {

    abstract val items: ObservableList<AddressSpaceComponent>?

    open fun add(component: AddressSpaceComponent) {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun remove(component: AddressSpaceComponent) {
        throw UnsupportedOperationException("Cannot add")
    }

    open fun browse(): CompletableFuture<BrowseResult> {
        throw UnsupportedOperationException("Cannot browse")
    }

    fun readNodeId(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readNodeId()
    }

    fun readNodeClass(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readNodeClass()
    }

    fun readBrowseName(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readBrowseName()
    }

    fun readDisplayName(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readDisplayName()
    }

    fun readDescription(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readDescription()
    }

    fun readWriteMask(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readWriteMask()
    }

    fun readUserWriteMask(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readUserWriteMask()
    }

    open fun readValue(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readDataType(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readValueRank(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readArrayDimensions(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readAccessLevel(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readUserAccessLevel(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readMinimumSamplingInterval(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readHistorizing(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun readEventNotifier(): CompletableFuture<DataValue> {
        throw UnsupportedOperationException("Cannot read")
    }

    open fun writeValue(value: Number): CompletableFuture<StatusCode> {
        throw UnsupportedOperationException("Cannot write")
    }

    open fun subscribe(timeInterval: Double,
                       onItemCreated: (UaMonitoredItem, Int) -> Unit): CompletableFuture<List<UaMonitoredItem>> {
        throw UnsupportedOperationException("Cannot subscribe")
    }
}

class AddressSpaceNode(nodeId: NodeId, name: String, client: OpcUaClient) : AddressSpaceComponent(nodeId, name, client) {

    override var items: ObservableList<AddressSpaceComponent>? = null

    override fun readValue(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readValue()
    }

    override fun readDataType(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readDataType()
    }

    override fun readValueRank(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readValueRank()
    }

    override fun readArrayDimensions(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readArrayDimensions()
    }

    override fun readAccessLevel(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readAccessLevel()
    }

    override fun readUserAccessLevel(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readUserAccessLevel()
    }

    override fun readMinimumSamplingInterval(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readMinimumSamplingInterval()
    }

    override fun readHistorizing(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createVariableNode(nodeId)
        return variableNode.readHistorizing()
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

class AddressSpaceCatalogue(node: NodeId,
                            name: String,
                            client: OpcUaClient) : AddressSpaceComponent(node, name, client) {

    override val items: ObservableList<AddressSpaceComponent> = observableListOf()

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

    override fun readEventNotifier(): CompletableFuture<DataValue> {
        val variableNode = client.addressSpace.createObjectNode(nodeId)
        return variableNode.readEventNotifier()
    }
}
