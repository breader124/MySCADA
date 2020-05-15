package elka.achlebos.model.certificate

import elka.achlebos.model.CertificateLoadingException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

@Tag("unitTest")
class X509CertificateLoaderTest {
    companion object {
        @AfterAll
        @JvmStatic
        internal fun cleanup() {
            Files.deleteIfExists(path)
        }
    }

    private val certificate = X509CertificateCreator(info, path).certificate
    private val loader = X509CertificateLoader(password, path)

    @Test
    fun loadCertificateTest() {
        // when
        val loadedCertificate = loader.certificate
        // then
        assertThat(certificate).isEqualTo(loadedCertificate)
    }

    @Test
    fun loadNonExistingCertificateTest() {
        // given
        val path = Paths.get("exampleWrongPath")
        // when
        assertThatThrownBy {
            X509CertificateLoader(password, path)
        }.isInstanceOf(CertificateLoadingException::class.java)
    }

    @Test
    fun loadCertificateWithWrongPassword() {
        // given
        val wrongPassword = "wrongPassword"
        // when
        assertThatThrownBy {
            X509CertificateLoader(wrongPassword, path)
        }.isInstanceOf(CertificateLoadingException::class.java)
    }

    @Test
    fun loadedPublicKeyIsEqualToGiven() {
        // when
        val loaded = loader.keyPair.public
        // then
        assertThat(certificate.publicKey).isEqualTo(loaded)
    }
}