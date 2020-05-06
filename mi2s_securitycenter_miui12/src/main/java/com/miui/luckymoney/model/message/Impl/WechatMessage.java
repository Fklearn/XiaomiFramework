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

public class WechatMessage implements AppMessage, Serializable {
    private static final String AA_MESSAGE_PATTERN = "^(.*?:)?\\s*\\[AA.*?\\]";
    private static final String GROUP_CHAT_KEYWORD = "@chatroom";
    private static final String KEY_MESSAGE_CONVERSATION_ID = "Main_User";
    private static final String KEY_MESSAGE_TYPE = "MainUI_User_Last_Msg_Type";
    private static final String LUCKY_MONEY_KEYWORD = "[微信红包]";
    private static final String LUCK_MONEY_SHORT_KEYWORD = "红包";
    public static final String PATTERN_MESSAGE = "^(\\[(.*?)条\\])?(.*?):(.*)";
    private static final String PAYMENT_MESSAGE_PATTERN = "^\\s*\\[群收款\\]";
    private static final int TYPE_LUCKY_MONEY = 436207665;
    private static final int TYPE_UNKNOWN = -1;
    private static final long serialVersionUID = -8988492431073638830L;
    public String conversation;
    public String from = "";
    private final Pattern mAAMessagePattern = Pattern.compile(AA_MESSAGE_PATTERN);
    private final Notification mNotification;
    private final Pattern mPatternMessage = Pattern.compile(PATTERN_MESSAGE);
    private final Pattern mPaymentMessagePattern = Pattern.compile(PAYMENT_MESSAGE_PATTERN);
    public String message;
    public int notificationId;
    public String notificationTag;
    public PendingIntent pendingIntent;
    public long receivedTime;
    public int type = -1;
    public String wechatId = "";

    public WechatMessage(Context context, Notification notification) {
        Bundle extras;
        this.mNotification = notification;
        this.notificationId = notification.id;
        this.notificationTag = notification.tag;
        this.conversation = notification.getNotificationTitle();
        if (TextUtils.isEmpty(this.conversation)) {
            this.conversation = PackageUtil.getAppName(context, AppConstants.Package.PACKAGE_NAME_MM);
        }
        this.receivedTime = notification.notification.when;
        this.message = notification.getNotificationContent();
        if (this.message == null) {
            this.message = "";
        }
        Matcher matcher = this.mPatternMessage.matcher(this.message);
        if (matcher.find()) {
            this.from = matcher.group(3);
            if (this.from == null) {
                this.from = "";
            }
            this.message = matcher.group(4);
            if (this.message == null) {
                this.message = "";
            }
        }
        this.pendingIntent = notification.notification.contentIntent;
        c.a a2 = c.a.a((Object) this.pendingIntent);
        a2.a("getIntent", (Class<?>[]) null, new Object[0]);
        Intent intent = (Intent) a2.d();
        if (intent != null && (extras = intent.getExtras()) != null) {
            this.type = extras.getInt(KEY_MESSAGE_TYPE);
            this.wechatId = extras.getString(KEY_MESSAGE_CONVERSATION_ID, "");
            String str = this.wechatId;
            if (str != null) {
                this.wechatId = str.trim();
            }
        }
    }

    public PendingIntent getAction() {
        return this.pendingIntent;
    }

    public String getId() {
        return TextUtils.isEmpty(this.wechatId) ? this.conversation : this.wechatId;
    }

    public String getName() {
        return this.conversation;
    }

    public Notification getNotification() {
        return this.mNotification;
    }

    public String getWarningPackageName() {
        return AppConstants.Package.PACKAGE_NAME_MM;
    }

    public boolean isBusinessMessage() {
        return false;
    }

    public boolean isGroupMessage() {
        return TextUtils.isEmpty(this.wechatId) ? !this.conversation.equals(this.from) : this.wechatId.contains(GROUP_CHAT_KEYWORD);
    }

    public boolean isHongbao() {
        int i = this.type;
        if (i == -1) {
            return this.message.contains(LUCKY_MONEY_KEYWORD);
        }
        if (i != TYPE_LUCKY_MONEY || this.mPaymentMessagePattern.matcher(this.message).find()) {
            return false;
        }
        if (this.message.contains(LUCK_MONEY_SHORT_KEYWORD)) {
            return true;
        }
        if (this.mAAMessagePattern.matcher(this.message).find()) {
        }
        return false;
    }

    public String toString() {
        return "coversation:" + this.conversation + "\nmessage:" + this.message + "\nfrom:" + this.from + "\nwhen:" + this.receivedTime + "\nisLucky:" + isHongbao() + "\nisGroup:" + isGroupMessage() + "\nwechatId:" + this.wechatId;
    }
}
