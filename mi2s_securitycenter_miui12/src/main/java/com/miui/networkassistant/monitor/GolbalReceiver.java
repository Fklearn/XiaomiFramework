package com.miui.networkassistant.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.networkassistant.monitor.impl.BootMonitor;
import com.miui.networkassistant.monitor.impl.ImsiMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GolbalReceiver extends BroadcastReceiver {
    private static final String TAG = "GolbalReceiver";
    private static HashMap<String, ArrayList<IMonitor>> sMonitorMap = new HashMap<>();

    static {
        new BootMonitor().register();
        new ImsiMonitor().register();
    }

    public static synchronized void addMonitor(String str, IMonitor iMonitor) {
        synchronized (GolbalReceiver.class) {
            ArrayList arrayList = sMonitorMap.get(str);
            if (arrayList == null) {
                arrayList = new ArrayList();
                sMonitorMap.put(str, arrayList);
            }
            if (!arrayList.contains(iMonitor)) {
                arrayList.add(iMonitor);
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, String.format("onReceive, action:%s", new Object[]{action}));
        ArrayList arrayList = sMonitorMap.get(action);
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((IMonitor) it.next()).invoke(context, intent);
            }
        }
    }
}
