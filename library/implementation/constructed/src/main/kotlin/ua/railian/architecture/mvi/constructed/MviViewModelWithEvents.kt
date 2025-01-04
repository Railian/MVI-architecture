package ua.railian.architecture.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.embedded.MviViewModelWithEvents as EmbeddedMviViewModelWithEvents

public abstract class MviViewModelWithEvents<STATE, INTENT, RESULT, EVENT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : EmbeddedMviViewModelWithEvents<STATE, INTENT, RESULT, EVENT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, RESULT>
    protected abstract val stateReducer: MviStateReducer<RESULT, STATE>
    protected abstract val eventEmitter: MviEventEmitter<RESULT, STATE, EVENT>

    final override fun processIntent(
        intent: INTENT,
        state: StateFlow<STATE>,
    ): Flow<RESULT> {
        return intentProcessor.processIntent(
            intent = intent,
            state = state,
        )
    }

    final override fun reduxState(
        result: RESULT,
        currentState: STATE,
    ): STATE {
        return stateReducer.reduxState(
            result = result,
            currentState = currentState,
        )
    }

    final override fun emitEvent(
        result: RESULT,
        currentState: STATE,
    ): EVENT? {
        return eventEmitter.emitEvent(
            result = result,
            currentState = currentState,
        )
    }
}
