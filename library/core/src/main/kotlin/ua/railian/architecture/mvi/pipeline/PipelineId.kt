package ua.railian.architecture.mvi.pipeline

public typealias PipelineId = Any

public fun interface PipelineIdGenerator {
    public fun next(): PipelineId
}

public typealias PipelineIdGeneratorFactory = () -> PipelineIdGenerator
