package com.miui.superpower.notification;

import android.service.notification.StatusBarNotification;
import com.miui.gamebooster.service.NotificationListenerCallback;

class e extends NotificationListenerCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8125a;

    e(f fVar) {
        this.f8125a = fVar;
    }

    public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
        this.f8125a.a(statusBarNotification);
    }

    public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
    }
}
