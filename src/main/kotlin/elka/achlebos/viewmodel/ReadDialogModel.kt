package elka.achlebos.viewmodel

import elka.achlebos.model.data.AddressSpaceComponent
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant
import tornadofx.*

class ReadDialogModel : ViewModel() {
    fun readNodeId(component: AddressSpaceComponent): Variant = component.readNodeId().get().value

    fun readNodeClass(component: AddressSpaceComponent): Variant = component.readNodeClass().get().value

    fun readBrowseName(component: AddressSpaceComponent): Variant = component.readBrowseName().get().value

    fun readDisplayName(component: AddressSpaceComponent): Variant = component.readDisplayName().get().value

    fun readDescription(component: AddressSpaceComponent): Variant = component.readDescription().get().value

    fun readWriteMask(component: AddressSpaceComponent): Variant = component.readWriteMask().get().value

    fun readUserWriteMask(component: AddressSpaceComponent): Variant = component.readUserWriteMask().get().value

    fun readValue(component: AddressSpaceComponent): Variant = component.readValue().get().value

    fun readDataType(component: AddressSpaceComponent): Variant = component.readDataType().get().value

    fun readValueRank(component: AddressSpaceComponent): Variant = component.readValueRank().get().value

    fun readArrayDimensions(component: AddressSpaceComponent): Variant = component.readArrayDimensions().get().value

    fun readAccessLevel(component: AddressSpaceComponent): Variant = component.readAccessLevel().get().value

    fun readUserAccessLevel(component: AddressSpaceComponent): Variant = component.readUserAccessLevel().get().value

    fun readMinimumSamplingInterval(component: AddressSpaceComponent): Variant = component.readMinimumSamplingInterval().get().value

    fun readHistorizing(component: AddressSpaceComponent): Variant = component.readHistorizing().get().value

    fun readEventNotifier(component: AddressSpaceComponent): Variant = component.readEventNotifier().get().value
}