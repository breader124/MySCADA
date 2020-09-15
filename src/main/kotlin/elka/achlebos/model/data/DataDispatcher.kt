package elka.achlebos.model.data

import java.util.*
import java.util.logging.Logger

object DataDispatcher : Observable() {
    private val log = Logger.getLogger(DataDispatcher::class.simpleName)

    private val queuesMap: MutableMap<Int, Queue<Any>> = mutableMapOf()
    private val observers: MutableList<Observer> = mutableListOf()
    private val changedQueues: MutableSet<Int> = mutableSetOf()

    val nextQueueNum: Int
        get() = queuesMap.size

    fun allocateNewQueue(): Int {
        val underlyingQueue = LinkedList<Any>()
        queuesMap[nextQueueNum] = underlyingQueue

        log.info("Allocated new queue with num: ${nextQueueNum - 1}")

        return nextQueueNum - 1
    }

    fun addDataToQueue(queueNum: Int, data: Any) {
        log.info("Data received for queue: $queueNum = $data")

        queuesMap[queueNum]?.add(data)

        changedQueues.add(queueNum)
        notifyObservers()
        changedQueues.remove(queueNum)
    }

    fun fetchDataFromQueue(queueNum: Int): List<Any> {
        val fetchedData = LinkedList<Any>()
        queuesMap[queueNum]?.also {
            it.forEach { _ -> fetchedData.add(it.poll()) }
        }
        return fetchedData
    }

    fun removeQueue(queueNum: Int) {
        queuesMap.remove(queueNum)
        log.info("Requested to remove queue with num: $queueNum")
    }

    override fun addObserver(o: Observer?) {
        o?.also {
            observers.add(it)
        }
    }

    override fun deleteObserver(o: Observer?) {
        o?.also {
            observers.remove(it)
        }
    }

    override fun notifyObservers() {
        observers.forEach {
            it.update(this, changedQueues)
        }
    }
}