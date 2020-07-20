package elka.achlebos.viewmodel

import elka.achlebos.model.certificate.X509CertificateInfo
import elka.achlebos.model.certificate.X509CertificateManager
import javafx.collections.ObservableList
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class CertificateCreationViewModel: ItemViewModel<X509CertificateManager>() {
    fun listCertificates(path: Path): ObservableList<Path> = Files.list(path)
                .filter { filePath -> Files.isRegularFile(filePath) }
                .toList()
                .asObservable()

    fun createCertificate(info: X509CertificateInfo, path: Path) = item.create(info, path)

    fun removeCertificate(path: Path) = item.remove(path)
}