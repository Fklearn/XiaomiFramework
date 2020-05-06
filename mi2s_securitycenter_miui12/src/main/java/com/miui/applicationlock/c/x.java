package com.miui.applicationlock.c;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import com.xiaomi.ad.feedback.IAdFeedbackService;
import java.util.List;

class x implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAdFeedbackListener f3331a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3332b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f3333c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ List f3334d;
    final /* synthetic */ y e;

    x(y yVar, IAdFeedbackListener iAdFeedbackListener, String str, String str2, List list) {
        this.e = yVar;
        this.f3331a = iAdFeedbackListener;
        this.f3332b = str;
        this.f3333c = str2;
        this.f3334d = list;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            IAdFeedbackService unused = this.e.f3337c = IAdFeedbackService.Stub.asInterface(iBinder);
            if (this.e.e) {
                boolean unused2 = this.e.e = false;
                this.e.f3337c.showFeedbackWindowAndTrackResultForMultiAds(this.f3331a, this.f3332b, this.f3333c, this.f3334d);
            }
        } catch (Exception e2) {
            Log.w(y.f3335a, "service connected exception", e2);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IAdFeedbackService unused = this.e.f3337c = null;
    }
}
