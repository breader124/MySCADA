package elka.achlebos.view

import elka.achlebos.model.certificate.X509CertificateInfo
import elka.achlebos.model.certificate.X509CertificateManager
import elka.achlebos.viewmodel.CertificateInfoViewModel
import elka.achlebos.viewmodel.CertificateManagerViewModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.GroupLayout

class CertificateCreatedEvent : FXEvent()

class CertificateManagerView : View("Certificate Manager") {
    private val viewModel: CertificateManagerViewModel by inject()

    private val certificates = mutableListOf<Path>().asObservable()
    private val listSelected = SimpleObjectProperty<Path>()

    init {
        viewModel.item = X509CertificateManager()
        refreshExistingCertificates()
    }

    override val root = borderpane {
        center = listview(certificates) {
            bindSelected(listSelected)
        }

        bottom = buttonbar {
            button("Load").setOnAction {
                TODO("Loading certificate")
            }
            button("Create").setOnAction {
                openInternalWindow(CertificateCreationFragment())
            }
            button("Remove").setOnAction {
                viewModel.removeCertificate(listSelected.value)
                refreshExistingCertificates()
            }
        }

        subscribe<CertificateCreatedEvent> {
            refreshExistingCertificates()
        }
    }

    private fun refreshExistingCertificates() {
        val here = Paths.get(".")
        val filesHere = viewModel.listCertificates(here)

        certificates.removeIf { elem -> !filesHere.contains(elem) }
        filesHere.forEach {
            if (!certificates.contains(it)) {
                certificates.add(it)
            }
        }
    }
}

class CertificateCreationFragment : Fragment("Certificate Creator") {
    private val certificateManagerModel: CertificateManagerViewModel by inject()
    private val infoModel: CertificateInfoViewModel by inject()

    private val domainNames = infoModel.dnsNames.asObservable()
    private val newDomain = SimpleStringProperty()

    private val ipAddresses = infoModel.ipAddresses.asObservable()
    private val newIpAddress = SimpleStringProperty()

    init {
        infoModel.item = X509CertificateInfo()
    }

    override val root = scrollpane(fitToWidth = true, fitToHeight = true) {
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
                field("Validity time") {
                    datepicker(infoModel.pickedDate).setOnAction {
                        infoModel.setPeriod()
                    }
                }
                vbox {
                    field("Domain names") {
                        listview(domainNames)
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
                        listview(ipAddresses)
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
                    runAsync {
                        infoModel.commit()
                        val certInfo = infoModel.item

//                        TODO("Certificate should have its own name")
                        val here = Paths.get(".", "cert")

                        certificateManagerModel.createCertificate(certInfo, here)
                    } ui {
                        fire(CertificateCreatedEvent())
                    }

                    close()
                }
            }
        }
    }
}

