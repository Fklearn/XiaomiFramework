package com.miui.luckymoney.service;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class PhoneStateMonitor {
    /* access modifiers changed from: private */
    public static final CopyOnWriteArrayList<PhoneStateListener> mListeners = new CopyOnWriteArrayList<>();
    private static final PhoneStateListener mMonitor = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            super.onCallStateChanged(i, str);
            Iterator it = PhoneStateMonitor.mListeners.iterator();
            while (it.hasNext()) {
                ((PhoneStateListener) it.next()).onCallStateChanged(i, str);
            }
        }
    };

    public static synchronized boolean isPhoneBusy(Context context) {
        boolean z;
        synchronized (PhoneStateMonitor.class) {
            z = ((TelephonyManager) context.getSystemService("phone")).getCallState() != 0;
        }
        return z;
    }

    public static synchronized void registerListener(PhoneStateListener phoneStateListener) {
        synchronized (PhoneStateMonitor.class) {
            if (!mListeners.contains(phoneStateListener)) {
                mListeners.add(phoneStateListener);
            }
        }
    }

    public static synchronized void startMonitor(Context context) {
        synchronized (PhoneStateMonitor.class) {
            ((TelephonyManager) context.getSystemService("phone")).listen(mMonitor, 32);
        }
    }

    public static synchronized void stopMonitor(Context context) {
        synchronized (PhoneStateMonitor.class) {
            ((TelephonyManager) context.getSystemService("phone")).listen(mMonitor, 0);
        }
    }

    public static synchronized void unregisterListener(PhoneStateListener phoneStateListener) {
        synchronized (PhoneStateMonitor.class) {
            mListeners.remove(phoneStateListener);
        }
    }
}
