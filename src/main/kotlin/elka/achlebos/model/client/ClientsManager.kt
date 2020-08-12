package elka.achlebos.model.client

import javafx.collections.ObservableList
import tornadofx.*

object ClientsManager {
    val connected: ObservableList<Client> = observableListOf()

    fun addClient(client: Client): Boolean = connected.add(client)

    fun removeClient(client: Client): Boolean = connected.remove(client)
}