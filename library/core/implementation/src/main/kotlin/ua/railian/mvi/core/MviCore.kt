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
import kotlin.reflect.KProperty

public fun <INTENT> mviCore(
    config: BaseMviConfig,
    viewModelScope: CoroutineScope,
    initialIntents: Flow<INTENT> = emptyFlow(),
    process: suspend MviPipelineLogger.(intent: INTENT) -> Unit,
): ReadOnlyProperty<MviModel<*, *>, MviCore<INTENT>> {
    return MviCoreDelegate(
        config = config,
        viewModelScope = viewModelScope,
        initialIntents = initialIntents,
        process = process,
    )
}

private class MviCoreDelegate<INTENT>(
    private val config: BaseMviConfig,
    private val viewModelScope: CoroutineScope,
    private val initialIntents: Flow<INTENT> = emptyFlow(),
    private val process: suspend MviPipelineLogger.(intent: INTENT) -> Unit
) : ReadOnlyProperty<MviModel<*, *>, MviCore<INTENT>> {

    private lateinit var mviCore: MviCore<INTENT>

    private fun initMviCore(thisRef: MviModel<*, *>) {
        mviCore = MviCore(
            config = config,
            initialIntents = initialIntents,
            viewModelScope = viewModelScope,
            mviModelClass = thisRef::class,
            process = process,
        )
    }

    override fun getValue(
        thisRef: MviModel<*, *>,
        property: KProperty<*>,
    ): MviCore<INTENT> {
        if (!::mviCore.isInitialized) {
            initMviCore(thisRef)
        }
        return mviCore
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
