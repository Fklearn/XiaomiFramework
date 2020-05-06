package com.miui.antivirus.result;

import com.miui.antivirus.activity.MainActivity;
import com.miui.applicationlock.c.y;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.antivirus.result.d  reason: case insensitive filesystem */
class C0241d extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WeakReference f2829a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0243f f2830b;

    C0241d(C0243f fVar, WeakReference weakReference) {
        this.f2830b = fVar;
        this.f2829a = weakReference;
    }

    public void onFinished(int i) {
        MainActivity mainActivity = (MainActivity) this.f2829a.get();
        if (i != -1 && mainActivity != null) {
            this.f2830b.d(mainActivity);
            y.b().b(mainActivity.getApplicationContext());
        }
    }
}
