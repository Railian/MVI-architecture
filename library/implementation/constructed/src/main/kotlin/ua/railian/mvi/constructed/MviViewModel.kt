package ua.railian.mvi.constructed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import ua.railian.mvi.MviModel
import ua.railian.mvi.config.BaseMviConfig
import ua.railian.mvi.config.BaseMviConfigEditor
import ua.railian.mvi.config.GlobalMviConfig
import ua.railian.mvi.config.SharedMviConfig
import ua.railian.mvi.core.mviCore
import ua.railian.mvi.flow.MviMutableStateFlow
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.MviPipelineLogger
import ua.railian.mvi.log.Priority.Info

public abstract class MviViewModel<STATE, INTENT, RESULT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModel<STATE, INTENT>, ViewModel() {

    protected val config: MviConfig =
        MviConfigEditor(sharedConfig).apply(settings)

    private val core by mviCore(
        config = config,
        initialIntents = initialIntents,
        viewModelScope = viewModelScope,
        process = { intent -> process(intent) },
    )

    private val mviState = MviMutableStateFlow(initialState)
    final override val state: StateFlow<STATE> =
        core.prepareState(mviState, initialState)

    //region process intent
    final override fun processAsync(intent: INTENT): Job = core.processAsync(intent)

    private suspend fun MviPipelineLogger.process(intent: INTENT) {
        val state = mviState.loggable(this)
        intentProcessor.processIntent(intent, state).collect { result ->
            log(Info, Category.Result) { "produce result $result" }
            state.update { stateReducer.reduxState(result, it) }
        }
    }
    //endregion

    protected abstract val intentProcessor: MviIntentProcessor<INTENT, STATE, RESULT>
    protected abstract val stateReducer: MviStateReducer<RESULT, STATE>

    //region MviConfig
    public interface MviConfig : BaseMviConfig {
        public interface Editor : MviConfig, BaseMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source)
    //endregion
}
