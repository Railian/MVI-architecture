package ua.railian.architecture.mvi.config

import ua.railian.architecture.mvi.MviModel
import ua.railian.architecture.mvi.log.Category
import ua.railian.architecture.mvi.log.Level
import ua.railian.architecture.mvi.log.Output
import ua.railian.architecture.mvi.pipeline.PipelineId

public interface LoggerMviConfig {
    public val tag: MviModel<*, *>.() -> String?
//    public val tag: String?
    public val level: Level
    public val categories: Set<Category>
    public val filter: (pipelineId: PipelineId) -> Boolean
    public val outputs: Set<Output>

    public interface Editor : LoggerMviConfig {
        override var tag: MviModel<*, *>.() -> String?
//        override var tag: String?
        override var level: Level
        override var categories: Set<Category>
        override var filter: (pipelineId: PipelineId) -> Boolean
        override var outputs: Set<Output>
    }
}
