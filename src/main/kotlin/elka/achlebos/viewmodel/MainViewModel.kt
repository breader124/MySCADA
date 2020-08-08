package elka.achlebos.viewmodel

import elka.achlebos.model.*
import elka.achlebos.view.CertificateCreationView
import elka.achlebos.view.popups.CertificateCreationErrorDialog
import elka.achlebos.view.popups.ConnectionCreatedDialog
import elka.achlebos.view.popups.ConnectionRefusedDialog
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeParseException;

class MainViewModel : ViewModel() {
    init {
        preferences {
            clear() // TODO("remove at further stage of project")
        }

        subscribe<CertificateCreatedEvent> { event ->
            preferences {
                putBoolean("isCertificateAlreadyExists", true)
                put("certificateName", event.certificateName)
                put("applicationUri", event.appUri)

                val expirationDate = LocalDate.now().plus(event.validityPeriod)
                put("expirationDate", expirationDate.toString())
            }
        }

        subscribe<CertificateRemovedEvent> {
            preferences {
                putBoolean("isCertificateAlreadyExists", false)
            }
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

    fun checkIfNeedToCreateNewCert(): Boolean {
        var needToCreateNewCert = false
        preferences {
            val isCertificateAlreadyExists = getBoolean("isCertificateAlreadyExists", false)
            val isCertificateNotExpired = checkIfCertificateNotExpired()

            needToCreateNewCert = (!isCertificateAlreadyExists || !isCertificateNotExpired)
        }
        return needToCreateNewCert
    }

    private fun checkIfCertificateNotExpired(): Boolean {
        var isCertificateNotExpired = false
        preferences {
            try {
                val expirationDate = LocalDate.parse(get("expirationDate", ""))
                isCertificateNotExpired = expirationDate.isBefore(LocalDate.now())
            } catch (exc: DateTimeParseException) {
                // suppress exception
            }
        }

        return isCertificateNotExpired
    }
}