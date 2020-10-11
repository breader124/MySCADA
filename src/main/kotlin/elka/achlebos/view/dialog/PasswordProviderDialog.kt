package elka.achlebos.view.dialog

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class PasswordProviderDialog : Fragment("Enter password") {
    private val passwordProperty = SimpleStringProperty("")
    var shouldContinue: Boolean = false

    override val root = form {
        fieldset {
            field("Password:") {
                passwordfield(passwordProperty)
            }

            buttonbar {
                button("Proceed") {
                    action {
                        shouldContinue = true
                        close()
                    }
                }
            }
        }
    }

    fun getPassword(): String = passwordProperty.value
}