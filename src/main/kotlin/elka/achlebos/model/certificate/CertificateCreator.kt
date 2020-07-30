package elka.achlebos.model.certificate

import elka.achlebos.model.CertificateCreationException
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyPair
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

abstract class AbstractCertificateCreator(protected open val info: CertificateInfo,
                                          protected val certificateName: String) {
    protected val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    abstract val certificate: Certificate
    abstract val keyPair: KeyPair

    @Throws(CertificateCreationException::class)
    protected abstract fun createCertificate(): Certificate
}

class X509CertificateCreator(
        override val info: X509CertificateInfo,
        certificateName: String
) : AbstractCertificateCreator(info, certificateName) {

    override val certificate: X509Certificate by lazy { createCertificate() }
    override lateinit var keyPair: KeyPair

    init {
        initializeKeyStore()
    }

    @Throws(CertificateCreationException::class)
    private fun initializeKeyStore() {
        val passwordChars = info.password.toCharArray()
        keyStore.load(null, passwordChars)
        persistKeyStore()
    }

    @Throws(CertificateCreationException::class)
    override fun createCertificate(): X509Certificate {
        keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048)
        val certificate = UserX509CertificateBuilder(info, keyPair).build()
        storeCertificate(certificate, keyPair)

        return certificate
    }

    @Throws(CertificateCreationException::class)
    private fun storeCertificate(certificate: X509Certificate, keyPair: KeyPair) {
        val passwordChars = info.password.toCharArray()
        keyStore.setKeyEntry(
                certificateName,
                keyPair.private,
                passwordChars,
                arrayOf(certificate)
        )
        persistKeyStore()
    }

    @Throws(CertificateCreationException::class)
    private fun persistKeyStore() {
        try {
            val passwordChars = info.password.toCharArray()

            TODO("keyStore should be saved in config directory")
            val pathToKeyStore = Paths.get("keyStore.jks")

            val outputStream = Files.newOutputStream(pathToKeyStore)
            keyStore.store(outputStream, passwordChars)
        } catch (exc: IOException) {
            throw CertificateCreationException(exc.localizedMessage ?: "")
        }
    }
}