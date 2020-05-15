package elka.achlebos.viewmodel

import elka.achlebos.model.certificate.X509CertificateInfo
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.time.LocalDate
import java.time.Period

class CertificateInfoViewModel : ItemViewModel<X509CertificateInfo>() {
    val password = bind(X509CertificateInfo::password)
    val commonName = bind(X509CertificateInfo::commonName)
    val organization = bind(X509CertificateInfo::organization)
    val organizationalUnit = bind(X509CertificateInfo::organizationalUnit)
    val localityName = bind(X509CertificateInfo::localityName)
    val countryCode = bind(X509CertificateInfo::countryCode)
    val applicationUri = bind(X509CertificateInfo::applicationUri)

    private val validityPeriod = bind(X509CertificateInfo::validityPeriod)
    val pickedDate = SimpleObjectProperty<LocalDate>()

    private val dnsNamesProperty = bind(X509CertificateInfo::dnsNames)
    val dnsNames: List<String> by dnsNamesProperty

    private val ipAddressesProperty = bind(X509CertificateInfo::ipAddresses)
    val ipAddresses: List<String> by ipAddressesProperty

    fun setPeriod() {
        val now = LocalDate.now()
        val picked = pickedDate.value
        validityPeriod.value = Period.between(now, picked)
    }
}