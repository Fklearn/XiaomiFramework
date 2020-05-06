package de.greenrobot.event.util;

import android.app.Activity;
import android.util.Log;
import de.greenrobot.event.EventBus;
import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncExecutor {
    /* access modifiers changed from: private */
    public final EventBus eventBus;
    /* access modifiers changed from: private */
    public final Constructor<?> failureEventConstructor;
    /* access modifiers changed from: private */
    public final Object scope;
    private final Executor threadPool;

    public interface RunnableEx {
        void run() throws Exception;
    }

    public static class Builder {
        private EventBus eventBus;
        private Class<?> failureEventType;
        private Executor threadPool;

        private Builder() {
        }

        public Builder threadPool(Executor threadPool2) {
            this.threadPool = threadPool2;
            return this;
        }

        public Builder failureEventType(Class<?> failureEventType2) {
            this.failureEventType = failureEventType2;
            return this;
        }

        public Builder eventBus(EventBus eventBus2) {
            this.eventBus = eventBus2;
            return this;
        }

        public AsyncExecutor build() {
            return buildForScope((Object) null);
        }

        public AsyncExecutor buildForActivityScope(Activity activity) {
            return buildForScope(activity.getClass());
        }

        public AsyncExecutor buildForScope(Object executionContext) {
            if (this.eventBus == null) {
                this.eventBus = EventBus.getDefault();
            }
            if (this.threadPool == null) {
                this.threadPool = Executors.newCachedThreadPool();
            }
            if (this.failureEventType == null) {
                this.failureEventType = ThrowableFailureEvent.class;
            }
            return new AsyncExecutor(this.threadPool, this.eventBus, this.failureEventType, executionContext);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AsyncExecutor create() {
        return new Builder().build();
    }

    private AsyncExecutor(Executor threadPool2, EventBus eventBus2, Class<?> failureEventType, Object scope2) {
        this.threadPool = threadPool2;
        this.eventBus = eventBus2;
        this.scope = scope2;
        try {
            this.failureEventConstructor = failureEventType.getConstructor(new Class[]{Throwable.class});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failure event class must have a constructor with one parameter of type Throwable", e);
        }
    }

    public void execute(final RunnableEx runnable) {
        this.threadPool.execute(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    try {
                        Object event = AsyncExecutor.this.failureEventConstructor.newInstance(new Object[]{e});
                        if (event instanceof HasExecutionScope) {
                            ((HasExecutionScope) event).setExecutionScope(AsyncExecutor.this.scope);
                        }
                        AsyncExecutor.this.eventBus.post(event);
                    } catch (Exception e1) {
                        Log.e(EventBus.TAG, "Original exception:", e);
                        throw new RuntimeException("Could not create failure event", e1);
                    }
                }
            }
        });
    }
}
