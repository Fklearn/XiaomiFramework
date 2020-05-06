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

public class MiTalkMessage implements AppMessage, Serializable {
    public static final String CONVERSATION_TYPE_GROUP = "group";
    public static final String KEY_CONVERSATION_ID = "conversation_id";
    public static final String KEY_MESSAGE_TYPE = "msg_type";
    public static final int MESSAGE_TYPE_LUCKY_MONKEY = 55;
    public static final int MESSAGE_TYPE_UNKNOWN = -1;
    public static final String PATTERN_MESSAGE = "^(\\[\\d+条\\])?\\s*(.*?):\\s*(\\[米聊红包\\](.*))";
    private static final long serialVersionUID = -8988492431073638830L;
    public String conversationId = "";
    public String conversationName = "";
    public String from = "";
    public boolean isGroup = false;
    public boolean isLuckMoney = false;
    private final Notification mNotification;
    private final Pattern mPatternMessage = Pattern.compile(PATTERN_MESSAGE);
    public String message = "";
    public int messageType = -1;
    public int notificationId;
    public String notificationTag;
    public PendingIntent pendingIntent;
    public long receivedTime;

    public MiTalkMessage(Context context, Notification notification) {
        Bundle extras;
        this.mNotification = notification;
        this.notificationId = notification.id;
        this.notificationTag = notification.tag;
        this.receivedTime = notification.notification.when;
        this.message = notification.getNotificationContent();
        Matcher matcher = this.mPatternMessage.matcher(this.message);
        if (matcher.find()) {
            this.isLuckMoney = true;
            this.isGroup = true;
            this.from = matcher.group(2);
            if (this.from == null) {
                this.from = "";
            }
            this.message = matcher.group(3);
            if (this.message == null) {
                this.message = "";
            }
        }
        this.pendingIntent = notification.notification.contentIntent;
        c.a a2 = c.a.a((Object) this.pendingIntent);
        a2.a("getIntent", (Class<?>[]) null, new Object[0]);
        Intent intent = (Intent) a2.d();
        if (!(intent == null || (extras = intent.getExtras()) == null)) {
            this.messageType = extras.getInt(KEY_MESSAGE_TYPE, -1);
            int i = this.messageType;
            if (i != -1) {
                this.isLuckMoney = i == 55;
            }
            this.conversationId = extras.getString(KEY_CONVERSATION_ID, "");
            if (!TextUtils.isEmpty(this.conversationId)) {
                this.conversationId = this.conversationId.trim();
                if (this.conversationId.endsWith(CONVERSATION_TYPE_GROUP)) {
                    this.isGroup = true;
                } else {
                    this.isGroup = false;
                }
            }
        }
        this.conversationName = notification.getNotificationTitle();
        if (TextUtils.isEmpty(this.conversationName)) {
            this.conversationName = PackageUtil.getAppName(context, AppConstants.Package.PACKAGE_NAME_MITALK);
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
            sb.append("mitalk_");
            str = this.conversationId;
        } else {
            sb = new StringBuilder();
            sb.append("mitalk_");
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
        return AppConstants.Package.PACKAGE_NAME_MITALK;
    }

    public boolean isBusinessMessage() {
        return false;
    }

    public boolean isGroupMessage() {
        return this.isGroup;
    }

    public boolean isHongbao() {
        return this.isLuckMoney;
    }

    public String toString() {
        return "coversation:" + this.conversationName + "\nmessage:" + this.message + "\nfrom:" + this.from + "\nwhen:" + this.receivedTime + "\nisLucky:" + isHongbao() + "\nisGroup:" + isGroupMessage() + "\nconversationId:" + this.conversationId;
    }
}
