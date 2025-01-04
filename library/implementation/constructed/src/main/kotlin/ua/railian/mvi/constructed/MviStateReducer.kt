package ua.railian.mvi.constructed

public fun interface MviStateReducer<RESULT, STATE> {
    public fun reduxState(result: RESULT, currentState: STATE): STATE
}
