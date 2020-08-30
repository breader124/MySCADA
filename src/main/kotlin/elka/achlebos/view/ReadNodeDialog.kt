package elka.achlebos.view

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

    override val root = form {
        fieldset {
            combobox(selectedOption, readOptions)

            textarea(readResultProperty)

            button("Read") {
                action {
                    runAsync {
                        model.performNodeRead(component, selectedOption.value)
                    } ui {
                        readResultProperty.value = it.value?.toString() ?: "No value"
                    }
                }
            }
        }
    }
}