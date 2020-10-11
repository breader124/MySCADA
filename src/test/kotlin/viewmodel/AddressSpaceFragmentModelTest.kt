package viewmodel

import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.model.server.Server
import elka.achlebos.viewmodel.AddressSpaceFragmentModel
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.stack.core.types.builtin.*
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import tornadofx.*
import java.lang.Exception
import java.util.concurrent.CompletableFuture

class AddressSpaceFragmentModelTest {

    private val model = AddressSpaceFragmentModel()

    private val catComponent = Mockito.mock(AddressSpaceCatalogue::class.java)
    private val nodeComponent = Mockito.mock(AddressSpaceNode::class.java)
    private val currentClient = Mockito.mock(OpcUaClient::class.java)
    private val server = Mockito.mock(Server::class.java)

    private val refDescriptions = createReferenceDescriptions()

    @BeforeEach
    fun init() {
        val itemsList = observableListOf<AddressSpaceComponent>()
        `when`(catComponent.items).thenReturn(itemsList)
        `when`(catComponent.add(any(AddressSpaceComponent::class.java))).thenCallRealMethod()
    }

    @Test
    fun shouldAddDiscoveredCatalogueContentToAncestorCatalogueWhenDiscoveryInvoked() {
        // given
        val f = CompletableFuture.supplyAsync {
            BrowseResult(
                    StatusCode.GOOD,
                    ByteString.NULL_VALUE,
                    refDescriptions
            )
        }
        `when`(catComponent.browse()).thenReturn(f)

        // when
        val items = model.discoverCatalogueContent(catComponent, currentClient) ?: fail("Items null")

        // then
        val cataloguesNum = items.count { it is AddressSpaceCatalogue }
        val nodesNum = refDescriptions.size - cataloguesNum
        assertThat(cataloguesNum).isEqualTo(5)
        assertThat(nodesNum).isEqualTo(5)
        assertThat(items.size).isEqualTo(10)
    }

    @Test
    fun shouldReturnEmptyItemsListWhenAncestorComponentWasNodeType() {
        // given + when
        val items = model.discoverCatalogueContent(nodeComponent, currentClient)

        // then
        assertThat(items.isNullOrEmpty())
    }

    @Test
    fun shouldReturnEmptyItemsListWhenExceptionOccurredWhileDiscoveryProcess() {
        // given
        val compFuture: CompletableFuture<BrowseResult> = CompletableFuture.supplyAsync{ throw Exception() }
        `when`(catComponent.browse()).thenReturn(compFuture)

        // when
        val items = model.discoverCatalogueContent(nodeComponent, currentClient)

        // then
        assertThat(items.isNullOrEmpty())
    }
}

private fun createReferenceDescriptions(): Array<ReferenceDescription> {
    val r = arrayListOf<ReferenceDescription>()
    for (i in 0..9) {
        val nodeId = NodeId(i, i)
        val rd = ReferenceDescription(
                nodeId,
                false,
                ExpandedNodeId(nodeId, null),
                QualifiedName(i, "qualified_name_$i"),
                LocalizedText("some text with index $i"),
                if (i % 2 == 0) NodeClass.Object else NodeClass.Variable,
                ExpandedNodeId(nodeId)
        )
        r.add(rd)
    }
    return r.toTypedArray()
}

private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)