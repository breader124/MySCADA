package elka.achlebos.model.certificate

import elka.achlebos.model.CertificateCreationException
import elka.achlebos.model.CertificateLoadingException
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.cert.Certificate

abstract class CertificateManager {
    protected abstract val creator: AbstractCertificateCreator
    protected abstract val loader: AbstractCertificateLoader

    @Throws(CertificateCreationException::class)
    abstract fun create(certificateInfo: CertificateInfo, certName: String): Pair<Certificate, KeyPair>

    @Throws(CertificateLoadingException::class)
    abstract fun load(password: String, path: Path): Pair<Certificate, KeyPair>

    abstract fun remove(path: Path)
}

class X509CertificateManager : CertificateManager() {
    override lateinit var creator: X509CertificateCreator
    override lateinit var loader: X509CertificateLoader

    @Throws(CertificateCreationException::class)
    override fun create(certificateInfo: CertificateInfo, certName: String): Pair<Certificate, KeyPair> {
        val info = certificateInfo as X509CertificateInfo
        creator = X509CertificateCreator(info, certName)
        return Pair(creator.certificate, creator.keyPair)
    }

    @Throws(CertificateLoadingException::class)
    override fun load(password: String, path: Path): Pair<Certificate, KeyPair> {
        loader = X509CertificateLoader(password, path)
        return Pair(loader.certificate, loader.keyPair)
    }

    override fun remove(path: Path) {
        Files.deleteIfExists(path)
    }
}