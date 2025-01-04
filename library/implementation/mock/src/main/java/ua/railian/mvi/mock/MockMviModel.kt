package ua.railian.mvi.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.railian.mvi.MviModel
import ua.railian.mvi.queue.QueueDispatcher

public typealias MockMviModelProcessor<STATE, INTENT> =
        suspend MockMviModel<STATE, INTENT>.PipelineScope.(intent: INTENT) -> Unit

public open class MockMviModel<STATE, INTENT>(
    initialState: STATE,
    protected val mviModelScope: CoroutineScope = createMockMviModelScope(),
    private val intentProcessor: MockMviModelProcessor<STATE, INTENT>? = null,
) : MviModel<STATE, INTENT> {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<STATE> = _state

    final override fun processAsync(intent: INTENT): Job {
        intentProcessor ?: return Job().apply { complete() }
        return mviModelScope.launch {
            intentProcessor.invoke(PipelineScope(), intent)
        }
    }

    public inner class PipelineScope {
        public val state: MutableStateFlow<STATE> = _state
        public val queueDispatcher: QueueDispatcher<Any> = QueueDispatcher()
    }
}
