package elka.achlebos.model.certificate

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder
import java.security.KeyPair
import java.security.cert.Certificate
import java.security.cert.X509Certificate

internal abstract class AbstractUserCertificateBuilder(private val info: CertificateInfo) {
    abstract fun build(): Certificate
}

internal class UserX509CertificateBuilder(
        private val info: X509CertificateInfo,
        private val keyPair: KeyPair
) : AbstractUserCertificateBuilder(info) {
    override fun build(): X509Certificate {
        val builder = SelfSignedCertificateBuilder(keyPair)
                .setCommonName(info.commonName)
                .setOrganization(info.organization)
                .setOrganizationalUnit(info.organizationalUnit)
                .setLocalityName(info.localityName)
                .setCountryCode(info.countryCode)
                .setApplicationUri(info.applicationUri)
                .setValidityPeriod(info.validityPeriod)
        info.dnsNames.forEach { builder.addDnsName(it) }
        info.ipAddresses.forEach { builder.addIpAddress(it) }

        return builder.build()
    }
}