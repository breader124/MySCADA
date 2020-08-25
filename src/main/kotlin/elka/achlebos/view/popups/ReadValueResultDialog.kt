package elka.achlebos.view.popups

import org.eclipse.milo.opcua.stack.core.types.builtin.Variant
import tornadofx.*

class ReadValueResultDialog : Fragment() {
    val valueRead: Variant by param()

    override val root = form {
        fieldset {
            field("Value") {
                textarea(valueRead.value.toString()) {
                    text = when(valueRead.value) {
                        is Array<*> -> (valueRead.value as Array<*>).contentDeepToString()
                        else -> valueRead.value.toString()
                    }

                    isWrapText = true
                    isEditable = false
                }
            }
        }
    }
}