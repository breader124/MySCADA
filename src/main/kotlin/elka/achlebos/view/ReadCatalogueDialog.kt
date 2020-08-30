package elka.achlebos.view

import elka.achlebos.model.data.AddressSpaceCatalogue
import elka.achlebos.viewmodel.CatalogueReadOption
import elka.achlebos.viewmodel.ReadDialogModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ReadCatalogueDialog : View() {
    val component: AddressSpaceCatalogue by param()
    private val model: ReadDialogModel by inject()

    private val readOptions = observableListOf(CatalogueReadOption.values().toList())
    private val selectedOption = SimpleObjectProperty<CatalogueReadOption>(CatalogueReadOption.NODE_ID)

    private val readResultProperty = SimpleStringProperty()

    override val root = form {
        fieldset {
            combobox(selectedOption, readOptions)

            textarea(readResultProperty)

            button("Read") {
                action {
                    runAsync {
                        model.performCatalogueRead(component, selectedOption.value)
                    } ui {
                        readResultProperty.value = it.value?.toString() ?: "No value"
                    }
                }
            }
        }
    }
}