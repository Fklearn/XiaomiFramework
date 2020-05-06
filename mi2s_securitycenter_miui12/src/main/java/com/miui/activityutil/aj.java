package com.miui.activityutil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;

public final class aj {
    public static File a(Context context, String str) {
        File file = new File(context.getFilesDir(), a("bWFjdXRpbA=="));
        file.mkdirs();
        return new File(file, str);
    }

    public static String a(String str) {
        return new String(Base64.decode(str, 0));
    }

    private static SubscriptionInfo a() {
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(SubscriptionManager.getDefault().getDefaultVoiceSlotId());
            if (subscriptionInfoForSlot != null && subscriptionInfoForSlot.isActivated()) {
                return subscriptionInfoForSlot;
            }
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                if (subscriptionInfo.isActivated()) {
                    return subscriptionInfo;
                }
            }
        }
        return null;
    }

    public static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void a(Runnable runnable) {
        new ak(runnable).start();
    }

    private static boolean a(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1;
    }

    private static String b(String str) {
        return Base64.encodeToString(str.getBytes(), 0);
    }
}
