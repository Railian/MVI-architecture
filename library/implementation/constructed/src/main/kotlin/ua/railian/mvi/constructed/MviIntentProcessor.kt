package ua.railian.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public fun interface MviIntentProcessor<INTENT, STATE, RESULT> {
    public fun processIntent(intent: INTENT, state: StateFlow<STATE>): Flow<RESULT>
}
