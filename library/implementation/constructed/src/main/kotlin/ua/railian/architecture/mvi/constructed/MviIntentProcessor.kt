package ua.railian.architecture.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public fun interface MviIntentProcessor<INTENT, STATE, EFFECT> {
    public fun processIntent(intent: INTENT, state: StateFlow<STATE>): Flow<EFFECT>
}
