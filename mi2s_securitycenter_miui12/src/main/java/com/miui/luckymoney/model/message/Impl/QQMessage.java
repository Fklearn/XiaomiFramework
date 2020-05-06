package com.miui.luckymoney.model.message.Impl;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import b.b.o.g.c;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.model.Notification;
import com.miui.luckymoney.model.message.AppMessage;
import com.miui.luckymoney.utils.PackageUtil;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QQMessage implements AppMessage, Serializable {
    public static final String KEY_CONVERSATION_ID = "uin";
    public static final String KEY_CONVERSATION_NAME = "uinname";
    public static final String KEY_CONVERSATION_TYPE = "uintype";
    public static final String LUCKY_MONEY_KEYWORD = "[QQ红包]";
    public static final String PATTERN_MESSAGE = "^(.*?):\\s*(.*)";
    public static final int TYPE_DISCUSS_GROUP = 3000;
    public static final int TYPE_PERSISTENT_GROUP = 1;
    public static final int TYPE_UNKNOWN = -1;
    private static final long serialVersionUID = -8988492431073638830L;
    public String conversationId = "";
    public String conversationName = "";
    public String from = "";
    private final Notification mNotification;
    private final Pattern mPatternMessage = Pattern.compile(PATTERN_MESSAGE);
    public String message = "";
    public int notificationId;
    public String notificationTag;
    public PendingIntent pendingIntent;
    public long receivedTime;
    public boolean treatedAsGroupMessage = false;
    public int type = -1;

    public QQMessage(Context context, Notification notification) {
        Bundle extras;
        this.mNotification = notification;
        this.notificationId = notification.id;
        this.notificationTag = notification.tag;
        this.receivedTime = notification.notification.when;
        this.message = notification.getNotificationContent();
        if (this.message == null) {
            this.message = "";
        }
        Matcher matcher = this.mPatternMessage.matcher(this.message);
        if (matcher.find()) {
            this.from = matcher.group(1);
            if (this.from == null) {
                this.from = "";
            }
            this.message = matcher.group(2);
            if (this.message == null) {
                this.message = "";
            }
        }
        this.pendingIntent = notification.notification.contentIntent;
        c.a a2 = c.a.a((Object) this.pendingIntent);
        a2.a("getIntent", (Class<?>[]) null, new Object[0]);
        Intent intent = (Intent) a2.d();
        if (!(intent == null || (extras = intent.getExtras()) == null)) {
            this.type = extras.getInt(KEY_CONVERSATION_TYPE, -1);
            this.conversationId = extras.getString(KEY_CONVERSATION_ID, "");
            if (!TextUtils.isEmpty(this.conversationId)) {
                this.conversationId = this.conversationId.trim();
            }
            this.conversationName = extras.getString(KEY_CONVERSATION_NAME, "");
            if (!TextUtils.isEmpty(this.conversationName)) {
                this.conversationName = this.conversationName.trim();
            }
        }
        if (TextUtils.isEmpty(this.conversationName)) {
            this.conversationName = notification.getNotificationTitle();
        }
        if (TextUtils.isEmpty(this.conversationName)) {
            this.conversationName = PackageUtil.getAppName(context, AppConstants.Package.PACKAGE_NAME_QQ);
        }
    }

    public PendingIntent getAction() {
        return this.pendingIntent;
    }

    public String getId() {
        StringBuilder sb;
        String str;
        if (!TextUtils.isEmpty(this.conversationId)) {
            sb = new StringBuilder();
            sb.append("tencentqq_");
            str = this.conversationId;
        } else {
            sb = new StringBuilder();
            sb.append("tencentqq_");
            str = this.conversationName;
        }
        sb.append(str);
        return sb.toString();
    }

    public String getName() {
        return this.conversationName;
    }

    public Notification getNotification() {
        return this.mNotification;
    }

    public String getWarningPackageName() {
        return AppConstants.Package.PACKAGE_NAME_QQ;
    }

    public boolean isBusinessMessage() {
        return false;
    }

    public boolean isGroupMessage() {
        int i = this.type;
        if (1 == i || 3000 == i) {
            return true;
        }
        return this.treatedAsGroupMessage;
    }

    public boolean isHongbao() {
        return !TextUtils.isEmpty(this.message) && this.message.contains(LUCKY_MONEY_KEYWORD);
    }

    public String toString() {
        return "coversation:" + this.conversationName + "\nmessage:" + this.message + "\nfrom:" + this.from + "\nwhen:" + this.receivedTime + "\nisLucky:" + isHongbao() + "\nisGroup:" + isGroupMessage() + "\nconversationId:" + this.conversationId;
    }
}
