package com.android.server.policy;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.StatusBarManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.miui.R;
import android.net.Uri;
import android.os.AnrMonitor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.pm.PackageManagerService;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import com.android.server.wm.ActivityStackSupervisorInjector;
import com.android.server.wm.DisplayFrames;
import com.android.server.wm.DisplayPolicy;
import com.android.server.wm.WindowManagerService;
import com.miui.server.AccessController;
import com.miui.server.AutoDisableScreenButtonsManager;
import com.miui.server.MiuiPointerEventListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import miui.core.SdkManager;
import miui.os.Build;
import miui.os.DeviceFeature;
import miui.os.SystemProperties;
import miui.provider.SettingsStringUtil;
import miui.util.EdgeSettingsUtils;
import miui.util.FeatureParser;
import miui.util.HapticFeedbackUtil;
import miui.util.ITouchFeature;
import miui.util.ProximitySensorWrapper;
import miui.util.SmartCoverManager;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseMiuiPhoneWindowManager extends PhoneWindowManager {
    private static final int BTN_MOUSE = 272;
    private static final long CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS = 300;
    private static final long CAMERA_POWER_DOUBLE_TAP_MIN_TIME_MS = 0;
    private static final int COMBINE_VOLUME_KEY_DELAY_TIME = 150;
    private static final boolean DEBUG = false;
    private static final int DOUBLE_CLICK_AI_KEY_TIME = 300;
    private static final int DOUBLE_TAP_HOME_NOTHING = 0;
    private static final int DOUBLE_TAP_HOME_RECENT_PANEL = 1;
    private static final int ENABLE_HOME_KEY_DOUBLE_TAP_INTERVAL = 300;
    private static final int ENABLE_HOME_KEY_PRESS_INTERVAL = 300;
    private static final int ENABLE_VOLUME_KEY_PRESS_COUNTS = 2;
    private static final int ENABLE_VOLUME_KEY_PRESS_INTERVAL = 300;
    protected static final int INTERCEPT_EXPECTED_RESULT_GO_TO_SLEEP = -1;
    protected static final int INTERCEPT_EXPECTED_RESULT_NONE = 0;
    protected static final int INTERCEPT_EXPECTED_RESULT_WAKE_UP = 1;
    private static final int KEYCODE_AI = 689;
    private static final String KEY_GAME_BOOSTER = "gb_boosting";
    private static final int LAUNCH_SOS_BY_PRESS_POWER_KEY_CONTINUOUSLY = 5;
    private static final int LONG_PRESS_AI_KEY_TIME = 500;
    private static final long LONG_PRESS_POWER_KEY_TIMEOUT = 3000;
    private static final int LONG_PRESS_VOLUME_DOWN_ACTION_NONE = 0;
    private static final int LONG_PRESS_VOLUME_DOWN_ACTION_PAY = 2;
    private static final int LONG_PRESS_VOLUME_DOWN_ACTION_STREET_SNAP = 1;
    private static final int LONG_PRESS_VOLUME_TIME = 1000;
    private static final int MSG_COMBINE_VOLUME_KEY_DELAY_TIME = 3000;
    private static final String NAVIGATION_BAR_WINDOW_LOADED = "navigation_bar_window_loaded";
    private static final String PERMISSION_INTERNAL_GENERAL_API = "miui.permission.USE_INTERNAL_GENERAL_API";
    protected static final String REASON_FP_DPAD_CENTER_WAKEUP = "miui.policy:FINGERPRINT_DPAD_CENTER";
    private static final String SCREEN_KEY_LONG_PRESS_VOLUME_DOWN = "screen_key_long_press_volume_down";
    private static final int SHORTCUT_BACK_POWER = (getKeyBitmask(4) | getKeyBitmask(26));
    private static final int SHORTCUT_HOME_POWER = (getKeyBitmask(3) | getKeyBitmask(26));
    private static final int SHORTCUT_MENU_POWER = (getKeyBitmask(187) | getKeyBitmask(26));
    private static final int SHORTCUT_SCREENSHOT_ANDROID = (getKeyBitmask(26) | getKeyBitmask(25));
    private static final int SHORTCUT_SCREENSHOT_MIUI = (getKeyBitmask(187) | getKeyBitmask(25));
    private static final int SHORTCUT_SCREENSHOT_SINGLE_KEY = (getKeyBitmask(3) | getKeyBitmask(25));
    private static final int SHORTCUT_UNLOCK = (getKeyBitmask(4) | getKeyBitmask(24));
    protected static final boolean SUPPORT_EDGE_TOUCH_VOLUME = FeatureParser.getBoolean("support_edge_touch_volume", false);
    private static final String SYSTEM_SETTINGS_VR_MODE = "vr_mode";
    private static final int SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR = 16;
    private static final int[] WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = {2003, 2010};
    static final ArrayList<Integer> sScreenRecorderKeyEventList = new ArrayList<>();
    static final ArrayList<Integer> sVoiceAssistKeyEventList = new ArrayList<>();
    /* access modifiers changed from: private */
    public static final ComponentName talkBackServiceName = new ComponentName("com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService");
    boolean mAccessibilityShortcutOnLockScreen;
    /* access modifiers changed from: private */
    public SettingsStringUtil.SettingStringHelper mAccessibilityShortcutSetting;
    private PowerManager.WakeLock mAiKeyWakeLock;
    private AudioManager mAudioManager;
    private AutoDisableScreenButtonsManager mAutoDisableScreenButtonsManager;
    private Binder mBinder = new Binder();
    /* access modifiers changed from: private */
    public ProgressBar mBootProgress;
    /* access modifiers changed from: private */
    public String[] mBootText;
    /* access modifiers changed from: private */
    public TextView mBootTextView;
    private ComponentName mCameraComponentName;
    private Intent mCameraIntent;
    boolean mCameraKeyWakeScreen;
    /* access modifiers changed from: private */
    public int mCurrentUserId;
    /* access modifiers changed from: private */
    public int mDoubleClickAiKeyCount = 0;
    private boolean mDoubleClickAiKeyIsConsumed;
    private Runnable mDoubleClickAiKeyRunnable = new Runnable() {
        public void run() {
            Slog.d(DisplayPolicy.TAG, "double click ai key");
            BaseMiuiPhoneWindowManager.this.startAiKeyService("key_double_click_ai_button_settings");
        }
    };
    String mDoubleClickPowerKey;
    /* access modifiers changed from: private */
    public int mDoubleTapOnHomeBehavior = 0;
    boolean mDpadCenterDown;
    private Intent mDumpLogIntent;
    /* access modifiers changed from: private */
    public float mEdgeModeSize;
    private EdgeSettingsUtils mEdgeSettingsUtils;
    /* access modifiers changed from: private */
    public boolean mForbidFullScreen;
    protected List<String> mFpNavEventNameList = null;
    protected boolean mFrontFingerprintSensor;
    protected Handler mHandler;
    /* access modifiers changed from: private */
    public HapticFeedbackUtil mHapticFeedbackUtil;
    private boolean mHasCameraFlash;
    boolean mHaveBankCard;
    boolean mHaveTranksCard;
    private PowerManager.WakeLock mHelpKeyWakeLock;
    boolean mHomeConsumed;
    boolean mHomeDoubleClickPending;
    private final Runnable mHomeDoubleClickTimeoutRunnable;
    boolean mHomeDoubleTapPending;
    private final Runnable mHomeDoubleTapTimeoutRunnable;
    boolean mHomeDownAfterDpCenter;
    private int mInputMethodWindowVisibleHeight;
    private BroadcastReceiver mInternalBroadcastReceiver;
    protected boolean mIsStatusBarVisibleInFullscreen;
    /* access modifiers changed from: private */
    public boolean mIsSupportEdgeMode;
    /* access modifiers changed from: private */
    public boolean mIsSupportGloablTounchDirection;
    /* access modifiers changed from: private */
    public boolean mIsVRMode;
    String mKeyCombinationPowerBack;
    String mKeyCombinationPowerHome;
    String mKeyCombinationPowerMenu;
    protected int mKeyLongPressTimeout = ViewConfiguration.getLongPressTimeout();
    private int mKeyPressed;
    private int mKeyPressing;
    private boolean mKeyguardOnWhenHomeDown = false;
    private long mLastClickAiKeyTime = 0;
    private long mLastPowerDown;
    /* access modifiers changed from: private */
    public boolean mLongPressAiKeyIsConsumed;
    String mLongPressBackKey;
    private Runnable mLongPressDownAiKeyRunnable = new Runnable() {
        public void run() {
            int unused = BaseMiuiPhoneWindowManager.this.mDoubleClickAiKeyCount = 0;
            boolean unused2 = BaseMiuiPhoneWindowManager.this.mLongPressAiKeyIsConsumed = true;
            Slog.d(DisplayPolicy.TAG, "long press down ai key");
            BaseMiuiPhoneWindowManager.this.startAiKeyService("key_long_press_down_ai_button_settings");
        }
    };
    String mLongPressHomeKey;
    String mLongPressMenuKey;
    String mLongPressMenuKeyWhenLock;
    String mLongPressPowerKey;
    private Runnable mLongPressUpAiKeyRunnable = new Runnable() {
        public void run() {
            int unused = BaseMiuiPhoneWindowManager.this.mDoubleClickAiKeyCount = 0;
            Slog.d(DisplayPolicy.TAG, "long press up ai key");
            BaseMiuiPhoneWindowManager.this.startAiKeyService("key_long_press_up_ai_button_settings");
        }
    };
    /* access modifiers changed from: private */
    public int mLongPressVolumeDownBehavior = 0;
    boolean mMikeymodeEnabled;
    /* access modifiers changed from: private */
    public Dialog mMiuiBootMsgDialog;
    protected MiuiKeyguardServiceDelegate mMiuiKeyguardDelegate;
    protected MiuiPointerEventListener mMiuiPointerEventListener;
    int mNavBarHeight;
    int mNavBarHeightLand;
    int mNavBarWidth;
    private Intent mNfcIntent;
    Runnable mPowerLongPressOriginal = getPowerLongPress();
    private int mPressPowerKeyCount = 1;
    /* access modifiers changed from: private */
    public boolean mPressToAppSwitch;
    private MiuiScreenOnProximityLock mProximitySensor;
    /* access modifiers changed from: private */
    public final ProximitySensorWrapper.ProximitySensorChangeListener mProximitySensorListener = new ProximitySensorWrapper.ProximitySensorChangeListener() {
        public void onSensorChanged(boolean tooClose) {
            BaseMiuiPhoneWindowManager.this.mProximitySensorWrapper.unregisterAllListeners();
            ProximitySensorWrapper unused = BaseMiuiPhoneWindowManager.this.mProximitySensorWrapper = null;
            if (tooClose) {
                Slog.w("BaseMiuiPhoneWindowManager", "Going to sleep due to KEYCODE_WAKEUP/KEYCODE_DPAD_CENTER: proximity sensor too close");
                BaseMiuiPhoneWindowManager.this.mPowerManager.goToSleep(SystemClock.uptimeMillis());
            }
        }
    };
    /* access modifiers changed from: private */
    public ProximitySensorWrapper mProximitySensorWrapper = null;
    private boolean mRequestShowMenu;
    private RotationWatcher mRotationWatcher;
    protected int mScreenOffReason;
    BroadcastReceiver mScreenRecordeEnablekKeyEventReceiver;
    private boolean mScreenRecorderEnabled;
    BroadcastReceiver mScreenshotReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            BaseMiuiPhoneWindowManager.this.mHandler.removeCallbacks(BaseMiuiPhoneWindowManager.this.getScreenshotChordLongPress());
            BaseMiuiPhoneWindowManager.this.mHandler.postDelayed(BaseMiuiPhoneWindowManager.this.getScreenshotChordLongPress(), intent.getLongExtra("capture_delay", 0));
        }
    };
    private MiuiSettingsObserver mSettingsObserver;
    private int mShortcutPressing;
    boolean mShortcutServiceIsTalkBack;
    /* access modifiers changed from: private */
    public boolean mShortcutTriggered;
    private Runnable mSingleClickAiKeyRunnable = new Runnable() {
        public void run() {
            int unused = BaseMiuiPhoneWindowManager.this.mDoubleClickAiKeyCount = 0;
            Slog.d(DisplayPolicy.TAG, "single click ai key");
            BaseMiuiPhoneWindowManager.this.startAiKeyService("key_single_click_ai_button_settings");
        }
    };
    boolean mSingleKeyUse;
    private SmartCoverManager mSmartCoverManager = new SmartCoverManager();
    BroadcastReceiver mStatusBarExitFullscreenReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            BaseMiuiPhoneWindowManager.this.setStatusBarInFullscreen(false);
        }
    };
    private HashSet<Integer> mSuperWaitingKey = new HashSet<>();
    protected boolean mSupportTapFingerprintSensorToHome;
    private HashSet<String> mSystemKeyPackages = new HashSet<>();
    boolean mTalkBackIsOpened;
    boolean mTestModeEnabled;
    String mThreeGestureDown;
    /* access modifiers changed from: private */
    public boolean mTorchEnabled;
    private int mTrackDumpLogKeyCodeLastKeyCode;
    private boolean mTrackDumpLogKeyCodePengding;
    private long mTrackDumpLogKeyCodeStartTime;
    private int mTrackDumpLogKeyCodeTimeOut;
    private int mTrackDumpLogKeyCodeVolumeDownTimes;
    boolean mTrackballWakeScreen;
    /* access modifiers changed from: private */
    public final Runnable mTurnOffTorch = new Runnable() {
        public void run() {
            if (BaseMiuiPhoneWindowManager.this.mTorchEnabled) {
                boolean unused = BaseMiuiPhoneWindowManager.this.setTorch(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mVoiceAssistEnabled;
    private long mVolumeButtonPrePressedTime;
    private long mVolumeButtonPressedCount;
    /* access modifiers changed from: private */
    public boolean mVolumeDownKeyConsumed;
    private boolean mVolumeDownKeyPressed;
    private long mVolumeDownKeyTime;
    boolean mVolumeKeyLaunchCamera;
    private PowerManager.WakeLock mVolumeKeyWakeLock;
    /* access modifiers changed from: private */
    public boolean mVolumeUpKeyConsumed;
    private boolean mVolumeUpKeyPressed;
    private long mVolumeUpKeyTime;
    /* access modifiers changed from: private */
    public boolean mWifiOnly;
    private WindowManagerPolicy.WindowState mWin;
    private Binder mWindowFlagBinder = new Binder();

    /* access modifiers changed from: protected */
    public abstract int callSuperInterceptKeyBeforeQueueing(KeyEvent keyEvent, int i, boolean z);

    /* access modifiers changed from: protected */
    public abstract void finishActivityInternal(IBinder iBinder, int i, Intent intent) throws RemoteException;

    /* access modifiers changed from: protected */
    public abstract void forceStopPackage(String str, int i, String str2);

    /* access modifiers changed from: protected */
    public abstract WindowManagerPolicy.WindowState getKeyguardWindowState();

    /* access modifiers changed from: protected */
    public abstract int getWakePolicyFlag();

    /* access modifiers changed from: protected */
    public abstract boolean interceptPowerKeyByFingerPrintKey();

    /* access modifiers changed from: protected */
    public abstract boolean isFingerPrintKey(KeyEvent keyEvent);

    /* access modifiers changed from: protected */
    public abstract boolean isScreenOnInternal();

    /* access modifiers changed from: protected */
    public abstract void launchAssistActionInternal(String str, Bundle bundle);

    /* access modifiers changed from: protected */
    public abstract void launchRecentPanelInternal();

    /* access modifiers changed from: protected */
    public abstract void onStatusBarPanelRevealed(IStatusBarService iStatusBarService);

    /* access modifiers changed from: protected */
    public abstract void preloadRecentAppsInternal();

    /* access modifiers changed from: protected */
    public abstract int processFingerprintNavigationEvent(KeyEvent keyEvent, boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean screenOffBecauseOfProxSensor();

    /* access modifiers changed from: protected */
    public abstract void toggleSplitScreenInternal();

    public BaseMiuiPhoneWindowManager() {
        this.mSystemKeyPackages.add(PackageManagerService.PLATFORM_PACKAGE_NAME);
        this.mSystemKeyPackages.add(AccessController.PACKAGE_SYSTEMUI);
        this.mSystemKeyPackages.add("com.android.phone");
        this.mSystemKeyPackages.add("com.android.mms");
        this.mSystemKeyPackages.add("com.android.contacts");
        this.mSystemKeyPackages.add("com.miui.home");
        this.mSystemKeyPackages.add("com.jeejen.family.miui");
        this.mSystemKeyPackages.add("com.android.incallui");
        this.mSystemKeyPackages.add("com.miui.backup");
        this.mSystemKeyPackages.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        this.mSystemKeyPackages.add("com.xiaomi.mihomemanager");
        this.mSystemKeyPackages.add("com.miui.securityadd");
        this.mHomeDoubleTapTimeoutRunnable = new Runnable() {
            public void run() {
                if (BaseMiuiPhoneWindowManager.this.mHomeDoubleTapPending) {
                    BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager = BaseMiuiPhoneWindowManager.this;
                    baseMiuiPhoneWindowManager.mHomeDoubleTapPending = false;
                    baseMiuiPhoneWindowManager.launchHomeFromHotKey(0);
                }
            }
        };
        this.mHomeDoubleClickTimeoutRunnable = new Runnable() {
            public void run() {
                if (BaseMiuiPhoneWindowManager.this.mHomeDoubleClickPending) {
                    BaseMiuiPhoneWindowManager.this.mHomeDoubleClickPending = false;
                }
            }
        };
        this.mScreenRecorderEnabled = false;
        this.mScreenRecordeEnablekKeyEventReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean enable = intent.getBooleanExtra("enable", false);
                Slog.i(DisplayPolicy.TAG, "mScreenRecordeEnablekKeyEventReceiver enable=" + enable);
                BaseMiuiPhoneWindowManager.this.setScreenRecorderEnabled(enable);
            }
        };
        this.mTrackDumpLogKeyCodePengding = false;
        this.mTrackDumpLogKeyCodeStartTime = 0;
        this.mTrackDumpLogKeyCodeLastKeyCode = 25;
        this.mTrackDumpLogKeyCodeTimeOut = 2000;
        this.mTrackDumpLogKeyCodeVolumeDownTimes = 0;
        this.mVoiceAssistEnabled = false;
        this.mInternalBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.miui.app.ExtraStatusBarManager.action_enter_drive_mode".equals(action)) {
                    boolean unused = BaseMiuiPhoneWindowManager.this.mForbidFullScreen = true;
                } else if ("com.miui.app.ExtraStatusBarManager.action_leave_drive_mode".equals(action)) {
                    boolean unused2 = BaseMiuiPhoneWindowManager.this.mForbidFullScreen = false;
                }
            }
        };
    }

    private static int getKeyBitmask(int keycode) {
        if (keycode == 3) {
            return 8;
        }
        if (keycode == 4) {
            return 16;
        }
        if (keycode == 82) {
            return 2;
        }
        if (keycode == 187) {
            return 4;
        }
        switch (keycode) {
            case WindowManagerService.H.WAITING_FOR_DRAWN_TIMEOUT /*24*/:
                return 128;
            case WindowManagerService.H.SHOW_STRICT_MODE_VIOLATION /*25*/:
                return 64;
            case 26:
                return 32;
            default:
                return 1;
        }
    }

    static {
        sScreenRecorderKeyEventList.add(3);
        sScreenRecorderKeyEventList.add(4);
        sScreenRecorderKeyEventList.add(82);
        sScreenRecorderKeyEventList.add(187);
        sScreenRecorderKeyEventList.add(24);
        sScreenRecorderKeyEventList.add(25);
        sScreenRecorderKeyEventList.add(26);
        sVoiceAssistKeyEventList.add(4);
    }

    /* access modifiers changed from: protected */
    public void initInternal(Context context, IWindowManager windowManager, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        Resources res = context.getResources();
        this.mNavBarWidth = res.getDimensionPixelSize(17105309);
        this.mNavBarHeight = res.getDimensionPixelSize(17105304);
        this.mNavBarHeightLand = res.getDimensionPixelSize(17105306);
        this.mIsSupportGloablTounchDirection = ITouchFeature.getInstance().hasSupportGlobalTouchDirection();
        this.mIsSupportEdgeMode = ITouchFeature.getInstance().hasSupportEdgeMode();
        if (this.mIsSupportGloablTounchDirection) {
            handleTouchFeatureDirectionModeChange();
        }
        this.mHandler = new H();
        this.mSettingsObserver = new MiuiSettingsObserver(this.mHandler);
        this.mMiuiPointerEventListener = new MiuiPointerEventListener(context);
        this.mSettingsObserver.observe();
        setPowerLongPress(new Runnable() {
            public void run() {
                BaseMiuiPhoneWindowManager.this.mPowerLongPressOriginal.run();
            }
        });
        this.mVolumeKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mVolumeKeyWakeLock");
        this.mAiKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mAiKeyWakeLock");
        this.mHelpKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mHelpKeyWakeLock");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CAPTURE_SCREENSHOT");
        context.registerReceiverAsUser(this.mScreenshotReceiver, UserHandle.ALL, filter, PERMISSION_INTERNAL_GENERAL_API, (Handler) null);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("com.miui.app.ExtraStatusBarManager.EXIT_FULLSCREEN");
        context.registerReceiver(this.mStatusBarExitFullscreenReceiver, filter2);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("miui.intent.SCREEN_RECORDER_ENABLE_KEYEVENT");
        context.registerReceiver(this.mScreenRecordeEnablekKeyEventReceiver, filter3);
        IntentFilter filter4 = new IntentFilter();
        filter4.addAction("com.miui.app.ExtraStatusBarManager.action_enter_drive_mode");
        filter4.addAction("com.miui.app.ExtraStatusBarManager.action_leave_drive_mode");
        context.registerReceiverAsUser(this.mInternalBroadcastReceiver, UserHandle.ALL, filter4, PERMISSION_INTERNAL_GENERAL_API, this.mHandler);
        this.mHasCameraFlash = Build.hasCameraFlash(this.mContext);
        this.mHapticFeedbackUtil = new HapticFeedbackUtil(context, false);
        this.mAutoDisableScreenButtonsManager = new AutoDisableScreenButtonsManager(context, this.mHandler);
        this.mSmartCoverManager.init(context, this.mPowerManager);
        saveWindowTypeLayer(context);
    }

    private boolean canReceiveInput(WindowManagerPolicy.WindowState win) {
        if (!(((win.getAttrs().flags & 8) != 0) ^ ((win.getAttrs().flags & 131072) != 0))) {
            return true;
        }
        return false;
    }

    private void saveWindowTypeLayer(Context context) {
        JSONObject typeLayers = new JSONObject();
        for (int type : new int[]{2000, 2001, 2013}) {
            int layer = getWindowLayerFromTypeLw(type);
            if (layer != 2) {
                try {
                    typeLayers.put(Integer.toString(type), layer);
                } catch (JSONException ex) {
                    Slog.e(DisplayPolicy.TAG, "JSONException", ex);
                }
            }
        }
        MiuiSettings.System.putString(context.getContentResolver(), "window_type_layer", typeLayers.toString());
    }

    /* access modifiers changed from: protected */
    public void systemReadyInternal() {
        PackageManager pm = this.mContext.getPackageManager();
        if (pm != null && pm.hasSystemFeature("android.hardware.sensor.proximity") && !DeviceFeature.hasSupportAudioPromity()) {
            this.mProximitySensor = new MiuiScreenOnProximityLock(this.mContext, this.mMiuiKeyguardDelegate, this.mHandler.getLooper());
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "torch_state", 0);
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_test_mode_on", 0);
        this.mIsVRMode = false;
        Settings.System.putInt(this.mContext.getContentResolver(), SYSTEM_SETTINGS_VR_MODE, 0);
        if (Build.VERSION.SDK_INT < 24) {
            SdkManager.initialize(ActivityThread.currentApplication(), (Map) null);
            SdkManager.start((Map) null);
        }
        this.mFrontFingerprintSensor = FeatureParser.getBoolean("front_fingerprint_sensor", false);
        this.mSupportTapFingerprintSensorToHome = FeatureParser.getBoolean("support_tap_fingerprint_sensor_to_home", false);
        this.mFpNavEventNameList = new ArrayList();
        String[] strArray = FeatureParser.getStringArray("fp_nav_event_name_list");
        if (strArray != null) {
            for (String str : strArray) {
                this.mFpNavEventNameList.add(str);
            }
        }
        Settings.Global.putStringForUser(this.mContext.getContentResolver(), "policy_control", "immersive.preconfirms=*", -2);
        if (Settings.System.getInt(this.mContext.getContentResolver(), "persist.camera.snap.enable", 0) == 1) {
            Settings.System.putInt(this.mContext.getContentResolver(), "persist.camera.snap.enable", 0);
            if (!this.mHaveTranksCard) {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "key_long_press_volume_down", "Street-snap", this.mCurrentUserId);
            } else {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "key_long_press_volume_down", "none", this.mCurrentUserId);
            }
        }
        this.mSettingsObserver.onChange(false);
        if (this.mIsSupportEdgeMode) {
            handleEdgeModeFeatureDirectionModeChange();
        }
    }

    public void startedWakingUp(int why) {
        boolean proximitySensorEnableSettings;
        super.startedWakingUp(why);
        int pSensorEnableInt = Settings.Global.getInt(this.mContext.getContentResolver(), "enable_screen_on_proximity_sensor", -1);
        if (pSensorEnableInt == -1) {
            proximitySensorEnableSettings = MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "enable_screen_on_proximity_sensor", this.mContext.getResources().getBoolean(R.bool.config_screen_on_proximity_sensor_default));
            MiuiSettings.Global.putBoolean(this.mContext.getContentResolver(), "enable_screen_on_proximity_sensor", proximitySensorEnableSettings);
        } else {
            proximitySensorEnableSettings = pSensorEnableInt != 0;
        }
        if (this.mProximitySensor != null && isDeviceProvisioned() && !MiuiSettings.System.isInSmallWindowMode(this.mContext) && proximitySensorEnableSettings && !this.mIsVRMode) {
            this.mProximitySensor.aquire();
        }
    }

    public void screenTurningOn(WindowManagerPolicy.ScreenOnListener screenOnListener) {
        MiuiKeyguardServiceDelegate miuiKeyguardServiceDelegate;
        super.screenTurningOn(screenOnListener);
        if (screenOnListener == null && (miuiKeyguardServiceDelegate = this.mMiuiKeyguardDelegate) != null) {
            miuiKeyguardServiceDelegate.onScreenTurnedOnWithoutListener();
        }
        this.mSmartCoverManager.notifyScreenTurningOn();
    }

    public void screenTurnedOff() {
        super.screenTurnedOff();
    }

    public void finishedWakingUp(int why) {
        super.finishedWakingUp(why);
        if (this.mIsSupportGloablTounchDirection) {
            handleEdgeModeFeatureDirectionModeChange();
        }
    }

    public void startedGoingToSleep(int why) {
        releaseScreenOnProximitySensor(true);
        super.startedGoingToSleep(why);
    }

    public void finishedGoingToSleep(int why) {
        screenTurnedOffInternal(why);
        super.finishedGoingToSleep(why);
    }

    /* access modifiers changed from: protected */
    public void screenTurnedOffInternal(int why) {
        resetKeyStatus();
        this.mScreenOffReason = why;
    }

    public void notifyLidSwitchChanged(long whenNanos, boolean lidOpen) {
        if (!this.mSmartCoverManager.notifyLidSwitchChanged(lidOpen, this.mSystemBooted)) {
            this.mDefaultDisplayPolicy.setLidState(-1);
            return;
        }
        int i = 0;
        boolean mLidControlsSleep = this.mSmartCoverManager.getSmartCoverMode() == 1;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (mLidControlsSleep) {
            i = 1;
        }
        Settings.Global.putInt(contentResolver, "lid_behavior", i);
        super.notifyLidSwitchChanged(whenNanos, lidOpen);
    }

    private void releaseScreenOnProximitySensor(boolean isNowRelease) {
        MiuiScreenOnProximityLock miuiScreenOnProximityLock = this.mProximitySensor;
        if (miuiScreenOnProximityLock != null) {
            miuiScreenOnProximityLock.release(isNowRelease);
        }
    }

    private void exitFreeFormWindowIfNeeded(WindowManagerPolicy.WindowState win) {
        try {
            for (ActivityManager.StackInfo stackInfo : ActivityTaskManager.getService().getAllStackInfos()) {
                if (stackInfo.configuration.windowConfiguration.getWindowingMode() == 5) {
                    ActivityTaskManager.getService().setTaskWindowingMode(stackInfo.taskIds[0], 1, false);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public long interceptKeyBeforeDispatching(WindowManagerPolicy.WindowState win, KeyEvent event, int policyFlags) {
        WindowManagerPolicy.WindowState windowState = win;
        int repeatCount = event.getRepeatCount();
        boolean down = event.getAction() == 0;
        boolean canceled = event.isCanceled();
        MiuiKeyguardServiceDelegate miuiKeyguardServiceDelegate = this.mMiuiKeyguardDelegate;
        boolean keyguardActive = miuiKeyguardServiceDelegate != null && miuiKeyguardServiceDelegate.isShowingAndNotHidden();
        if (down && repeatCount == 0) {
            this.mWin = windowState;
        }
        int keyCode = event.getKeyCode();
        if (keyCode == 82) {
            if (this.mTestModeEnabled) {
                Slog.w("BaseMiuiPhoneWindowManager", "Ignoring MENU because mTestModeEnabled = " + this.mTestModeEnabled);
                return 0;
            } else if (isLockDeviceWindow(win)) {
                Slog.w("BaseMiuiPhoneWindowManager", "device locked, pass MENU to lock window");
                return 0;
            } else if (!this.mPressToAppSwitch) {
                return 0;
            } else {
                if (!this.mRequestShowMenu) {
                    if (!keyguardOn()) {
                        if (down) {
                            preloadRecentApps();
                        } else {
                            launchRecentPanel();
                        }
                    }
                    return -1;
                } else if (repeatCount != 0) {
                    return -1;
                } else {
                    if (!down) {
                        this.mRequestShowMenu = false;
                    }
                    return 0;
                }
            }
        } else if (keyCode == 3) {
            if (this.mTestModeEnabled) {
                Slog.w("BaseMiuiPhoneWindowManager", "Ignoring HOME because mTestModeEnabled = " + this.mTestModeEnabled);
                return 0;
            } else if (isLockDeviceWindow(win)) {
                Slog.w("BaseMiuiPhoneWindowManager", "device locked, pass HOME to lock window");
                return 0;
            } else {
                if (down) {
                    WindowManager.LayoutParams attrs = windowState != null ? win.getAttrs() : null;
                    if (attrs != null) {
                        int type = attrs.type;
                        if (type == 2004 || type == 2009) {
                            return 0;
                        }
                        for (int i : WINDOW_TYPES_WHERE_HOME_DOESNT_WORK) {
                            if (type == i) {
                                removeKeyLongPress(keyCode);
                            }
                        }
                    }
                } else if (this.mHomeConsumed) {
                    this.mHomeConsumed = false;
                    return -1;
                } else if (canceled || !isScreenOnFully()) {
                    Slog.i(DisplayPolicy.TAG, "Ignoring HOME; event canceled.");
                } else {
                    TelecomManager telecomManager = getTelecommService();
                    if (this.mWifiOnly || telecomManager == null || !telecomManager.isRinging() || !isInCallScreenShowing()) {
                        if (keyguardActive) {
                            ((StatusBarManager) this.mContext.getSystemService("statusbar")).collapsePanels();
                        }
                        if (this.mKeyguardOnWhenHomeDown) {
                            Slog.i(DisplayPolicy.TAG, "Ignoring HOME; keyguard is on when first Home down");
                        } else if (this.mDoubleTapOnHomeBehavior == 0 || keyguardActive) {
                            launchHomeFromHotKey(0);
                        } else {
                            this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                            this.mHomeDoubleTapPending = true;
                            this.mHandler.postDelayed(this.mHomeDoubleTapTimeoutRunnable, CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS);
                            return -1;
                        }
                    } else {
                        Slog.i(DisplayPolicy.TAG, "Ignoring HOME; there's a ringing incoming call.");
                    }
                }
                if (repeatCount == 0) {
                    if (this.mHomeDoubleTapPending) {
                        this.mHomeDoubleTapPending = false;
                        this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                        handleDoubleTapOnHome();
                    } else if (this.mDoubleTapOnHomeBehavior == 1) {
                        preloadRecentApps();
                    }
                }
                return -1;
            }
        } else if (keyCode == 25 && this.mVolumeDownKeyConsumed) {
            if (!down) {
                this.mVolumeDownKeyConsumed = false;
            }
            return -1;
        } else if (keyCode != 24 || !this.mVolumeUpKeyConsumed) {
            return super.interceptKeyBeforeDispatching(win, event, policyFlags);
        } else {
            if (!down) {
                this.mVolumeUpKeyConsumed = false;
            }
            return -1;
        }
    }

    private boolean isInCallScreenShowing() {
        String runningActivity = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getClassName();
        if ("com.android.phone.MiuiInCallScreen".equals(runningActivity) || "com.android.incallui.InCallActivity".equals(runningActivity)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void markShortcutTriggered() {
        this.mShortcutTriggered = true;
        this.mShortcutPressing |= this.mKeyPressing;
        callInterceptPowerKeyUp(false);
    }

    private boolean handleKeyCombination() {
        int i = this.mKeyPressed;
        if (i != this.mKeyPressing || this.mShortcutTriggered) {
            return false;
        }
        if (i == SHORTCUT_HOME_POWER) {
            boolean result = postKeyFunction(this.mKeyCombinationPowerHome, 0, "key_combination_power_home") | stopLockTaskMode();
            if ("none".equals(this.mKeyCombinationPowerHome)) {
                return false;
            }
            return result;
        } else if (i == SHORTCUT_BACK_POWER && !this.mSingleKeyUse) {
            boolean result2 = postKeyFunction(this.mKeyCombinationPowerBack, 0, "key_combination_power_back");
            if ("none".equals(this.mKeyCombinationPowerBack)) {
                return false;
            }
            return result2;
        } else if (this.mKeyPressed == SHORTCUT_MENU_POWER && !this.mSingleKeyUse) {
            boolean result3 = postKeyFunction(this.mKeyCombinationPowerMenu, 0, "key_combination_power_menu");
            if ("none".equals(this.mKeyCombinationPowerMenu)) {
                return false;
            }
            return result3;
        } else if (this.mKeyPressed == SHORTCUT_SCREENSHOT_MIUI && !this.mSingleKeyUse) {
            return postKeyFunction("screen_shot", 0, (String) null);
        } else {
            if (this.mKeyPressed == SHORTCUT_SCREENSHOT_SINGLE_KEY && this.mSingleKeyUse) {
                return postKeyFunction("screen_shot", 0, (String) null);
            }
            MiuiScreenOnProximityLock miuiScreenOnProximityLock = this.mProximitySensor;
            if (miuiScreenOnProximityLock == null || !miuiScreenOnProximityLock.isHeld()) {
                return false;
            }
            if ((this.mKeyPressed != SHORTCUT_UNLOCK && (!hasNavigationBar() || (this.mKeyPressed & getKeyBitmask(24)) == 0)) || this.mSingleKeyUse) {
                return false;
            }
            releaseScreenOnProximitySensor(false);
            return true;
        }
    }

    public static void sendRecordCountEvent(Context context, String category, String event) {
        Intent intent = new Intent("com.miui.gallery.intent.action.SEND_STAT");
        intent.setPackage(AccessController.PACKAGE_GALLERY);
        intent.putExtra("stat_type", "count_event");
        intent.putExtra("category", category);
        intent.putExtra("event", event);
        context.sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public Toast makeAllUserToastAndShow(String text, int duration) {
        Toast toast = Toast.makeText(this.mContext, text, duration);
        toast.getWindowParams().privateFlags |= 16;
        toast.show();
        return toast;
    }

    /* access modifiers changed from: protected */
    public boolean stopLockTaskMode() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isInLockTaskMode() {
        return false;
    }

    private void resetKeyStatus() {
        this.mKeyPressed = 0;
        this.mKeyPressing = 0;
        this.mShortcutPressing = 0;
        this.mShortcutTriggered = false;
    }

    /* access modifiers changed from: private */
    public void startAiKeyService(String pressType) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.ai.AidaemonService"));
            intent.putExtra("key_ai_button_settings", pressType);
            this.mContext.startServiceAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            Slog.e(DisplayPolicy.TAG, e.toString());
        }
    }

    private void handleAiKeyEvent(KeyEvent event, boolean down) {
        if (down) {
            long keyDownTime = SystemClock.uptimeMillis();
            if (event.getRepeatCount() == 0) {
                this.mDoubleClickAiKeyIsConsumed = false;
                this.mLongPressAiKeyIsConsumed = false;
                this.mDoubleClickAiKeyCount++;
                this.mHandler.postDelayed(this.mLongPressDownAiKeyRunnable, 500);
            }
            if (keyDownTime - this.mLastClickAiKeyTime < CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS && this.mDoubleClickAiKeyCount == 2) {
                this.mHandler.post(this.mDoubleClickAiKeyRunnable);
                this.mDoubleClickAiKeyIsConsumed = true;
                this.mDoubleClickAiKeyCount = 0;
                this.mHandler.removeCallbacks(this.mSingleClickAiKeyRunnable);
                this.mHandler.removeCallbacks(this.mLongPressDownAiKeyRunnable);
            }
            this.mLastClickAiKeyTime = keyDownTime;
        } else if (this.mLongPressAiKeyIsConsumed) {
            this.mHandler.post(this.mLongPressUpAiKeyRunnable);
        } else if (!this.mDoubleClickAiKeyIsConsumed) {
            this.mHandler.postDelayed(this.mSingleClickAiKeyRunnable, CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS);
            this.mHandler.removeCallbacks(this.mLongPressDownAiKeyRunnable);
        }
    }

    /* access modifiers changed from: protected */
    public int intercept(KeyEvent event, int policyFlags, boolean isScreenOn, int expectedResult) {
        int keyCode = event.getKeyCode();
        if (!(event.getAction() == 0) && this.mSuperWaitingKey.contains(Integer.valueOf(keyCode))) {
            this.mSuperWaitingKey.remove(Integer.valueOf(keyCode));
            if (26 == keyCode) {
                event = KeyEvent.changeFlags(event, event.getFlags() | 32);
            }
            callSuperInterceptKeyBeforeQueueing(event, policyFlags, isScreenOn);
        }
        return expectedResult;
    }

    /* access modifiers changed from: protected */
    public void registerProximitySensor() {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (BaseMiuiPhoneWindowManager.this.mProximitySensorWrapper == null) {
                    BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager = BaseMiuiPhoneWindowManager.this;
                    ProximitySensorWrapper unused = baseMiuiPhoneWindowManager.mProximitySensorWrapper = new ProximitySensorWrapper(baseMiuiPhoneWindowManager.mContext);
                    BaseMiuiPhoneWindowManager.this.mProximitySensorWrapper.registerListener(BaseMiuiPhoneWindowManager.this.mProximitySensorListener);
                }
            }
        });
    }

    private boolean shouldInterceptKey(int keyCode, boolean isScreenOn) {
        if (this.mIsVRMode) {
            Slog.w(DisplayPolicy.TAG, "VR mode drop all keys.");
            return true;
        } else if (SystemProperties.getInt("sys.in_shutdown_progress", 0) == 1) {
            Slog.w(DisplayPolicy.TAG, "this device is being shut down, ignore key event.");
            return true;
        } else if (isScreenOn || (4 != keyCode && 82 != keyCode)) {
            return false;
        } else {
            Slog.w(DisplayPolicy.TAG, "Drop back or menu key when screen is off");
            return true;
        }
    }

    private boolean markKeyPressAndHandleKeyCombin(int keyCode, boolean down) {
        if (this.mKeyPressing == 0) {
            resetKeyStatus();
        }
        int keyBitMask = getKeyBitmask(keyCode);
        if (down) {
            this.mKeyPressed |= keyBitMask;
            this.mKeyPressing |= keyBitMask;
        } else {
            this.mKeyPressing &= ~keyBitMask;
        }
        if (handleKeyCombination()) {
            return true;
        }
        if (!this.mShortcutTriggered || (this.mShortcutPressing & keyBitMask) == 0 || down) {
            return false;
        }
        removeKeyLongPress(keyCode);
        this.mShortcutPressing &= ~keyBitMask;
        return true;
    }

    /* access modifiers changed from: protected */
    public int interceptKeyBeforeQueueingInternal(KeyEvent event, int policyFlags, boolean isScreenOn) {
        int keyCode;
        boolean keyguardActive;
        int keyCode2;
        String str;
        KeyEvent keyEvent = event;
        int policyFlags2 = policyFlags;
        boolean z = isScreenOn;
        if (!this.mSystemBooted) {
            return 0;
        }
        int keyCode3 = event.getKeyCode();
        boolean down = event.getAction() == 0;
        int repeatCount = event.getRepeatCount();
        boolean isInjected = (16777216 & policyFlags2) != 0;
        MiuiKeyguardServiceDelegate miuiKeyguardServiceDelegate = this.mMiuiKeyguardDelegate;
        boolean keyguardActive2 = miuiKeyguardServiceDelegate != null && (!z ? miuiKeyguardServiceDelegate.isShowing() : miuiKeyguardServiceDelegate.isShowingAndNotHidden());
        Slog.w("BaseMiuiPhoneWindowManager", "keyCode:" + keyCode3 + " down:" + down + " eventTime:" + event.getEventTime() + " downTime:" + event.getDownTime() + " policyFlags:" + Integer.toHexString(policyFlags) + " flags:" + Integer.toHexString(event.getFlags()) + " deviceId:" + event.getDeviceId() + " isScreenOn:" + z + " keyguardActive:" + keyguardActive2 + " repeatCount:" + repeatCount);
        AnrMonitor.screenHangMonitor(keyCode3, down, getPowerLongPressTimeOut());
        if (down) {
            trackDumpLogKeyCode(event);
        }
        if (isTrackInputEvenForScreenRecorder(event)) {
            sendKeyEventBroadcast(event);
        }
        if (isTrackInputEventForVoiceAssist(event)) {
            sendVoiceAssistKeyEventBroadcast(event);
        }
        if (keyCode3 == 4) {
            sendBackKeyEventBroadcast(event);
        }
        if (shouldInterceptKey(keyCode3, z)) {
            return 0;
        }
        if (keyCode3 != 82 || this.mRequestShowMenu) {
            keyCode = keyCode3;
        } else {
            keyCode = 187;
        }
        if (interceptPowerKeyByFingerPrintKey() != 0) {
            return 0;
        }
        if (isFingerPrintKey(event)) {
            return processFingerprintNavigationEvent(keyEvent, z);
        }
        if (markKeyPressAndHandleKeyCombin(keyCode, down)) {
            return intercept(keyEvent, policyFlags2, z, 0);
        }
        if (keyCode != 26 || !this.mTestModeEnabled) {
            if (keyCode == 25 && down && (str = this.mLongPressPowerKey) != null && this.mHandler.hasMessages(1, str)) {
                this.mHandler.removeMessages(1, this.mLongPressPowerKey);
            }
            if (keyCode != KEYCODE_AI || this.mTestModeEnabled) {
                if ((!(keyCode == 259) || !down) || AccessController.PACKAGE_CAMERA.equals(this.mFocusedWindow.getOwningPackage())) {
                    if (keyCode == 3) {
                        if (down && repeatCount == 0) {
                            this.mKeyguardOnWhenHomeDown = this.mMiuiKeyguardDelegate.isShowingAndNotHidden();
                            if (this.mDpadCenterDown) {
                                this.mHomeDownAfterDpCenter = true;
                            }
                        }
                        if (this.mFrontFingerprintSensor && inFingerprintEnrolling()) {
                            return 0;
                        }
                    }
                    if (keyCode == 25) {
                        if (!down) {
                            this.mVolumeDownKeyPressed = false;
                            cancelPendingAccessibilityShortcutAction();
                        } else if (z && !this.mVolumeDownKeyPressed) {
                            if ((event.getFlags() & 1024) == 0) {
                                this.mVolumeDownKeyPressed = true;
                                this.mVolumeDownKeyTime = event.getDownTime();
                                this.mVolumeDownKeyConsumed = false;
                                interceptAccessibilityShortcutChord(keyguardActive2);
                            }
                        }
                    } else if (keyCode == 24) {
                        if (!down) {
                            this.mVolumeUpKeyPressed = false;
                            cancelPendingAccessibilityShortcutAction();
                        } else if (z && !this.mVolumeUpKeyPressed) {
                            if ((event.getFlags() & 1024) == 0) {
                                this.mVolumeUpKeyPressed = true;
                                this.mVolumeUpKeyTime = event.getDownTime();
                                this.mVolumeUpKeyConsumed = false;
                                interceptAccessibilityShortcutChord(keyguardActive2);
                            }
                        }
                    }
                    MiuiScreenOnProximityLock miuiScreenOnProximityLock = this.mProximitySensor;
                    if (miuiScreenOnProximityLock != null && miuiScreenOnProximityLock.shouldBeBlocked(isScreenOnFully(), keyEvent)) {
                        return intercept(keyEvent, policyFlags2, z, 0);
                    }
                    if (z) {
                        keyCode2 = keyCode;
                        keyguardActive = keyguardActive2;
                        if (this.mAutoDisableScreenButtonsManager.handleDisableButtons(keyCode, down, this.mSingleKeyUse, !this.mSmartCoverManager.getSmartCoverLidOpen() && this.mDefaultDisplayPolicy.getLidState() == 0, event)) {
                            return 0;
                        }
                    } else {
                        keyCode2 = keyCode;
                        keyguardActive = keyguardActive2;
                    }
                    if (!down) {
                        removeKeyLongPress(keyCode2);
                    } else if (event.getRepeatCount() == 0) {
                        if (this.mKeyPressed == getKeyBitmask(keyCode2)) {
                            postKeyLongPress(keyCode2, keyguardActive);
                        } else {
                            removeKeyLongPress(keyCode2);
                        }
                    }
                    if (keyCode2 == 224 && !z && keyguardActive && this.mProximitySensor != null) {
                        registerProximitySensor();
                    }
                    streetSnap(z, keyCode2, down, keyEvent);
                    if (this.mVolumeKeyLaunchCamera && interceptVoluemeKeyStartCamera(event, policyFlags, isScreenOn)) {
                        return intercept(keyEvent, policyFlags2, z, 0);
                    }
                    if (interceptPowerKey(keyEvent, z)) {
                        return intercept(keyEvent, policyFlags2, z, 0);
                    }
                    if (interceptHomeKeyStartNfc(event, policyFlags, isScreenOn)) {
                        return intercept(keyEvent, policyFlags2, z, 0);
                    }
                    if (!z && !isInjected) {
                        boolean isWakeKey = true;
                        boolean allowToWake = false;
                        if (keyCode2 == 27) {
                            allowToWake = this.mCameraKeyWakeScreen;
                        } else if (keyCode2 != 272) {
                            isWakeKey = false;
                        } else {
                            allowToWake = this.mTrackballWakeScreen;
                        }
                        if (isWakeKey) {
                            if (!allowToWake) {
                                policyFlags2 = (~getWakePolicyFlag()) & policyFlags2;
                            } else if (down) {
                                return intercept(keyEvent, policyFlags2, z, 0);
                            } else {
                                if (keyguardActive) {
                                    this.mMiuiKeyguardDelegate.onWakeKeyWhenKeyguardShowingTq(26, false);
                                }
                                return intercept(keyEvent, policyFlags2, z, 1);
                            }
                        }
                    } else if (this.mCameraKeyWakeScreen && keyguardActive && keyCode2 == 27 && !down) {
                        return intercept(keyEvent, policyFlags2, z, -1);
                    }
                    if (shouldInterceptHeadSetHookKey(keyCode2, keyEvent)) {
                        return 0;
                    }
                    boolean z2 = keyguardActive;
                    if (sendOthersBroadcast(down, isScreenOn, keyguardActive, keyCode2, event)) {
                        return intercept(keyEvent, policyFlags2, z, 0);
                    }
                    if (down && (24 == keyCode2 || 25 == keyCode2 || 26 == keyCode2)) {
                        this.mSuperWaitingKey.add(Integer.valueOf(keyCode2));
                    }
                    playSoundEffect(policyFlags2, keyCode2, down, repeatCount);
                    return callSuperInterceptKeyBeforeQueueing(keyEvent, policyFlags2, z);
                }
                this.mCameraIntent = getCameraIntent("stabilizer");
                try {
                    this.mHelpKeyWakeLock.acquire(5000);
                    this.mContext.startActivityAsUser(this.mCameraIntent, UserHandle.CURRENT);
                    return 0;
                } catch (ActivityNotFoundException e) {
                    Slog.e(DisplayPolicy.TAG, "mCameraIntent problem", e);
                    return 0;
                }
            } else if (miui.os.Build.IS_GLOBAL_BUILD) {
                handleAiKeyEvent(keyEvent, down);
                callSuperInterceptKeyBeforeQueueing(event, policyFlags, isScreenOn);
                return 0;
            } else {
                Intent intent = new Intent("android.intent.action.ASSIST");
                intent.setPackage("com.miui.voiceassist");
                intent.putExtra("key_action", event.getAction());
                intent.putExtra("key_event_time", event.getEventTime());
                launchVoiceAssistant(intent, "ai_key");
                this.mAiKeyWakeLock.acquire(5000);
                return 0;
            }
        } else {
            Slog.w("BaseMiuiPhoneWindowManager", "Ignoring POWER because mTestModeEnabled = " + this.mTestModeEnabled);
            return 1;
        }
    }

    private void interceptAccessibilityShortcutChord(boolean keyguardActive) {
        if (this.mVolumeDownKeyPressed && this.mVolumeUpKeyPressed && this.mTalkBackIsOpened) {
            long now = SystemClock.uptimeMillis();
            if (now <= this.mVolumeDownKeyTime + 150 && now <= this.mVolumeUpKeyTime + 150) {
                this.mVolumeDownKeyConsumed = true;
                this.mVolumeUpKeyConsumed = true;
                if (!this.mShortcutServiceIsTalkBack || (keyguardActive && !this.mAccessibilityShortcutOnLockScreen)) {
                    Handler handler = this.mHandler;
                    handler.sendMessageDelayed(handler.obtainMessage(1, "close_talkback"), 3000);
                }
            }
        }
    }

    private void cancelPendingAccessibilityShortcutAction() {
        this.mHandler.removeMessages(1, "close_talkback");
    }

    private boolean closeTorchWhenScreenOff(boolean isScreenOn) {
        if (!this.mTorchEnabled || isScreenOn) {
            return false;
        }
        setTorch(false);
        return true;
    }

    private void streetSnap(boolean isScreenOn, int keyCode, boolean down, KeyEvent event) {
        if (!isScreenOn && this.mLongPressVolumeDownBehavior == 1) {
            Intent keyIntent = null;
            if (keyCode == 24 || keyCode == 25) {
                keyIntent = new Intent("miui.intent.action.CAMERA_KEY_BUTTON");
            } else if (down && keyCode == 26) {
                keyIntent = new Intent("android.intent.action.KEYCODE_POWER_UP");
            }
            if (keyIntent != null) {
                keyIntent.setClassName(AccessController.PACKAGE_CAMERA, "com.android.camera.snap.SnapKeyReceiver");
                keyIntent.putExtra("key_code", keyCode);
                keyIntent.putExtra("key_action", event.getAction());
                keyIntent.putExtra("key_event_time", event.getEventTime());
                this.mContext.sendBroadcastAsUser(keyIntent, UserHandle.CURRENT);
            }
        }
    }

    private boolean shouldInterceptHeadSetHookKey(int keyCode, KeyEvent event) {
        if (!this.mMikeymodeEnabled || keyCode != 79) {
            return false;
        }
        Intent mikeyIntent = new Intent("miui.intent.action.MIKEY_BUTTON");
        mikeyIntent.setPackage("com.xiaomi.miclick");
        mikeyIntent.putExtra("key_action", event.getAction());
        mikeyIntent.putExtra("key_event_time", event.getEventTime());
        this.mContext.sendBroadcast(mikeyIntent);
        return true;
    }

    private boolean sendOthersBroadcast(boolean down, boolean isScreenOn, boolean keyguardActive, int keyCode, KeyEvent event) {
        IStatusBarService statusBarService;
        int i = keyCode;
        KeyEvent keyEvent = event;
        if (down) {
            if (isScreenOn && !keyguardActive && (i == 26 || i == 25 || i == 24 || i == 164 || i == 85 || i == 79)) {
                Intent i2 = new Intent("miui.intent.action.KEYCODE_EXTERNAL");
                i2.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                i2.addFlags(1073741824);
                sendAsyncBroadcast(i2);
            }
            boolean stopNotification = i == 26;
            if (!stopNotification && keyguardActive && (i == 25 || i == 24 || i == 164)) {
                stopNotification = true;
            }
            if (stopNotification && this.mSystemReady && (statusBarService = getStatusBarService()) != null) {
                onStatusBarPanelRevealed(statusBarService);
            }
            if (i == 25 || i == 24) {
                ContentResolver cr = this.mContext.getContentResolver();
                String proc = Settings.System.getString(cr, "remote_control_proc_name");
                String pkg = Settings.System.getString(cr, "remote_control_pkg_name");
                if (!(proc == null || pkg == null)) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    if (checkProcessRunning(proc)) {
                        Intent i3 = new Intent("miui.intent.action.REMOTE_CONTROL");
                        i3.setPackage(pkg);
                        i3.addFlags(1073741824);
                        i3.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                        sendAsyncBroadcast(i3);
                        return true;
                    }
                    Settings.System.putString(cr, "remote_control_proc_name", (String) null);
                    Settings.System.putString(cr, "remote_control_pkg_name", (String) null);
                }
            }
        } else if (i == 26) {
            sendAsyncBroadcast(new Intent("android.intent.action.KEYCODE_POWER_UP"));
        }
        return false;
    }

    private boolean inFingerprintEnrolling() {
        try {
            return "com.android.settings.NewFingerprintInternalActivity".equals(((ActivityManager) this.mContext.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getClassName());
        } catch (Exception e) {
            Slog.e(DisplayPolicy.TAG, "Exception", e);
        }
    }

    /* access modifiers changed from: private */
    public void setStatusBarInFullscreen(boolean show) {
        this.mIsStatusBarVisibleInFullscreen = show;
        try {
            IStatusBarService statusbar = getStatusBarService();
            if (statusbar != null) {
                statusbar.disable(show ? Integer.MIN_VALUE : 0, this.mBinder, PackageManagerService.PLATFORM_PACKAGE_NAME);
            }
        } catch (RemoteException e) {
            Slog.e(DisplayPolicy.TAG, "RemoteException", e);
            this.mStatusBarService = null;
        }
        try {
            this.mWindowManager.updateRotation(false, true);
        } catch (RemoteException e2) {
            Slog.e(DisplayPolicy.TAG, "RemoteException", e2);
        }
    }

    /* access modifiers changed from: protected */
    public void registerStatusBarInputEventReceiver() {
    }

    /* access modifiers changed from: protected */
    public void unregisterStatusBarInputEventReceiver() {
    }

    protected class StatusBarPointEventTracker {
        private float mDownX = -1.0f;
        private float mDownY = -1.0f;

        public StatusBarPointEventTracker() {
        }

        /* access modifiers changed from: protected */
        public void onTrack(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mDownX = motionEvent.getRawX();
                this.mDownY = motionEvent.getRawY();
            } else if (actionMasked == 1 || actionMasked == 2 || actionMasked == 3) {
                float statusBarExpandHeight = BaseMiuiPhoneWindowManager.this.mContext.getResources().getFraction(R.fraction.config_full_screen_expand_status_bar_height_ratio, 0, 0);
                float f = this.mDownY;
                if (statusBarExpandHeight >= f && f != -1.0f) {
                    float distanceX = Math.abs(this.mDownX - motionEvent.getRawX());
                    float distanceY = Math.abs(this.mDownY - motionEvent.getRawY());
                    if (2.0f * distanceX <= distanceY && 0.0f <= distanceY) {
                        BaseMiuiPhoneWindowManager.this.setStatusBarInFullscreen(true);
                        this.mDownY = 0.0f;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean setTorch(boolean enable) {
        if (!this.mHasCameraFlash) {
            return false;
        }
        Intent intent = new Intent("miui.intent.action.TOGGLE_TORCH");
        intent.putExtra("miui.intent.extra.IS_ENABLE", enable);
        this.mContext.sendBroadcast(intent);
        return true;
    }

    private void postKeyLongPress(int keyCode, boolean underKeyguard) {
        if (keyCode != 3) {
            if (keyCode == 4) {
                postKeyFunction(this.mLongPressBackKey, this.mKeyLongPressTimeout, "long_press_back_key");
            } else if (keyCode == 26) {
                postKeyFunction(this.mLongPressPowerKey, this.mKeyLongPressTimeout, "long_press_power_key");
            } else if (keyCode != 82 && keyCode != 187) {
            } else {
                if (underKeyguard && !TextUtils.isEmpty(this.mLongPressMenuKeyWhenLock) && !"none".equals(this.mLongPressMenuKeyWhenLock)) {
                    postKeyFunction(this.mLongPressMenuKeyWhenLock, this.mKeyLongPressTimeout, "long_press_menu_key_when_lock");
                } else if (this.mPressToAppSwitch || !"show_menu".equals(this.mLongPressMenuKey)) {
                    postKeyFunction(this.mLongPressMenuKey, this.mKeyLongPressTimeout, "long_press_menu_key");
                }
            }
        } else if (!this.mFrontFingerprintSensor || ((!this.mSupportTapFingerprintSensorToHome && !"capricorn".equals(miui.os.Build.DEVICE)) || !underKeyguard)) {
            postKeyFunction(this.mLongPressHomeKey, this.mKeyLongPressTimeout, "long_press_home_key");
        }
    }

    private void removeKeyLongPress(int keyCode) {
        if (keyCode == 3) {
            removeKeyFunction(this.mLongPressHomeKey);
        } else if (keyCode == 4) {
            removeKeyFunction(this.mLongPressBackKey);
        } else if (keyCode == 26) {
            removeKeyFunction(this.mLongPressPowerKey);
        } else if (keyCode == 82 || keyCode == 187) {
            removeKeyFunction(this.mLongPressMenuKey);
            removeKeyFunction(this.mLongPressMenuKeyWhenLock);
        }
    }

    private boolean postKeyFunction(String action, int delay, String shortcut) {
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        Message message = this.mHandler.obtainMessage(1, action);
        Bundle bundle = new Bundle();
        bundle.putString("shortcut", shortcut);
        message.setData(bundle);
        this.mHandler.sendMessageDelayed(message, (long) delay);
        return true;
    }

    private void removeKeyFunction(String action) {
        this.mHandler.removeMessages(1, action);
    }

    private class H extends Handler {
        static final int MSG_KEY_FUNCTION = 1;

        private H() {
        }

        public void handleMessage(Message msg) {
            String originalShortcut;
            if (!BaseMiuiPhoneWindowManager.this.mShortcutTriggered && BaseMiuiPhoneWindowManager.this.mPowerManager.isScreenOn()) {
                String shortcut = msg.getData().getString("shortcut");
                boolean triggered = false;
                String effectKey = "virtual_key_longpress";
                String action = (String) msg.obj;
                if (action != null) {
                    if ("launch_camera".equals(action)) {
                        BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager = BaseMiuiPhoneWindowManager.this;
                        triggered = baseMiuiPhoneWindowManager.launchApp(baseMiuiPhoneWindowManager.getCameraIntent(shortcut));
                    } else if ("screen_shot".equals(action)) {
                        BaseMiuiPhoneWindowManager.this.mHandler.removeCallbacks(BaseMiuiPhoneWindowManager.this.getScreenshotChordLongPress());
                        BaseMiuiPhoneWindowManager.this.mHandler.post(BaseMiuiPhoneWindowManager.this.getScreenshotChordLongPress());
                        BaseMiuiPhoneWindowManager.sendRecordCountEvent(BaseMiuiPhoneWindowManager.this.mContext, PhoneWindowManager.SYSTEM_DIALOG_REASON_SCREENSHOT, "key_shortcut");
                        triggered = true;
                    } else if ("launch_voice_assistant".equals(action)) {
                        triggered = BaseMiuiPhoneWindowManager.this.launchVoiceAssistant(new Intent("android.intent.action.ASSIST"), shortcut);
                        effectKey = "screen_button_voice_assist";
                    } else if ("launch_google_search".equals(action)) {
                        Bundle args = new Bundle();
                        args.putInt("android.intent.extra.ASSIST_INPUT_DEVICE_ID", -1);
                        triggered = BaseMiuiPhoneWindowManager.this.launchAssistAction((String) null, args);
                        effectKey = "screen_button_voice_assist";
                    } else if ("go_to_sleep".equals(action)) {
                        BaseMiuiPhoneWindowManager.this.mPowerManager.goToSleep(SystemClock.uptimeMillis());
                        triggered = true;
                    } else if ("turn_on_torch".equals(action)) {
                        TelecomManager telecomManager = BaseMiuiPhoneWindowManager.this.getTelecommService();
                        if (BaseMiuiPhoneWindowManager.this.mWifiOnly || (telecomManager != null && telecomManager.getCallState() == 0)) {
                            BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager2 = BaseMiuiPhoneWindowManager.this;
                            triggered = baseMiuiPhoneWindowManager2.setTorch(true ^ baseMiuiPhoneWindowManager2.mTorchEnabled);
                        }
                    } else if ("close_app".equals(action)) {
                        triggered = BaseMiuiPhoneWindowManager.this.closeApp("close_app".equals(BaseMiuiPhoneWindowManager.this.mLongPressBackKey));
                    } else if ("show_menu".equals(action)) {
                        triggered = BaseMiuiPhoneWindowManager.this.showMenu();
                    } else if ("mi_pay".equals(action)) {
                        Intent nfcIntent = BaseMiuiPhoneWindowManager.this.getNfcIntent();
                        nfcIntent.putExtra("event_source", "double_click_power");
                        triggered = BaseMiuiPhoneWindowManager.this.launchApp(nfcIntent);
                    } else if ("dump_log".equals(action)) {
                        BaseMiuiPhoneWindowManager.this.sendAsyncBroadcast(BaseMiuiPhoneWindowManager.this.getDumpLogIntent());
                        BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager3 = BaseMiuiPhoneWindowManager.this;
                        Toast unused = baseMiuiPhoneWindowManager3.makeAllUserToastAndShow(baseMiuiPhoneWindowManager3.mContext.getString(R.string.start_dump_log), 0);
                        triggered = true;
                    } else if ("launch_recents".equals(action)) {
                        BaseMiuiPhoneWindowManager.this.preloadRecentApps();
                        triggered = BaseMiuiPhoneWindowManager.this.launchRecentPanel();
                    } else if ("split_screen".equals(action)) {
                        BaseMiuiPhoneWindowManager.this.toggleSplitScreenInternal();
                        triggered = true;
                    } else if ("close_talkback".equals(action)) {
                        if (BaseMiuiPhoneWindowManager.this.mVolumeDownKeyConsumed && BaseMiuiPhoneWindowManager.this.mVolumeUpKeyConsumed) {
                            BaseMiuiPhoneWindowManager.this.closeTalkBack();
                            Slog.e("combine volume key", "talkback is closed");
                        }
                    } else if ("none".equals(action) && (originalShortcut = Settings.System.getStringForUser(BaseMiuiPhoneWindowManager.this.mContext.getContentResolver(), "key_tips", -2)) != null && originalShortcut.equals(shortcut)) {
                        Settings.System.putStringForUser(BaseMiuiPhoneWindowManager.this.mContext.getContentResolver(), "key_tips", "key_none", -2);
                        BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager4 = BaseMiuiPhoneWindowManager.this;
                        triggered = baseMiuiPhoneWindowManager4.launchApp(baseMiuiPhoneWindowManager4.getPowerGuideIntent());
                    }
                    if (triggered) {
                        BaseMiuiPhoneWindowManager.this.mHapticFeedbackUtil.performHapticFeedback(effectKey, false);
                        BaseMiuiPhoneWindowManager.this.markShortcutTriggered();
                    }
                    super.handleMessage(msg);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void closeTalkBack() {
        SettingsStringUtil.SettingStringHelper settingStringHelper = this.mAccessibilityShortcutSetting;
        settingStringHelper.write(SettingsStringUtil.ComponentNameSet.remove(settingStringHelper.read(), talkBackServiceName));
    }

    /* access modifiers changed from: private */
    public Intent getPowerGuideIntent() {
        Intent powerGuideIntent = new Intent();
        powerGuideIntent.setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.guidePage.PowerGuideDialogActivityV2");
        powerGuideIntent.putExtra("showSwitchNotice", true);
        powerGuideIntent.addFlags(805306368);
        return powerGuideIntent;
    }

    /* access modifiers changed from: private */
    public boolean launchApp(Intent intent) {
        if (!isUserSetupComplete()) {
            return false;
        }
        try {
            intent.addFlags(268435456);
            this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
            return true;
        } catch (ActivityNotFoundException e) {
            Slog.e(DisplayPolicy.TAG, "ActivityNotFoundException", e);
            return false;
        } catch (IllegalStateException e2) {
            Slog.e(DisplayPolicy.TAG, "IllegalStateException", e2);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean launchVoiceAssistant(Intent intent, String shortcut) {
        if (!isUserSetupComplete()) {
            return false;
        }
        try {
            intent.putExtra("voice_assist_start_from_key", shortcut);
            ResolveInfo info = AppGlobals.getPackageManager().resolveIntent(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 65536, 0);
            ComponentName componentName = ComponentName.unflattenFromString(this.mContext.getResources().getString(R.string.config_xiaoaiComponent));
            if (!(info == null || info.activityInfo == null || !componentName.getPackageName().equals(info.activityInfo.packageName))) {
                intent.setComponent(componentName);
                if (this.mContext.startForegroundServiceAsUser(intent, UserHandle.CURRENT) != null) {
                    return true;
                }
            }
            intent.setComponent((ComponentName) null);
            Slog.w(DisplayPolicy.TAG, "launchVoiceAssistant startAcitvity");
            return launchApp(intent);
        } catch (RemoteException e) {
            Slog.e(DisplayPolicy.TAG, "RemoteException", e);
            return false;
        } catch (SecurityException e2) {
            Slog.e(DisplayPolicy.TAG, "SecurityException", e2);
            return false;
        } catch (IllegalStateException e3) {
            Slog.e(DisplayPolicy.TAG, "IllegalStateException", e3);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public long getPowerLongPressTimeOut() {
        String str = this.mLongPressPowerKey;
        if (str == null || "none".equals(str) || !isUserSetupComplete()) {
            return ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout();
        }
        return 3000;
    }

    /* access modifiers changed from: private */
    public boolean closeApp(boolean isTriggeredByBack) {
        WindowManagerPolicy.WindowState _win;
        if (isTriggeredByBack) {
            _win = this.mWin;
        } else {
            _win = this.mFocusedWindow;
        }
        if (_win == null || _win.getAttrs() == null) {
            return false;
        }
        int type = _win.getAttrs().type;
        if ((type < 1 || type > 99) && (type < 1000 || type > 1999)) {
            return false;
        }
        String title = null;
        String packageName = _win.getAttrs().packageName;
        PackageManager pm = this.mContext.getPackageManager();
        int OwningUserId = UserHandle.getUserId(_win.getOwningUid());
        try {
            String className = _win.getAttrs().getTitle().toString();
            int index = className.lastIndexOf(47);
            if (index >= 0) {
                title = pm.getActivityInfo(new ComponentName(packageName, (String) className.subSequence(index + 1, className.length())), 0).loadLabel(pm).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(DisplayPolicy.TAG, "NameNotFoundException", e);
        }
        try {
            if (TextUtils.isEmpty(title)) {
                title = pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString();
            }
        } catch (PackageManager.NameNotFoundException e2) {
            Slog.e(DisplayPolicy.TAG, "NameNotFoundException", e2);
        }
        if (TextUtils.isEmpty(title)) {
            title = packageName;
        }
        if (packageName.equals("com.miui.home")) {
            return true;
        }
        if (this.mSystemKeyPackages.contains(packageName)) {
            makeAllUserToastAndShow(this.mContext.getString(R.string.force_exit_skip_message, new Object[]{title}), 0);
            return true;
        }
        try {
            finishActivityInternal(_win.getAttrs().token, 0, (Intent) null);
        } catch (RemoteException e3) {
            Slog.e(DisplayPolicy.TAG, "RemoteException", e3);
        }
        forceStopPackage(packageName, OwningUserId, "key shortcut");
        makeAllUserToastAndShow(this.mContext.getString(R.string.force_exit_message, new Object[]{title}), 0);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean launchAssistAction(String hint, Bundle args) {
        sendCloseSystemWindows(PhoneWindowManager.SYSTEM_DIALOG_REASON_ASSIST);
        if (!isUserSetupComplete()) {
            return false;
        }
        if ((this.mContext.getResources().getConfiguration().uiMode & 15) == 4) {
            ((SearchManager) this.mContext.getSystemService("search")).launchLegacyAssist(hint, UserHandle.myUserId(), args);
            return true;
        }
        launchAssistActionInternal(hint, args);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean launchRecentPanel() {
        sendCloseSystemWindows(PhoneWindowManager.SYSTEM_DIALOG_REASON_RECENT_APPS);
        if (keyguardOn()) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
        TelecomManager telecomManager = getTelecommService();
        if (this.mWifiOnly || telecomManager == null || !telecomManager.isRinging() || !isInCallScreenShowing()) {
            launchRecentPanelInternal();
            return true;
        }
        Slog.i(DisplayPolicy.TAG, "Ignoring recent apps button; there's a ringing incoming call.");
        return true;
    }

    /* access modifiers changed from: private */
    public void preloadRecentApps() {
        preloadRecentAppsInternal();
    }

    /* access modifiers changed from: private */
    public boolean showMenu() {
        this.mRequestShowMenu = true;
        this.mHapticFeedbackUtil.performHapticFeedback("virtual_key_longpress", false);
        markShortcutTriggered();
        injectEvent(82);
        return false;
    }

    private void injectEvent(int injectKeyCode) {
        long now = SystemClock.uptimeMillis();
        long j = now;
        long j2 = now;
        int i = injectKeyCode;
        KeyEvent homeUp = new KeyEvent(j, j2, 0, i, 0, 0, -1, 0);
        KeyEvent homeUp2 = new KeyEvent(j, j2, 1, i, 0, 0, -1, 0);
        InputManager.getInstance().injectInputEvent(homeUp, 0);
        InputManager.getInstance().injectInputEvent(homeUp2, 0);
    }

    private AudioManager getAudioManager() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        }
        return this.mAudioManager;
    }

    private void playSoundEffect(int policyFlags, int keyCode, boolean down, int repeatCount) {
        if (down && (policyFlags & 2) != 0 && repeatCount == 0 && !this.mVibrator.hasVibrator() && !hasNavigationBar()) {
            if (keyCode == 3 || keyCode == 4 || keyCode == 82 || keyCode == 84 || keyCode == 187) {
                playSoundEffect();
            }
        }
    }

    private boolean playSoundEffect() {
        AudioManager audioManager = getAudioManager();
        if (audioManager == null) {
            return false;
        }
        audioManager.playSoundEffect(0);
        return true;
    }

    public boolean performHapticFeedback(int uid, String packageName, int effectId, boolean always, String reason) {
        if (this.mHapticFeedbackUtil.isSupportedEffect(effectId)) {
            return this.mHapticFeedbackUtil.performHapticFeedback(effectId, always);
        }
        return super.performHapticFeedback(uid, packageName, effectId, always, reason);
    }

    /* access modifiers changed from: private */
    public boolean isGameMode() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), KEY_GAME_BOOSTER, 0) == 1;
    }

    /* access modifiers changed from: private */
    public void handleTouchFeatureDirectionModeChange() {
        if (this.mRotationWatcher == null) {
            this.mRotationWatcher = new RotationWatcher();
        }
        if (this.mIsSupportGloablTounchDirection || isGameMode() || this.mIsSupportEdgeMode) {
            int rotation = this.mContext.getDisplay().getRotation();
            Slog.d(DisplayPolicy.TAG, "set rotation = " + rotation);
            ITouchFeature.getInstance().setTouchMode(8, rotation);
            try {
                this.mWindowManager.watchRotation(this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.mWindowManager.removeRotationWatcher(this.mRotationWatcher);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
    }

    class RotationWatcher extends IRotationWatcher.Stub {
        RotationWatcher() {
        }

        public void onRotationChanged(int i) throws RemoteException {
            Slog.d(DisplayPolicy.TAG, "rotation changed = " + i);
            ITouchFeature.getInstance().setTouchMode(8, i);
            if (!BaseMiuiPhoneWindowManager.this.isGameMode() && BaseMiuiPhoneWindowManager.this.mIsSupportEdgeMode) {
                BaseMiuiPhoneWindowManager.this.handleEdgeModeFeatureDirectionModeChange();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleEdgeModeFeatureDirectionModeChange() {
        int rotation = this.mContext.getDisplay().getRotation();
        if (this.mEdgeSettingsUtils == null) {
            this.mEdgeSettingsUtils = EdgeSettingsUtils.getInstance(this.mContext);
        }
        ArrayList<Integer> list = this.mEdgeSettingsUtils.getSuppressionRect(rotation, this.mEdgeModeSize);
        ITouchFeature.getInstance().setEdgeMode(15, list, list.size());
    }

    class MiuiSettingsObserver extends ContentObserver {
        MiuiSettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver resolver = BaseMiuiPhoneWindowManager.this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor("trackball_wake_screen"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("camera_key_preferred_action_type"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("camera_key_preferred_action_shortcut_id"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("volumekey_wake_screen"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("volumekey_launch_camera"), false, this, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("auto_test_mode_on"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("single_key_use_enable"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("key_bank_card_in_ese"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("key_trans_card_in_ese"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("key_long_press_volume_down"), false, this, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("torch_state"), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor("screen_key_press_app_switch"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor(BaseMiuiPhoneWindowManager.SYSTEM_SETTINGS_VR_MODE), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("enable_mikey_mode"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("enabled_accessibility_services"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_shortcut_enabled"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_shortcut_target_service"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_shortcut_on_lock_screen"), false, this, -1);
            if (!miui.os.Build.IS_GLOBAL_BUILD) {
                resolver.registerContentObserver(Settings.System.getUriFor("long_press_power_key"), false, this, -1);
            }
            resolver.registerContentObserver(Settings.System.getUriFor("double_click_power_key"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("three_gesture_down"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("long_press_home_key"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("long_press_menu_key"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("long_press_menu_key_when_lock"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("long_press_back_key"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("key_combination_power_home"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("key_combination_power_menu"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("key_combination_power_back"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("long_press_power_launch_xiaoai"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("send_back_when_xiaoai_appear"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("edge_size"), false, this, -1);
            if ((!BaseMiuiPhoneWindowManager.this.mIsSupportGloablTounchDirection || BaseMiuiPhoneWindowManager.this.mIsSupportEdgeMode) && DeviceFeature.SUPPORT_GAME_MODE) {
                resolver.registerContentObserver(Settings.Secure.getUriFor(BaseMiuiPhoneWindowManager.KEY_GAME_BOOSTER), false, this, -1);
            }
            onChange(false, Settings.System.getUriFor("long_press_power_launch_xiaoai"));
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (Settings.Secure.getUriFor(BaseMiuiPhoneWindowManager.KEY_GAME_BOOSTER).equals(uri)) {
                if (!BaseMiuiPhoneWindowManager.this.mIsSupportEdgeMode) {
                    BaseMiuiPhoneWindowManager.this.handleTouchFeatureDirectionModeChange();
                } else if (!BaseMiuiPhoneWindowManager.this.isGameMode()) {
                    BaseMiuiPhoneWindowManager.this.handleEdgeModeFeatureDirectionModeChange();
                }
            } else if (Settings.System.getUriFor("long_press_power_launch_xiaoai").equals(uri)) {
                boolean z = true;
                if (Settings.System.getIntForUser(BaseMiuiPhoneWindowManager.this.mContext.getContentResolver(), "long_press_power_launch_xiaoai", 0, BaseMiuiPhoneWindowManager.this.mCurrentUserId) != 1) {
                    z = false;
                }
                boolean longPressPowerKeyLaunchXiaoai = z;
                if (miui.os.Build.IS_GLOBAL_BUILD) {
                    BaseMiuiPhoneWindowManager.this.mLongPressPowerKey = longPressPowerKeyLaunchXiaoai ? "launch_google_search" : null;
                } else if (longPressPowerKeyLaunchXiaoai) {
                    MiuiSettings.Key.setPowerKeyLaunchVoiceAssistant(BaseMiuiPhoneWindowManager.this.mContext);
                }
            } else {
                super.onChange(selfChange, uri);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:102:0x029f  */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x02d5  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x02d7  */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x02e8  */
        /* JADX WARNING: Removed duplicated region for block: B:112:0x02ea  */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x02f9  */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x031d  */
        /* JADX WARNING: Removed duplicated region for block: B:123:0x031f  */
        /* JADX WARNING: Removed duplicated region for block: B:126:0x0332  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0139  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x013b  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x014f  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x0151  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x0166  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0174  */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x0176  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0182  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x01af  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01b1  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x01be  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x01c0  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x01d4  */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x01d6  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x01df  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x01e5  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x01fb  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x01fd  */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x0212  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0214  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x0228  */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x022a  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x023c  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0271  */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x0291  */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x0293  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r14) {
            /*
                r13 = this;
                com.android.server.policy.BaseMiuiPhoneWindowManager r0 = com.android.server.policy.BaseMiuiPhoneWindowManager.this
                android.content.Context r0 = r0.mContext
                android.content.ContentResolver r0 = r0.getContentResolver()
                com.android.server.policy.BaseMiuiPhoneWindowManager r1 = com.android.server.policy.BaseMiuiPhoneWindowManager.this
                java.lang.Object r1 = r1.mLock
                monitor-enter(r1)
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r2 = r2.mContext     // Catch:{ all -> 0x034c }
                android.provider.MiuiSettings.Key.updateOldKeyFunctionToNew(r2)     // Catch:{ all -> 0x034c }
                boolean r2 = miui.os.Build.IS_GLOBAL_BUILD     // Catch:{ all -> 0x034c }
                if (r2 != 0) goto L_0x0027
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "long_press_power_key"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mLongPressPowerKey = r3     // Catch:{ all -> 0x034c }
            L_0x0027:
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "double_click_power_key"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mDoubleClickPowerKey = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "long_press_home_key"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mLongPressHomeKey = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "long_press_menu_key"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mLongPressMenuKey = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                boolean r2 = r2.hasNavigationBar()     // Catch:{ all -> 0x034c }
                if (r2 != 0) goto L_0x006b
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "long_press_menu_key_when_lock"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mLongPressMenuKeyWhenLock = r3     // Catch:{ all -> 0x034c }
                goto L_0x0070
            L_0x006b:
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                r3 = 0
                r2.mLongPressMenuKeyWhenLock = r3     // Catch:{ all -> 0x034c }
            L_0x0070:
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "long_press_back_key"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mLongPressBackKey = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "key_combination_power_home"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mKeyCombinationPowerHome = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "key_combination_power_menu"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mKeyCombinationPowerMenu = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r4 = "key_combination_power_back"
                java.lang.String r3 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r3, r4)     // Catch:{ all -> 0x034c }
                r2.mKeyCombinationPowerBack = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r3 = "screen_key_press_app_switch"
                com.android.server.policy.BaseMiuiPhoneWindowManager r4 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r4 = r4.mCurrentUserId     // Catch:{ all -> 0x034c }
                r5 = 1
                int r3 = android.provider.Settings.System.getIntForUser(r0, r3, r5, r4)     // Catch:{ all -> 0x034c }
                r4 = 0
                if (r3 == 0) goto L_0x00c1
                r3 = r5
                goto L_0x00c2
            L_0x00c1:
                r3 = r4
            L_0x00c2:
                boolean unused = r2.mPressToAppSwitch = r3     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r2 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r2 = r2.mContext     // Catch:{ all -> 0x034c }
                java.lang.String r3 = "three_gesture_down"
                java.lang.String r2 = android.provider.MiuiSettings.Key.getKeyAndGestureShortcutFunction(r2, r3)     // Catch:{ all -> 0x034c }
                if (r2 == 0) goto L_0x0102
                java.lang.String r3 = "none"
                boolean r3 = r3.equals(r2)     // Catch:{ all -> 0x034c }
                if (r3 == 0) goto L_0x00dc
                goto L_0x0102
            L_0x00dc:
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r3 = r3.mThreeGestureDown     // Catch:{ all -> 0x034c }
                if (r3 == 0) goto L_0x00ef
                java.lang.String r3 = "none"
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r6 = r6.mThreeGestureDown     // Catch:{ all -> 0x034c }
                boolean r3 = r3.equals(r6)     // Catch:{ all -> 0x034c }
                if (r3 == 0) goto L_0x00fa
            L_0x00ef:
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.WindowManagerPolicy$WindowManagerFuncs r3 = r3.mWindowManagerFuncs     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.miui.server.MiuiPointerEventListener r6 = r6.mMiuiPointerEventListener     // Catch:{ all -> 0x034c }
                r3.registerPointerEventListener(r6, r4)     // Catch:{ all -> 0x034c }
            L_0x00fa:
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.miui.server.MiuiPointerEventListener r3 = r3.mMiuiPointerEventListener     // Catch:{ all -> 0x034c }
                r3.setThreeGestureAction(r2)     // Catch:{ all -> 0x034c }
                goto L_0x0120
            L_0x0102:
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r3 = r3.mThreeGestureDown     // Catch:{ all -> 0x034c }
                if (r3 == 0) goto L_0x0120
                java.lang.String r3 = "none"
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r6 = r6.mThreeGestureDown     // Catch:{ all -> 0x034c }
                boolean r3 = r3.equals(r6)     // Catch:{ all -> 0x034c }
                if (r3 != 0) goto L_0x0120
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.android.server.policy.WindowManagerPolicy$WindowManagerFuncs r3 = r3.mWindowManagerFuncs     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                com.miui.server.MiuiPointerEventListener r6 = r6.mMiuiPointerEventListener     // Catch:{ all -> 0x034c }
                r3.unregisterPointerEventListener(r6, r4)     // Catch:{ all -> 0x034c }
            L_0x0120:
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                r3.mThreeGestureDown = r2     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                boolean r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.SUPPORT_EDGE_TOUCH_VOLUME     // Catch:{ all -> 0x034c }
                if (r6 != 0) goto L_0x013b
                java.lang.String r6 = "volumekey_launch_camera"
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r7 = r7.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r6 = android.provider.Settings.System.getIntForUser(r0, r6, r4, r7)     // Catch:{ all -> 0x034c }
                if (r6 != r5) goto L_0x013b
                r6 = r5
                goto L_0x013c
            L_0x013b:
                r6 = r4
            L_0x013c:
                r3.mVolumeKeyLaunchCamera = r6     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r6 = "trackball_wake_screen"
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r7 = r7.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r6 = android.provider.Settings.System.getIntForUser(r0, r6, r4, r7)     // Catch:{ all -> 0x034c }
                if (r6 != r5) goto L_0x0151
                r6 = r5
                goto L_0x0152
            L_0x0151:
                r6 = r4
            L_0x0152:
                r3.mTrackballWakeScreen = r6     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r6 = "enable_mikey_mode"
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r7 = r7.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r6 = android.provider.Settings.Secure.getIntForUser(r0, r6, r4, r7)     // Catch:{ all -> 0x034c }
                if (r6 == 0) goto L_0x0166
                r6 = r5
                goto L_0x0167
            L_0x0166:
                r6 = r4
            L_0x0167:
                r3.mMikeymodeEnabled = r6     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r6 = "torch_state"
                int r6 = android.provider.Settings.Global.getInt(r0, r6, r4)     // Catch:{ all -> 0x034c }
                if (r6 == 0) goto L_0x0176
                r6 = r5
                goto L_0x0177
            L_0x0176:
                r6 = r4
            L_0x0177:
                boolean unused = r3.mTorchEnabled = r6     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                boolean r3 = r3.mTorchEnabled     // Catch:{ all -> 0x034c }
                if (r3 != 0) goto L_0x018f
                com.android.server.policy.BaseMiuiPhoneWindowManager r3 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.os.Handler r3 = r3.mHandler     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.Runnable r6 = r6.mTurnOffTorch     // Catch:{ all -> 0x034c }
                r3.removeCallbacks(r6)     // Catch:{ all -> 0x034c }
            L_0x018f:
                java.lang.String r3 = "camera_key_preferred_action_type"
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r6 = r6.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r3 = android.provider.Settings.System.getIntForUser(r0, r3, r4, r6)     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                if (r5 != r3) goto L_0x01b1
                r7 = 4
                java.lang.String r8 = "camera_key_preferred_action_shortcut_id"
                r9 = -1
                com.android.server.policy.BaseMiuiPhoneWindowManager r10 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r10 = r10.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r8 = android.provider.Settings.System.getIntForUser(r0, r8, r9, r10)     // Catch:{ all -> 0x034c }
                if (r7 != r8) goto L_0x01b1
                r7 = r5
                goto L_0x01b2
            L_0x01b1:
                r7 = r4
            L_0x01b2:
                r6.mCameraKeyWakeScreen = r7     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r7 = "auto_test_mode_on"
                int r7 = android.provider.Settings.Global.getInt(r0, r7, r4)     // Catch:{ all -> 0x034c }
                if (r7 == 0) goto L_0x01c0
                r7 = r5
                goto L_0x01c1
            L_0x01c0:
                r7 = r4
            L_0x01c1:
                r6.mTestModeEnabled = r7     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r7 = "single_key_use_enable"
                com.android.server.policy.BaseMiuiPhoneWindowManager r8 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r8 = r8.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r7 = android.provider.Settings.System.getIntForUser(r0, r7, r4, r8)     // Catch:{ all -> 0x034c }
                if (r7 != r5) goto L_0x01d6
                r7 = r5
                goto L_0x01d7
            L_0x01d6:
                r7 = r4
            L_0x01d7:
                r6.mSingleKeyUse = r7     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                boolean r6 = r6.mSingleKeyUse     // Catch:{ all -> 0x034c }
                if (r6 == 0) goto L_0x01e5
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int unused = r6.mDoubleTapOnHomeBehavior = r5     // Catch:{ all -> 0x034c }
                goto L_0x01ea
            L_0x01e5:
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int unused = r6.mDoubleTapOnHomeBehavior = r4     // Catch:{ all -> 0x034c }
            L_0x01ea:
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r7 = "send_back_when_xiaoai_appear"
                com.android.server.policy.BaseMiuiPhoneWindowManager r8 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r8 = r8.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r7 = android.provider.Settings.System.getIntForUser(r0, r7, r4, r8)     // Catch:{ all -> 0x034c }
                if (r7 == 0) goto L_0x01fd
                r7 = r5
                goto L_0x01fe
            L_0x01fd:
                r7 = r4
            L_0x01fe:
                boolean unused = r6.mVoiceAssistEnabled = r7     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r7 = "key_bank_card_in_ese"
                com.android.server.policy.BaseMiuiPhoneWindowManager r8 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r8 = r8.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r7 = android.provider.Settings.Secure.getIntForUser(r0, r7, r4, r8)     // Catch:{ all -> 0x034c }
                if (r7 <= 0) goto L_0x0214
                r7 = r5
                goto L_0x0215
            L_0x0214:
                r7 = r4
            L_0x0215:
                r6.mHaveBankCard = r7     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r6 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r7 = "key_trans_card_in_ese"
                com.android.server.policy.BaseMiuiPhoneWindowManager r8 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r8 = r8.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r7 = android.provider.Settings.Secure.getIntForUser(r0, r7, r4, r8)     // Catch:{ all -> 0x034c }
                if (r7 <= 0) goto L_0x022a
                r7 = r5
                goto L_0x022b
            L_0x022a:
                r7 = r4
            L_0x022b:
                r6.mHaveTranksCard = r7     // Catch:{ all -> 0x034c }
                java.lang.String r6 = "key_long_press_volume_down"
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r7 = r7.mCurrentUserId     // Catch:{ all -> 0x034c }
                java.lang.String r6 = android.provider.Settings.Secure.getStringForUser(r0, r6, r7)     // Catch:{ all -> 0x034c }
                if (r6 == 0) goto L_0x0271
                java.lang.String r7 = "Street-snap"
                boolean r7 = r7.equals(r6)     // Catch:{ all -> 0x034c }
                if (r7 != 0) goto L_0x026b
                java.lang.String r7 = "Street-snap-picture"
                boolean r7 = r7.equals(r6)     // Catch:{ all -> 0x034c }
                if (r7 != 0) goto L_0x026b
                java.lang.String r7 = "Street-snap-movie"
                boolean r7 = r7.equals(r6)     // Catch:{ all -> 0x034c }
                if (r7 == 0) goto L_0x0255
                goto L_0x026b
            L_0x0255:
                java.lang.String r7 = "public_transportation_shortcuts"
                boolean r7 = r7.equals(r6)     // Catch:{ all -> 0x034c }
                if (r7 == 0) goto L_0x0265
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                r8 = 2
                int unused = r7.mLongPressVolumeDownBehavior = r8     // Catch:{ all -> 0x034c }
                goto L_0x0280
            L_0x0265:
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int unused = r7.mLongPressVolumeDownBehavior = r4     // Catch:{ all -> 0x034c }
                goto L_0x0280
            L_0x026b:
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int unused = r7.mLongPressVolumeDownBehavior = r5     // Catch:{ all -> 0x034c }
                goto L_0x0280
            L_0x0271:
                java.lang.String r7 = "key_long_press_volume_down"
                java.lang.String r8 = "none"
                com.android.server.policy.BaseMiuiPhoneWindowManager r9 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r9 = r9.mCurrentUserId     // Catch:{ all -> 0x034c }
                android.provider.Settings.Secure.putStringForUser(r0, r7, r8, r9)     // Catch:{ all -> 0x034c }
            L_0x0280:
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r8 = "vr_mode"
                com.android.server.policy.BaseMiuiPhoneWindowManager r9 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r9 = r9.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r8 = android.provider.Settings.System.getIntForUser(r0, r8, r4, r9)     // Catch:{ all -> 0x034c }
                if (r8 != r5) goto L_0x0293
                r8 = r5
                goto L_0x0294
            L_0x0293:
                r8 = r4
            L_0x0294:
                boolean unused = r7.mIsVRMode = r8     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                miui.provider.SettingsStringUtil$SettingStringHelper r7 = r7.mAccessibilityShortcutSetting     // Catch:{ all -> 0x034c }
                if (r7 != 0) goto L_0x02b9
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                miui.provider.SettingsStringUtil$SettingStringHelper r8 = new miui.provider.SettingsStringUtil$SettingStringHelper     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r9 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r9 = r9.mContext     // Catch:{ all -> 0x034c }
                android.content.ContentResolver r9 = r9.getContentResolver()     // Catch:{ all -> 0x034c }
                java.lang.String r10 = "enabled_accessibility_services"
                com.android.server.policy.BaseMiuiPhoneWindowManager r11 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r11 = r11.mCurrentUserId     // Catch:{ all -> 0x034c }
                r8.<init>(r9, r10, r11)     // Catch:{ all -> 0x034c }
                miui.provider.SettingsStringUtil.SettingStringHelper unused = r7.mAccessibilityShortcutSetting = r8     // Catch:{ all -> 0x034c }
            L_0x02b9:
                com.android.server.policy.BaseMiuiPhoneWindowManager r7 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                miui.provider.SettingsStringUtil$SettingStringHelper r7 = r7.mAccessibilityShortcutSetting     // Catch:{ all -> 0x034c }
                java.lang.String r7 = r7.read()     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r8 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                boolean r9 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x034c }
                if (r9 != 0) goto L_0x02d7
                android.content.ComponentName r9 = com.android.server.policy.BaseMiuiPhoneWindowManager.talkBackServiceName     // Catch:{ all -> 0x034c }
                boolean r9 = miui.provider.SettingsStringUtil.ComponentNameSet.contains(r7, r9)     // Catch:{ all -> 0x034c }
                if (r9 == 0) goto L_0x02d7
                r9 = r5
                goto L_0x02d8
            L_0x02d7:
                r9 = r4
            L_0x02d8:
                r8.mTalkBackIsOpened = r9     // Catch:{ all -> 0x034c }
                java.lang.String r8 = "accessibility_shortcut_enabled"
                com.android.server.policy.BaseMiuiPhoneWindowManager r9 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r9 = r9.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r8 = android.provider.Settings.Secure.getIntForUser(r0, r8, r5, r9)     // Catch:{ all -> 0x034c }
                if (r8 != r5) goto L_0x02ea
                r8 = r5
                goto L_0x02eb
            L_0x02ea:
                r8 = r4
            L_0x02eb:
                java.lang.String r9 = "accessibility_shortcut_target_service"
                com.android.server.policy.BaseMiuiPhoneWindowManager r10 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r10 = r10.mCurrentUserId     // Catch:{ all -> 0x034c }
                java.lang.String r9 = android.provider.Settings.Secure.getStringForUser(r0, r9, r10)     // Catch:{ all -> 0x034c }
                if (r9 != 0) goto L_0x0305
                com.android.server.policy.BaseMiuiPhoneWindowManager r10 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                android.content.Context r10 = r10.mContext     // Catch:{ all -> 0x034c }
                r11 = 17039719(0x1040167, float:2.4245577E-38)
                java.lang.String r10 = r10.getString(r11)     // Catch:{ all -> 0x034c }
                r9 = r10
            L_0x0305:
                com.android.server.policy.BaseMiuiPhoneWindowManager r10 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                if (r8 == 0) goto L_0x031f
                boolean r11 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x034c }
                if (r11 != 0) goto L_0x031f
                android.content.ComponentName r11 = android.content.ComponentName.unflattenFromString(r9)     // Catch:{ all -> 0x034c }
                android.content.ComponentName r12 = com.android.server.policy.BaseMiuiPhoneWindowManager.talkBackServiceName     // Catch:{ all -> 0x034c }
                boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x034c }
                if (r11 == 0) goto L_0x031f
                r11 = r5
                goto L_0x0320
            L_0x031f:
                r11 = r4
            L_0x0320:
                r10.mShortcutServiceIsTalkBack = r11     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r10 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r11 = "accessibility_shortcut_on_lock_screen"
                com.android.server.policy.BaseMiuiPhoneWindowManager r12 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r12 = r12.mCurrentUserId     // Catch:{ all -> 0x034c }
                int r11 = android.provider.Settings.Secure.getIntForUser(r0, r11, r4, r12)     // Catch:{ all -> 0x034c }
                if (r11 != r5) goto L_0x0333
                r4 = r5
            L_0x0333:
                r10.mAccessibilityShortcutOnLockScreen = r4     // Catch:{ all -> 0x034c }
                com.android.server.policy.BaseMiuiPhoneWindowManager r4 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                java.lang.String r5 = "edge_size"
                r10 = 1058642330(0x3f19999a, float:0.6)
                com.android.server.policy.BaseMiuiPhoneWindowManager r11 = com.android.server.policy.BaseMiuiPhoneWindowManager.this     // Catch:{ all -> 0x034c }
                int r11 = r11.mCurrentUserId     // Catch:{ all -> 0x034c }
                float r5 = android.provider.Settings.System.getFloatForUser(r0, r5, r10, r11)     // Catch:{ all -> 0x034c }
                float unused = r4.mEdgeModeSize = r5     // Catch:{ all -> 0x034c }
                monitor-exit(r1)     // Catch:{ all -> 0x034c }
                return
            L_0x034c:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x034c }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.BaseMiuiPhoneWindowManager.MiuiSettingsObserver.onChange(boolean):void");
        }
    }

    public void setCurrentUserLw(int newUserId) {
        super.setCurrentUserLw(newUserId);
        this.mCurrentUserId = newUserId;
        this.mAutoDisableScreenButtonsManager.onUserSwitch(newUserId);
        this.mSmartCoverManager.onUserSwitch(newUserId);
        this.mAccessibilityShortcutSetting.setUserId(newUserId);
        this.mSettingsObserver.onChange(false);
    }

    public void showBootMessage(final CharSequence msg, boolean always) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog == null) {
                    BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager = BaseMiuiPhoneWindowManager.this;
                    Dialog unused = baseMiuiPhoneWindowManager.mMiuiBootMsgDialog = new Dialog(baseMiuiPhoneWindowManager.mContext, R.style.MiuiBootMessageDialog) {
                        public boolean dispatchKeyEvent(KeyEvent event) {
                            return true;
                        }

                        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
                            return true;
                        }

                        public boolean dispatchTouchEvent(MotionEvent ev) {
                            return true;
                        }

                        public boolean dispatchTrackballEvent(MotionEvent ev) {
                            return true;
                        }

                        public boolean dispatchGenericMotionEvent(MotionEvent ev) {
                            return true;
                        }

                        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                            return true;
                        }
                    };
                    View view = LayoutInflater.from(BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getContext()).inflate(R.layout.boot_msg, (ViewGroup) null);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.setContentView(view);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getWindow().setType(2021);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getWindow().addFlags(UsbTerminalTypes.TERMINAL_TELE_PHONE);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getWindow().setDimAmount(1.0f);
                    WindowManager.LayoutParams lp = BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getWindow().getAttributes();
                    lp.screenOrientation = 5;
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.getWindow().setAttributes(lp);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.setCancelable(false);
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.show();
                    ImageView bootLogo = (ImageView) view.findViewById(R.id.boot_logo);
                    bootLogo.setVisibility(0);
                    if ("beryllium".equals(miui.os.Build.DEVICE)) {
                        String hwc = android.os.SystemProperties.get("ro.boot.hwc", "");
                        if (hwc.contains("INDIA")) {
                            bootLogo.setImageResource(R.drawable.boot_logo_poco_india);
                        } else if (hwc.contains("GLOBAL")) {
                            bootLogo.setImageResource(R.drawable.boot_logo_poco_global);
                        }
                    }
                    ProgressBar unused2 = BaseMiuiPhoneWindowManager.this.mBootProgress = (ProgressBar) view.findViewById(R.id.boot_progressbar);
                    BaseMiuiPhoneWindowManager.this.mBootProgress.setVisibility(4);
                    BaseMiuiPhoneWindowManager baseMiuiPhoneWindowManager2 = BaseMiuiPhoneWindowManager.this;
                    String[] unused3 = baseMiuiPhoneWindowManager2.mBootText = baseMiuiPhoneWindowManager2.mContext.getResources().getStringArray(R.array.boot_msg_text);
                    if (BaseMiuiPhoneWindowManager.this.mBootText != null && BaseMiuiPhoneWindowManager.this.mBootText.length > 0) {
                        TextView unused4 = BaseMiuiPhoneWindowManager.this.mBootTextView = (TextView) view.findViewById(R.id.boot_text);
                        BaseMiuiPhoneWindowManager.this.mBootTextView.setVisibility(4);
                    }
                }
                List<String> parseList = new ArrayList<>();
                CharSequence charSequence = msg;
                if (charSequence != null) {
                    for (String sp : String.valueOf(charSequence).replaceAll("[^0-9]", ",").split(",")) {
                        if (sp.length() > 0) {
                            parseList.add(sp);
                        }
                    }
                }
                if (parseList.size() == 2) {
                    int progress = Integer.parseInt(parseList.get(0));
                    int total = Integer.parseInt(parseList.get(1));
                    if (progress > total) {
                        int tmp = progress;
                        progress = total;
                        total = tmp;
                    }
                    if (total > 3) {
                        BaseMiuiPhoneWindowManager.this.mBootProgress.setVisibility(0);
                        BaseMiuiPhoneWindowManager.this.mBootProgress.setMax(total);
                        BaseMiuiPhoneWindowManager.this.mBootProgress.setProgress(progress);
                        if (BaseMiuiPhoneWindowManager.this.mBootTextView != null && BaseMiuiPhoneWindowManager.this.mBootText != null) {
                            BaseMiuiPhoneWindowManager.this.mBootTextView.setVisibility(0);
                            int pos = (BaseMiuiPhoneWindowManager.this.mBootText.length * progress) / total;
                            if (pos >= BaseMiuiPhoneWindowManager.this.mBootText.length) {
                                pos = BaseMiuiPhoneWindowManager.this.mBootText.length - 1;
                            }
                            BaseMiuiPhoneWindowManager.this.mBootTextView.setText(BaseMiuiPhoneWindowManager.this.mBootText[pos]);
                        }
                    }
                }
            }
        });
    }

    public void hideBootMessages() {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog != null) {
                    BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog.dismiss();
                    Dialog unused = BaseMiuiPhoneWindowManager.this.mMiuiBootMsgDialog = null;
                    ProgressBar unused2 = BaseMiuiPhoneWindowManager.this.mBootProgress = null;
                    TextView unused3 = BaseMiuiPhoneWindowManager.this.mBootTextView = null;
                    String[] unused4 = BaseMiuiPhoneWindowManager.this.mBootText = null;
                }
            }
        });
    }

    static IWindowManager getWindownManagerService() {
        IWindowManager service = IWindowManager.Stub.asInterface(ServiceManager.checkService("window"));
        if (service == null) {
            Slog.w(DisplayPolicy.TAG, "Unable to find IWindowManager interface.");
        }
        return service;
    }

    /* access modifiers changed from: package-private */
    public boolean checkProcessRunning(String processName) {
        List<ActivityManager.RunningAppProcessInfo> procs;
        ActivityManager am = (ActivityManager) this.mContext.getSystemService("activity");
        if (am == null || (procs = am.getRunningAppProcesses()) == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo info : procs) {
            if (processName.equalsIgnoreCase(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public boolean canBeHiddenByKeyguardLw(WindowManagerPolicy.WindowState win) {
        WindowManager.LayoutParams lp;
        if (win == null || (lp = win.getAttrs()) == null || (lp.extraFlags & 2048) == 0) {
            return super.canBeHiddenByKeyguardLw(win);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isPhoneOffhook() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (telephonyManager != null) {
            return telephonyManager.isOffhook();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void sendAsyncBroadcast(final Intent intent) {
        if (this.mSystemReady) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    BaseMiuiPhoneWindowManager.this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void sendAsyncBroadcast(final Intent intent, final String receiverPermission) {
        if (this.mSystemReady) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    BaseMiuiPhoneWindowManager.this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT, receiverPermission);
                }
            });
        }
    }

    public void enableScreenAfterBoot() {
        super.enableScreenAfterBoot();
        this.mWifiOnly = SystemProperties.getBoolean("ro.radio.noril", false);
        if (this.mSmartCoverManager.enableLidAfterBoot(this.mDefaultDisplayPolicy.getLidState())) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "lid_behavior", 0);
        }
    }

    public void finishLayoutLw(DisplayFrames displayFrames) {
        int inputMethodHeight = displayFrames.getInputMethodWindowVisibleHeight();
        if (this.mInputMethodWindowVisibleHeight != inputMethodHeight) {
            this.mInputMethodWindowVisibleHeight = inputMethodHeight;
            Slog.i(DisplayPolicy.TAG, "input method visible height changed " + inputMethodHeight);
            Intent intent = new Intent("miui.intent.action.INPUT_METHOD_VISIBLE_HEIGHT_CHANGED");
            intent.putExtra("miui.intent.extra.input_method_visible_height", this.mInputMethodWindowVisibleHeight);
            sendAsyncBroadcast(intent, PERMISSION_INTERNAL_GENERAL_API);
        }
    }

    /* access modifiers changed from: protected */
    public boolean getForbidFullScreenFlag() {
        return this.mForbidFullScreen;
    }

    private void handleDoubleTapOnHome() {
        if (this.mDoubleTapOnHomeBehavior == 1) {
            this.mHomeConsumed = true;
            launchRecentPanel();
        }
    }

    private boolean isNfcEnable(boolean ishomeclick) {
        if (ishomeclick) {
            if ("sagit".equals(miui.os.Build.DEVICE) || "jason".equals(miui.os.Build.DEVICE)) {
                return false;
            }
            if (this.mHaveBankCard || this.mHaveTranksCard) {
                return true;
            }
            return false;
        } else if (this.mLongPressVolumeDownBehavior != 2 || !this.mHaveTranksCard) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean interceptHomeKeyStartNfc(KeyEvent event, int policyFlags, boolean isScreenOn) {
        MiuiKeyguardServiceDelegate miuiKeyguardServiceDelegate;
        boolean intercept = false;
        if (!isNfcEnable(true)) {
            return false;
        }
        boolean down = event.getAction() == 0;
        int keyCode = event.getKeyCode();
        boolean isInjected = (16777216 & policyFlags) != 0;
        boolean KeyguardNotActive = !this.mHomeDoubleClickPending && (miuiKeyguardServiceDelegate = this.mMiuiKeyguardDelegate) != null && !miuiKeyguardServiceDelegate.isShowingAndNotHidden();
        if (isInjected || ((isScreenOn && KeyguardNotActive) || keyCode != 3 || !down || event.getRepeatCount() != 0)) {
            return false;
        }
        if (this.mHomeDoubleClickPending) {
            this.mNfcIntent = getNfcIntent();
            try {
                this.mHandler.removeCallbacks(this.mHomeDoubleClickTimeoutRunnable);
                intercept = true;
                this.mHomeDoubleClickPending = false;
                this.mHomeConsumed = true;
                this.mMiuiKeyguardDelegate.OnDoubleClickHome();
                this.mContext.startActivityAsUser(this.mNfcIntent, UserHandle.CURRENT);
                return true;
            } catch (ActivityNotFoundException e) {
                Slog.e(DisplayPolicy.TAG, "mNfcIntent problem", e);
                return intercept;
            }
        } else {
            this.mHomeDoubleClickPending = true;
            this.mHandler.postDelayed(this.mHomeDoubleClickTimeoutRunnable, CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS);
            return false;
        }
    }

    private boolean interceptPowerKey(KeyEvent event, boolean isScreenOn) {
        String str;
        if (event.getAction() != 0 || event.getKeyCode() != 26) {
            return false;
        }
        boolean interceptWhenScreenOn = false;
        boolean interceptWhenScreenOff = closeTorchWhenScreenOff(isScreenOn);
        long doubleTapInterval = event.getEventTime() - this.mLastPowerDown;
        if (doubleTapInterval >= CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS || doubleTapInterval <= 0) {
            this.mPressPowerKeyCount = 1;
        } else {
            this.mPressPowerKeyCount++;
            if (this.mPressPowerKeyCount <= 2 && (str = this.mDoubleClickPowerKey) != null && !str.equals("none")) {
                interceptWhenScreenOn = isScreenOn;
            }
        }
        this.mLastPowerDown = event.getEventTime();
        if (this.mPressPowerKeyCount == 5 && isSosEnable(this.mContext) && !isInSosMode(this.mContext)) {
            Intent intent = new Intent("miui.intent.action.LAUNCH_SOS");
            intent.setPackage("com.android.settings");
            launchApp(intent);
        }
        if (this.mPressPowerKeyCount == 2) {
            Slog.i(DisplayPolicy.TAG, "Power button double tap gesture detected, " + this.mDoubleClickPowerKey + ". Interval=" + doubleTapInterval + "ms");
            if ("launch_camera".equals(this.mDoubleClickPowerKey)) {
                launchApp(getCameraIntent("power_double_tap"));
            } else if ("turn_on_torch".equals(this.mDoubleClickPowerKey)) {
                TelecomManager telecomManager = getTelecommService();
                if (this.mWifiOnly || (telecomManager != null && telecomManager.getCallState() == 0)) {
                    setTorch(!this.mTorchEnabled);
                }
            } else if ("mi_pay".equals(this.mDoubleClickPowerKey)) {
                Intent nfcIntent = getNfcIntent();
                nfcIntent.putExtra("event_source", "double_click_power");
                launchApp(nfcIntent);
            }
        }
        if (interceptWhenScreenOn || interceptWhenScreenOff) {
            return true;
        }
        return false;
    }

    private static boolean isSosEnable(Context context) {
        return context != null && Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_enable", 0) == 1;
    }

    private static boolean isInSosMode(Context context) {
        return context != null && Settings.Secure.getInt(context.getContentResolver(), "key_is_in_miui_sos_mode", 0) == 1;
    }

    /* access modifiers changed from: protected */
    public boolean interceptVoluemeKeyStartCamera(KeyEvent event, int policyFlags, boolean isScreenOn) {
        boolean KeyguardNotActive = true;
        boolean down = event.getAction() == 0;
        int keyCode = event.getKeyCode();
        boolean isInjected = (16777216 & policyFlags) != 0;
        MiuiKeyguardServiceDelegate miuiKeyguardServiceDelegate = this.mMiuiKeyguardDelegate;
        if (miuiKeyguardServiceDelegate == null || miuiKeyguardServiceDelegate.isShowingAndNotHidden()) {
            KeyguardNotActive = false;
        }
        if (isInjected || ((isScreenOn && KeyguardNotActive) || isAudioActive() || keyCode != 25 || !down || event.getRepeatCount() != 0)) {
            return false;
        }
        long now = SystemClock.elapsedRealtime();
        if (now - this.mVolumeButtonPrePressedTime < CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS) {
            this.mVolumeButtonPressedCount++;
        } else {
            this.mVolumeButtonPressedCount = 1;
            this.mVolumeButtonPrePressedTime = now;
        }
        if (this.mVolumeButtonPressedCount < 2) {
            return false;
        }
        this.mCameraIntent = getCameraIntent("double_click_volume_down");
        try {
            this.mVolumeKeyWakeLock.acquire(5000);
            this.mContext.startActivityAsUser(this.mCameraIntent, UserHandle.CURRENT);
            return true;
        } catch (ActivityNotFoundException e) {
            Slog.e(DisplayPolicy.TAG, "mCameraIntent problem", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public Intent getNfcIntent() {
        if (this.mNfcIntent == null) {
            this.mNfcIntent = new Intent();
            this.mNfcIntent.setFlags(536870912);
            this.mNfcIntent.putExtra("StartActivityWhenLocked", true);
            this.mNfcIntent.setAction("com.miui.intent.action.DOUBLE_CLICK");
        }
        return this.mNfcIntent;
    }

    /* access modifiers changed from: private */
    public Intent getCameraIntent(String reason) {
        if (this.mCameraIntent == null) {
            this.mCameraIntent = new Intent();
            this.mCameraIntent.setFlags(276856832);
            this.mCameraIntent.putExtra("ShowCameraWhenLocked", true);
            this.mCameraIntent.putExtra("StartActivityWhenLocked", true);
            this.mCameraIntent.setAction("android.media.action.STILL_IMAGE_CAMERA");
            if (this.mCameraComponentName == null) {
                this.mCameraComponentName = ComponentName.unflattenFromString(this.mContext.getResources().getString(R.string.config_camera_component));
            }
            this.mCameraIntent.setComponent(this.mCameraComponentName);
        }
        this.mCameraIntent.putExtra("com.android.systemui.camera_launch_source", reason);
        return this.mCameraIntent;
    }

    /* access modifiers changed from: private */
    public Intent getDumpLogIntent() {
        if (this.mDumpLogIntent == null) {
            this.mDumpLogIntent = new Intent();
            this.mDumpLogIntent.setPackage("com.miui.bugreport");
            this.mDumpLogIntent.setAction("com.miui.bugreport.service.action.DUMPLOG");
        }
        return this.mDumpLogIntent;
    }

    private boolean isAudioActive() {
        boolean active = false;
        int mode = getAudioManager().getMode();
        if (mode > 0 && mode < 4) {
            return true;
        }
        int size = AudioSystem.getNumStreamTypes();
        int i = 0;
        while (i < size && (1 == i || !(active = AudioSystem.isStreamActive(i, 0)))) {
            i++;
        }
        return active;
    }

    /* access modifiers changed from: private */
    public void setScreenRecorderEnabled(boolean enable) {
        this.mScreenRecorderEnabled = enable;
    }

    private boolean isTrackInputEvenForScreenRecorder(KeyEvent event) {
        if (!this.mScreenRecorderEnabled || !sScreenRecorderKeyEventList.contains(Integer.valueOf(event.getKeyCode()))) {
            return false;
        }
        return true;
    }

    private void sendKeyEventBroadcast(KeyEvent event) {
        Intent intent = new Intent("miui.intent.SCREEN_RECORDER_TRACK_KEYEVENT");
        intent.setPackage("com.miui.screenrecorder");
        intent.putExtra("keycode", event.getKeyCode());
        intent.putExtra("isdown", event.getAction() == 0);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private void trackDumpLogKeyCode(KeyEvent event) {
        int code = event.getKeyCode();
        if (code != 25 && code != 24) {
            this.mTrackDumpLogKeyCodePengding = false;
        } else if (!this.mTrackDumpLogKeyCodePengding && code == 24) {
            this.mTrackDumpLogKeyCodePengding = true;
            this.mTrackDumpLogKeyCodeStartTime = event.getEventTime();
            this.mTrackDumpLogKeyCodeLastKeyCode = 24;
            this.mTrackDumpLogKeyCodeVolumeDownTimes = 0;
        } else if (!this.mTrackDumpLogKeyCodePengding) {
        } else {
            if (event.getEventTime() - this.mTrackDumpLogKeyCodeStartTime >= ((long) this.mTrackDumpLogKeyCodeTimeOut) || code == this.mTrackDumpLogKeyCodeLastKeyCode) {
                this.mTrackDumpLogKeyCodePengding = false;
                if (code == 24) {
                    this.mTrackDumpLogKeyCodePengding = true;
                    this.mTrackDumpLogKeyCodeStartTime = event.getEventTime();
                    this.mTrackDumpLogKeyCodeLastKeyCode = 24;
                    this.mTrackDumpLogKeyCodeVolumeDownTimes = 0;
                    return;
                }
                return;
            }
            this.mTrackDumpLogKeyCodeLastKeyCode = code;
            if (code == 25) {
                this.mTrackDumpLogKeyCodeVolumeDownTimes++;
            }
            if (this.mTrackDumpLogKeyCodeVolumeDownTimes == 3) {
                this.mTrackDumpLogKeyCodePengding = false;
                Slog.w("BaseMiuiPhoneWindowManager", "DumpLog triggered");
                this.mHandler.sendMessage(this.mHandler.obtainMessage(1, "dump_log"));
            }
        }
    }

    private boolean isTrackInputEventForVoiceAssist(KeyEvent event) {
        if (!this.mVoiceAssistEnabled || !sVoiceAssistKeyEventList.contains(Integer.valueOf(event.getKeyCode()))) {
            return false;
        }
        return true;
    }

    private void sendVoiceAssistKeyEventBroadcast(KeyEvent event) {
        Intent intent = new Intent("miui.intent.VOICE_ASSIST_TRACK_KEYEVENT");
        intent.setPackage("com.miui.voiceassist");
        intent.putExtra("keycode", event.getKeyCode());
        intent.putExtra("isdown", event.getAction() == 0);
        sendAsyncBroadcast(intent);
    }

    private void sendBackKeyEventBroadcast(KeyEvent event) {
        Intent intent = new Intent("miui.intent.KEYCODE_BACK");
        intent.putExtra("android.intent.extra.KEY_EVENT", event);
        sendAsyncBroadcast(intent);
    }

    private boolean isLockDeviceWindow(WindowManagerPolicy.WindowState win) {
        WindowManager.LayoutParams lp;
        if (win == null || (lp = win.getAttrs()) == null || (lp.extraFlags & 2048) == 0) {
            return false;
        }
        return true;
    }

    public void dump(String prefix, PrintWriter pw, String[] args) {
        super.dump(prefix, pw, args);
        pw.print(prefix);
        pw.println("BaseMiuiPhoneWindowManager");
        String prefix2 = prefix + "  ";
        pw.print(prefix2);
        pw.print("mInputMethodWindowVisibleHeight=");
        pw.println(this.mInputMethodWindowVisibleHeight);
        pw.print(prefix2);
        pw.print("mFrontFingerprintSensor=");
        pw.println(this.mFrontFingerprintSensor);
        pw.print(prefix2);
        pw.print("mSupportTapFingerprintSensorToHome=");
        pw.println(this.mSupportTapFingerprintSensorToHome);
        pw.print(prefix2);
        pw.print("mScreenOffReason=");
        pw.println(this.mScreenOffReason);
        pw.print(prefix2);
        pw.print("mIsStatusBarVisibleInFullscreen=");
        pw.println(this.mIsStatusBarVisibleInFullscreen);
        pw.print(prefix2);
        pw.print("mHasCameraFlash=");
        pw.println(this.mHasCameraFlash);
        pw.print(prefix2);
        pw.print("mTorchEnabled=");
        pw.println(this.mTorchEnabled);
        pw.print(prefix2);
        pw.print("mScreenRecorderEnabled=");
        pw.println(this.mScreenRecorderEnabled);
        pw.print(prefix2);
        pw.print("mVoiceAssistEnabled=");
        pw.println(this.mVoiceAssistEnabled);
        pw.print(prefix2);
        pw.print("mWifiOnly=");
        pw.println(this.mWifiOnly);
        pw.print("    ");
        pw.println("KeyPress");
        pw.print(prefix2);
        pw.print("mKeyPressed=");
        pw.print(Integer.toBinaryString(this.mKeyPressed));
        pw.print(" mKeyPressing=");
        pw.print(Integer.toBinaryString(this.mKeyPressing));
        pw.print(" mShortcutPressing=");
        pw.println(Integer.toBinaryString(this.mShortcutPressing));
        pw.print(prefix2);
        pw.print("KEYCODE_MENU KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(82)));
        pw.print(prefix2);
        pw.print("KEYCODE_APP_SWITCH KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(187)));
        pw.print(prefix2);
        pw.print("KEYCODE_HOME KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(3)));
        pw.print(prefix2);
        pw.print("KEYCODE_BACK KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(4)));
        pw.print(prefix2);
        pw.print("KEYCODE_POWER KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(26)));
        pw.print(prefix2);
        pw.print("KEYCODE_VOLUME_DOWN KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(25)));
        pw.print(prefix2);
        pw.print("KEYCODE_VOLUME_UP KeyBitmask=");
        pw.println(Integer.toBinaryString(getKeyBitmask(24)));
        pw.print(prefix2);
        pw.print("ElSE KEYCODE KeyBitmask=");
        pw.println(Integer.toBinaryString(1));
        pw.print(prefix2);
        pw.print("SHORTCUT_HOME_POWER=");
        pw.println(Integer.toBinaryString(SHORTCUT_HOME_POWER));
        pw.print(prefix2);
        pw.print("SHORTCUT_BACK_POWER=");
        pw.println(Integer.toBinaryString(SHORTCUT_BACK_POWER));
        pw.print(prefix2);
        pw.print("SHORTCUT_MENU_POWER=");
        pw.println(Integer.toBinaryString(SHORTCUT_MENU_POWER));
        pw.print(prefix2);
        pw.print("SHORTCUT_SCREENSHOT_ANDROID=");
        pw.println(Integer.toBinaryString(SHORTCUT_SCREENSHOT_ANDROID));
        pw.print(prefix2);
        pw.print("SHORTCUT_SCREENSHOT_MIUI=");
        pw.println(Integer.toBinaryString(SHORTCUT_SCREENSHOT_MIUI));
        pw.print(prefix2);
        pw.print("SHORTCUT_UNLOCK=");
        pw.println(Integer.toBinaryString(SHORTCUT_UNLOCK));
        pw.print(prefix2);
        pw.print("mShortcutTriggered=");
        pw.println(this.mShortcutTriggered);
        pw.print(prefix2);
        pw.print("mDpadCenterDown=");
        pw.println(this.mDpadCenterDown);
        pw.print(prefix2);
        pw.print("mHomeDownAfterDpCenter=");
        pw.println(this.mHomeDownAfterDpCenter);
        pw.print("    ");
        pw.println("KeyResponseSetting");
        pw.print(prefix2);
        pw.print("mCurrentUserId=");
        pw.println(this.mCurrentUserId);
        pw.print(prefix2);
        pw.print("mPressToAppSwitch=");
        pw.println(this.mPressToAppSwitch);
        pw.print(prefix2);
        pw.print("mMikeymodeEnabled=");
        pw.println(this.mMikeymodeEnabled);
        pw.print(prefix2);
        pw.print("mCameraKeyWakeScreen=");
        pw.println(this.mCameraKeyWakeScreen);
        pw.print(prefix2);
        pw.print("mTrackballWakeScreen=");
        pw.println(this.mTrackballWakeScreen);
        pw.print(prefix2);
        pw.print("mVolumeKeyLaunchCamera=");
        pw.println(this.mVolumeKeyLaunchCamera);
        pw.print(prefix2);
        pw.print("mTestModeEnabled=");
        pw.println(this.mTestModeEnabled);
        pw.print(prefix2);
        pw.print("mDoubleClickPowerKey=");
        pw.println(this.mDoubleClickPowerKey);
        pw.print(prefix2);
        pw.print("mThreeGestureDown=");
        pw.println(this.mThreeGestureDown);
        pw.print(prefix2);
        pw.print("mLongPressHomeKey=");
        pw.println(this.mLongPressHomeKey);
        pw.print(prefix2);
        pw.print("mLongPressMenuKey=");
        pw.println(this.mLongPressMenuKey);
        pw.print(prefix2);
        pw.print("mLongPressMenuKeyWhenLock=");
        pw.println(this.mLongPressMenuKeyWhenLock);
        pw.print(prefix2);
        pw.print("mLongPressBackKey=");
        pw.println(this.mLongPressBackKey);
        pw.print(prefix2);
        pw.print("mLongPressPowerKey=");
        pw.println(this.mLongPressPowerKey);
        pw.print(prefix2);
        pw.print("mKeyCombinationPowerHome=");
        pw.println(this.mKeyCombinationPowerHome);
        pw.print(prefix2);
        pw.print("mKeyCombinationPowerMenu=");
        pw.println(this.mKeyCombinationPowerMenu);
        pw.print(prefix2);
        pw.print("mKeyCombinationPowerBack=");
        pw.println(this.mKeyCombinationPowerBack);
        pw.print(prefix2);
        pw.print("mScreenButtonsDisabled=");
        pw.println(this.mAutoDisableScreenButtonsManager.isScreenButtonsDisabled());
        pw.print(prefix2);
        pw.print("mVolumeButtonPrePressedTime=");
        pw.println(this.mVolumeButtonPrePressedTime);
        pw.print(prefix2);
        pw.print("mVolumeButtonPressedCount=");
        pw.println(this.mVolumeButtonPressedCount);
        pw.print(prefix2);
        pw.print("mHaveBankCard=");
        pw.println(this.mHaveBankCard);
        pw.print(prefix2);
        pw.print("mHaveTranksCard=");
        pw.println(this.mHaveTranksCard);
        pw.print(prefix2);
        pw.print("mLongPressVolumeDownBehavior=");
        pw.println(this.mLongPressVolumeDownBehavior);
        pw.print(prefix2);
        pw.print("mIsVRMode=");
        pw.println(this.mIsVRMode);
        pw.print(prefix2);
        pw.print("mTalkBackIsOpened=");
        pw.println(this.mTalkBackIsOpened);
        pw.print(prefix2);
        pw.print("mShortcutServiceIsTalkBack=");
        pw.println(this.mShortcutServiceIsTalkBack);
        this.mSmartCoverManager.dump(prefix2, pw);
    }
}
