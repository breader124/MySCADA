package elka.achlebos.view

import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.viewmodel.ReadDialogModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

enum class ReadOption {
    NODE_ID,
    NODE_CLASS,
    BROWSE_NAME,
    DISPLAY_NAME,
    DESCRIPTION,
    WRITE_MASKS,
    USER_WRITE_MASK,
    VALUE,
    DATA_TYPE,
    VALUE_RANK,
    ARRAY_DIMENSION,
    ACCESS_LEVEL,
    USER_ACCESS_LEVEL,
    MINIMUM_SAMPLING_INTERVAL,
    HISTORIZING,
    EVENT_NOTIFIER,;

    override fun toString(): String {
        val name = super.toString()
        return name
                .split("_", " ")
                .joinToString(" ") { it.toLowerCase().capitalize() }
    }
}

class ReadDialog : View() {
    private val component: AddressSpaceComponent by param()
    private val model: ReadDialogModel by inject()

    private val readOptionList = ReadOption.values().toList().asObservable()
    private val selectedOption = SimpleObjectProperty<ReadOption>()

    init {
        selectedOption.value = ReadOption.NODE_ID
    }

    override val root = form {
        fieldset {
            combobox(selectedOption, readOptionList)

            textarea("Here will be result")

            button("Read") {
                action {
                    println("Value obtained")
                }
            }
        }
    }
}