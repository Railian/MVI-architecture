package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.receiveAsFlow
import ua.railian.architecture.mvi.config.ActionFlowConfig
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.MviCategoryLogger
import ua.railian.architecture.mvi.log.MviLogger
import ua.railian.architecture.mvi.log.MviPipelineCategoryLogger
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.Priority.Warn
import ua.railian.architecture.mvi.log.with

public interface MviMutableActionFlow<ACTION> : MutableActionFlow<ACTION> {
    public fun loggable(logger: MviPipelineLogger): MutableActionFlow<ACTION>
}

public fun <ACTION> MviMutableActionFlow(
    config: ActionFlowConfig,
    logger: MviLogger,
): MviMutableActionFlow<ACTION> {
    return MviMutableActionFlowImpl(config, logger)
}

private class MviMutableActionFlowImpl<ACTION>(
    config: ActionFlowConfig,
    logger: MviLogger,
) : MviMutableActionFlow<ACTION>,
    MviCategoryLogger by logger with Category.Action {

    private val channel = Channel<ACTION>(
        capacity = config.capacity,
        onBufferOverflow = config.onBufferOverflow,
        onUndeliveredElement = { action ->
            log(Warn) { "action $action was dropped" }
        },
    )

    private val flow = channel.receiveAsFlow()

    override suspend fun send(action: ACTION) {
        channel.send(action)
    }

    override fun trySend(action: ACTION): Boolean {
        return channel.trySend(action).isSuccess
    }

    override suspend fun collect(collector: FlowCollector<ACTION>) {
        flow.collect(collector)
    }

    override fun loggable(
        logger: MviPipelineLogger,
    ): MutableActionFlow<ACTION> {
        return Loggable(logger)
    }

    inner class Loggable(
        logger: MviPipelineLogger,
    ) : MutableActionFlow<ACTION> by this,
        MviPipelineCategoryLogger by logger with Category.Action {

        override suspend fun send(action: ACTION) {
            log(Info) { "send action $action" }
            channel.send(action)
            log(Info) { "action $action was sent" }
        }

        override fun trySend(action: ACTION): Boolean {
            val result = channel.trySend(action)
            log(if (result.isSuccess) Info else Warn) {
                val formattedResult = when {
                    result.isSuccess -> "success"
                    result.isFailure -> "failure"
                    result.isClosed -> "closed"
                    else -> "unknown"
                }
                "try send action $action ($formattedResult)"
            }
            return result.isSuccess
        }
    }
}
