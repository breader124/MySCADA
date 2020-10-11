package elka.achlebos.viewmodel

import elka.achlebos.model.CertificateLoadingException
import elka.achlebos.model.ConnectionCreatedEvent
import elka.achlebos.model.certificate.X509CertificateManager
import elka.achlebos.model.server.Server
import elka.achlebos.model.server.ServerManager
import elka.achlebos.view.dialog.PasswordProviderDialog
import javafx.scene.control.Alert.AlertType
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeParseException

class MainViewModel : ViewModel() {
    init {
        subscribe<ConnectionCreatedEvent> { event ->
            alert(AlertType.INFORMATION, "Connection created successfully")
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

    fun exportCertAndPrivateKeyToBinaryFormat() {
        val keyStorePath: Path = Paths.get("keyStore.jks")
        preferences {
            val certName = get("certificateName", "")
            val certificateManager = X509CertificateManager()
            try {
                val modal = find<PasswordProviderDialog>().apply {
                    openModal(block = true)
                }

                if (modal.shouldContinue) {
                    val (cert, keyPair) = certificateManager.load(modal.getPassword(), keyStorePath, certName)

                    var chosenFile = chooseFile(
                            title = "Choose certificate file",
                            filters = emptyArray()
                    ).firstOrNull()

                    chosenFile?.apply {
                        File(path).writeBytes(cert.encoded)
                    }

                    chosenFile = chooseFile(
                            title = "Choose private key file",
                            filters = emptyArray()
                    ).firstOrNull()

                    chosenFile?.apply {
                        File(path).writeBytes(keyPair.private.encoded)
                    }
                }
            } catch (exc: CertificateLoadingException) {
                alert(AlertType.ERROR, "Provided password is incorrect")
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