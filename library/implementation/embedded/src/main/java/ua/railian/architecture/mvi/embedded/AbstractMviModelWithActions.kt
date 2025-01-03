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
import ua.railian.architecture.mvi.simple.AbstractMviModelWithActions as SimpleAbstractMviModelWithActions

public abstract class AbstractMviModelWithActions<STATE, INTENT, RESULT, ACTION>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : SimpleAbstractMviModelWithActions<STATE, INTENT, ACTION>(
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
                emitAction(result, state.value)
                    ?.let { action -> actions.send(action) }
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

    protected abstract fun emitAction(
        result: RESULT,
        currentState: STATE,
    ): ACTION?
}
