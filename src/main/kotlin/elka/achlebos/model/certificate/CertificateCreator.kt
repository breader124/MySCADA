package elka.achlebos.model.certificate

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class AbstractCertificateCreator(protected open val info: CertificateInfo,
                                          protected val path: Path,
                                          protected val password: String) {
    protected val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_TYPE)
    abstract val certificate: Certificate
    abstract val keyPair: KeyPair

    @Throws(CertificateCreationException::class)
    protected abstract fun create(): Certificate
}

class X509AbstractCertificateCreator(
        override val info: X509CertificateInfo,
        path: Path,
        password: String
) : AbstractCertificateCreator(info, path, password) {

    override val certificate: X509Certificate by lazy { create() }
    override lateinit var keyPair: KeyPair

    @Throws(CertificateCreationException::class)
    override fun create(): X509Certificate {
        keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(4096)
        val certificate = UserX509CertificateBuilder(info, keyPair).build()
        storeCertificate(certificate, keyPair)

        return certificate
    }

    @Throws(CertificateCreationException::class)
    private fun storeCertificate(certificate: X509Certificate, keyPair: KeyPair) {
        val passwordChars = password.toCharArray()
        val certName = path.fileName.toString()
        keyStore.load(null, passwordChars)
        keyStore.setKeyEntry(certName, keyPair.private, passwordChars, arrayOf(certificate))
        try {
            val outputStream = Files.newOutputStream(path)
            keyStore.store(outputStream, passwordChars)
        } catch (exc: IOException) {
            throw CertificateCreationException(exc.localizedMessage ?: "")
        }
    }
}