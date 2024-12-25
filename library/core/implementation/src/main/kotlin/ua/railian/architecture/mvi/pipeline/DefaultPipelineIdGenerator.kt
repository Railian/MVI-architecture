package ua.railian.architecture.mvi.pipeline

import kotlinx.atomicfu.atomic

internal class DefaultPipelineIdGenerator :
    PipelineIdGenerator {
    private var nextId by atomic(initial = 0L)
    override fun next(): Long = nextId++
}
