package elka.achlebos.view

import elka.achlebos.model.data.AddressSpaceComponent
import javafx.stage.FileChooser
import tornadofx.*
import java.nio.file.Files

class MainView : View("MySCADA") {
    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    println("New connection created")
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
                            mode = FileChooserMode.Single
                    )
                    f.firstOrNull()?.also {
                        Files.deleteIfExists(it.toPath())
                    }
                }
            }
        }

        left = treeview<AddressSpaceComponent> {

        }
    }
}
