package ua.railian.mvi.config

public interface SharedMviConfig :
    BaseMviConfig, EventsMviConfig {

    public interface Editor : SharedMviConfig,
        BaseMviConfig.Editor,
        EventsMviConfig.Editor
}

public fun SharedMviConfig(
    source: SharedMviConfig = GlobalMviConfig,
    editor: SharedMviConfig.Editor.() -> Unit,
): SharedMviConfig {
    return SharedMviConfigEditor(source).apply(editor)
}

private open class SharedMviConfigEditor(
    source: SharedMviConfig,
) : SharedMviConfig.Editor,
    BaseMviConfig.Editor by BaseMviConfigEditor(source),
    EventsMviConfig.Editor by EventsMviConfigEditor(source)
