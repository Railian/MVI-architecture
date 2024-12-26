package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.flow.MutableStateFlow
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.MviPipelineCategoryLogger
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.Priority.Warn
import ua.railian.architecture.mvi.log.with

public interface MviMutableStateFlow<STATE> : MutableStateFlow<STATE> {
    public fun loggable(logger: MviPipelineLogger): MviMutableStateFlow<STATE>
}

public fun <STATE> MviMutableStateFlow(
    initialValue: STATE,
): MviMutableStateFlow<STATE> {
    return MviMutableStateFlowImpl(
        delegate = MutableStateFlow(initialValue),
    )
}

private class MviMutableStateFlowImpl<STATE>(
    private val delegate: MutableStateFlow<STATE>,
) : MviMutableStateFlow<STATE>,
    MutableStateFlow<STATE> by delegate {

    override fun loggable(
        logger: MviPipelineLogger,
    ): MviMutableStateFlow<STATE> {
        return Loggable(logger)
    }

    private inner class Loggable(
        logger: MviPipelineLogger
    ) : MviMutableStateFlow<STATE> by this,
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
