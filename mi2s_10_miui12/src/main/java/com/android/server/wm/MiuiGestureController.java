package com.android.server.wm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import android.view.InputMonitor;
import com.android.internal.content.PackageMonitor;
import com.android.server.Watchdog;
import com.miui.internal.transition.IMiuiAppTransitionAnimationHelper;
import com.miui.internal.transition.IMiuiGestureControlHelper;
import com.miui.internal.transition.MiuiAppTransitionAnimationSpec;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class MiuiGestureController {
    static boolean DEBUG_ALL = false;
    static boolean DEBUG_CANCEL = true;
    static boolean DEBUG_DETECT = false;
    static boolean DEBUG_FOLLOW = false;
    static boolean DEBUG_HOME = true;
    static boolean DEBUG_INPUT = false;
    static boolean DEBUG_PERFORMANCE = false;
    static boolean DEBUG_PROGRESS = false;
    static boolean DEBUG_RECENTS = true;
    static boolean DEBUG_STEP = false;
    static final String DEFAULT_MIUI_HOME_PACKAGE_NAME = "com.miui.home";
    private static final String GESTURE_STUB_WINDOW = "GestureStub";
    static final String INPUT_CONSUMER_GESTURE = "gesture_input_consumer";
    static final String INPUT_MONITOR_GESTURE = "gesture_input_monitor";
    public static final String TAG = "MiuiGesture";
    static final int THUMBNAIL_ANIMATION_TIMEOUT = 101;
    static final int WATCHDOG_TIMEOUT = 60000;
    static final Object mGestureLock = new Object();
    /* access modifiers changed from: private */
    public boolean mAssistAvailable;
    private ContentObserver mAssistContentObserver;
    ActivityTaskManagerService mAtms;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DisplayContent mDefaultDisplay;
    /* access modifiers changed from: private */
    public boolean mDefaultHomeIsMiuiHome;
    private DisplayContent mDisplayContent;
    private ContentObserver mForceImmersiveNavBarListener;
    private ContentObserver mGameBoostListener;
    private Runnable mGestureFinishRunnable = new Runnable() {
        public final void run() {
            MiuiGestureController.this.reset();
        }
    };
    GestureHelperDeathRecipient mGestureHelperDeathRecipient;
    private MiuiGesturePointerEventListener mGestureListener = null;
    GestureThreadHandler mHandler;
    private HandlerThread mHandlerThread;
    boolean mHasResumeRecentsBehind;
    InputMonitor mInputMonitor;
    private boolean mIsAppTransitionSkipped = false;
    /* access modifiers changed from: private */
    public boolean mIsGameBoost;
    /* access modifiers changed from: private */
    public volatile boolean mIsGestureOpen;
    /* access modifiers changed from: private */
    public boolean mIsMistakeTouch;
    /* access modifiers changed from: private */
    public boolean mIsRecentsWithinLauncher;
    /* access modifiers changed from: private */
    public boolean mIsSuperSavePowerMode;
    private boolean mIsUseMiuiGestureController;
    private boolean mKeepWallpaperShowing;
    boolean mLaunchRecentsFromGesture;
    private ContentObserver mMistakeTouchListener;
    IMiuiAppTransitionAnimationHelper mMiuiAppTransitionAnimationHelper;
    private IMiuiGestureControlHelper mMiuiGestureControlHelper;
    private LauncherPackageMonitor mPackageMonitor;
    boolean mStopLaunchRecentsBehind;
    private ContentObserver mSuperSavePowerObserver;
    private final BroadcastReceiver mUserPreferenceChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean defaultHomeIsMiuiHome = MiuiGestureController.this.defaultHomeIsMiuiHome();
            if (MiuiGestureController.this.mDefaultHomeIsMiuiHome != defaultHomeIsMiuiHome) {
                boolean unused = MiuiGestureController.this.mDefaultHomeIsMiuiHome = defaultHomeIsMiuiHome;
                MiuiGestureController.this.updateGestureController();
            }
        }
    };
    WindowManagerService mWmService;

    public static class MiuiLaunchIconInfo {
        public String launchIconName;
        public int userId;
    }

    MiuiGestureController(WindowManagerService service, ActivityTaskManagerService atms) {
        this.mWmService = service;
        this.mAtms = atms;
        this.mContext = service.mContext;
        this.mPackageMonitor = new LauncherPackageMonitor();
        LauncherPackageMonitor launcherPackageMonitor = this.mPackageMonitor;
        Context context = this.mContext;
        launcherPackageMonitor.register(context, context.getMainLooper(), UserHandle.ALL, true);
        this.mContext.registerReceiver(this.mUserPreferenceChangeReceiver, new IntentFilter("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED"));
        this.mIsRecentsWithinLauncher = isRecentsWithinLauncher();
        this.mDefaultHomeIsMiuiHome = defaultHomeIsMiuiHome();
        this.mIsUseMiuiGestureController = isUseMiuiGestureController();
        if (this.mIsUseMiuiGestureController) {
            initGestureController();
        }
    }

    private void initGestureController() {
        this.mDisplayContent = this.mWmService.getDefaultDisplayContentLocked();
        this.mHandlerThread = new HandlerThread("miui.gesture", -8);
        this.mHandlerThread.start();
        this.mHandler = new GestureThreadHandler(this.mHandlerThread.getLooper());
        Watchdog.getInstance().addThread(this.mHandler, 60000);
        this.mGestureListener = new MiuiGesturePointerEventListener(this.mWmService, this);
        this.mDefaultDisplay = this.mWmService.getDefaultDisplayContentLocked();
        init();
    }

    private void resetGestureController() {
        this.mDisplayContent = null;
        if (this.mHandler != null) {
            Watchdog.getInstance().removeThread(this.mHandler);
            this.mHandler = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mHandlerThread = null;
        }
        this.mGestureListener.clear();
        this.mGestureListener = null;
        this.mDefaultDisplay = null;
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
        this.mIsGestureOpen = false;
        unregisterContentObserver();
    }

    private void unregisterContentObserver() {
        if (this.mForceImmersiveNavBarListener != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mForceImmersiveNavBarListener);
            this.mForceImmersiveNavBarListener = null;
        }
        if (this.mMistakeTouchListener != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mMistakeTouchListener);
            this.mMistakeTouchListener = null;
        }
        if (this.mGameBoostListener != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mGameBoostListener);
            this.mGameBoostListener = null;
        }
        if (this.mSuperSavePowerObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mSuperSavePowerObserver);
            this.mSuperSavePowerObserver = null;
        }
        if (this.mAssistContentObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mAssistContentObserver);
            this.mAssistContentObserver = null;
        }
    }

    private boolean isUseMiuiGestureController() {
        return !this.mIsRecentsWithinLauncher || !this.mDefaultHomeIsMiuiHome;
    }

    /* access modifiers changed from: private */
    public boolean defaultHomeIsMiuiHome() {
        String pkg;
        ResolveInfo homeInfo = this.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 786432);
        if (homeInfo == null || homeInfo.activityInfo == null || (pkg = homeInfo.activityInfo.packageName) == null || DEFAULT_MIUI_HOME_PACKAGE_NAME.equals(pkg)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isRecentsWithinLauncher() {
        Bundle metaData;
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.mContext.getPackageManager().getPackageInfo(DEFAULT_MIUI_HOME_PACKAGE_NAME, 128);
        } catch (Exception e) {
            Slog.d("MiuiGesture", "isRecentsWithinLauncher: getPackageInfo error.", e);
        }
        if (packageInfo == null || packageInfo.applicationInfo == null || (metaData = packageInfo.applicationInfo.metaData) == null) {
            return false;
        }
        return metaData.getBoolean("supportRecents", false);
    }

    private class LauncherPackageMonitor extends PackageMonitor {
        private LauncherPackageMonitor() {
        }

        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            onPackageModified(packageName);
            return true;
        }

        public void onPackageAdded(String packageName, int uid) {
            onPackageModified(packageName);
        }

        public void onPackageRemoved(String packageName, int uid) {
            onPackageModified(packageName);
        }

        public void onPackageModified(String packageName) {
            boolean isRecentsWithinLauncher;
            if (packageName != null && MiuiGestureController.DEFAULT_MIUI_HOME_PACKAGE_NAME.equals(packageName) && MiuiGestureController.this.mIsRecentsWithinLauncher != (isRecentsWithinLauncher = MiuiGestureController.this.isRecentsWithinLauncher())) {
                boolean unused = MiuiGestureController.this.mIsRecentsWithinLauncher = isRecentsWithinLauncher;
                MiuiGestureController.this.updateGestureController();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateGestureController() {
        boolean isUseMiuiGestureController = isUseMiuiGestureController();
        if (this.mIsUseMiuiGestureController != isUseMiuiGestureController) {
            this.mIsUseMiuiGestureController = isUseMiuiGestureController;
            if (this.mIsUseMiuiGestureController) {
                initGestureController();
            } else {
                resetGestureController();
            }
        }
    }

    private void init() {
        boolean hasNavigationBar = this.mWmService.hasNavigationBar(this.mDisplayContent.getDisplayId());
        if (DEBUG_PROGRESS) {
            Slog.d("MiuiGesture", "hasNavigationBar: " + hasNavigationBar);
        }
        if (this.mDefaultDisplay != null && hasNavigationBar) {
            if (MiuiSettings.Global.getBoolean(this.mContext.getContentResolver(), "force_fsg_nav_bar")) {
                this.mIsGestureOpen = true;
                monitorInput();
            }
            boolean z = false;
            this.mIsMistakeTouch = Settings.Global.getInt(this.mContext.getContentResolver(), "show_mistake_touch_toast", 1) != 0;
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "gb_boosting", 0, -2) != 0) {
                z = true;
            }
            this.mIsGameBoost = z;
            this.mIsSuperSavePowerMode = MiuiSettings.System.isSuperSaveModeOpen(this.mContext, -2);
            this.mAssistAvailable = isSupportGoogleAssist(-2);
            Settings.Secure.putInt(this.mContext.getContentResolver(), "fw_fsgesture_support_superpower", 1);
            registerContentObserver();
        }
    }

    /* access modifiers changed from: private */
    public void monitorInput() {
        this.mInputMonitor = this.mWmService.mInputManager.monitorGestureInput(INPUT_MONITOR_GESTURE, this.mDefaultDisplay.getDisplayId());
        this.mGestureListener.initMonitor(this.mInputMonitor.getInputChannel());
    }

    private void registerContentObserver() {
        this.mForceImmersiveNavBarListener = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                boolean isOpen = MiuiSettings.Global.getBoolean(MiuiGestureController.this.mContext.getContentResolver(), "force_fsg_nav_bar");
                if (MiuiGestureController.this.mDefaultDisplay != null) {
                    if (isOpen && !MiuiGestureController.this.mIsGestureOpen) {
                        MiuiGestureController.this.monitorInput();
                        boolean unused = MiuiGestureController.this.mIsGestureOpen = true;
                    }
                    if (!isOpen && MiuiGestureController.this.mIsGestureOpen && MiuiGestureController.this.mInputMonitor != null) {
                        MiuiGestureController.this.mInputMonitor.dispose();
                        MiuiGestureController miuiGestureController = MiuiGestureController.this;
                        miuiGestureController.mInputMonitor = null;
                        boolean unused2 = miuiGestureController.mIsGestureOpen = false;
                    }
                    synchronized (MiuiGestureController.this.mWmService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            MiuiGestureController.this.ensureActivitiesVisible();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        };
        this.mMistakeTouchListener = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiGestureController miuiGestureController = MiuiGestureController.this;
                boolean z = false;
                if (Settings.Global.getInt(miuiGestureController.mContext.getContentResolver(), "show_mistake_touch_toast", 0) != 0) {
                    z = true;
                }
                boolean unused = miuiGestureController.mIsMistakeTouch = z;
            }
        };
        this.mGameBoostListener = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiGestureController miuiGestureController = MiuiGestureController.this;
                boolean z = false;
                if (Settings.Secure.getIntForUser(miuiGestureController.mContext.getContentResolver(), "gb_boosting", 0, -2) != 0) {
                    z = true;
                }
                boolean unused = miuiGestureController.mIsGameBoost = z;
            }
        };
        this.mSuperSavePowerObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiGestureController miuiGestureController = MiuiGestureController.this;
                boolean unused = miuiGestureController.mIsSuperSavePowerMode = MiuiSettings.System.isSuperSaveModeOpen(miuiGestureController.mContext, -2);
            }
        };
        this.mAssistContentObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiGestureController miuiGestureController = MiuiGestureController.this;
                boolean unused = miuiGestureController.mAssistAvailable = miuiGestureController.isSupportGoogleAssist(-2);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_fsg_nav_bar"), false, this.mForceImmersiveNavBarListener, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("show_mistake_touch_toast"), false, this.mMistakeTouchListener, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("gb_boosting"), false, this.mGameBoostListener, -2);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("power_supersave_mode_open"), false, this.mSuperSavePowerObserver, -2);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("assistant"), false, this.mAssistContentObserver);
    }

    public boolean isSupportGoogleAssist(int userId) {
        ComponentName cmp = getAssistInfoForUser(userId);
        if (cmp != null) {
            return "com.google.android.googlequicksearchbox".equals(cmp.getPackageName());
        }
        return false;
    }

    private ComponentName getAssistInfoForUser(int userId) {
        String setting = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "assistant", userId);
        if (setting != null) {
            return ComponentName.unflattenFromString(setting);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void notifyGestureStartRecents() {
        IMiuiGestureControlHelper iMiuiGestureControlHelper;
        if (this.mIsUseMiuiGestureController && (iMiuiGestureControlHelper = this.mMiuiGestureControlHelper) != null) {
            try {
                iMiuiGestureControlHelper.notifyGestureStartRecents();
            } catch (RemoteException e) {
                Slog.e("MiuiGesture", "fail to notifyGestureStartRecents");
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyGestureAnimationStart() {
        IMiuiGestureControlHelper iMiuiGestureControlHelper;
        if (this.mIsUseMiuiGestureController && (iMiuiGestureControlHelper = this.mMiuiGestureControlHelper) != null) {
            try {
                iMiuiGestureControlHelper.notifyGestureAnimationStart();
            } catch (RemoteException e) {
                Slog.e("MiuiGesture", "fail to notifyGestureAnimationStart");
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyGestureAnimationCancel() {
        IMiuiGestureControlHelper iMiuiGestureControlHelper;
        if (this.mIsUseMiuiGestureController && (iMiuiGestureControlHelper = this.mMiuiGestureControlHelper) != null) {
            try {
                iMiuiGestureControlHelper.notifyGestureAnimationCancel();
            } catch (RemoteException e) {
                Slog.e("MiuiGesture", "fail to notifyGestureAnimationCancel");
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyGestureAnimationEnd() {
        IMiuiGestureControlHelper iMiuiGestureControlHelper;
        if (this.mIsUseMiuiGestureController && (iMiuiGestureControlHelper = this.mMiuiGestureControlHelper) != null) {
            try {
                iMiuiGestureControlHelper.notifyGestureAnimationEnd();
            } catch (RemoteException e) {
                Slog.e("MiuiGesture", "fail to notifyGestureAnimationEnd");
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelGoHomeAnimationIfNeeded() {
        if (this.mIsUseMiuiGestureController && this.mIsGestureOpen) {
            MiuiGesturePointerEventListener miuiGesturePointerEventListener = this.mGestureListener;
            miuiGesturePointerEventListener.cancelGoHomeAnimationIfNeeded(miuiGesturePointerEventListener.mTopAppWindowToken);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelGoHomeAnimationIfNeeded(AppWindowToken token) {
        if (this.mIsUseMiuiGestureController && this.mIsGestureOpen) {
            this.mGestureListener.cancelGoHomeAnimationIfNeeded(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyStartFromRecents(ActivityRecord r) {
        if (this.mIsUseMiuiGestureController && this.mIsGestureOpen && r.mAppWindowToken != null && r.mAppWindowToken.getDisplayContent() == this.mWmService.getDefaultDisplayContentLocked()) {
            unsetAppTransitionSkipped();
            this.mGestureListener.notifyStartFromRecents(r.mAppWindowToken);
            WindowManagerService windowManagerService = this.mWmService;
            windowManagerService.mPendingExecuteAppTransition = true;
            windowManagerService.mH.removeMessages(103);
            this.mWmService.mH.sendEmptyMessageDelayed(103, 100);
        }
    }

    public void ensurePinnedStackVisible(TaskStack taskStack) {
        if (this.mIsUseMiuiGestureController) {
            this.mGestureListener.ensurePinnedStackVisible(taskStack);
        }
    }

    public void displayConfigurationChange(DisplayContent displayContent, Configuration configuration) {
        if (this.mIsUseMiuiGestureController) {
            this.mGestureListener.displayConfigurationChange(displayContent, configuration);
        }
    }

    final class GestureThreadHandler extends Handler {
        GestureThreadHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                synchronized (MiuiGestureController.mGestureLock) {
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isGestureOpen() {
        return this.mIsGestureOpen;
    }

    /* access modifiers changed from: package-private */
    public boolean isMistakeTouch() {
        return this.mIsMistakeTouch;
    }

    /* access modifiers changed from: package-private */
    public boolean isGameBoostState() {
        return this.mIsGameBoost;
    }

    /* access modifiers changed from: package-private */
    public boolean isSuperSavePowerMode() {
        return this.mIsSuperSavePowerMode;
    }

    /* access modifiers changed from: package-private */
    public boolean isAssistAvailable() {
        return this.mAssistAvailable;
    }

    /* access modifiers changed from: package-private */
    public void tryToSetGestureStubWindow(WindowState win) {
        if (this.mIsUseMiuiGestureController && win != null && GESTURE_STUB_WINDOW.equals(win.mAttrs.getTitle()) && win.mAttrs.type == 2027) {
            if (DEBUG_ALL) {
                Slog.w("MiuiGesture", "set gesture stub window: " + win);
            }
            MiuiGestureDetector.setGestureStubWindow(win);
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureActivitiesVisible() {
        if (this.mIsUseMiuiGestureController) {
            this.mAtms.mStackSupervisor.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void setMiuiAppTransitionAnimationHelper(IMiuiAppTransitionAnimationHelper helper) {
        if (this.mIsUseMiuiGestureController) {
            this.mMiuiAppTransitionAnimationHelper = helper;
        }
    }

    /* access modifiers changed from: package-private */
    public void setRecentsItemCoordinates(int startX, int startY, int targetWidth, int targetHeight) {
        if (this.mIsUseMiuiGestureController) {
            this.mGestureListener.mRecentsStrategy.setRecentsItemCoordinates(startX, startY, targetWidth, targetHeight);
        }
    }

    /* access modifiers changed from: package-private */
    public void setLoadBackHomeAnimation(boolean loadBackHomeAnimation) {
        if (this.mIsUseMiuiGestureController) {
            this.mGestureListener.setLoadBackHomeAnimation(loadBackHomeAnimation);
        }
    }

    /* access modifiers changed from: package-private */
    public void registerMiuiGestureControlHelper(IMiuiGestureControlHelper helper) {
        if ((!isRecentsWithinLauncher() || !defaultHomeIsMiuiHome()) && helper != null) {
            unlinkToDeathMiuiGestureHelper();
            this.mGestureHelperDeathRecipient = new GestureHelperDeathRecipient();
            try {
                helper.asBinder().linkToDeath(this.mGestureHelperDeathRecipient, 0);
                this.mMiuiGestureControlHelper = helper;
            } catch (RemoteException e) {
                Slog.w("MiuiGesture", "MiuiGestureControlHelper linkToDeath failed.");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterMiuiGestureControlHelper(GestureHelperDeathRecipient recipient) {
        if (isRecentsWithinLauncher() && defaultHomeIsMiuiHome()) {
            return;
        }
        if (recipient != this.mGestureHelperDeathRecipient) {
            Slog.w("MiuiGesture", "The death recipient has changed, we have registered a new GestureHelper so we don't need to unregister now.");
            return;
        }
        unlinkToDeathMiuiGestureHelper();
        this.mMiuiGestureControlHelper = null;
    }

    private void unlinkToDeathMiuiGestureHelper() {
        IMiuiGestureControlHelper iMiuiGestureControlHelper = this.mMiuiGestureControlHelper;
        if (iMiuiGestureControlHelper != null && this.mGestureHelperDeathRecipient != null) {
            iMiuiGestureControlHelper.asBinder().unlinkToDeath(this.mGestureHelperDeathRecipient, 0);
            this.mGestureHelperDeathRecipient = null;
        }
    }

    private final class GestureHelperDeathRecipient implements IBinder.DeathRecipient {
        private GestureHelperDeathRecipient() {
        }

        public void binderDied() {
            MiuiGestureController.this.unregisterMiuiGestureControlHelper(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyGestureReady(AppWindowToken token) {
        if (this.mIsUseMiuiGestureController) {
            this.mWmService.mH.removeCallbacks(this.mGestureFinishRunnable);
            if (token != null) {
                Task task = token.getTask();
                if (task != null) {
                    task.mUpdateTaskSnapshotByGesture = true;
                }
                this.mWmService.mTaskSnapshotController.handleSnapshotTaskByGesture(token);
            }
        }
    }

    public void notifyGestureFinish(boolean immediate) {
        this.mWmService.mH.postDelayed(this.mGestureFinishRunnable, immediate ? 0 : 1000);
    }

    public void cancelRecents() {
        if (this.mLaunchRecentsFromGesture) {
            this.mLaunchRecentsFromGesture = false;
            this.mHasResumeRecentsBehind = false;
            this.mAtms.mStackSupervisor.mHandler.post(new Runnable() {
                public final void run() {
                    MiuiGestureController.this.lambda$cancelRecents$0$MiuiGestureController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$cancelRecents$0$MiuiGestureController() {
        synchronized (this.mAtms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAtms.mStackSupervisor.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: private */
    public void reset() {
        this.mKeepWallpaperShowing = false;
        this.mLaunchRecentsFromGesture = false;
        this.mHasResumeRecentsBehind = false;
    }

    /* access modifiers changed from: package-private */
    public void setSkipAppTransition() {
        this.mIsAppTransitionSkipped = true;
    }

    /* access modifiers changed from: package-private */
    public void unsetAppTransitionSkipped() {
        if (this.mIsUseMiuiGestureController) {
            this.mIsAppTransitionSkipped = false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAppTransitionSkipped() {
        return this.mIsAppTransitionSkipped;
    }

    /* access modifiers changed from: package-private */
    public void startGestureAnimation(WindowStateAnimator animator) {
        if (animator != null) {
            animator.mHandleByGesture = true;
            if (animator.mWin.mAppToken != null) {
                animator.mWin.mAppToken.mHandleByGesture = true;
            }
        }
        this.mKeepWallpaperShowing = true;
    }

    /* access modifiers changed from: package-private */
    public void stopGestureAnimation(WindowStateAnimator animator) {
        if (animator != null) {
            animator.mHandleByGesture = false;
            if (animator.mWin.mAppToken != null) {
                animator.mWin.mAppToken.mHandleByGesture = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setKeepWallpaperShowing(boolean show) {
        if (this.mIsUseMiuiGestureController) {
            this.mKeepWallpaperShowing = show;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean keepWallpaperShowing() {
        return this.mKeepWallpaperShowing;
    }

    public void setLaunchRecentsBehind(boolean launchBehind) {
        this.mStopLaunchRecentsBehind = !launchBehind;
    }

    public boolean isHomeAppToken(AppWindowToken token) {
        return ActivityStackInjector.isDefaultHome(token);
    }

    public boolean isMiuiHomeAppToken(AppWindowToken token) {
        return ActivityStackInjector.isDefaultHome(token) && ActivityStackInjector.isMiuiHome(token.mActivityRecord);
    }

    public void launchHome(AppWindowToken token) {
        if (token != null) {
            this.mAtms.mStackSupervisor.mHandler.post(new Runnable(token) {
                private final /* synthetic */ AppWindowToken f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MiuiGestureController.this.lambda$launchHome$1$MiuiGestureController(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x006c, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$launchHome$1$MiuiGestureController(com.android.server.wm.AppWindowToken r8) {
        /*
            r7 = this;
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mAtms
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0070 }
            com.android.server.wm.ActivityRecord r1 = r8.mActivityRecord     // Catch:{ all -> 0x0070 }
            r2 = 0
            if (r1 != 0) goto L_0x0026
            com.android.server.wm.ActivityTaskManagerService r3 = r7.mAtms     // Catch:{ all -> 0x0070 }
            r3.stopAppSwitches()     // Catch:{ all -> 0x0070 }
            com.android.server.wm.ActivityTaskManagerService r3 = r7.mAtms     // Catch:{ all -> 0x0070 }
            com.android.server.wm.RootActivityContainer r3 = r3.mRootActivityContainer     // Catch:{ all -> 0x0070 }
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mAtms     // Catch:{ all -> 0x0070 }
            com.android.server.wm.RootActivityContainer r4 = r4.mRootActivityContainer     // Catch:{ all -> 0x0070 }
            int r4 = r4.mCurrentUser     // Catch:{ all -> 0x0070 }
            java.lang.String r5 = "launchHomeByGesture"
            r3.startHomeOnDisplay(r4, r5, r2)     // Catch:{ all -> 0x0070 }
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0026:
            boolean r3 = com.android.server.wm.ActivityStackInjector.isMiuiHome(r1)     // Catch:{ all -> 0x0070 }
            android.content.pm.ActivityInfo r4 = r1.info     // Catch:{ all -> 0x0070 }
            if (r4 == 0) goto L_0x0038
            if (r3 == 0) goto L_0x0038
            android.content.pm.ActivityInfo r4 = r1.info     // Catch:{ all -> 0x0070 }
            int r5 = r4.flags     // Catch:{ all -> 0x0070 }
            r5 = r5 | 16384(0x4000, float:2.2959E-41)
            r4.flags = r5     // Catch:{ all -> 0x0070 }
        L_0x0038:
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mAtms     // Catch:{ all -> 0x0070 }
            r4.stopAppSwitches()     // Catch:{ all -> 0x0070 }
            int r4 = r1.launchedFromUid     // Catch:{ all -> 0x0070 }
            android.content.Intent r5 = r1.intent     // Catch:{ all -> 0x0070 }
            java.lang.String r6 = r1.launchedFromPackage     // Catch:{ all -> 0x0070 }
            r1.deliverNewIntentLocked(r4, r5, r6)     // Catch:{ all -> 0x0070 }
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mAtms     // Catch:{ all -> 0x0070 }
            com.android.server.wm.RootActivityContainer r4 = r4.mRootActivityContainer     // Catch:{ all -> 0x0070 }
            com.android.server.wm.ActivityTaskManagerService r5 = r7.mAtms     // Catch:{ all -> 0x0070 }
            com.android.server.wm.RootActivityContainer r5 = r5.mRootActivityContainer     // Catch:{ all -> 0x0070 }
            int r5 = r5.mCurrentUser     // Catch:{ all -> 0x0070 }
            java.lang.String r6 = "launchHomeByGesture"
            r4.startHomeOnDisplay(r5, r6, r2)     // Catch:{ all -> 0x0070 }
            android.content.pm.ActivityInfo r2 = r1.info     // Catch:{ all -> 0x0070 }
            if (r2 == 0) goto L_0x006b
            if (r3 == 0) goto L_0x006b
            android.content.pm.ActivityInfo r2 = r1.info     // Catch:{ all -> 0x0070 }
            int r2 = r2.flags     // Catch:{ all -> 0x0070 }
            r2 = r2 & 16384(0x4000, float:2.2959E-41)
            if (r2 == 0) goto L_0x006b
            android.content.pm.ActivityInfo r2 = r1.info     // Catch:{ all -> 0x0070 }
            int r4 = r2.flags     // Catch:{ all -> 0x0070 }
            r4 = r4 & -16385(0xffffffffffffbfff, float:NaN)
            r2.flags = r4     // Catch:{ all -> 0x0070 }
        L_0x006b:
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0070:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureController.lambda$launchHome$1$MiuiGestureController(com.android.server.wm.AppWindowToken):void");
    }

    public MiuiAppTransitionAnimationSpec getLaunchIconInfo(MiuiLaunchIconInfo launchIconInfo) {
        IMiuiAppTransitionAnimationHelper iMiuiAppTransitionAnimationHelper = this.mMiuiAppTransitionAnimationHelper;
        if (iMiuiAppTransitionAnimationHelper == null || launchIconInfo == null) {
            return null;
        }
        try {
            Binder.allowBlocking(iMiuiAppTransitionAnimationHelper.asBinder());
            long startTime = SystemClock.uptimeMillis();
            MiuiAppTransitionAnimationSpec spec = this.mMiuiAppTransitionAnimationHelper.getSpec(launchIconInfo.launchIconName, launchIconInfo.userId);
            checkTime(startTime, "getSpec from launcher");
            return spec;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String[] args) {
        if (this.mIsUseMiuiGestureController) {
            if (args == null || args.length <= 1) {
                pw.println("dump of gesture:");
                synchronized (mGestureLock) {
                    pw.print("    ");
                    pw.println("mIsGestureOpen=" + this.mIsGestureOpen);
                    pw.print("    ");
                    pw.println("mHasResumeRecentsBehind=" + this.mHasResumeRecentsBehind);
                    pw.print("    ");
                    pw.println("mIsAppTransitionSkipped=" + this.mIsAppTransitionSkipped);
                    pw.print("    ");
                    pw.println("mKeepWallpaperShowing=" + this.mKeepWallpaperShowing);
                    pw.print("    ");
                    pw.println("mHotSpaceHeight=" + MiuiGestureDetector.getGestureHotSpaceHeight(false));
                }
                this.mGestureListener.dump(pw, "    ");
                FullScreenEventReporter.dump(pw, "    ");
                return;
            }
            String next = args[1];
            if ("0".equals(next)) {
                DEBUG_ALL = false;
                DEBUG_STEP = false;
            } else if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(next)) {
                DEBUG_ALL = true;
                DEBUG_STEP = false;
            } else if (SplitScreenReporter.ACTION_EXIT_SPLIT.equals(next)) {
                DEBUG_ALL = true;
                DEBUG_STEP = true;
            } else {
                pw.println("unknown cmd " + next);
            }
            toggleDebug();
        }
    }

    private void toggleDebug() {
        boolean z = DEBUG_PROGRESS;
        boolean z2 = DEBUG_ALL;
        DEBUG_PROGRESS = z | z2;
        DEBUG_DETECT |= z2;
        DEBUG_FOLLOW |= z2;
        DEBUG_INPUT |= z2;
        DEBUG_HOME |= z2;
        DEBUG_RECENTS |= z2;
        DEBUG_CANCEL |= z2;
        DEBUG_PERFORMANCE |= z2;
    }

    private void checkTime(long startTime, String where) {
        long now = SystemClock.uptimeMillis();
        if (now - startTime > 50) {
            Slog.w("MiuiGesture", "Slow operation: " + (now - startTime) + "ms so far, now at " + where);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyIgnoreInput(AppWindowToken token, boolean ignore, boolean immediately) {
        if (token != null) {
            token.mIgnoreInput = ignore;
            if (DEBUG_RECENTS || DEBUG_INPUT) {
                Slog.d("MiuiGesture", "set " + token + ", ignoreInput = " + ignore);
            }
            if (immediately) {
                InputMonitor inputMonitor = this.mWmService.getDefaultDisplayContentLocked().getInputMonitor();
                synchronized (this.mWmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        inputMonitor.forceUpdateImmediately();
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setRecentsWindowState(WindowState w) {
        this.mGestureListener.setRecentsWindowState(w);
    }

    /* access modifiers changed from: package-private */
    public void setRecentsAppWindowToken(AppWindowToken token) {
        this.mGestureListener.setRecentsAppWindowToken(token);
    }

    /* access modifiers changed from: package-private */
    public boolean isGestureRunning() {
        if (!this.mIsUseMiuiGestureController) {
            return false;
        }
        return this.mGestureListener.isGestureRunning();
    }

    /* access modifiers changed from: package-private */
    public final boolean isRecentsStackLaunchBehind(ConfigurationContainer container) {
        if (this.mIsUseMiuiGestureController && container.getActivityType() == 3 && this.mLaunchRecentsFromGesture) {
            return true;
        }
        return false;
    }

    static boolean isWindowDummyVisible(WindowState w) {
        return (w == null || w.mAppToken == null || !w.mAppToken.mIsDummyVisible) ? false : true;
    }

    static boolean isAppDummyVisible(AppWindowToken aToken) {
        return aToken != null && aToken.mIsDummyVisible;
    }

    static boolean isActivityDummyVisible(ActivityRecord r) {
        return r != null && r.mIsDummyVisible;
    }

    /* access modifiers changed from: package-private */
    public void notifyAppDummyVisible(DisplayContent dc) {
        if (this.mIsUseMiuiGestureController) {
            dc.forAllWindows((Consumer<WindowState>) new Consumer() {
                public final void accept(Object obj) {
                    MiuiGestureController.this.lambda$notifyAppDummyVisible$2$MiuiGestureController((WindowState) obj);
                }
            }, true);
        }
    }

    public /* synthetic */ void lambda$notifyAppDummyVisible$2$MiuiGestureController(WindowState w) {
        if (w.mAppToken != null) {
            if (w.mAppToken.mIsDummyVisible && w.mWinAnimator != null) {
                w.mWinAnimator.hide("hide by gesture");
            }
            if (w.mAppToken.mIsDummyAnimating && !isGestureRunning()) {
                setKeepWallpaperShowing(false);
            }
            w.mAppToken.mIsDummyAnimating = false;
        }
    }
}
