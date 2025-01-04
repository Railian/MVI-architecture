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
import ua.railian.mvi.simple.MviViewModelWithEvents as SimpleMviViewModelWithEvents

public abstract class MviViewModelWithEvents<STATE, INTENT, RESULT, EVENT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : SimpleMviViewModelWithEvents<STATE, INTENT, EVENT>(
    initialState = initialState,
    initialIntents = initialIntents,
    sharedConfig = sharedConfig,
    settings = settings,
) {
    final override suspend fun PipelineScope.process(intent: INTENT) {
        with(logger with Category.Result) {
            processIntent(intent, state).collect { result ->
                log(Info) { "produce result: $result" }
                state.update { reduxState(result, it) }
                emitEvent(result, state.value)
                    ?.let { event -> events.send(event) }
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

    protected abstract fun emitEvent(
        result: RESULT,
        currentState: STATE,
    ): EVENT?
}
