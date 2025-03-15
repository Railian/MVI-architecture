package ua.railian.mvi

import ua.railian.mvi.holder.EventsHolder

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
