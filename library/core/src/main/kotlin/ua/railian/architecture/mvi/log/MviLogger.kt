package ua.railian.architecture.mvi.log

import ua.railian.architecture.mvi.pipeline.PipelineId

public interface MviLogger {
    public fun log(
        priority: Priority,
        category: Category,
        pipelineId: PipelineId? = null,
        message: () -> String,
    )
}

public interface MviCategoryLogger : MviLogger {
    public val category: Category
    public fun log(
        priority: Priority,
        pipelineId: PipelineId? = null,
        message: () -> String,
    )
}

public interface MviPipelineLogger : MviLogger {
    public val pipelineId: PipelineId
    public fun log(
        priority: Priority,
        category: Category,
        message: () -> String,
    )
}

public interface MviPipelineCategoryLogger : MviCategoryLogger, MviPipelineLogger {
    public fun log(
        priority: Priority,
        message: () -> String,
    )
}
