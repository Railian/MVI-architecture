package ua.railian.mvi.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.railian.mvi.MviModelWithEvents
import ua.railian.mvi.config.EventFlowConfig
import ua.railian.mvi.flow.MutableEventFlow
import ua.railian.mvi.flow.MviMutableEventFlow
import ua.railian.mvi.queue.QueueDispatcher

public typealias MockMviModelWithEventsProcessor<STATE, INTENT, EVENT> =
        suspend MockMviModelWithEvents<STATE, INTENT, EVENT>.PipelineScope.(intent: INTENT) -> Unit

public open class MockMviModelWithEvents<STATE, INTENT, EVENT>(
    initialState: STATE,
    protected val coroutineScope: CoroutineScope = createMockMviModelScope(),
    private val intentProcessor: MockMviModelWithEventsProcessor<STATE, INTENT, EVENT>? = null,
) : MviModelWithEvents<STATE, INTENT, EVENT> {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<STATE> = _state

    private val _events = MviMutableEventFlow<EVENT>(EventFlowConfig.Unlimited)
    final override val events: Flow<EVENT> = _events

    final override fun processAsync(intent: INTENT): Job {
        intentProcessor ?: return Job().apply { complete() }
        return coroutineScope.launch {
            intentProcessor.invoke(PipelineScope(), intent)
        }
    }

    public inner class PipelineScope {
        public val state: MutableStateFlow<STATE> = _state
        public val events: MutableEventFlow<EVENT> = _events
        public val queueDispatcher: QueueDispatcher<Any> = QueueDispatcher()
    }
}
