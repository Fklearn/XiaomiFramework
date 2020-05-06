package com.android.server.wm;

import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.util.BoostFramework;
import android.view.MotionEvent;
import android.view.WindowManagerPolicyConstants;

public class TaskTapPointerEventListener implements WindowManagerPolicyConstants.PointerEventListener {
    private final DisplayContent mDisplayContent;
    public BoostFramework mPerfObj = null;
    private int mPointerIconType = 1;
    private final WindowManagerService mService;
    private final Rect mTmpRect = new Rect();
    private final Region mTouchExcludeRegion = new Region();

    public TaskTapPointerEventListener(WindowManagerService service, DisplayContent displayContent) {
        this.mService = service;
        this.mDisplayContent = displayContent;
        if (this.mPerfObj == null) {
            this.mPerfObj = new BoostFramework();
        }
    }

    public void onPointerEvent(MotionEvent motionEvent) {
        BoostFramework boostFramework;
        BoostFramework boostFramework2;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            synchronized (this) {
                if (!this.mTouchExcludeRegion.contains(x, y)) {
                    this.mService.mTaskPositioningController.handleTapOutsideTask(this.mDisplayContent, x, y);
                }
            }
        } else if (actionMasked == 7 || actionMasked == 9) {
            int x2 = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            Task task = this.mDisplayContent.findTaskForResizePoint(x2, y2);
            int iconType = 1;
            if (task != null) {
                task.getDimBounds(this.mTmpRect);
                if (!this.mTmpRect.isEmpty() && !this.mTmpRect.contains(x2, y2)) {
                    int i = 1014;
                    if (x2 < this.mTmpRect.left) {
                        if (y2 < this.mTmpRect.top) {
                            i = 1017;
                        } else if (y2 > this.mTmpRect.bottom) {
                            i = 1016;
                        }
                        iconType = i;
                    } else if (x2 > this.mTmpRect.right) {
                        if (y2 < this.mTmpRect.top) {
                            i = 1016;
                        } else if (y2 > this.mTmpRect.bottom) {
                            i = 1017;
                        }
                        iconType = i;
                    } else if (y2 < this.mTmpRect.top || y2 > this.mTmpRect.bottom) {
                        iconType = 1015;
                    }
                }
            }
            if (this.mPointerIconType != iconType) {
                this.mPointerIconType = iconType;
                if (this.mPointerIconType == 1) {
                    this.mService.mH.removeMessages(55);
                    this.mService.mH.obtainMessage(55, x2, y2, this.mDisplayContent).sendToTarget();
                } else {
                    InputManager.getInstance().setPointerIconType(this.mPointerIconType);
                }
            }
        } else if (actionMasked == 10) {
            int x3 = (int) motionEvent.getX();
            int y3 = (int) motionEvent.getY();
            if (this.mPointerIconType != 1) {
                this.mPointerIconType = 1;
                this.mService.mH.removeMessages(55);
                this.mService.mH.obtainMessage(55, x3, y3, this.mDisplayContent).sendToTarget();
            }
        }
        if (ActivityStackSupervisor.mIsPerfBoostAcquired && this.mPerfObj != null) {
            if (ActivityStackSupervisor.mPerfHandle > 0) {
                this.mPerfObj.perfLockReleaseHandler(ActivityStackSupervisor.mPerfHandle);
                ActivityStackSupervisor.mPerfHandle = -1;
            }
            ActivityStackSupervisor.mIsPerfBoostAcquired = false;
        }
        if (ActivityStackSupervisor.mPerfSendTapHint && (boostFramework2 = this.mPerfObj) != null) {
            boostFramework2.perfHint(4163, (String) null);
            ActivityStackSupervisor.mPerfSendTapHint = false;
        }
        if (ActivityDisplay.mIsPerfBoostAcquired && this.mPerfObj != null) {
            if (ActivityDisplay.mPerfHandle > 0) {
                this.mPerfObj.perfLockReleaseHandler(ActivityDisplay.mPerfHandle);
                ActivityDisplay.mPerfHandle = -1;
            }
            ActivityDisplay.mIsPerfBoostAcquired = false;
        }
        if (ActivityDisplay.mPerfSendTapHint && (boostFramework = this.mPerfObj) != null) {
            boostFramework.perfHint(4163, (String) null);
            ActivityDisplay.mPerfSendTapHint = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void setTouchExcludeRegion(Region newRegion) {
        synchronized (this) {
            this.mTouchExcludeRegion.set(newRegion);
        }
    }
}
