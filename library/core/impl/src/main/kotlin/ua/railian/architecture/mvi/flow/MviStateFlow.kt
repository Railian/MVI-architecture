package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.MviPipelineCategoryLogger
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.Priority.Warn
import ua.railian.architecture.mvi.log.with

public interface MviMutableStateFlow<STATE> : MutableStateFlow<STATE> {
    public fun loggable(logger: MviPipelineLogger): MutableStateFlow<STATE>
}

public fun <STATE> MviMutableStateFlow(
    initialValue: STATE,
    onFirstCollect: () -> Unit,
): MviMutableStateFlow<STATE> {
    return MviMutableStateFlowImpl(
        delegate = MutableStateFlow(initialValue),
        onFirstCollect = onFirstCollect,
    )
}

private class MviMutableStateFlowImpl<STATE>(
    private val delegate: MutableStateFlow<STATE>,
    private val onFirstCollect: () -> Unit,
) : MviMutableStateFlow<STATE>,
    MutableStateFlow<STATE> by delegate {

    override suspend fun collect(collector: FlowCollector<STATE>): Nothing {
        interceptFirstCollect()
        delegate.collect(collector)
    }

    private var isFirstCollect = true

    private fun interceptFirstCollect() {
        if (isFirstCollect) {
            isFirstCollect = false
            onFirstCollect()
        }
    }

    override fun loggable(
        logger: MviPipelineLogger,
    ): MutableStateFlow<STATE> {
        return Loggable(logger)
    }

    private inner class Loggable(
        logger: MviPipelineLogger
    ) : MutableStateFlow<STATE> by this,
        MviPipelineCategoryLogger by logger with Category.State {

        override var value: STATE
            get() = delegate.value
            set(value) {
                delegate.value = value
                log(Info) { "update state to $value" }
            }

        override suspend fun emit(value: STATE) {
            delegate.emit(value)
            log(Info) { "update state to $value" }
        }

        override fun tryEmit(value: STATE): Boolean {
            val success = delegate.tryEmit(value)
            if (success) log(Info) { "update state to $value" }
            else log(Warn) { "failed to update state $value" }
            return success
        }

        override fun compareAndSet(expect: STATE, update: STATE): Boolean {
            val success = delegate.compareAndSet(expect = expect, update = update)
            if (success) log(Info) { "update state to $value" }
            else log(Warn) { "failed to update state to $value" }
            return success
        }
    }
}
