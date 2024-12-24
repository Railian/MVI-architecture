package ua.railian.architecture.mvi

import kotlinx.coroutines.flow.Flow

/**
 * Interface that exposes a stream of actions through [Flow].
 *
 * @param ACTION The type of actions emitted by the model.
 */
public interface ActionsHolder<ACTION> {

    /**
     * A [Flow] that emits actions.
     */
    public val actions: Flow<ACTION>
}

/**
 * An extended interface of [MviModel] for a Model in the MVI
 * (Model-View-Intent) architecture that includes actions.
 *
 * @param STATE The type representing the state of the model.
 * @param INTENT The type representing the intents that modify the state and can emits actions.
 * @param ACTION The type representing actions emitted by the model.
 */
public interface MviModelWithActions<STATE, INTENT, ACTION> :
    MviModel<STATE, INTENT>,
    ActionsHolder<ACTION>
