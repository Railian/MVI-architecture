package ua.railian.architecture.mvi.constructed

public fun interface MviStateReducer<EFFECT, STATE> {
    public fun reduxState(effect: EFFECT, currentState: STATE): STATE
}
