package ua.railian.mvi.constructed

public fun interface MviStateMapper<DOMAIN_STATE, UI_STATE> {
    public fun mapState(state: DOMAIN_STATE): UI_STATE
}
