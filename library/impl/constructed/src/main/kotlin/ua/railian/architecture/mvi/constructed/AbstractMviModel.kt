package ua.railian.architecture.mvi.constructed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.embedded.AbstractMviModel as EmbeddedAbstractMviModel

public abstract class AbstractMviModel<STATE, INTENT, EFFECT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : EmbeddedAbstractMviModel<STATE, INTENT, EFFECT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, EFFECT>
    protected abstract val stateReducer: MviStateReducer<EFFECT, STATE>

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
}
