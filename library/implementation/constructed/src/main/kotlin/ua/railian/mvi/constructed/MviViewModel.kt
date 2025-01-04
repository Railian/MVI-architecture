package ua.railian.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.mvi.config.GlobalMviConfig
import ua.railian.mvi.config.SharedMviConfig
import ua.railian.mvi.embedded.MviViewModel as EmbeddedMviViewModel

public abstract class MviViewModel<STATE, INTENT, RESULT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : EmbeddedMviViewModel<STATE, INTENT, RESULT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, RESULT>
    protected abstract val stateReducer: MviStateReducer<RESULT, STATE>

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
}
