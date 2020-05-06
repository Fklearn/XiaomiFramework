package com.android.server.accessibility;

abstract class BaseEventStreamTransformation implements EventStreamTransformation {
    private EventStreamTransformation mNext;

    BaseEventStreamTransformation() {
    }

    public void setNext(EventStreamTransformation next) {
        this.mNext = next;
    }

    public EventStreamTransformation getNext() {
        return this.mNext;
    }
}
