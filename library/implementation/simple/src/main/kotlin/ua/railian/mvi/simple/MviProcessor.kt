package ua.railian.mvi.simple

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.MviLogger
import ua.railian.mvi.log.Priority
import ua.railian.mvi.log.Priority.Error
import ua.railian.mvi.log.Priority.Info
import ua.railian.mvi.log.Priority.Warn
import ua.railian.mvi.log.with
import ua.railian.mvi.pipeline.PipelineId
import ua.railian.mvi.pipeline.PipelineIdGenerator
import kotlin.time.Duration
import kotlin.time.measureTime

internal class MviProcessor<INTENT>(
    private val viewModelScope: CoroutineScope,
    private val pipelineIdGenerator: PipelineIdGenerator,
    private val process: suspend (pipelineId: PipelineId, intent: INTENT) -> Unit,
    logger: MviLogger,
) {
    private val categoryLogger = logger with Category.Intent

    internal suspend fun process(intent: INTENT, initial: Boolean = false) {
        with(categoryLogger with pipelineIdGenerator.next()) {
            val intentDescriptor = if (initial) "initial intent" else "intent"
            log(Info) { "process $intentDescriptor $intent" }
            val duration = measureTime(
                onCancelled = { log(Warn) { "$intentDescriptor $intent was cancelled" } },
                onFailed = { log(Error) { "$intentDescriptor $intent failed with $it" } },
                block = { process(pipelineId, intent) },
            )
            log(Info) { "$intentDescriptor $intent was processed in $duration" }
        }
    }

    internal fun processAsync(intent: INTENT): Job {
        return viewModelScope.launch { process(intent) }
    }
}

private inline fun measureTime(
    onCancelled: (cause: CancellationException) -> Unit,
    onFailed: (cause: Throwable) -> Unit,
    block: () -> Unit,
): Duration {
    return runCatching {
        measureTime(block)
    }.onFailure { cause ->
        when (cause) {
            is CancellationException -> onCancelled(cause)
            else -> onFailed(cause)
        }
    }.getOrThrow()
}
