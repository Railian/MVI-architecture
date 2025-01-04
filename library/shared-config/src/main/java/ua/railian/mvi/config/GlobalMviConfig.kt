package ua.railian.mvi.config

public object GlobalMviConfig : SharedMviConfig by GlobalMviConfigEditor {
    public fun edit(editor: SharedMviConfig.Editor.() -> Unit) {
        GlobalMviConfigEditor.apply(editor)
    }
}

private object GlobalMviConfigEditor : SharedMviConfig.Editor,
    BaseMviConfig.Editor by BaseMviConfigEditor(),
    EventsMviConfig.Editor by EventsMviConfigEditor()
