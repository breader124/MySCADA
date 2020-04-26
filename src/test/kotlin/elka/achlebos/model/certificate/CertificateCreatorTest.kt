package elka.achlebos.model.certificate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.security.cert.X509Certificate

@Tag("unitTest")
class X509AbstractCertificateCreatorTest {
    companion object {
        @AfterAll
        @JvmStatic
        internal fun cleanup() {
            Files.deleteIfExists(path)
        }
    }

    private val creator = X509AbstractCertificateCreator(info, path, password)

    @Test
    fun createCertificateTest() {
        // when
        val certificate: X509Certificate = creator.certificate
        // then
        assertThat(Files.exists(path)).isTrue()
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