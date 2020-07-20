package elka.achlebos.view

import tornadofx.*

class MainView : View("MySCADA") {
    override val root = borderpane {
        top = menubar {
            menu("Certificates") {
                item("Create").action {
                    openInternalWindow<CertificateCreationView>()
                }
                item("Remove").action {

                }
            }
        }
    }
}
