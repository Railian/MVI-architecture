package ua.railian.mvi.simple

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.MviLogger
import ua.railian.mvi.log.Priority.Error
import ua.railian.mvi.log.Priority.Info
import ua.railian.mvi.log.Priority.Warn
import ua.railian.mvi.log.with
import ua.railian.mvi.pipeline.PipelineId
import ua.railian.mvi.pipeline.PipelineIdGenerator
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Processes intents in the MVI (Model-View-Intent) pattern.
 *
 * This class handles the processing of intents by invoking a user-defined suspendable
 * function. It also logs the processing lifecycle and measures the execution time for
 * monitoring and debugging purposes.
 *
 * @param INTENT The type of intents to be processed.
 * @property viewModelScope The [CoroutineScope] in which intents are processed.
 * @property pipelineIdGenerator Generates unique pipeline IDs for logging and tracking intent processing.
 * @property process A suspendable function that processes an intent, taking a pipeline ID and the intent as parameters.
 * @param logger An [MviLogger] instance for structured logging.
 */
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

    /**
     * Processes an intent asynchronously by launching it in the [viewModelScope].
     *
     * @param intent The intent to process.
     * @return A [Job] representing the processing task.
     */
    internal fun processAsync(intent: INTENT): Job {
        return viewModelScope.launch { process(intent) }
    }
}

/**
 * Measures the execution time of a block, handling cancellations and failures.
 *
 * @param onCancelled A callback invoked if the block is cancelled, with the cancellation cause.
 * @param onFailed A callback invoked if the block fails due to an exception, with the exception.
 * @param block The code block whose execution time is to be measured.
 * @return The duration of the block's execution as a [Duration].
 */
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
