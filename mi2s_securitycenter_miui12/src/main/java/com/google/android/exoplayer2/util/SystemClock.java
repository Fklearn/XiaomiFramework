package com.google.android.exoplayer2.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

final class SystemClock implements Clock {
    SystemClock() {
    }

    public HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback) {
        return new SystemHandlerWrapper(new Handler(looper, callback));
    }

    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }

    public void sleep(long j) {
        android.os.SystemClock.sleep(j);
    }

    public long uptimeMillis() {
        return android.os.SystemClock.uptimeMillis();
    }
}
