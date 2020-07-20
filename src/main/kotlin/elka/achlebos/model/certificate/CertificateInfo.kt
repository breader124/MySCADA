package elka.achlebos.model.certificate

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.Period

abstract class CertificateInfo

class X509CertificateInfo(
        password: String? = null,
        commonName: String? = null,
        organization: String? = null,
        organizationalUnit: String? = null,
        localityName: String? = null,
        countryCode: String? = null,
        applicationUri: String? = null,
        validityPeriod: Period? = Period.ZERO,
        dnsNames: List<String>? = observableListOf(),
        ipAddresses: List<String>? = observableListOf()
) : CertificateInfo() {
    val passwordProperty = SimpleStringProperty(this, "password", password)
    var password: String by passwordProperty

    val commonNameProperty = SimpleStringProperty(this, "commonName", commonName)
    var commonName: String by commonNameProperty

    val organizationProperty = SimpleStringProperty(this, "organization", organization)
    var organization: String by organizationProperty

    val organizationalUnitProperty = SimpleStringProperty(this, "organizationalUnit", organizationalUnit)
    var organizationalUnit: String by organizationalUnitProperty

    val localityNameProperty = SimpleStringProperty(this, "localityName", localityName)
    var localityName: String by localityNameProperty

    val countryCodeProperty = SimpleStringProperty(this, "countryCode", countryCode)
    var countryCode: String by countryCodeProperty

    val applicationUriProperty = SimpleStringProperty(this, "applicationUri", applicationUri)
    var applicationUri: String by applicationUriProperty

    val validityPeriodProperty = SimpleObjectProperty<Period>(this, "validityPeriod", validityPeriod)
    var validityPeriod: Period by validityPeriodProperty

    val dnsNamesProperty = SimpleObjectProperty<List<String>>(this, "dnsNames", dnsNames)
    var dnsNames: List<String> by dnsNamesProperty

    val ipAddressesProperty = SimpleObjectProperty<List<String>>(this, "ipAddresses", ipAddresses)
    var ipAddresses: List<String> by ipAddressesProperty
}