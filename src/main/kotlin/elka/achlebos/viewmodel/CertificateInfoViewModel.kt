package elka.achlebos.viewmodel

import elka.achlebos.model.certificate.X509CertificateInfo
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.time.LocalDate
import java.time.Period

class CertificateInfoViewModel : ItemViewModel<X509CertificateInfo>() {
    val password = bind(X509CertificateInfo::passwordProperty)
    val commonName = bind(X509CertificateInfo::commonNameProperty)
    val organization = bind(X509CertificateInfo::organizationProperty)
    val organizationalUnit = bind(X509CertificateInfo::organizationalUnitProperty)
    val localityName = bind(X509CertificateInfo::localityNameProperty)
    val countryCode = bind(X509CertificateInfo::countryCodeProperty)
    val applicationUri = bind(X509CertificateInfo::applicationUriProperty)

    private val validityPeriod = bind(X509CertificateInfo::validityPeriodProperty)
    val pickedDate = SimpleObjectProperty<LocalDate>()

    private val dnsNamesProperty = bind(X509CertificateInfo::dnsNamesProperty)
    val dnsNames: List<String> by dnsNamesProperty

    private val ipAddressesProperty = bind(X509CertificateInfo::ipAddressesProperty)
    val ipAddresses: List<String> by ipAddressesProperty

    fun setPeriod() {
        val now = LocalDate.now()
        val picked = pickedDate.value
        validityPeriod.value = Period.between(now, picked)
    }

    fun storeInformationInPreferences() {
        preferences {
            putBoolean("isCertificateAlreadyExists", true)
            put("certificateName", item.commonName)
            put("applicationUri", item.applicationUri)

            val expirationDate = LocalDate.now().plus(item.validityPeriod)
            put("expirationDate", expirationDate.toString())
        }
    }
}