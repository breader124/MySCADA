package elka.achlebos.viewmodel

import elka.achlebos.model.data.AddressSpaceComponent
import javafx.scene.control.Alert
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant
import tornadofx.*

// TODO handle read timeout

class ReadDialogModel : ViewModel() {
    private fun readNodeId(component: AddressSpaceComponent): Variant = component.readNodeId().get().value

    private fun readNodeClass(component: AddressSpaceComponent): Variant = component.readNodeClass().get().value

    private fun readBrowseName(component: AddressSpaceComponent): Variant = component.readBrowseName().get().value

    private fun readDisplayName(component: AddressSpaceComponent): Variant = component.readDisplayName().get().value

    private fun readDescription(component: AddressSpaceComponent): Variant = component.readDescription().get().value

    private fun readWriteMask(component: AddressSpaceComponent): Variant = component.readWriteMask().get().value

    private fun readUserWriteMask(component: AddressSpaceComponent): Variant = component.readUserWriteMask().get().value

    private fun readValue(component: AddressSpaceComponent): Variant = component.readValue().get().value

    private fun readDataType(component: AddressSpaceComponent): Variant = component.readDataType().get().value

    private fun readValueRank(component: AddressSpaceComponent): Variant = component.readValueRank().get().value

    private fun readArrayDimensions(component: AddressSpaceComponent): Variant = component.readArrayDimensions().get().value

    private fun readAccessLevel(component: AddressSpaceComponent): Variant = component.readAccessLevel().get().value

    private fun readUserAccessLevel(component: AddressSpaceComponent): Variant = component.readUserAccessLevel().get().value

    private fun readMinimumSamplingInterval(component: AddressSpaceComponent): Variant = component.readMinimumSamplingInterval().get().value

    private fun readHistorizing(component: AddressSpaceComponent): Variant = component.readHistorizing().get().value

    private fun readEventNotifier(component: AddressSpaceComponent): Variant = component.readEventNotifier().get().value

    fun performNodeRead(component: AddressSpaceComponent, option: NodeReadOption): Variant {
        return when (option) {
            NodeReadOption.NODE_ID -> readNodeId(component)
            NodeReadOption.NODE_CLASS -> readNodeClass(component)
            NodeReadOption.BROWSE_NAME -> readBrowseName(component)
            NodeReadOption.DISPLAY_NAME -> readDisplayName(component)
            NodeReadOption.DESCRIPTION -> readDescription(component)
            NodeReadOption.WRITE_MASKS -> readWriteMask(component)
            NodeReadOption.USER_WRITE_MASK -> readUserWriteMask(component)
            NodeReadOption.VALUE -> readValue(component)
            NodeReadOption.DATA_TYPE -> readDataType(component)
            NodeReadOption.VALUE_RANK -> readValueRank(component)
            NodeReadOption.ARRAY_DIMENSION -> readArrayDimensions(component)
            NodeReadOption.ACCESS_LEVEL -> readAccessLevel(component)
            NodeReadOption.USER_ACCESS_LEVEL -> readUserAccessLevel(component)
            NodeReadOption.MINIMUM_SAMPLING_INTERVAL -> readMinimumSamplingInterval(component)
            NodeReadOption.HISTORIZING -> readHistorizing(component)
        }
    }

    fun performCatalogueRead(component: AddressSpaceComponent, option: CatalogueReadOption): Variant {
        return when (option) {
            CatalogueReadOption.NODE_ID -> readNodeId(component)
            CatalogueReadOption.NODE_CLASS -> readNodeClass(component)
            CatalogueReadOption.BROWSE_NAME -> readBrowseName(component)
            CatalogueReadOption.DISPLAY_NAME -> readDisplayName(component)
            CatalogueReadOption.DESCRIPTION -> readDescription(component)
            CatalogueReadOption.WRITE_MASKS -> readWriteMask(component)
            CatalogueReadOption.USER_WRITE_MASK -> readUserWriteMask(component)
            CatalogueReadOption.EVENT_NOTIFIER -> readEventNotifier(component)
        }
    }

    fun handleReadException(exc: Throwable) {
        log.info("Handling discovery exception")
        alert(Alert.AlertType.ERROR, "Timed out. Please try again")
        log.severe(exc.localizedMessage)
    }
}