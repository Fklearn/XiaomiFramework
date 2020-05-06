package com.miui.gamebooster.gbservices;

import android.service.notification.StatusBarNotification;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.service.NotificationListenerCallback;

class t extends NotificationListenerCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f4381a;

    t(x xVar) {
        this.f4381a = xVar;
    }

    public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
        if (!a.b(false) && this.f4381a.c(statusBarNotification) && C0393y.a(statusBarNotification.getPackageName(), this.f4381a.f4385a) && !"com.android.settings".equals(statusBarNotification.getPackageName())) {
            x xVar = this.f4381a;
            xVar.i = statusBarNotification;
            xVar.f.h().postDelayed(new s(this), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }

    public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
    }
}
