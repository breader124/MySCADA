package elka.achlebos.view

import elka.achlebos.viewmodel.MainViewModel
import javafx.stage.StageStyle
import tornadofx.*

class MainView : View("MySCADA") {
    private val viewModel: MainViewModel by inject()

    override fun onDock() {
        super.onDock()
        if (viewModel.checkIfNeedToCreateNewCert()) {
            find<CertificateCreationView>().openModal(
                    stageStyle = StageStyle.UNDECORATED,
                    escapeClosesWindow = false
            )
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
                    find<CertificateCreationView>().openModal(
                            stageStyle = StageStyle.UNDECORATED,
                            escapeClosesWindow = true
                    )
                }

                item("Remove").action {
                    viewModel.switchCertificateAlreadyExistsToFalse()
                    find<CertificateCreationView>().openModal(
                            stageStyle = StageStyle.UNDECORATED,
                            escapeClosesWindow = false
                    )
                }
            }
        }

        left<AddressSpaceFragment>()

    }
}
