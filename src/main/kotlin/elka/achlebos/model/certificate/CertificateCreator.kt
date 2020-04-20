package elka.achlebos.model.certificate

import java.nio.file.Path
import java.security.KeyStore

abstract class CertificateCreator(private val path: Path, private val info: CertificateInfo) {
    protected val keyStore: KeyStore = KeyStore.getInstance("PKCS12")

    abstract fun create()
}

class X509CertificateCreator(path: Path, info: X509CertificateInfo) : CertificateCreator(path, info) {
    override fun create() {
        TODO()
    }
}