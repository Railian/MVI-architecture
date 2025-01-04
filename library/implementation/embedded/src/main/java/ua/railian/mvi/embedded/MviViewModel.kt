package ua.railian.mvi.embedded

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import ua.railian.mvi.config.GlobalMviConfig
import ua.railian.mvi.config.SharedMviConfig
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.Priority.Info
import ua.railian.mvi.log.with
import ua.railian.mvi.simple.MviViewModel as SimpleMviViewModel

public abstract class MviViewModel<STATE, INTENT, RESULT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : SimpleMviViewModel<STATE, INTENT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    final override suspend fun PipelineScope.process(intent: INTENT) {
        with(logger with Category.Result) {
            processIntent(intent, state).collect { result ->
                log(Info) { "produce result $result" }
                state.update { reduxState(result, it) }
            }
        }
    }

    protected abstract fun processIntent(
        intent: INTENT,
        state: StateFlow<STATE>,
    ): Flow<RESULT>

    protected abstract fun reduxState(
        result: RESULT,
        currentState: STATE,
    ): STATE
}
