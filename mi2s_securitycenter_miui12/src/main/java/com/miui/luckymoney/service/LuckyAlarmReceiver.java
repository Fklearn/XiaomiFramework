package com.miui.luckymoney.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import b.b.c.j.B;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.config.DoNotDisturbConstants;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.view.AlarmHeadsUpView;
import com.miui.luckymoney.ui.view.AlarmLockScreenView;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;

public class LuckyAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "LuckyAlarmReceiver";
    /* access modifiers changed from: private */
    public Intent resIntent;

    public void onReceive(final Context context, Intent intent) {
        String str;
        CommonConfig instance = CommonConfig.getInstance(context);
        this.resIntent = new Intent();
        String stringExtra = intent.getStringExtra("type");
        String stringExtra2 = intent.getStringExtra("activityName");
        if (stringExtra2 == null) {
            stringExtra2 = "活动";
        }
        String str2 = TAG;
        Log.i(str2, "receive LuckyAlarm:" + stringExtra2);
        if (MijiaAlertModel.KEY_URL.equals(stringExtra)) {
            str = intent.getStringExtra(MijiaAlertModel.KEY_URL);
            if (str != null) {
                this.resIntent = new Intent("android.intent.action.VIEW", Uri.parse(str));
                this.resIntent.setFlags(268435456);
            }
        } else if ("intent".equals(stringExtra)) {
            str = intent.getStringExtra("intent");
            this.resIntent = new Intent();
            this.resIntent.setFlags(268435456);
            if (str != null) {
                this.resIntent.setComponent(ComponentName.unflattenFromString(str));
            }
        } else {
            str = "null";
        }
        String packageNameFromIntent = PackageUtil.getPackageNameFromIntent(this.resIntent);
        if (!PackageUtil.isIntentExist(context, this.resIntent, (String) null) || !instance.getXiaomiLuckyMoneyEnable() || !instance.getLuckyAlarmEnable() || !instance.getLuckyAlarmPackageOpen(packageNameFromIntent)) {
            return;
        }
        if (!instance.isDNDModeEffective() || instance.getDNDModeLevel() != DoNotDisturbConstants.DND_LEVEL_NO_EVERYTHING) {
            ScreenUtil.powerOnScreen(context);
            if (instance.getLuckyAlarmSoundEnable()) {
                NotificationUtil.playNotification(context, R.raw.lucky_alarm);
            }
            if (ScreenUtil.isScreenLocked(context)) {
                final AlarmLockScreenView alarmLockScreenView = new AlarmLockScreenView(context);
                alarmLockScreenView.setPositiveClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        alarmLockScreenView.dismiss();
                        ScreenUtil.unlockKeyguard(context, PendingIntent.getActivity(context, 0, LuckyAlarmReceiver.this.resIntent, 1073741824));
                        MiStatUtil.recordLuckyAlarmLockedNoti(LuckyAlarmReceiver.this.resIntent.toString(), true);
                        NotificationUtil.stopNotification(context, R.raw.lucky_alarm);
                    }
                });
                alarmLockScreenView.show(packageNameFromIntent, stringExtra2 + "即将开始");
                MiStatUtil.recordLuckyAlarmLockedNoti(str, false);
            } else {
                final AlarmHeadsUpView alarmHeadsUpView = new AlarmHeadsUpView(context);
                alarmHeadsUpView.setPositiveOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        context.startActivity(LuckyAlarmReceiver.this.resIntent);
                        NotificationUtil.stopNotification(context, R.raw.lucky_alarm);
                        alarmHeadsUpView.dismiss();
                        MiStatUtil.recordLuckyAlarmNoti(LuckyAlarmReceiver.this.resIntent.toString(), true);
                    }
                });
                alarmHeadsUpView.show(packageNameFromIntent, stringExtra2 + "即将开始");
                MiStatUtil.recordLuckyAlarmNoti(this.resIntent.toURI(), false);
            }
            Intent intent2 = new Intent();
            intent2.setAction(Constants.ACTION_UPDATE_ALARM_CONFIG);
            context.sendBroadcastAsUser(intent2, B.b());
        }
    }
}
