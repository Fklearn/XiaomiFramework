package com.miui.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import b.b.o.g.e;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RemoteAnalyticsReceiver extends BroadcastReceiver {
    private static final String ACTION_REMOTE_ANALYTICS = "com.miui.security.remote.analytics";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_EVENT_PARAM = "event_param";
    private static final String KEY_EVENT_VALUE = "event_value";

    public void onReceive(Context context, Intent intent) {
        if (ACTION_REMOTE_ANALYTICS.equals(intent.getAction())) {
            String str = null;
            try {
                str = (String) e.a((Object) intent, String.class, "getSender", (Class<?>[]) null, new Object[0]);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
            if (!TextUtils.isEmpty(str)) {
                String stringExtra = intent.getStringExtra(KEY_EVENT_NAME);
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(KEY_EVENT_PARAM);
                ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra(KEY_EVENT_VALUE);
                RemoteAnalyticsManager.getInstance();
                RemoteAnalyticsManager.trackEvent(str, stringExtra, stringArrayListExtra, stringArrayListExtra2);
            }
        }
    }
}
