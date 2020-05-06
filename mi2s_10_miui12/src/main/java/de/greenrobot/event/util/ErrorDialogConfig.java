package de.greenrobot.event.util;

import android.content.res.Resources;
import android.util.Log;
import de.greenrobot.event.EventBus;

public class ErrorDialogConfig {
    int defaultDialogIconId;
    final int defaultErrorMsgId;
    Class<?> defaultEventTypeOnDialogClosed;
    final int defaultTitleId;
    EventBus eventBus;
    boolean logExceptions = true;
    final ExceptionToResourceMapping mapping;
    final Resources resources;
    String tagForLoggingExceptions;

    public ErrorDialogConfig(Resources resources2, int defaultTitleId2, int defaultMsgId) {
        this.resources = resources2;
        this.defaultTitleId = defaultTitleId2;
        this.defaultErrorMsgId = defaultMsgId;
        this.mapping = new ExceptionToResourceMapping();
    }

    public ErrorDialogConfig addMapping(Class<? extends Throwable> clazz, int msgId) {
        this.mapping.addMapping(clazz, msgId);
        return this;
    }

    public int getMessageIdForThrowable(Throwable throwable) {
        Integer resId = this.mapping.mapThrowable(throwable);
        if (resId != null) {
            return resId.intValue();
        }
        String str = EventBus.TAG;
        Log.d(str, "No specific message ressource ID found for " + throwable);
        return this.defaultErrorMsgId;
    }

    public void setDefaultDialogIconId(int defaultDialogIconId2) {
        this.defaultDialogIconId = defaultDialogIconId2;
    }

    public void setDefaultEventTypeOnDialogClosed(Class<?> defaultEventTypeOnDialogClosed2) {
        this.defaultEventTypeOnDialogClosed = defaultEventTypeOnDialogClosed2;
    }

    public void disableExceptionLogging() {
        this.logExceptions = false;
    }

    public void setTagForLoggingExceptions(String tagForLoggingExceptions2) {
        this.tagForLoggingExceptions = tagForLoggingExceptions2;
    }

    public void setEventBus(EventBus eventBus2) {
        this.eventBus = eventBus2;
    }

    /* access modifiers changed from: package-private */
    public EventBus getEventBus() {
        EventBus eventBus2 = this.eventBus;
        return eventBus2 != null ? eventBus2 : EventBus.getDefault();
    }
}
