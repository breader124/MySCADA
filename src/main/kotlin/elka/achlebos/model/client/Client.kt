package elka.achlebos.model.client

import elka.achlebos.model.data.AddressSpaceCatalogue
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.stack.core.Identifiers

class Client(private val name: String, val opcUaClient: OpcUaClient) {
    val rootCatalogue = AddressSpaceCatalogue(Identifiers.RootFolder, "Root", opcUaClient)

    override fun toString(): String {
        return name
    }
}