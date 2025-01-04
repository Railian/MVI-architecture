package ua.railian.mvi.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.railian.mvi.MviModel
import ua.railian.mvi.queue.QueueDispatcher

public typealias MockMviViewModelProcessor<STATE, INTENT> =
        suspend MockMviViewModel<STATE, INTENT>.ProcessorScope.(intent: INTENT) -> Unit

public open class MockMviViewModel<STATE, INTENT>(
    initialState: STATE,
    viewModelScope: CoroutineScope = createMockMviViewModelScope(),
    private val intentProcessor: MockMviViewModelProcessor<STATE, INTENT>? = null,
) : MviModel<STATE, INTENT>, ViewModel(viewModelScope) {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<STATE> = _state

    final override fun processAsync(intent: INTENT): Job {
        intentProcessor ?: return Job().apply { complete() }
        return viewModelScope.launch {
            intentProcessor.invoke(ProcessorScope(), intent)
        }
    }

    public inner class ProcessorScope {
        public val state: MutableStateFlow<STATE> = _state
        public val queueDispatcher: QueueDispatcher<Any> = QueueDispatcher()
    }
}
