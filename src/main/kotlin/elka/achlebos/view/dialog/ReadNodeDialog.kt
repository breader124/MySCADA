package elka.achlebos.view.dialog

import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.viewmodel.NodeReadOption
import elka.achlebos.viewmodel.ReadDialogModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ReadNodeDialog : View() {
    val component: AddressSpaceComponent by param()
    private val model: ReadDialogModel by inject()

    private val readOptions = observableListOf(NodeReadOption.values().toList())
    private val selectedOption = SimpleObjectProperty<NodeReadOption>(NodeReadOption.NODE_ID)

    private val readResultProperty = SimpleStringProperty()

    init {
        title = component.name
    }

    override val root = form {
        fieldset {
            combobox(selectedOption, readOptions)

            textarea(readResultProperty) {
                isWrapText = true
                isEditable = false
            }

            button("Read") {
                action {
                    runAsync {
                        model.performNodeRead(component, selectedOption.value)
                    } ui {
                        readResultProperty.value = when (it.value) {
                            is Array<*> -> (it.value as Array<*>).contentDeepToString()
                            else -> it.value?.toString() ?: "No value"
                        }
                    }
                }
            }
        }
    }
}