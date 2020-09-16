package elka.achlebos.view

import elka.achlebos.model.certificate.X509CertificateInfo
import elka.achlebos.model.certificate.X509CertificateManager
import elka.achlebos.view.popup.CertificateCreationErrorDialog
import elka.achlebos.viewmodel.CertificateCreationViewModel
import elka.achlebos.viewmodel.CertificateInfoViewModel
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*
import java.io.IOException
import java.time.LocalDate

class CertificateCreationView : View("Certificate Creator") {
    private val certificateCreationModel: CertificateCreationViewModel by inject()
    private val infoModel: CertificateInfoViewModel by inject()

    private val newDomain = SimpleStringProperty()
    private val newIpAddress = SimpleStringProperty()

    private val ROW_HEIGHT = 30
    private val NUM_OF_ROWS = 6
    private val PREFERRED_LISTVIEW_HEIGHT = (NUM_OF_ROWS * ROW_HEIGHT).toDouble()

    init {
        certificateCreationModel.item = X509CertificateManager()
        infoModel.item = X509CertificateInfo()
        infoModel.pickedDate.value = LocalDate.now().plusMonths(1)
        infoModel.setPeriod()
    }

    override val root = scrollpane(fitToWidth = true) {
        form {
            fieldset("Information") {
                field("Common name") {
                    textfield(infoModel.commonName).required()
                }
                field("Organization") {
                    textfield(infoModel.organization).required()
                }
                field("Organizational unit") {
                    textfield(infoModel.organizationalUnit).required()
                }
                field("Locality name") {
                    textfield(infoModel.localityName).required()
                }
                field("Country code") {
                    textfield(infoModel.countryCode).required()
                }
                field("Application URI") {
                    textfield(infoModel.applicationUri).required()
                }
                field("Valid until") {
                    datepicker(infoModel.pickedDate) {
                        value = LocalDate.now().plusMonths(1)

                        setOnAction {
                            if (value != null) {
                                infoModel.setPeriod()
                            } else {
                                value = LocalDate.now().plusMonths(1)
                            }
                        }
                    }
                }
                vbox {
                    field("Domain names") {
                        listview(infoModel.dnsNames) {
                            prefHeight = PREFERRED_LISTVIEW_HEIGHT
                        }
                    }
                    hbox {
                        textfield(newDomain)
                        button("+").setOnAction {
                            infoModel.dnsNames.value.add(newDomain.value)
                            newDomain.value = ""
                        }
                        alignment = Pos.CENTER_RIGHT
                    }
                }
                vbox {
                    field("IP addresses") {
                        listview(infoModel.ipAddresses) {
                            prefHeight = PREFERRED_LISTVIEW_HEIGHT
                        }
                    }
                    hbox {
                        textfield(newIpAddress)
                        button("+").setOnAction {
                            infoModel.ipAddresses.value.add(newIpAddress.value)
                            newIpAddress.value = ""
                        }
                        alignment = Pos.CENTER_RIGHT
                    }
                }
                fieldset("Password") {
                    passwordfield(infoModel.password).required()
                }
            }
            buttonbar {
                button("Save") {
                    enableWhen(infoModel.valid)

                    action {
                        infoModel.commit()
                        val certInfo = infoModel.item
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

    override fun onUndock() {
        infoModel.commonName.value = ""
        infoModel.password.value = "" 
        infoModel.commonName.value = "" 
        infoModel.organization.value = "" 
        infoModel.organizationalUnit.value = "" 
        infoModel.localityName.value = "" 
        infoModel.countryCode.value = "" 
        infoModel.applicationUri.value = ""
        infoModel.pickedDate.value = LocalDate.now().plusMonths(1)
        infoModel.dnsNames.value = observableListOf()
        infoModel.ipAddresses.value = observableListOf()
    }
}

