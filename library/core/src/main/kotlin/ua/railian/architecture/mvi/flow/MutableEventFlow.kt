package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.flow.Flow

public interface MutableEventFlow<EVENT> : Flow<EVENT> {
    public suspend fun send(event: EVENT)
    public fun trySend(event: EVENT): Boolean
}
