package com.miui.luckymoney.model.message;

import android.app.PendingIntent;
import com.miui.luckymoney.model.Notification;

public interface AppMessage {
    PendingIntent getAction();

    String getId();

    String getName();

    Notification getNotification();

    String getWarningPackageName();

    boolean isBusinessMessage();

    boolean isGroupMessage();

    boolean isHongbao();
}
