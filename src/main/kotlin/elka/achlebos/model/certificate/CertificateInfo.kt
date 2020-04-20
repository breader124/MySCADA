package elka.achlebos.model.certificate

import java.time.Period

abstract class CertificateInfo

data class X509CertificateInfo(
        val commonName: String,
        val organization: String,
        val organizationalUnit: String,
        val localityName: String,
        val countryCode: String,
        val applicationUri: String,
        val validityPeriod: Period,
        val dnsNames: List<String>,
        val ipAddresses: List<String>
) : CertificateInfo()