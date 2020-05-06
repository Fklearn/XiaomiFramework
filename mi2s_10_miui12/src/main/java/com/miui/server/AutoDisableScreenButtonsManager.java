package com.miui.server;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.miui.R;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.miui.enterprise.RestrictionsHelper;
import java.util.List;
import miui.app.AlertDialog;
import miui.securityspace.CrossUserUtils;
import miui.util.AutoDisableScreenButtonsHelper;
import miui.util.SmartCoverManager;
import miui.view.AutoDisableScreenbuttonsFloatView;

public class AutoDisableScreenButtonsManager {
    private static final int ENABLE_KEY_PRESS_INTERVAL = 2000;
    private static final String PREF_ADSB_NOT_SHOW_PROMPTS = "ADSB_NOT_SHOW_PROMPTS";
    private static final ComponentName SettingsActionComponent = ComponentName.unflattenFromString("com.android.settings/.AutoDisableScreenButtonsAppListSettingsActivity");
    private static final String TAG = "AutoDisableScreenButtonsManager";
    private static final int TMP_DISABLE_BUTTON = 2;
    private static AutoDisableScreenButtonsManager sAutoDisableScreenButtonsManager;
    private String mCloudConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mCurUserId = 0;
    /* access modifiers changed from: private */
    public AutoDisableScreenbuttonsFloatView mFloatView;
    private Handler mHandler = new Handler();
    private final Object mLock = new Object();
    private int mScreenButtonPressedKeyCode;
    private long mScreenButtonPressedTime;
    private boolean mScreenButtonsDisabled;
    /* access modifiers changed from: private */
    public boolean mScreenButtonsTmpDisabled;
    /* access modifiers changed from: private */
    public boolean mStatusBarVisibleOld = true;
    private long mToastShowTime;
    private boolean mTwice = false;
    private String mUserSetting;

    public AutoDisableScreenButtonsManager(Context context, Handler handler) {
        this.mContext = context;
        resetButtonsStatus();
        new DisableButtonsSettingsObserver(handler).observe();
        sAutoDisableScreenButtonsManager = this;
    }

    public void onStatusBarVisibilityChange(final boolean visible) {
        if (visible != this.mStatusBarVisibleOld) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (visible) {
                        if (AutoDisableScreenButtonsManager.this.mScreenButtonsTmpDisabled) {
                            AutoDisableScreenButtonsManager.this.saveTmpDisableButtonsStatus(false);
                        }
                        if (AutoDisableScreenButtonsManager.this.mFloatView != null) {
                            AutoDisableScreenButtonsManager.this.mFloatView.dismiss();
                        }
                    } else {
                        ComponentName cn = AutoDisableScreenButtonsManager.getRunningTopActivity(AutoDisableScreenButtonsManager.this.mContext);
                        if (cn != null) {
                            int flag = AutoDisableScreenButtonsHelper.getAppFlag(AutoDisableScreenButtonsManager.this.mContext, cn.getPackageName());
                            if (flag == 2) {
                                AutoDisableScreenButtonsManager.this.saveTmpDisableButtonsStatus(true);
                            } else if (flag == 1) {
                                AutoDisableScreenButtonsManager.this.showFloat();
                            }
                        }
                    }
                    boolean unused = AutoDisableScreenButtonsManager.this.mStatusBarVisibleOld = visible;
                }
            });
        }
    }

    public static void onStatusBarVisibilityChangeStatic(boolean visible) {
        AutoDisableScreenButtonsManager autoDisableScreenButtonsManager = sAutoDisableScreenButtonsManager;
        if (autoDisableScreenButtonsManager != null) {
            autoDisableScreenButtonsManager.onStatusBarVisibilityChange(visible);
        }
    }

    public boolean handleDisableButtons(int keyCode, boolean down, boolean disableForSingleKey, boolean disableForLidClose, KeyEvent event) {
        boolean isVirtual = event.getDevice().isVirtual();
        boolean isVirtualHardKey = (event.getFlags() & 64) != 0;
        if (RestrictionsHelper.hasKeyCodeRestriction(this.mContext, keyCode, CrossUserUtils.getCurrentUserId())) {
            return true;
        }
        if (keyCode != 3) {
            if (!(keyCode == 4 || keyCode == 82)) {
                if (keyCode != 84) {
                    if (keyCode != 187) {
                        return false;
                    }
                }
            }
            if (disableForSingleKey && !isVirtual) {
                Slog.i(TAG, "disableForSingleKey keyCode:" + keyCode);
                return true;
            }
        }
        if (isVirtual && !isVirtualHardKey) {
            return false;
        }
        if (disableForLidClose && SmartCoverManager.deviceDisableKeysWhenLidClose()) {
            Slog.i(TAG, "disableForLidClose keyCode:" + keyCode);
            return true;
        } else if (!screenButtonsInterceptKey(keyCode, down)) {
            return false;
        } else {
            Slog.i(TAG, "screenButtonsDisabled keyCode:" + keyCode);
            return true;
        }
    }

    public boolean screenButtonsInterceptKey(int keycode, boolean down) {
        if (!isScreenButtonsDisabled()) {
            return false;
        }
        if (down) {
            long time = SystemClock.elapsedRealtime();
            if (time - this.mScreenButtonPressedTime >= 2000 || this.mScreenButtonPressedKeyCode != keycode || !this.mTwice) {
                this.mScreenButtonPressedTime = time;
                this.mScreenButtonPressedKeyCode = keycode;
                this.mTwice = true;
                if (time - this.mToastShowTime > 2000) {
                    this.mToastShowTime = time;
                    showToast((CharSequence) this.mContext.getString(R.string.auto_disable_screenbuttons_tap_again_exit_toast_text), this.mHandler);
                }
            } else {
                this.mTwice = false;
                resetButtonsStatus();
                return false;
            }
        }
        return true;
    }

    public void onUserSwitch(int uid) {
        if (this.mCurUserId != uid) {
            this.mCurUserId = uid;
            updateSettings();
        }
    }

    public boolean isScreenButtonsDisabled() {
        return this.mScreenButtonsDisabled || this.mScreenButtonsTmpDisabled;
    }

    private void showToast(boolean enabled, Handler h) {
        int i;
        Context context = this.mContext;
        if (enabled) {
            i = R.string.auto_disable_screenbuttons_enable_toast_text;
        } else {
            i = R.string.auto_disable_screenbuttons_disable_toast_text;
        }
        showToast((CharSequence) context.getString(i), h);
    }

    private void showToast(final CharSequence text, Handler h) {
        if (h != null) {
            h.post(new Runnable() {
                public void run() {
                    AutoDisableScreenButtonsManager.this.showToastInner(text);
                }
            });
        } else {
            showToastInner(text);
        }
    }

    /* access modifiers changed from: private */
    public void showToastInner(CharSequence text) {
        Toast toast = Toast.makeText(this.mContext, text, 0);
        toast.setType(2006);
        toast.getWindowParams().privateFlags |= 16;
        toast.show();
    }

    /* access modifiers changed from: private */
    public void showFloat() {
        Log.d(TAG, "showing auto disable screen buttons float window...");
        if (this.mFloatView == null) {
            this.mFloatView = AutoDisableScreenbuttonsFloatView.inflate(this.mContext);
            this.mFloatView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AutoDisableScreenButtonsManager.this.mFloatView.dismiss();
                    AutoDisableScreenButtonsManager.this.saveTmpDisableButtonsStatus(true);
                    boolean unused = AutoDisableScreenButtonsManager.this.showPromptsIfNeeds();
                }
            });
            this.mFloatView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    AutoDisableScreenButtonsManager.this.mFloatView.dismiss();
                    AutoDisableScreenButtonsManager.this.showSettings();
                    return true;
                }
            });
        }
        this.mFloatView.show();
    }

    /* access modifiers changed from: private */
    public boolean showPromptsIfNeeds() {
        Object obj = AutoDisableScreenButtonsHelper.getValue(this.mContext, PREF_ADSB_NOT_SHOW_PROMPTS);
        if (obj == null ? false : ((Boolean) obj).booleanValue()) {
            return false;
        }
        AlertDialog adlg = new AlertDialog.Builder(this.mContext).setTitle(R.string.auto_disable_screenbuttons_prompts_title).setMessage(R.string.auto_disable_screenbuttons_prompts_message).setCheckBox(true, this.mContext.getString(R.string.auto_disable_screenbuttons_prompts_not_show_again)).setCancelable(true).setPositiveButton(R.string.auto_disable_screenbuttons_prompts_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (((AlertDialog) dialog).isChecked()) {
                    AutoDisableScreenButtonsHelper.setValue(AutoDisableScreenButtonsManager.this.mContext, AutoDisableScreenButtonsManager.PREF_ADSB_NOT_SHOW_PROMPTS, true);
                }
            }
        }).create();
        adlg.getWindow().setType(2003);
        adlg.show();
        return true;
    }

    /* access modifiers changed from: private */
    public void showSettings() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(SettingsActionComponent);
        intent.setFlags(268435456);
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "start activity exception, component = " + SettingsActionComponent);
        }
    }

    private void saveDisableButtonsStatus(boolean disable) {
        this.mScreenButtonsDisabled = disable;
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "screen_buttons_state", disable, this.mCurUserId);
    }

    /* access modifiers changed from: private */
    public void saveTmpDisableButtonsStatus(boolean tmp) {
        this.mScreenButtonsTmpDisabled = tmp;
        if (!this.mScreenButtonsDisabled) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "screen_buttons_state", tmp ? 2 : 0, this.mCurUserId);
        }
    }

    private void resetButtonsStatus() {
        saveDisableButtonsStatus(false);
        this.mScreenButtonsTmpDisabled = false;
    }

    public void resetTmpButtonsStatus() {
        this.mScreenButtonsTmpDisabled = false;
    }

    /* access modifiers changed from: private */
    public static ComponentName getRunningTopActivity(Context context) {
        List<ActivityManager.RunningTaskInfo> runningTaskInfos;
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        if (am == null || (runningTaskInfos = am.getRunningTasks(1)) == null || runningTaskInfos.size() <= 0) {
            return null;
        }
        return runningTaskInfos.get(0).topActivity;
    }

    private class DisableButtonsSettingsObserver extends ContentObserver {
        public DisableButtonsSettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            AutoDisableScreenButtonsManager.this.updateSettings();
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver resolver = AutoDisableScreenButtonsManager.this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.Secure.getUriFor("screen_buttons_state"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("auto_disable_screen_button"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("auto_disable_screen_button_cloud_setting"), false, this, -1);
            onChange(false);
        }
    }

    /* access modifiers changed from: private */
    public void updateSettings() {
        ContentResolver resolver = this.mContext.getContentResolver();
        synchronized (this.mLock) {
            int btnState = Settings.Secure.getIntForUser(resolver, "screen_buttons_state", 0, this.mCurUserId);
            if (btnState == 0) {
                this.mScreenButtonsDisabled = false;
            } else if (btnState == 1) {
                this.mScreenButtonsDisabled = true;
            }
            String userSetting = MiuiSettings.System.getStringForUser(resolver, "auto_disable_screen_button", this.mCurUserId);
            if (userSetting != null && !userSetting.equals(this.mUserSetting)) {
                this.mUserSetting = userSetting;
                AutoDisableScreenButtonsHelper.updateUserJson(userSetting);
            }
            String cloudConfig = Settings.System.getString(resolver, "auto_disable_screen_button_cloud_setting");
            if (cloudConfig != null && !cloudConfig.equals(this.mCloudConfig)) {
                this.mCloudConfig = cloudConfig;
                AutoDisableScreenButtonsHelper.updateCloudJson(cloudConfig);
            }
        }
    }
}
