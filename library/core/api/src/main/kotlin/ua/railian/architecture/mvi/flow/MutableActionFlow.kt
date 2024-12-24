package ua.railian.architecture.mvi.flow

import kotlinx.coroutines.flow.Flow

public interface MutableActionFlow<ACTION> : Flow<ACTION> {
    public suspend fun send(action: ACTION)
    public fun trySend(action: ACTION): Boolean
}
