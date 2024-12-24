package ua.railian.architecture.mvi.simple

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart
import ua.railian.architecture.mvi.MviModelWithActions
import ua.railian.architecture.mvi.config.ActionsMviConfig
import ua.railian.architecture.mvi.config.ActionsMviConfigEditor
import ua.railian.architecture.mvi.config.BaseMviConfig
import ua.railian.architecture.mvi.config.BaseMviConfigEditor
import ua.railian.architecture.mvi.config.GlobalMviConfig
import ua.railian.architecture.mvi.config.SharedMviConfig
import ua.railian.architecture.mvi.flow.MutableActionFlow
import ua.railian.architecture.mvi.flow.MviMutableActionFlow
import ua.railian.architecture.mvi.flow.MviMutableStateFlow
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.with
import ua.railian.architecture.mvi.pipeline.PipelineId

public abstract class AbstractMviModelWithActions<STATE, INTENT, ACTION>(
    initialState: STATE,
    initialIntents: Flow<INTENT> = emptyFlow(),
    sharedConfig: SharedMviConfig = GlobalMviConfig,
    settings: MviConfig.Editor.() -> Unit = {},
) : MviModelWithActions<STATE, INTENT, ACTION>, BaseMviModel<STATE, INTENT>(
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

    private val mviActions by lazy {
        MviMutableActionFlow<ACTION>(
            config = config.actions,
            logger = logger,
        )
    }

    init {
        baseInit()
    }

    final override val state: StateFlow<STATE> = mviState.asStateFlow()

    final override val actions: Flow<ACTION> = mviActions
        .onStart { if (config.lazyInit) initializer.initAsync() }

    //region PipelineScope
    public open inner class PipelineScope internal constructor(public val pipelineId: PipelineId) {
        private val mviModelRef = this@AbstractMviModelWithActions
        public val logger: MviPipelineLogger = mviModelRef.logger with pipelineId
        public val state: MutableStateFlow<STATE> = mviState.loggable(logger)
        public val actions: MutableActionFlow<ACTION> = mviActions.loggable(logger)
    }

    final override suspend fun process(pipelineId: PipelineId, intent: INTENT) {
        PipelineScope(pipelineId).process(intent)
    }

    protected abstract suspend fun PipelineScope.process(intent: INTENT)
    //endregion

    //region MviConfig
    public interface MviConfig : BaseMviConfig, ActionsMviConfig {
        public interface Editor : MviConfig,
            BaseMviConfig.Editor,
            ActionsMviConfig.Editor
    }

    private class MviConfigEditor(source: SharedMviConfig) : MviConfig.Editor,
        BaseMviConfig.Editor by BaseMviConfigEditor(source),
        ActionsMviConfig.Editor by ActionsMviConfigEditor(source)
    //endregion
}
