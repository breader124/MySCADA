package elka.achlebos.viewmodel

import elka.achlebos.model.CertificateCreatedEvent
import elka.achlebos.model.CertificateCreationErrorEvent
import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.ConnectionRefusedEvent
import elka.achlebos.view.CertificateCreationView
import elka.achlebos.view.popups.ConnectionCreatedDialog
import elka.achlebos.view.popups.ConnectionRefusedDialog
import javafx.stage.StageStyle
import tornadofx.*

class MainViewModel : ViewModel() {
    init {
        preferences {
            clear()

            val isCertificateAlreadyExists = getBoolean("isCertificateAlreadyExists", false)
            if (!isCertificateAlreadyExists) {
                find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
            }
        }

        subscribe<CertificateCreatedEvent> { event ->
            preferences {
                putBoolean("isCertificateAlreadyExists", true)
                put("certificateName", event.certificateName)
                put("applicationUri", event.appUri)
            }
            find<CertificateCreationView>().close()
        }

        subscribe<CertificateCreationErrorEvent> {

        }

        subscribe<ConnectionCreatedEvent> {
            find<ConnectionCreatedDialog>().openWindow()
        }

        subscribe<ConnectionRefusedEvent> {
            find<ConnectionRefusedDialog>().openWindow()
        }
    }
}