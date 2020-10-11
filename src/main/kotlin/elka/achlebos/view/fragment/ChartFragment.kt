package elka.achlebos.view.fragment

import elka.achlebos.model.data.DataDispatcher
import elka.achlebos.viewmodel.ChartFragmentViewModel
import javafx.embed.swing.SwingNode
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeriesCollection
import tornadofx.*
import java.text.SimpleDateFormat
import java.util.*

class ChartFragment(dataQueueNum: UUID) : Fragment() {
    private val viewModel = ChartFragmentViewModel(dataQueueNum)

    private var chartPanel: ChartPanel by singleAssign()
    private var chart: JFreeChart by singleAssign()
    private val dataset = TimeSeriesCollection()

    override val root = borderpane()

    init {
        DataDispatcher.addObserver(viewModel)
        initializeChart()
        displayChart()
    }

    private fun initializeChart() {
        dataset.addSeries(viewModel.series)

        chart = ChartFactory.createTimeSeriesChart(
                "",
                "Timestamp",
                "Value",
                dataset,
                true,
                true,
                false
        )
        val xAxis = chart.xyPlot.domainAxis as DateAxis
        val format = "HH:mm:ss"
        xAxis.dateFormatOverride = SimpleDateFormat(format)
        chartPanel = ChartPanel(chart)
    }

    private fun displayChart() {
        val swingNode = SwingNode()
        swingNode.content = chartPanel
        root.center = swingNode
    }
}
