package elka.achlebos.view.popups

import org.eclipse.milo.opcua.stack.core.types.builtin.Variant
import tornadofx.*

class ReadResultDialog : Fragment() {
    val valueRead: Variant by param()

    override val root = form {

    }
}