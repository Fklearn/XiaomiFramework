package miui.cloud.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class XBroadcast {

    public static class BroadcastResult {
        public int code;
        public String data;
        public Bundle extra;

        public BroadcastResult(int i, String str, Bundle bundle) {
            this.code = i;
            this.data = str;
            this.extra = bundle;
        }
    }

    public static BroadcastResult syncSendBroadcast(Context context, Intent intent, String str) {
        try {
            return syncSendBroadcast(context, intent, str, -1);
        } catch (TimeoutException unused) {
            throw new IllegalStateException("Never reach here. ");
        }
    }

    public static BroadcastResult syncSendBroadcast(Context context, Intent intent, String str, long j) {
        HandlerThread handlerThread = new HandlerThread(XBroadcast.class.getName());
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final XWrapper xWrapper = new XWrapper();
        context.sendOrderedBroadcast(intent, str, new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                XLogger.log("result received. ");
                XWrapper.this.set(new BroadcastResult(getResultCode(), getResultData(), getResultExtras(true)));
                countDownLatch.countDown();
            }
        }, new Handler(looper), -1, (String) null, (Bundle) null);
        if (j < 0) {
            try {
                countDownLatch.await();
            } catch (Throwable th) {
                looper.quit();
                throw th;
            }
        } else if (!countDownLatch.await(j, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
        looper.quit();
        return (BroadcastResult) xWrapper.get();
    }
}
