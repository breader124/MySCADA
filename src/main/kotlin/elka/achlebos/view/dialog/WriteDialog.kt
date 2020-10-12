package elka.achlebos.view.dialog

import elka.achlebos.model.data.AddressSpaceNode
import elka.achlebos.viewmodel.WriteDialogModel
import javafx.scene.control.Alert.AlertType
import tornadofx.*

class WriteDialog : View() {
    val node: AddressSpaceNode by param()
    private val model: WriteDialogModel by inject()

    override val root = form {
        fieldset {
            field("Value") {
                textfield(model.input) {
                    model.addValidator(this, model.input) {
                        val isBlank = !model.input.value.isNotBlank()
                        val isInputNotNumeric = !model.input.value.isDouble()
                        if (isBlank || isInputNotNumeric) error("Value should be numeric") else null
                    }
                }
            }
        }

        button("Write to node") {
            enableWhen(model.valid)

            action {
                runAsync {
                    model.writeTo(node)
                } success {
                    var result = ""
                    result = if (it.isGood) "Success" else ""
                    result = if (it.isBad) "Failure" else result
                    result = if (it.isUncertain) "Uncertain" else result
                    val message = "Result: $result"
                    alert(AlertType.INFORMATION, "Write node...", message)
                } fail {
                    alert(AlertType.ERROR, "Network error occurred writing to node")
                } finally {
                    close()
                }
            }
        }
    }
}