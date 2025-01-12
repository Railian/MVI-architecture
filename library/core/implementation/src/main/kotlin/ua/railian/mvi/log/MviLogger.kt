package ua.railian.mvi.log

import ua.railian.mvi.config.LoggerMviConfig
import ua.railian.mvi.pipeline.PipelineId
import kotlin.reflect.KClass

private class MviLoggerImpl(
    private val config: LoggerMviConfig,
    private val mviModelClass: KClass<*>,
) : MviLogger {

    override fun log(
        priority: Priority,
        category: Category,
        pipelineId: PipelineId?,
        message: () -> String
    ) {
        if (priority !in config.level.priorities) return
        if (category !in config.categories) return
        if (pipelineId?.let(config.filter) == false) return
        config.outputs.forEach { output ->
            output.invoke(config.tag(mviModelClass), priority) {
                buildString {
                    pipelineId?.let { append("${it}: ") }
                    append(message())
                }
            }
        }
    }
}

private class MviCategoryLoggerImpl(
    private val delegate: MviLogger,
    override val category: Category,
) : MviCategoryLogger, MviLogger by delegate {

    override fun log(priority: Priority, pipelineId: PipelineId?, message: () -> String) {
        delegate.log(priority, category, pipelineId, message)
    }
}

private class MviPipelineLoggerImpl(
    private val delegate: MviLogger,
    override val pipelineId: PipelineId,
) : MviPipelineLogger, MviLogger by delegate {

    override fun log(priority: Priority, category: Category, message: () -> String) {
        delegate.log(priority, category, pipelineId, message)
    }
}

private class MviPipelineCategoryLoggerImpl(
    private val delegate: MviLogger,
    override val category: Category,
    override val pipelineId: PipelineId,
) : MviPipelineCategoryLogger, MviLogger by delegate {

    override fun log(priority: Priority, message: () -> String) {
        delegate.log(priority, category, pipelineId, message)
    }

    override fun log(priority: Priority, pipelineId: PipelineId?, message: () -> String) {
        delegate.log(priority, category, pipelineId, message)
    }

    override fun log(priority: Priority, category: Category, message: () -> String) {
        delegate.log(priority, category, pipelineId, message)
    }
}

public fun MviLogger(
    config: LoggerMviConfig,
    mviModelClass: KClass<*>,
): MviLogger = MviLoggerImpl(config, mviModelClass)

public infix fun MviLogger.with(category: Category): MviCategoryLogger {
    return MviCategoryLoggerImpl(this, category)
}

public infix fun MviLogger.with(pipelineId: PipelineId): MviPipelineLogger {
    return MviPipelineLoggerImpl(this, pipelineId)
}

public infix fun MviPipelineLogger.with(category: Category): MviPipelineCategoryLogger {
    return MviPipelineCategoryLoggerImpl(this, category, pipelineId)
}

public infix fun MviCategoryLogger.with(pipelineId: PipelineId): MviPipelineCategoryLogger {
    return MviPipelineCategoryLoggerImpl(this, category, pipelineId)
}
