package com.miui.luckymoney.model.message.Impl;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import b.b.c.c.d;
import b.b.o.g.c;
import com.miui.luckymoney.model.Notification;
import com.miui.luckymoney.model.message.AppMessage;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.securitycenter.R;
import java.io.Serializable;

public class BusinessMessage implements AppMessage, Serializable {
    public static final String[] PATTERN_MESSAGE = {"您在中秋现金红包", "现金红包月月送", "米粉过年大狂欢", "元宵现金红包大派送"};
    private static final long serialVersionUID = -8988492431073638830L;
    public String conversationId = "";
    public String conversationName = "";
    public String from = "";
    private boolean isLuckMoney;
    private final Notification mNotification;
    public String message = "";
    public int notificationId;
    public String notificationTag;
    private PendingIntent pendingIntent;
    public long receivedTime;

    public BusinessMessage(Context context, Notification notification) {
        boolean z = false;
        this.isLuckMoney = false;
        this.mNotification = notification;
        this.notificationId = notification.id;
        this.notificationTag = notification.tag;
        this.receivedTime = notification.notification.when;
        this.message = notification.getNotificationContent();
        this.pendingIntent = notification.notification.contentIntent;
        c.a a2 = c.a.a((Object) this.pendingIntent);
        a2.a("getIntent", (Class<?>[]) null, new Object[0]);
        Intent intent = (Intent) a2.d();
        if (!(intent == null || this.message == null)) {
            ComponentName component = intent.getComponent();
            if (component != null) {
                String packageName = component.getPackageName();
                z = (!TextUtils.isEmpty(packageName) && "com.mipay.wallet".equals(packageName) && stringContains(this.message, PATTERN_MESSAGE)) ? true : z;
            }
            this.isLuckMoney = z;
        }
        this.conversationName = notification.getNotificationTitle();
        if (TextUtils.isEmpty(this.conversationName)) {
            this.conversationName = PackageUtil.getAppName(context, "com.mipay.wallet");
        }
    }

    private boolean stringContains(String str, String[] strArr) {
        for (String contains : strArr) {
            if (str.contains(contains)) {
                return true;
            }
        }
        return false;
    }

    public PendingIntent getAction() {
        return this.pendingIntent;
    }

    public String getId() {
        StringBuilder sb;
        String str;
        if (!TextUtils.isEmpty(this.conversationId)) {
            sb = new StringBuilder();
            sb.append("business_");
            str = this.conversationId;
        } else {
            sb = new StringBuilder();
            sb.append("business_");
            str = this.conversationName;
        }
        sb.append(str);
        return sb.toString();
    }

    public String getName() {
        return d.a().getString(R.string.mi_pay_lucky);
    }

    public Notification getNotification() {
        return this.mNotification;
    }

    public String getWarningPackageName() {
        return "com.mipay.wallet";
    }

    public boolean isBusinessMessage() {
        return true;
    }

    public boolean isGroupMessage() {
        return false;
    }

    public boolean isHongbao() {
        return this.isLuckMoney;
    }

    public String toString() {
        return "conversationName:" + this.conversationName + "\nmessage:" + this.message + "\nfromWhen:" + this.from + "\nwhen:" + this.receivedTime + "\nisLucky:" + isHongbao() + "\nisGroup:" + isGroupMessage() + "\nconversationId:" + this.conversationId;
    }
}
