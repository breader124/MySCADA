package elka.achlebos.model.certificate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.security.cert.X509Certificate

@Tag("unitTest")
class X509CertificateCreatorTest {
    companion object {
        @AfterAll
        @JvmStatic
        internal fun cleanup() {
            val certPath = Paths.get("./$CERT_NAME")
            Files.deleteIfExists(certPath)
        }
    }

    private val creator = X509CertificateCreator(info, password, path)

    @Test
    fun createCertificateTest() {
        // when
        val certificate: X509Certificate = creator.certificate
        // then
        val certPath = Paths.get(path.toString(), CERT_NAME)
        assertThat(Files.exists(certPath)).isTrue()
    }

    @Test
    fun checkCreatedPublicKey() {
        // when
        val certificate = creator.certificate
        // then
        val certificatePublicKey = certificate.publicKey
        val savedPublicKey = creator.keyPair.public
        assertThat(certificatePublicKey).isEqualTo(savedPublicKey)
    }
}