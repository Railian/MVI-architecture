package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.receiveAsFlow
import ua.railian.architecture.mvi.config.EventFlowConfig
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.MviLogger
import ua.railian.architecture.mvi.log.MviPipelineCategoryLogger
import ua.railian.architecture.mvi.log.MviPipelineLogger
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.Priority.Warn
import ua.railian.architecture.mvi.log.with

public interface MviMutableEventFlow<EVENT> : MutableEventFlow<EVENT> {
    public fun loggable(logger: MviPipelineLogger): MutableEventFlow<EVENT>
}

public fun <EVENT> MviMutableEventFlow(
    config: EventFlowConfig,
    logger: MviLogger? = null,
): MviMutableEventFlow<EVENT> {
    return MviMutableEventFlowImpl(config) { event ->
        logger?.log(Warn, Category.Event) { "event $event was dropped" }
    }
}

private class MviMutableEventFlowImpl<EVENT>(
    config: EventFlowConfig,
    onUndelivered: (event: EVENT) -> Unit,
) : MviMutableEventFlow<EVENT> {

    private val channel = Channel(
        capacity = config.capacity,
        onBufferOverflow = config.onBufferOverflow,
        onUndeliveredElement = onUndelivered,
    )

    private val flow = channel.receiveAsFlow()

    override suspend fun send(event: EVENT) {
        channel.send(event)
    }

    override fun trySend(event: EVENT): Boolean {
        return channel.trySend(event).isSuccess
    }

    override suspend fun collect(collector: FlowCollector<EVENT>) {
        flow.collect(collector)
    }

    override fun loggable(
        logger: MviPipelineLogger,
    ): MutableEventFlow<EVENT> {
        return Loggable(logger)
    }

    inner class Loggable(
        logger: MviPipelineLogger,
    ) : MutableEventFlow<EVENT> by this,
        MviPipelineCategoryLogger by logger with Category.Event {

        override suspend fun send(event: EVENT) {
            log(Info) { "send event $event" }
            channel.send(event)
            log(Info) { "event $event was sent" }
        }

        override fun trySend(event: EVENT): Boolean {
            val result = channel.trySend(event)
            log(if (result.isSuccess) Info else Warn) {
                val formattedResult = when {
                    result.isSuccess -> "success"
                    result.isFailure -> "failure"
                    result.isClosed -> "closed"
                    else -> "unknown"
                }
                "try send event $event ($formattedResult)"
            }
            return result.isSuccess
        }
    }
}
