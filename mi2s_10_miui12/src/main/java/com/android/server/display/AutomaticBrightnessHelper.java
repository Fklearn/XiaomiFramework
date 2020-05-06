package com.android.server.display;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.server.MiuiBgThread;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.BrightnessEvent;

public class AutomaticBrightnessHelper {
    private static final int MSG_REPORT_BRIGHTNESS_EVENT = 0;
    private static final String TAG = "AutomaticBrightnessHelper";
    private static BrightnessEventCallBack sBrightnessCallback;
    private static Handler sHandler;

    public interface BrightnessEventCallBack {
        void onBrightnessEvent(int i, float f, float f2);
    }

    public static void init(BrightnessEventCallBack callBack) {
        sBrightnessCallback = callBack;
        sHandler = new H(MiuiBgThread.getHandler().getLooper());
    }

    public static void recordBrightnessEvent(BrightnessEvent event) {
        Handler handler = sHandler;
        if (handler != null) {
            handler.obtainMessage(0, event).sendToTarget();
        }
    }

    public static void onBrightnessModeChanged(int eventType, float oldValue, float newValue) {
        BrightnessEventCallBack brightnessEventCallBack = sBrightnessCallback;
        if (brightnessEventCallBack != null) {
            brightnessEventCallBack.onBrightnessEvent(eventType, oldValue, newValue);
        }
    }

    public static void onBrightnessOverrideFromWindow(int eventType, float oldValue, float newValue) {
        BrightnessEventCallBack brightnessEventCallBack = sBrightnessCallback;
        if (brightnessEventCallBack != null) {
            brightnessEventCallBack.onBrightnessEvent(eventType, oldValue, newValue);
        }
    }

    private static class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                AutomaticBrightnessHelper.report((BrightnessEvent) msg.obj);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void report(BrightnessEvent event) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);
        MQSEventManagerDelegate.getInstance().reportBrightnessEvent(bundle);
    }
}
