package ua.railian.architecture.mvi.config

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

public interface EventsMviConfig {
    public val events: EventFlowConfig

    public interface Editor : EventsMviConfig {
        override var events: EventFlowConfig
    }
}

/**
 * Configuration for EventFlow which controls how events are buffered and processed.
 */
public sealed class EventFlowConfig(
    public open val capacity: Int = Channel.RENDEZVOUS,
    public open val onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
) {

    /**
     * A configuration with no buffer. Each event is processed immediately, and if there's a backpressure,
     * the producer will suspend until the consumer is ready.
     */
    public data object Rendezvous : EventFlowConfig()

    /**
     * A configuration that allows unlimited buffering of events.
     * Events are never dropped, and backpressure is not applied.
     */
    public data object Unlimited : EventFlowConfig(
        capacity = Channel.UNLIMITED,
    )

    /**
     * A configuration where only the most recent event is retained.
     * Any previous events in the buffer are overwritten.
     */
    public data object Conflated : EventFlowConfig(
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
    ) : EventFlowConfig(
        capacity = capacity,
        onBufferOverflow = onBufferOverflow,
    )
}
