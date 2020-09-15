package elka.achlebos.model.data

import java.util.*
import java.util.logging.Logger

object DataDispatcher : Observable() {
    private val log = Logger.getLogger(DataDispatcher::class.simpleName)

    private val queuesMap: MutableMap<UUID, Queue<Any>> = mutableMapOf()
    private val observers: MutableList<Observer> = mutableListOf()
    private val changedQueues: MutableSet<UUID> = mutableSetOf()

    fun allocateNewQueue(queueNum: UUID) {
        val underlyingQueue = LinkedList<Any>()
        queuesMap[queueNum] = underlyingQueue

        log.info("Allocated new queue with num: $queueNum")
    }

    fun addDataToQueue(queueNum: UUID, data: Any) {
        log.info("Data received for queue: $queueNum = $data")

        queuesMap[queueNum]?.add(data)

        changedQueues.add(queueNum)
        notifyObservers()
        changedQueues.remove(queueNum)
    }

    fun fetchDataFromQueue(queueNum: UUID): List<Any> {
        val fetchedData = LinkedList<Any>()
        queuesMap[queueNum]?.also {
            it.forEach { _ -> fetchedData.add(it.poll()) }
        }
        return fetchedData
    }

    fun removeQueue(queueNum: UUID) {
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