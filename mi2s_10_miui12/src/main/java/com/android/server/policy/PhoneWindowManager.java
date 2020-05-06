package com.android.server.policy;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.app.IUiModeManager;
import android.app.ProfilerInfo;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.hdmi.HdmiAudioSystemClient;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.input.InputManagerInternal;
import android.media.AudioAttributes;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.IDeviceIdleController;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager;
import android.service.vr.IPersistentVrStateCallbacks;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.MutableBoolean;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.IDisplayFoldListener;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.autofill.AutofillManagerInternal;
import com.android.internal.R;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.ArrayUtils;
import com.android.server.ExtconStateObserver;
import com.android.server.ExtconUEventObserver;
import com.android.server.GestureLauncherService;
import com.android.server.LocalServices;
import com.android.server.SystemServiceManager;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;
import com.android.server.policy.keyguard.KeyguardStateMonitor;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.vr.VrManagerInternal;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.AppTransition;
import com.android.server.wm.DisplayFrames;
import com.android.server.wm.DisplayPolicy;
import com.android.server.wm.DisplayRotation;
import com.android.server.wm.WindowManagerInternal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import miui.os.DeviceFeature;
import miui.slide.SlideCoverListener;

public class PhoneWindowManager implements WindowManagerPolicy {
    private static final int BRIGHTNESS_STEPS = 10;
    private static final long BUGREPORT_TV_GESTURE_TIMEOUT_MILLIS = 1000;
    static final boolean DEBUG_INPUT = false;
    static final boolean DEBUG_KEYGUARD = false;
    static final boolean DEBUG_SPLASH_SCREEN = false;
    static final boolean DEBUG_WAKEUP = true;
    static final int DOUBLE_TAP_HOME_NOTHING = 0;
    static final int DOUBLE_TAP_HOME_RECENT_SYSTEM_UI = 1;
    static final boolean ENABLE_DESK_DOCK_HOME_CAPTURE = false;
    static final boolean ENABLE_VR_HEADSET_HOME_CAPTURE = true;
    private static final float KEYGUARD_SCREENSHOT_CHORD_DELAY_MULTIPLIER = 2.5f;
    static final int LAST_LONG_PRESS_HOME_BEHAVIOR = 2;
    static final int LONG_PRESS_BACK_GO_TO_VOICE_ASSIST = 1;
    static final int LONG_PRESS_BACK_NOTHING = 0;
    static final int LONG_PRESS_HOME_ALL_APPS = 1;
    static final int LONG_PRESS_HOME_ASSIST = 2;
    static final int LONG_PRESS_HOME_NOTHING = 0;
    static final int LONG_PRESS_POWER_ASSISTANT = 5;
    static final int LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
    static final int LONG_PRESS_POWER_GO_TO_VOICE_ASSIST = 4;
    static final int LONG_PRESS_POWER_NOTHING = 0;
    static final int LONG_PRESS_POWER_SHUT_OFF = 2;
    static final int LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM = 3;
    private static final long MOVING_DISPLAY_TO_TOP_DURATION_MILLIS = 10;
    private static final int MSG_ACCESSIBILITY_SHORTCUT = 17;
    private static final int MSG_ACCESSIBILITY_TV = 19;
    private static final int MSG_BACK_LONG_PRESS = 16;
    private static final int MSG_BUGREPORT_TV = 18;
    private static final int MSG_DISPATCH_BACK_KEY_TO_AUTOFILL = 20;
    private static final int MSG_DISPATCH_MEDIA_KEY_REPEAT_WITH_WAKE_LOCK = 4;
    private static final int MSG_DISPATCH_MEDIA_KEY_WITH_WAKE_LOCK = 3;
    private static final int MSG_DISPATCH_SHOW_GLOBAL_ACTIONS = 10;
    private static final int MSG_DISPATCH_SHOW_RECENTS = 9;
    private static final int MSG_HANDLE_ALL_APPS = 22;
    private static final int MSG_HANDLE_FUNCTION_KEY_AND_BACK_KEY = 31;
    private static final int MSG_HIDE_BOOT_MESSAGE = 11;
    private static final int MSG_KEYGUARD_DRAWN_COMPLETE = 5;
    private static final int MSG_KEYGUARD_DRAWN_TIMEOUT = 6;
    private static final int MSG_LAUNCH_ASSIST = 23;
    private static final int MSG_LAUNCH_ASSIST_LONG_PRESS = 24;
    private static final int MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK = 12;
    private static final int MSG_MOVE_DISPLAY_TO_TOP = 28;
    private static final int MSG_NOTIFY_USER_ACTIVITY = 26;
    private static final int MSG_POWER_DELAYED_PRESS = 13;
    private static final int MSG_POWER_LONG_PRESS = 14;
    private static final int MSG_POWER_VERY_LONG_PRESS = 25;
    private static final int MSG_RINGER_TOGGLE_CHORD = 27;
    private static final int MSG_SHOW_PICTURE_IN_PICTURE_MENU = 15;
    private static final int MSG_SYSTEM_KEY_PRESS = 21;
    private static final int MSG_WINDOW_MANAGER_DRAWN_COMPLETE = 7;
    static final int MULTI_PRESS_POWER_BRIGHTNESS_BOOST = 2;
    static final int MULTI_PRESS_POWER_NOTHING = 0;
    static final int MULTI_PRESS_POWER_THEATER_MODE = 1;
    static final int PENDING_KEY_NULL = -1;
    private static final int POWER_BUTTON_SUPPRESSION_DELAY_DEFAULT_MILLIS = 800;
    private static final long SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS = 150;
    static final int SHORT_PRESS_POWER_CLOSE_IME_OR_GO_HOME = 5;
    static final int SHORT_PRESS_POWER_GO_HOME = 4;
    static final int SHORT_PRESS_POWER_GO_TO_SLEEP = 1;
    static final int SHORT_PRESS_POWER_NOTHING = 0;
    static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP = 2;
    static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME = 3;
    static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP = 0;
    static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME = 1;
    static final int SHORT_PRESS_WINDOW_NOTHING = 0;
    static final int SHORT_PRESS_WINDOW_PICTURE_IN_PICTURE = 1;
    static final boolean SHOW_SPLASH_SCREENS = true;
    private static final boolean SUPPORT_FOD = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    public static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    public static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    public static final String SYSTEM_DIALOG_REASON_SCREENSHOT = "screenshot";
    static final String TAG = "WindowManager";
    public static final int TOAST_WINDOW_TIMEOUT = 3500;
    private static final int USER_ACTIVITY_NOTIFICATION_DELAY = 200;
    static final int VERY_LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
    static final int VERY_LONG_PRESS_POWER_NOTHING = 0;
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    static final int WAITING_FOR_DRAWN_TIMEOUT = 1000;
    /* access modifiers changed from: private */
    public static final int[] WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = {2003, 2010};
    static final boolean localLOGV = false;
    static SparseArray<String> sApplicationLaunchKeyCategories = new SparseArray<>();
    private boolean mA11yShortcutChordVolumeUpKeyConsumed;
    private long mA11yShortcutChordVolumeUpKeyTime;
    private boolean mA11yShortcutChordVolumeUpKeyTriggered;
    AccessibilityManager mAccessibilityManager;
    /* access modifiers changed from: private */
    public AccessibilityShortcutController mAccessibilityShortcutController;
    private boolean mAccessibilityTvKey1Pressed;
    private boolean mAccessibilityTvKey2Pressed;
    private boolean mAccessibilityTvScheduled;
    ActivityManagerInternal mActivityManagerInternal;
    ActivityTaskManagerInternal mActivityTaskManagerInternal;
    private HashSet<Integer> mAllowLockscreenWhenOnDisplays = new HashSet<>();
    boolean mAllowStartActivityForLongPressOnPowerDuringSetup;
    private boolean mAllowTheaterModeWakeFromCameraLens;
    private boolean mAllowTheaterModeWakeFromKey;
    private boolean mAllowTheaterModeWakeFromLidSwitch;
    private boolean mAllowTheaterModeWakeFromMotion;
    private boolean mAllowTheaterModeWakeFromMotionWhenNotDreaming;
    private boolean mAllowTheaterModeWakeFromPowerKey;
    /* access modifiers changed from: private */
    public boolean mAllowTheaterModeWakeFromWakeGesture;
    private boolean mAodShowing;
    AppOpsManager mAppOpsManager;
    AudioManagerInternal mAudioManagerInternal;
    AutofillManagerInternal mAutofillManagerInternal;
    volatile boolean mBackKeyHandled;
    volatile boolean mBeganFromNonInteractive;
    boolean mBeingHangUp;
    boolean mBootMessageNeedsHiding;
    ProgressDialog mBootMsgDialog = null;
    PowerManager.WakeLock mBroadcastWakeLock;
    private boolean mBugreportTvKey1Pressed;
    private boolean mBugreportTvKey2Pressed;
    private boolean mBugreportTvScheduled;
    BurnInProtectionHelper mBurnInProtectionHelper;
    long[] mCalendarDateVibePattern;
    volatile boolean mCameraGestureTriggeredDuringGoingToSleep;
    int mCameraLensCoverState = -1;
    Intent mCarDockIntent;
    boolean mConsumeSearchKeyUp;
    Context mContext;
    private final Interpolator mCubicEaseOutInterpolator = new Interpolator() {
        public float getInterpolation(float v) {
            float f = v - 1.0f;
            float v2 = f;
            return (f * v2 * v2) + 1.0f;
        }
    };
    private int mCurrentUserId;
    Display mDefaultDisplay;
    DisplayPolicy mDefaultDisplayPolicy;
    DisplayRotation mDefaultDisplayRotation;
    Intent mDeskDockIntent;
    private volatile boolean mDismissImeOnBackKeyPressed;
    private DisplayFoldController mDisplayFoldController;
    private final SparseArray<DisplayHomeButtonHandler> mDisplayHomeButtonHandlers = new SparseArray<>();
    DisplayManager mDisplayManager;
    BroadcastReceiver mDockReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DOCK_EVENT".equals(intent.getAction())) {
                PhoneWindowManager.this.mDefaultDisplayPolicy.setDockMode(intent.getIntExtra("android.intent.extra.DOCK_STATE", 0));
            } else {
                try {
                    IUiModeManager uiModeService = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
                    PhoneWindowManager.this.mUiMode = uiModeService.getCurrentModeType();
                } catch (RemoteException e) {
                }
            }
            PhoneWindowManager.this.updateRotation(true);
            PhoneWindowManager.this.mDefaultDisplayRotation.updateOrientationListener();
        }
    };
    int mDoublePressOnPowerBehavior;
    /* access modifiers changed from: private */
    public int mDoubleTapOnHomeBehavior;
    DreamManagerInternal mDreamManagerInternal;
    BroadcastReceiver mDreamReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DREAMING_STARTED".equals(intent.getAction())) {
                if (PhoneWindowManager.this.mKeyguardDelegate != null) {
                    PhoneWindowManager.this.mKeyguardDelegate.onDreamingStarted();
                }
            } else if ("android.intent.action.DREAMING_STOPPED".equals(intent.getAction()) && PhoneWindowManager.this.mKeyguardDelegate != null) {
                PhoneWindowManager.this.mKeyguardDelegate.onDreamingStopped();
            }
        }
    };
    private boolean mEnableCarDockHomeCapture = true;
    boolean mEnableShiftMenuBugReports = false;
    volatile boolean mEndCallKeyHandled;
    private Runnable mEndCallLongPress = new Runnable() {
        public void run() {
            PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
            phoneWindowManager.mEndCallKeyHandled = true;
            boolean unused = phoneWindowManager.performHapticFeedback(0, false, "End Call - Long Press - Show Global Actions");
            PhoneWindowManager.this.showGlobalActionsInternal();
        }
    };
    int mEndcallBehavior;
    private UEventObserver mExtEventObserver = new UEventObserver() {
        public void onUEvent(UEventObserver.UEvent event) {
            if (event.get("status") != null) {
                PhoneWindowManager.this.mDefaultDisplayPolicy.setHdmiPlugged("connected".equals(event.get("status")));
            }
        }
    };
    private final SparseArray<KeyCharacterMap.FallbackAction> mFallbackActions = new SparseArray<>();
    WindowManagerPolicy.WindowState mFocusedWindow;
    MiuiGlobalActions mGlobalActions;
    private GlobalKeyManager mGlobalKeyManager;
    private boolean mGoToSleepOnButtonPressTheaterMode;
    volatile boolean mGoingToSleep;
    private UEventObserver mHDMIObserver = new UEventObserver() {
        public void onUEvent(UEventObserver.UEvent event) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setHdmiPlugged(SplitScreenReporter.ACTION_ENTER_SPLIT.equals(event.get("SWITCH_STATE")));
        }
    };
    private boolean mHandleVolumeKeysInWM;
    Handler mHandler;
    boolean mHapticTextHandleEnabled;
    private boolean mHasFeatureHdmiCec;
    private boolean mHasFeatureLeanback;
    private boolean mHasFeatureWatch;
    boolean mHasSoftInput = false;
    boolean mHaveBuiltInKeyboard;
    boolean mHavePendingMediaKeyRepeatWithWakeLock;
    HdmiControl mHdmiControl;
    Intent mHomeIntent;
    int mIncallBackBehavior;
    int mIncallPowerBehavior;
    int mInitialMetaState;
    InputManagerInternal mInputManagerInternal;
    InputMethodManagerInternal mInputMethodManagerInternal;
    private boolean mKeyguardBound;
    private WindowManagerPolicy.WindowState mKeyguardCandidate = null;
    KeyguardServiceDelegate mKeyguardDelegate;
    final KeyguardServiceDelegate.DrawnListener mKeyguardDrawnCallback = new KeyguardServiceDelegate.DrawnListener() {
        public void onDrawn(long screenOnDelay) {
            Slog.i("WindowManager", "mKeyguardDelegate.ShowListener.onDrawn.");
            if (PhoneWindowManager.this.mScreenOnDelay >= 0) {
                PhoneWindowManager.this.mScreenOnDelay = Math.min(screenOnDelay, 600);
            }
            PhoneWindowManager.this.mHandler.sendEmptyMessage(5);
        }
    };
    private boolean mKeyguardDrawnOnce;
    volatile boolean mKeyguardOccluded;
    private boolean mKeyguardOccludedChanged;
    boolean mLanguageSwitchKeyPressed;
    private boolean mLidControlsDisplayFold;
    int mLidKeyboardAccessibility;
    int mLidNavigationAccessibility;
    final Object mLock = new Object();
    int mLockScreenTimeout;
    boolean mLockScreenTimerActive;
    private final LogDecelerateInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
    MetricsLogger mLogger;
    int mLongPressOnBackBehavior;
    /* access modifiers changed from: private */
    public int mLongPressOnHomeBehavior;
    int mLongPressOnPowerBehavior;
    long[] mLongPressVibePattern;
    int mMetaState;
    private volatile long mMovingDisplayToTopKeyTime;
    /* access modifiers changed from: private */
    public volatile boolean mMovingDisplayToTopKeyTriggered;
    BroadcastReceiver mMultiuserReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                PhoneWindowManager.this.mSettingsObserver.onChange(false);
                PhoneWindowManager.this.mDefaultDisplayRotation.onUserSwitch();
                PhoneWindowManager.this.mWindowManagerFuncs.onUserSwitched();
            }
        }
    };
    volatile boolean mNavBarVirtualKeyHapticFeedbackEnabled = true;
    private boolean mNotifyUserActivity;
    boolean mPendingCapsLockToggle;
    private boolean mPendingKeyguardOccluded;
    boolean mPendingMetaAction;
    volatile int mPendingWakeKey = -1;
    private boolean mPerDisplayFocusEnabled = false;
    final IPersistentVrStateCallbacks mPersistentVrModeListener = new IPersistentVrStateCallbacks.Stub() {
        public void onPersistentVrStateChanged(boolean enabled) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setPersistentVrModeEnabled(enabled);
        }
    };
    volatile boolean mPictureInPictureVisible;
    private Runnable mPossibleVeryLongPressReboot = new Runnable() {
        public void run() {
            PhoneWindowManager.this.mActivityManagerInternal.prepareForPossibleShutdown();
        }
    };
    private int mPowerButtonSuppressionDelayMillis = 800;
    volatile boolean mPowerKeyHandled;
    volatile int mPowerKeyPressCounter;
    PowerManager.WakeLock mPowerKeyWakeLock;
    PowerManager mPowerManager;
    PowerManagerInternal mPowerManagerInternal;
    boolean mPreloadedRecentApps;
    int mRecentAppsHeldModifiers;
    volatile boolean mRecentsVisible;
    volatile boolean mRequestedOrGoingToSleep;
    private int mRingerToggleChord = 0;
    boolean mSafeMode;
    long[] mSafeModeEnabledVibePattern;
    ScreenLockTimeout mScreenLockTimeout = new ScreenLockTimeout();
    ActivityTaskManagerInternal.SleepToken mScreenOffSleepToken;
    public long mScreenOnDelay;
    boolean mScreenOnFully;
    private boolean mScreenshotChordEnabled;
    private long mScreenshotChordPowerKeyTime;
    private boolean mScreenshotChordPowerKeyTriggered;
    private boolean mScreenshotChordVolumeDownKeyConsumed;
    private long mScreenshotChordVolumeDownKeyTime;
    private boolean mScreenshotChordVolumeDownKeyTriggered;
    private final ScreenshotRunnable mScreenshotRunnable = new ScreenshotRunnable();
    boolean mSearchKeyShortcutPending;
    SearchManager mSearchManager;
    final Object mServiceAquireLock = new Object();
    SettingsObserver mSettingsObserver;
    int mShortPressOnPowerBehavior;
    int mShortPressOnSleepBehavior;
    int mShortPressOnWindowBehavior;
    private LongSparseArray<IShortcutService> mShortcutKeyServices = new LongSparseArray<>();
    ShortcutManager mShortcutManager;
    SlideCoverListener mSlideScreenListener;
    StatusBarManagerInternal mStatusBarManagerInternal;
    IStatusBarService mStatusBarService;
    private boolean mSupportLongPressPowerWhenNonInteractive;
    boolean mSystemBooted;
    boolean mSystemNavigationKeysEnabled;
    boolean mSystemReady;
    private final MutableBoolean mTmpBoolean = new MutableBoolean(false);
    private volatile int mTopFocusedDisplayId = -1;
    int mTriplePressOnPowerBehavior;
    int mUiMode;
    IUiModeManager mUiModeManager;
    boolean mUseTvRouting;
    int mVeryLongPressOnPowerBehavior;
    int mVeryLongPressTimeout;
    Vibrator mVibrator;
    Intent mVrHeadsetHomeIntent;
    volatile VrManagerInternal mVrManagerInternal;
    boolean mWakeGestureEnabledSetting;
    MyWakeGestureListener mWakeGestureListener;
    IWindowManager mWindowManager;
    final Runnable mWindowManagerDrawCallback = new Runnable() {
        public void run() {
            Slog.i("WindowManager", "All windows ready for display!");
            PhoneWindowManager.this.mHandler.sendEmptyMessage(7);
        }
    };
    WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
    WindowManagerInternal mWindowManagerInternal;

    static {
        sApplicationLaunchKeyCategories.append(64, "android.intent.category.APP_BROWSER");
        sApplicationLaunchKeyCategories.append(65, "android.intent.category.APP_EMAIL");
        sApplicationLaunchKeyCategories.append(207, "android.intent.category.APP_CONTACTS");
        sApplicationLaunchKeyCategories.append(208, "android.intent.category.APP_CALENDAR");
        sApplicationLaunchKeyCategories.append(209, "android.intent.category.APP_MUSIC");
        sApplicationLaunchKeyCategories.append(210, "android.intent.category.APP_CALCULATOR");
    }

    private class PolicyHandler extends Handler {
        private PolicyHandler() {
        }

        public void handleMessage(Message msg) {
            boolean z = false;
            switch (msg.what) {
                case 3:
                    PhoneWindowManager.this.dispatchMediaKeyWithWakeLock((KeyEvent) msg.obj);
                    return;
                case 4:
                    PhoneWindowManager.this.dispatchMediaKeyRepeatWithWakeLock((KeyEvent) msg.obj);
                    return;
                case 5:
                    Slog.w("WindowManager", "Setting mKeyguardDrawComplete");
                    PhoneWindowManager.this.finishKeyguardDrawn();
                    return;
                case 6:
                    Slog.w("WindowManager", "Keyguard drawn timeout. Setting mKeyguardDrawComplete");
                    PhoneWindowManager.this.finishKeyguardDrawn();
                    return;
                case 7:
                    Slog.w("WindowManager", "Setting mWindowManagerDrawComplete");
                    PhoneWindowManager.this.finishWindowsDrawn();
                    return;
                case 9:
                    PhoneWindowManager.this.showRecentApps(false);
                    return;
                case 10:
                    PhoneWindowManager.this.showGlobalActionsInternal();
                    return;
                case 11:
                    PhoneWindowManager.this.handleHideBootMessage();
                    return;
                case 12:
                    PhoneWindowManager.this.launchVoiceAssistWithWakeLock();
                    return;
                case 13:
                    PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                    long longValue = ((Long) msg.obj).longValue();
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    phoneWindowManager.powerPress(longValue, z, msg.arg2);
                    PhoneWindowManager.this.finishPowerKeyPress();
                    return;
                case 14:
                    PhoneWindowManager.this.powerLongPress();
                    return;
                case 15:
                    PhoneWindowManager.this.showPictureInPictureMenuInternal();
                    return;
                case 16:
                    PhoneWindowManager.this.backLongPress();
                    return;
                case 17:
                    PhoneWindowManager.this.accessibilityShortcutActivated();
                    return;
                case 18:
                    PhoneWindowManager.this.requestFullBugreport();
                    return;
                case 19:
                    if (PhoneWindowManager.this.mAccessibilityShortcutController.isAccessibilityShortcutAvailable(false)) {
                        PhoneWindowManager.this.accessibilityShortcutActivated();
                        return;
                    }
                    return;
                case 20:
                    PhoneWindowManager.this.mAutofillManagerInternal.onBackKeyPressed();
                    return;
                case 21:
                    PhoneWindowManager.this.sendSystemKeyToStatusBar(msg.arg1);
                    return;
                case 22:
                    PhoneWindowManager.this.launchAllAppsAction();
                    return;
                case 23:
                    int deviceId = msg.arg1;
                    PhoneWindowManager.this.launchAssistAction((String) msg.obj, deviceId);
                    return;
                case 24:
                    PhoneWindowManager.this.launchAssistLongPressAction();
                    return;
                case 25:
                    PhoneWindowManager.this.powerVeryLongPress();
                    return;
                case PhoneWindowManager.MSG_NOTIFY_USER_ACTIVITY /*26*/:
                    removeMessages(PhoneWindowManager.MSG_NOTIFY_USER_ACTIVITY);
                    Intent intent = new Intent("android.intent.action.USER_ACTIVITY_NOTIFICATION");
                    intent.addFlags(1073741824);
                    PhoneWindowManager.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.USER_ACTIVITY");
                    return;
                case PhoneWindowManager.MSG_RINGER_TOGGLE_CHORD /*27*/:
                    PhoneWindowManager.this.handleRingerChordGesture();
                    return;
                case 28:
                    PhoneWindowManager.this.mWindowManagerFuncs.moveDisplayToTop(msg.arg1);
                    boolean unused = PhoneWindowManager.this.mMovingDisplayToTopKeyTriggered = false;
                    return;
                case 31:
                    PhoneWindowManager.this.processFunctionKeyOnGamePad(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver resolver = PhoneWindowManager.this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor("end_button_behavior"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("incall_power_button_behavior"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("incall_back_button_behavior"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("wake_gesture_enabled"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("volume_hush_gesture"), false, this, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor("system_navigation_keys_enabled"), false, this, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("power_button_long_press"), false, this, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("power_button_very_long_press"), false, this, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("power_button_suppression_delay_after_gesture_wake"), false, this, -1);
            PhoneWindowManager.this.updateSettings();
        }

        public void onChange(boolean selfChange) {
            PhoneWindowManager.this.updateSettings();
            PhoneWindowManager.this.updateRotation(false);
        }
    }

    class MyWakeGestureListener extends WakeGestureListener {
        MyWakeGestureListener(Context context, Handler handler) {
            super(context, handler);
        }

        public void onWakeUp() {
            synchronized (PhoneWindowManager.this.mLock) {
                if (PhoneWindowManager.this.shouldEnableWakeGestureLp()) {
                    boolean unused = PhoneWindowManager.this.performHapticFeedback(1, false, "Wake Up");
                    boolean unused2 = PhoneWindowManager.this.wakeUp(SystemClock.uptimeMillis(), PhoneWindowManager.this.mAllowTheaterModeWakeFromWakeGesture, 4, "android.policy:GESTURE");
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleRingerChordGesture() {
        if (this.mRingerToggleChord != 0) {
            getAudioManagerInternal();
            this.mAudioManagerInternal.silenceRingerModeInternal("volume_hush");
            Settings.Secure.putInt(this.mContext.getContentResolver(), "hush_gesture_used", 1);
            this.mLogger.action(1440, this.mRingerToggleChord);
        }
    }

    /* access modifiers changed from: package-private */
    public IStatusBarService getStatusBarService() {
        IStatusBarService iStatusBarService;
        synchronized (this.mServiceAquireLock) {
            if (this.mStatusBarService == null) {
                this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
            }
            iStatusBarService = this.mStatusBarService;
        }
        return iStatusBarService;
    }

    /* access modifiers changed from: package-private */
    public StatusBarManagerInternal getStatusBarManagerInternal() {
        StatusBarManagerInternal statusBarManagerInternal;
        synchronized (this.mServiceAquireLock) {
            if (this.mStatusBarManagerInternal == null) {
                this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            }
            statusBarManagerInternal = this.mStatusBarManagerInternal;
        }
        return statusBarManagerInternal;
    }

    /* access modifiers changed from: package-private */
    public AudioManagerInternal getAudioManagerInternal() {
        AudioManagerInternal audioManagerInternal;
        synchronized (this.mServiceAquireLock) {
            if (this.mAudioManagerInternal == null) {
                this.mAudioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
            }
            audioManagerInternal = this.mAudioManagerInternal;
        }
        return audioManagerInternal;
    }

    private void interceptBackKeyDown() {
        this.mLogger.count("key_back_down", 1);
        this.mBackKeyHandled = false;
        if (hasLongPressOnBackBehavior()) {
            Message msg = this.mHandler.obtainMessage(16);
            msg.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(msg, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
        }
    }

    private boolean interceptBackKeyUp(KeyEvent event) {
        TelecomManager telecomManager;
        this.mLogger.count("key_back_up", 1);
        boolean handled = this.mBackKeyHandled;
        cancelPendingBackKeyAction();
        if (this.mHasFeatureWatch && (telecomManager = getTelecommService()) != null) {
            if (telecomManager.isRinging()) {
                telecomManager.silenceRinger();
                return false;
            } else if ((1 & this.mIncallBackBehavior) != 0 && telecomManager.isInCall()) {
                return telecomManager.endCall();
            }
        }
        if (this.mAutofillManagerInternal != null && event.getKeyCode() == 4) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(20));
        }
        return handled;
    }

    private void interceptPowerKeyDown(KeyEvent event, boolean interactive) {
        if (!this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.acquire();
        }
        if (this.mPowerKeyPressCounter != 0) {
            this.mHandler.removeMessages(13);
        }
        this.mWindowManagerFuncs.onPowerKeyDown(interactive);
        if (interactive && !this.mScreenshotChordPowerKeyTriggered && (event.getFlags() & 1024) == 0) {
            this.mScreenshotChordPowerKeyTriggered = true;
            this.mScreenshotChordPowerKeyTime = event.getDownTime();
            interceptScreenshotChord();
            interceptRingerToggleChord();
        }
        TelecomManager telecomManager = getTelecommService();
        boolean hungUp = false;
        if (telecomManager != null) {
            if (telecomManager.isRinging()) {
                telecomManager.silenceRinger();
            } else if ((this.mIncallPowerBehavior & 2) != 0 && telecomManager.isInCall() && interactive) {
                hungUp = telecomManager.endCall();
            }
        }
        GestureLauncherService gestureService = (GestureLauncherService) LocalServices.getService(GestureLauncherService.class);
        boolean gesturedServiceIntercepted = false;
        if (gestureService != null) {
            gesturedServiceIntercepted = gestureService.interceptPowerKeyDown(event, interactive, this.mTmpBoolean);
            if (this.mTmpBoolean.value && this.mRequestedOrGoingToSleep) {
                this.mCameraGestureTriggeredDuringGoingToSleep = true;
            }
        }
        sendSystemKeyToStatusBarAsync(event.getKeyCode());
        schedulePossibleVeryLongPressReboot();
        this.mPowerKeyHandled = hungUp || this.mScreenshotChordVolumeDownKeyTriggered || this.mA11yShortcutChordVolumeUpKeyTriggered || gesturedServiceIntercepted;
        if (this.mPowerKeyHandled) {
            return;
        }
        if (!interactive) {
            wakeUpFromPowerKey(event.getDownTime());
            if (this.mSupportLongPressPowerWhenNonInteractive && hasLongPressOnPowerBehavior()) {
                if ((event.getFlags() & 128) != 0) {
                    powerLongPress();
                } else {
                    Message msg = this.mHandler.obtainMessage(14);
                    msg.setAsynchronous(true);
                    this.mHandler.sendMessageDelayed(msg, getPowerLongPressTimeOut());
                    if (hasVeryLongPressOnPowerBehavior()) {
                        Message longMsg = this.mHandler.obtainMessage(25);
                        longMsg.setAsynchronous(true);
                        this.mHandler.sendMessageDelayed(longMsg, (long) this.mVeryLongPressTimeout);
                    }
                }
                this.mBeganFromNonInteractive = true;
            } else if (getMaxMultiPressPowerCount() <= 1) {
                this.mPowerKeyHandled = true;
            } else {
                this.mBeganFromNonInteractive = true;
            }
        } else if (!hasLongPressOnPowerBehavior()) {
        } else {
            if ((event.getFlags() & 128) != 0) {
                powerLongPress();
                return;
            }
            Message msg2 = this.mHandler.obtainMessage(14);
            msg2.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(msg2, getPowerLongPressTimeOut());
            if (hasVeryLongPressOnPowerBehavior()) {
                Message longMsg2 = this.mHandler.obtainMessage(25);
                longMsg2.setAsynchronous(true);
                this.mHandler.sendMessageDelayed(longMsg2, (long) this.mVeryLongPressTimeout);
            }
        }
    }

    private void interceptPowerKeyUp(KeyEvent event, boolean interactive, boolean canceled) {
        boolean handled = canceled || this.mPowerKeyHandled;
        this.mScreenshotChordPowerKeyTriggered = false;
        cancelPendingScreenshotChordAction();
        cancelPendingPowerKeyAction();
        if (!handled) {
            if ((event.getFlags() & 128) == 0) {
                Handler handler = this.mHandler;
                WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs = this.mWindowManagerFuncs;
                Objects.requireNonNull(windowManagerFuncs);
                handler.post(new Runnable() {
                    public final void run() {
                        WindowManagerPolicy.WindowManagerFuncs.this.triggerAnimationFailsafe();
                    }
                });
            }
            this.mPowerKeyPressCounter++;
            int maxCount = getMaxMultiPressPowerCount();
            long eventTime = event.getDownTime();
            if (this.mPowerKeyPressCounter < maxCount) {
                Message msg = this.mHandler.obtainMessage(13, interactive, this.mPowerKeyPressCounter, Long.valueOf(eventTime));
                msg.setAsynchronous(true);
                this.mHandler.sendMessageDelayed(msg, (long) ViewConfiguration.getMultiPressTimeout());
                return;
            }
            powerPress(eventTime, interactive, this.mPowerKeyPressCounter);
        }
        finishPowerKeyPress();
    }

    /* access modifiers changed from: private */
    public void finishPowerKeyPress() {
        this.mBeganFromNonInteractive = false;
        this.mPowerKeyPressCounter = 0;
        if (this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.release();
        }
    }

    private void cancelPendingPowerKeyAction() {
        if (!this.mPowerKeyHandled) {
            this.mPowerKeyHandled = true;
            this.mHandler.removeMessages(14);
        }
        if (hasVeryLongPressOnPowerBehavior()) {
            this.mHandler.removeMessages(25);
        }
        cancelPossibleVeryLongPressReboot();
    }

    private void cancelPendingBackKeyAction() {
        if (!this.mBackKeyHandled) {
            this.mBackKeyHandled = true;
            this.mHandler.removeMessages(16);
        }
    }

    /* access modifiers changed from: private */
    public void powerPress(long eventTime, boolean interactive, int count) {
        int i;
        if (!this.mDefaultDisplayPolicy.isScreenOnEarly() || this.mDefaultDisplayPolicy.isScreenOnFully()) {
            Slog.d("WindowManager", "powerPress: eventTime=" + eventTime + " interactive=" + interactive + " count=" + count + " beganFromNonInteractive=" + this.mBeganFromNonInteractive + " mShortPressOnPowerBehavior=" + this.mShortPressOnPowerBehavior);
            if (count == 2) {
                powerMultiPressAction(eventTime, interactive, this.mDoublePressOnPowerBehavior);
            } else if (count == 3) {
                powerMultiPressAction(eventTime, interactive, this.mTriplePressOnPowerBehavior);
            } else if (interactive && !this.mBeganFromNonInteractive && (i = this.mShortPressOnPowerBehavior) != 0) {
                if (i == 1) {
                    goToSleepFromPowerButton(eventTime, 0);
                } else if (i == 2) {
                    goToSleepFromPowerButton(eventTime, 1);
                } else if (i != 3) {
                    if (i == 4) {
                        shortPressPowerGoHome();
                    } else if (i == 5) {
                        if (this.mDismissImeOnBackKeyPressed) {
                            if (this.mInputMethodManagerInternal == null) {
                                this.mInputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
                            }
                            InputMethodManagerInternal inputMethodManagerInternal = this.mInputMethodManagerInternal;
                            if (inputMethodManagerInternal != null) {
                                inputMethodManagerInternal.hideCurrentInputMethod();
                                return;
                            }
                            return;
                        }
                        shortPressPowerGoHome();
                    }
                } else if (goToSleepFromPowerButton(eventTime, 1)) {
                    launchHomeFromHotKey(0);
                }
            }
        } else {
            Slog.i("WindowManager", "Suppressed redundant power key press while already in the process of turning the screen on.");
        }
    }

    private boolean goToSleepFromPowerButton(long eventTime, int flags) {
        PowerManager.WakeData lastWakeUp = this.mPowerManagerInternal.getLastWakeup();
        if (lastWakeUp != null && lastWakeUp.wakeReason == 4) {
            int i = Settings.Global.getInt(this.mContext.getContentResolver(), "power_button_suppression_delay_after_gesture_wake", 800);
            long now = SystemClock.uptimeMillis();
            if (this.mPowerButtonSuppressionDelayMillis > 0 && now < lastWakeUp.wakeTime + ((long) this.mPowerButtonSuppressionDelayMillis)) {
                Slog.i("WindowManager", "Sleep from power button suppressed. Time since gesture: " + (now - lastWakeUp.wakeTime) + "ms");
                return false;
            }
        }
        goToSleep(eventTime, 4, flags);
        return true;
    }

    private void goToSleep(long eventTime, int reason, int flags) {
        this.mRequestedOrGoingToSleep = true;
        this.mPowerManager.goToSleep(eventTime, reason, flags);
    }

    private void shortPressPowerGoHome() {
        launchHomeFromHotKey(0, true, false);
        if (isKeyguardShowingAndNotOccluded()) {
            this.mKeyguardDelegate.onShortPowerPressedGoHome();
        }
    }

    private void powerMultiPressAction(long eventTime, boolean interactive, int behavior) {
        if (behavior == 0) {
            return;
        }
        if (behavior != 1) {
            if (behavior == 2) {
                Slog.i("WindowManager", "Starting brightness boost.");
                if (!interactive) {
                    wakeUpFromPowerKey(eventTime);
                }
                this.mPowerManager.boostScreenBrightness(eventTime);
            }
        } else if (!isUserSetupComplete()) {
            Slog.i("WindowManager", "Ignoring toggling theater mode - device not setup.");
        } else if (isTheaterModeEnabled()) {
            Slog.i("WindowManager", "Toggling theater mode off.");
            Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
            if (!interactive) {
                wakeUpFromPowerKey(eventTime);
            }
        } else {
            Slog.i("WindowManager", "Toggling theater mode on.");
            Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 1);
            if (this.mGoToSleepOnButtonPressTheaterMode && interactive) {
                goToSleep(eventTime, 4, 0);
            }
        }
    }

    private int getLidBehavior() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "lid_behavior", 0);
    }

    private int getMaxMultiPressPowerCount() {
        if (this.mTriplePressOnPowerBehavior != 0) {
            return 3;
        }
        if (this.mDoublePressOnPowerBehavior != 0) {
            return 2;
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public void powerLongPress() {
        int behavior = getResolvedLongPressOnPowerBehavior();
        if (behavior != 0) {
            boolean z = true;
            if (behavior == 1) {
                this.mPowerKeyHandled = true;
                performHapticFeedback(0, false, "Power - Long Press - Global Actions");
                showGlobalActionsInternal();
            } else if (behavior == 2 || behavior == 3) {
                this.mPowerKeyHandled = true;
                performHapticFeedback(0, false, "Power - Long Press - Shut Off");
                sendCloseSystemWindows(SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS);
                WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs = this.mWindowManagerFuncs;
                if (behavior != 2) {
                    z = false;
                }
                windowManagerFuncs.shutdown(z);
            } else if (behavior == 4) {
                this.mPowerKeyHandled = true;
                performHapticFeedback(0, false, "Power - Long Press - Go To Voice Assist");
                launchVoiceAssist(this.mAllowStartActivityForLongPressOnPowerDuringSetup);
            } else if (behavior == 5) {
                this.mPowerKeyHandled = true;
                performHapticFeedback(0, false, "Power - Long Press - Go To Assistant");
                launchAssistAction((String) null, Integer.MIN_VALUE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void powerVeryLongPress() {
        int i = this.mVeryLongPressOnPowerBehavior;
        if (i != 0 && i == 1) {
            this.mPowerKeyHandled = true;
            performHapticFeedback(0, false, "Power - Very Long Press - Show Global Actions");
            showGlobalActionsInternal();
        }
    }

    /* access modifiers changed from: private */
    public void backLongPress() {
        this.mBackKeyHandled = true;
        int i = this.mLongPressOnBackBehavior;
        if (i != 0 && i == 1) {
            launchVoiceAssist(false);
        }
    }

    /* access modifiers changed from: private */
    public void accessibilityShortcutActivated() {
        this.mAccessibilityShortcutController.performAccessibilityShortcut();
    }

    private void sleepPress() {
        if (this.mShortPressOnSleepBehavior == 1) {
            launchHomeFromHotKey(0, false, true);
        }
    }

    private void sleepRelease(long eventTime) {
        int i = this.mShortPressOnSleepBehavior;
        if (i == 0 || i == 1) {
            Slog.i("WindowManager", "sleepRelease() calling goToSleep(GO_TO_SLEEP_REASON_SLEEP_BUTTON)");
            goToSleep(eventTime, 6, 0);
        }
    }

    private int getResolvedLongPressOnPowerBehavior() {
        if (FactoryTest.isLongPressOnPowerOffEnabled()) {
            return 3;
        }
        return this.mLongPressOnPowerBehavior;
    }

    private boolean hasLongPressOnPowerBehavior() {
        return getResolvedLongPressOnPowerBehavior() != 0;
    }

    private boolean hasVeryLongPressOnPowerBehavior() {
        return this.mVeryLongPressOnPowerBehavior != 0;
    }

    private boolean hasLongPressOnBackBehavior() {
        return this.mLongPressOnBackBehavior != 0;
    }

    private void interceptScreenshotChord() {
        if (this.mScreenshotChordEnabled && this.mScreenshotChordVolumeDownKeyTriggered && this.mScreenshotChordPowerKeyTriggered && !this.mA11yShortcutChordVolumeUpKeyTriggered) {
            long now = SystemClock.uptimeMillis();
            if (now <= this.mScreenshotChordVolumeDownKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS && now <= this.mScreenshotChordPowerKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS) {
                this.mScreenshotChordVolumeDownKeyConsumed = true;
                cancelPendingPowerKeyAction();
                this.mScreenshotRunnable.setScreenshotType(1);
                this.mHandler.post(this.mScreenshotRunnable);
            }
        }
    }

    private void interceptAccessibilityShortcutChord() {
        if (this.mAccessibilityShortcutController.isAccessibilityShortcutAvailable(isKeyguardLocked()) && this.mScreenshotChordVolumeDownKeyTriggered && this.mA11yShortcutChordVolumeUpKeyTriggered && !this.mScreenshotChordPowerKeyTriggered) {
            long now = SystemClock.uptimeMillis();
            if (now <= this.mScreenshotChordVolumeDownKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS && now <= this.mA11yShortcutChordVolumeUpKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS) {
                this.mScreenshotChordVolumeDownKeyConsumed = true;
                this.mA11yShortcutChordVolumeUpKeyConsumed = true;
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(17), getAccessibilityShortcutTimeout());
            }
        }
    }

    private void interceptRingerToggleChord() {
        if (this.mRingerToggleChord != 0 && this.mScreenshotChordPowerKeyTriggered && this.mA11yShortcutChordVolumeUpKeyTriggered) {
            long now = SystemClock.uptimeMillis();
            if (now <= this.mA11yShortcutChordVolumeUpKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS && now <= this.mScreenshotChordPowerKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS) {
                this.mA11yShortcutChordVolumeUpKeyConsumed = true;
                cancelPendingPowerKeyAction();
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(MSG_RINGER_TOGGLE_CHORD), getRingerToggleChordDelay());
            }
        }
    }

    private long getAccessibilityShortcutTimeout() {
        ViewConfiguration config = ViewConfiguration.get(this.mContext);
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_shortcut_dialog_shown", 0, this.mCurrentUserId) == 0) {
            return config.getAccessibilityShortcutKeyTimeout();
        }
        return config.getAccessibilityShortcutKeyTimeoutAfterConfirmation();
    }

    private long getScreenshotChordLongPressDelay() {
        if (this.mKeyguardDelegate.isShowing()) {
            return (long) (((float) ViewConfiguration.get(this.mContext).getScreenshotChordKeyTimeout()) * KEYGUARD_SCREENSHOT_CHORD_DELAY_MULTIPLIER);
        }
        return ViewConfiguration.get(this.mContext).getScreenshotChordKeyTimeout();
    }

    private long getRingerToggleChordDelay() {
        return (long) ViewConfiguration.getTapTimeout();
    }

    private void cancelPendingScreenshotChordAction() {
        this.mHandler.removeCallbacks(this.mScreenshotRunnable);
    }

    private void cancelPendingAccessibilityShortcutAction() {
        this.mHandler.removeMessages(17);
    }

    private void cancelPendingRingerToggleChordAction() {
        this.mHandler.removeMessages(MSG_RINGER_TOGGLE_CHORD);
    }

    private class ScreenshotRunnable implements Runnable {
        private int mScreenshotType;

        private ScreenshotRunnable() {
            this.mScreenshotType = 1;
        }

        public void setScreenshotType(int screenshotType) {
            this.mScreenshotType = screenshotType;
        }

        public void run() {
            PhoneWindowManager.this.mDefaultDisplayPolicy.takeScreenshot(this.mScreenshotType);
        }
    }

    public void showGlobalActions() {
        this.mHandler.removeMessages(10);
        this.mHandler.sendEmptyMessage(10);
    }

    /* access modifiers changed from: package-private */
    public void showGlobalActionsInternal() {
        if (this.mGlobalActions == null) {
            this.mGlobalActions = new MiuiGlobalActions(this.mContext, this.mWindowManagerFuncs);
        }
        this.mGlobalActions.showDialog(isKeyguardShowingAndNotOccluded(), isDeviceProvisioned());
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public boolean isUserSetupComplete() {
        boolean z = false;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0) {
            z = true;
        }
        boolean isSetupComplete = z;
        if (this.mHasFeatureLeanback) {
            return isSetupComplete & isTvUserSetupComplete();
        }
        return isSetupComplete;
    }

    private boolean isTvUserSetupComplete() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "tv_user_setup_complete", 0, -2) != 0;
    }

    /* access modifiers changed from: private */
    public void handleShortPressOnHome(int displayId) {
        HdmiControl hdmiControl = getHdmiControl();
        if (hdmiControl != null) {
            hdmiControl.turnOnTv();
        }
        DreamManagerInternal dreamManagerInternal = this.mDreamManagerInternal;
        if (dreamManagerInternal == null || !dreamManagerInternal.isDreaming()) {
            launchHomeFromHotKey(displayId);
        } else {
            this.mDreamManagerInternal.stopDream(false);
        }
    }

    private HdmiControl getHdmiControl() {
        if (this.mHdmiControl == null) {
            if (!this.mHasFeatureHdmiCec) {
                return null;
            }
            HdmiControlManager manager = (HdmiControlManager) this.mContext.getSystemService("hdmi_control");
            HdmiPlaybackClient client = null;
            if (manager != null) {
                client = manager.getPlaybackClient();
            }
            this.mHdmiControl = new HdmiControl(client);
        }
        return this.mHdmiControl;
    }

    private static class HdmiControl {
        private final HdmiPlaybackClient mClient;

        private HdmiControl(HdmiPlaybackClient client) {
            this.mClient = client;
        }

        public void turnOnTv() {
            HdmiPlaybackClient hdmiPlaybackClient = this.mClient;
            if (hdmiPlaybackClient != null) {
                hdmiPlaybackClient.oneTouchPlay(new HdmiPlaybackClient.OneTouchPlayCallback() {
                    public void onComplete(int result) {
                        if (result != 0) {
                            Log.w("WindowManager", "One touch play failed: " + result);
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void launchAllAppsAction() {
        Intent intent = new Intent("android.intent.action.ALL_APPS");
        if (this.mHasFeatureLeanback) {
            PackageManager pm = this.mContext.getPackageManager();
            Intent intentLauncher = new Intent("android.intent.action.MAIN");
            intentLauncher.addCategory("android.intent.category.HOME");
            ResolveInfo resolveInfo = pm.resolveActivityAsUser(intentLauncher, DumpState.DUMP_DEXOPT, this.mCurrentUserId);
            if (resolveInfo != null) {
                intent.setPackage(resolveInfo.activityInfo.packageName);
            }
        }
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    private void showPictureInPictureMenu(KeyEvent event) {
        this.mHandler.removeMessages(15);
        Message msg = this.mHandler.obtainMessage(15);
        msg.setAsynchronous(true);
        msg.sendToTarget();
    }

    /* access modifiers changed from: private */
    public void showPictureInPictureMenuInternal() {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.showPictureInPictureMenu();
        }
    }

    private class DisplayHomeButtonHandler {
        /* access modifiers changed from: private */
        public final int mDisplayId;
        private boolean mHomeConsumed;
        /* access modifiers changed from: private */
        public boolean mHomeDoubleTapPending;
        private final Runnable mHomeDoubleTapTimeoutRunnable = new Runnable() {
            public void run() {
                if (DisplayHomeButtonHandler.this.mHomeDoubleTapPending) {
                    boolean unused = DisplayHomeButtonHandler.this.mHomeDoubleTapPending = false;
                    PhoneWindowManager.this.handleShortPressOnHome(DisplayHomeButtonHandler.this.mDisplayId);
                }
            }
        };
        private boolean mHomePressed;

        DisplayHomeButtonHandler(int displayId) {
            this.mDisplayId = displayId;
        }

        /* access modifiers changed from: package-private */
        public int handleHomeButton(WindowManagerPolicy.WindowState win, KeyEvent event) {
            boolean keyguardOn = PhoneWindowManager.this.keyguardOn();
            int repeatCount = event.getRepeatCount();
            boolean down = event.getAction() == 0;
            boolean canceled = event.isCanceled();
            if (!down) {
                if (this.mDisplayId == 0) {
                    PhoneWindowManager.this.cancelPreloadRecentApps();
                }
                this.mHomePressed = false;
                if (this.mHomeConsumed) {
                    this.mHomeConsumed = false;
                    return -1;
                } else if (canceled) {
                    Log.i("WindowManager", "Ignoring HOME; event canceled.");
                    return -1;
                } else if (PhoneWindowManager.this.mDoubleTapOnHomeBehavior != 0) {
                    PhoneWindowManager.this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                    this.mHomeDoubleTapPending = true;
                    PhoneWindowManager.this.mHandler.postDelayed(this.mHomeDoubleTapTimeoutRunnable, (long) ViewConfiguration.getDoubleTapTimeout());
                    return -1;
                } else {
                    PhoneWindowManager.this.mHandler.post(new Runnable() {
                        public final void run() {
                            PhoneWindowManager.DisplayHomeButtonHandler.this.lambda$handleHomeButton$0$PhoneWindowManager$DisplayHomeButtonHandler();
                        }
                    });
                    return -1;
                }
            } else {
                WindowManager.LayoutParams attrs = win != null ? win.getAttrs() : null;
                if (attrs != null) {
                    int type = attrs.type;
                    if (type == 2009 || (attrs.privateFlags & 1024) != 0) {
                        return 0;
                    }
                    for (int t : PhoneWindowManager.WINDOW_TYPES_WHERE_HOME_DOESNT_WORK) {
                        if (type == t) {
                            return -1;
                        }
                    }
                }
                if (repeatCount == 0) {
                    this.mHomePressed = true;
                    if (this.mHomeDoubleTapPending) {
                        this.mHomeDoubleTapPending = false;
                        PhoneWindowManager.this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                        handleDoubleTapOnHome();
                    } else if (PhoneWindowManager.this.mDoubleTapOnHomeBehavior == 1 && this.mDisplayId == 0) {
                        PhoneWindowManager.this.preloadRecentApps();
                    }
                } else if ((event.getFlags() & 128) != 0 && !keyguardOn) {
                    PhoneWindowManager.this.mHandler.post(new Runnable(event) {
                        private final /* synthetic */ KeyEvent f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            PhoneWindowManager.DisplayHomeButtonHandler.this.lambda$handleHomeButton$1$PhoneWindowManager$DisplayHomeButtonHandler(this.f$1);
                        }
                    });
                }
                return -1;
            }
        }

        public /* synthetic */ void lambda$handleHomeButton$0$PhoneWindowManager$DisplayHomeButtonHandler() {
            PhoneWindowManager.this.handleShortPressOnHome(this.mDisplayId);
        }

        public /* synthetic */ void lambda$handleHomeButton$1$PhoneWindowManager$DisplayHomeButtonHandler(KeyEvent event) {
            handleLongPressOnHome(event.getDeviceId());
        }

        private void handleDoubleTapOnHome() {
            if (PhoneWindowManager.this.mDoubleTapOnHomeBehavior == 1) {
                this.mHomeConsumed = true;
                PhoneWindowManager.this.toggleRecentApps();
            }
        }

        private void handleLongPressOnHome(int deviceId) {
            if (PhoneWindowManager.this.mLongPressOnHomeBehavior != 0) {
                this.mHomeConsumed = true;
                boolean unused = PhoneWindowManager.this.performHapticFeedback(0, false, "Home - Long Press");
                int access$3400 = PhoneWindowManager.this.mLongPressOnHomeBehavior;
                if (access$3400 == 1) {
                    PhoneWindowManager.this.launchAllAppsAction();
                } else if (access$3400 != 2) {
                    Log.w("WindowManager", "Undefined home long press behavior: " + PhoneWindowManager.this.mLongPressOnHomeBehavior);
                } else {
                    PhoneWindowManager.this.launchAssistAction((String) null, deviceId);
                }
            }
        }

        public String toString() {
            return String.format("mDisplayId = %d, mHomePressed = %b", new Object[]{Integer.valueOf(this.mDisplayId), Boolean.valueOf(this.mHomePressed)});
        }
    }

    private boolean isRoundWindow() {
        return this.mContext.getResources().getConfiguration().isScreenRound();
    }

    public void setDefaultDisplay(WindowManagerPolicy.DisplayContentInfo displayContentInfo) {
        this.mDefaultDisplay = displayContentInfo.getDisplay();
        this.mDefaultDisplayRotation = displayContentInfo.getDisplayRotation();
        this.mDefaultDisplayPolicy = this.mDefaultDisplayRotation.getDisplayPolicy();
    }

    public void init(Context context, IWindowManager windowManager, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        int maxRadius;
        int maxVertical;
        int minVertical;
        int maxHorizontal;
        int minHorizontal;
        Context context2 = context;
        this.mContext = context2;
        this.mWindowManager = windowManager;
        this.mWindowManagerFuncs = windowManagerFuncs;
        this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
        this.mDreamManagerInternal = (DreamManagerInternal) LocalServices.getService(DreamManagerInternal.class);
        this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mHasFeatureWatch = this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        this.mHasFeatureLeanback = this.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        this.mHasFeatureHdmiCec = this.mContext.getPackageManager().hasSystemFeature("android.hardware.hdmi.cec");
        this.mAccessibilityShortcutController = new AccessibilityShortcutController(this.mContext, new Handler(), this.mCurrentUserId);
        this.mLogger = new MetricsLogger();
        boolean burnInProtectionEnabled = context.getResources().getBoolean(17891435);
        boolean burnInProtectionDevMode = SystemProperties.getBoolean("persist.debug.force_burn_in", false);
        if (burnInProtectionEnabled || burnInProtectionDevMode) {
            if (burnInProtectionDevMode) {
                minHorizontal = -8;
                maxHorizontal = 8;
                minVertical = -8;
                maxVertical = -4;
                maxRadius = isRoundWindow() ? 6 : -1;
            } else {
                Resources resources = context.getResources();
                int minHorizontal2 = resources.getInteger(17694756);
                int maxHorizontal2 = resources.getInteger(17694753);
                int minVertical2 = resources.getInteger(17694757);
                int maxVertical2 = resources.getInteger(17694755);
                maxRadius = resources.getInteger(17694754);
                minHorizontal = minHorizontal2;
                maxHorizontal = maxHorizontal2;
                minVertical = minVertical2;
                maxVertical = maxVertical2;
            }
            BurnInProtectionHelper burnInProtectionHelper = r2;
            BurnInProtectionHelper burnInProtectionHelper2 = new BurnInProtectionHelper(context, minHorizontal, maxHorizontal, minVertical, maxVertical, maxRadius);
            this.mBurnInProtectionHelper = burnInProtectionHelper;
        }
        this.mHandler = new PolicyHandler();
        this.mWakeGestureListener = new MyWakeGestureListener(this.mContext, this.mHandler);
        try {
            if (DeviceFeature.hasMirihiSupport()) {
                this.mSlideScreenListener = new SlideCoverListener(this.mContext);
            }
        } catch (Exception e) {
            Slog.d("SlideCoverListener", e.toString());
        }
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver.observe();
        this.mShortcutManager = new ShortcutManager(context2);
        this.mUiMode = context.getResources().getInteger(17694782);
        this.mHomeIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mHomeIntent.addCategory("android.intent.category.HOME");
        this.mHomeIntent.addFlags(270532608);
        this.mEnableCarDockHomeCapture = context.getResources().getBoolean(17891436);
        this.mCarDockIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mCarDockIntent.addCategory("android.intent.category.CAR_DOCK");
        this.mCarDockIntent.addFlags(270532608);
        this.mDeskDockIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mDeskDockIntent.addCategory("android.intent.category.DESK_DOCK");
        this.mDeskDockIntent.addFlags(270532608);
        this.mVrHeadsetHomeIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mVrHeadsetHomeIntent.addCategory("android.intent.category.VR_HOME");
        this.mVrHeadsetHomeIntent.addFlags(270532608);
        this.mPowerManager = (PowerManager) context2.getSystemService("power");
        boolean z = true;
        this.mBroadcastWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mBroadcastWakeLock");
        this.mPowerKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mPowerKeyWakeLock");
        this.mEnableShiftMenuBugReports = SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.debuggable"));
        this.mLidKeyboardAccessibility = this.mContext.getResources().getInteger(17694817);
        this.mLidNavigationAccessibility = this.mContext.getResources().getInteger(17694818);
        this.mLidControlsDisplayFold = this.mContext.getResources().getBoolean(17891473);
        this.mAllowTheaterModeWakeFromKey = this.mContext.getResources().getBoolean(17891351);
        this.mAllowTheaterModeWakeFromPowerKey = this.mAllowTheaterModeWakeFromKey || this.mContext.getResources().getBoolean(17891355);
        this.mAllowTheaterModeWakeFromMotion = this.mContext.getResources().getBoolean(17891353);
        this.mAllowTheaterModeWakeFromMotionWhenNotDreaming = this.mContext.getResources().getBoolean(17891354);
        this.mAllowTheaterModeWakeFromCameraLens = this.mContext.getResources().getBoolean(17891348);
        this.mAllowTheaterModeWakeFromLidSwitch = this.mContext.getResources().getBoolean(17891352);
        this.mAllowTheaterModeWakeFromWakeGesture = this.mContext.getResources().getBoolean(17891350);
        this.mGoToSleepOnButtonPressTheaterMode = this.mContext.getResources().getBoolean(17891463);
        this.mSupportLongPressPowerWhenNonInteractive = this.mContext.getResources().getBoolean(17891538);
        this.mLongPressOnBackBehavior = this.mContext.getResources().getInteger(17694822);
        this.mShortPressOnPowerBehavior = this.mContext.getResources().getInteger(17694894);
        this.mLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694824);
        this.mVeryLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694909);
        this.mDoublePressOnPowerBehavior = this.mContext.getResources().getInteger(17694796);
        this.mTriplePressOnPowerBehavior = this.mContext.getResources().getInteger(17694906);
        this.mShortPressOnSleepBehavior = this.mContext.getResources().getInteger(17694895);
        this.mVeryLongPressTimeout = this.mContext.getResources().getInteger(17694910);
        this.mAllowStartActivityForLongPressOnPowerDuringSetup = this.mContext.getResources().getBoolean(17891347);
        this.mHapticTextHandleEnabled = this.mContext.getResources().getBoolean(17891442);
        if (AudioSystem.getPlatformType(this.mContext) != 2) {
            z = false;
        }
        this.mUseTvRouting = z;
        this.mHandleVolumeKeysInWM = this.mContext.getResources().getBoolean(17891465);
        this.mPerDisplayFocusEnabled = this.mContext.getResources().getBoolean(17891332);
        readConfigurationDependentBehaviors();
        if (this.mLidControlsDisplayFold) {
            this.mDisplayFoldController = DisplayFoldController.create(context2, 0);
        } else if (SystemProperties.getBoolean("persist.debug.force_foldable", false)) {
            this.mDisplayFoldController = DisplayFoldController.createWithProxSensor(context2, 0);
        }
        this.mAccessibilityManager = (AccessibilityManager) context2.getSystemService("accessibility");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
        filter.addAction(UiModeManager.ACTION_EXIT_CAR_MODE);
        filter.addAction(UiModeManager.ACTION_ENTER_DESK_MODE);
        filter.addAction(UiModeManager.ACTION_EXIT_DESK_MODE);
        filter.addAction("android.intent.action.DOCK_EVENT");
        Intent intent = context2.registerReceiver(this.mDockReceiver, filter);
        if (intent != null) {
            this.mDefaultDisplayPolicy.setDockMode(intent.getIntExtra("android.intent.extra.DOCK_STATE", 0));
        }
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.DREAMING_STARTED");
        filter2.addAction("android.intent.action.DREAMING_STOPPED");
        context2.registerReceiver(this.mDreamReceiver, filter2);
        context2.registerReceiver(this.mMultiuserReceiver, new IntentFilter("android.intent.action.USER_SWITCHED"));
        this.mVibrator = (Vibrator) context2.getSystemService("vibrator");
        this.mLongPressVibePattern = getLongIntArray(this.mContext.getResources(), 17236037);
        this.mCalendarDateVibePattern = getLongIntArray(this.mContext.getResources(), 17235996);
        this.mSafeModeEnabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236058);
        this.mScreenshotChordEnabled = this.mContext.getResources().getBoolean(17891450);
        this.mGlobalKeyManager = new GlobalKeyManager(this.mContext);
        initializeHdmiState();
        if (!this.mPowerManager.isInteractive()) {
            startedGoingToSleep(2);
            finishedGoingToSleep(2);
        }
        this.mWindowManagerInternal.registerAppTransitionListener(new WindowManagerInternal.AppTransitionListener() {
            public int onAppTransitionStartingLocked(int transit, long duration, long statusBarAnimationStartTime, long statusBarAnimationDuration) {
                return PhoneWindowManager.this.handleStartTransitionForKeyguardLw(transit, duration);
            }

            public void onAppTransitionCancelledLocked(int transit) {
                int unused = PhoneWindowManager.this.handleStartTransitionForKeyguardLw(transit, 0);
            }
        });
        this.mKeyguardDelegate = new KeyguardServiceDelegate(this.mContext, new KeyguardStateMonitor.StateCallback() {
            public void onTrustedChanged() {
                PhoneWindowManager.this.mWindowManagerFuncs.notifyKeyguardTrustedChanged();
            }

            public void onShowingChanged() {
                PhoneWindowManager.this.mWindowManagerFuncs.onKeyguardShowingAndNotOccludedChanged();
            }

            public void unblockScreenOn() {
            }
        });
    }

    private void readConfigurationDependentBehaviors() {
        Resources res = this.mContext.getResources();
        this.mLongPressOnHomeBehavior = res.getInteger(17694823);
        int i = this.mLongPressOnHomeBehavior;
        if (i < 0 || i > 2) {
            this.mLongPressOnHomeBehavior = 0;
        }
        this.mDoubleTapOnHomeBehavior = res.getInteger(17694797);
        int i2 = this.mDoubleTapOnHomeBehavior;
        if (i2 < 0 || i2 > 1) {
            this.mDoubleTapOnHomeBehavior = 0;
        }
        this.mShortPressOnWindowBehavior = 0;
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.picture_in_picture")) {
            this.mShortPressOnWindowBehavior = 1;
        }
    }

    public void updateSettings() {
        ContentResolver resolver = this.mContext.getContentResolver();
        boolean updateRotation = false;
        synchronized (this.mLock) {
            this.mEndcallBehavior = Settings.System.getIntForUser(resolver, "end_button_behavior", 2, -2);
            this.mIncallPowerBehavior = Settings.Secure.getIntForUser(resolver, "incall_power_button_behavior", 1, -2);
            boolean hasSoftInput = false;
            this.mIncallBackBehavior = Settings.Secure.getIntForUser(resolver, "incall_back_button_behavior", 0, -2);
            this.mSystemNavigationKeysEnabled = Settings.Secure.getIntForUser(resolver, "system_navigation_keys_enabled", 0, -2) == 1;
            this.mRingerToggleChord = Settings.Secure.getIntForUser(resolver, "volume_hush_gesture", 0, -2);
            this.mPowerButtonSuppressionDelayMillis = Settings.Global.getInt(resolver, "power_button_suppression_delay_after_gesture_wake", 800);
            if (!this.mContext.getResources().getBoolean(17891578)) {
                this.mRingerToggleChord = 0;
            }
            boolean wakeGestureEnabledSetting = Settings.Secure.getIntForUser(resolver, "wake_gesture_enabled", 0, -2) != 0;
            if (this.mWakeGestureEnabledSetting != wakeGestureEnabledSetting) {
                this.mWakeGestureEnabledSetting = wakeGestureEnabledSetting;
                updateWakeGestureListenerLp();
            }
            this.mLockScreenTimeout = Settings.System.getIntForUser(resolver, "screen_off_timeout", 0, -2);
            String imId = Settings.Secure.getStringForUser(resolver, "default_input_method", -2);
            if (imId != null && imId.length() > 0) {
                hasSoftInput = true;
            }
            if (this.mHasSoftInput != hasSoftInput) {
                this.mHasSoftInput = hasSoftInput;
                updateRotation = true;
            }
            this.mLongPressOnPowerBehavior = Settings.Global.getInt(resolver, "power_button_long_press", this.mContext.getResources().getInteger(17694824));
            this.mVeryLongPressOnPowerBehavior = Settings.Global.getInt(resolver, "power_button_very_long_press", this.mContext.getResources().getInteger(17694909));
        }
        if (updateRotation) {
            updateRotation(true);
        }
    }

    private void updateWakeGestureListenerLp() {
        if (shouldEnableWakeGestureLp()) {
            this.mWakeGestureListener.requestWakeUpTrigger();
        } else {
            this.mWakeGestureListener.cancelWakeUpTrigger();
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldEnableWakeGestureLp() {
        if (!this.mWakeGestureEnabledSetting || this.mDefaultDisplayPolicy.isAwake() || ((getLidBehavior() == 1 && this.mDefaultDisplayPolicy.getLidState() == 0) || !this.mWakeGestureListener.isSupported())) {
            return false;
        }
        return true;
    }

    public int checkAddPermission(WindowManager.LayoutParams attrs, int[] outAppOp) {
        ApplicationInfo appInfo;
        int type = attrs.type;
        if (((attrs.privateFlags & DumpState.DUMP_DEXOPT) != 0) && this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0) {
            return -8;
        }
        outAppOp[0] = -1;
        if ((type < 1 || type > 99) && ((type < 1000 || type > 1999) && (type < 2000 || type > 2999))) {
            return -10;
        }
        if (type < 2000 || type > 2999) {
            return 0;
        }
        if (WindowManager.LayoutParams.isSystemAlertWindowType(type)) {
            outAppOp[0] = 24;
            int callingUid = Binder.getCallingUid();
            if (UserHandle.getAppId(callingUid) == 1000) {
                return 0;
            }
            try {
                appInfo = this.mContext.getPackageManager().getApplicationInfoAsUser(attrs.packageName, 0, UserHandle.getUserId(callingUid));
            } catch (PackageManager.NameNotFoundException e) {
                appInfo = null;
            }
            if (appInfo != null && (type == 2038 || appInfo.targetSdkVersion < MSG_NOTIFY_USER_ACTIVITY)) {
                int mode = this.mAppOpsManager.noteOpNoThrow(outAppOp[0], callingUid, attrs.packageName);
                if (mode == 0 || mode == 1) {
                    return 0;
                }
                if (mode != 2) {
                    if (this.mContext.checkCallingOrSelfPermission("android.permission.SYSTEM_ALERT_WINDOW") == 0) {
                        return 0;
                    }
                    return -8;
                } else if (appInfo.targetSdkVersion < 23) {
                    return 0;
                } else {
                    return -8;
                }
            } else if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0) {
                return 0;
            } else {
                return -8;
            }
        } else if (type != 2005) {
            if (!(type == 2011 || type == 2013 || type == 2023 || type == 2035 || type == 2037)) {
                switch (type) {
                    case 2030:
                    case 2031:
                    case 2032:
                        break;
                    default:
                        if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0) {
                            return 0;
                        }
                        return -8;
                }
            }
            return 0;
        } else {
            outAppOp[0] = 45;
            return 0;
        }
    }

    public boolean checkShowToOwnerOnly(WindowManager.LayoutParams attrs) {
        int i = attrs.type;
        if (!(i == 3 || i == 2014 || i == 2024 || i == 2030 || i == 2034 || i == 2037 || i == 2026 || i == 2027)) {
            switch (i) {
                case 2000:
                case 2001:
                case 2002:
                    break;
                default:
                    switch (i) {
                        case 2007:
                        case 2008:
                        case 2009:
                            break;
                        default:
                            switch (i) {
                                case 2017:
                                case 2018:
                                case 2019:
                                case 2020:
                                case 2021:
                                case 2022:
                                    break;
                                default:
                                    if ((attrs.privateFlags & 16) == 0) {
                                        return true;
                                    }
                                    break;
                            }
                    }
            }
        }
        if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void readLidState() {
        this.mDefaultDisplayPolicy.setLidState(this.mWindowManagerFuncs.getLidState());
    }

    private void readCameraLensCoverState() {
        this.mCameraLensCoverState = this.mWindowManagerFuncs.getCameraLensCoverState();
    }

    private boolean isHidden(int accessibilityMode) {
        int lidState = this.mDefaultDisplayPolicy.getLidState();
        if (accessibilityMode != 1) {
            if (accessibilityMode == 2 && lidState == 1) {
                return true;
            }
            return false;
        } else if (lidState == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void adjustConfigurationLw(Configuration config, int keyboardPresence, int navigationPresence) {
        this.mHaveBuiltInKeyboard = (keyboardPresence & 1) != 0;
        readConfigurationDependentBehaviors();
        readLidState();
        if (config.keyboard == 1 || (keyboardPresence == 1 && isHidden(this.mLidKeyboardAccessibility))) {
            config.hardKeyboardHidden = 2;
            if (!this.mHasSoftInput) {
                config.keyboardHidden = 2;
            }
        }
        if (config.navigation == 1 || (navigationPresence == 1 && isHidden(this.mLidNavigationAccessibility))) {
            config.navigationHidden = 2;
        }
    }

    public int getMaxWallpaperLayer() {
        return getWindowLayerFromTypeLw(2000);
    }

    public boolean isKeyguardHostWindow(WindowManager.LayoutParams attrs) {
        return attrs.type == 2000;
    }

    public boolean canBeHiddenByKeyguardLw(WindowManagerPolicy.WindowState win) {
        int i;
        if ((win.getAppToken() != null && !win.isHoldOn()) || (i = win.getAttrs().type) == 2000 || i == 2013 || i == 2019 || i == 2023 || getWindowLayerLw(win) >= getWindowLayerFromTypeLw(2000)) {
            return false;
        }
        return true;
    }

    private boolean shouldBeHiddenByKeyguard(WindowManagerPolicy.WindowState win, WindowManagerPolicy.WindowState imeTarget) {
        WindowManager.LayoutParams attrs = win.getAttrs();
        if (attrs.type == 2034 && !this.mWindowManagerInternal.isStackVisibleLw(3)) {
            return true;
        }
        if (win.isInputMethodWindow() && (this.mAodShowing || !this.mDefaultDisplayPolicy.isWindowManagerDrawComplete())) {
            return true;
        }
        boolean allowWhenLocked = win.isInputMethodWindow() && (imeTarget != null && imeTarget.isVisibleLw() && (imeTarget.canShowWhenLocked() || !canBeHiddenByKeyguardLw(imeTarget)));
        boolean isKeyguardShowing = this.mKeyguardDelegate.isShowing();
        if (isKeyguardShowing && isKeyguardOccluded()) {
            allowWhenLocked |= win.canShowWhenLocked() || (attrs.privateFlags & 256) != 0;
        }
        if (!isKeyguardShowing || allowWhenLocked || win.getDisplayId() != 0) {
            return false;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 21 */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x01ad, code lost:
        android.util.Log.w("WindowManager", "view not successfully added to wm, removing view");
        r9.removeViewImmediate(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r14.setFlags(((r19 | 16) | 8) | 131072, ((r19 | 16) | 8) | 131072);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        r14.setDefaultIcon(r28);
        r14.setDefaultLogo(r29);
        r14.setLayout(-1, -1);
        r0 = r14.getAttributes();
        r0.token = r2;
        r0.packageName = r3;
        r0.windowAnimations = r14.getWindowStyle().getResourceId(8, 0);
        r0.privateFlags |= 1;
        r0.privateFlags |= 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00f9, code lost:
        if (r25.supportsScreen() != false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00fb, code lost:
        r0.privateFlags |= 128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0101, code lost:
        r0.setTitle("Splash Screen " + r3);
        addSplashscreenContent(r14, r12);
        r9 = (android.view.WindowManager) r12.getSystemService("window");
        r10 = r14.getDecorView();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0135, code lost:
        if ((r12.getResources().getConfiguration().uiMode & 48) == 32) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0137, code lost:
        if (r15 == null) goto L_0x013b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0139, code lost:
        r4 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x013b, code lost:
        r4 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x013c, code lost:
        com.android.server.policy.PhoneWindowManagerInjector.addStartingWindow(r12, r10, r14, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x013f, code lost:
        r9.addView(r10, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0146, code lost:
        if (r10.getParent() == null) goto L_0x014e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0148, code lost:
        r4 = new com.android.server.policy.SplashScreenSurface(r10, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x014e, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0153, code lost:
        if (r10.getParent() != null) goto L_0x0160;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0155, code lost:
        android.util.Log.w("WindowManager", "view not successfully added to wm, removing view");
        r9.removeViewImmediate(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0160, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0161, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0162, code lost:
        r13 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0166, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0167, code lost:
        r13 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x016a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x016b, code lost:
        r13 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0173, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0175, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.policy.WindowManagerPolicy.StartingSurface addSplashScreen(android.os.IBinder r22, java.lang.String r23, int r24, android.content.res.CompatibilityInfo r25, java.lang.CharSequence r26, int r27, int r28, int r29, int r30, android.content.res.Configuration r31, int r32) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r27
            r6 = r31
            r7 = r32
            r8 = 0
            if (r3 != 0) goto L_0x0012
            return r8
        L_0x0012:
            r9 = 0
            r10 = 0
            android.content.Context r0 = r1.mContext     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            android.content.Context r11 = r1.getDisplayContext(r0, r7)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            if (r11 != 0) goto L_0x0031
            if (r10 == 0) goto L_0x0030
            android.view.ViewParent r12 = r10.getParent()
            if (r12 != 0) goto L_0x0030
            java.lang.String r12 = "WindowManager"
            java.lang.String r13 = "view not successfully added to wm, removing view"
            android.util.Log.w(r12, r13)
            r9.removeViewImmediate(r10)
        L_0x0030:
            return r8
        L_0x0031:
            r12 = r11
            int r0 = r12.getThemeResId()     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            if (r4 != r0) goto L_0x003a
            if (r5 == 0) goto L_0x0045
        L_0x003a:
            r0 = 4
            android.content.Context r0 = r12.createPackageContext(r3, r0)     // Catch:{ NameNotFoundException -> 0x0044 }
            r12 = r0
            r12.setTheme(r4)     // Catch:{ NameNotFoundException -> 0x0044 }
            goto L_0x0045
        L_0x0044:
            r0 = move-exception
        L_0x0045:
            r0 = 0
            r13 = 1
            if (r6 == 0) goto L_0x0072
            android.content.res.Configuration r14 = android.content.res.Configuration.EMPTY     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            boolean r14 = r6.equals(r14)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            if (r14 != 0) goto L_0x0072
            android.content.Context r14 = r12.createConfigurationContext(r6)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r14.setTheme(r4)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            int[] r15 = com.android.internal.R.styleable.Window     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            android.content.res.TypedArray r15 = r14.obtainStyledAttributes(r15)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            int r16 = r15.getResourceId(r13, r0)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r17 = r16
            r0 = r17
            if (r0 == 0) goto L_0x006f
            android.graphics.drawable.Drawable r17 = r14.getDrawable(r0)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            if (r17 == 0) goto L_0x006f
            r12 = r14
        L_0x006f:
            r15.recycle()     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
        L_0x0072:
            com.android.internal.policy.MiuiPhoneWindow r0 = new com.android.internal.policy.MiuiPhoneWindow     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r0.<init>(r12)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r14 = r0
            r14.setIsStartingWindow(r13)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            android.content.res.Resources r0 = r12.getResources()     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            java.lang.CharSequence r0 = r0.getText(r5, r8)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r15 = r0
            if (r15 == 0) goto L_0x008c
            r14.setTitle(r15, r13)     // Catch:{ BadTokenException -> 0x01b9, RuntimeException -> 0x0187, all -> 0x017f }
            r8 = r26
            goto L_0x0092
        L_0x008c:
            r8 = r26
            r0 = 0
            r14.setTitle(r8, r0)     // Catch:{ BadTokenException -> 0x017d, RuntimeException -> 0x017b, all -> 0x0179 }
        L_0x0092:
            r0 = 3
            r14.setType(r0)     // Catch:{ BadTokenException -> 0x017d, RuntimeException -> 0x017b, all -> 0x0179 }
            com.android.server.policy.WindowManagerPolicy$WindowManagerFuncs r0 = r1.mWindowManagerFuncs     // Catch:{ BadTokenException -> 0x017d, RuntimeException -> 0x017b, all -> 0x0179 }
            java.lang.Object r18 = r0.getWindowManagerLock()     // Catch:{ BadTokenException -> 0x017d, RuntimeException -> 0x017b, all -> 0x0179 }
            monitor-enter(r18)     // Catch:{ BadTokenException -> 0x017d, RuntimeException -> 0x017b, all -> 0x0179 }
            if (r7 != 0) goto L_0x00b1
            boolean r0 = r1.mKeyguardOccluded     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x00b1
            r0 = 524288(0x80000, float:7.34684E-40)
            r0 = r30 | r0
            r19 = r0
            goto L_0x00b3
        L_0x00aa:
            r0 = move-exception
            r13 = r28
            r19 = r30
            goto L_0x0171
        L_0x00b1:
            r19 = r30
        L_0x00b3:
            monitor-exit(r18)     // Catch:{ all -> 0x016e }
            r0 = r19 | 16
            r13 = 8
            r0 = r0 | r13
            r18 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r18
            r20 = r19 | 16
            r20 = r20 | 8
            r13 = r20 | r18
            r14.setFlags(r0, r13)     // Catch:{ BadTokenException -> 0x016a, RuntimeException -> 0x0166, all -> 0x0161 }
            r13 = r28
            r14.setDefaultIcon(r13)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4 = r29
            r14.setDefaultLogo(r4)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r0 = -1
            r14.setLayout(r0, r0)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            android.view.WindowManager$LayoutParams r0 = r14.getAttributes()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r0.token = r2     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r0.packageName = r3     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            android.content.res.TypedArray r4 = r14.getWindowStyle()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r5 = 8
            r6 = 0
            int r4 = r4.getResourceId(r5, r6)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r0.windowAnimations = r4     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            int r4 = r0.privateFlags     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r5 = 1
            r4 = r4 | r5
            r0.privateFlags = r4     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            int r4 = r0.privateFlags     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4 = r4 | 16
            r0.privateFlags = r4     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            boolean r4 = r25.supportsScreen()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            if (r4 != 0) goto L_0x0101
            int r4 = r0.privateFlags     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r0.privateFlags = r4     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
        L_0x0101:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4.<init>()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            java.lang.String r5 = "Splash Screen "
            r4.append(r5)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4.append(r3)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            java.lang.String r4 = r4.toString()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r0.setTitle(r4)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r1.addSplashscreenContent(r14, r12)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            java.lang.String r4 = "window"
            java.lang.Object r4 = r12.getSystemService(r4)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            android.view.WindowManager r4 = (android.view.WindowManager) r4     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r9 = r4
            android.view.View r4 = r14.getDecorView()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r10 = r4
            android.content.res.Resources r4 = r12.getResources()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            android.content.res.Configuration r4 = r4.getConfiguration()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            int r4 = r4.uiMode     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4 = r4 & 48
            r5 = 32
            if (r4 == r5) goto L_0x013f
            if (r15 == 0) goto L_0x013b
            r4 = r15
            goto L_0x013c
        L_0x013b:
            r4 = r8
        L_0x013c:
            com.android.server.policy.PhoneWindowManagerInjector.addStartingWindow(r12, r10, r14, r4)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
        L_0x013f:
            r9.addView(r10, r0)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            android.view.ViewParent r4 = r10.getParent()     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            if (r4 == 0) goto L_0x014e
            com.android.server.policy.SplashScreenSurface r4 = new com.android.server.policy.SplashScreenSurface     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            r4.<init>(r10, r2)     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
            goto L_0x014f
        L_0x014e:
            r4 = 0
        L_0x014f:
            android.view.ViewParent r5 = r10.getParent()
            if (r5 != 0) goto L_0x0160
            java.lang.String r5 = "WindowManager"
            java.lang.String r6 = "view not successfully added to wm, removing view"
            android.util.Log.w(r5, r6)
            r9.removeViewImmediate(r10)
        L_0x0160:
            return r4
        L_0x0161:
            r0 = move-exception
            r13 = r28
            goto L_0x01ea
        L_0x0166:
            r0 = move-exception
            r13 = r28
            goto L_0x018e
        L_0x016a:
            r0 = move-exception
            r13 = r28
            goto L_0x01c0
        L_0x016e:
            r0 = move-exception
            r13 = r28
        L_0x0171:
            monitor-exit(r18)     // Catch:{ all -> 0x0177 }
            throw r0     // Catch:{ BadTokenException -> 0x0175, RuntimeException -> 0x0173 }
        L_0x0173:
            r0 = move-exception
            goto L_0x018e
        L_0x0175:
            r0 = move-exception
            goto L_0x01c0
        L_0x0177:
            r0 = move-exception
            goto L_0x0171
        L_0x0179:
            r0 = move-exception
            goto L_0x0182
        L_0x017b:
            r0 = move-exception
            goto L_0x018a
        L_0x017d:
            r0 = move-exception
            goto L_0x01bc
        L_0x017f:
            r0 = move-exception
            r8 = r26
        L_0x0182:
            r13 = r28
            r19 = r30
            goto L_0x01ea
        L_0x0187:
            r0 = move-exception
            r8 = r26
        L_0x018a:
            r13 = r28
            r19 = r30
        L_0x018e:
            java.lang.String r4 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e9 }
            r5.<init>()     // Catch:{ all -> 0x01e9 }
            r5.append(r2)     // Catch:{ all -> 0x01e9 }
            java.lang.String r6 = " failed creating starting window"
            r5.append(r6)     // Catch:{ all -> 0x01e9 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x01e9 }
            android.util.Log.w(r4, r5, r0)     // Catch:{ all -> 0x01e9 }
            if (r10 == 0) goto L_0x01e7
            android.view.ViewParent r0 = r10.getParent()
            if (r0 != 0) goto L_0x01e7
        L_0x01ad:
            java.lang.String r0 = "WindowManager"
            java.lang.String r4 = "view not successfully added to wm, removing view"
            android.util.Log.w(r0, r4)
            r9.removeViewImmediate(r10)
            goto L_0x01e7
        L_0x01b9:
            r0 = move-exception
            r8 = r26
        L_0x01bc:
            r13 = r28
            r19 = r30
        L_0x01c0:
            java.lang.String r4 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e9 }
            r5.<init>()     // Catch:{ all -> 0x01e9 }
            r5.append(r2)     // Catch:{ all -> 0x01e9 }
            java.lang.String r6 = " already running, starting window not displayed. "
            r5.append(r6)     // Catch:{ all -> 0x01e9 }
            java.lang.String r6 = r0.getMessage()     // Catch:{ all -> 0x01e9 }
            r5.append(r6)     // Catch:{ all -> 0x01e9 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x01e9 }
            android.util.Log.w(r4, r5)     // Catch:{ all -> 0x01e9 }
            if (r10 == 0) goto L_0x01e7
            android.view.ViewParent r0 = r10.getParent()
            if (r0 != 0) goto L_0x01e7
            goto L_0x01ad
        L_0x01e7:
            r4 = 0
            return r4
        L_0x01e9:
            r0 = move-exception
        L_0x01ea:
            if (r10 == 0) goto L_0x01fd
            android.view.ViewParent r4 = r10.getParent()
            if (r4 != 0) goto L_0x01fd
            java.lang.String r4 = "WindowManager"
            java.lang.String r5 = "view not successfully added to wm, removing view"
            android.util.Log.w(r4, r5)
            r9.removeViewImmediate(r10)
        L_0x01fd:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.PhoneWindowManager.addSplashScreen(android.os.IBinder, java.lang.String, int, android.content.res.CompatibilityInfo, java.lang.CharSequence, int, int, int, int, android.content.res.Configuration, int):com.android.server.policy.WindowManagerPolicy$StartingSurface");
    }

    private void addSplashscreenContent(PhoneWindow win, Context ctx) {
        Drawable drawable;
        TypedArray a = ctx.obtainStyledAttributes(R.styleable.Window);
        int resId = a.getResourceId(48, 0);
        a.recycle();
        if (resId != 0 && (drawable = ctx.getDrawable(resId)) != null) {
            View v = new View(ctx);
            v.setBackground(drawable);
            win.setContentView(v);
        }
    }

    private Context getDisplayContext(Context context, int displayId) {
        if (displayId == 0) {
            return context;
        }
        Display targetDisplay = this.mDisplayManager.getDisplay(displayId);
        if (targetDisplay == null) {
            return null;
        }
        return context.createDisplayContext(targetDisplay);
    }

    public Animation createHiddenByKeyguardExit(boolean onWallpaper, boolean goingToNotificationShade) {
        if (goingToNotificationShade) {
            return AnimationUtils.loadAnimation(this.mContext, 17432820);
        }
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(this.mContext, 17432819);
        List<Animation> animations = set.getAnimations();
        for (int i = animations.size() - 1; i >= 0; i--) {
            animations.get(i).setInterpolator(this.mLogDecelerateInterpolator);
        }
        return set;
    }

    private boolean isMiuiRom() {
        return !TextUtils.isEmpty(SystemProperties.get("ro.miui.ui.version.name", "")) || !TextUtils.isEmpty(SystemProperties.get("ro.miui.ui.version.code", ""));
    }

    public Animation createHiddenByKeyguardExitForAppWindow(boolean onWallpaper, boolean goingToNotificationShade) {
        if (goingToNotificationShade) {
            return AnimationUtils.loadAnimation(this.mContext, 17432820);
        }
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(this.mContext, 17432821);
        List<Animation> animations = set.getAnimations();
        for (int i = animations.size() - 1; i >= 0; i--) {
            animations.get(i).setInterpolator(this.mCubicEaseOutInterpolator);
        }
        return set;
    }

    public Animation createKeyguardWallpaperExit(boolean goingToNotificationShade) {
        if (goingToNotificationShade) {
            return null;
        }
        return AnimationUtils.loadAnimation(this.mContext, 17432825);
    }

    private static void awakenDreams() {
        IDreamManager dreamManager = getDreamManager();
        if (dreamManager != null) {
            try {
                dreamManager.awaken();
            } catch (RemoteException e) {
            }
        }
    }

    static IDreamManager getDreamManager() {
        return IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
    }

    /* access modifiers changed from: package-private */
    public TelecomManager getTelecommService() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }

    static IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
        if (audioService == null) {
            Log.w("WindowManager", "Unable to find IAudioService interface.");
        }
        return audioService;
    }

    /* access modifiers changed from: package-private */
    public boolean keyguardOn() {
        return isKeyguardShowingAndNotOccluded() || inKeyguardRestrictedKeyInputMode();
    }

    public long interceptKeyBeforeDispatching(WindowManagerPolicy.WindowState win, KeyEvent event, int policyFlags) {
        long result = interceptKeyBeforeDispatchingInner(win, event, policyFlags);
        int eventDisplayId = event.getDisplayId();
        if (result != 0 || this.mPerDisplayFocusEnabled || eventDisplayId == -1 || eventDisplayId == this.mTopFocusedDisplayId) {
            return result;
        }
        long eventDownTime = event.getDownTime();
        if (this.mMovingDisplayToTopKeyTime < eventDownTime) {
            this.mMovingDisplayToTopKeyTime = eventDownTime;
            this.mMovingDisplayToTopKeyTriggered = true;
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(28, eventDisplayId, 0));
            return MOVING_DISPLAY_TO_TOP_DURATION_MILLIS;
        } else if (this.mMovingDisplayToTopKeyTriggered) {
            return MOVING_DISPLAY_TO_TOP_DURATION_MILLIS;
        } else {
            Slog.w("WindowManager", "Dropping key targeting non-focused display #" + eventDisplayId + " keyCode=" + KeyEvent.keyCodeToString(event.getKeyCode()));
            return -1;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:220:0x02b7 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x02b8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long interceptKeyBeforeDispatchingInner(com.android.server.policy.WindowManagerPolicy.WindowState r31, android.view.KeyEvent r32, int r33) {
        /*
            r30 = this;
            r1 = r30
            r2 = r32
            boolean r3 = r30.keyguardOn()
            int r4 = r32.getKeyCode()
            int r5 = r32.getRepeatCount()
            int r6 = r32.getMetaState()
            int r7 = r32.getFlags()
            int r0 = r32.getAction()
            if (r0 != 0) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            r10 = r0
            boolean r11 = r32.isCanceled()
            int r12 = r32.getDisplayId()
            boolean r0 = r1.mScreenshotChordEnabled
            r13 = 150(0x96, double:7.4E-322)
            r15 = 25
            r16 = -1
            if (r0 == 0) goto L_0x005a
            r0 = r7 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x005a
            boolean r0 = r1.mScreenshotChordVolumeDownKeyTriggered
            if (r0 == 0) goto L_0x004e
            boolean r0 = r1.mScreenshotChordPowerKeyTriggered
            if (r0 != 0) goto L_0x004e
            long r18 = android.os.SystemClock.uptimeMillis()
            long r8 = r1.mScreenshotChordVolumeDownKeyTime
            long r8 = r8 + r13
            int r0 = (r18 > r8 ? 1 : (r18 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x004e
            long r13 = r8 - r18
            return r13
        L_0x004e:
            if (r4 != r15) goto L_0x005a
            boolean r0 = r1.mScreenshotChordVolumeDownKeyConsumed
            if (r0 == 0) goto L_0x005a
            if (r10 != 0) goto L_0x0059
            r8 = 0
            r1.mScreenshotChordVolumeDownKeyConsumed = r8
        L_0x0059:
            return r16
        L_0x005a:
            com.android.internal.accessibility.AccessibilityShortcutController r0 = r1.mAccessibilityShortcutController
            r8 = 0
            boolean r0 = r0.isAccessibilityShortcutAvailable(r8)
            if (r0 == 0) goto L_0x009d
            r0 = r7 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x009d
            boolean r0 = r1.mScreenshotChordVolumeDownKeyTriggered
            boolean r9 = r1.mA11yShortcutChordVolumeUpKeyTriggered
            r0 = r0 ^ r9
            if (r0 == 0) goto L_0x0083
            long r18 = android.os.SystemClock.uptimeMillis()
            boolean r0 = r1.mScreenshotChordVolumeDownKeyTriggered
            if (r0 == 0) goto L_0x0079
            long r8 = r1.mScreenshotChordVolumeDownKeyTime
            goto L_0x007b
        L_0x0079:
            long r8 = r1.mA11yShortcutChordVolumeUpKeyTime
        L_0x007b:
            long r8 = r8 + r13
            int r21 = (r18 > r8 ? 1 : (r18 == r8 ? 0 : -1))
            if (r21 >= 0) goto L_0x0083
            long r13 = r8 - r18
            return r13
        L_0x0083:
            if (r4 != r15) goto L_0x008f
            boolean r8 = r1.mScreenshotChordVolumeDownKeyConsumed
            if (r8 == 0) goto L_0x008f
            if (r10 != 0) goto L_0x008e
            r8 = 0
            r1.mScreenshotChordVolumeDownKeyConsumed = r8
        L_0x008e:
            return r16
        L_0x008f:
            r0 = 24
            if (r4 != r0) goto L_0x009d
            boolean r8 = r1.mA11yShortcutChordVolumeUpKeyConsumed
            if (r8 == 0) goto L_0x009d
            if (r10 != 0) goto L_0x009c
            r8 = 0
            r1.mA11yShortcutChordVolumeUpKeyConsumed = r8
        L_0x009c:
            return r16
        L_0x009d:
            int r8 = r1.mRingerToggleChord
            if (r8 == 0) goto L_0x00d1
            r8 = r7 & 1024(0x400, float:1.435E-42)
            if (r8 != 0) goto L_0x00d1
            boolean r8 = r1.mA11yShortcutChordVolumeUpKeyTriggered
            if (r8 == 0) goto L_0x00bf
            boolean r8 = r1.mScreenshotChordPowerKeyTriggered
            if (r8 != 0) goto L_0x00bf
            long r8 = android.os.SystemClock.uptimeMillis()
            r18 = r6
            r19 = r7
            long r6 = r1.mA11yShortcutChordVolumeUpKeyTime
            long r6 = r6 + r13
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 >= 0) goto L_0x00c3
            long r13 = r6 - r8
            return r13
        L_0x00bf:
            r18 = r6
            r19 = r7
        L_0x00c3:
            r0 = 24
            if (r4 != r0) goto L_0x00d5
            boolean r6 = r1.mA11yShortcutChordVolumeUpKeyConsumed
            if (r6 == 0) goto L_0x00d5
            if (r10 != 0) goto L_0x00d0
            r6 = 0
            r1.mA11yShortcutChordVolumeUpKeyConsumed = r6
        L_0x00d0:
            return r16
        L_0x00d1:
            r18 = r6
            r19 = r7
        L_0x00d5:
            boolean r6 = r1.mPendingMetaAction
            if (r6 == 0) goto L_0x00e2
            boolean r6 = android.view.KeyEvent.isMetaKey(r4)
            if (r6 != 0) goto L_0x00e2
            r6 = 0
            r1.mPendingMetaAction = r6
        L_0x00e2:
            boolean r6 = r1.mPendingCapsLockToggle
            if (r6 == 0) goto L_0x00f5
            boolean r6 = android.view.KeyEvent.isMetaKey(r4)
            if (r6 != 0) goto L_0x00f5
            boolean r6 = android.view.KeyEvent.isAltKey(r4)
            if (r6 != 0) goto L_0x00f5
            r6 = 0
            r1.mPendingCapsLockToggle = r6
        L_0x00f5:
            r6 = 3
            if (r4 != r6) goto L_0x0115
            android.util.SparseArray<com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler> r0 = r1.mDisplayHomeButtonHandlers
            java.lang.Object r0 = r0.get(r12)
            com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler r0 = (com.android.server.policy.PhoneWindowManager.DisplayHomeButtonHandler) r0
            if (r0 != 0) goto L_0x010d
            com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler r6 = new com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler
            r6.<init>(r12)
            r0 = r6
            android.util.SparseArray<com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler> r6 = r1.mDisplayHomeButtonHandlers
            r6.put(r12, r0)
        L_0x010d:
            r6 = r31
            int r7 = r0.handleHomeButton(r6, r2)
            long r7 = (long) r7
            return r7
        L_0x0115:
            r6 = r31
            r7 = 82
            r8 = 2
            r13 = 0
            java.lang.String r9 = "WindowManager"
            if (r4 != r7) goto L_0x014f
            r0 = 1
            if (r10 == 0) goto L_0x014d
            if (r5 != 0) goto L_0x014d
            boolean r7 = r1.mEnableShiftMenuBugReports
            if (r7 == 0) goto L_0x014d
            r7 = r18 & 1
            r15 = 1
            if (r7 != r15) goto L_0x014d
            android.content.Intent r7 = new android.content.Intent
            java.lang.String r8 = "android.intent.action.BUG_REPORT"
            r7.<init>(r8)
            r22 = r7
            android.content.Context r7 = r1.mContext
            android.os.UserHandle r23 = android.os.UserHandle.CURRENT
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r21 = r7
            r21.sendOrderedBroadcastAsUser(r22, r23, r24, r25, r26, r27, r28, r29)
            return r16
        L_0x014d:
            goto L_0x0274
        L_0x014f:
            r7 = 84
            if (r4 != r7) goto L_0x0169
            if (r10 == 0) goto L_0x015e
            if (r5 != 0) goto L_0x0168
            r7 = 1
            r1.mSearchKeyShortcutPending = r7
            r7 = 0
            r1.mConsumeSearchKeyUp = r7
            goto L_0x0168
        L_0x015e:
            r7 = 0
            r1.mSearchKeyShortcutPending = r7
            boolean r0 = r1.mConsumeSearchKeyUp
            if (r0 == 0) goto L_0x0168
            r1.mConsumeSearchKeyUp = r7
            return r16
        L_0x0168:
            return r13
        L_0x0169:
            r7 = 187(0xbb, float:2.62E-43)
            if (r4 != r7) goto L_0x017d
            if (r3 != 0) goto L_0x017c
            if (r10 == 0) goto L_0x0177
            if (r5 != 0) goto L_0x0177
            r30.preloadRecentApps()
            goto L_0x017c
        L_0x0177:
            if (r10 != 0) goto L_0x017c
            r30.toggleRecentApps()
        L_0x017c:
            return r16
        L_0x017d:
            r7 = 42
            if (r4 != r7) goto L_0x0196
            boolean r7 = r32.isMetaPressed()
            if (r7 == 0) goto L_0x0196
            if (r10 == 0) goto L_0x0274
            com.android.internal.statusbar.IStatusBarService r7 = r30.getStatusBarService()
            if (r7 == 0) goto L_0x0194
            r7.expandNotificationsPanel()     // Catch:{ RemoteException -> 0x0193 }
            goto L_0x0194
        L_0x0193:
            r0 = move-exception
        L_0x0194:
            goto L_0x0274
        L_0x0196:
            r7 = 47
            if (r4 != r7) goto L_0x01c4
            boolean r7 = r32.isMetaPressed()
            if (r7 == 0) goto L_0x01c4
            boolean r7 = r32.isCtrlPressed()
            if (r7 == 0) goto L_0x01c4
            if (r10 == 0) goto L_0x0274
            if (r5 != 0) goto L_0x0274
            boolean r0 = r32.isShiftPressed()
            if (r0 == 0) goto L_0x01b3
            r20 = r8
            goto L_0x01b5
        L_0x01b3:
            r20 = 1
        L_0x01b5:
            r0 = r20
            com.android.server.policy.PhoneWindowManager$ScreenshotRunnable r7 = r1.mScreenshotRunnable
            r7.setScreenshotType(r0)
            android.os.Handler r7 = r1.mHandler
            com.android.server.policy.PhoneWindowManager$ScreenshotRunnable r8 = r1.mScreenshotRunnable
            r7.post(r8)
            return r16
        L_0x01c4:
            r7 = 76
            if (r4 != r7) goto L_0x01e1
            boolean r7 = r32.isMetaPressed()
            if (r7 == 0) goto L_0x01e1
            if (r10 == 0) goto L_0x0274
            if (r5 != 0) goto L_0x0274
            boolean r0 = r30.isKeyguardLocked()
            if (r0 != 0) goto L_0x0274
            int r0 = r32.getDeviceId()
            r1.toggleKeyboardShortcutsMenu(r0)
            goto L_0x0274
        L_0x01e1:
            r7 = 219(0xdb, float:3.07E-43)
            if (r4 != r7) goto L_0x01eb
            java.lang.String r0 = "KEYCODE_ASSIST should be handled in interceptKeyBeforeQueueing"
            android.util.Slog.wtf(r9, r0)
            return r16
        L_0x01eb:
            r7 = 231(0xe7, float:3.24E-43)
            if (r4 != r7) goto L_0x01f5
            java.lang.String r0 = "KEYCODE_VOICE_ASSIST should be handled in interceptKeyBeforeQueueing"
            android.util.Slog.wtf(r9, r0)
            return r16
        L_0x01f5:
            r7 = 120(0x78, float:1.68E-43)
            if (r4 != r7) goto L_0x020b
            if (r10 == 0) goto L_0x020a
            if (r5 != 0) goto L_0x020a
            com.android.server.policy.PhoneWindowManager$ScreenshotRunnable r0 = r1.mScreenshotRunnable
            r7 = 1
            r0.setScreenshotType(r7)
            android.os.Handler r0 = r1.mHandler
            com.android.server.policy.PhoneWindowManager$ScreenshotRunnable r7 = r1.mScreenshotRunnable
            r0.post(r7)
        L_0x020a:
            return r16
        L_0x020b:
            r7 = 221(0xdd, float:3.1E-43)
            if (r4 == r7) goto L_0x04a0
            r0 = 220(0xdc, float:3.08E-43)
            if (r4 != r0) goto L_0x0217
            r13 = r18
            goto L_0x04a2
        L_0x0217:
            r0 = 24
            if (r4 == r0) goto L_0x0253
            if (r4 == r15) goto L_0x0253
            r0 = 164(0xa4, float:2.3E-43)
            if (r4 != r0) goto L_0x0222
            goto L_0x0253
        L_0x0222:
            r0 = 61
            if (r4 != r0) goto L_0x022d
            boolean r0 = r32.isMetaPressed()
            if (r0 == 0) goto L_0x022d
            return r13
        L_0x022d:
            boolean r0 = r1.mHasFeatureLeanback
            if (r0 == 0) goto L_0x0238
            boolean r0 = r1.interceptBugreportGestureTv(r4, r10)
            if (r0 == 0) goto L_0x0238
            return r16
        L_0x0238:
            r0 = 284(0x11c, float:3.98E-43)
            if (r4 != r0) goto L_0x0274
            if (r10 != 0) goto L_0x0252
            android.os.Handler r0 = r1.mHandler
            r7 = 22
            r0.removeMessages(r7)
            android.os.Handler r0 = r1.mHandler
            android.os.Message r0 = r0.obtainMessage(r7)
            r7 = 1
            r0.setAsynchronous(r7)
            r0.sendToTarget()
        L_0x0252:
            return r16
        L_0x0253:
            boolean r0 = r1.mUseTvRouting
            if (r0 != 0) goto L_0x049a
            boolean r0 = r1.mHandleVolumeKeysInWM
            if (r0 == 0) goto L_0x025f
            r13 = r18
            goto L_0x049c
        L_0x025f:
            com.android.server.wm.DisplayPolicy r0 = r1.mDefaultDisplayPolicy
            boolean r0 = r0.isPersistentVrModeEnabled()
            if (r0 == 0) goto L_0x0274
            android.view.InputDevice r0 = r32.getDevice()
            if (r0 == 0) goto L_0x0274
            boolean r7 = r0.isExternal()
            if (r7 != 0) goto L_0x0274
            return r16
        L_0x0274:
            r0 = 0
            boolean r7 = android.view.KeyEvent.isModifierKey(r4)
            if (r7 == 0) goto L_0x02b0
            boolean r7 = r1.mPendingCapsLockToggle
            if (r7 != 0) goto L_0x0287
            int r7 = r1.mMetaState
            r1.mInitialMetaState = r7
            r7 = 1
            r1.mPendingCapsLockToggle = r7
            goto L_0x02b0
        L_0x0287:
            r7 = 1
            int r15 = r32.getAction()
            if (r15 != r7) goto L_0x02b0
            int r7 = r1.mMetaState
            r15 = r7 & 50
            r21 = 458752(0x70000, float:6.42848E-40)
            r21 = r7 & r21
            if (r21 == 0) goto L_0x02ab
            if (r15 == 0) goto L_0x02ab
            int r13 = r1.mInitialMetaState
            r14 = r15 | r21
            r7 = r7 ^ r14
            if (r13 != r7) goto L_0x02ab
            android.hardware.input.InputManagerInternal r7 = r1.mInputManagerInternal
            int r13 = r32.getDeviceId()
            r7.toggleCapsLock(r13)
            r0 = 1
        L_0x02ab:
            r7 = 0
            r1.mPendingCapsLockToggle = r7
            r7 = r0
            goto L_0x02b1
        L_0x02b0:
            r7 = r0
        L_0x02b1:
            r13 = r18
            r1.mMetaState = r13
            if (r7 == 0) goto L_0x02b8
            return r16
        L_0x02b8:
            boolean r0 = android.view.KeyEvent.isMetaKey(r4)
            if (r0 == 0) goto L_0x02d2
            if (r10 == 0) goto L_0x02c4
            r8 = 1
            r1.mPendingMetaAction = r8
            goto L_0x02d1
        L_0x02c4:
            boolean r0 = r1.mPendingMetaAction
            if (r0 == 0) goto L_0x02d1
            int r0 = r32.getDeviceId()
            java.lang.String r8 = "android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD"
            r1.launchAssistAction(r8, r0)
        L_0x02d1:
            return r16
        L_0x02d2:
            boolean r0 = r1.mSearchKeyShortcutPending
            r14 = 268435456(0x10000000, float:2.5243549E-29)
            if (r0 == 0) goto L_0x0335
            android.view.KeyCharacterMap r15 = r32.getKeyCharacterMap()
            boolean r0 = r15.isPrintingKey(r4)
            if (r0 == 0) goto L_0x0335
            r8 = 1
            r1.mConsumeSearchKeyUp = r8
            r8 = 0
            r1.mSearchKeyShortcutPending = r8
            if (r10 == 0) goto L_0x0334
            if (r5 != 0) goto L_0x0334
            if (r3 != 0) goto L_0x0334
            com.android.server.policy.ShortcutManager r0 = r1.mShortcutManager
            android.content.Intent r8 = r0.getIntent(r15, r4, r13)
            if (r8 == 0) goto L_0x031c
            r8.addFlags(r14)
            android.os.UserHandle r0 = android.os.UserHandle.CURRENT     // Catch:{ ActivityNotFoundException -> 0x0302 }
            r1.startActivityAsUser(r8, r0)     // Catch:{ ActivityNotFoundException -> 0x0302 }
            r30.dismissKeyboardShortcutsMenu()     // Catch:{ ActivityNotFoundException -> 0x0302 }
            goto L_0x031b
        L_0x0302:
            r0 = move-exception
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r6 = "Dropping shortcut key combination because the activity to which it is registered was not found: SEARCH+"
            r14.append(r6)
            java.lang.String r6 = android.view.KeyEvent.keyCodeToString(r4)
            r14.append(r6)
            java.lang.String r6 = r14.toString()
            android.util.Slog.w(r9, r6, r0)
        L_0x031b:
            goto L_0x0334
        L_0x031c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "Dropping unregistered shortcut key combination: SEARCH+"
            r0.append(r6)
            java.lang.String r6 = android.view.KeyEvent.keyCodeToString(r4)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            android.util.Slog.i(r9, r0)
        L_0x0334:
            return r16
        L_0x0335:
            r0 = 65536(0x10000, float:9.18355E-41)
            if (r10 == 0) goto L_0x037e
            if (r5 != 0) goto L_0x037e
            if (r3 != 0) goto L_0x037e
            r6 = r13 & r0
            if (r6 == 0) goto L_0x037e
            android.view.KeyCharacterMap r6 = r32.getKeyCharacterMap()
            boolean r15 = r6.isPrintingKey(r4)
            if (r15 == 0) goto L_0x037e
            com.android.server.policy.ShortcutManager r15 = r1.mShortcutManager
            r18 = -458753(0xfffffffffff8ffff, float:NaN)
            r0 = r13 & r18
            android.content.Intent r15 = r15.getIntent(r6, r4, r0)
            if (r15 == 0) goto L_0x037e
            r15.addFlags(r14)
            android.os.UserHandle r0 = android.os.UserHandle.CURRENT     // Catch:{ ActivityNotFoundException -> 0x0364 }
            r1.startActivityAsUser(r15, r0)     // Catch:{ ActivityNotFoundException -> 0x0364 }
            r30.dismissKeyboardShortcutsMenu()     // Catch:{ ActivityNotFoundException -> 0x0364 }
            goto L_0x037d
        L_0x0364:
            r0 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r14 = "Dropping shortcut key combination because the activity to which it is registered was not found: META+"
            r8.append(r14)
            java.lang.String r14 = android.view.KeyEvent.keyCodeToString(r4)
            r8.append(r14)
            java.lang.String r8 = r8.toString()
            android.util.Slog.w(r9, r8, r0)
        L_0x037d:
            return r16
        L_0x037e:
            if (r10 == 0) goto L_0x03bf
            if (r5 != 0) goto L_0x03bf
            if (r3 != 0) goto L_0x03bf
            android.util.SparseArray<java.lang.String> r0 = sApplicationLaunchKeyCategories
            java.lang.Object r0 = r0.get(r4)
            r6 = r0
            java.lang.String r6 = (java.lang.String) r6
            if (r6 == 0) goto L_0x03bf
            java.lang.String r0 = "android.intent.action.MAIN"
            android.content.Intent r8 = android.content.Intent.makeMainSelectorActivity(r0, r6)
            r8.setFlags(r14)
            android.os.UserHandle r0 = android.os.UserHandle.CURRENT     // Catch:{ ActivityNotFoundException -> 0x03a1 }
            r1.startActivityAsUser(r8, r0)     // Catch:{ ActivityNotFoundException -> 0x03a1 }
            r30.dismissKeyboardShortcutsMenu()     // Catch:{ ActivityNotFoundException -> 0x03a1 }
            goto L_0x03be
        L_0x03a1:
            r0 = move-exception
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "Dropping application launch key because the activity to which it is registered was not found: keyCode="
            r14.append(r15)
            r14.append(r4)
            java.lang.String r15 = ", category="
            r14.append(r15)
            r14.append(r6)
            java.lang.String r14 = r14.toString()
            android.util.Slog.w(r9, r14, r0)
        L_0x03be:
            return r16
        L_0x03bf:
            if (r10 == 0) goto L_0x03e7
            if (r5 != 0) goto L_0x03e7
            r0 = 61
            if (r4 != r0) goto L_0x03e7
            int r0 = r1.mRecentAppsHeldModifiers
            if (r0 != 0) goto L_0x03f7
            if (r3 != 0) goto L_0x03f7
            boolean r0 = r30.isUserSetupComplete()
            if (r0 == 0) goto L_0x03f7
            int r0 = r32.getModifiers()
            r0 = r0 & -194(0xffffffffffffff3e, float:NaN)
            boolean r6 = android.view.KeyEvent.metaStateHasModifiers(r0, r8)
            if (r6 == 0) goto L_0x03e6
            r1.mRecentAppsHeldModifiers = r0
            r6 = 1
            r1.showRecentApps(r6)
            return r16
        L_0x03e6:
            goto L_0x03f7
        L_0x03e7:
            if (r10 != 0) goto L_0x03f7
            int r0 = r1.mRecentAppsHeldModifiers
            if (r0 == 0) goto L_0x03f7
            r0 = r0 & r13
            if (r0 != 0) goto L_0x03f7
            r6 = 0
            r1.mRecentAppsHeldModifiers = r6
            r8 = 1
            r1.hideRecentApps(r8, r6)
        L_0x03f7:
            r0 = 62
            if (r4 != r0) goto L_0x0403
            r6 = 487424(0x77000, float:6.83027E-40)
            r6 = r6 & r13
            if (r6 == 0) goto L_0x0403
            r6 = 1
            goto L_0x0404
        L_0x0403:
            r6 = 0
        L_0x0404:
            if (r10 == 0) goto L_0x0424
            if (r5 != 0) goto L_0x0424
            r8 = 204(0xcc, float:2.86E-43)
            if (r4 == r8) goto L_0x040e
            if (r6 == 0) goto L_0x0424
        L_0x040e:
            r0 = r13 & 193(0xc1, float:2.7E-43)
            if (r0 == 0) goto L_0x0416
            r8 = -1
            r20 = r8
            goto L_0x0418
        L_0x0416:
            r20 = 1
        L_0x0418:
            r0 = r20
            com.android.server.policy.WindowManagerPolicy$WindowManagerFuncs r8 = r1.mWindowManagerFuncs
            int r9 = r32.getDeviceId()
            r8.switchKeyboardLayout(r9, r0)
            return r16
        L_0x0424:
            boolean r8 = r1.mLanguageSwitchKeyPressed
            if (r8 == 0) goto L_0x0434
            if (r10 != 0) goto L_0x0434
            r8 = 204(0xcc, float:2.86E-43)
            if (r4 == r8) goto L_0x0430
            if (r4 != r0) goto L_0x0434
        L_0x0430:
            r8 = 0
            r1.mLanguageSwitchKeyPressed = r8
            return r16
        L_0x0434:
            boolean r0 = isValidGlobalKey(r4)
            if (r0 == 0) goto L_0x0445
            com.android.server.policy.GlobalKeyManager r0 = r1.mGlobalKeyManager
            android.content.Context r8 = r1.mContext
            boolean r0 = r0.handleGlobalKey(r8, r4, r2)
            if (r0 == 0) goto L_0x0445
            return r16
        L_0x0445:
            if (r10 == 0) goto L_0x0491
            long r8 = (long) r4
            boolean r0 = r32.isCtrlPressed()
            if (r0 == 0) goto L_0x0454
            r14 = 17592186044416(0x100000000000, double:8.6916947597938E-311)
            long r8 = r8 | r14
        L_0x0454:
            boolean r0 = r32.isAltPressed()
            if (r0 == 0) goto L_0x0460
            r14 = 8589934592(0x200000000, double:4.243991582E-314)
            long r8 = r8 | r14
        L_0x0460:
            boolean r0 = r32.isShiftPressed()
            if (r0 == 0) goto L_0x046c
            r14 = 4294967296(0x100000000, double:2.121995791E-314)
            long r8 = r8 | r14
        L_0x046c:
            boolean r0 = r32.isMetaPressed()
            if (r0 == 0) goto L_0x0475
            r14 = 281474976710656(0x1000000000000, double:1.390671161567E-309)
            long r8 = r8 | r14
        L_0x0475:
            android.util.LongSparseArray<com.android.internal.policy.IShortcutService> r0 = r1.mShortcutKeyServices
            java.lang.Object r0 = r0.get(r8)
            r14 = r0
            com.android.internal.policy.IShortcutService r14 = (com.android.internal.policy.IShortcutService) r14
            if (r14 == 0) goto L_0x0491
            boolean r0 = r30.isUserSetupComplete()     // Catch:{ RemoteException -> 0x048a }
            if (r0 == 0) goto L_0x0489
            r14.notifyShortcutKeyPressed(r8)     // Catch:{ RemoteException -> 0x048a }
        L_0x0489:
            goto L_0x0490
        L_0x048a:
            r0 = move-exception
            android.util.LongSparseArray<com.android.internal.policy.IShortcutService> r15 = r1.mShortcutKeyServices
            r15.delete(r8)
        L_0x0490:
            return r16
        L_0x0491:
            r0 = 65536(0x10000, float:9.18355E-41)
            r0 = r0 & r13
            if (r0 == 0) goto L_0x0497
            return r16
        L_0x0497:
            r8 = 0
            return r8
        L_0x049a:
            r13 = r18
        L_0x049c:
            r1.dispatchDirectAudioEvent(r2)
            return r16
        L_0x04a0:
            r13 = r18
        L_0x04a2:
            if (r10 == 0) goto L_0x0513
            if (r4 != r7) goto L_0x04a8
            r8 = 1
            goto L_0x04a9
        L_0x04a8:
            r8 = -1
        L_0x04a9:
            r0 = r8
            android.content.Context r6 = r1.mContext
            android.content.ContentResolver r6 = r6.getContentResolver()
            r7 = -3
            java.lang.String r8 = "screen_brightness_mode"
            r9 = 0
            int r6 = android.provider.Settings.System.getIntForUser(r6, r8, r9, r7)
            if (r6 == 0) goto L_0x04c7
            android.content.Context r8 = r1.mContext
            android.content.ContentResolver r8 = r8.getContentResolver()
            java.lang.String r14 = "screen_brightness_mode"
            android.provider.Settings.System.putIntForUser(r8, r14, r9, r7)
        L_0x04c7:
            android.os.PowerManager r8 = r1.mPowerManager
            int r8 = r8.getMinimumScreenBrightnessSetting()
            android.os.PowerManager r9 = r1.mPowerManager
            int r9 = r9.getMaximumScreenBrightnessSetting()
            int r14 = r9 - r8
            int r14 = r14 + 10
            r15 = 1
            int r14 = r14 - r15
            int r14 = r14 / 10
            int r14 = r14 * r0
            android.content.Context r15 = r1.mContext
            android.content.ContentResolver r15 = r15.getContentResolver()
            android.os.PowerManager r7 = r1.mPowerManager
            int r7 = r7.getDefaultScreenBrightnessSetting()
            r20 = r0
            java.lang.String r0 = "screen_brightness"
            r2 = -3
            int r0 = android.provider.Settings.System.getIntForUser(r15, r0, r7, r2)
            int r0 = r0 + r14
            int r0 = java.lang.Math.min(r9, r0)
            int r0 = java.lang.Math.max(r8, r0)
            android.content.Context r7 = r1.mContext
            android.content.ContentResolver r7 = r7.getContentResolver()
            java.lang.String r15 = "screen_brightness"
            android.provider.Settings.System.putIntForUser(r7, r15, r0, r2)
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r7 = "com.android.intent.action.SHOW_BRIGHTNESS_DIALOG"
            r2.<init>(r7)
            android.os.UserHandle r7 = android.os.UserHandle.CURRENT_OR_SELF
            r1.startActivityAsUser(r2, r7)
        L_0x0513:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.PhoneWindowManager.interceptKeyBeforeDispatchingInner(com.android.server.policy.WindowManagerPolicy$WindowState, android.view.KeyEvent, int):long");
    }

    private boolean interceptBugreportGestureTv(int keyCode, boolean down) {
        if (keyCode == 23) {
            this.mBugreportTvKey1Pressed = down;
        } else if (keyCode == 4) {
            this.mBugreportTvKey2Pressed = down;
        }
        if (!this.mBugreportTvKey1Pressed || !this.mBugreportTvKey2Pressed) {
            if (this.mBugreportTvScheduled) {
                this.mHandler.removeMessages(18);
                this.mBugreportTvScheduled = false;
            }
        } else if (!this.mBugreportTvScheduled) {
            this.mBugreportTvScheduled = true;
            Message msg = Message.obtain(this.mHandler, 18);
            msg.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(msg, 1000);
        }
        return this.mBugreportTvScheduled;
    }

    private boolean interceptAccessibilityGestureTv(int keyCode, boolean down) {
        if (keyCode == 4) {
            this.mAccessibilityTvKey1Pressed = down;
        } else if (keyCode == 20) {
            this.mAccessibilityTvKey2Pressed = down;
        }
        if (!this.mAccessibilityTvKey1Pressed || !this.mAccessibilityTvKey2Pressed) {
            if (this.mAccessibilityTvScheduled) {
                this.mHandler.removeMessages(19);
                this.mAccessibilityTvScheduled = false;
            }
        } else if (!this.mAccessibilityTvScheduled) {
            this.mAccessibilityTvScheduled = true;
            Message msg = Message.obtain(this.mHandler, 19);
            msg.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(msg, getAccessibilityShortcutTimeout());
        }
        return this.mAccessibilityTvScheduled;
    }

    /* access modifiers changed from: private */
    public void requestFullBugreport() {
        if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.debuggable")) || Settings.Global.getInt(this.mContext.getContentResolver(), "development_settings_enabled", 0) == 1) {
            try {
                ActivityManager.getService().requestBugReport(0);
            } catch (RemoteException e) {
                Slog.e("WindowManager", "Error taking bugreport", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void processFunctionKeyOnGamePad(int keycode) {
        Intent intent = new Intent("com.blackshark.gamecontroller.KEY_MAP_VIEW");
        intent.setPackage("com.blackshark.gamecontroller");
        intent.putExtra("keycode", keycode);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private boolean handleFunctionKeyOnGamePad(KeyEvent event) {
        if (event.getDevice().getVendorId() != 65504 || event.getKeyCode() != 107) {
            return false;
        }
        if (event.getAction() == 1) {
            Message msg = this.mHandler.obtainMessage(31);
            msg.setAsynchronous(true);
            msg.arg1 = event.getKeyCode();
            this.mHandler.sendMessage(msg);
        }
        return true;
    }

    public KeyEvent dispatchUnhandledKey(WindowManagerPolicy.WindowState win, KeyEvent event, int policyFlags) {
        KeyCharacterMap.FallbackAction fallbackAction;
        KeyEvent fallbackEvent = null;
        if ((event.getFlags() & 1024) == 0) {
            KeyCharacterMap kcm = event.getKeyCharacterMap();
            int keyCode = event.getKeyCode();
            int metaState = event.getMetaState();
            boolean initialDown = event.getAction() == 0 && event.getRepeatCount() == 0;
            if (initialDown) {
                fallbackAction = kcm.getFallbackAction(keyCode, metaState);
            } else {
                fallbackAction = this.mFallbackActions.get(keyCode);
            }
            if (fallbackAction != null) {
                fallbackEvent = KeyEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), fallbackAction.keyCode, event.getRepeatCount(), fallbackAction.metaState, event.getDeviceId(), event.getScanCode(), event.getFlags() | 1024, event.getSource(), event.getDisplayId(), (String) null);
                if (!interceptFallback(win, fallbackEvent, policyFlags)) {
                    fallbackEvent.recycle();
                    fallbackEvent = null;
                }
                if (initialDown) {
                    this.mFallbackActions.put(keyCode, fallbackAction);
                } else if (event.getAction() == 1) {
                    this.mFallbackActions.remove(keyCode);
                    fallbackAction.recycle();
                }
            } else {
                WindowManagerPolicy.WindowState windowState = win;
                int i = policyFlags;
            }
        } else {
            WindowManagerPolicy.WindowState windowState2 = win;
            int i2 = policyFlags;
        }
        return fallbackEvent;
    }

    private boolean interceptFallback(WindowManagerPolicy.WindowState win, KeyEvent fallbackEvent, int policyFlags) {
        if ((interceptKeyBeforeQueueing(fallbackEvent, policyFlags) & 1) == 0 || interceptKeyBeforeDispatching(win, fallbackEvent, policyFlags) != 0) {
            return false;
        }
        return true;
    }

    public void setTopFocusedDisplay(int displayId) {
        this.mTopFocusedDisplayId = displayId;
    }

    public void registerDisplayFoldListener(IDisplayFoldListener listener) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.registerDisplayFoldListener(listener);
        }
    }

    public void unregisterDisplayFoldListener(IDisplayFoldListener listener) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.unregisterDisplayFoldListener(listener);
        }
    }

    public void setOverrideFoldedArea(Rect area) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.setOverrideFoldedArea(area);
        }
    }

    public Rect getFoldedArea() {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            return displayFoldController.getFoldedArea();
        }
        return new Rect();
    }

    public void onDefaultDisplayFocusChangedLw(WindowManagerPolicy.WindowState newFocus) {
        this.mFocusedWindow = newFocus;
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.onDefaultDisplayFocusChanged(newFocus != null ? newFocus.getOwningPackage() : null);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void registerShortcutKey(long shortcutCode, IShortcutService shortcutService) throws RemoteException {
        synchronized (this.mLock) {
            IShortcutService service = this.mShortcutKeyServices.get(shortcutCode);
            if (service != null) {
                if (service.asBinder().pingBinder()) {
                    throw new RemoteException("Key already exists.");
                }
            }
            this.mShortcutKeyServices.put(shortcutCode, shortcutService);
        }
    }

    public void onKeyguardOccludedChangedLw(boolean occluded) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null || !keyguardServiceDelegate.isShowing()) {
            setKeyguardVisibilityState(occluded, false);
            return;
        }
        this.mPendingKeyguardOccluded = occluded;
        this.mKeyguardOccludedChanged = true;
    }

    /* access modifiers changed from: private */
    public int handleStartTransitionForKeyguardLw(int transit, long duration) {
        if (this.mKeyguardOccludedChanged) {
            this.mKeyguardOccludedChanged = false;
            if (setKeyguardVisibilityState(this.mPendingKeyguardOccluded, false)) {
                return 5;
            }
        }
        if (AppTransition.isKeyguardGoingAwayTransit(transit)) {
            startKeyguardExitAnimation(SystemClock.uptimeMillis(), duration);
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void launchAssistLongPressAction() {
        performHapticFeedback(0, false, "Assist - Long Press");
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_ASSIST);
        Intent intent = new Intent("android.intent.action.SEARCH_LONG_PRESS");
        intent.setFlags(268435456);
        try {
            SearchManager searchManager = getSearchManager();
            if (searchManager != null) {
                searchManager.stopSearch();
            }
            startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (ActivityNotFoundException e) {
            Slog.w("WindowManager", "No activity to handle assist long press action.", e);
        }
    }

    /* access modifiers changed from: private */
    public void launchAssistAction(String hint, int deviceId) {
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_ASSIST);
        if (isUserSetupComplete()) {
            Bundle args = null;
            if (deviceId > Integer.MIN_VALUE) {
                args = new Bundle();
                args.putInt("android.intent.extra.ASSIST_INPUT_DEVICE_ID", deviceId);
            }
            if ((this.mContext.getResources().getConfiguration().uiMode & 15) == 4) {
                ((SearchManager) this.mContext.getSystemService("search")).launchLegacyAssist(hint, UserHandle.myUserId(), args);
                return;
            }
            if (hint != null) {
                if (args == null) {
                    args = new Bundle();
                }
                args.putBoolean(hint, true);
            }
            StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
            if (statusbar != null) {
                statusbar.startAssist(args);
            }
        }
    }

    private void launchVoiceAssist(boolean allowDuringSetup) {
        boolean keyguardActive;
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            keyguardActive = false;
        } else {
            keyguardActive = keyguardServiceDelegate.isShowing();
        }
        if (!keyguardActive) {
            startActivityAsUser(new Intent("android.intent.action.VOICE_ASSIST"), (Bundle) null, UserHandle.CURRENT_OR_SELF, allowDuringSetup);
        }
    }

    private void startActivityAsUser(Intent intent, UserHandle handle) {
        startActivityAsUser(intent, (Bundle) null, handle);
    }

    private void startActivityAsUser(Intent intent, Bundle bundle, UserHandle handle) {
        startActivityAsUser(intent, bundle, handle, false);
    }

    private void startActivityAsUser(Intent intent, Bundle bundle, UserHandle handle, boolean allowDuringSetup) {
        if (allowDuringSetup || isUserSetupComplete()) {
            this.mContext.startActivityAsUser(intent, bundle, handle);
            return;
        }
        Slog.i("WindowManager", "Not starting activity because user setup is in progress: " + intent);
    }

    private SearchManager getSearchManager() {
        if (this.mSearchManager == null) {
            this.mSearchManager = (SearchManager) this.mContext.getSystemService("search");
        }
        return this.mSearchManager;
    }

    /* access modifiers changed from: private */
    public void preloadRecentApps() {
        this.mPreloadedRecentApps = true;
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.preloadRecentApps();
        }
    }

    /* access modifiers changed from: private */
    public void cancelPreloadRecentApps() {
        if (this.mPreloadedRecentApps) {
            this.mPreloadedRecentApps = false;
            StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
            if (statusbar != null) {
                statusbar.cancelPreloadRecentApps();
            }
        }
    }

    /* access modifiers changed from: private */
    public void toggleRecentApps() {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.toggleRecentApps();
        }
    }

    public void showRecentApps() {
        this.mHandler.removeMessages(9);
        this.mHandler.obtainMessage(9).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void showRecentApps(boolean triggeredFromAltTab) {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.showRecentApps(triggeredFromAltTab);
        }
    }

    private void toggleKeyboardShortcutsMenu(int deviceId) {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.toggleKeyboardShortcutsMenu(deviceId);
        }
    }

    private void dismissKeyboardShortcutsMenu() {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.dismissKeyboardShortcutsMenu();
        }
    }

    private void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHome) {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.hideRecentApps(triggeredFromAltTab, triggeredFromHome);
        }
    }

    /* access modifiers changed from: package-private */
    public void launchHomeFromHotKey(int displayId) {
        launchHomeFromHotKey(displayId, true, true);
    }

    /* access modifiers changed from: package-private */
    public void launchHomeFromHotKey(final int displayId, final boolean awakenFromDreams, boolean respectKeyguard) {
        if (respectKeyguard) {
            if (!isKeyguardShowingAndNotOccluded()) {
                if (!this.mKeyguardOccluded && this.mKeyguardDelegate.isInputRestricted()) {
                    this.mKeyguardDelegate.verifyUnlock(new WindowManagerPolicy.OnKeyguardExitResult() {
                        public void onKeyguardExitResult(boolean success) {
                            if (success) {
                                PhoneWindowManager.this.startDockOrHome(displayId, true, awakenFromDreams);
                            }
                        }
                    });
                    return;
                }
            } else {
                return;
            }
        }
        if (this.mRecentsVisible) {
            try {
                ActivityManager.getService().stopAppSwitches();
            } catch (RemoteException e) {
            }
            if (awakenFromDreams) {
                awakenDreams();
            }
            hideRecentApps(false, true);
            return;
        }
        startDockOrHome(displayId, true, awakenFromDreams);
    }

    public void setRecentsVisibilityLw(boolean visible) {
        this.mRecentsVisible = visible;
    }

    public void setPipVisibilityLw(boolean visible) {
        this.mPictureInPictureVisible = visible;
    }

    public void setNavBarVirtualKeyHapticFeedbackEnabledLw(boolean enabled) {
        this.mNavBarVirtualKeyHapticFeedbackEnabled = enabled;
    }

    public void applyKeyguardPolicyLw(WindowManagerPolicy.WindowState win, WindowManagerPolicy.WindowState imeTarget) {
        if (!canBeHiddenByKeyguardLw(win)) {
            return;
        }
        if (shouldBeHiddenByKeyguard(win, imeTarget)) {
            win.hideLw(false);
            return;
        }
        if (!win.isVisibleLw() && win.isHoldOn()) {
            win.setHoldOn(false);
        }
        win.showLw(false);
    }

    public void setKeyguardCandidateLw(WindowManagerPolicy.WindowState win) {
        this.mKeyguardCandidate = win;
        setKeyguardOccludedLw(this.mKeyguardOccluded, true);
    }

    private boolean setKeyguardOccludedLw(boolean isOccluded, boolean force) {
        boolean wasOccluded = this.mKeyguardOccluded;
        boolean showing = this.mKeyguardDelegate.isShowing();
        boolean changed = wasOccluded != isOccluded || force;
        if (!isOccluded && changed && showing) {
            this.mKeyguardOccluded = false;
            this.mKeyguardDelegate.setOccluded(false, true);
            WindowManagerPolicy.WindowState windowState = this.mKeyguardCandidate;
            if (windowState != null) {
                windowState.getAttrs().privateFlags |= 1024;
                if (!this.mKeyguardDelegate.hasLockscreenWallpaper()) {
                    this.mKeyguardCandidate.getAttrs().flags |= DumpState.DUMP_DEXOPT;
                }
            }
            return true;
        } else if (isOccluded && changed && showing) {
            this.mKeyguardOccluded = true;
            this.mKeyguardDelegate.setOccluded(true, false);
            WindowManagerPolicy.WindowState windowState2 = this.mKeyguardCandidate;
            if (windowState2 != null) {
                windowState2.getAttrs().privateFlags &= -1025;
                this.mKeyguardCandidate.getAttrs().flags &= -1048577;
            }
            return true;
        } else if (!changed) {
            return false;
        } else {
            this.mKeyguardOccluded = isOccluded;
            this.mKeyguardDelegate.setOccluded(isOccluded, false);
            return false;
        }
    }

    public void notifyLidSwitchChanged(long whenNanos, boolean lidOpen) {
        int newLidState = lidOpen;
        if (newLidState != this.mDefaultDisplayPolicy.getLidState()) {
            this.mDefaultDisplayPolicy.setLidState((int) newLidState);
            applyLidSwitchState();
            updateRotation(true);
            if (lidOpen) {
                wakeUp(SystemClock.uptimeMillis(), this.mAllowTheaterModeWakeFromLidSwitch, 9, "android.policy:LID");
            } else if (getLidBehavior() != 1) {
                this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
            }
        }
    }

    public void notifyCameraLensCoverSwitchChanged(long whenNanos, boolean lensCovered) {
        boolean keyguardActive;
        Intent intent;
        int lensCoverState = lensCovered;
        int i = this.mCameraLensCoverState;
        if (i != lensCoverState) {
            if (i == 1 && lensCoverState == 0) {
                KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                if (keyguardServiceDelegate == null) {
                    keyguardActive = false;
                } else {
                    keyguardActive = keyguardServiceDelegate.isShowing();
                }
                if (keyguardActive) {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE");
                } else {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
                }
                wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromCameraLens, 5, "android.policy:CAMERA_COVER");
                startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
            }
            this.mCameraLensCoverState = (int) lensCoverState;
        }
    }

    /* access modifiers changed from: package-private */
    public void initializeHdmiState() {
        int oldMask = StrictMode.allowThreadDiskReadsMask();
        try {
            initializeHdmiStateInternal();
        } finally {
            StrictMode.setThreadPolicyMask(oldMask);
        }
    }

    /* access modifiers changed from: package-private */
    public void initializeHdmiStateInternal() {
        boolean plugged = false;
        this.mExtEventObserver.startObserving("mdss_mdp/drm/card");
        if (new File("/sys/devices/virtual/switch/hdmi/state").exists()) {
            this.mHDMIObserver.startObserving("DEVPATH=/devices/virtual/switch/hdmi");
            FileReader reader = null;
            try {
                FileReader reader2 = new FileReader("/sys/class/switch/hdmi/state");
                char[] buf = new char[15];
                int n = reader2.read(buf);
                if (n > 1) {
                    boolean z = false;
                    if (Integer.parseInt(new String(buf, 0, n - 1)) != 0) {
                        z = true;
                    }
                    plugged = z;
                }
                try {
                    reader2.close();
                } catch (IOException e) {
                }
            } catch (IOException ex) {
                Slog.w("WindowManager", "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex);
                if (reader != null) {
                    reader.close();
                }
            } catch (NumberFormatException ex2) {
                Slog.w("WindowManager", "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex2);
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } else if (ExtconUEventObserver.extconExists() && ExtconUEventObserver.namedExtconDirExists("hdmi")) {
            HdmiVideoExtconUEventObserver observer = new HdmiVideoExtconUEventObserver();
            plugged = observer.init();
            this.mHDMIObserver = observer;
        }
        this.mDefaultDisplayPolicy.setHdmiPlugged(plugged, true);
    }

    public void notifyFpClientState(boolean start, boolean iskeyguard) {
        this.mWindowManagerFuncs.onFpClientStateChanged(start, iskeyguard);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x03d3  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x03dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int interceptKeyBeforeQueueing(android.view.KeyEvent r20, int r21) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            boolean r0 = r7.mSystemBooted
            r1 = 0
            if (r0 != 0) goto L_0x000a
            return r1
        L_0x000a:
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r21 & r0
            r2 = 1
            if (r0 == 0) goto L_0x0013
            r0 = r2
            goto L_0x0014
        L_0x0013:
            r0 = r1
        L_0x0014:
            r9 = r0
            int r0 = r20.getAction()
            if (r0 != 0) goto L_0x001d
            r0 = r2
            goto L_0x001e
        L_0x001d:
            r0 = r1
        L_0x001e:
            r10 = r0
            boolean r11 = r20.isCanceled()
            int r12 = r20.getKeyCode()
            int r13 = r20.getDisplayId()
            r0 = 16777216(0x1000000, float:2.3509887E-38)
            r0 = r21 & r0
            if (r0 == 0) goto L_0x0033
            r0 = r2
            goto L_0x0034
        L_0x0033:
            r0 = r1
        L_0x0034:
            r14 = r0
            com.android.server.policy.keyguard.KeyguardServiceDelegate r0 = r7.mKeyguardDelegate
            if (r0 != 0) goto L_0x003b
            r0 = r1
            goto L_0x0046
        L_0x003b:
            if (r9 == 0) goto L_0x0042
            boolean r0 = r19.isKeyguardShowingAndNotOccluded()
            goto L_0x0046
        L_0x0042:
            boolean r0 = r0.isShowing()
        L_0x0046:
            r15 = r0
            boolean r0 = r19.isMiuiRom()
            r3 = 23
            java.lang.String r4 = "WindowManager"
            if (r0 != 0) goto L_0x005e
            boolean r0 = r7.mScreenOnFully
            if (r0 == 0) goto L_0x005e
            if (r12 != r3) goto L_0x005e
            java.lang.String r0 = "ignore KEYCODE_DPAD_CENTER in native build!"
            android.util.Log.d(r4, r0)
            return r1
        L_0x005e:
            r0 = r21 & 1
            if (r0 != 0) goto L_0x006b
            boolean r0 = r20.isWakeKey()
            if (r0 == 0) goto L_0x0069
            goto L_0x006b
        L_0x0069:
            r0 = r1
            goto L_0x006c
        L_0x006b:
            r0 = r2
        L_0x006c:
            r5 = -1
            if (r9 != 0) goto L_0x009b
            if (r14 == 0) goto L_0x0074
            if (r0 != 0) goto L_0x0074
            goto L_0x009b
        L_0x0074:
            if (r9 != 0) goto L_0x0084
            boolean r6 = r7.shouldDispatchInputWhenNonInteractive(r13, r12)
            if (r6 == 0) goto L_0x0084
            r6 = 1
            r7.mPendingWakeKey = r5
            r16 = r0
            r17 = r6
            goto L_0x00b2
        L_0x0084:
            r6 = 0
            if (r0 == 0) goto L_0x0090
            if (r10 == 0) goto L_0x008f
            boolean r5 = r7.isWakeKeyWhenScreenOff(r12)
            if (r5 != 0) goto L_0x0090
        L_0x008f:
            r0 = 0
        L_0x0090:
            if (r0 == 0) goto L_0x0096
            if (r10 == 0) goto L_0x0096
            r7.mPendingWakeKey = r12
        L_0x0096:
            r16 = r0
            r17 = r6
            goto L_0x00b2
        L_0x009b:
            r6 = 1
            r0 = 0
            if (r9 == 0) goto L_0x00ae
            int r3 = r7.mPendingWakeKey
            if (r12 != r3) goto L_0x00a7
            if (r10 != 0) goto L_0x00a7
            r3 = 0
            r6 = r3
        L_0x00a7:
            r7.mPendingWakeKey = r5
            r16 = r0
            r17 = r6
            goto L_0x00b2
        L_0x00ae:
            r16 = r0
            r17 = r6
        L_0x00b2:
            boolean r0 = isValidGlobalKey(r12)
            if (r0 == 0) goto L_0x00d1
            com.android.server.policy.GlobalKeyManager r0 = r7.mGlobalKeyManager
            boolean r0 = r0.shouldHandleGlobalKey(r12, r8)
            if (r0 == 0) goto L_0x00d1
            if (r16 == 0) goto L_0x00d0
            long r2 = r20.getEventTime()
            boolean r4 = r7.mAllowTheaterModeWakeFromKey
            r5 = 6
            java.lang.String r6 = "android.policy:KEY"
            r1 = r19
            r1.wakeUp(r2, r4, r5, r6)
        L_0x00d0:
            return r17
        L_0x00d1:
            int r0 = r20.getFlags()
            r0 = r0 & 64
            if (r0 == 0) goto L_0x00db
            r0 = r2
            goto L_0x00dc
        L_0x00db:
            r0 = r1
        L_0x00dc:
            r18 = r0
            if (r10 == 0) goto L_0x00f2
            r0 = r21 & 2
            if (r0 == 0) goto L_0x00f2
            if (r18 == 0) goto L_0x00ea
            boolean r0 = r7.mNavBarVirtualKeyHapticFeedbackEnabled
            if (r0 == 0) goto L_0x00f2
        L_0x00ea:
            int r0 = r20.getRepeatCount()
            if (r0 != 0) goto L_0x00f2
            r0 = r2
            goto L_0x00f3
        L_0x00f2:
            r0 = r1
        L_0x00f3:
            r3 = r0
            boolean r0 = r19.handleFunctionKeyOnGamePad(r20)
            if (r0 == 0) goto L_0x00fb
            return r1
        L_0x00fb:
            r5 = 4
            if (r12 == r5) goto L_0x0381
            r0 = 5
            if (r12 == r0) goto L_0x0366
            r0 = 6
            if (r12 == r0) goto L_0x030d
            r0 = 79
            r6 = 3
            if (r12 == r0) goto L_0x02e2
            r0 = 130(0x82, float:1.82E-43)
            if (r12 == r0) goto L_0x02e2
            r0 = 164(0xa4, float:2.3E-43)
            r5 = 24
            if (r12 == r0) goto L_0x01f8
            r0 = 171(0xab, float:2.4E-43)
            if (r12 == r0) goto L_0x01e5
            r0 = 219(0xdb, float:3.07E-43)
            if (r12 == r0) goto L_0x01af
            r0 = 231(0xe7, float:3.24E-43)
            if (r12 == r0) goto L_0x0195
            r0 = 276(0x114, float:3.87E-43)
            if (r12 == r0) goto L_0x0188
            r0 = 126(0x7e, float:1.77E-43)
            if (r12 == r0) goto L_0x02e2
            r0 = 127(0x7f, float:1.78E-43)
            if (r12 == r0) goto L_0x02e2
            switch(r12) {
                case 24: goto L_0x01f8;
                case 25: goto L_0x01f8;
                case 26: goto L_0x0165;
                default: goto L_0x012e;
            }
        L_0x012e:
            switch(r12) {
                case 85: goto L_0x02e2;
                case 86: goto L_0x02e2;
                case 87: goto L_0x02e2;
                case 88: goto L_0x02e2;
                case 89: goto L_0x02e2;
                case 90: goto L_0x02e2;
                case 91: goto L_0x02e2;
                default: goto L_0x0131;
            }
        L_0x0131:
            switch(r12) {
                case 222: goto L_0x02e2;
                case 223: goto L_0x0148;
                case 224: goto L_0x0141;
                default: goto L_0x0134;
            }
        L_0x0134:
            switch(r12) {
                case 280: goto L_0x0139;
                case 281: goto L_0x0139;
                case 282: goto L_0x0139;
                case 283: goto L_0x0139;
                default: goto L_0x0137;
            }
        L_0x0137:
            goto L_0x0386
        L_0x0139:
            r17 = r17 & -2
            r19.interceptSystemNavigationKey(r20)
            r0 = r3
            goto L_0x0391
        L_0x0141:
            r17 = r17 & -2
            r16 = 1
            r0 = r3
            goto L_0x0391
        L_0x0148:
            r17 = r17 & -2
            r16 = 0
            android.os.PowerManager r0 = r7.mPowerManager
            boolean r0 = r0.isInteractive()
            if (r0 != 0) goto L_0x0155
            r3 = 0
        L_0x0155:
            if (r10 == 0) goto L_0x015c
            r19.sleepPress()
            goto L_0x0386
        L_0x015c:
            long r4 = r20.getEventTime()
            r7.sleepRelease(r4)
            goto L_0x0386
        L_0x0165:
            int r0 = r20.getAction()
            java.lang.String r0 = android.view.KeyEvent.actionToString(r0)
            boolean r4 = r7.mPowerKeyHandled
            int r5 = r7.mPowerKeyPressCounter
            com.android.server.policy.EventLogTags.writeInterceptPower(r0, r4, r5)
            r19.cancelPendingAccessibilityShortcutAction()
            r17 = r17 & -2
            r16 = 0
            if (r10 == 0) goto L_0x0183
            r7.interceptPowerKeyDown(r8, r9)
            goto L_0x0386
        L_0x0183:
            r7.interceptPowerKeyUp(r8, r9, r11)
            goto L_0x0386
        L_0x0188:
            r17 = r17 & -2
            r16 = 0
            if (r10 != 0) goto L_0x0386
            android.os.PowerManagerInternal r0 = r7.mPowerManagerInternal
            r0.setUserInactiveOverrideFromWindowManager()
            goto L_0x0386
        L_0x0195:
            if (r10 != 0) goto L_0x01aa
            android.os.PowerManager$WakeLock r0 = r7.mBroadcastWakeLock
            r0.acquire()
            android.os.Handler r0 = r7.mHandler
            r4 = 12
            android.os.Message r0 = r0.obtainMessage(r4)
            r0.setAsynchronous(r2)
            r0.sendToTarget()
        L_0x01aa:
            r17 = r17 & -2
            r0 = r3
            goto L_0x0391
        L_0x01af:
            int r0 = r20.getRepeatCount()
            if (r0 <= 0) goto L_0x01b7
            r0 = r2
            goto L_0x01b8
        L_0x01b7:
            r0 = r1
        L_0x01b8:
            if (r10 == 0) goto L_0x01c8
            if (r0 == 0) goto L_0x01c8
            android.os.Handler r4 = r7.mHandler
            android.os.Message r4 = r4.obtainMessage(r5)
            r4.setAsynchronous(r2)
            r4.sendToTarget()
        L_0x01c8:
            if (r10 != 0) goto L_0x01e0
            if (r0 != 0) goto L_0x01e0
            android.os.Handler r4 = r7.mHandler
            int r5 = r20.getDeviceId()
            r6 = 0
            r2 = 23
            android.os.Message r2 = r4.obtainMessage(r2, r5, r1, r6)
            r4 = 1
            r2.setAsynchronous(r4)
            r2.sendToTarget()
        L_0x01e0:
            r17 = r17 & -2
            r0 = r3
            goto L_0x0391
        L_0x01e5:
            int r0 = r7.mShortPressOnWindowBehavior
            r2 = 1
            if (r0 != r2) goto L_0x0386
            boolean r0 = r7.mPictureInPictureVisible
            if (r0 == 0) goto L_0x0386
            if (r10 != 0) goto L_0x01f3
            r19.showPictureInPictureMenu(r20)
        L_0x01f3:
            r17 = r17 & -2
            r0 = r3
            goto L_0x0391
        L_0x01f8:
            r0 = 25
            if (r12 != r0) goto L_0x022f
            if (r10 == 0) goto L_0x0225
            r19.cancelPendingRingerToggleChordAction()
            if (r9 == 0) goto L_0x0269
            boolean r0 = r7.mScreenshotChordVolumeDownKeyTriggered
            if (r0 != 0) goto L_0x0269
            int r0 = r20.getFlags()
            r0 = r0 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x0269
            r2 = 1
            r7.mScreenshotChordVolumeDownKeyTriggered = r2
            long r1 = r20.getDownTime()
            r7.mScreenshotChordVolumeDownKeyTime = r1
            r1 = 0
            r7.mScreenshotChordVolumeDownKeyConsumed = r1
            r19.cancelPendingPowerKeyAction()
            r19.interceptScreenshotChord()
            r19.interceptAccessibilityShortcutChord()
            goto L_0x0269
        L_0x0225:
            r1 = 0
            r7.mScreenshotChordVolumeDownKeyTriggered = r1
            r19.cancelPendingScreenshotChordAction()
            r19.cancelPendingAccessibilityShortcutAction()
            goto L_0x0269
        L_0x022f:
            if (r12 != r5) goto L_0x0269
            if (r10 == 0) goto L_0x025d
            if (r9 == 0) goto L_0x0269
            boolean r0 = r7.mA11yShortcutChordVolumeUpKeyTriggered
            if (r0 != 0) goto L_0x0269
            int r0 = r20.getFlags()
            r0 = r0 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x0269
            r1 = 1
            r7.mA11yShortcutChordVolumeUpKeyTriggered = r1
            long r0 = r20.getDownTime()
            r7.mA11yShortcutChordVolumeUpKeyTime = r0
            r1 = 0
            r7.mA11yShortcutChordVolumeUpKeyConsumed = r1
            r19.cancelPendingPowerKeyAction()
            r19.cancelPendingScreenshotChordAction()
            r19.cancelPendingRingerToggleChordAction()
            r19.interceptAccessibilityShortcutChord()
            r19.interceptRingerToggleChord()
            goto L_0x0269
        L_0x025d:
            r1 = 0
            r7.mA11yShortcutChordVolumeUpKeyTriggered = r1
            r19.cancelPendingScreenshotChordAction()
            r19.cancelPendingAccessibilityShortcutAction()
            r19.cancelPendingRingerToggleChordAction()
        L_0x0269:
            if (r10 == 0) goto L_0x02c2
            int r0 = r20.getKeyCode()
            r7.sendSystemKeyToStatusBarAsync(r0)
            android.telecom.TelecomManager r2 = r19.getTelecommService()
            if (r2 == 0) goto L_0x0290
            boolean r0 = r7.mHandleVolumeKeysInWM
            if (r0 != 0) goto L_0x0290
            boolean r0 = r2.isRinging()
            if (r0 == 0) goto L_0x0290
            java.lang.String r0 = "interceptKeyBeforeQueueing: VOLUME key-down while ringing: Silence ringer!"
            android.util.Log.i(r4, r0)
            r2.silenceRinger()
            r17 = r17 & -2
            r0 = r3
            goto L_0x0391
        L_0x0290:
            r5 = 0
            android.media.IAudioService r0 = getAudioService()     // Catch:{ Exception -> 0x029b }
            int r0 = r0.getMode()     // Catch:{ Exception -> 0x029b }
            r5 = r0
            goto L_0x02a1
        L_0x029b:
            r0 = move-exception
            java.lang.String r1 = "Error getting AudioService in interceptKeyBeforeQueueing."
            android.util.Log.e(r4, r1, r0)
        L_0x02a1:
            if (r2 == 0) goto L_0x02a9
            boolean r0 = r2.isInCall()
            if (r0 != 0) goto L_0x02ab
        L_0x02a9:
            if (r5 != r6) goto L_0x02ad
        L_0x02ab:
            r0 = 1
            goto L_0x02ae
        L_0x02ad:
            r0 = 0
        L_0x02ae:
            if (r0 == 0) goto L_0x02c2
            r1 = r17 & 1
            if (r1 != 0) goto L_0x02c2
            android.content.Context r1 = r7.mContext
            android.media.session.MediaSessionLegacyHelper r1 = android.media.session.MediaSessionLegacyHelper.getHelper(r1)
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            r6 = 0
            r1.sendVolumeKeyEvent(r8, r4, r6)
            goto L_0x0386
        L_0x02c2:
            boolean r0 = r7.mUseTvRouting
            if (r0 != 0) goto L_0x02dd
            boolean r0 = r7.mHandleVolumeKeysInWM
            if (r0 == 0) goto L_0x02cb
            goto L_0x02dd
        L_0x02cb:
            r0 = r17 & 1
            if (r0 != 0) goto L_0x0386
            android.content.Context r0 = r7.mContext
            android.media.session.MediaSessionLegacyHelper r0 = android.media.session.MediaSessionLegacyHelper.getHelper(r0)
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = 1
            r0.sendVolumeKeyEvent(r8, r1, r2)
            goto L_0x0386
        L_0x02dd:
            r17 = r17 | 1
            r0 = r3
            goto L_0x0391
        L_0x02e2:
            android.content.Context r0 = r7.mContext
            android.media.session.MediaSessionLegacyHelper r0 = android.media.session.MediaSessionLegacyHelper.getHelper(r0)
            boolean r0 = r0.isGlobalPriorityActive()
            if (r0 == 0) goto L_0x02f0
            r17 = r17 & -2
        L_0x02f0:
            r0 = r17 & 1
            if (r0 != 0) goto L_0x0386
            android.os.PowerManager$WakeLock r0 = r7.mBroadcastWakeLock
            r0.acquire()
            android.os.Handler r0 = r7.mHandler
            android.view.KeyEvent r1 = new android.view.KeyEvent
            r1.<init>(r8)
            android.os.Message r0 = r0.obtainMessage(r6, r1)
            r1 = 1
            r0.setAsynchronous(r1)
            r0.sendToTarget()
            goto L_0x0386
        L_0x030d:
            r17 = r17 & -2
            if (r10 == 0) goto L_0x0339
            android.telecom.TelecomManager r0 = r19.getTelecommService()
            r1 = 0
            if (r0 == 0) goto L_0x031c
            boolean r1 = r0.endCall()
        L_0x031c:
            if (r9 == 0) goto L_0x0335
            if (r1 != 0) goto L_0x0335
            r2 = 0
            r7.mEndCallKeyHandled = r2
            android.os.Handler r2 = r7.mHandler
            java.lang.Runnable r4 = r7.mEndCallLongPress
            android.content.Context r5 = r7.mContext
            android.view.ViewConfiguration r5 = android.view.ViewConfiguration.get(r5)
            long r5 = r5.getDeviceGlobalActionKeyTimeout()
            r2.postDelayed(r4, r5)
            goto L_0x0338
        L_0x0335:
            r2 = 1
            r7.mEndCallKeyHandled = r2
        L_0x0338:
            goto L_0x0386
        L_0x0339:
            boolean r0 = r7.mEndCallKeyHandled
            if (r0 != 0) goto L_0x0386
            android.os.Handler r0 = r7.mHandler
            java.lang.Runnable r1 = r7.mEndCallLongPress
            r0.removeCallbacks(r1)
            if (r11 != 0) goto L_0x0386
            int r0 = r7.mEndcallBehavior
            r1 = 1
            r0 = r0 & r1
            if (r0 == 0) goto L_0x0353
            boolean r0 = r19.goHome()
            if (r0 == 0) goto L_0x0353
            goto L_0x0386
        L_0x0353:
            int r0 = r7.mEndcallBehavior
            r0 = r0 & 2
            if (r0 == 0) goto L_0x0386
            long r0 = r20.getEventTime()
            r2 = 4
            r4 = 0
            r7.goToSleep(r0, r2, r4)
            r16 = 0
            r0 = r3
            goto L_0x0391
        L_0x0366:
            if (r10 == 0) goto L_0x0386
            android.telecom.TelecomManager r0 = r19.getTelecommService()
            if (r0 == 0) goto L_0x037f
            boolean r1 = r0.isRinging()
            if (r1 == 0) goto L_0x037f
            java.lang.String r1 = "interceptKeyBeforeQueueing: CALL key-down while ringing: Answer the call!"
            android.util.Log.i(r4, r1)
            r0.acceptRingingCall()
            r17 = r17 & -2
        L_0x037f:
            r0 = r3
            goto L_0x0391
        L_0x0381:
            if (r10 == 0) goto L_0x0388
            r19.interceptBackKeyDown()
        L_0x0386:
            r0 = r3
            goto L_0x0391
        L_0x0388:
            boolean r0 = r19.interceptBackKeyUp(r20)
            if (r0 == 0) goto L_0x0390
            r17 = r17 & -2
        L_0x0390:
            r0 = r3
        L_0x0391:
            boolean r1 = r7.mHasFeatureLeanback
            if (r1 == 0) goto L_0x03a5
            r1 = 4
            if (r12 == r1) goto L_0x039d
            r1 = 20
            if (r12 == r1) goto L_0x039d
            goto L_0x03a5
        L_0x039d:
            boolean r1 = r7.interceptAccessibilityGestureTv(r12, r10)
            if (r1 == 0) goto L_0x03a5
            r17 = r17 & -2
        L_0x03a5:
            com.android.internal.accessibility.AccessibilityShortcutController r1 = r7.mAccessibilityShortcutController
            boolean r2 = r19.isKeyguardLocked()
            boolean r1 = r1.isAccessibilityShortcutAvailable(r2)
            if (r1 == 0) goto L_0x03d1
            r1 = 54
            if (r12 == r1) goto L_0x03b6
            goto L_0x03d1
        L_0x03b6:
            if (r10 == 0) goto L_0x03d1
            boolean r1 = r20.isCtrlPressed()
            if (r1 == 0) goto L_0x03d1
            boolean r1 = r20.isAltPressed()
            if (r1 == 0) goto L_0x03d1
            android.os.Handler r1 = r7.mHandler
            r2 = 17
            android.os.Message r2 = r1.obtainMessage(r2)
            r1.sendMessage(r2)
            r17 = r17 & -2
        L_0x03d1:
            if (r0 == 0) goto L_0x03da
            java.lang.String r1 = "Virtual Key - Press"
            r2 = 0
            r3 = 1
            r7.performHapticFeedback(r3, r2, r1)
        L_0x03da:
            if (r16 == 0) goto L_0x03ea
            long r2 = r20.getEventTime()
            boolean r4 = r7.mAllowTheaterModeWakeFromKey
            r5 = 6
            java.lang.String r6 = "android.policy:KEY"
            r1 = r19
            r1.wakeUp(r2, r4, r5, r6)
        L_0x03ea:
            return r17
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.PhoneWindowManager.interceptKeyBeforeQueueing(android.view.KeyEvent, int):int");
    }

    private void interceptSystemNavigationKey(KeyEvent event) {
        if (event.getAction() != 1) {
            return;
        }
        if ((!this.mAccessibilityManager.isEnabled() || !this.mAccessibilityManager.sendFingerprintGesture(event.getKeyCode())) && this.mSystemNavigationKeysEnabled) {
            sendSystemKeyToStatusBarAsync(event.getKeyCode());
        }
    }

    /* access modifiers changed from: private */
    public void sendSystemKeyToStatusBar(int keyCode) {
        IStatusBarService statusBar = getStatusBarService();
        if (statusBar != null) {
            try {
                statusBar.handleSystemKey(keyCode);
            } catch (RemoteException e) {
            }
        }
    }

    private void sendSystemKeyToStatusBarAsync(int keyCode) {
        Message message = this.mHandler.obtainMessage(21, keyCode, 0);
        message.setAsynchronous(true);
        this.mHandler.sendMessage(message);
    }

    private static boolean isValidGlobalKey(int keyCode) {
        if (keyCode == MSG_NOTIFY_USER_ACTIVITY || keyCode == 223 || keyCode == 224) {
            return false;
        }
        return true;
    }

    private boolean isWakeKeyWhenScreenOff(int keyCode) {
        if (!(keyCode == 24 || keyCode == 25)) {
            if (!(keyCode == MSG_RINGER_TOGGLE_CHORD || keyCode == 79 || keyCode == 130)) {
                if (keyCode != 164) {
                    if (!(keyCode == 222 || keyCode == 126 || keyCode == 127)) {
                        switch (keyCode) {
                            case HdmiCecKeycode.CEC_KEYCODE_INITIAL_CONFIGURATION:
                            case HdmiCecKeycode.CEC_KEYCODE_SELECT_BROADCAST_TYPE:
                            case HdmiCecKeycode.CEC_KEYCODE_SELECT_SOUND_PRESENTATION:
                            case 88:
                            case 89:
                            case 90:
                            case 91:
                                break;
                            default:
                                return true;
                        }
                    }
                }
            }
            return false;
        }
        if (this.mDefaultDisplayPolicy.getDockMode() != 0) {
            return true;
        }
        return false;
    }

    public int interceptMotionBeforeQueueingNonInteractive(int displayId, long whenNanos, int policyFlags) {
        if ((policyFlags & 1) != 0) {
            if (wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromMotion, 7, "android.policy:MOTION")) {
                return 0;
            }
        }
        if (shouldDispatchInputWhenNonInteractive(displayId, 0)) {
            return 1;
        }
        if (isTheaterModeEnabled() && (policyFlags & 1) != 0) {
            wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromMotionWhenNotDreaming, 7, "android.policy:MOTION");
        }
        return 0;
    }

    private boolean shouldDispatchInputWhenNonInteractive(int displayId, int keyCode) {
        Display display;
        IDreamManager dreamManager;
        boolean isDefaultDisplay = displayId == 0 || displayId == -1;
        if (isDefaultDisplay) {
            display = this.mDefaultDisplay;
        } else {
            display = this.mDisplayManager.getDisplay(displayId);
        }
        boolean displayOff = display == null || display.getState() == 1;
        if (!displayOff || this.mHasFeatureWatch) {
            if (isKeyguardShowingAndNotOccluded() && !displayOff && !this.mBeingHangUp) {
                return true;
            }
            if ((!this.mHasFeatureWatch || !(keyCode == 4 || keyCode == 264)) && isDefaultDisplay && (dreamManager = getDreamManager()) != null) {
                try {
                    if (dreamManager.isDreaming()) {
                        return true;
                    }
                } catch (RemoteException e) {
                    Slog.e("WindowManager", "RemoteException when checking if dreaming", e);
                }
            }
            return false;
        } else if (!SUPPORT_FOD) {
            return false;
        } else {
            if (keyCode == 0 || keyCode == 354) {
                return true;
            }
            return false;
        }
    }

    private void dispatchDirectAudioEvent(KeyEvent event) {
        HdmiAudioSystemClient audioSystemClient;
        HdmiControlManager hdmiControlManager = getHdmiControlManager();
        if (hdmiControlManager != null && !hdmiControlManager.getSystemAudioMode() && shouldCecAudioDeviceForwardVolumeKeysSystemAudioModeOff() && (audioSystemClient = hdmiControlManager.getAudioSystemClient()) != null) {
            audioSystemClient.sendKeyEvent(event.getKeyCode(), event.getAction() == 0);
        } else if (event.getAction() == 0) {
            int keyCode = event.getKeyCode();
            String pkgName = this.mContext.getOpPackageName();
            if (keyCode == 24) {
                try {
                    getAudioService().adjustSuggestedStreamVolume(1, Integer.MIN_VALUE, 4101, pkgName, "WindowManager");
                } catch (Exception e) {
                    Log.e("WindowManager", "Error dispatching volume up in dispatchTvAudioEvent.", e);
                }
            } else if (keyCode == 25) {
                try {
                    getAudioService().adjustSuggestedStreamVolume(-1, Integer.MIN_VALUE, 4101, pkgName, "WindowManager");
                } catch (Exception e2) {
                    Log.e("WindowManager", "Error dispatching volume down in dispatchTvAudioEvent.", e2);
                }
            } else if (keyCode == 164) {
                try {
                    if (event.getRepeatCount() == 0) {
                        getAudioService().adjustSuggestedStreamVolume(101, Integer.MIN_VALUE, 4101, pkgName, "WindowManager");
                    }
                } catch (Exception e3) {
                    Log.e("WindowManager", "Error dispatching mute in dispatchTvAudioEvent.", e3);
                }
            }
        }
    }

    private HdmiControlManager getHdmiControlManager() {
        if (!this.mHasFeatureHdmiCec) {
            return null;
        }
        return (HdmiControlManager) this.mContext.getSystemService(HdmiControlManager.class);
    }

    private boolean shouldCecAudioDeviceForwardVolumeKeysSystemAudioModeOff() {
        return RoSystemProperties.CEC_AUDIO_DEVICE_FORWARD_VOLUME_KEYS_SYSTEM_AUDIO_MODE_OFF;
    }

    /* access modifiers changed from: package-private */
    public void dispatchMediaKeyWithWakeLock(KeyEvent event) {
        if (this.mHavePendingMediaKeyRepeatWithWakeLock) {
            this.mHandler.removeMessages(4);
            this.mHavePendingMediaKeyRepeatWithWakeLock = false;
            this.mBroadcastWakeLock.release();
        }
        dispatchMediaKeyWithWakeLockToAudioService(event);
        if (event.getAction() == 0 && event.getRepeatCount() == 0) {
            this.mHavePendingMediaKeyRepeatWithWakeLock = true;
            Message msg = this.mHandler.obtainMessage(4, event);
            msg.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(msg, (long) ViewConfiguration.getKeyRepeatTimeout());
            return;
        }
        this.mBroadcastWakeLock.release();
    }

    /* access modifiers changed from: package-private */
    public void dispatchMediaKeyRepeatWithWakeLock(KeyEvent event) {
        this.mHavePendingMediaKeyRepeatWithWakeLock = false;
        dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.changeTimeRepeat(event, SystemClock.uptimeMillis(), 1, event.getFlags() | 128));
        this.mBroadcastWakeLock.release();
    }

    /* access modifiers changed from: package-private */
    public void dispatchMediaKeyWithWakeLockToAudioService(KeyEvent event) {
        if (this.mActivityManagerInternal.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(this.mContext).sendMediaButtonEvent(event, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void launchVoiceAssistWithWakeLock() {
        IDeviceIdleController dic;
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_ASSIST);
        if (!keyguardOn()) {
            dic = new Intent("android.speech.action.WEB_SEARCH");
        } else {
            IDeviceIdleController dic2 = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
            if (dic2 != null) {
                try {
                    dic2.exitIdle("voice-search");
                } catch (RemoteException e) {
                }
            }
            IDeviceIdleController voiceIntent = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
            voiceIntent.putExtra("android.speech.extras.EXTRA_SECURE", true);
            dic = voiceIntent;
        }
        startActivityAsUser(dic, UserHandle.CURRENT_OR_SELF);
        this.mBroadcastWakeLock.release();
    }

    public void startedGoingToSleep(int why) {
        Slog.i("WindowManager", "Started going to sleep... (why=" + WindowManagerPolicyConstants.offReasonToString(why) + ")");
        this.mGoingToSleep = true;
        this.mRequestedOrGoingToSleep = true;
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onStartedGoingToSleep(why);
        }
    }

    public void finishedGoingToSleep(int why) {
        EventLogTags.writeScreenToggled(0);
        Slog.i("WindowManager", "Finished going to sleep... (why=" + WindowManagerPolicyConstants.offReasonToString(why) + ")");
        MetricsLogger.histogram(this.mContext, "screen_timeout", this.mLockScreenTimeout / 1000);
        this.mGoingToSleep = false;
        this.mRequestedOrGoingToSleep = false;
        this.mDefaultDisplayPolicy.setAwake(false);
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
            updateLockScreenTimeout();
        }
        this.mDefaultDisplayRotation.updateOrientationListener();
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onFinishedGoingToSleep(why, this.mCameraGestureTriggeredDuringGoingToSleep);
        }
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.finishedGoingToSleep();
        }
        this.mCameraGestureTriggeredDuringGoingToSleep = false;
    }

    public void startedWakingUp(int why) {
        EventLogTags.writeScreenToggled(1);
        Slog.i("WindowManager", "Started waking up... (why=" + WindowManagerPolicyConstants.onReasonToString(why) + ")");
        this.mDefaultDisplayPolicy.setAwake(true);
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
            updateLockScreenTimeout();
        }
        this.mDefaultDisplayRotation.updateOrientationListener();
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onStartedWakingUp();
        }
    }

    public void wakingUp(String reason) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onWakingUp(reason);
        }
    }

    public void finishedWakingUp(int why) {
        Slog.i("WindowManager", "Finished waking up... (why=" + WindowManagerPolicyConstants.onReasonToString(why) + ")");
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onFinishedWakingUp();
        }
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.finishedWakingUp();
        }
    }

    private void wakeUpFromPowerKey(long eventTime) {
        wakeUp(eventTime, this.mAllowTheaterModeWakeFromPowerKey, 1, "android.policy:POWER");
    }

    /* access modifiers changed from: private */
    public boolean wakeUp(long wakeTime, boolean wakeInTheaterMode, int reason, String details) {
        boolean theaterModeEnabled = isTheaterModeEnabled();
        if (!wakeInTheaterMode && theaterModeEnabled) {
            return false;
        }
        if (theaterModeEnabled) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
        }
        this.mPowerManager.wakeUp(wakeTime, reason, details);
        return true;
    }

    /* access modifiers changed from: private */
    public void finishKeyguardDrawn() {
        if (this.mDefaultDisplayPolicy.finishKeyguardDrawn()) {
            synchronized (this.mLock) {
                if (this.mKeyguardDelegate != null) {
                    this.mHandler.removeMessages(6);
                }
            }
            this.mWindowManagerInternal.waitForAllWindowsDrawn(this.mWindowManagerDrawCallback, 1000);
        }
    }

    public void screenTurnedOff() {
        Slog.i("WindowManager", "Screen turned off...");
        updateScreenOffSleepToken(true);
        this.mDefaultDisplayPolicy.screenTurnedOff();
        synchronized (this.mLock) {
            if (this.mKeyguardDelegate != null) {
                this.mKeyguardDelegate.onScreenTurnedOff();
            }
        }
        this.mDefaultDisplayRotation.updateOrientationListener();
        reportScreenStateToVrManager(false);
    }

    private long getKeyguardDrawnTimeout() {
        return ((SystemServiceManager) LocalServices.getService(SystemServiceManager.class)).isBootCompleted() ? 1000 : 5000;
    }

    public void screenTurningOn(WindowManagerPolicy.ScreenOnListener screenOnListener) {
        Slog.i("WindowManager", "Screen turning on...");
        updateScreenOffSleepToken(false);
        this.mDefaultDisplayPolicy.screenTurnedOn(screenOnListener);
        synchronized (this.mLock) {
            this.mScreenOnFully = false;
            if (this.mKeyguardDelegate == null || !this.mKeyguardDelegate.hasKeyguard()) {
                Slog.i("WindowManager", "null mKeyguardDelegate: setting mKeyguardDrawComplete.");
                this.mHandler.sendEmptyMessage(5);
            } else {
                this.mHandler.removeMessages(6);
                this.mHandler.sendEmptyMessageDelayed(6, getKeyguardDrawnTimeout());
                this.mKeyguardDelegate.onScreenTurningOn(this.mKeyguardDrawnCallback);
            }
        }
    }

    public void screenTurnedOn() {
        synchronized (this.mLock) {
            if (this.mKeyguardDelegate != null) {
                this.mKeyguardDelegate.onScreenTurnedOn();
            }
        }
        reportScreenStateToVrManager(true);
    }

    public void screenTurningOff(WindowManagerPolicy.ScreenOffListener screenOffListener) {
        this.mWindowManagerFuncs.screenTurningOff(screenOffListener);
        synchronized (this.mLock) {
            if (this.mKeyguardDelegate != null) {
                this.mKeyguardDelegate.onScreenTurningOff();
            }
        }
    }

    private void reportScreenStateToVrManager(boolean isScreenOn) {
        if (this.mVrManagerInternal != null) {
            this.mVrManagerInternal.onScreenStateChanged(isScreenOn);
        }
    }

    /* access modifiers changed from: private */
    public void finishWindowsDrawn() {
        if (this.mDefaultDisplayPolicy.finishWindowsDrawn()) {
            finishScreenTurningOn();
        }
    }

    private void finishScreenTurningOn() {
        boolean enableScreen;
        this.mDefaultDisplayRotation.updateOrientationListener();
        WindowManagerPolicy.ScreenOnListener listener = this.mDefaultDisplayPolicy.getScreenOnListener();
        if (this.mDefaultDisplayPolicy.finishScreenTurningOn()) {
            boolean awake = this.mDefaultDisplayPolicy.isAwake();
            synchronized (this.mLock) {
                if (this.mKeyguardDrawnOnce || !awake) {
                    enableScreen = false;
                } else {
                    this.mKeyguardDrawnOnce = true;
                    enableScreen = true;
                    if (this.mBootMessageNeedsHiding) {
                        this.mBootMessageNeedsHiding = false;
                        hideBootMessages();
                    }
                }
            }
            if (listener != null) {
                listener.onScreenOn(this.mScreenOnDelay);
                this.mScreenOnDelay = 0;
            }
            if (enableScreen) {
                try {
                    this.mWindowManager.enableScreenIfNeeded();
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000f, code lost:
        if (r2.mBootMsgDialog == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0011, code lost:
        android.util.Slog.i("WindowManager", "handleHideBootMessage: dismissing");
        r2.mBootMsgDialog.dismiss();
        r2.mBootMsgDialog = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleHideBootMessage() {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mLock
            monitor-enter(r0)
            boolean r1 = r2.mKeyguardDrawnOnce     // Catch:{ all -> 0x0021 }
            if (r1 != 0) goto L_0x000c
            r1 = 1
            r2.mBootMessageNeedsHiding = r1     // Catch:{ all -> 0x0021 }
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return
        L_0x000c:
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            android.app.ProgressDialog r0 = r2.mBootMsgDialog
            if (r0 == 0) goto L_0x0020
            java.lang.String r0 = "WindowManager"
            java.lang.String r1 = "handleHideBootMessage: dismissing"
            android.util.Slog.i(r0, r1)
            android.app.ProgressDialog r0 = r2.mBootMsgDialog
            r0.dismiss()
            r0 = 0
            r2.mBootMsgDialog = r0
        L_0x0020:
            return
        L_0x0021:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.PhoneWindowManager.handleHideBootMessage():void");
    }

    public boolean isScreenOn() {
        return this.mDefaultDisplayPolicy.isScreenOnEarly();
    }

    public boolean okToAnimate() {
        return this.mDefaultDisplayPolicy.isAwake() && (!this.mGoingToSleep || this.mAodShowing);
    }

    public void enableKeyguard(boolean enabled) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.setKeyguardEnabled(enabled);
        }
    }

    public void exitKeyguardSecurely(WindowManagerPolicy.OnKeyguardExitResult callback) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.verifyUnlock(callback);
        }
    }

    public boolean isKeyguardShowingAndNotOccluded() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null && keyguardServiceDelegate.isShowing() && !this.mKeyguardOccluded) {
            return true;
        }
        return false;
    }

    public boolean isKeyguardTrustedLw() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isTrusted();
    }

    public boolean isKeyguardLocked() {
        return keyguardOn();
    }

    public boolean isKeyguardSecure(int userId) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isSecure(userId);
    }

    public boolean isKeyguardOccluded() {
        if (this.mKeyguardDelegate == null) {
            return false;
        }
        return this.mKeyguardOccluded;
    }

    public boolean inKeyguardRestrictedKeyInputMode() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isInputRestricted();
    }

    public void dismissKeyguardLw(IKeyguardDismissCallback callback, CharSequence message) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null && keyguardServiceDelegate.isShowing()) {
            this.mKeyguardDelegate.dismiss(callback, message);
        } else if (callback != null) {
            try {
                callback.onDismissError();
            } catch (RemoteException e) {
                Slog.w("WindowManager", "Failed to call callback", e);
            }
        }
    }

    public boolean isKeyguardDrawnLw() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mKeyguardDrawnOnce;
        }
        return z;
    }

    public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.startKeyguardExitAnimation(startTime, fadeoutDuration);
        }
    }

    /* access modifiers changed from: package-private */
    public void sendCloseSystemWindows() {
        PhoneWindow.sendCloseSystemWindows(this.mContext, (String) null);
    }

    /* access modifiers changed from: package-private */
    public void sendCloseSystemWindows(String reason) {
        PhoneWindow.sendCloseSystemWindows(this.mContext, reason);
    }

    public void setSafeMode(boolean safeMode) {
        this.mSafeMode = safeMode;
        if (safeMode) {
            performHapticFeedback(10001, true, "Safe Mode Enabled");
        }
    }

    static long[] getLongIntArray(Resources r, int resid) {
        return ArrayUtils.convertToLongArray(r.getIntArray(resid));
    }

    private void bindKeyguard() {
        synchronized (this.mLock) {
            if (!this.mKeyguardBound) {
                this.mKeyguardBound = true;
                this.mKeyguardDelegate.bindService(this.mContext);
            }
        }
    }

    public void onSystemUiStarted() {
        bindKeyguard();
    }

    public void systemReady() {
        this.mKeyguardDelegate.onSystemReady();
        this.mVrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        if (this.mVrManagerInternal != null) {
            this.mVrManagerInternal.addPersistentVrModeStateListener(this.mPersistentVrModeListener);
        }
        readCameraLensCoverState();
        updateUiMode();
        this.mDefaultDisplayRotation.updateOrientationListener();
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mHandler.post(new Runnable() {
                public void run() {
                    PhoneWindowManager.this.updateSettings();
                }
            });
            if (this.mSystemBooted) {
                this.mKeyguardDelegate.onBootCompleted();
            }
        }
        this.mAutofillManagerInternal = (AutofillManagerInternal) LocalServices.getService(AutofillManagerInternal.class);
    }

    public void systemBooted() {
        bindKeyguard();
        synchronized (this.mLock) {
            this.mSystemBooted = true;
            if (this.mSystemReady) {
                this.mKeyguardDelegate.onBootCompleted();
            }
        }
        startedWakingUp(3);
        finishedWakingUp(3);
        screenTurningOn((WindowManagerPolicy.ScreenOnListener) null);
        screenTurnedOn();
        try {
            if (DeviceFeature.hasMirihiSupport() && this.mSlideScreenListener != null) {
                this.mSlideScreenListener.systemReady();
            }
        } catch (Exception e) {
            Slog.d("SlideCoverListener", e.toString());
        }
    }

    public boolean canDismissBootAnimation() {
        return this.mDefaultDisplayPolicy.isKeyguardDrawComplete();
    }

    public void showBootMessage(final CharSequence msg, boolean always) {
        this.mHandler.post(new Runnable() {
            public void run() {
                int theme;
                if (PhoneWindowManager.this.mBootMsgDialog == null) {
                    if (PhoneWindowManager.this.mContext.getPackageManager().hasSystemFeature("android.software.leanback")) {
                        theme = 16974873;
                    } else {
                        theme = 0;
                    }
                    PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                    phoneWindowManager.mBootMsgDialog = new ProgressDialog(phoneWindowManager.mContext, theme) {
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
                    if (PhoneWindowManager.this.mContext.getPackageManager().isDeviceUpgrading()) {
                        PhoneWindowManager.this.mBootMsgDialog.setTitle(17039511);
                    } else {
                        PhoneWindowManager.this.mBootMsgDialog.setTitle(17039503);
                    }
                    PhoneWindowManager.this.mBootMsgDialog.setProgressStyle(0);
                    PhoneWindowManager.this.mBootMsgDialog.setIndeterminate(true);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setType(2021);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().addFlags(258);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setDimAmount(1.0f);
                    WindowManager.LayoutParams lp = PhoneWindowManager.this.mBootMsgDialog.getWindow().getAttributes();
                    lp.screenOrientation = 5;
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setAttributes(lp);
                    PhoneWindowManager.this.mBootMsgDialog.setCancelable(false);
                    PhoneWindowManager.this.mBootMsgDialog.show();
                }
                PhoneWindowManager.this.mBootMsgDialog.setMessage(msg);
            }
        });
    }

    public void hideBootMessages() {
        this.mHandler.sendEmptyMessage(11);
    }

    public void requestUserActivityNotification() {
        if (!this.mNotifyUserActivity && !this.mHandler.hasMessages(MSG_NOTIFY_USER_ACTIVITY)) {
            this.mNotifyUserActivity = true;
        }
    }

    public void userActivity() {
        synchronized (this.mScreenLockTimeout) {
            if (this.mLockScreenTimerActive) {
                this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                this.mHandler.postDelayed(this.mScreenLockTimeout, (long) this.mLockScreenTimeout);
            }
        }
        if (this.mDefaultDisplayPolicy.isAwake() && this.mNotifyUserActivity) {
            this.mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_USER_ACTIVITY, 200);
            this.mNotifyUserActivity = false;
        }
    }

    class ScreenLockTimeout implements Runnable {
        Bundle options;

        ScreenLockTimeout() {
        }

        public void run() {
            synchronized (this) {
                if (PhoneWindowManager.this.mKeyguardDelegate != null) {
                    PhoneWindowManager.this.mKeyguardDelegate.doKeyguardTimeout(this.options);
                }
                PhoneWindowManager.this.mLockScreenTimerActive = false;
                this.options = null;
            }
        }

        public void setLockOptions(Bundle options2) {
            this.options = options2;
        }
    }

    public void lockNow(Bundle options) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
        this.mHandler.removeCallbacks(this.mScreenLockTimeout);
        if (options != null) {
            this.mScreenLockTimeout.setLockOptions(options);
        }
        this.mHandler.post(this.mScreenLockTimeout);
    }

    public void setAllowLockscreenWhenOn(int displayId, boolean allow) {
        if (allow) {
            this.mAllowLockscreenWhenOnDisplays.add(Integer.valueOf(displayId));
        } else {
            this.mAllowLockscreenWhenOnDisplays.remove(Integer.valueOf(displayId));
        }
        updateLockScreenTimeout();
    }

    private void updateLockScreenTimeout() {
        synchronized (this.mScreenLockTimeout) {
            boolean enable = !this.mAllowLockscreenWhenOnDisplays.isEmpty() && this.mDefaultDisplayPolicy.isAwake() && this.mKeyguardDelegate != null && this.mKeyguardDelegate.isSecure(this.mCurrentUserId);
            if (this.mLockScreenTimerActive != enable) {
                if (enable) {
                    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                    this.mHandler.postDelayed(this.mScreenLockTimeout, (long) this.mLockScreenTimeout);
                } else {
                    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                }
                this.mLockScreenTimerActive = enable;
            }
        }
    }

    private void schedulePossibleVeryLongPressReboot() {
        this.mHandler.removeCallbacks(this.mPossibleVeryLongPressReboot);
        this.mHandler.postDelayed(this.mPossibleVeryLongPressReboot, (long) this.mVeryLongPressTimeout);
    }

    private void cancelPossibleVeryLongPressReboot() {
        this.mHandler.removeCallbacks(this.mPossibleVeryLongPressReboot);
    }

    private void updateScreenOffSleepToken(boolean acquire) {
        if (!acquire) {
            ActivityTaskManagerInternal.SleepToken sleepToken = this.mScreenOffSleepToken;
            if (sleepToken != null) {
                sleepToken.release();
                this.mScreenOffSleepToken = null;
            }
        } else if (this.mScreenOffSleepToken == null) {
            this.mScreenOffSleepToken = this.mActivityTaskManagerInternal.acquireSleepToken("ScreenOff", 0);
        }
    }

    public void enableScreenAfterBoot() {
        readLidState();
        applyLidSwitchState();
        updateRotation(true);
    }

    private void applyLidSwitchState() {
        DisplayFoldController displayFoldController;
        int lidState = this.mDefaultDisplayPolicy.getLidState();
        boolean z = true;
        if (this.mLidControlsDisplayFold && (displayFoldController = this.mDisplayFoldController) != null) {
            if (lidState != 0) {
                z = false;
            }
            displayFoldController.requestDeviceFolded(z);
        } else if (lidState == 0) {
            int lidBehavior = getLidBehavior();
            if (lidBehavior == 1) {
                goToSleep(SystemClock.uptimeMillis(), 3, 1);
            } else if (lidBehavior == 2) {
                this.mWindowManagerFuncs.lockDeviceNow();
            }
        }
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUiMode() {
        if (this.mUiModeManager == null) {
            this.mUiModeManager = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
        }
        try {
            this.mUiMode = this.mUiModeManager.getCurrentModeType();
        } catch (RemoteException e) {
        }
    }

    public int getUiMode() {
        return this.mUiMode;
    }

    /* access modifiers changed from: package-private */
    public void updateRotation(boolean alwaysSendConfiguration) {
        try {
            this.mWindowManager.updateRotation(alwaysSendConfiguration, false);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public Intent createHomeDockIntent() {
        Intent intent = null;
        int i = this.mUiMode;
        if (i == 3) {
            if (this.mEnableCarDockHomeCapture) {
                intent = this.mCarDockIntent;
            }
        } else if (i != 2) {
            if (i == 6) {
                int dockMode = this.mDefaultDisplayPolicy.getDockMode();
                if (dockMode == 1 || dockMode == 4 || dockMode == 3) {
                    intent = this.mDeskDockIntent;
                }
            } else if (i == 7) {
                intent = this.mVrHeadsetHomeIntent;
            }
        }
        if (intent == null) {
            return null;
        }
        ActivityInfo ai = null;
        ResolveInfo info = this.mContext.getPackageManager().resolveActivityAsUser(intent, 65664, this.mCurrentUserId);
        if (info != null) {
            ai = info.activityInfo;
        }
        if (ai == null || ai.metaData == null || !ai.metaData.getBoolean("android.dock_home")) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setClassName(ai.packageName, ai.name);
        return intent2;
    }

    /* access modifiers changed from: package-private */
    public void startDockOrHome(int displayId, boolean fromHomeKey, boolean awakenFromDreams) {
        try {
            ActivityManager.getService().stopAppSwitches();
        } catch (RemoteException e) {
        }
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_HOME_KEY);
        if (awakenFromDreams) {
            awakenDreams();
        }
        if (!isUserSetupComplete()) {
            Slog.i("WindowManager", "Not going home because user setup is in progress.");
            return;
        }
        Intent dock = createHomeDockIntent();
        if (dock != null) {
            if (fromHomeKey) {
                try {
                    dock.putExtra("android.intent.extra.FROM_HOME_KEY", fromHomeKey);
                } catch (ActivityNotFoundException e2) {
                }
            }
            startActivityAsUser(dock, UserHandle.CURRENT);
            return;
        }
        this.mActivityTaskManagerInternal.startHomeOnDisplay(this.mCurrentUserId, "startDockOrHome", displayId, true, fromHomeKey);
    }

    /* access modifiers changed from: package-private */
    public boolean goHome() {
        if (!isUserSetupComplete()) {
            Slog.i("WindowManager", "Not going home because user setup is in progress.");
            return false;
        }
        try {
            if (SystemProperties.getInt("persist.sys.uts-test-mode", 0) == 1) {
                Log.d("WindowManager", "UTS-TEST-MODE");
            } else {
                ActivityManager.getService().stopAppSwitches();
                sendCloseSystemWindows();
                Intent dock = createHomeDockIntent();
                if (dock != null) {
                    if (ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, (String) null, dock, dock.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 1, (ProfilerInfo) null, (Bundle) null, -2) == 1) {
                        return false;
                    }
                }
            }
            if (ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, (String) null, this.mHomeIntent, this.mHomeIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 1, (ProfilerInfo) null, (Bundle) null, -2) == 1) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
        }
    }

    private boolean isTheaterModeEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) == 1;
    }

    /* access modifiers changed from: private */
    public boolean performHapticFeedback(int effectId, boolean always, String reason) {
        return performHapticFeedback(Process.myUid(), this.mContext.getOpPackageName(), effectId, always, reason);
    }

    public boolean performHapticFeedback(int uid, String packageName, int effectId, boolean always, String reason) {
        VibrationEffect effect;
        if (!this.mVibrator.hasVibrator()) {
            return false;
        }
        if (((Settings.System.getIntForUser(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0, -2) == 0) && !always) || (effect = getVibrationEffect(effectId)) == null) {
            return false;
        }
        this.mVibrator.vibrate(uid, packageName, effect, reason, VIBRATION_ATTRIBUTES);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002d, code lost:
        return android.os.VibrationEffect.get(21);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.os.VibrationEffect getVibrationEffect(int r6) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x004a
            r0 = 0
            r1 = 1
            if (r6 == r1) goto L_0x0045
            r2 = 10001(0x2711, float:1.4014E-41)
            r3 = 0
            if (r6 == r2) goto L_0x002e
            r2 = 2
            switch(r6) {
                case 3: goto L_0x0045;
                case 4: goto L_0x0027;
                case 5: goto L_0x0024;
                case 6: goto L_0x001f;
                case 7: goto L_0x001a;
                case 8: goto L_0x001a;
                case 9: goto L_0x0015;
                case 10: goto L_0x001a;
                case 11: goto L_0x001a;
                case 12: goto L_0x0045;
                case 13: goto L_0x001a;
                case 14: goto L_0x004a;
                case 15: goto L_0x0045;
                case 16: goto L_0x0045;
                case 17: goto L_0x0010;
                default: goto L_0x000f;
            }
        L_0x000f:
            return r3
        L_0x0010:
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r1)
            return r0
        L_0x0015:
            boolean r0 = r5.mHapticTextHandleEnabled
            if (r0 != 0) goto L_0x0027
            return r3
        L_0x001a:
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r2, r0)
            return r0
        L_0x001f:
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r2)
            return r0
        L_0x0024:
            long[] r2 = r5.mCalendarDateVibePattern
            goto L_0x0031
        L_0x0027:
            r0 = 21
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r0)
            return r0
        L_0x002e:
            long[] r2 = r5.mSafeModeEnabledVibePattern
        L_0x0031:
            int r4 = r2.length
            if (r4 != 0) goto L_0x0035
            return r3
        L_0x0035:
            int r3 = r2.length
            r4 = -1
            if (r3 != r1) goto L_0x0040
            r0 = r2[r0]
            android.os.VibrationEffect r0 = android.os.VibrationEffect.createOneShot(r0, r4)
            return r0
        L_0x0040:
            android.os.VibrationEffect r0 = android.os.VibrationEffect.createWaveform(r2, r4)
            return r0
        L_0x0045:
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r0)
            return r0
        L_0x004a:
            r0 = 5
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.PhoneWindowManager.getVibrationEffect(int):android.os.VibrationEffect");
    }

    public void keepScreenOnStartedLw() {
    }

    public void keepScreenOnStoppedLw() {
        if (isKeyguardShowingAndNotOccluded()) {
            this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
        }
    }

    public boolean hasNavigationBar() {
        return this.mDefaultDisplayPolicy.hasNavigationBar();
    }

    public void setDismissImeOnBackKeyPressed(boolean newValue) {
        this.mDismissImeOnBackKeyPressed = newValue;
    }

    public void setCurrentUserLw(int newUserId) {
        this.mCurrentUserId = newUserId;
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.setCurrentUser(newUserId);
        }
        AccessibilityShortcutController accessibilityShortcutController = this.mAccessibilityShortcutController;
        if (accessibilityShortcutController != null) {
            accessibilityShortcutController.setCurrentUser(newUserId);
        }
        StatusBarManagerInternal statusBar = getStatusBarManagerInternal();
        if (statusBar != null) {
            statusBar.setCurrentUser(newUserId);
        }
    }

    public void setSwitchingUser(boolean switching) {
        this.mKeyguardDelegate.setSwitchingUser(switching);
    }

    public boolean isTopLevelWindow(int windowType) {
        if (windowType < 1000 || windowType > 1999 || windowType == 1003) {
            return true;
        }
        return false;
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1159641169922L, this.mDefaultDisplayRotation.getUserRotationMode());
        proto.write(1159641169923L, this.mDefaultDisplayRotation.getUserRotation());
        proto.write(1159641169924L, this.mDefaultDisplayRotation.getCurrentAppOrientation());
        proto.write(1133871366149L, this.mDefaultDisplayPolicy.isScreenOnFully());
        proto.write(1133871366150L, this.mDefaultDisplayPolicy.isKeyguardDrawComplete());
        proto.write(1133871366151L, this.mDefaultDisplayPolicy.isWindowManagerDrawComplete());
        proto.write(1133871366156L, this.mKeyguardOccluded);
        proto.write(1133871366157L, this.mKeyguardOccludedChanged);
        proto.write(1133871366158L, this.mPendingKeyguardOccluded);
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.writeToProto(proto, 1146756268052L);
        }
        proto.end(token);
    }

    public void dump(String prefix, PrintWriter pw, String[] args) {
        pw.print(prefix);
        pw.print("mSafeMode=");
        pw.print(this.mSafeMode);
        pw.print(" mSystemReady=");
        pw.print(this.mSystemReady);
        pw.print(" mSystemBooted=");
        pw.println(this.mSystemBooted);
        pw.print(prefix);
        pw.print("mCameraLensCoverState=");
        pw.println(WindowManagerPolicy.WindowManagerFuncs.cameraLensStateToString(this.mCameraLensCoverState));
        pw.print(prefix);
        pw.print("mWakeGestureEnabledSetting=");
        pw.println(this.mWakeGestureEnabledSetting);
        pw.print(prefix);
        pw.print("mUiMode=");
        pw.print(Configuration.uiModeToString(this.mUiMode));
        pw.print("mEnableCarDockHomeCapture=");
        pw.println(this.mEnableCarDockHomeCapture);
        pw.print(prefix);
        pw.print("mLidKeyboardAccessibility=");
        pw.print(this.mLidKeyboardAccessibility);
        pw.print(" mLidNavigationAccessibility=");
        pw.print(this.mLidNavigationAccessibility);
        pw.print(" getLidBehavior=");
        pw.println(lidBehaviorToString(getLidBehavior()));
        pw.print(prefix);
        pw.print("mLongPressOnBackBehavior=");
        pw.println(longPressOnBackBehaviorToString(this.mLongPressOnBackBehavior));
        pw.print(prefix);
        pw.print("mLongPressOnHomeBehavior=");
        pw.println(longPressOnHomeBehaviorToString(this.mLongPressOnHomeBehavior));
        pw.print(prefix);
        pw.print("mDoubleTapOnHomeBehavior=");
        pw.println(doubleTapOnHomeBehaviorToString(this.mDoubleTapOnHomeBehavior));
        pw.print(prefix);
        pw.print("mShortPressOnPowerBehavior=");
        pw.println(shortPressOnPowerBehaviorToString(this.mShortPressOnPowerBehavior));
        pw.print(prefix);
        pw.print("mLongPressOnPowerBehavior=");
        pw.println(longPressOnPowerBehaviorToString(this.mLongPressOnPowerBehavior));
        pw.print(prefix);
        pw.print("mVeryLongPressOnPowerBehavior=");
        pw.println(veryLongPressOnPowerBehaviorToString(this.mVeryLongPressOnPowerBehavior));
        pw.print(prefix);
        pw.print("mDoublePressOnPowerBehavior=");
        pw.println(multiPressOnPowerBehaviorToString(this.mDoublePressOnPowerBehavior));
        pw.print(prefix);
        pw.print("mTriplePressOnPowerBehavior=");
        pw.println(multiPressOnPowerBehaviorToString(this.mTriplePressOnPowerBehavior));
        pw.print(prefix);
        pw.print("mShortPressOnSleepBehavior=");
        pw.println(shortPressOnSleepBehaviorToString(this.mShortPressOnSleepBehavior));
        pw.print(prefix);
        pw.print("mShortPressOnWindowBehavior=");
        pw.println(shortPressOnWindowBehaviorToString(this.mShortPressOnWindowBehavior));
        pw.print(prefix);
        pw.print("mAllowStartActivityForLongPressOnPowerDuringSetup=");
        pw.println(this.mAllowStartActivityForLongPressOnPowerDuringSetup);
        pw.print(prefix);
        pw.print("mHasSoftInput=");
        pw.print(this.mHasSoftInput);
        pw.print(" mHapticTextHandleEnabled=");
        pw.println(this.mHapticTextHandleEnabled);
        pw.print(prefix);
        pw.print("mDismissImeOnBackKeyPressed=");
        pw.print(this.mDismissImeOnBackKeyPressed);
        pw.print(" mIncallPowerBehavior=");
        pw.println(incallPowerBehaviorToString(this.mIncallPowerBehavior));
        pw.print(prefix);
        pw.print("mIncallBackBehavior=");
        pw.print(incallBackBehaviorToString(this.mIncallBackBehavior));
        pw.print(" mEndcallBehavior=");
        pw.println(endcallBehaviorToString(this.mEndcallBehavior));
        pw.print(prefix);
        pw.print("mDisplayHomeButtonHandlers=");
        for (int i = 0; i < this.mDisplayHomeButtonHandlers.size(); i++) {
            pw.println(this.mDisplayHomeButtonHandlers.get(this.mDisplayHomeButtonHandlers.keyAt(i)));
        }
        pw.print(prefix);
        pw.print("mKeyguardOccluded=");
        pw.print(this.mKeyguardOccluded);
        pw.print(" mKeyguardOccludedChanged=");
        pw.print(this.mKeyguardOccludedChanged);
        pw.print(" mPendingKeyguardOccluded=");
        pw.println(this.mPendingKeyguardOccluded);
        pw.print(prefix);
        pw.print("mAllowLockscreenWhenOnDisplays=");
        pw.print(!this.mAllowLockscreenWhenOnDisplays.isEmpty());
        pw.print(" mLockScreenTimeout=");
        pw.print(this.mLockScreenTimeout);
        pw.print(" mLockScreenTimerActive=");
        pw.println(this.mLockScreenTimerActive);
        if (this.mHasFeatureLeanback) {
            pw.print(prefix);
            pw.print("mAccessibilityTvKey1Pressed=");
            pw.println(this.mAccessibilityTvKey1Pressed);
            pw.print(prefix);
            pw.print("mAccessibilityTvKey2Pressed=");
            pw.println(this.mAccessibilityTvKey2Pressed);
            pw.print(prefix);
            pw.print("mAccessibilityTvScheduled=");
            pw.println(this.mAccessibilityTvScheduled);
        }
        this.mGlobalKeyManager.dump(prefix, pw);
        MyWakeGestureListener myWakeGestureListener = this.mWakeGestureListener;
        if (myWakeGestureListener != null) {
            myWakeGestureListener.dump(pw, prefix);
        }
        BurnInProtectionHelper burnInProtectionHelper = this.mBurnInProtectionHelper;
        if (burnInProtectionHelper != null) {
            burnInProtectionHelper.dump(prefix, pw);
        }
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.dump(prefix, pw);
        }
        pw.print(prefix);
        pw.println("Looper state:");
        Looper looper = this.mHandler.getLooper();
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(pw);
        looper.dump(printWriterPrinter, prefix + "  ");
    }

    private static String endcallBehaviorToString(int behavior) {
        StringBuilder sb = new StringBuilder();
        if ((behavior & 1) != 0) {
            sb.append("home|");
        }
        if ((behavior & 2) != 0) {
            sb.append("sleep|");
        }
        int N = sb.length();
        if (N == 0) {
            return "<nothing>";
        }
        return sb.substring(0, N - 1);
    }

    private static String incallPowerBehaviorToString(int behavior) {
        if ((behavior & 2) != 0) {
            return "hangup";
        }
        return "sleep";
    }

    private static String incallBackBehaviorToString(int behavior) {
        if ((behavior & 1) != 0) {
            return "hangup";
        }
        return "<nothing>";
    }

    private static String longPressOnBackBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "LONG_PRESS_BACK_NOTHING";
        }
        if (behavior != 1) {
            return Integer.toString(behavior);
        }
        return "LONG_PRESS_BACK_GO_TO_VOICE_ASSIST";
    }

    private static String longPressOnHomeBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "LONG_PRESS_HOME_NOTHING";
        }
        if (behavior == 1) {
            return "LONG_PRESS_HOME_ALL_APPS";
        }
        if (behavior != 2) {
            return Integer.toString(behavior);
        }
        return "LONG_PRESS_HOME_ASSIST";
    }

    private static String doubleTapOnHomeBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "DOUBLE_TAP_HOME_NOTHING";
        }
        if (behavior != 1) {
            return Integer.toString(behavior);
        }
        return "DOUBLE_TAP_HOME_RECENT_SYSTEM_UI";
    }

    private static String shortPressOnPowerBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "SHORT_PRESS_POWER_NOTHING";
        }
        if (behavior == 1) {
            return "SHORT_PRESS_POWER_GO_TO_SLEEP";
        }
        if (behavior == 2) {
            return "SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP";
        }
        if (behavior == 3) {
            return "SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME";
        }
        if (behavior == 4) {
            return "SHORT_PRESS_POWER_GO_HOME";
        }
        if (behavior != 5) {
            return Integer.toString(behavior);
        }
        return "SHORT_PRESS_POWER_CLOSE_IME_OR_GO_HOME";
    }

    private static String longPressOnPowerBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "LONG_PRESS_POWER_NOTHING";
        }
        if (behavior == 1) {
            return "LONG_PRESS_POWER_GLOBAL_ACTIONS";
        }
        if (behavior == 2) {
            return "LONG_PRESS_POWER_SHUT_OFF";
        }
        if (behavior == 3) {
            return "LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM";
        }
        if (behavior == 4) {
            return "LONG_PRESS_POWER_GO_TO_VOICE_ASSIST";
        }
        if (behavior != 5) {
            return Integer.toString(behavior);
        }
        return "LONG_PRESS_POWER_ASSISTANT";
    }

    private static String veryLongPressOnPowerBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "VERY_LONG_PRESS_POWER_NOTHING";
        }
        if (behavior != 1) {
            return Integer.toString(behavior);
        }
        return "VERY_LONG_PRESS_POWER_GLOBAL_ACTIONS";
    }

    private static String multiPressOnPowerBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "MULTI_PRESS_POWER_NOTHING";
        }
        if (behavior == 1) {
            return "MULTI_PRESS_POWER_THEATER_MODE";
        }
        if (behavior != 2) {
            return Integer.toString(behavior);
        }
        return "MULTI_PRESS_POWER_BRIGHTNESS_BOOST";
    }

    private static String shortPressOnSleepBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "SHORT_PRESS_SLEEP_GO_TO_SLEEP";
        }
        if (behavior != 1) {
            return Integer.toString(behavior);
        }
        return "SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME";
    }

    private static String shortPressOnWindowBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "SHORT_PRESS_WINDOW_NOTHING";
        }
        if (behavior != 1) {
            return Integer.toString(behavior);
        }
        return "SHORT_PRESS_WINDOW_PICTURE_IN_PICTURE";
    }

    private static String lidBehaviorToString(int behavior) {
        if (behavior == 0) {
            return "LID_BEHAVIOR_NONE";
        }
        if (behavior == 1) {
            return "LID_BEHAVIOR_SLEEP";
        }
        if (behavior != 2) {
            return Integer.toString(behavior);
        }
        return "LID_BEHAVIOR_LOCK";
    }

    public boolean setAodShowing(boolean aodShowing) {
        if (this.mAodShowing == aodShowing) {
            return false;
        }
        this.mAodShowing = aodShowing;
        return true;
    }

    public void finishLayoutLw(DisplayFrames displayFrames) {
    }

    private class HdmiVideoExtconUEventObserver extends ExtconStateObserver<Boolean> {
        private static final String HDMI_EXIST = "HDMI=1";
        private static final String NAME = "hdmi";
        private final ExtconUEventObserver.ExtconInfo mHdmi;

        private HdmiVideoExtconUEventObserver() {
            this.mHdmi = new ExtconUEventObserver.ExtconInfo(NAME);
        }

        /* access modifiers changed from: private */
        public boolean init() {
            boolean plugged = false;
            try {
                plugged = ((Boolean) parseStateFromFile(this.mHdmi)).booleanValue();
            } catch (FileNotFoundException e) {
                Slog.w("WindowManager", this.mHdmi.getStatePath() + " not found while attempting to determine initial state", e);
            } catch (IOException e2) {
                Slog.e("WindowManager", "Error reading " + this.mHdmi.getStatePath() + " while attempting to determine initial state", e2);
            }
            startObserving(this.mHdmi);
            return plugged;
        }

        public void updateState(ExtconUEventObserver.ExtconInfo extconInfo, String eventName, Boolean state) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setHdmiPlugged(state.booleanValue());
        }

        public Boolean parseState(ExtconUEventObserver.ExtconInfo extconIfno, String state) {
            return Boolean.valueOf(state.contains(HDMI_EXIST));
        }
    }

    /* access modifiers changed from: package-private */
    public Runnable getScreenshotChordLongPress() {
        return this.mScreenshotRunnable;
    }

    /* access modifiers changed from: package-private */
    public Runnable getPowerLongPress() {
        return this.mEndCallLongPress;
    }

    /* access modifiers changed from: package-private */
    public void setPowerLongPress(Runnable value) {
        this.mEndCallLongPress = value;
    }

    /* access modifiers changed from: package-private */
    public void callInterceptPowerKeyUp(boolean canceled) {
    }

    /* access modifiers changed from: protected */
    public boolean setKeyguardVisibilityState(boolean hide, boolean force) {
        return setKeyguardOccludedLw(hide, force);
    }

    /* access modifiers changed from: protected */
    public WindowManagerPolicy.WindowState getKeyguardWindowState() {
        return null;
    }

    /* access modifiers changed from: protected */
    public long getPowerLongPressTimeOut() {
        return ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout();
    }

    /* access modifiers changed from: protected */
    public boolean isScreenOnFully() {
        return this.mDefaultDisplayPolicy.isScreenOnFully();
    }

    public void setHangUpEnable(boolean hangUp) {
        this.mBeingHangUp = hangUp;
    }
}
