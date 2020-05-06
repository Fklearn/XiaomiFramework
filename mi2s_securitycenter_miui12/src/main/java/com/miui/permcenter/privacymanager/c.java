package com.miui.permcenter.privacymanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.UserHandle;
import b.b.c.j.B;
import b.b.c.j.v;
import b.b.c.j.x;
import b.b.o.a.a;
import com.miui.permcenter.privacymanager.a.e;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.securitycenter.R;
import java.util.List;
import miui.util.Log;

public class c {
    public static Notification a(Context context, String str, String str2, e eVar, boolean z) {
        PendingIntent pendingIntent;
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon_securitycenter);
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.security_small_icon).setWhen(System.currentTimeMillis()).setLargeIcon(decodeResource).setContentTitle(str).setContentText(str2).setAutoCancel(true).setPriority(2).setShowWhen(true).setSound(Uri.EMPTY, (AudioAttributes) null);
        if (eVar != null) {
            if (eVar.f6342d == -1) {
                pendingIntent = PendingIntent.getActivity(context, 1, AppBehaviorRecordActivity.b("privacy_notification"), 134217728);
            } else {
                Intent a3 = PrivacyDetailActivity.a(eVar.b(), eVar.d(), "privacy_notification");
                a3.putExtra("privacy_guide", true);
                pendingIntent = PendingIntent.getActivity(context, Math.abs(eVar.hashCode()), a3, 134217728);
            }
            a2.setContentIntent(pendingIntent);
        }
        Notification build = a2.build();
        a.b(build, z);
        a.a(build, true);
        return build;
    }

    public static void a(Context context, e eVar, boolean z) {
        String str;
        int i;
        Object[] objArr;
        Resources resources;
        if (B.c() != UserHandle.myUserId()) {
            Log.i("BehaviorRecord-Notification", "System in: " + B.c() + ", But SecurityCenter in: " + UserHandle.myUserId());
            return;
        }
        String string = context.getResources().getString(R.string.privacy_notification_subtitle);
        if (eVar != null) {
            int i2 = eVar.f6342d;
            int i3 = 1;
            String str2 = "AuthManager@";
            if (i2 == 0) {
                str = context.getResources().getString(R.string.app_behavior_notification_title_single_group, new Object[]{x.j(context, eVar.b())});
            } else if (i2 == -1) {
                str = context.getResources().getString(R.string.app_behavior_notification_title_multi);
            } else {
                if (i2 != 2 || !o.g(eVar.a())) {
                    resources = context.getResources();
                    i = o.c(eVar.a());
                    objArr = new Object[]{x.j(context, eVar.b())};
                } else {
                    resources = context.getResources();
                    i = o.b(eVar.a());
                    objArr = new Object[]{x.j(context, eVar.b())};
                }
                str = resources.getString(i, objArr);
                i3 = eVar.hashCode();
                str2 = eVar.c();
            }
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                if (notificationManager != null) {
                    v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
                    notificationManager.notify(str2, i3, a(context, str, string, eVar, z));
                    Log.i("BehaviorRecord-Notification", "show notification, title: " + str + " , subTitle : " + string + " , floatEnable + " + z);
                }
            } catch (Exception e) {
                Log.e("BehaviorRecord-Notification", "showPrivacyUsingNotification exception: " + e);
            }
        }
    }

    public static void a(Context context, List<e> list) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null && list != null) {
            for (e next : list) {
                notificationManager.cancel(next.c(), next.hashCode());
                Log.i("BehaviorRecord-Notification", "cancelNotification: " + next.hashCode());
            }
        }
    }
}
