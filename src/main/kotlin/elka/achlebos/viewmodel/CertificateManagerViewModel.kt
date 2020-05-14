package elka.achlebos.viewmodel

import elka.achlebos.model.certificate.X509CertificateManager
import javafx.collections.ObservableList
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class CertificateManagerViewModel: ItemViewModel<X509CertificateManager>() {
    fun listCertificates(path: Path): ObservableList<Path> {
        return Files.list(path)
                .filter { filePath -> Files.isRegularFile(filePath) }
                .toList()
                .asObservable()
    }
}