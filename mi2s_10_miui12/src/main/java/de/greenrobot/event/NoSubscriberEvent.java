package de.greenrobot.event;

public final class NoSubscriberEvent {
    public final EventBus eventBus;
    public final Object originalEvent;

    public NoSubscriberEvent(EventBus eventBus2, Object originalEvent2) {
        this.eventBus = eventBus2;
        this.originalEvent = originalEvent2;
    }
}
