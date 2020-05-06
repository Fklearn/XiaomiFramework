package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.server.SystemService;
import miui.util.FeatureParser;

public class PaperModeService extends SystemService {
    private static final String TAG = "PaperModeService";
    /* access modifiers changed from: private */
    public static final Uri URI_PAPER_MODE_ENABLE = Settings.System.getUriFor("screen_paper_mode_enabled");
    private static final Uri URI_PAPER_MODE_TIME_ENABLE = Settings.System.getUriFor("screen_paper_mode_time_enabled");
    private Context mContext;
    private Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public boolean mPaperModeEnabled;
    /* access modifiers changed from: private */
    public boolean mPaperModeTimeEnabled;
    private SettingsObserver mSettingsObserver;

    public PaperModeService(Context context) {
        super(context);
        this.mContext = context;
    }

    public void onStart() {
    }

    public void onBootPhase(int phase) {
        if (phase == 1000) {
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            this.mContext.getContentResolver().registerContentObserver(URI_PAPER_MODE_ENABLE, false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(URI_PAPER_MODE_TIME_ENABLE, false, this.mSettingsObserver, -1);
            this.mContext.registerReceiver(new UserSwitchReceiver(), new IntentFilter("android.intent.action.USER_SWITCHED"));
            this.mContext.registerReceiver(new ScreenOnReceiver(), new IntentFilter("android.intent.action.SCREEN_ON"));
            updateSettings();
            if (!initColorService(this.mContext)) {
                updatePaperMode();
            }
        }
    }

    private boolean initColorService(Context context) {
        if (FeatureParser.getBoolean("is_xiaomi", true) || ServiceManager.getService("com.qti.snapdragon.sdk.display.IColorService") != null) {
            if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_paper_mode", 1) == 2) {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_enabled", 0, -2);
                this.mContext.getContentResolver().delete(Settings.System.getUriFor("screen_paper_mode"), (String) null, (String[]) null);
            }
            return false;
        }
        Intent intent = new Intent();
        intent.setClassName("com.qti.service.colorservice", "com.qti.service.colorservice.ColorServiceApp");
        return context.bindService(intent, new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                PaperModeService.this.updatePaperMode();
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        }, 1);
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            PaperModeService.this.updateSettings();
            if (PaperModeService.URI_PAPER_MODE_ENABLE.equals(uri)) {
                PaperModeService.this.updatePaperMode();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSettings() {
        boolean z = false;
        this.mPaperModeEnabled = Settings.System.getIntForUser(getContext().getContentResolver(), "screen_paper_mode_enabled", 0, -2) != 0;
        if (Settings.System.getIntForUser(getContext().getContentResolver(), "screen_paper_mode_time_enabled", 0, -2) != 0) {
            z = true;
        }
        this.mPaperModeTimeEnabled = z;
    }

    private class UserSwitchReceiver extends BroadcastReceiver {
        private UserSwitchReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            PaperModeService.this.updateSettings();
            PaperModeService.this.updatePaperMode();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        private ScreenOnReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (PaperModeService.this.mPaperModeTimeEnabled) {
                MiuiSettings.ScreenEffect.setScreenPaperMode(PaperModeService.this.mPaperModeEnabled);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updatePaperMode() {
        MiuiSettings.ScreenEffect.setScreenPaperMode(this.mPaperModeEnabled);
    }
}
