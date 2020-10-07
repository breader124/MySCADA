package elka.achlebos.view.dialog

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class PasswordProviderDialog : Fragment("Enter password") {
    private val passwordProperty = SimpleStringProperty("")

    override val root = form {
        fieldset {
            field("Password:") {
                passwordfield(passwordProperty)
            }

            buttonbar {
                button("Proceed") {
                    action {
                        close()
                    }
                }
            }
        }
    }

    fun getPassword(): String = passwordProperty.value
}