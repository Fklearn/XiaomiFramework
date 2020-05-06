package com.miui.powercenter.powerui;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7157a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Uri f7158b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f7159c;

    j(Context context, Uri uri, int i) {
        this.f7157a = context;
        this.f7158b = uri;
        this.f7159c = i;
    }

    public void run() {
        try {
            Ringtone ringtone = RingtoneManager.getRingtone(this.f7157a, this.f7158b);
            if (ringtone != null) {
                if (this.f7159c >= 0) {
                    ringtone.setStreamType(this.f7159c);
                }
                ringtone.play();
            }
        } catch (Exception e) {
            Log.e("PowerNoticeUtils", "error playing ringtone " + this.f7158b, e);
        }
    }
}
