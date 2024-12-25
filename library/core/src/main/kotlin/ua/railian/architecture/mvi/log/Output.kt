package ua.railian.architecture.mvi.log

public fun interface Output {
    public operator fun invoke(
        tag: String?,
        priority: Priority,
        message: () -> String,
    )

    public companion object
}
