package elka.achlebos.view

import elka.achlebos.view.fragment.AddressSpaceFragment
import elka.achlebos.viewmodel.MainViewModel
import javafx.stage.StageStyle
import tornadofx.*


class MainView : View("MySCADA") {
    private val viewModel: MainViewModel by inject()


    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    if (viewModel.checkIfNeedToCreateNewCert()) {
                        openCertificateCreationWindow()
                    }
                    find<ConnectionCreationView>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }

            menu("Certificate") {
                item("Create").action {
                    openCloseableCertificateCreationWindow()
                }

                item("Remove").action {
                    viewModel.switchCertificateAlreadyExistsToFalse()
                    openCertificateCreationWindow()
                }
            }
        }

        left<AddressSpaceFragment>()
    }

    private fun openCertificateCreationWindow() {
        openInternalWindow<CertificateCreationView>(
                modal = true,
                escapeClosesWindow = false,
                closeButton = false
        )
    }

    private fun openCloseableCertificateCreationWindow() {
        openInternalWindow<CertificateCreationView>(
                modal = true,
                escapeClosesWindow = true,
                closeButton = true
        )
    }
}
