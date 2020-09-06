package elka.achlebos.viewmodel

import elka.achlebos.model.*
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.popup.ConnectionCreatedDialog
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeParseException;

class MainViewModel : ViewModel() {
    init {
        subscribe<ConnectionCreatedEvent> { event ->
            find<ConnectionCreatedDialog>().openWindow()
            ServerManager.addServer(Server(event.name, event.opcUaClient))
        }
    }

    fun switchCertificateAlreadyExistsToFalse() {
        preferences {
            putBoolean("isCertificateAlreadyExists", false)
        }

        log.info("Set 'isCertificateAlreadyExists' parameter to false")
    }

    fun checkIfNeedToCreateNewCert(): Boolean {
        var needToCreateNewCert = false
        preferences {
            val isCertificateAlreadyExists = getBoolean("isCertificateAlreadyExists", false)
            val isCertificateNotExpired = checkIfCertificateNotExpired()

            needToCreateNewCert = (!isCertificateAlreadyExists || !isCertificateNotExpired)
        }

        log.info("Checking if there is need to create new cert with final result: $needToCreateNewCert")

        return needToCreateNewCert
    }

    private fun checkIfCertificateNotExpired(): Boolean {
        var isCertificateNotExpired = false
        preferences {
            try {
                val expirationDate = LocalDate.parse(get("expirationDate", ""))
                isCertificateNotExpired = LocalDate.now().isBefore(expirationDate)
            } catch (exc: DateTimeParseException) {
                // suppress exception
            }
        }

        log.info("Checking if certificate is not expired with final result: $isCertificateNotExpired")

        return isCertificateNotExpired
    }
}