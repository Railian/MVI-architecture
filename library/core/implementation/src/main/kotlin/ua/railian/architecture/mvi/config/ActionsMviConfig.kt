package ua.railian.architecture.mvi.config

private object EventsMviConfigDefaults : EventsMviConfig {
    override val events: EventFlowConfig = EventFlowConfig.Unlimited
}

public class EventsMviConfigEditor(
    source: EventsMviConfig = EventsMviConfigDefaults,
) : EventsMviConfig.Editor {
    override var events: EventFlowConfig = source.events
}
