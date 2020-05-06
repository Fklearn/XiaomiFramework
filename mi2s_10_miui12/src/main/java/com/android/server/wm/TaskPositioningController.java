package com.android.server.wm;

import android.app.IActivityTaskManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Slog;
import android.view.Display;
import android.view.IWindow;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import com.android.internal.annotations.GuardedBy;
import com.android.server.input.InputManagerService;

class TaskPositioningController {
    private final IActivityTaskManager mActivityManager;
    private final Handler mHandler;
    private final InputManagerService mInputManager;
    private SurfaceControl mInputSurface;
    private DisplayContent mPositioningDisplay;
    private final WindowManagerService mService;
    @GuardedBy({"WindowManagerSerivce.mWindowMap"})
    private TaskPositioner mTaskPositioner;
    private final Rect mTmpClipRect = new Rect();
    private IBinder mTransferTouchFromToken;

    /* access modifiers changed from: package-private */
    public boolean isPositioningLocked() {
        return this.mTaskPositioner != null;
    }

    /* access modifiers changed from: package-private */
    public InputWindowHandle getDragWindowHandleLocked() {
        TaskPositioner taskPositioner = this.mTaskPositioner;
        if (taskPositioner != null) {
            return taskPositioner.mDragWindowHandle;
        }
        return null;
    }

    TaskPositioningController(WindowManagerService service, InputManagerService inputManager, IActivityTaskManager activityManager, Looper looper) {
        this.mService = service;
        this.mInputManager = inputManager;
        this.mActivityManager = activityManager;
        this.mHandler = new Handler(looper);
    }

    /* access modifiers changed from: package-private */
    public void hideInputSurface(SurfaceControl.Transaction t, int displayId) {
        SurfaceControl surfaceControl;
        DisplayContent displayContent = this.mPositioningDisplay;
        if (displayContent != null && displayContent.getDisplayId() == displayId && (surfaceControl = this.mInputSurface) != null) {
            t.hide(surfaceControl);
        }
    }

    /* access modifiers changed from: package-private */
    public void showInputSurface(SurfaceControl.Transaction t, int displayId) {
        DisplayContent displayContent = this.mPositioningDisplay;
        if (displayContent != null && displayContent.getDisplayId() == displayId) {
            DisplayContent dc = this.mService.mRoot.getDisplayContent(displayId);
            if (this.mInputSurface == null) {
                this.mInputSurface = this.mService.makeSurfaceBuilder(dc.getSession()).setContainerLayer().setName("Drag and Drop Input Consumer").build();
            }
            InputWindowHandle h = getDragWindowHandleLocked();
            if (h == null) {
                Slog.w(DisplayPolicy.TAG, "Drag is in progress but there is no drag window handle.");
                return;
            }
            t.show(this.mInputSurface);
            t.setInputWindowInfo(this.mInputSurface, h);
            t.setLayer(this.mInputSurface, Integer.MAX_VALUE);
            Display display = dc.getDisplay();
            Point p = new Point();
            display.getRealSize(p);
            this.mTmpClipRect.set(0, 0, p.x, p.y);
            t.setWindowCrop(this.mInputSurface, this.mTmpClipRect);
            t.transferTouchFocus(this.mTransferTouchFromToken, h.token);
            this.mTransferTouchFromToken = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean startMovingTask(IWindow window, float startX, float startY) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowState win = this.mService.windowForClientLocked((Session) null, window, false);
                try {
                    if (!startPositioningLocked(win, false, false, startX, startY)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    try {
                        this.mActivityManager.setFocusedTask(win.getTask().mTaskId);
                        return true;
                    } catch (RemoteException e) {
                        return true;
                    }
                } catch (Throwable th) {
                    th = th;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void handleTapOutsideTask(DisplayContent displayContent, int x, int y) {
        this.mHandler.post(new Runnable(displayContent, x, y) {
            private final /* synthetic */ DisplayContent f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TaskPositioningController.this.lambda$handleTapOutsideTask$0$TaskPositioningController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$handleTapOutsideTask$0$TaskPositioningController(DisplayContent displayContent, int x, int y) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task task = displayContent.findTaskForResizePoint(x, y);
                if (task != null) {
                    if (!startPositioningLocked(task.getTopVisibleAppMainWindow(), true, task.preserveOrientationOnResize(), (float) x, (float) y)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    try {
                        this.mActivityManager.setFocusedTask(task.mTaskId);
                    } catch (RemoteException e) {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    private boolean startPositioningLocked(WindowState win, boolean resize, boolean preserveOrientation, float startX, float startY) {
        if (win == null || win.getAppToken() == null) {
            Slog.w(DisplayPolicy.TAG, "startPositioningLocked: Bad window " + win);
            return false;
        } else if (win.mInputChannel == null) {
            Slog.wtf(DisplayPolicy.TAG, "startPositioningLocked: " + win + " has no input channel,  probably being removed");
            return false;
        } else {
            DisplayContent displayContent = win.getDisplayContent();
            if (displayContent == null) {
                Slog.w(DisplayPolicy.TAG, "startPositioningLocked: Invalid display content " + win);
                return false;
            }
            this.mPositioningDisplay = displayContent;
            this.mTaskPositioner = TaskPositioner.create(this.mService);
            WindowState transferFocusFromWin = win;
            if (!(displayContent.mCurrentFocus == null || displayContent.mCurrentFocus == win || displayContent.mCurrentFocus.mAppToken != win.mAppToken)) {
                transferFocusFromWin = displayContent.mCurrentFocus;
            }
            this.mTransferTouchFromToken = transferFocusFromWin.mInputChannel.getToken();
            this.mTaskPositioner.register(displayContent);
            this.mTaskPositioner.startDrag(win, resize, preserveOrientation, startX, startY);
            return true;
        }
    }

    public void finishTaskPositioning(IWindow window) {
        TaskPositioner taskPositioner = this.mTaskPositioner;
        if (taskPositioner != null && taskPositioner.mClientCallback == window.asBinder()) {
            finishTaskPositioning();
        }
    }

    /* access modifiers changed from: package-private */
    public void finishTaskPositioning() {
        this.mService.mAnimationHandler.post(new Runnable() {
            public final void run() {
                TaskPositioningController.this.lambda$finishTaskPositioning$1$TaskPositioningController();
            }
        });
    }

    public /* synthetic */ void lambda$finishTaskPositioning$1$TaskPositioningController() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                cleanUpTaskPositioner();
                this.mPositioningDisplay = null;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    private void cleanUpTaskPositioner() {
        TaskPositioner positioner = this.mTaskPositioner;
        if (positioner != null) {
            this.mTaskPositioner = null;
            positioner.unregister();
        }
    }
}
