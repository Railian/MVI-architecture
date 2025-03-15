package ua.railian.mvi.holder

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface that exposes a stream of state updates.
 *
 * @param STATE The type of the state managed by the model.
 */
public interface StateHolder<STATE> {

    /**
     * A [StateFlow] that holds the current state of the model.
     */
    public val state: StateFlow<STATE>
}