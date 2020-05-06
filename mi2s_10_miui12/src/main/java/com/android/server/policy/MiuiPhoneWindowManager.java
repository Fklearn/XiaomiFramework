package com.android.server.policy;

import android.app.ActivityManagerNative;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.MiuiStatusBarManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.hardware.input.InputManager;
import android.miui.R;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.LocalServices;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wm.AccountHelper;
import com.android.server.wm.DisplayPolicy;
import com.miui.server.AccessController;
import java.lang.reflect.Method;
import miui.app.AlertDialog;
import miui.os.Build;
import miui.process.ProcessManagerInternal;
import miui.view.MiuiSecurityPermissionHandler;

public class MiuiPhoneWindowManager extends BaseMiuiPhoneWindowManager {
    private static final int ACTION_NOT_PASS_TO_USER = 0;
    private static final int ACTION_PASS_TO_USER = 1;
    private static final int FINGERPRINT_NAV_ACTION_DEFAULT = -1;
    private static final int FINGERPRINT_NAV_ACTION_HOME = 1;
    private static final int FINGERPRINT_NAV_ACTION_NONE = 0;
    protected static final int NAV_BAR_BOTTOM = 0;
    protected static final int NAV_BAR_LEFT = 2;
    protected static final int NAV_BAR_RIGHT = 1;
    private static final boolean SUPPORT_POWERFP = SystemProperties.getBoolean("ro.hardware.fp.sideCap", false);
    private long interceptPowerKeyTimeByDpadCenter = -1;
    /* access modifiers changed from: private */
    public AccountHelper mAccountHelper;
    private BiometricManager mBiometricManager;
    private int mDisplayHeight;
    private int mDisplayRotation;
    private int mDisplayWidth;
    /* access modifiers changed from: private */
    public AlertDialog mFpNavCenterActionChooseDialog = null;
    private Method mGetFpLockoutModeMethod = null;
    /* access modifiers changed from: private */
    public MiuiSecurityPermissionHandler mMiuiSecurityPermissionHandler;
    /* access modifiers changed from: private */
    public MIUIWatermarkCallback mPhoneWindowCallback;

    public interface MIUIWatermarkCallback {
        void onHideWatermark();

        void onShowWatermark();
    }

    public void init(Context context, IWindowManager windowManager, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        super.init(context, windowManager, windowManagerFuncs);
        initInternal(context, windowManager, windowManagerFuncs);
    }

    public void systemReady() {
        super.systemReady();
        this.mMiuiKeyguardDelegate = new MiuiKeyguardServiceDelegate(this, this.mKeyguardDelegate, this.mPowerManager);
        this.mBiometricManager = (BiometricManager) this.mContext.getSystemService(BiometricManager.class);
        systemReadyInternal();
        if (Build.IS_PRIVATE_BUILD || Build.IS_PRIVATE_WATER_MARKER) {
            this.mAccountHelper = AccountHelper.getInstance();
            this.mAccountHelper.registerAccountListener(this.mContext, new AccountHelper.AccountCallback() {
                public void onXiaomiAccountLogin() {
                    if (MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler != null) {
                        MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler.handleAccountLogin();
                    }
                }

                public void onXiaomiAccountLogout() {
                    if (MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler != null) {
                        MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler.handleAccountLogout();
                    }
                }

                public void onWifiSettingFinish() {
                    if (MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler != null) {
                        MiuiPhoneWindowManager.this.mMiuiSecurityPermissionHandler.handleWifiSettingFinish();
                    }
                }
            });
            this.mMiuiSecurityPermissionHandler = new MiuiSecurityPermissionHandler(this.mContext, new MiuiSecurityPermissionHandler.PermissionViewCallback() {
                public void onShowWaterMarker() {
                    if (MiuiPhoneWindowManager.this.mPhoneWindowCallback != null) {
                        MiuiPhoneWindowManager.this.mPhoneWindowCallback.onShowWatermark();
                    }
                }

                public void onAddAccount() {
                    MiuiPhoneWindowManager.this.mAccountHelper.addAccount(MiuiPhoneWindowManager.this.mContext);
                }

                public void onListenAccount(int mode) {
                    MiuiPhoneWindowManager.this.mAccountHelper.ListenAccount(mode);
                }

                public void onUnListenAccount(int mode) {
                    MiuiPhoneWindowManager.this.mAccountHelper.UnListenAccount(mode);
                }

                public void onHideWaterMarker() {
                    if (MiuiPhoneWindowManager.this.mPhoneWindowCallback != null) {
                        MiuiPhoneWindowManager.this.mPhoneWindowCallback.onHideWatermark();
                    }
                }

                public void onListenPermission() {
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void launchRecentPanelInternal() {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.toggleRecentApps();
        }
    }

    /* access modifiers changed from: protected */
    public void preloadRecentAppsInternal() {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.preloadRecentApps();
        }
    }

    /* access modifiers changed from: protected */
    public void toggleSplitScreenInternal() {
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            statusbar.toggleSplitScreen();
        }
    }

    /* access modifiers changed from: protected */
    public void launchAssistActionInternal(String hint, Bundle args) {
        if (hint != null) {
            args.putBoolean(hint, true);
        }
        StatusBarManagerInternal statusbar = getStatusBarManagerInternal();
        if (statusbar != null) {
            Slog.i(DisplayPolicy.TAG, "launch Google Assist");
            statusbar.startAssist(args);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isScreenOnInternal() {
        return isScreenOn();
    }

    /* access modifiers changed from: protected */
    public void finishActivityInternal(IBinder token, int code, Intent data) throws RemoteException {
        ActivityManagerNative.getDefault().finishActivity(token, code, data, 0);
    }

    /* access modifiers changed from: protected */
    public void forceStopPackage(String packageName, int OwningUserId, String reason) {
        ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).forceStopPackage(packageName, OwningUserId, reason);
    }

    public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags) {
        return interceptKeyBeforeQueueingInternal(event, policyFlags, (536870912 & policyFlags) != 0);
    }

    /* access modifiers changed from: protected */
    public int callSuperInterceptKeyBeforeQueueing(KeyEvent event, int policyFlags, boolean isScreenOn) {
        return super.interceptKeyBeforeQueueing(event, policyFlags);
    }

    /* access modifiers changed from: protected */
    public WindowManagerPolicy.WindowState getKeyguardWindowState() {
        return null;
    }

    /* access modifiers changed from: protected */
    public int intercept(KeyEvent event, int policyFlags, boolean isScreenOn, int expectedResult) {
        super.intercept(event, policyFlags, isScreenOn, expectedResult);
        PowerManager pm = (PowerManager) this.mContext.getSystemService("power");
        if (expectedResult == -1) {
            pm.goToSleep(SystemClock.uptimeMillis());
            return 0;
        } else if (expectedResult != 1) {
            return 0;
        } else {
            pm.wakeUp(SystemClock.uptimeMillis());
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public int getWakePolicyFlag() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public boolean screenOffBecauseOfProxSensor() {
        return false;
    }

    public int getWindowLayerFromTypeLw(int type, boolean canAddInternalSystemWindow) {
        if (type >= 1 && type <= 99) {
            return 2;
        }
        switch (type) {
            case 2000:
                return 18;
            case 2001:
            case 2033:
                return 4;
            case 2002:
                return 3;
            case 2003:
                if (canAddInternalSystemWindow) {
                    return 11;
                }
                return 10;
            case 2004:
                return 16;
            case 2005:
                return 8;
            case 2006:
                if (canAddInternalSystemWindow) {
                    return 22;
                }
                return 11;
            case 2007:
                return 9;
            case 2008:
                return 7;
            case 2009:
                return 20;
            case 2010:
                if (canAddInternalSystemWindow) {
                    return 26;
                }
                return 10;
            case 2011:
                return 14;
            case 2012:
                return 15;
            case 2013:
                return 1;
            case 2014:
                return 19;
            case 2015:
                return 31;
            case 2016:
                return 29;
            case 2017:
                return 17;
            case 2018:
                return 33;
            case 2019:
                return 23;
            case 2020:
                return 21;
            case 2021:
                return 32;
            case 2022:
                return 6;
            case 2023:
                return 13;
            case 2024:
                return 24;
            case 2026:
                return 28;
            case 2027:
                return 27;
            case 2030:
            case 2037:
                return 2;
            case 2031:
                return 5;
            case 2032:
                return 30;
            case 2034:
                return 2;
            case 2035:
                return 2;
            case 2036:
                return 25;
            case 2038:
                return 12;
            default:
                Slog.e(DisplayPolicy.TAG, "Unknown window type: " + type);
                return 2;
        }
    }

    /* access modifiers changed from: protected */
    public void onStatusBarPanelRevealed(IStatusBarService statusBarService) {
        try {
            statusBarService.onPanelRevealed(true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public boolean stopLockTaskMode() {
        try {
            IActivityTaskManager activityTaskManager = ActivityTaskManager.getService();
            if (activityTaskManager == null || !activityTaskManager.isInLockTaskMode()) {
                return false;
            }
            activityTaskManager.stopSystemLockTaskMode();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isInLockTaskMode() {
        try {
            IActivityTaskManager activityTaskManager = ActivityTaskManager.getService();
            if (activityTaskManager != null) {
                return activityTaskManager.isInLockTaskMode();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isFingerPrintKey(KeyEvent event) {
        if (event.getDevice() == null || this.mFpNavEventNameList == null || !this.mFpNavEventNameList.contains(event.getDevice().getName())) {
            return false;
        }
        int keyCode = event.getKeyCode();
        if (keyCode == 22 || keyCode == 23) {
            return true;
        }
        return false;
    }

    private void processFrontFingerprintDpcenterEvent(KeyEvent event) {
        if (event.getAction() == 1) {
            if (this.mDpadCenterDown) {
                this.mDpadCenterDown = false;
                if (this.mHomeDownAfterDpCenter) {
                    this.mHomeDownAfterDpCenter = false;
                    Slog.w("BaseMiuiPhoneWindowManager", "After dpcenter & home down, ignore tap fingerprint");
                    return;
                }
            }
            if (isDeviceProvisioned() && !this.mMiuiKeyguardDelegate.isShowingAndNotHidden() && event.getEventTime() - event.getDownTime() < 300) {
                if (this.mSingleKeyUse) {
                    injectEvent(event, 4, -1);
                    return;
                }
                int action = Settings.System.getIntForUser(this.mContext.getContentResolver(), "fingerprint_nav_center_action", -1, -2);
                if (-1 == action) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            MiuiPhoneWindowManager.this.bringUpActionChooseDlg();
                        }
                    });
                } else if (1 == action) {
                    injectEvent(event, 3, -1);
                } else if (action != 0) {
                }
            }
        } else if (event.getAction() == 0) {
            this.mDpadCenterDown = true;
        }
    }

    private void processFrontFingerprintDprightEvent(KeyEvent event) {
        Slog.d("BaseMiuiPhoneWindowManager", "processFrontFingerprintDprightEvent");
    }

    private void processBackFingerprintDpcenterEvent(KeyEvent event, boolean isScreenOn) {
        if (event.getAction() == 0 && isDeviceProvisioned()) {
            boolean lockout = false;
            if (isScreenOn) {
                if (this.mFocusedWindow != null && AccessController.PACKAGE_CAMERA.equals(this.mFocusedWindow.getOwningPackage()) && !SUPPORT_POWERFP) {
                    injectEvent(event, 27, event.getDeviceId());
                }
                this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
            } else if (SUPPORT_POWERFP) {
                if (getFingerprintLockoutMode(this.mBiometricManager) != 0) {
                    lockout = true;
                }
                if (hasEnrolledFingerpirntForAuthentication() != 11 && lockout) {
                    Slog.d("BaseMiuiPhoneWindowManager", "fingerprint lockoutmode: " + lockout);
                    this.interceptPowerKeyTimeByDpadCenter = SystemClock.uptimeMillis() + 300;
                    this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "miui.policy:FINGERPRINT_DPAD_CENTER");
                }
            } else {
                this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "miui.policy:FINGERPRINT_DPAD_CENTER");
            }
        }
    }

    /* access modifiers changed from: protected */
    public int processFingerprintNavigationEvent(KeyEvent event, boolean isScreenOn) {
        int keyCode = event.getKeyCode();
        if (keyCode == 22) {
            processFrontFingerprintDprightEvent(event);
            return 0;
        } else if (keyCode != 23) {
            return 0;
        } else {
            if (!this.mFrontFingerprintSensor) {
                processBackFingerprintDpcenterEvent(event, isScreenOn);
                return 0;
            } else if (!this.mSupportTapFingerprintSensorToHome) {
                return 0;
            } else {
                processFrontFingerprintDpcenterEvent(event);
                return 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean interceptPowerKeyByFingerPrintKey() {
        return this.interceptPowerKeyTimeByDpadCenter > SystemClock.uptimeMillis();
    }

    /* access modifiers changed from: protected */
    public int hasEnrolledFingerpirntForAuthentication() {
        return this.mBiometricManager.canAuthenticate();
    }

    /* access modifiers changed from: protected */
    public int getFingerprintLockoutMode(Object bm) {
        try {
            if (this.mGetFpLockoutModeMethod == null) {
                this.mGetFpLockoutModeMethod = bm.getClass().getDeclaredMethod("getLockoutMode", new Class[0]);
            }
            return ((Integer) this.mGetFpLockoutModeMethod.invoke(bm, new Object[0])).intValue();
        } catch (Exception e) {
            Slog.e("BaseMiuiPhoneWindowManager", "getFingerprintLockoutMode function exception");
            e.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void bringUpActionChooseDlg() {
        if (this.mFpNavCenterActionChooseDialog == null) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int value;
                    if (which == -1) {
                        value = 1;
                    } else {
                        value = 0;
                    }
                    Settings.System.putIntForUser(MiuiPhoneWindowManager.this.mContext.getContentResolver(), "fingerprint_nav_center_action", value, -2);
                    if (MiuiPhoneWindowManager.this.mFpNavCenterActionChooseDialog != null) {
                        MiuiPhoneWindowManager.this.mFpNavCenterActionChooseDialog.dismiss();
                        AlertDialog unused = MiuiPhoneWindowManager.this.mFpNavCenterActionChooseDialog = null;
                    }
                }
            };
            this.mFpNavCenterActionChooseDialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.fp_nav_center_action_choose_dlg_title).setMessage(R.string.fp_nav_center_action_choose_dlg_msg).setPositiveButton(R.string.fp_nav_center_action_choose_dlg_positive_btn_text, listener).setNegativeButton(R.string.fp_nav_center_action_choose_dlg_negative_btn_text, listener).setCancelable(false).create();
            WindowManager.LayoutParams lp = this.mFpNavCenterActionChooseDialog.getWindow().getAttributes();
            lp.type = 2008;
            this.mFpNavCenterActionChooseDialog.getWindow().setAttributes(lp);
            this.mFpNavCenterActionChooseDialog.show();
        }
    }

    private void injectEvent(KeyEvent event, int injectKeyCode, int deviceId) {
        long now = SystemClock.uptimeMillis();
        long j = now;
        long j2 = now;
        int i = injectKeyCode;
        int i2 = deviceId;
        KeyEvent homeUp = new KeyEvent(j, j2, 0, i, 0, 0, i2, 0, event.getFlags(), event.getSource());
        KeyEvent homeUp2 = new KeyEvent(j, j2, 1, i, 0, 0, i2, 0, event.getFlags(), event.getSource());
        InputManager.getInstance().injectInputEvent(homeUp, 0);
        InputManager.getInstance().injectInputEvent(homeUp2, 0);
    }

    private boolean hideStatusBar(int flag, int sys) {
        if ((flag & 1024) == 0 && (sys & 4) == 0) {
            return false;
        }
        return true;
    }

    private boolean hideNavBar(int flag, int sys) {
        if ((sys & 2) == 0 && (sys & 6144) == 0) {
            return false;
        }
        return true;
    }

    private int getExtraWindowSystemUiVis(WindowManagerPolicy.WindowState transWin) {
        int vis = 0;
        if (transWin != null) {
            vis = 0 | transWin.getAttrs().extraFlags;
            if (transWin.getAttrs().type == 3) {
                vis |= 1;
            }
        }
        return MiuiStatusBarManager.getSystemUIVisibilityFlags(vis);
    }

    private boolean drawsSystemBarBackground(WindowManagerPolicy.WindowState win) {
        return win == null || (win.getAttrs().flags & Integer.MIN_VALUE) != 0;
    }

    private boolean forcesDrawStatusBarBackground(WindowManagerPolicy.WindowState win) {
        return win == null || (win.getAttrs().privateFlags & 131072) != 0;
    }

    public void registerMIUIWatermarkCallback(MIUIWatermarkCallback callback) {
        this.mPhoneWindowCallback = callback;
    }
}
