package elka.achlebos.view

import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.viewmodel.MainViewModel
import javafx.stage.StageStyle
import tornadofx.*

class MainView : View("MySCADA") {
    private val viewModel: MainViewModel by inject()

    override fun onDock() {
        super.onDock()
        if (viewModel.checkIfNeedToCreateNewCert()) {
            find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
        }
    }

    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    find<ConnectionCreationView>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }

            menu("Certificate") {
                item("Create").action {
                    find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
                }

                item("Remove").action {
                    viewModel.switchCertificateAlreadyExistsToFalse()
                    find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }
        }

        left = treeview<AddressSpaceComponent> {
//            TODO()
        }
    }
}
