package ua.railian.architecture.mvi.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class MviInitializer<INTENT>(
    private val viewModelScope: CoroutineScope,
    private val initialIntents: Flow<INTENT>,
    private val intentProcessor: MviProcessor<INTENT>,
) {
    private var initialized = false
    private val mutex = Mutex()

    internal fun initAsync(): Job {
        if (initialized) return completedJob
        return viewModelScope.launch {
            mutex.withLock {
                if (initialized) return@launch
                initialIntents.collect { intent ->
                    intentProcessor.process(intent, initial = true)
                }
                initialized = true
            }
        }
    }
}
