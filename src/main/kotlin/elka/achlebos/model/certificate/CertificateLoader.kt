package elka.achlebos.model.certificate

import java.nio.file.Path
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class CertificateLoader(val path: Path, val password: String) {
    private val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)

    abstract fun load(): Certificate
}

class X509CertificateLoader(path: Path, password: String) : CertificateLoader(path, password) {
    override fun load(): X509Certificate {
        TODO()
    }
}