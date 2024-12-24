package ua.railian.architecture.mvi.simple

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.MviModel
import ua.railian.architecture.mvi.config.BaseMviConfig
import ua.railian.architecture.mvi.config.BaseMviConfigEditor
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.flow.MviMutableStateFlow
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.with
import ua.railian.architecture.mvi.pipeline.PipelineId

public abstract class AbstractMviModel<STATE, INTENT>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModel<STATE, INTENT>, BaseMviModel<STATE, INTENT>(
    initialIntents = initialIntents,
) {
    override val config: MviConfig = MviConfigEditor(sharedConfig).apply(settings)

    private val mviState by lazy {
        MviMutableStateFlow(
            initialValue = initialState,
            onFirstCollect = {
                if (config.lazyInit) {
                    initializer.initAsync()
                }
            },
        )
    }

    init {
        baseInit()
    }

    final override val state: StateFlow<STATE> = mviState.asStateFlow()

    //region PipelineScope
    public inner class PipelineScope internal constructor(public val pipelineId: PipelineId) {
        private val mviModelRef = this@AbstractMviModel
        public val logger: MviPipelineLogger = mviModelRef.logger with pipelineId
        public val state: MutableStateFlow<STATE> = mviState.loggable(logger)
    }

    final override suspend fun process(pipelineId: PipelineId, intent: INTENT) {
        PipelineScope(pipelineId).process(intent)
    }

    protected abstract suspend fun PipelineScope.process(intent: INTENT)
    //endregion

    //region MviConfig
    public interface MviConfig : BaseMviConfig {
        public interface Editor : MviConfig, BaseMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source)
    //endregion
}
