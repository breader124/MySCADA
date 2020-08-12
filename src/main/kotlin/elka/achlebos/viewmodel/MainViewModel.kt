package elka.achlebos.viewmodel

import elka.achlebos.model.*
import elka.achlebos.model.client.Client
import elka.achlebos.model.client.ClientsManager
import elka.achlebos.view.popups.ConnectionCreatedDialog
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeParseException;

class MainViewModel : ViewModel() {
    init {
        preferences {
            clear() // TODO("remove at further stage of project")
        }

        subscribe<ConnectionCreatedEvent> { event ->
            find<ConnectionCreatedDialog>().openWindow()

            val c = Client(event.opcUaClient)
            ClientsManager.addClient(c)
        }
    }

    fun switchCertificateAlreadyExistsToFalse() {
        preferences {
            putBoolean("isCertificateAlreadyExists", false)
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
                isCertificateNotExpired = LocalDate.now().isBefore(expirationDate)
            } catch (exc: DateTimeParseException) {
                // suppress exception
            }
        }

        return isCertificateNotExpired
    }
}