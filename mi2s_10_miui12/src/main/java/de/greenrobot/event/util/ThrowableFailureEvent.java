package de.greenrobot.event.util;

public class ThrowableFailureEvent implements HasExecutionScope {
    private Object executionContext;
    protected final boolean suppressErrorUi;
    protected final Throwable throwable;

    public ThrowableFailureEvent(Throwable throwable2) {
        this.throwable = throwable2;
        this.suppressErrorUi = false;
    }

    public ThrowableFailureEvent(Throwable throwable2, boolean suppressErrorUi2) {
        this.throwable = throwable2;
        this.suppressErrorUi = suppressErrorUi2;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public boolean isSuppressErrorUi() {
        return this.suppressErrorUi;
    }

    public Object getExecutionScope() {
        return this.executionContext;
    }

    public void setExecutionScope(Object executionContext2) {
        this.executionContext = executionContext2;
    }
}
