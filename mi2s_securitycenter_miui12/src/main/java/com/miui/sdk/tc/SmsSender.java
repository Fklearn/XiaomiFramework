package com.miui.sdk.tc;

import android.util.Log;

public class SmsSender {
    private static IRealSmsSender sRealSmsSender;

    public interface IRealSmsSender {
        void sendTextMessage(String str, String str2, int i);
    }

    public static void sendTextMessage(String str, String str2, int i) {
        Log.i("SmsSender", String.format("addr:%s, text:%s, slotId:%d", new Object[]{str, str2, Integer.valueOf(i)}));
        IRealSmsSender iRealSmsSender = sRealSmsSender;
        if (iRealSmsSender != null) {
            iRealSmsSender.sendTextMessage(str, str2, i);
        }
    }

    public static void setRealSmsSender(IRealSmsSender iRealSmsSender) {
        sRealSmsSender = iRealSmsSender;
    }
}
