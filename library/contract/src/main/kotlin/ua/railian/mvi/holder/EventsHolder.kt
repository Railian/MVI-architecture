package ua.railian.mvi.holder

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