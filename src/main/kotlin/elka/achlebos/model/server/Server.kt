package elka.achlebos.model.server

import elka.achlebos.model.data.AddressSpaceCatalogue
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.stack.core.Identifiers
import java.util.concurrent.CompletableFuture

class Server(private val name: String, val opcUaClient: OpcUaClient) {
    val rootCatalogue = AddressSpaceCatalogue(Identifiers.RootFolder, "Root", opcUaClient)

    fun disconnect(): CompletableFuture<OpcUaClient> = opcUaClient.disconnect()

    override fun toString(): String {
        return name
    }
}