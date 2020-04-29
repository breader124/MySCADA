package elka.achlebos.model.client

object ClientsManager {
    val connected: MutableList<Client> = mutableListOf()

    fun addClient(client: Client): Boolean = connected.add(client)

    fun removeClient(client: Client): Boolean = connected.remove(client)
}