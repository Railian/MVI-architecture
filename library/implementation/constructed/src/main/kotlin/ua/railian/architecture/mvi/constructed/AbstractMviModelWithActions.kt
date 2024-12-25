package ua.railian.architecture.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.embedded.AbstractMviModelWithActions as AbstractMviModelWithActions

public abstract class AbstractMviModelWithActions<STATE, INTENT, EFFECT, ACTION>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : AbstractMviModelWithActions<STATE, INTENT, EFFECT, ACTION>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, EFFECT>
    protected abstract val stateReducer: MviStateReducer<EFFECT, STATE>
    protected abstract val actionEmitter: MviActionEmitter<EFFECT, STATE, ACTION>

    final override suspend fun processIntent(
        intent: INTENT,
        state: StateFlow<STATE>,
    ): Flow<EFFECT> {
        return intentProcessor.processIntent(intent, state)
    }

    final override fun reduxState(
        effect: EFFECT,
        currentState: STATE,
    ): STATE {
        return stateReducer.reduxState(effect, currentState)
    }

    final override fun emitAction(
        effect: EFFECT,
        currentState: STATE,
    ): ACTION? {
        return actionEmitter.emitAction(effect, currentState)
    }
}
