package com.android.server.am;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.miui.R;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ProgressReporter;
import com.android.server.UiThread;
import com.android.server.pm.DumpState;
import java.util.List;

public abstract class PreBootBroadcaster extends IIntentReceiver.Stub {
    private static final int MSG_HIDE = 2;
    private static final int MSG_SHOW = 1;
    private static final String TAG = "PreBootBroadcaster";
    private Handler mHandler = new Handler(UiThread.get().getLooper(), (Handler.Callback) null, true) {
        public void handleMessage(Message msg) {
            PendingIntent contentIntent;
            Message message = msg;
            Context context = PreBootBroadcaster.this.mService.mContext;
            NotificationManager notifManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            int max = message.arg1;
            int index = message.arg2;
            int i = message.what;
            if (i == 1) {
                CharSequence title = context.getText(R.string.miui_upgrading_notification_title);
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
                intent.putExtra("android.intent.extra.TEXT", "help_url_upgrading");
                if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                    contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                } else {
                    contentIntent = null;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(max == 0 ? 0 : (index * 100) / max);
                sb.append("%");
                notifManager.notifyAsUser(PreBootBroadcaster.TAG, 13, new Notification.Builder(PreBootBroadcaster.this.mService.mContext, SystemNotificationChannels.UPDATES).setSmallIcon(17303570).setWhen(0).setOngoing(true).setTicker(title).setColor(context.getColor(17170460)).setContentText(sb.toString()).setContentTitle(title).setContentIntent(contentIntent).setVisibility(1).setProgress(max, index, false).build(), UserHandle.of(PreBootBroadcaster.this.mUserId));
            } else if (i == 2) {
                notifManager.cancelAsUser(PreBootBroadcaster.TAG, 13, UserHandle.of(PreBootBroadcaster.this.mUserId));
            }
        }
    };
    private int mIndex = 0;
    private final Intent mIntent;
    private final ProgressReporter mProgress;
    private final boolean mQuiet;
    /* access modifiers changed from: private */
    public final ActivityManagerService mService;
    private final List<ResolveInfo> mTargets;
    /* access modifiers changed from: private */
    public final int mUserId;

    public abstract void onFinished();

    public PreBootBroadcaster(ActivityManagerService service, int userId, ProgressReporter progress, boolean quiet) {
        this.mService = service;
        this.mUserId = userId;
        this.mProgress = progress;
        this.mQuiet = quiet;
        this.mIntent = new Intent("android.intent.action.PRE_BOOT_COMPLETED");
        this.mIntent.addFlags(33554688);
        this.mTargets = this.mService.mContext.getPackageManager().queryBroadcastReceiversAsUser(this.mIntent, DumpState.DUMP_DEXOPT, UserHandle.of(userId));
    }

    public void sendNext() {
        ActivityManagerService activityManagerService;
        if (this.mIndex >= this.mTargets.size()) {
            onFinished();
        } else if (!this.mService.isUserRunning(this.mUserId, 0)) {
            Slog.i(TAG, "User " + this.mUserId + " is no longer running; skipping remaining receivers");
            this.mHandler.obtainMessage(2).sendToTarget();
            onFinished();
        } else {
            List<ResolveInfo> list = this.mTargets;
            int i = this.mIndex;
            this.mIndex = i + 1;
            ResolveInfo ri = list.get(i);
            ComponentName componentName = ri.activityInfo.getComponentName();
            if (this.mProgress != null) {
                CharSequence label = ri.activityInfo.loadLabel(this.mService.mContext.getPackageManager());
                this.mProgress.setProgress(this.mIndex, this.mTargets.size(), this.mService.mContext.getString(17039502, new Object[]{label}));
            }
            Slog.i(TAG, "Pre-boot of " + componentName.toShortString() + " for user " + this.mUserId);
            EventLogTags.writeAmPreBoot(this.mUserId, componentName.getPackageName());
            this.mIntent.setComponent(componentName);
            ActivityManagerService activityManagerService2 = this.mService;
            synchronized (activityManagerService2) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    activityManagerService = activityManagerService2;
                    ComponentName componentName2 = componentName;
                    ResolveInfo resolveInfo = ri;
                    try {
                        this.mService.broadcastIntentLocked((ProcessRecord) null, (String) null, this.mIntent, (String) null, this, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), this.mUserId);
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        th = th;
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    activityManagerService = activityManagerService2;
                    ComponentName componentName3 = componentName;
                    ResolveInfo resolveInfo2 = ri;
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        sendNext();
    }
}
