package com.android.server.wm;

import android.app.MiuiStatusBarManager;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Slog;
import android.view.WindowManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.policy.WindowManagerPolicy;
import com.miui.server.AutoDisableScreenButtonsManager;
import miui.util.CustomizeUtil;

public abstract class BaseMiuiDisplayPolicy extends DisplayPolicy {
    static final int MSG_INIT_OBSERVER = 1;
    private static final String NAVIGATION_BAR_WINDOW_LOADED = "navigation_bar_window_loaded";
    private boolean mEnableNotchConfig;
    protected Handler mHandler;
    protected HandlerThread mHandlerThread = new HandlerThread("DisplayPolicy");
    private String mPackage;
    protected MiuiSettingsObserver mSettingsObserver;
    private Binder mStatusBarDisableToken = new Binder();
    IStatusBarService mStatusBarService;
    private boolean mStatusBarVisible = true;
    protected boolean mSupportFsg = MiuiSettings.Global.getBoolean(this.mContext.getContentResolver(), "force_fsg_nav_bar");
    /* access modifiers changed from: private */
    public Binder mWindowFlagBinder = new Binder();
    /* access modifiers changed from: private */
    public Binder mWindowStatusBinder = new Binder();

    /* access modifiers changed from: protected */
    public abstract WindowState getKeyguardWindowState();

    /* access modifiers changed from: protected */
    public void updateNavigationBarWidth() {
    }

    BaseMiuiDisplayPolicy(WindowManagerService service, DisplayContent displayContent) {
        super(service, displayContent);
        this.mHandlerThread.start();
        this.mHandler = new H(this.mHandlerThread.getLooper());
        this.mHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: protected */
    public void systemReadyInternal() {
        MiuiSettingsObserver miuiSettingsObserver = this.mSettingsObserver;
        if (miuiSettingsObserver != null) {
            miuiSettingsObserver.onChange(false);
        }
    }

    public void removeWindowLw(WindowState win) {
        if (this.mStatusBar == win) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    MiuiSettings.System.putBoolean(BaseMiuiDisplayPolicy.this.mContext.getContentResolver(), "status_bar_window_loaded", false);
                }
            });
        } else if (this.mNavigationBar == win) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    MiuiSettings.System.putBoolean(BaseMiuiDisplayPolicy.this.mContext.getContentResolver(), BaseMiuiDisplayPolicy.NAVIGATION_BAR_WINDOW_LOADED, false);
                }
            });
        }
        super.removeWindowLw(win);
    }

    public void layoutWindowLw(WindowState win, WindowState attached, DisplayFrames displayFrames) {
        WindowState windowState = win;
        DisplayFrames displayFrames2 = displayFrames;
        super.layoutWindowLw(win, attached, displayFrames);
        boolean z = true;
        if (win.getAttrs().layoutInDisplayCutoutMode != 1) {
            z = false;
        }
        boolean enableNotch = z;
        if (CustomizeUtil.HAS_NOTCH && windowState == this.mTopFullscreenOpaqueWindowState && (!TextUtils.equals(this.mPackage, win.getOwningPackage()) || this.mEnableNotchConfig != enableNotch)) {
            final String pkg = win.getOwningPackage();
            this.mPackage = pkg;
            this.mEnableNotchConfig = enableNotch;
            final boolean enableNotchConfig = enableNotch;
            this.mHandler.post(new Runnable() {
                public void run() {
                    try {
                        IStatusBarService statusbar = BaseMiuiDisplayPolicy.this.getStatusBarService();
                        Bundle ext = new Bundle();
                        ext.putString(SplitScreenReporter.STR_PKG, pkg);
                        ext.putBoolean("enable_config", enableNotchConfig);
                        if (statusbar != null) {
                            statusbar.setStatus(0, BaseMiuiDisplayPolicy.this.mWindowStatusBinder, "upate_specail_mode", ext);
                        }
                    } catch (RemoteException e) {
                        Slog.e(DisplayPolicy.TAG, "RemoteException", e);
                        BaseMiuiDisplayPolicy.this.mStatusBarService = null;
                    }
                }
            });
        }
        if ((windowState != this.mStatusBar || canReceiveInput(win)) && windowState != this.mNavigationBar) {
            WindowFrames windowFrames = win.getWindowFrames();
            Rect pf = windowFrames.mParentFrame;
            Rect df = windowFrames.mDisplayFrame;
            Rect of = windowFrames.mOverscanFrame;
            Rect dcf = windowFrames.mDecorFrame;
            Rect cf = windowFrames.mContentFrame;
            Rect vf = windowFrames.mVisibleFrame;
            Rect sf = windowFrames.mStableFrame;
            WindowManager.LayoutParams attrs = win.getAttrs();
            int fl = PolicyControl.getWindowFlags(windowState, attrs);
            int sysUiFl = PolicyControl.getSystemUiVisibility(windowState, (WindowManager.LayoutParams) null);
            boolean isDefaultDisplay = win.isDefaultDisplay();
            int sim = attrs.softInputMode;
            int adjust = sim & 240;
            int i = sim;
            boolean z2 = enableNotch;
            if ((attrs.type == 2004 || attrs.type == 2009 || forceLayoutHideNavigation(win)) && isDefaultDisplay && (fl & 65792) == 65792 && attached == null && originalCanHideNavigationBar() && (sysUiFl & 512) != 0) {
                pf.set(displayFrames2.mOverscan);
                df.set(displayFrames2.mOverscan);
                of.set(displayFrames2.mUnrestricted);
                dcf.right = displayFrames2.mUnrestricted.right;
                dcf.bottom = displayFrames2.mUnrestricted.bottom;
                cf.set(displayFrames2.mContent);
                sf.set(displayFrames2.mStable);
                if ((fl & 1024) != 0) {
                    cf.set(displayFrames2.mRestricted);
                } else if (win.isVoiceInteraction()) {
                    cf.set(displayFrames2.mVoiceContent);
                } else if (adjust != 16) {
                    cf.set(displayFrames2.mDock);
                } else {
                    cf.set(displayFrames2.mContent);
                }
                if ((sysUiFl & 256) != 0) {
                    if ((fl & 1024) != 0) {
                        cf.intersectUnchecked(displayFrames2.mStableFullscreen);
                    } else {
                        cf.intersectUnchecked(displayFrames2.mStable);
                    }
                }
                if (adjust != 48) {
                    vf.set(displayFrames2.mCurrent);
                } else {
                    vf.set(cf);
                }
                cf.intersectUnchecked(displayFrames2.mDisplayCutoutSafe);
                win.computeFrameLw();
            }
        }
    }

    private boolean originalCanHideNavigationBar() {
        return this.mHasNavigationBar;
    }

    private boolean canReceiveInput(WindowState win) {
        if (!(((win.getAttrs().flags & 8) != 0) ^ ((win.getAttrs().flags & 131072) != 0))) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean forceShowNavigationBar() {
        return CustomizeUtil.needChangeSize() && !forceLayoutHideNavigation();
    }

    private boolean forceLayoutHideNavigation() {
        WindowState win;
        if (this.mFocusedWindow != null) {
            win = this.mFocusedWindow;
        } else {
            win = this.mTopFullscreenOpaqueWindowState;
        }
        return forceLayoutHideNavigation(win);
    }

    private boolean forceLayoutHideNavigation(WindowState win) {
        return this.mHasNavigationBar && win != null && CustomizeUtil.forceLayoutHideNavigation(win.getOwningPackage());
    }

    /* access modifiers changed from: package-private */
    public int getExtraSystemUiVisibility(WindowState win) {
        return getExtraSystemUiVisibility(win, 0.0f);
    }

    /* access modifiers changed from: package-private */
    public int getExtraSystemUiVisibility(WindowState win, float maxAspect) {
        WindowManagerPolicy.WindowState keyguard = getKeyguardWindowState();
        WindowManagerPolicy.WindowState transWin = (keyguard == null || !keyguard.isVisibleLw()) ? this.mTopFullscreenOpaqueWindowState : keyguard;
        int flags = 0;
        if (transWin != null) {
            flags = 0 | transWin.getAttrs().extraFlags;
            if (transWin.getAttrs().type == 3) {
                flags |= 1;
            }
        }
        int flag = MiuiStatusBarManager.getDisabledFlags(flags);
        WindowState winState = chooseNavigationColorWindowLw(this.mTopFullscreenOpaqueWindowState, this.mTopFullscreenOpaqueOrDimmingWindowState, (WindowState) this.mWindowManagerFuncs.getInputMethodWindowLw(), this.mNavigationBarPosition);
        if (this.mHasNavigationBar && winState != null) {
            if (winState == this.mWindowManagerFuncs.getInputMethodWindowLw() || winState == this.mTopFullscreenOpaqueWindowState) {
                flag = ((PolicyControl.getSystemUiVisibility(winState, (WindowManager.LayoutParams) null) & 16) == 16 || (winState.getAttrs().extraFlags & 64) == 64) ? flag | 512 : flag & -513;
            } else if (winState == this.mTopFullscreenOpaqueOrDimmingWindowState && winState.isDimming()) {
                flag &= -513;
            }
        }
        if (this.mHasNavigationBar && this.mTopFullscreenOpaqueWindowState == null && this.mFocusedWindow != null && (this.mFocusedWindow.getAttrs().extraFlags | 64) != 0) {
            flag |= 512;
        }
        if (this.mHasNavigationBar && transWin != null && this.mTopFullscreenOpaqueWindowState == transWin && CustomizeUtil.isRestrict(maxAspect)) {
            flag &= -577;
        }
        if (!(win == null || (win.getAttrs().extraFlags & 32768) == 0)) {
            flag |= 2048;
        }
        final int disabledFlag = flag;
        this.mHandler.post(new Runnable() {
            public void run() {
                try {
                    IStatusBarService statusbar = BaseMiuiDisplayPolicy.this.getStatusBarService();
                    if (statusbar != null) {
                        statusbar.disable(disabledFlag, BaseMiuiDisplayPolicy.this.mWindowFlagBinder, BaseMiuiDisplayPolicy.this.mContext.getPackageName());
                    }
                } catch (RemoteException e) {
                    Slog.e(DisplayPolicy.TAG, "RemoteException", e);
                    BaseMiuiDisplayPolicy.this.mStatusBarService = null;
                }
            }
        });
        return MiuiStatusBarManager.getSystemUIVisibilityFlags(flags);
    }

    public int finishPostLayoutPolicyLw() {
        int i;
        int changes = super.finishPostLayoutPolicyLw();
        if (!(this.mStatusBar == null || this.mStatusBarVisible == this.mStatusBar.isVisibleLw())) {
            this.mStatusBarVisible = this.mStatusBar.isVisibleLw();
            AutoDisableScreenButtonsManager.onStatusBarVisibilityChangeStatic(this.mStatusBarVisible);
            try {
                IStatusBarService statusbar = getStatusBarService();
                if (statusbar != null) {
                    if (this.mStatusBarVisible) {
                        i = 0;
                    } else {
                        i = 256;
                    }
                    statusbar.disable(i, this.mStatusBarDisableToken, "system");
                }
            } catch (RemoteException e) {
                Slog.e(DisplayPolicy.TAG, "RemoteException when disable status bar visible", e);
                this.mStatusBarService = null;
            }
        }
        return changes;
    }

    /* access modifiers changed from: package-private */
    public IStatusBarService getStatusBarService() {
        if (this.mStatusBarService == null) {
            this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        }
        return this.mStatusBarService;
    }

    class MiuiSettingsObserver extends ContentObserver {
        MiuiSettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            BaseMiuiDisplayPolicy.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_fsg_nav_bar"), false, this, -1);
            onChange(false);
        }

        public void onChange(boolean selfChange) {
            boolean supportFsg = MiuiSettings.Global.getBoolean(BaseMiuiDisplayPolicy.this.mContext.getContentResolver(), "force_fsg_nav_bar");
            if (supportFsg != BaseMiuiDisplayPolicy.this.mSupportFsg) {
                BaseMiuiDisplayPolicy baseMiuiDisplayPolicy = BaseMiuiDisplayPolicy.this;
                baseMiuiDisplayPolicy.mSupportFsg = supportFsg;
                baseMiuiDisplayPolicy.updateNavigationBarWidth();
            }
        }
    }

    private class H extends Handler {
        H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                BaseMiuiDisplayPolicy baseMiuiDisplayPolicy = BaseMiuiDisplayPolicy.this;
                baseMiuiDisplayPolicy.mSettingsObserver = new MiuiSettingsObserver(this);
                BaseMiuiDisplayPolicy.this.mSettingsObserver.observe();
            }
        }
    }
}
