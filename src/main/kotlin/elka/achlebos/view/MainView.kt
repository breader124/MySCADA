package elka.achlebos.view

import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.viewmodel.MainViewModel
import javafx.stage.StageStyle
import tornadofx.*

class MainView : View("MySCADA") {
    private val viewModel: MainViewModel = MainViewModel()

    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    find<ConnectionCreationView>().openWindow(stageStyle = StageStyle.UTILITY)
                }
            }
        }

        left = treeview<AddressSpaceComponent> {
//            TODO()
        }
    }
}
