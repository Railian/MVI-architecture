package ua.railian.mvi.dualstate.embedded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ua.railian.mvi.MviModelWithEvents
import ua.railian.mvi.config.BaseMviConfig
import ua.railian.mvi.config.BaseMviConfigEditor
import ua.railian.mvi.config.EventsMviConfig
import ua.railian.mvi.config.EventsMviConfigEditor
import ua.railian.mvi.config.GlobalMviConfig
import ua.railian.mvi.config.SharedMviConfig
import ua.railian.mvi.core.mviCore
import ua.railian.mvi.flow.MviMutableEventFlow
import ua.railian.mvi.flow.MviMutableStateFlow
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.MviPipelineLogger
import ua.railian.mvi.log.Priority.Info

public abstract class MviViewModelWithEvents<DOMAIN_STATE, UI_STATE, INTENT, RESULT, EVENT>(
    initialState: DOMAIN_STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModelWithEvents<UI_STATE, INTENT, EVENT>, ViewModel() {

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

    private val mviEvents = MviMutableEventFlow<EVENT>(config.events, core.logger)
    final override val events: Flow<EVENT> = core.prepareEvents(mviEvents)

    //region process intent
    final override fun processAsync(intent: INTENT): Job = core.processAsync(intent)

    private suspend fun MviPipelineLogger.process(intent: INTENT) {
        val state = mviState.loggable(this)
        val events = mviEvents.loggable(this)
        processIntent(intent, state).collect { result ->
            log(Info, Category.Result) { "produce result $result" }
            state.update { reduxState(result, it) }
            emitEvent(result, state.value)
                ?.let { event -> events.send(event) }
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

    protected abstract fun emitEvent(
        result: RESULT,
        currentState: DOMAIN_STATE,
    ): EVENT?

    protected abstract fun mapState(
        state: DOMAIN_STATE,
    ): UI_STATE

    //region MviConfig
    public interface MviConfig : BaseMviConfig, EventsMviConfig {
        public interface Editor : MviConfig,
            BaseMviConfig.Editor,
            EventsMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source),
        EventsMviConfig.Editor by EventsMviConfigEditor(source)
    //endregion
}
