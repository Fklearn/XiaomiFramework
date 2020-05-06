package com.miui.permcenter;

import android.service.notification.StatusBarNotification;
import com.miui.gamebooster.service.NotificationListenerCallback;

class h extends NotificationListenerCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f6109a;

    h(j jVar) {
        this.f6109a = jVar;
    }

    public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
        this.f6109a.e.post(new g(this, statusBarNotification));
        if (this.f6109a.f != null) {
            this.f6109a.f.b(statusBarNotification);
        }
    }

    public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
        if (this.f6109a.f != null) {
            this.f6109a.f.a(statusBarNotification);
        }
    }
}
