package elka.achlebos.model.data

import org.eclipse.milo.opcua.sdk.client.api.nodes.Node
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue
import java.util.concurrent.CompletableFuture

abstract class AddressSpaceComponent(open val node: Node) {
    open fun add(component: AddressSpaceComponent): Boolean = throw UnsupportedOperationException("Cannot add")

    open fun remove(component: AddressSpaceComponent): Boolean = throw UnsupportedOperationException("Cannot add")

    open fun readValue(): CompletableFuture<DataValue> = throw UnsupportedOperationException("Cannot add")
}

class AddressSpaceNode(override val node: VariableNode) : AddressSpaceComponent(node) {
    override fun readValue(): CompletableFuture<DataValue> = node.readValue()
}

class AddressSpaceCatalogue(node: Node) : AddressSpaceComponent(node) {
    private val items: MutableList<AddressSpaceComponent> = mutableListOf()

    override fun add(component: AddressSpaceComponent) = items.add(component)

    override fun remove(component: AddressSpaceComponent) = items.remove(component)
}