package ua.railian.mvi.config

import ua.railian.mvi.pipeline.PipelineIdGeneratorFactory

public interface BaseMviConfig {
    public val lazyInit: Boolean
    public val logger: LoggerMviConfig
    public val pipelineIdGeneratorFactory: PipelineIdGeneratorFactory

    public interface Editor : BaseMviConfig {
        override var lazyInit: Boolean
        override val logger: LoggerMviConfig.Editor
        override var pipelineIdGeneratorFactory: PipelineIdGeneratorFactory
    }
}

public fun BaseMviConfig.Editor.logger(edit: LoggerMviConfig.Editor.() -> Unit): Unit =
    logger.edit()

public fun BaseMviConfig.Editor.pipelineIdGeneratorFactory(factory: PipelineIdGeneratorFactory) {
    pipelineIdGeneratorFactory = factory
}
