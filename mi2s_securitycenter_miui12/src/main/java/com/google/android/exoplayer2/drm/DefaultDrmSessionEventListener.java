package com.google.android.exoplayer2.drm;

import android.os.Handler;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public interface DefaultDrmSessionEventListener {

    public static final class EventDispatcher {
        private final CopyOnWriteArrayList<HandlerAndListener> listeners = new CopyOnWriteArrayList<>();

        private static final class HandlerAndListener {
            public final Handler handler;
            public final DefaultDrmSessionEventListener listener;

            public HandlerAndListener(Handler handler2, DefaultDrmSessionEventListener defaultDrmSessionEventListener) {
                this.handler = handler2;
                this.listener = defaultDrmSessionEventListener;
            }
        }

        public void addListener(Handler handler, DefaultDrmSessionEventListener defaultDrmSessionEventListener) {
            Assertions.checkArgument((handler == null || defaultDrmSessionEventListener == null) ? false : true);
            this.listeners.add(new HandlerAndListener(handler, defaultDrmSessionEventListener));
        }

        public void drmKeysLoaded() {
            Iterator<HandlerAndListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener next = it.next();
                final DefaultDrmSessionEventListener defaultDrmSessionEventListener = next.listener;
                next.handler.post(new Runnable() {
                    public void run() {
                        defaultDrmSessionEventListener.onDrmKeysLoaded();
                    }
                });
            }
        }

        public void drmKeysRemoved() {
            Iterator<HandlerAndListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener next = it.next();
                final DefaultDrmSessionEventListener defaultDrmSessionEventListener = next.listener;
                next.handler.post(new Runnable() {
                    public void run() {
                        defaultDrmSessionEventListener.onDrmKeysRemoved();
                    }
                });
            }
        }

        public void drmKeysRestored() {
            Iterator<HandlerAndListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener next = it.next();
                final DefaultDrmSessionEventListener defaultDrmSessionEventListener = next.listener;
                next.handler.post(new Runnable() {
                    public void run() {
                        defaultDrmSessionEventListener.onDrmKeysRestored();
                    }
                });
            }
        }

        public void drmSessionManagerError(final Exception exc) {
            Iterator<HandlerAndListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener next = it.next();
                final DefaultDrmSessionEventListener defaultDrmSessionEventListener = next.listener;
                next.handler.post(new Runnable() {
                    public void run() {
                        defaultDrmSessionEventListener.onDrmSessionManagerError(exc);
                    }
                });
            }
        }

        public void removeListener(DefaultDrmSessionEventListener defaultDrmSessionEventListener) {
            Iterator<HandlerAndListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener next = it.next();
                if (next.listener == defaultDrmSessionEventListener) {
                    this.listeners.remove(next);
                }
            }
        }
    }

    void onDrmKeysLoaded();

    void onDrmKeysRemoved();

    void onDrmKeysRestored();

    void onDrmSessionManagerError(Exception exc);
}
