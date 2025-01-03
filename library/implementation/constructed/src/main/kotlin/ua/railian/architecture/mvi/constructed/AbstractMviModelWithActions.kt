package ua.railian.architecture.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.embedded.AbstractMviModelWithActions as AbstractMviModelWithActions

public abstract class AbstractMviModelWithActions<STATE, INTENT, RESULT, ACTION>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : AbstractMviModelWithActions<STATE, INTENT, RESULT, ACTION>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, RESULT>
    protected abstract val stateReducer: MviStateReducer<RESULT, STATE>
    protected abstract val actionEmitter: MviActionEmitter<RESULT, STATE, ACTION>

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

    final override fun emitAction(
        result: RESULT,
        currentState: STATE,
    ): ACTION? {
        return actionEmitter.emitAction(
            result = result,
            currentState = currentState,
        )
    }
}
