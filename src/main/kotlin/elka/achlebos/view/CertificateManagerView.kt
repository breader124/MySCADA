package elka.achlebos.view

import elka.achlebos.viewmodel.CertificateManagerViewModel
import tornadofx.*
import java.nio.file.Paths

class CertificateManagerView : View("Certificate Manager") {
    val viewModel: CertificateManagerViewModel by inject()

    override val root = borderpane {
        center = listview(viewModel.listCertificates(Paths.get(".")))
        bottom = hbox {
            button("Load").setOnAction {
                print("Loaded")
            }
            button("Create").setOnAction {
                openInternalWindow(CertificateCreationFragment())
            }
            button("Remove").setOnAction {
                print("Removed")
            }
        }
    }
}

class CertificateCreationFragment : Fragment("Certificate Creator") {
    override val root = form {
        field {  }

        button("Save").setOnAction {
            print("Saved")
            close()
        }
    }
}

//val commonName: String,
//val organization: String,
//val organizationalUnit: String,
//val localityName: String,
//val countryCode: String,
//val applicationUri: String,
//val validityPeriod: Period,
//val dnsNames: List<String>,
//val ipAddresses: List<String>