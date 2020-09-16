package elka.achlebos.view

import elka.achlebos.model.SubscriptionCreatedEvent
import elka.achlebos.model.SubscriptionRemoveRequestEvent
import elka.achlebos.view.fragment.AddressSpaceFragment
import elka.achlebos.view.fragment.ChartFragment
import elka.achlebos.viewmodel.MainViewModel
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.StageStyle
import tornadofx.*
import java.util.*


class MainView : View("MySCADA") {
    private val viewModel: MainViewModel by inject()
    private var chartTabPane: TabPane = tabpane()

    init {
        subscribe<SubscriptionCreatedEvent> {
            addNewChartToTabPane(it.queueNum, it.componentName)
        }
    }

    override val root = borderpane {
        top = menubar {
            menu("Connection") {
                item("New").action {
                    if (viewModel.checkIfNeedToCreateNewCert()) {
                        openCertificateCreationWindow()
                    }
                    find<ConnectionCreationView>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }

            menu("Certificate") {
                item("Create").action {
                    openCloseableCertificateCreationWindow()
                }

                item("Remove").action {
                    viewModel.switchCertificateAlreadyExistsToFalse()
                    openCertificateCreationWindow()
                }

                item("Export to binary form").action {
                    viewModel.exportCertificateToBinaryFormat()
                }
            }
        }

        left<AddressSpaceFragment>()

        center = chartTabPane
    }

    private fun openCertificateCreationWindow() {
        find<CertificateCreationView>().openModal(
                stageStyle = StageStyle.UNDECORATED,
                escapeClosesWindow = false,
                block = true
        )
    }

    private fun openCloseableCertificateCreationWindow() {
        find<CertificateCreationView>().openModal(
                stageStyle = StageStyle.UNDECORATED,
                escapeClosesWindow = true
        )
    }

    private fun addNewChartToTabPane(dataQueueNum: UUID, tabName: String) {
        val chartTabFragment = ChartFragment(dataQueueNum)
        var chartTab = Tab()
        tabpane {
            chartTab = tab(chartTabFragment) {
                text = tabName

                setOnCloseRequest {
                    fire(SubscriptionRemoveRequestEvent(dataQueueNum))
                }
            }
        }
        chartTabPane.tabs.add(chartTab)
        chartTab.select()
    }
}
