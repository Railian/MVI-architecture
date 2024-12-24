package ua.railian.architecture.mvi.config

public interface SharedMviConfig :
    BaseMviConfig, ActionsMviConfig {

    public interface Editor : SharedMviConfig,
        BaseMviConfig.Editor,
        ActionsMviConfig.Editor
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
    ActionsMviConfig.Editor by ActionsMviConfigEditor(source)
