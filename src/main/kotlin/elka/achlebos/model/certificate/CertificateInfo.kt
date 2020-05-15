package elka.achlebos.model.certificate

import tornadofx.*
import java.time.Period

abstract class CertificateInfo

data class X509CertificateInfo(
        val password: String = "",
        val commonName: String = "",
        val organization: String = "",
        val organizationalUnit: String = "",
        val localityName: String = "",
        val countryCode: String = "",
        val applicationUri: String = "",
        val validityPeriod: Period = Period.ZERO,
        val dnsNames: List<String> = observableListOf(),
        val ipAddresses: List<String> = observableListOf()
) : CertificateInfo()