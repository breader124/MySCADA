package elka.achlebos.model.certificate

import elka.achlebos.model.CertificateLoadingException
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class AbstractCertificateLoader(protected val password: String, protected val path: Path) {
    protected val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    protected val certName = path.fileName.toString()

    abstract val certificate: Certificate
    abstract val keyPair: KeyPair

    @Throws(CertificateLoadingException::class)
    protected abstract fun loadCertificate(): Certificate
}

class X509CertificateLoader(password: String, path: Path) : AbstractCertificateLoader(password, path) {

    override val certificate: Certificate by lazy { loadCertificate() }
    override val keyPair: KeyPair by lazy { loadKeyPair() }

    init {
        initializeKeyStore()
    }

    override fun loadCertificate(): X509Certificate {
        return keyStore.getCertificate(certName) as X509Certificate
    }

    @Throws(CertificateLoadingException::class)
    private fun loadKeyPair(): KeyPair {
        val privateKey = keyStore.getKey(certName, password.toCharArray())
        val publicKey = certificate.publicKey
        return KeyPair(publicKey, privateKey as PrivateKey)
    }

    @Throws(CertificateLoadingException::class)
    private fun initializeKeyStore() {
        val passwordChars = password.toCharArray()
        try {
            val inputStream = Files.newInputStream(path)
            keyStore.load(inputStream, passwordChars)
        } catch (exc: Exception) {
            throw CertificateLoadingException(exc.localizedMessage)
        }
    }
}