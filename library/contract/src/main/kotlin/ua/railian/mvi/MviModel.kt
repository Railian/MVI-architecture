package ua.railian.mvi

import kotlinx.coroutines.Job
import ua.railian.mvi.holder.StateHolder

/**
 * Interface for a Model in the MVI (Model-View-Intent) architecture.
 *
 * @param STATE The type representing the state of the model.
 * @param INTENT The type representing the intents that modify the state.
 */
public interface MviModel<STATE, INTENT> : StateHolder<STATE> {

    /**
     * Process an intent asynchronously.
     *
     * @param intent The intent to be processed.
     * @return A [Job] representing the ongoing processing.
     */
    public fun processAsync(intent: INTENT): Job
}
