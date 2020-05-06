package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.Activity;
import android.widget.Toast;
import com.miui.permcenter.n;
import com.miui.permcenter.privacymanager.a.a;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;

class m implements n.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f6451a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a f6452b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity.i f6453c;

    m(Activity activity, a aVar, PrivacyDetailActivity.i iVar) {
        this.f6451a = activity;
        this.f6452b = aVar;
        this.f6453c = iVar;
    }

    public void a(String str, int i) {
        Activity activity = this.f6451a;
        Toast.makeText(activity, activity.getString(((Integer) o.o.get(i)).intValue(), new Object[]{this.f6452b.j()}), 0).show();
        this.f6453c.a(Long.valueOf(this.f6452b.i()), i);
    }
}
