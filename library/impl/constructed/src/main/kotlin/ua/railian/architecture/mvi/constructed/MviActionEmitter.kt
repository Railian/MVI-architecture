package ua.railian.architecture.mvi.constructed

public fun interface MviActionEmitter<EFFECT, STATE, ACTION> {
    public fun emitAction(effect: EFFECT, currentState: STATE): ACTION?
}
