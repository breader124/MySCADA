package elka.achlebos.viewmodel

import elka.achlebos.model.data.AddressSpaceNode
import javafx.beans.property.SimpleStringProperty
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode
import tornadofx.*

class WriteDialogModel : ViewModel() {
    val input = SimpleStringProperty("")

    fun writeTo(node: AddressSpaceNode): StatusCode {
        val valueAsDouble = input.value.toDouble()
        return node.writeValue(valueAsDouble)
                .whenComplete { statusCode: StatusCode?, _: Throwable? ->
                    statusCode?.also {
                        val msg = "Request for writing node ${node.name} value's completed with status code: $it"
                        log.info(msg)
                    }
                }
                .exceptionally {
                    throw it
                }
                .get()
    }
}