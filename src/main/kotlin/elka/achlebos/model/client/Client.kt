package elka.achlebos.model.client

import elka.achlebos.model.data.AddressSpaceCatalogue
import org.eclipse.milo.opcua.sdk.client.api.AddressSpace
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node

class Client(rootNode: Node, val addressSpace: AddressSpace) {
    val rootCatalogue = AddressSpaceCatalogue(rootNode)

    fun browseCatalogue(catalogue: AddressSpaceCatalogue) {
        TODO()
    }
}