package ua.railian.mvi.dualstate.embedded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
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

public abstract class MviViewModel<DOMAIN_STATE, UI_STATE, INTENT, RESULT>(
    initialState: DOMAIN_STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModel<UI_STATE, INTENT>, ViewModel() {

    protected val config: MviConfig =
        MviConfigEditor(sharedConfig).apply(settings)

    private val core by mviCore(
        config = config,
        initialIntents = initialIntents,
        viewModelScope = viewModelScope,
        process = { intent -> process(intent) },
    )

    private val mviState = MviMutableStateFlow(initialState)
    final override val state: StateFlow<UI_STATE> by lazy {
        core.prepareState(mviState.map(::mapState), mapState(initialState))
    }

    //region process intent
    final override fun processAsync(intent: INTENT): Job = core.processAsync(intent)

    private suspend fun MviPipelineLogger.process(intent: INTENT) {
        val state = mviState.loggable(this)
        processIntent(intent, state).collect { result ->
            log(Info, Category.Result) { "produce result $result" }
            state.update { reduxState(result, it) }
        }
    }
    //endregion

    protected abstract fun processIntent(
        intent: INTENT,
        state: StateFlow<DOMAIN_STATE>,
    ): Flow<RESULT>

    protected abstract fun reduxState(
        result: RESULT,
        currentState: DOMAIN_STATE,
    ): DOMAIN_STATE

    protected abstract fun mapState(
        state: DOMAIN_STATE,
    ): UI_STATE

    //region MviConfig
    public interface MviConfig : BaseMviConfig {
        public interface Editor : MviConfig, BaseMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source)
    //endregion
}
