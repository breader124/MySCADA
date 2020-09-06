package elka.achlebos.view.fragment

import javafx.embed.swing.SwingNode
import javafx.scene.layout.VBox
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import tornadofx.*

class ChartFragment : Fragment() {

    private var chartPanel: ChartPanel
    private var chart: JFreeChart
    private val dataset = XYSeriesCollection()

    private lateinit var n: VBox

    init {
        val series = XYSeries("Received values")
        series.add(0, 0)
        series.add(1, 1)
        series.add(2, 2)
        series.add(3, 8)
        series.add(4, 5)

        dataset.addSeries(series)

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

    override val root = vbox {
        n = this

        button {
            action {
                addChart()
            }
        }
    }

    private fun addChart() {
        val swingNode = SwingNode()
        swingNode.content = chartPanel
        n.replaceChildren(swingNode)
    }
}
