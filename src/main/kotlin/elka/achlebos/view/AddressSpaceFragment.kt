package elka.achlebos.view

import elka.achlebos.model.client.Client
import elka.achlebos.model.client.ClientsManager
import elka.achlebos.model.data.AddressSpaceComponent
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import tornadofx.*

class AddressSpaceFragment : Fragment() {

    private val connectedServers: ObservableList<Client> = ClientsManager.connected
    private val selectedServer = SimpleObjectProperty<Client>()

    private var serverTreeVBox: VBox by singleAssign()

    init {
        selectedServer.onChange {
            it?.run {
                val treeView = generateTreeFor(rootCatalogue)
                serverTreeVBox.replaceChildren(treeView)
            }
        }
    }

    override val root = vbox {
        combobox(selectedServer, connectedServers)

        vbox {
            serverTreeVBox = this
        }
    }

    private fun generateTreeFor(root: AddressSpaceComponent): TreeView<AddressSpaceComponent> {
        return treeview(TreeItem(root)) {
            populate {
                it.value.items
            }

            cellFormat {
                label(this.item.toString())
            }

            onUserSelect {
                println("$it")
            }
        }
    }
}