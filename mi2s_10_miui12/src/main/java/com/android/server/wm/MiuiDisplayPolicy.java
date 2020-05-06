package com.android.server.wm;

import android.app.MiuiStatusBarManager;
import android.content.res.Resources;

public class MiuiDisplayPolicy extends BaseMiuiDisplayPolicy {
    protected static final int NAV_BAR_BOTTOM = 0;
    protected static final int NAV_BAR_LEFT = 2;
    protected static final int NAV_BAR_RIGHT = 1;
    private StatusBarController mStatusBarController;

    MiuiDisplayPolicy(WindowManagerService service, DisplayContent displayContent) {
        super(service, displayContent);
    }

    /* access modifiers changed from: package-private */
    public void setStatusBarController(StatusBarController statusBarController) {
        this.mStatusBarController = statusBarController;
    }

    private boolean isTransparentAllowed(WindowState win) {
        StatusBarController statusBarController = this.mStatusBarController;
        return statusBarController == null || statusBarController.isTransparentAllowed(win);
    }

    public void systemReady() {
        super.systemReady();
        systemReadyInternal();
    }

    public void onConfigurationChanged() {
        super.onConfigurationChanged();
        updateNavigationBarWidth();
    }

    /* access modifiers changed from: package-private */
    public int getExtraSystemUiVisibility(WindowState win, float maxAspect) {
        WindowState fullscreenTransWin;
        super.getExtraSystemUiVisibility(win, maxAspect);
        boolean dockedDrawsStatusBarBackground = true;
        boolean forceOpaqueStatusBar = this.mForceShowSystemBars && !this.mForceStatusBarFromKeyguard;
        if (isStatusBarKeyguard()) {
            fullscreenTransWin = this.mStatusBar;
        } else {
            fullscreenTransWin = this.mTopFullscreenOpaqueWindowState;
        }
        boolean fullscreenDrawsStatusBarBackground = isTransparentAllowed(this.mTopFullscreenOpaqueWindowState) && ((drawsSystemBarBackground(this.mTopFullscreenOpaqueWindowState) && (getExtraWindowSystemUiVis(fullscreenTransWin) & 1073741824) == 0) || forcesDrawStatusBarBackground(this.mTopFullscreenOpaqueWindowState));
        int dockedVis = getExtraWindowSystemUiVis(this.mTopDockedOpaqueWindowState);
        if (!isTransparentAllowed(this.mTopDockedOpaqueWindowState) || ((!drawsSystemBarBackground(this.mTopDockedOpaqueWindowState) || (1073741824 & dockedVis) != 0) && !forcesDrawStatusBarBackground(this.mTopDockedOpaqueWindowState))) {
            dockedDrawsStatusBarBackground = false;
        }
        if (fullscreenDrawsStatusBarBackground && dockedDrawsStatusBarBackground) {
            return (0 | 8) & -1073741825;
        }
        if (fullscreenTransWin != this.mStatusBar || forceOpaqueStatusBar) {
            return 0 & -1073741833;
        }
        return 0;
    }

    private int getExtraWindowSystemUiVis(WindowState transWin) {
        int vis = 0;
        if (transWin != null) {
            vis = 0 | transWin.getAttrs().extraFlags;
            if (transWin.getAttrs().type == 3) {
                vis |= 1;
            }
        }
        return MiuiStatusBarManager.getSystemUIVisibilityFlags(vis);
    }

    private boolean drawsSystemBarBackground(WindowState win) {
        return win == null || (win.getAttrs().flags & Integer.MIN_VALUE) != 0;
    }

    private boolean forcesDrawStatusBarBackground(WindowState win) {
        return win == null || (win.getAttrs().privateFlags & 131072) != 0;
    }

    public WindowState getKeyguardWindowState() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void updateNavigationBarWidth() {
        int i;
        Resources res = getCurrentUserResources();
        DisplayRotation displayRotation = this.mDisplayContent.getDisplayRotation();
        int portraitRotation = displayRotation.getPortraitRotation();
        int upsideDownRotation = displayRotation.getUpsideDownRotation();
        int landscapeRotation = displayRotation.getLandscapeRotation();
        int seascapeRotation = displayRotation.getSeascapeRotation();
        int[] iArr = this.mNavigationBarWidthForRotationDefault;
        int[] iArr2 = this.mNavigationBarWidthForRotationDefault;
        int[] iArr3 = this.mNavigationBarWidthForRotationDefault;
        int[] iArr4 = this.mNavigationBarWidthForRotationDefault;
        if (this.mSupportFsg) {
            i = 0;
        } else {
            i = res.getDimensionPixelSize(17105309);
        }
        iArr4[seascapeRotation] = i;
        iArr3[landscapeRotation] = i;
        iArr2[upsideDownRotation] = i;
        iArr[portraitRotation] = i;
    }
}
