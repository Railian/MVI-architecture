package ua.railian.architecture.mvi.simple

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ua.railian.architecture.mvi.MviModelWithEvents
import ua.railian.architecture.mvi.config.EventsMviConfig
import ua.railian.architecture.mvi.config.EventsMviConfigEditor
import ua.railian.architecture.mvi.config.BaseMviConfig
import ua.railian.architecture.mvi.config.BaseMviConfigEditor
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.flow.MutableEventFlow
import ua.railian.architecture.mvi.flow.MviMutableEventFlow
import ua.railian.architecture.mvi.flow.MviMutableStateFlow
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.with
import ua.railian.architecture.mvi.pipeline.PipelineId

public abstract class MviViewModelWithEvents<STATE, INTENT, EVENT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModelWithEvents<STATE, INTENT, EVENT>, BaseMviViewModel<STATE, INTENT>(
    initialIntents = initialIntents,
) {
    override val config: MviConfig = MviConfigEditor(sharedConfig).apply(settings)

    private val mviState = MviMutableStateFlow(initialState)

    private val mviEvents by lazy {
        MviMutableEventFlow<EVENT>(
            config = config.events,
            logger = logger,
        )
    }

    init {
        baseInit()
    }

    final override val state: StateFlow<STATE> = mviState
        .onStart { if (config.lazyInit) initializer.initAsync() }
        .stateIn(viewModelScope, SharingStarted.Lazily, initialState)

    final override val events: Flow<EVENT> = mviEvents
        .onStart { if (config.lazyInit) initializer.initAsync() }

    //region PipelineScope
    public open inner class PipelineScope internal constructor(public val pipelineId: PipelineId) {
        private val mviModelRef = this@MviViewModelWithEvents
        public val logger: MviPipelineLogger = mviModelRef.logger with pipelineId
        public val state: MutableStateFlow<STATE> = mviState.loggable(logger)
        public val events: MutableEventFlow<EVENT> = mviEvents.loggable(logger)
    }

    final override suspend fun process(pipelineId: PipelineId, intent: INTENT) {
        PipelineScope(pipelineId).process(intent)
    }

    protected abstract suspend fun PipelineScope.process(intent: INTENT)
    //endregion

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
