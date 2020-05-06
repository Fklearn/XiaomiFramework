package de.greenrobot.event;

public final class SubscriberExceptionEvent {
    public final Object causingEvent;
    public final Object causingSubscriber;
    public final EventBus eventBus;
    public final Throwable throwable;

    public SubscriberExceptionEvent(EventBus eventBus2, Throwable throwable2, Object causingEvent2, Object causingSubscriber2) {
        this.eventBus = eventBus2;
        this.throwable = throwable2;
        this.causingEvent = causingEvent2;
        this.causingSubscriber = causingSubscriber2;
    }
}
