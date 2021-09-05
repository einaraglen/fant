package no.ntnu.fant_app.activities

interface EventListener {
    fun update(eventType: String?)
}

class EventManager(vararg operations: String) {
    private var listeners = hashMapOf<String, ArrayList<EventListener>>()

    init {
        for (operation in operations) {
            listeners[operation] = ArrayList<EventListener>()
        }
    }

    fun subscribe(eventType: String, listener: EventListener) {
        val users = listeners.get(eventType)
        users?.add(listener)
    }

    fun unsubscribe(eventType: String, listener: EventListener) {
        val users = listeners.get(eventType)
        users?.remove(listener)
    }

    fun notify(eventType: String) {
        val users = listeners.get(eventType)
        users?.let {
            for (listener in it) {
                listener.update(eventType)
            }
        }
    }
}