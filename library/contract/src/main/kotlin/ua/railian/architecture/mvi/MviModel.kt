package ua.railian.architecture.mvi

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for a Model in the MVI (Model-View-Intent) architecture.
 *
 * @param STATE The type representing the state of the model.
 * @param INTENT The type representing the intents that modify the state.
 */
public interface MviModel<STATE, INTENT> {

    /**
     * A [StateFlow] that holds the current state of the model.
     */
    public val state: StateFlow<STATE>

    /**
     * Handle an intent asynchronously.
     *
     * @param intent The intent to be handled.
     * @return A [Job] representing the ongoing handling.
     */
    public fun handleAsync(intent: INTENT): Job
}
