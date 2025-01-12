package ua.railian.mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ua.railian.mvi.MviModel
import ua.railian.mvi.config.BaseMviConfig
import ua.railian.mvi.log.MviLogger
import ua.railian.mvi.log.MviPipelineLogger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

public fun <INTENT> mviCore(
    config: BaseMviConfig,
    viewModelScope: CoroutineScope,
    initialIntents: Flow<INTENT> = emptyFlow(),
    process: suspend MviPipelineLogger.(intent: INTENT) -> Unit,
): ReadOnlyProperty<MviModel<*, *>, MviCore<INTENT>> {
    return ReadOnlyProperty { thisRef, _ ->
        MviCore(
            config = config,
            initialIntents = initialIntents,
            viewModelScope = viewModelScope,
            mviModelClass = thisRef::class,
            process = process,
        )
    }
}

public class MviCore<INTENT>(
    private val config: BaseMviConfig,
    private val viewModelScope: CoroutineScope,
    initialIntents: Flow<INTENT> = emptyFlow(),
    mviModelClass: KClass<*>,
    process: suspend MviPipelineLogger.(intent: INTENT) -> Unit,
) {
    public val logger: MviLogger = MviLogger(
        config = config.logger,
        mviModelClass = mviModelClass,
    )

    private val intentProcessor = MviProcessor(
        viewModelScope = viewModelScope,
        pipelineIdGenerator = config.pipelineIdGeneratorFactory(),
        process = process,
        logger = logger,
    )

    private val initializer = MviInitializer(
        viewModelScope = viewModelScope,
        initialIntents = initialIntents,
        intentProcessor = intentProcessor,
    )

    init {
        if (!config.lazyInit) initializer.initAsync()
    }

    public fun processAsync(intent: INTENT): Job {
        if (config.lazyInit) initializer.initAsync()
        return intentProcessor.processAsync(intent)
    }

    public fun <STATE> prepareState(
        stateFlow: Flow<STATE>,
        initialState: STATE,
    ): StateFlow<STATE> {
        return stateFlow
            .onStart { if (config.lazyInit) initializer.initAsync() }
            .stateIn(viewModelScope, SharingStarted.Lazily, initialState)
    }

    public fun <EVENT> prepareEvents(
        eventsFlow: Flow<EVENT>,
    ): Flow<EVENT> {
        return eventsFlow
            .onStart { if (config.lazyInit) initializer.initAsync() }
    }
}
