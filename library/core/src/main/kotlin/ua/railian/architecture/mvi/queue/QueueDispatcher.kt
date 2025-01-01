package ua.railian.architecture.mvi.queue

public interface QueueDispatcher<QUEUE> {
    public suspend fun enqueue(queue: QUEUE, block: suspend () -> Unit)
    public suspend fun restart(queue: QUEUE, block: suspend () -> Unit)
    public fun clear(queue: QUEUE)
}

public fun <QUEUE> QueueDispatcher(): QueueDispatcher<QUEUE> {
    return QueueDispatcherImpl()
}

private class QueueDispatcherImpl<QUEUE> : QueueDispatcher<QUEUE> {

    private val queueMap = mutableMapOf<QUEUE, Queue>()

    override suspend fun enqueue(queue: QUEUE, block: suspend () -> Unit) {
        synchronized(queueMap) {
            queueMap.getOrPut(queue, ::Queue)
        }.enqueue(block)
    }

    override suspend fun restart(queue: QUEUE, block: suspend () -> Unit) {
        synchronized(queueMap) {
            queueMap.getOrPut(queue, ::Queue)
        }.restart(block)
    }

    override fun clear(queue: QUEUE) {
        synchronized(queueMap) {
            queueMap[queue]?.clear()
        }
    }
}
