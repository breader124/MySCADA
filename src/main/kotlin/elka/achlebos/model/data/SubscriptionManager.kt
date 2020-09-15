package elka.achlebos.model.data

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger
import java.util.*

object SubscriptionManager {
    private val activeSubscriptions: MutableMap<UUID, UaSubscription> = mutableMapOf()
    private val activeMonitoredItems: MutableMap<UUID, UaMonitoredItem> = mutableMapOf()

    fun registerSubscription(uuid: UUID, item: UaSubscription) {
        activeSubscriptions[uuid] = item
    }

    fun registerMonitoredItem(uuid: UUID, item: UaMonitoredItem) {
        activeMonitoredItems[uuid] = item
    }

    fun getSubscriptionIdForUUID(uuid: UUID): UaSubscription? = activeSubscriptions[uuid]

    fun getMonitoredItemForUUID(uuid: UUID): UaMonitoredItem? = activeMonitoredItems[uuid]

    fun deleteSubscription(uuid: UUID) {
        activeSubscriptions.remove(uuid)
        activeMonitoredItems.remove(uuid)
    }
}