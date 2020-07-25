package elka.achlebos.view.popups

import tornadofx.*

class CertificateAlreadyExistsDialog: Fragment() {
    override val root = label("Certificate with given name already exists")
}