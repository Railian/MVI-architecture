package ua.railian.architecture.mvi.constructed

public fun interface MviActionEmitter<RESULT, STATE, ACTION> {
    public fun emitAction(result: RESULT, currentState: STATE): ACTION?
}
