package com.android.server.policy;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.miui.R;
import android.os.Binder;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.LocalServices;
import com.android.server.pm.PackageManagerService;
import com.android.server.policy.WindowManagerPolicy;
import miui.date.DateUtils;
import miui.maml.LanguageHelper;
import miui.maml.RenderThread;
import miui.maml.ResourceManager;
import miui.maml.ScreenContext;
import miui.maml.ScreenElementRoot;
import miui.maml.component.MamlView;
import miui.maml.data.Variables;
import miui.maml.util.Utils;
import miui.maml.util.ZipResourceLoader;
import miui.os.Build;
import miui.security.SecurityManager;

class MiuiGlobalActions {
    private static final String BOOT_ALARM_INTENT_SERVICE = "com.miui.powercenter.provider.BootAlarmIntentService";
    private static final int MESSAGE_DISMISS = 0;
    private static final int MESSAGE_REFRESH = 1;
    private static final String SHUTDOWN_ALARM_CLOCK_OFFSET = "shutdown_alarm_clock_offset";
    private static final String SHUTDOWN_ALARM_SERVICE_NAME = "com.android.deskclock.util.ShutdownAlarm";
    private static final String TAG = "MiuiGlobalActions";
    private static final int WAKE_ALARM_TIME_OFFSET = 120;
    private ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            MiuiGlobalActions.this.mHandler.sendEmptyMessage(1);
        }
    };
    private final AudioManager mAudioManager;
    private Binder mBinder = new Binder();
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) && !PhoneWindowManager.SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS.equals(intent.getStringExtra(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY))) {
                MiuiGlobalActions.this.mHandler.sendEmptyMessage(0);
            }
        }
    };
    private final ScreenElementRoot.OnExternCommandListener mCommandListener = new ScreenElementRoot.OnExternCommandListener() {
        public void onCommand(String command, Double para1, String para2) {
            if ("airplane".equals(command)) {
                MiuiGlobalActions.this.sendAction(9);
            } else if ("silent".equals(command)) {
                MiuiGlobalActions.this.sendAction(5);
            } else if ("reboot".equals(command)) {
                try {
                    MiuiGlobalActions.getPowerManager().reboot(false, (String) null, false);
                } catch (RemoteException e) {
                }
            } else if ("shutdown".equals(command)) {
                new Thread("ShutdownThread") {
                    public void run() {
                        try {
                            MiuiGlobalActions.getPowerManager().shutdown(false, "userrequested", false);
                        } catch (RemoteException e) {
                        }
                    }
                }.start();
            } else if ("dismiss".equals(command)) {
                MiuiGlobalActions.this.mHandler.sendEmptyMessage(0);
            }
        }
    };
    private final Context mContext;
    private final View.OnLayoutChangeListener mDialogLayoutChangeListener = new View.OnLayoutChangeListener() {
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (MiuiGlobalActions.this.mRotation != MiuiGlobalActions.this.mWindowManager.getDefaultDisplay().getRotation()) {
                MiuiGlobalActions.this.mHandler.sendEmptyMessage(0);
            }
        }
    };
    private final GlobalActionsProvider mGlobalActionsProvider;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                MiuiGlobalActions.this.dismiss();
            } else if (i == 1) {
                MiuiGlobalActions.this.updateVariables();
            }
        }
    };
    private RenderThread mRenderThread;
    private ResourceManager mResourceManager;
    private BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MiuiGlobalActions.this.mHandler.sendEmptyMessage(1);
        }
    };
    private GlobalActionsMamlView mRoot;
    /* access modifiers changed from: private */
    public int mRotation;
    /* access modifiers changed from: private */
    public ScreenElementRoot mScreenElementRoot;
    private IStatusBarService mStatusBarService;
    /* access modifiers changed from: private */
    public final WindowManager mWindowManager;

    public MiuiGlobalActions(Context context, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mResourceManager = new ResourceManager(new ZipResourceLoader("/system/media/theme/default/powermenu"));
        this.mScreenElementRoot = new ScreenElementRoot(new ScreenContext(this.mContext, this.mResourceManager));
        this.mScreenElementRoot.setOnExternCommandListener(this.mCommandListener);
        this.mScreenElementRoot.setKeepResource(true);
        this.mScreenElementRoot.load();
        this.mScreenElementRoot.init();
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mGlobalActionsProvider = (GlobalActionsProvider) LocalServices.getService(GlobalActionsProvider.class);
    }

    /* access modifiers changed from: private */
    public static IPowerManager getPowerManager() {
        return IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    }

    /* access modifiers changed from: private */
    public void sendAction(int toggleId) {
        Intent intent = new Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE");
        intent.putExtra("com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID", toggleId);
        this.mContext.sendBroadcast(intent);
    }

    private GlobalActionsMamlView loadMamlView() {
        this.mScreenElementRoot.getContext().mVariables.reset();
        updateVariables();
        LanguageHelper.load(this.mContext.getResources().getConfiguration().locale, this.mResourceManager, this.mScreenElementRoot.getContext().mVariables);
        return new GlobalActionsMamlView(this.mContext, this.mScreenElementRoot, 0);
    }

    /* access modifiers changed from: private */
    public void updateVariables() {
        long offset;
        long time;
        int i;
        boolean isAirplaneModeOn = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        Variables variables = this.mScreenElementRoot.getVariables();
        Utils.putVariableNumber("airplane_mode", variables, isAirplaneModeOn ? 1.0d : 0.0d);
        Utils.putVariableNumber("silent_mode", variables, MiuiSettings.SilenceMode.getZenMode(this.mContext) == 4 ? 1.0d : 0.0d);
        Utils.putVariableNumber("show_emergency", variables, "IN".equals(Build.getRegion()) ? 1.0d : 0.0d);
        try {
            String settingValue = Settings.System.getString(this.mContext.getContentResolver(), SHUTDOWN_ALARM_CLOCK_OFFSET);
            offset = settingValue != null ? Long.parseLong(settingValue) : 120;
        } catch (Exception e) {
            offset = 120;
            Log.e(TAG, "get deskclock ShutdownAlarm error " + e);
        }
        Utils.putVariableString("shutdown_info", variables, (String) null);
        SecurityManager securityManager = (SecurityManager) this.mContext.getSystemService("security");
        if (securityManager != null) {
            boolean isShutDownAlarm = true;
            long time2 = securityManager.getWakeUpTime(SHUTDOWN_ALARM_SERVICE_NAME);
            long autoBootTime = securityManager.getWakeUpTime(BOOT_ALARM_INTENT_SERVICE);
            if (time2 == 0 || (autoBootTime > 0 && autoBootTime < time2 + offset)) {
                time = autoBootTime;
                isShutDownAlarm = false;
            } else {
                time = time2 + offset;
            }
            if (time > 0) {
                Context context = this.mContext;
                if (isShutDownAlarm) {
                    i = R.string.reboot_info_shutdown_alarm;
                } else {
                    i = R.string.reboot_info_auto_boot;
                }
                String format = context.getString(i);
                SecurityManager securityManager2 = securityManager;
                Utils.putVariableString("shutdown_info", variables, String.format(format, new Object[]{DateUtils.formatRelativeTime(time * 1000, true)}));
            }
        }
        boolean isInLockTaskMode = isInLockTaskMode();
        Utils.putVariableNumber("hide_airplane_mode", variables, isInLockTaskMode ? 1.0d : 0.0d);
        Utils.putVariableNumber("hide_silent_mode", variables, isInLockTaskMode ? 1.0d : 0.0d);
    }

    private boolean isInLockTaskMode() {
        try {
            IActivityManager activityManager = ActivityManagerNative.getDefault();
            if (activityManager == null || !activityManager.isInLockTaskMode()) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: type inference failed for: r4v1, types: [android.view.View, com.android.server.policy.MiuiGlobalActions$GlobalActionsMamlView] */
    public void showDialog(boolean keyguardShowing, boolean isDeviceProvisioned) {
        GlobalActionsProvider globalActionsProvider = this.mGlobalActionsProvider;
        if ((globalActionsProvider == null || !globalActionsProvider.isGlobalActionsDisabled()) && Settings.Global.getInt(this.mContext.getContentResolver(), "com.xiaomi.system.devicelock.locked", 0) == 0 && this.mRoot == null) {
            this.mRotation = this.mWindowManager.getDefaultDisplay().getRotation();
            this.mRoot = loadMamlView();
            this.mRoot.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mRoot.setSystemUiVisibility(512);
            this.mRoot.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 1 && event.getKeyCode() == 4) {
                        MiuiGlobalActions.this.mScreenElementRoot.onCommand("finish");
                        return true;
                    } else if (event.getKeyCode() == 25 || event.getKeyCode() == 24) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            this.mRoot.setFocusableInTouchMode(true);
            this.mRoot.requestFocus();
            this.mRoot.addOnLayoutChangeListener(this.mDialogLayoutChangeListener);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2024, 16875780, 3);
            layoutParams.extraFlags = 32770;
            layoutParams.privateFlags |= 2;
            layoutParams.setTitle(TAG);
            layoutParams.format = 1;
            layoutParams.windowAnimations = R.style.Animation_GlobalAction;
            if (this.mScreenElementRoot.isMamlBlurWindow()) {
                layoutParams.flags &= -5;
                this.mRoot.setWindowLayoutParams(layoutParams);
            }
            this.mWindowManager.addView(this.mRoot, layoutParams);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            filter.addAction("android.intent.action.SCREEN_OFF");
            this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
            this.mContext.registerReceiver(this.mRingerModeReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
            try {
                this.mStatusBarService.disable(65536, this.mBinder, PackageManagerService.PLATFORM_PACKAGE_NAME);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [android.view.View, com.android.server.policy.MiuiGlobalActions$GlobalActionsMamlView] */
    public void dismiss() {
        if (this.mRoot != null) {
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            this.mContext.unregisterReceiver(this.mRingerModeReceiver);
            this.mContext.getContentResolver().unregisterContentObserver(this.mAirplaneModeObserver);
            this.mWindowManager.removeViewImmediate(this.mRoot);
            this.mRoot.cleanUp(true);
            this.mRoot = null;
            try {
                this.mStatusBarService.disable(0, this.mBinder, PackageManagerService.PLATFORM_PACKAGE_NAME);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private final class GlobalActionsMamlView extends MamlView {
        private boolean mCancelOnUp;
        private boolean mIntercepted;

        public GlobalActionsMamlView(Context context, ScreenElementRoot root, long startDelay) {
            super(context, root, startDelay);
        }

        public boolean dispatchTouchEvent(MotionEvent event) {
            return MiuiGlobalActions.super.dispatchTouchEvent(event);
        }
    }
}
