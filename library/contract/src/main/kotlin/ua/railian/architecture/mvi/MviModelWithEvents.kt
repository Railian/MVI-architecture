package ua.railian.architecture.mvi

import kotlinx.coroutines.flow.Flow

/**
 * Interface that exposes a stream of events through [Flow].
 *
 * @param EVENT The type of events emitted by the model.
 */
public interface EventsHolder<EVENT> {

    /**
     * A [Flow] that emits events.
     */
    public val events: Flow<EVENT>
}

/**
 * An extended interface of [MviModel] for a Model in the MVI
 * (Model-View-Intent) architecture that includes events.
 *
 * @param STATE The type representing the state of the model.
 * @param INTENT The type representing the intents that modify the state and can emits events.
 * @param EVENT The type representing events emitted by the model.
 */
public interface MviModelWithEvents<STATE, INTENT, EVENT> :
    MviModel<STATE, INTENT>,
    EventsHolder<EVENT>
