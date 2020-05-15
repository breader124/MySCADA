package elka.achlebos.model.certificate

import java.nio.file.Paths
import java.time.Period

internal const val certName = "example"
internal const val password = "password"
internal val path = Paths.get(".", certName)

internal val info = X509CertificateInfo(
        commonName = "App",
        organization = "WUT",
        organizationalUnit = "EiTI",
        localityName = "Warsaw",
        countryCode = "PL",
        applicationUri = "app:app:app",
        validityPeriod = Period.ofDays(42),
        dnsNames = listOf("first", "second", "third"),
        ipAddresses = listOf("192.168.0.1", "224.123.123.123"),
        password = password
)
