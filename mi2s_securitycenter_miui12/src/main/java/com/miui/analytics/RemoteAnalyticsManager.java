package com.miui.analytics;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.stat.MiStat;
import com.xiaomi.stat.MiStatParams;
import java.util.ArrayList;

public class RemoteAnalyticsManager {
    private static final int ID_TRACK_DATA = 1001;
    private static final String TAG = "RemoteAnalyticsManager";
    private static volatile RemoteAnalyticsManager sInstance;
    private Handler mBgHandler;

    private static final class MsgArgs {
        public String eventName;
        public String packageName;
        public MiStatParams params;

        private MsgArgs() {
        }
    }

    private static final class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (message.what != 1001) {
                super.handleMessage(message);
                return;
            }
            Object obj = message.obj;
            if (obj instanceof MsgArgs) {
                MsgArgs msgArgs = (MsgArgs) obj;
                MiStat.trackEvent(RemoteAnalyticsManager.createEventName(msgArgs.packageName, msgArgs.eventName), msgArgs.params);
            }
        }
    }

    private RemoteAnalyticsManager() {
        HandlerThread handlerThread = new HandlerThread(TAG, 10);
        handlerThread.start();
        this.mBgHandler = new WorkHandler(handlerThread.getLooper());
    }

    /* access modifiers changed from: private */
    public static String createEventName(String str, String str2) {
        return (str + "_" + str2).replace(".", "_");
    }

    public static RemoteAnalyticsManager getInstance() {
        if (sInstance == null) {
            synchronized (RemoteAnalyticsManager.class) {
                if (sInstance == null) {
                    sInstance = new RemoteAnalyticsManager();
                }
            }
        }
        return sInstance;
    }

    public static void trackEvent(String str, String str2, ArrayList<String> arrayList, ArrayList<String> arrayList2) {
        if (!TextUtils.isEmpty(str2)) {
            Message obtainMessage = sInstance.mBgHandler.obtainMessage(1001);
            MsgArgs msgArgs = new MsgArgs();
            msgArgs.packageName = str;
            msgArgs.eventName = str2;
            if (arrayList != null && arrayList2 != null && arrayList.size() > 0 && arrayList.size() == arrayList2.size()) {
                MiStatParams miStatParams = new MiStatParams();
                for (int i = 0; i < arrayList.size(); i++) {
                    miStatParams.putString(arrayList.get(i), arrayList2.get(i));
                }
                msgArgs.params = miStatParams;
            }
            obtainMessage.obj = msgArgs;
            sInstance.mBgHandler.sendMessage(obtainMessage);
        }
    }
}
