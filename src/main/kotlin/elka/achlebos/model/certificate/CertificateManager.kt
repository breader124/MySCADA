package elka.achlebos.model.certificate

import java.nio.file.Path

class CertificateManager(private val path: Path, private val password: String) {
    private lateinit var creator: CertificateCreator
    private lateinit var loader: CertificateLoader

    fun create(certificateInfo: CertificateInfo) {
        TODO()
    }

    fun load() {

    }

    fun remove() {

    }
}