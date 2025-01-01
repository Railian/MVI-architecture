package ua.railian.architecture.mvi.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.railian.architecture.mvi.MviModelWithActions
import ua.railian.architecture.mvi.config.ActionFlowConfig
import ua.railian.architecture.mvi.flow.MutableActionFlow
import ua.railian.architecture.mvi.flow.MviMutableActionFlow
import ua.railian.architecture.mvi.queue.QueueDispatcher

public typealias MockMviModelWithActionsProcessor<STATE, INTENT, ACTION> =
        suspend MockMviModelWithActions<STATE, INTENT, ACTION>.PipelineScope.(intent: INTENT) -> Unit

public open class MockMviModelWithActions<STATE, INTENT, ACTION>(
    initialState: STATE,
    protected val coroutineScope: CoroutineScope = createMockMviModelScope(),
    private val intentProcessor: MockMviModelWithActionsProcessor<STATE, INTENT, ACTION>? = null,
) : MviModelWithActions<STATE, INTENT, ACTION> {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<STATE> = _state

    private val _actions = MviMutableActionFlow<ACTION>(ActionFlowConfig.Unlimited)
    final override val actions: Flow<ACTION> = _actions

    final override fun processAsync(intent: INTENT): Job {
        intentProcessor ?: return Job().apply { complete() }
        return coroutineScope.launch {
            intentProcessor.invoke(PipelineScope(), intent)
        }
    }

    public inner class PipelineScope {
        public val state: MutableStateFlow<STATE> = _state
        public val actions: MutableActionFlow<ACTION> = _actions
        public val queueDispatcher: QueueDispatcher<Any> = QueueDispatcher()
    }
}
