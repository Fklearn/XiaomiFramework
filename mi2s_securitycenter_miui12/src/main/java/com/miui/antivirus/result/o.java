package com.miui.antivirus.result;

import com.miui.antivirus.activity.MainActivity;
import com.miui.applicationlock.c.y;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;

class o extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WeakReference f2845a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ q f2846b;

    o(q qVar, WeakReference weakReference) {
        this.f2846b = qVar;
        this.f2845a = weakReference;
    }

    public void onFinished(int i) {
        MainActivity mainActivity = (MainActivity) this.f2845a.get();
        if (i != -1 && mainActivity != null) {
            this.f2846b.b(mainActivity);
            y.b().b(mainActivity.getApplicationContext());
        }
    }
}
