package elka.achlebos.view.fragment

import elka.achlebos.model.data.DataDispatcher
import elka.achlebos.viewmodel.ChartFragmentViewModel
import javafx.embed.swing.SwingNode
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYSeriesCollection
import tornadofx.*
import java.util.*

class ChartFragment(dataQueueNum: Int) : Fragment() {
    private val viewModel = ChartFragmentViewModel(dataQueueNum)

    private var chartPanel: ChartPanel by singleAssign()
    private var chart: JFreeChart by singleAssign()
    private val dataset = XYSeriesCollection()

    override val root = borderpane()

    init {
        DataDispatcher.addObserver(viewModel)
        initializeChart()
        displayChart()
    }

    private fun initializeChart() {
        dataset.addSeries(viewModel.series)

        chart = ChartFactory.createXYLineChart(
                "Received values",
                "Timestamp",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        )
        chartPanel = ChartPanel(chart)
    }

    private fun displayChart() {
        val swingNode = SwingNode()
        swingNode.content = chartPanel
        root.center = swingNode
    }
}
