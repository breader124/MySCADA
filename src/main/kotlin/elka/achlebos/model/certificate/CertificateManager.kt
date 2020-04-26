package elka.achlebos.model.certificate

import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.cert.Certificate

abstract class CertificateManager(protected val password: String) {
    protected abstract val creatorAbstract: AbstractCertificateCreator
    protected abstract val loaderAbstract: AbstractCertificateLoader

    abstract fun create(certificateInfo: CertificateInfo, path: Path): Pair<Certificate, KeyPair>
    abstract fun load(path: Path): Pair<Certificate, KeyPair>
    abstract fun remove(path: Path)
}

class X509CertificateManager(password: String): CertificateManager(password) {
    override lateinit var creatorAbstract: X509AbstractCertificateCreator
    override lateinit var loaderAbstract: X509AbstractCertificateLoader

    @Throws(CertificateCreationException::class)
    override fun create(certificateInfo: CertificateInfo, path: Path): Pair<Certificate, KeyPair> {
        val info = certificateInfo as X509CertificateInfo
        creatorAbstract = X509AbstractCertificateCreator(info, path, password)
        return Pair(creatorAbstract.certificate, creatorAbstract.keyPair)
    }

    @Throws(CertificateLoadingException::class)
    override fun load(path: Path): Pair<Certificate, KeyPair> {
        loaderAbstract = X509AbstractCertificateLoader(path, password)
        return Pair(loaderAbstract.certificate, loaderAbstract.keyPair)
    }

    override fun remove(path: Path) {
        Files.deleteIfExists(path)
    }
}