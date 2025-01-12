package ua.railian.mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Initializes the Model-View-Intent (MVI) pattern for a ViewModel.
 *
 * This class is responsible for collecting and processing initial intents and ensuring
 * that initialization happens only once.
 *
 * @param INTENT The type of intents to process.
 * @property viewModelScope The [CoroutineScope] in which the initialization and intent processing will run.
 * @property initialIntents A [Flow] of initial intents that need to be processed during initialization.
 * @property intentProcessor The processor responsible for handling the incoming intents.
 */
internal class MviInitializer<INTENT>(
    private val viewModelScope: CoroutineScope,
    private val initialIntents: Flow<INTENT>,
    private val intentProcessor: MviProcessor<INTENT>,
) {
    private var initialized = false
    private val mutex = Mutex()

    /**
     * Asynchronously initializes the MVI system by collecting and processing the initial intents.
     *
     * Ensures that initialization happens only once, even if called multiple times,
     * using a thread-safe [Mutex].
     *
     * @return A [Job] representing the initialization task.
     */
    internal fun initAsync(): Job {
        if (initialized) return Job().apply { complete() }
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
