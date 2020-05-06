package com.miui.luckymoney.utils;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.j.i;
import com.miui.luckymoney.ui.activity.OpenLockScreenActivity;
import com.miui.luckymoney.ui.view.PendingIntentRunnable;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScreenUtil {
    private static final String ACTION_REMOVE_KEYGUARD_NOTIFICATION = "com.miui.app.ExtraStatusBarManager.action_remove_keyguard_notification";
    public static final String ACTION_SHOW_MIUI_SECURE_KEYGUARD = "xiaomi.intent.action.SHOW_SECURE_KEYGUARD";
    private static final CopyOnWriteArrayList<KeyguardUnlockedListener> keyguardUnlockedListeners = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public static PendingIntent latestAction;
    /* access modifiers changed from: private */
    public static boolean registered = false;
    private static BroadcastReceiver unlockBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            boolean unused = ScreenUtil.registered = false;
            if (ScreenUtil.latestAction != null) {
                new PendingIntentRunnable(ScreenUtil.latestAction).run();
                PendingIntent unused2 = ScreenUtil.latestAction = null;
                ScreenUtil.notifyKeyguardUnlocked();
            }
        }
    };

    public interface KeyguardUnlockedListener {
        void onKeyguardUnlocked();
    }

    public static synchronized void clearKeyguardNotifications(Context context) {
        synchronized (ScreenUtil.class) {
            context.sendBroadcast(new Intent(ACTION_REMOVE_KEYGUARD_NOTIFICATION));
        }
    }

    public static synchronized boolean isKeyguardSecure(Context context) {
        boolean isKeyguardSecure;
        synchronized (ScreenUtil.class) {
            isKeyguardSecure = ((KeyguardManager) context.getSystemService("keyguard")).isKeyguardSecure();
        }
        return isKeyguardSecure;
    }

    public static synchronized boolean isScreenLocked(Context context) {
        boolean isKeyguardLocked;
        synchronized (ScreenUtil.class) {
            isKeyguardLocked = ((KeyguardManager) context.getSystemService("keyguard")).isKeyguardLocked();
        }
        return isKeyguardLocked;
    }

    public static synchronized boolean isSecureLocked(Context context) {
        boolean z;
        synchronized (ScreenUtil.class) {
            z = isScreenLocked(context) && isKeyguardSecure(context);
        }
        return z;
    }

    public static synchronized void notifyKeyguardUnlocked() {
        synchronized (ScreenUtil.class) {
            Iterator<KeyguardUnlockedListener> it = keyguardUnlockedListeners.iterator();
            while (it.hasNext()) {
                it.next().onKeyguardUnlocked();
            }
        }
    }

    public static synchronized void powerOnScreen(Context context) {
        synchronized (ScreenUtil.class) {
            PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(805306378, "hongbaoassistant");
            try {
                newWakeLock.acquire();
            } finally {
                newWakeLock.release();
            }
        }
    }

    public static synchronized void register(KeyguardUnlockedListener keyguardUnlockedListener) {
        synchronized (ScreenUtil.class) {
            if (!keyguardUnlockedListeners.contains(keyguardUnlockedListener)) {
                keyguardUnlockedListeners.add(keyguardUnlockedListener);
            }
        }
    }

    public static void setNotchToolbarMarginTop(Context context, View view) {
        int f = i.f(context) + context.getResources().getDimensionPixelSize(R.dimen.notch_toolbar_margin_top);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, f, 0, 0);
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setStatusbarMarginTop(Context context, View view) {
        int f = i.f(context);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (marginLayoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginLayoutParams.topMargin = f;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static synchronized void unlockKeyguard(Context context, PendingIntent pendingIntent) {
        synchronized (ScreenUtil.class) {
            latestAction = pendingIntent;
            unlockMiuiKeyguard(context);
        }
    }

    private static void unlockMiuiKeyguard(Context context) {
        if (isScreenLocked(context)) {
            Intent intent = new Intent();
            intent.setClass(context, OpenLockScreenActivity.class);
            intent.setFlags(268435456);
            intent.putExtra(OpenLockScreenActivity.EXTRA_ACTION_INTENT, latestAction);
            context.startActivity(intent);
        } else {
            new PendingIntentRunnable(latestAction).run();
        }
        latestAction = null;
    }

    public static void unlockSecureMiuiKeyguard(Context context, PendingIntent pendingIntent) {
        if (registered) {
            context.unregisterReceiver(unlockBroadcastReceiver);
            registered = false;
        }
        latestAction = pendingIntent;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        context.registerReceiver(unlockBroadcastReceiver, intentFilter);
        registered = true;
        context.sendBroadcast(new Intent(ACTION_SHOW_MIUI_SECURE_KEYGUARD));
    }
}
