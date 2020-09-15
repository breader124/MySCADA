package elka.achlebos.viewmodel

import elka.achlebos.model.data.DataDispatcher
import org.jfree.data.xy.XYSeries
import tornadofx.*
import java.util.*

class ChartFragmentViewModel(private val dataQueueNum: UUID) : ViewModel(), Observer {
    val series = XYSeries("Received values")
    var lastIndex: Int = 0

    init {
        updateChartSeries()
    }

    override fun update(o: Observable?, arg: Any?) {
        val changedQueueSet = arg as Set<UUID>
        if (changedQueueSet.contains(dataQueueNum)) {
            updateChartSeries()
        }
    }

    private fun updateChartSeries() {
        val data = DataDispatcher.fetchDataFromQueue(dataQueueNum) as List<Number>
        for (number in data) {
            series.add(lastIndex++, number)
        }
    }
}
