package elka.achlebos.model.certificate

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.security.cert.X509Certificate
import java.time.Period

@Tag("unitTest")
class X509CertificateCreatorTest {
    private val info = X509CertificateInfo(
            commonName = "App",
            organization = "WUT",
            organizationalUnit = "EiTI",
            localityName = "Warsaw",
            countryCode = "PL",
            applicationUri = "app:app:app",
            validityPeriod = Period.ofDays(42),
            dnsNames = listOf("first", "second", "third"),
            ipAddresses = listOf("192.168.0.1", "224.123.123.123")
    )
    private val path = Paths.get(".")
    private val password = "password"
    private val creator = X509CertificateCreator(info, password, path)

    @Test
    fun createCertificateTest() {
        // when
        val certificate: X509Certificate = creator.create()
        // then
        val certPath = Paths.get(path.toString(), CERT_NAME)
        Assertions.assertThat(Files.exists(certPath)).isTrue()
    }

    companion object {
        @AfterAll
        @JvmStatic
        internal fun cleanup() {
            val certPath = Paths.get("./$CERT_NAME")
            Files.deleteIfExists(certPath)
        }
    }
}