package ua.railian.mvi.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

internal fun createMockMviViewModelScope(): CoroutineScope {
    return CoroutineScope(context = EmptyCoroutineContext + SupervisorJob())
}
