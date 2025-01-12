package ua.railian.mvi.config

import ua.railian.mvi.log.Category
import ua.railian.mvi.log.Level
import ua.railian.mvi.log.Output
import ua.railian.mvi.pipeline.PipelineId
import kotlin.reflect.KClass

public interface LoggerMviConfig {
    public val tag: KClass<*>.() -> String?
    public val level: Level
    public val categories: Set<Category>
    public val filter: (pipelineId: PipelineId) -> Boolean
    public val outputs: Set<Output>

    public interface Editor : LoggerMviConfig {
        override var tag: KClass<*>.() -> String?
        override var level: Level
        override var categories: Set<Category>
        override var filter: (pipelineId: PipelineId) -> Boolean
        override var outputs: Set<Output>
    }
}
