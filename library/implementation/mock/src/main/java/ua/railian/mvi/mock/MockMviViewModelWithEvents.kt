package ua.railian.mvi.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

public typealias MockMviViewModelWithEventsProcessor<STATE, INTENT, EVENT> =
        suspend MockMviViewModelWithEvents<STATE, INTENT, EVENT>.ProcessorScope.(intent: INTENT) -> Unit

public open class MockMviViewModelWithEvents<STATE, INTENT, EVENT>(
    initialState: STATE,
    viewModelScope: CoroutineScope = createMockMviViewModelScope(),
    private val intentProcessor: MockMviViewModelWithEventsProcessor<STATE, INTENT, EVENT>? = null,
) : MviModelWithEvents<STATE, INTENT, EVENT>, ViewModel(viewModelScope) {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<STATE> = _state

    private val _events = MviMutableEventFlow<EVENT>(EventFlowConfig.Unlimited)
    final override val events: Flow<EVENT> = _events

    final override fun processAsync(intent: INTENT): Job {
        intentProcessor ?: return Job().apply { complete() }
        return viewModelScope.launch {
            intentProcessor.invoke(ProcessorScope(), intent)
        }
    }

    public inner class ProcessorScope {
        public val state: MutableStateFlow<STATE> = _state
        public val events: MutableEventFlow<EVENT> = _events
        public val queueDispatcher: QueueDispatcher<Any> = QueueDispatcher()
    }
}
