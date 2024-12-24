package ua.railian.architecture.mvi.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.MviLogger
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.with
import ua.railian.architecture.mvi.pipeline.PipelineId
import ua.railian.architecture.mvi.pipeline.PipelineIdGenerator
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
            val duration = measureTime { process(pipelineId, intent) }
            log(Info) { "$intentDescriptor $intent was processed in $duration" }
        }
    }

    internal fun processAsync(intent: INTENT): Job {
        return viewModelScope.launch { process(intent) }
    }
}
