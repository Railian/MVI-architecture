package ua.railian.architecture.mvi.constructed

public fun interface MviEventEmitter<RESULT, STATE, EVENT> {
    public fun emitEvent(result: RESULT, currentState: STATE): EVENT?
}
