package ua.railian.architecture.mvi.embedded

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.with
import ua.railian.architecture.mvi.simple.AbstractMviModel as SimpleAbstractMviModel

public abstract class AbstractMviModel<STATE, INTENT, EFFECT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : SimpleAbstractMviModel<STATE, INTENT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    final override suspend fun PipelineScope.process(intent: INTENT) {
        with(logger with Category.Effect) {
            processIntent(intent, state).collect { effect ->
                log(Info) { "produce effect $effect" }
                state.update { reduxState(effect, it) }
            }
        }
    }

    protected abstract fun processIntent(
        intent: INTENT,
        state: StateFlow<STATE>,
    ): Flow<EFFECT>

    protected abstract fun reduxState(
        effect: EFFECT,
        currentState: STATE,
    ): STATE
}
