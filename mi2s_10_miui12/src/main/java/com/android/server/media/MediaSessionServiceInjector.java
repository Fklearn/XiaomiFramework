package com.android.server.media;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

public class MediaSessionServiceInjector {
    private static final String TAG = "MediaSessionService";

    public static boolean startVoiceAssistant(Context context) {
        Intent intent = new Intent("android.intent.action.ASSIST");
        try {
            ResolveInfo info = AppGlobals.getPackageManager().resolveIntent(intent, intent.resolveTypeIfNeeded(context.getContentResolver()), 65536, 0);
            Object obj = "com.miui.voiceassist";
            Object obj2 = "com.xiaomi.voiceassistant.CTAAlertActivity";
            Object obj3 = "com.xiaomi.voiceassistant.VoiceService";
            if (info != null) {
                if (info.activityInfo != null && "com.miui.voiceassist".equals(info.activityInfo.packageName) && "com.xiaomi.voiceassistant.CTAAlertActivity".equals(info.activityInfo.name)) {
                    intent.putExtra("voice_assist_start_from_key", "headset");
                    intent.setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.VoiceService");
                    context.startForegroundServiceAsUser(intent, UserHandle.CURRENT);
                    return true;
                }
            }
            Log.i(TAG, "startVoiceAssistant can't find service");
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
        return false;
    }
}
