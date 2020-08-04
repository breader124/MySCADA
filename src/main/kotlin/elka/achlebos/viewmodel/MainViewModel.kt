package elka.achlebos.viewmodel

import elka.achlebos.model.*
import elka.achlebos.view.CertificateCreationView
import elka.achlebos.view.popups.CertificateCreationErrorDialog
import elka.achlebos.view.popups.ConnectionCreatedDialog
import elka.achlebos.view.popups.ConnectionRefusedDialog
import javafx.stage.StageStyle
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

class MainViewModel : ViewModel() {
    init {
        preferences {
            clear() // TODO("remove at further stage of project")

            val isCertificateAlreadyExists = getBoolean("isCertificateAlreadyExists", false)
            val isCertificateExpired = checkIfCertificateExpired()

            if (!isCertificateAlreadyExists || isCertificateExpired) {
                find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
            }
        }

        subscribe<CertificateCreatedEvent> { event ->
            preferences {
                putBoolean("isCertificateAlreadyExists", true)
                put("certificateName", event.certificateName)
                put("applicationUri", event.appUri)

                val expirationDate = LocalDate.now().plus(event.validityPeriod)
                put("expirationDate", expirationDate.toString())
            }
            find<CertificateCreationView>().close()
        }

        subscribe<CertificateRemovedEvent> {
            preferences {
                putBoolean("isCertificateAlreadyExists", false)
            }
            find<CertificateCreationView>().openModal(stageStyle = StageStyle.UTILITY)
        }

        subscribe<CertificateCreationErrorEvent> {
            find<CertificateCreationErrorDialog>().openWindow()
        }

        subscribe<ConnectionCreatedEvent> {
            find<ConnectionCreatedDialog>().openWindow()
        }

        subscribe<ConnectionRefusedEvent> {
            find<ConnectionRefusedDialog>().openWindow()
        }
    }

    private fun checkIfCertificateExpired(): Boolean {
        var isCertificateExpired = true
        preferences {
            try {
                val expirationDate = LocalDate.parse(get("expirationDate", ""))
                isCertificateExpired = expirationDate.isBefore(LocalDate.now())
            } catch (exc: DateTimeParseException) {
                // suppress exception
            }
        }

        return isCertificateExpired
    }
}