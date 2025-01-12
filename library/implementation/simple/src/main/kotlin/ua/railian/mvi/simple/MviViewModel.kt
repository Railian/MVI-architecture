package ua.railian.mvi.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.mvi.MviModel
import ua.railian.mvi.core.mviCore
import ua.railian.mvi.config.BaseMviConfig
import ua.railian.mvi.config.BaseMviConfigEditor
import ua.railian.mvi.config.GlobalMviConfig
import ua.railian.mvi.config.SharedMviConfig
import ua.railian.mvi.flow.MviMutableStateFlow
import ua.railian.mvi.log.MviPipelineLogger
import ua.railian.mvi.log.with
import ua.railian.mvi.pipeline.PipelineId

public abstract class MviViewModel<STATE, INTENT>(
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
        process = { intent ->
            PipelineScope(pipelineId).process(intent)
        },
    )

    private val mviState = MviMutableStateFlow(initialState)
    final override val state: StateFlow<STATE> =
        core.prepareState(mviState, initialState)

    final override fun processAsync(intent: INTENT): Job = core.processAsync(intent)
    protected abstract suspend fun PipelineScope.process(intent: INTENT)

    //region PipelineScope
    public inner class PipelineScope internal constructor(public val pipelineId: PipelineId) {
        public val logger: MviPipelineLogger = core.logger with pipelineId
        public val state: MutableStateFlow<STATE> = mviState.loggable(logger)
    }
    //endregion

    //region MviConfig
    public interface MviConfig : BaseMviConfig {
        public interface Editor : MviConfig, BaseMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source)
    //endregion
}
