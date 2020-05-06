package com.miui.gamebooster.gbservices;

import android.service.notification.StatusBarNotification;
import com.miui.gamebooster.service.NotificationListenerCallback;
import com.miui.luckymoney.config.AppConstants;

/* renamed from: com.miui.gamebooster.gbservices.j  reason: case insensitive filesystem */
class C0367j extends NotificationListenerCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4358a;

    C0367j(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4358a = antiMsgAccessibilityService;
    }

    public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        if (AppConstants.Package.PACKAGE_NAME_QQ.equals(packageName) || AppConstants.Package.PACKAGE_NAME_MM.equals(packageName)) {
            this.f4358a.m.post(new C0366i(this, statusBarNotification));
        }
    }

    public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
    }
}
