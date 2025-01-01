package ua.railian.architecture.mvi.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

internal fun createMockMviModelScope(): CoroutineScope {
    return CoroutineScope(context = EmptyCoroutineContext + SupervisorJob())
}
