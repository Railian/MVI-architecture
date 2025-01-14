package ua.railian.mvi.config

import ua.railian.mvi.pipeline.DefaultPipelineIdGenerator
import ua.railian.mvi.pipeline.PipelineIdGeneratorFactory

public object BaseMviConfigDefaults : BaseMviConfig {
    override val lazyInit: Boolean = true
    override val logger: LoggerMviConfig = LoggerMviConfig()
    override val pipelineIdGeneratorFactory: PipelineIdGeneratorFactory =
        ::DefaultPipelineIdGenerator
}

public class BaseMviConfigEditor(
    source: BaseMviConfig = BaseMviConfigDefaults,
) : BaseMviConfig.Editor {
    override var lazyInit: Boolean = source.lazyInit
    override val logger: LoggerMviConfig.Editor = LoggerMviConfigEditor(source.logger)
    override var pipelineIdGeneratorFactory: PipelineIdGeneratorFactory =
        source.pipelineIdGeneratorFactory
}
