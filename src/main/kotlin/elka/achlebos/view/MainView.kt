package elka.achlebos.view

import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.ConnectionRefusedEvent
import elka.achlebos.model.data.AddressSpaceComponent
import elka.achlebos.view.popups.ConnectionCreatedDialog
import elka.achlebos.view.popups.ConnectionRefusedDialog
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*
import java.nio.file.Files

class MainView : View("MySCADA") {
    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    find<ConnectionCreationView>().openWindow(stageStyle = StageStyle.UTILITY)
                }
            }
            menu("Certificate") {
                item("Create").action {
                    openInternalWindow<CertificateCreationView>()
                }
                item("Remove").action {
                    val f = chooseFile(
                            title = "Choose certificate to delete",
                            filters = arrayOf(
                                    FileChooser.ExtensionFilter("Certificates", "*.msc")
                            ),
                            mode = FileChooserMode.Single,
                            owner = this@MainView.currentWindow
                    )
                    f.firstOrNull()?.also {
                        Files.deleteIfExists(it.toPath())
                    }
                }
            }
        }

        left = treeview<AddressSpaceComponent> {
//            TODO()
        }

        subscribe<ConnectionCreatedEvent> {
            find<ConnectionCreatedDialog>().openWindow()
        }

        subscribe<ConnectionRefusedEvent> {
            find<ConnectionRefusedDialog>().openWindow()
        }
    }
}
