package ua.railian.architecture.mvi.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ua.railian.architecture.mvi.MviModel
import ua.railian.architecture.mvi.config.BaseMviConfig
import ua.railian.architecture.mvi.log.MviLogger
import ua.railian.architecture.mvi.pipeline.PipelineId

public abstract class BaseMviModel<STATE, INTENT>(
    initialIntents: Flow<INTENT> = emptyFlow(),
) : MviModel<STATE, INTENT>, ViewModel() {

    protected abstract val config: BaseMviConfig

    protected val logger: MviLogger by lazy {
        MviLogger(
            config = config.logger,
            modelRef = this,
        )
    }

    private val intentProcessor by lazy {
        MviProcessor(
            viewModelScope = viewModelScope,
            pipelineIdGenerator = config.pipelineIdGeneratorFactory(),
            process = ::process,
            logger = logger,
        )
    }

    internal val initializer by lazy {
        MviInitializer(
            viewModelScope = viewModelScope,
            initialIntents = initialIntents,
            intentProcessor = intentProcessor,
        )
    }

    internal fun baseInit() {
        if (!config.lazyInit) initializer.initAsync()
    }

    public final override fun processAsync(intent: INTENT): Job {
        if (config.lazyInit) initializer.initAsync()
        return intentProcessor.processAsync(intent)
    }

    internal abstract suspend fun process(pipelineId: PipelineId, intent: INTENT)
}
