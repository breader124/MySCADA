package elka.achlebos.viewmodel

import elka.achlebos.model.CertificateLoadingException
import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.certificate.X509CertificateManager
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.popup.ConnectionCreatedDialog
import elka.achlebos.view.popup.ProvidedPasswordIsIncorrectDialog
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.security.PrivilegedActionException
import java.time.LocalDate
import java.time.format.DateTimeParseException

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

    fun exportCertificateToBinaryFormat() {
        val keyStorePath: Path = Paths.get("keyStore.jks")
        preferences {
            val certName = get("certificateName", "")
            val certificateManager = X509CertificateManager()
            try {
                // TODO change to use provided, not hardcoded password
                val (cert, _) = certificateManager.load("password", keyStorePath, certName)
                File("binaryFormCert").writeBytes(cert.encoded)
            } catch (exc: CertificateLoadingException) {
                find<ProvidedPasswordIsIncorrectDialog>().openWindow()
            }
        }

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