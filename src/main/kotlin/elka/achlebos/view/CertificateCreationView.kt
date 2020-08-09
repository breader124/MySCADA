package elka.achlebos.view

import elka.achlebos.model.certificate.X509CertificateInfo
import elka.achlebos.model.certificate.X509CertificateManager
import elka.achlebos.view.popups.CertificateCreationErrorDialog
import elka.achlebos.viewmodel.CertificateCreationViewModel
import elka.achlebos.viewmodel.CertificateInfoViewModel
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*
import java.io.IOException

class CertificateCreationView : View("Certificate Creator") {
    private val certificateCreationModel: CertificateCreationViewModel by inject()
    private val infoModel: CertificateInfoViewModel by inject()

    private val domainNames = infoModel.dnsNames.asObservable()
    private val newDomain = SimpleStringProperty()

    private val ipAddresses = infoModel.ipAddresses.asObservable()
    private val newIpAddress = SimpleStringProperty()

    private val ROW_HEIGHT = 30
    private val NUM_OF_ROWS = 6
    private val PREFERRED_LISTVIEW_HEIGHT = (NUM_OF_ROWS * ROW_HEIGHT).toDouble()

    init {
        certificateCreationModel.item = X509CertificateManager()
        infoModel.item = X509CertificateInfo()
    }

    override val root = scrollpane(fitToWidth = true) {
        form {
            fieldset("Information") {
                field("Common name") {
                    textfield(infoModel.commonName)
                }
                field("Organization") {
                    textfield(infoModel.organization)
                }
                field("Organizational unit") {
                    textfield(infoModel.organizationalUnit)
                }
                field("Locality name") {
                    textfield(infoModel.localityName)
                }
                field("Country code") {
                    textfield(infoModel.countryCode)
                }
                field("Application URI") {
                    textfield(infoModel.applicationUri)
                }
                field("Valid until") {
                    datepicker(infoModel.pickedDate).setOnAction {
                        infoModel.setPeriod()
                    }
                }
                vbox {
                    field("Domain names") {
                        listview(domainNames) {
                            prefHeight = PREFERRED_LISTVIEW_HEIGHT
                        }
                    }
                    hbox {
                        textfield(newDomain)
                        button("+").setOnAction {
                            domainNames.add(newDomain.value)
                        }

                        style {
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                }
                vbox {
                    field("IP addresses") {
                        listview(ipAddresses) {
                            prefHeight = PREFERRED_LISTVIEW_HEIGHT
                        }
                    }
                    hbox {
                        textfield(newIpAddress)
                        button("+").setOnAction {
                            ipAddresses.add(newIpAddress.value)
                        }

                        style {
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                }
                fieldset("Password") {
                    passwordfield(infoModel.password)
                }
            }
            buttonbar {
                button("Save").setOnAction {
                    infoModel.commit()

                    val certInfo = infoModel.item
                    certInfo.dnsNames = domainNames
                    certInfo.ipAddresses = ipAddresses

                    try {
                        certificateCreationModel.createCertificate(certInfo, certInfo.commonName)
                        infoModel.storeInformationInPreferences()
                        close()
                    } catch (exc: IOException) {
                        find<CertificateCreationErrorDialog>().openWindow()
                    }
                }
            }
        }
    }
}

