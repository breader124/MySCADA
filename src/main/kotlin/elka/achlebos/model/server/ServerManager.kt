package elka.achlebos.model.server

import javafx.collections.ObservableList
import tornadofx.*

object ServerManager {
    val CONNECTED: ObservableList<Server> = observableListOf()

    fun addServer(server: Server): Boolean = CONNECTED.add(server)

    fun removeServer(server: Server): Boolean = CONNECTED.remove(server)
}