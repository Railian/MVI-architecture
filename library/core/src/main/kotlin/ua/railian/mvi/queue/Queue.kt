package ua.railian.mvi.queue

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

public interface Queue {
    public suspend fun enqueue(block: suspend () -> Unit)
    public suspend fun restart(block: suspend () -> Unit)
    public fun clear()
}

public fun Queue(): Queue {
    return QueueImpl()
}

private class QueueImpl : Queue {

    private val semaphore = Semaphore(permits = 1)
    private val jobs = mutableListOf<Job>()

    override suspend fun enqueue(block: suspend () -> Unit) {
        coroutineScope {
            val job = launch {
                semaphore.withPermit { block() }
            }
            synchronized(jobs) { jobs += job }
            job.invokeOnCompletion {
                synchronized(jobs) { jobs -= job }
            }
        }
    }

    override suspend fun restart(block: suspend () -> Unit) {
        clear()
        enqueue(block)
    }

    override fun clear() {
        synchronized(jobs) { jobs.forEach(Job::cancel) }
    }
}
