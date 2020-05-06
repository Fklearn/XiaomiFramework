package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.KeyguardManager;
import android.app.TaskStackListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import com.android.server.pm.Settings;
import com.miui.internal.transition.IMiuiFreeFormGestureControlHelper;
import java.io.PrintWriter;
import miui.process.ForegroundInfo;
import miui.process.IForegroundWindowListener;

public class MiuiFreeFormGestureController {
    private static final String ACTION_DOWNWARD_MOVEMENT_SMALLWINDOW = "miui.intent.action.down";
    private static final String ACTION_FULLSCREEN_STATE_CHANGE = "com.miui.fullscreen_state_change";
    private static final String ACTION_INPUT_METHOD_VISIBLE_HEIGHT_CHANGED = "miui.intent.action.INPUT_METHOD_VISIBLE_HEIGHT_CHANGED";
    private static final String ACTION_KEYCODE_BACK = "miui.intent.KEYCODE_BACK";
    private static final String ACTION_LAUNCH_FULLSCREEN_FROM_FREEFORM = "miui.intent.action_launch_fullscreen_from_freeform";
    private static final String ACTION_UPWARD_MOVEMENT_SMALLWINDOW = "miui.intent.action.up";
    public static boolean DEBUG = false;
    private static final String EXTRA_FULLSCREEN_STATE_NAME = "state";
    private static final String EXTRA_INPUT_METHOD_VISIBLE_HEIGHT = "miui.intent.extra.input_method_visible_height";
    public static final String FREEFORM_INPUT_CONSUMER = "freeform_input_consumer";
    private static final String FULLSCREEN_STATE_CROSS_SAFE_AREA = "crossSafeArea";
    private static final String FULLSCREEN_STATE_TO_HOME = "toHome";
    private static final String FULLSCREEN_STATE_TO_RECENTS = "toRecents";
    private static final int MSG_ACTION_RECEIVER = 0;
    private static final String TAG = "MiuiFreeFormGestureController";
    private boolean mCouldBeenFocusWindow = true;
    private boolean mDisableScreenRotation = false;
    DisplayContent mDisplayContent;
    private IntentFilter mFilter = new IntentFilter();
    private FreeFormReceiver mFreeFormReceiver;
    private GestureHelperDeathRecipient mGestureHelperDeathRecipient;
    final MiuiFreeFormGesturePointerEventListener mGestureListener;
    GestureThreadHandler mHandler;
    private HandlerThread mHandlerThread;
    KeyguardManager mKeyguardManager;
    private IMiuiFreeFormGestureControlHelper mMiuiFreeFormGestureControlHelper;
    private IntentFilter mPackageFilter = new IntentFilter();
    WindowManagerService mService;
    final TaskStackListener mTaskStackListener = new TaskStackListener() {
        public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(MiuiFreeFormGestureController.TAG, "onTaskRemovalStarted");
            }
        }

        public void onTaskRemoved(int taskId) throws RemoteException {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(MiuiFreeFormGestureController.TAG, "onTaskRemoved");
            }
        }

        public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(MiuiFreeFormGestureController.TAG, "onTaskDescriptionChanged" + taskInfo);
            }
        }
    };
    MiuiFreeFormWindowController mWindowController;
    private IForegroundWindowListener.Stub mWindowListener = new IForegroundWindowListener.Stub() {
        public void onForegroundWindowChanged(ForegroundInfo foregroundInfo) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(MiuiFreeFormGestureController.TAG, "onForegroundWindowChanged: " + foregroundInfo.mForegroundPackageName);
            }
        }
    };

    MiuiFreeFormGestureController(WindowManagerService service) {
        this.mService = service;
        this.mDisplayContent = service.getDefaultDisplayContentLocked();
        this.mHandlerThread = new HandlerThread(TAG, -4);
        this.mHandlerThread.start();
        this.mHandler = new GestureThreadHandler(this.mHandlerThread.getLooper());
        this.mFreeFormReceiver = new FreeFormReceiver();
        this.mFilter.addAction(ACTION_FULLSCREEN_STATE_CHANGE);
        this.mFilter.addAction(ACTION_INPUT_METHOD_VISIBLE_HEIGHT_CHANGED);
        this.mFilter.addAction(ACTION_DOWNWARD_MOVEMENT_SMALLWINDOW);
        this.mFilter.addAction(ACTION_UPWARD_MOVEMENT_SMALLWINDOW);
        this.mFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        this.mFilter.addAction(ACTION_KEYCODE_BACK);
        this.mFilter.addAction(ACTION_LAUNCH_FULLSCREEN_FROM_FREEFORM);
        this.mPackageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        this.mPackageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        this.mPackageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mService.mContext.registerReceiver(this.mFreeFormReceiver, this.mFilter);
        this.mService.mContext.registerReceiver(this.mFreeFormReceiver, this.mPackageFilter);
        this.mGestureListener = new MiuiFreeFormGesturePointerEventListener(service, this);
        this.mWindowController = new MiuiFreeFormWindowController(service.mContext, this);
        try {
            ActivityTaskManager.getService().registerTaskStackListener(this.mTaskStackListener);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to call registerTaskStackListener", e);
        }
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
        setFreeformPackageName((String) null);
        MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = MiuiMultiWindowUtils.checkAppInstalled(this.mService.mContext, "com.phonetest.stresstest");
        if (!MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning) {
            MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = MiuiMultiWindowUtils.checkAppInstalled(this.mService.mContext, "com.android.commands.monkey");
        }
        this.mKeyguardManager = (KeyguardManager) service.mContext.getSystemService("keyguard");
    }

    public void unregisterEventListener() {
        try {
            Slog.d(TAG, "unregisterEventListener");
            this.mDisplayContent.unregisterPointerEventListener(this.mGestureListener);
        } catch (Exception e) {
        }
    }

    public void setDisableScreenRotation(boolean disableScreenRotation) {
        this.mDisableScreenRotation = disableScreenRotation;
    }

    public boolean isScreenRotationDisabled() {
        return this.mDisableScreenRotation;
    }

    private void registerEventListener() {
        try {
            Slog.d(TAG, "registerEventListener");
            unregisterEventListener();
            this.mDisplayContent.registerPointerEventListener(this.mGestureListener);
        } catch (Exception e) {
            Slog.d(TAG, "registerEventListener " + e);
            this.mDisplayContent.registerPointerEventListener(this.mGestureListener);
        }
        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
        MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = 0;
        miuiFreeFormGesturePointerEventListener.showCaptionView();
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", 0, -2);
    }

    /* access modifiers changed from: package-private */
    public void registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper helper) {
        Slog.d(TAG, "registerMiuiFreeFormGestureControlHelper helper=" + helper);
        if (helper != null) {
            unregisterMiuiFreeFormGestureControlHelper();
            this.mGestureHelperDeathRecipient = new GestureHelperDeathRecipient();
            try {
                helper.asBinder().linkToDeath(this.mGestureHelperDeathRecipient, 0);
                this.mMiuiFreeFormGestureControlHelper = helper;
            } catch (RemoteException e) {
                Slog.d(TAG, "miuiFreeFormGestureControlHelper linkToDeath failed." + e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterMiuiFreeFormGestureControlHelper() {
        Slog.d(TAG, "unregisterMiuiFreeFormGestureControlHelper");
        try {
            unlinkToDeathMiuiGestureHelper();
            this.mMiuiFreeFormGestureControlHelper = null;
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void unlinkToDeathMiuiGestureHelper() {
        Slog.d(TAG, "unlinkToDeathMiuiGestureHelper");
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null && this.mGestureHelperDeathRecipient != null) {
            iMiuiFreeFormGestureControlHelper.asBinder().unlinkToDeath(this.mGestureHelperDeathRecipient, 0);
            this.mGestureHelperDeathRecipient = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void setFreeformPackageName(final String packageName) {
        this.mHandler.post(new Runnable() {
            public void run() {
                Settings.Secure.putStringForUser(MiuiFreeFormGestureController.this.mService.mContext.getContentResolver(), "freeform_package_name", packageName, -2);
            }
        });
    }

    public void startShowFullScreenWindow() {
        this.mGestureListener.startShowFullScreenWindow();
    }

    public void startCloseFreeFormWindow() {
        Slog.d(TAG, "startCloseFreeFormWindow");
        for (AppWindowToken aToken : this.mGestureListener.mFreeFormAppTokens) {
            aToken.mIgnoreInput = false;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mGestureListener.unregisterInputConsumer();
        unregisterEventListener();
        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
        MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = -1;
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
        setFreeformPackageName((String) null);
        this.mWindowController.startRemoveOverLayViewAnimation();
        this.mWindowController.removeHotSpotView();
        this.mWindowController.removeOpenCloseTipWindow();
        DisplayContent displayContent = this.mDisplayContent;
        DisplayContent.mFreeFormWindowShowed = false;
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                ActivityStack activityStack;
                TaskStack taskStack = MiuiFreeFormGestureController.this.mDisplayContent.getTopStack();
                if (taskStack != null && taskStack.getWindowingMode() == 5 && (activityStack = taskStack.mActivityStack) != null) {
                    ActivityRecord topActivity = activityStack.getTopActivity();
                    Rect activityBounds = new Rect();
                    if (topActivity != null) {
                        activityBounds = topActivity.getBounds();
                    }
                    MiuiFreeFormGestureController.this.startupFreeFormWindow(activityBounds);
                    Slog.d(MiuiFreeFormGestureController.TAG, "startupFreeFormWindow for relaunch activity");
                }
            }
        }, 200);
    }

    public void startupFreeFormWindow() {
        startupFreeFormWindow((Rect) null);
    }

    public void startupFreeFormWindow(Rect activityBounds) {
        Slog.d(TAG, "startupFreeFormWindow");
        this.mGestureListener.unregisterInputConsumer();
        for (AppWindowToken aToken : this.mGestureListener.mFreeFormAppTokens) {
            aToken.mIgnoreInput = false;
        }
        registerEventListener();
        DisplayContent displayContent = this.mDisplayContent;
        boolean z = true;
        DisplayContent.mFreeFormWindowShowed = true;
        if (activityBounds != null) {
            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
            if (activityBounds.width() <= activityBounds.height()) {
                z = false;
            }
            miuiFreeFormGesturePointerEventListener.mIsLandcapeFreeform = z;
        }
        this.mWindowController.removeHotSpotView();
        this.mWindowController.addHotSpotView();
        if (!this.mGestureListener.mIsPortrait) {
            this.mWindowController.removeOverlayView();
            this.mWindowController.addOverlayView();
        } else if (this.mGestureListener.mIsLandcapeFreeform) {
            this.mWindowController.removeOverlayView();
            this.mWindowController.addOverlayView();
        }
    }

    public void createFreeformSurfaceCompleted(WindowState window) {
        if (window != null) {
            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
            boolean z = true;
            if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode == 0) {
                if (Settings.Secure.getInt(this.mService.mContext.getContentResolver(), "first_use_freeform", 0) == 0) {
                    this.mGestureListener.showTipWindow(1, window.getBounds());
                    Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "first_use_freeform", 1, -2);
                }
                this.mGestureListener.setShadowRadiusParas(window, 4.0f, 0.0f, 8.0f, 249.0f, 0.05f);
                Rect windowBounds = window.getBounds();
                MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener2 = this.mGestureListener;
                if (windowBounds.width() <= windowBounds.height()) {
                    z = false;
                }
                miuiFreeFormGesturePointerEventListener2.mIsLandcapeFreeform = z;
                if (this.mGestureListener.mIsPortrait && this.mGestureListener.mIsLandcapeFreeform) {
                    this.mWindowController.removeOverlayView();
                    this.mWindowController.addOverlayView();
                    MiuiFreeFormWindowController.DropWindowType = 0;
                    return;
                }
                return;
            }
            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener3 = this.mGestureListener;
            if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode == 1) {
                this.mGestureListener.setShadowRadiusParas(window, 4.0f, 0.0f, 8.0f, 249.0f, 0.03f);
            }
        }
    }

    public void removeFreeformSurface() {
        Slog.d(TAG, "removeFreeformSurface");
        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
        if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode == 1) {
            Slog.d(TAG, "removeFreeformSurface： mCurrentWindowMode = GESTURE_WINDOWING_MODE_SMALL_FREEFORM");
            this.mGestureListener.startShowFreeFormWindow();
        }
    }

    public void notifyFreeformCouldBeenFocusWindow(boolean focus) {
        this.mCouldBeenFocusWindow = focus;
        this.mService.updateFocusedWindowLocked(0, true);
    }

    public boolean isFreeformCouldBeenFocusWindow() {
        return this.mCouldBeenFocusWindow;
    }

    public void launchSmallFreeFormWindow() {
        Slog.d(TAG, "launchSmallFreeFormWindow");
        try {
            Slog.d(TAG, "registerEventListener");
            this.mDisplayContent.registerPointerEventListener(this.mGestureListener);
            this.mWindowController.removeHotSpotView();
            this.mWindowController.addHotSpotView();
        } catch (Exception e) {
            Slog.d(TAG, "registerEventListener " + e);
        }
        this.mGestureListener.launchSmallFreeFormWindow();
        DisplayContent displayContent = this.mDisplayContent;
        DisplayContent.mFreeFormWindowShowed = true;
    }

    public void setRequestedOrientation(int requestedOrientation, TaskRecord taskRecord) {
        this.mGestureListener.setRequestedOrientation(requestedOrientation, taskRecord);
    }

    public void turnFreeFormToSmallWindow() {
        Slog.d(TAG, "turnFreeFormToSmallWindow");
        this.mGestureListener.turnFreeFormToSmallWindow();
    }

    public void upwardMovementSmallWindow(int position) {
        this.mGestureListener.upwardMovementSmallWindow(position);
    }

    public void downwardMovementSmallWindow(int position) {
        this.mGestureListener.downwardMovementSmallWindow(position);
    }

    public void inputMethodVisibleChanged(int inputMethodHeight) {
        this.mGestureListener.inputMethodVisibleChanged(inputMethodHeight);
    }

    public Rect getSmallFreeFormWindowBounds() {
        return this.mGestureListener.getSmallFreeFormWindowBounds();
    }

    public void setSmallFreeFormWindowBounds(Rect smallWindowBounds) {
        this.mGestureListener.setSmallFreeFormWindowBounds(smallWindowBounds);
    }

    public void displayConfigurationChange(DisplayContent displayContent, Configuration configuration) {
        Slog.d(TAG, "displayConfigurationChange");
        if (displayContent.getDisplayId() == 0) {
            this.mGestureListener.updateScreenParams(displayContent, configuration);
        }
    }

    public void showScreenSurface() {
        this.mGestureListener.showScreenSurface();
    }

    public void hideScreenSurface() {
        this.mGestureListener.hideScreenSurface();
    }

    public void notifyFullScreenWidnowModeStart() {
        Slog.d(TAG, "notifyFullScreenWidnowModeStart");
        setFreeformPackageName((String) null);
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.notifyFullScreenWidnowModeStart();
                DisplayContent displayContent = this.mDisplayContent;
                DisplayContent.mFreeFormWindowShowed = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyExitFreeFormApplicationStart() {
        Slog.d(TAG, "notifyExitFreeFormApplicationStart");
        setFreeformPackageName((String) null);
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.notifyExitFreeFormApplicationStart();
                DisplayContent displayContent = this.mDisplayContent;
                DisplayContent.mFreeFormWindowShowed = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyShowCaptionView() {
        Slog.d(TAG, "notifyShowCaptionView" + this.mMiuiFreeFormGestureControlHelper);
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.showCaptionView();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyHideCaptionView() {
        Slog.d(TAG, "notifyHideCaptionView" + this.mMiuiFreeFormGestureControlHelper);
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.hideCaptionView();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyExitSmallFreeFormApplicationStart() {
        setFreeformPackageName((String) null);
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.notifyExitSmallFreeFormApplicationStart();
                DisplayContent displayContent = this.mDisplayContent;
                DisplayContent.mFreeFormWindowShowed = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyFreeFormApplicationResizeStart() {
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.notifyFreeFormApplicationResizeStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyFreeFormApplicationResizeEnd(long resizeTime) {
        IMiuiFreeFormGestureControlHelper iMiuiFreeFormGestureControlHelper = this.mMiuiFreeFormGestureControlHelper;
        if (iMiuiFreeFormGestureControlHelper != null) {
            try {
                iMiuiFreeFormGestureControlHelper.notifyFreeFormApplicationResizeEnd(resizeTime);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private final class GestureHelperDeathRecipient implements IBinder.DeathRecipient {
        private GestureHelperDeathRecipient() {
        }

        public void binderDied() {
            MiuiFreeFormGestureController.this.unlinkToDeathMiuiGestureHelper();
            MiuiFreeFormGestureController.this.unregisterEventListener();
            MiuiFreeFormGestureController.this.mWindowController.removeOpenCloseTipWindow();
            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = MiuiFreeFormGestureController.this.mGestureListener;
            MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = -1;
            Settings.Secure.putIntForUser(MiuiFreeFormGestureController.this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
            MiuiFreeFormGestureController.this.setFreeformPackageName((String) null);
        }
    }

    private final class FreeFormReceiver extends BroadcastReceiver {
        private FreeFormReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            MiuiFreeFormGestureController.this.handleFreeFormReceiver(intent);
        }
    }

    /* access modifiers changed from: private */
    public void handleFreeFormReceiver(Intent intent) {
        try {
            Slog.d(TAG, "handleFreeFormReceiver intent=" + intent);
            this.mWindowController.removeOpenCloseTipWindow();
            String action = intent.getAction();
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                Uri data = intent.getData();
                if (data != null) {
                    String packageName = data.getSchemeSpecificPart();
                    if ("com.phonetest.stresstest".equals(packageName) || "com.android.commands.monkey".equals(packageName)) {
                        MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = true;
                    }
                } else {
                    return;
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                Uri data2 = intent.getData();
                if (data2 != null) {
                    String packageName2 = data2.getSchemeSpecificPart();
                    if ("com.phonetest.stresstest".equals(packageName2)) {
                        if (MiuiMultiWindowUtils.checkAppInstalled(this.mService.mContext, "com.android.commands.monkey")) {
                            MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = true;
                        } else {
                            MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = false;
                        }
                    } else if ("com.android.commands.monkey".equals(packageName2)) {
                        if (MiuiMultiWindowUtils.checkAppInstalled(this.mService.mContext, "com.phonetest.stresstest")) {
                            MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = true;
                        } else {
                            MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning = false;
                        }
                    }
                } else {
                    return;
                }
            }
            if (!MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning && this.mDisplayContent.isStackVisible(5)) {
                MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
                if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode == 0) {
                    if (ACTION_FULLSCREEN_STATE_CHANGE.equals(action)) {
                        String stateName = intent.getExtras().getString(EXTRA_FULLSCREEN_STATE_NAME);
                        if ((FULLSCREEN_STATE_CROSS_SAFE_AREA.equals(stateName) || FULLSCREEN_STATE_TO_HOME.equals(stateName) || FULLSCREEN_STATE_TO_RECENTS.equals(stateName)) && this.mGestureListener.mTopFreeFormAppToken != null) {
                            notifyHideCaptionView();
                            this.mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    if (MiuiFreeFormGestureController.this.mGestureListener.mTopFreeFormAppToken == null) {
                                        MiuiFreeFormGestureController.this.mGestureListener.synchronizeFreeFormWindowInfo();
                                    }
                                    MiuiFreeFormGestureController.this.mService.mTaskSnapshotController.handleSnapshotTaskByGesture(MiuiFreeFormGestureController.this.mGestureListener.mTopFreeFormAppToken);
                                    MiuiFreeFormGestureController.this.turnFreeFormToSmallWindow();
                                }
                            }, 130);
                        }
                    } else if (!ACTION_INPUT_METHOD_VISIBLE_HEIGHT_CHANGED.equals(action)) {
                        if (!ACTION_DOWNWARD_MOVEMENT_SMALLWINDOW.equals(action)) {
                            if (!ACTION_UPWARD_MOVEMENT_SMALLWINDOW.equals(action)) {
                                if (ACTION_LAUNCH_FULLSCREEN_FROM_FREEFORM.equals(action)) {
                                    this.mGestureListener.startFullScreenFromFreeFormAnimation();
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener2 = this.mGestureListener;
                if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode != 1) {
                    ACTION_KEYCODE_BACK.equals(action);
                } else if (ACTION_LAUNCH_FULLSCREEN_FROM_FREEFORM.equals(action)) {
                    this.mGestureListener.startFullScreenFromSmallAnimation();
                } else if (ACTION_INPUT_METHOD_VISIBLE_HEIGHT_CHANGED.equals(action)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        inputMethodVisibleChanged(bundle.getInt(EXTRA_INPUT_METHOD_VISIBLE_HEIGHT));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final class GestureThreadHandler extends Handler {
        GestureThreadHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
        }
    }

    public int getCurrentFreeFormWindowMode() {
        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mGestureListener;
        return MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String[] args) {
        if (args == null || args.length <= 1) {
            pw.println("dump of freeform gesture");
            return;
        }
        String next = args[1];
        if ("0".equals(next)) {
            DEBUG = false;
        } else if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(next)) {
            DEBUG = true;
        } else {
            pw.println("unknown cmd " + next);
        }
    }
}
