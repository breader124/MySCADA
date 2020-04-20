package elka.achlebos.model.certificate

import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class CertificateLoader(val path: Path, val password: String) {
    protected val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_TYPE)
    abstract val certificate: Certificate
    abstract val keyPair: KeyPair

    protected abstract fun loadCertificate(): Certificate
}

class X509CertificateLoader(path: Path, password: String) : CertificateLoader(path, password) {

    override val certificate: Certificate by lazy { loadCertificate() }
    override val keyPair: KeyPair by lazy { loadKeyPair() }

    init {
        initializeKeyStore()
    }

    override fun loadCertificate(): X509Certificate {
        return keyStore.getCertificate(CERT_NAME) as X509Certificate
    }

    private fun loadKeyPair(): KeyPair {
        val privateKey = keyStore.getKey(CERT_NAME, password.toCharArray())
        val publicKey = certificate.publicKey
        return KeyPair(publicKey, privateKey as PrivateKey)
    }

    @Throws(CertificateLoadingException::class)
    private fun initializeKeyStore() {
        val passwordChars = password.toCharArray()
        try {
            val certPath = path.resolve(CERT_NAME)
            val inputStream = Files.newInputStream(certPath)
            keyStore.load(inputStream, passwordChars)
        } catch (exc: Exception) {
            throw CertificateLoadingException(exc.localizedMessage)
        }
    }
}