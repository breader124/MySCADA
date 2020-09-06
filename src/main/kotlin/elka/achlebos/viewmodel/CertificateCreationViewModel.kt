package elka.achlebos.viewmodel

import elka.achlebos.model.certificate.X509CertificateInfo
import elka.achlebos.model.certificate.X509CertificateManager
import javafx.collections.ObservableList
import sun.security.x509.X509CertInfo
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.streams.toList

class CertificateCreationViewModel: ItemViewModel<X509CertificateManager>() {
    fun createCertificate(info: X509CertificateInfo, certName: String) {
        item.create(info, certName)

        log.info("Created certificate with name: $certName and info: $info")
    }
}