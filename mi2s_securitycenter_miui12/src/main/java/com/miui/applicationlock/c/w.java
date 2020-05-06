package com.miui.applicationlock.c;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import com.xiaomi.ad.feedback.IAdFeedbackService;

class w implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAdFeedbackListener f3327a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3328b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f3329c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f3330d;
    final /* synthetic */ y e;

    w(y yVar, IAdFeedbackListener iAdFeedbackListener, String str, String str2, String str3) {
        this.e = yVar;
        this.f3327a = iAdFeedbackListener;
        this.f3328b = str;
        this.f3329c = str2;
        this.f3330d = str3;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            IAdFeedbackService unused = this.e.f3337c = IAdFeedbackService.Stub.asInterface(iBinder);
            this.e.f3337c.showFeedbackWindowAndTrackResult(this.f3327a, this.f3328b, this.f3329c, this.f3330d);
        } catch (Exception e2) {
            Log.w(y.f3335a, "service connected exception", e2);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IAdFeedbackService unused = this.e.f3337c = null;
    }
}
