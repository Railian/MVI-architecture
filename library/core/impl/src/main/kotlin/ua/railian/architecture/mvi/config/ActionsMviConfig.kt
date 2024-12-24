package ua.railian.architecture.mvi.config

private object ActionsMviConfigDefaults : ActionsMviConfig {
    override val actions: ActionFlowConfig = ActionFlowConfig.Unlimited
}

public class ActionsMviConfigEditor(
    source: ActionsMviConfig = ActionsMviConfigDefaults,
) : ActionsMviConfig.Editor {
    override var actions: ActionFlowConfig = source.actions
}
