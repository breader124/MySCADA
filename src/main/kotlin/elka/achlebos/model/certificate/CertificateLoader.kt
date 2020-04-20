package elka.achlebos.model.certificate

import java.nio.file.Path
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class CertificateLoader(private val path: Path) {
    private val keyStore = KeyStore.getInstance("PKCS12")

    abstract fun load(): Certificate
}

class X509CertificateLoader(path: Path) : CertificateLoader(path) {
    override fun load(): X509Certificate {
        TODO()
    }
}