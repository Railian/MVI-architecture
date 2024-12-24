package ua.railian.architecture.mvi.config

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

public interface ActionsMviConfig {
    public val actions: ActionFlowConfig

    public interface Editor : ActionsMviConfig {
        override var actions: ActionFlowConfig
    }
}

/**
 * Configuration for ActionFlow which controls how actions are buffered and processed.
 */
public sealed class ActionFlowConfig(
    public open val capacity: Int = Channel.RENDEZVOUS,
    public open val onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
) {

    /**
     * A configuration with no buffer. Each action is processed immediately, and if there's a backpressure,
     * the producer will suspend until the consumer is ready.
     */
    public data object Rendezvous : ActionFlowConfig()

    /**
     * A configuration that allows unlimited buffering of actions.
     * Actions are never dropped, and backpressure is not applied.
     */
    public data object Unlimited : ActionFlowConfig(
        capacity = Channel.UNLIMITED,
    )

    /**
     * A configuration where only the most recent action is retained.
     * Any previous actions in the buffer are overwritten.
     */
    public data object Conflated : ActionFlowConfig(
        capacity = Channel.CONFLATED,
    )

    /**
     * A configuration that supports a fixed buffer size.
     * If the buffer overflows, the specified [onBufferOverflow] strategy is applied.
     *
     * @property capacity The size of the buffer. Default is [Channel.BUFFERED].
     * @property onBufferOverflow Strategy to apply when the buffer overflows. Default is [BufferOverflow.SUSPEND].
     */
    public data class Buffered(
        override val capacity: Int = Channel.BUFFERED,
        override val onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    ) : ActionFlowConfig(
        capacity = capacity,
        onBufferOverflow = onBufferOverflow,
    )
}
