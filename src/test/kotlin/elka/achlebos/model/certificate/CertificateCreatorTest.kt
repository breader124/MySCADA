package elka.achlebos.model.certificate

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.Period

@Tag("unitTest")
class X509CertificateCreatorTest {
    private val info = X509CertificateInfo(
            commonName = "App",
            organization = "WUT",
            organizationalUnit = "EiTI",
            localityName = "lName",
            countryCode = "PL",
            applicationUri = "scada:app:asd",
            validityPeriod = Period.ofDays(42),
            dnsNames = listOf("first", "second", "third"),
            ipAddresses = listOf("192.168.0.1", "224.123.123.123")
    )

    @Test
    fun createCertificateTest() {

    }
}