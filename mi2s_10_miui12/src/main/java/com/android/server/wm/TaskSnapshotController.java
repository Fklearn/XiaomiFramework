package com.android.server.wm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.GraphicBuffer;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.os.Handler;
import android.util.ArraySet;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.ThreadedRenderer;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.wm.TaskSnapshotSurface;
import com.android.server.wm.utils.InsetUtils;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.function.Consumer;

class TaskSnapshotController {
    @VisibleForTesting
    static final int SNAPSHOT_MODE_APP_THEME = 1;
    @VisibleForTesting
    static final int SNAPSHOT_MODE_NONE = 2;
    @VisibleForTesting
    static final int SNAPSHOT_MODE_REAL = 0;
    private static final String TAG = "WindowManager";
    private final TaskSnapshotCache mCache;
    private final float mFullSnapshotScale;
    private final Handler mHandler = new Handler();
    private final boolean mIsRunningOnIoT;
    private final boolean mIsRunningOnTv;
    private final boolean mIsRunningOnWear;
    private final TaskSnapshotLoader mLoader;
    private final TaskSnapshotPersister mPersister;
    private final WindowManagerService mService;
    private final ArraySet<Task> mSkipClosingAppSnapshotTasks = new ArraySet<>();
    private final Rect mTmpRect = new Rect();
    private final ArraySet<Task> mTmpTasks = new ArraySet<>();

    TaskSnapshotController(WindowManagerService service) {
        this.mService = service;
        this.mPersister = new TaskSnapshotPersister(this.mService, $$Lambda$OPdXuZQLetMnocdH6XV32JbNQ3I.INSTANCE);
        this.mLoader = new TaskSnapshotLoader(this.mPersister);
        this.mCache = new TaskSnapshotCache(this.mService, this.mLoader);
        this.mIsRunningOnTv = this.mService.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        this.mIsRunningOnIoT = this.mService.mContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded");
        this.mIsRunningOnWear = this.mService.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        this.mFullSnapshotScale = this.mService.mContext.getResources().getFloat(17105059);
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        this.mPersister.start();
    }

    /* access modifiers changed from: package-private */
    public void onTransitionStarting(DisplayContent displayContent) {
        handleClosingApps(displayContent.mClosingApps);
    }

    /* access modifiers changed from: package-private */
    public void notifyAppVisibilityChanged(AppWindowToken appWindowToken, boolean visible) {
        if (!visible) {
            handleClosingApps(Sets.newArraySet(new AppWindowToken[]{appWindowToken}));
        }
    }

    /* access modifiers changed from: package-private */
    public void handleSnapshotTaskByGesture(AppWindowToken atoken) {
        Task task;
        if (!shouldDisableSnapshots() && (task = atoken.getTask()) != null && !this.mSkipClosingAppSnapshotTasks.contains(task)) {
            snapshotTasks(Sets.newArraySet(new Task[]{task}));
        }
    }

    private void handleClosingApps(ArraySet<AppWindowToken> closingApps) {
        if (!shouldDisableSnapshots()) {
            getClosingTasks(closingApps, this.mTmpTasks);
            snapshotTasks(this.mTmpTasks);
            this.mSkipClosingAppSnapshotTasks.clear();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addSkipClosingAppSnapshotTasks(ArraySet<Task> tasks) {
        this.mSkipClosingAppSnapshotTasks.addAll(tasks);
    }

    /* access modifiers changed from: package-private */
    public void snapshotTasks(ArraySet<Task> tasks) {
        ActivityManager.TaskSnapshot snapshot;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.valueAt(i);
            int mode = getSnapshotMode(task);
            if (mode == 0) {
                snapshot = snapshotTask(task);
            } else if (mode == 1) {
                snapshot = drawAppThemeSnapshot(task);
            } else if (mode != 2) {
                snapshot = null;
            }
            if (snapshot != null) {
                GraphicBuffer buffer = snapshot.getSnapshot();
                if (buffer.getWidth() == 0 || buffer.getHeight() == 0) {
                    buffer.destroy();
                    Slog.e("WindowManager", "Invalid task snapshot dimensions " + buffer.getWidth() + "x" + buffer.getHeight());
                } else {
                    this.mCache.putSnapshot(task, snapshot);
                    this.mPersister.persistSnapshot(task.mTaskId, task.mUserId, snapshot);
                    task.onSnapshotChanged(snapshot);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.TaskSnapshot getSnapshot(int taskId, int userId, boolean restoreFromDisk, boolean reducedResolution) {
        return this.mCache.getSnapshot(taskId, userId, restoreFromDisk, reducedResolution || TaskSnapshotPersister.DISABLE_FULL_SIZED_BITMAPS);
    }

    /* access modifiers changed from: package-private */
    public WindowManagerPolicy.StartingSurface createStartingSurface(AppWindowToken token, ActivityManager.TaskSnapshot snapshot) {
        return TaskSnapshotSurface.create(this.mService, token, snapshot);
    }

    private AppWindowToken findAppTokenForSnapshot(Task task) {
        for (int i = task.getChildCount() - 1; i >= 0; i--) {
            AppWindowToken appWindowToken = (AppWindowToken) task.getChildAt(i);
            if (appWindowToken != null && appWindowToken.isSurfaceShowing() && appWindowToken.findMainWindow() != null && appWindowToken.forAllWindows($$Lambda$TaskSnapshotController$b7mc92hqzbRpmpc99dYS4wKuL6Y.INSTANCE, true)) {
                return appWindowToken;
            }
        }
        return null;
    }

    static /* synthetic */ boolean lambda$findAppTokenForSnapshot$0(WindowState ws) {
        return ws.mWinAnimator != null && ws.mWinAnimator.getShown() && ws.mWinAnimator.mLastAlpha > 0.0f;
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl.ScreenshotGraphicBuffer createTaskSnapshot(Task task, float scaleFraction) {
        GraphicBuffer buffer;
        if (task.getSurfaceControl() == null) {
            return null;
        }
        task.getBounds(this.mTmpRect);
        Rect taskBounds = this.mTmpRect;
        if (task.inFreeformWindowingMode() && this.mService.getCurrentFreeFormWindowMode() == 0) {
            this.mTmpRect.set(taskBounds.left, taskBounds.top, taskBounds.left + ((int) (((float) taskBounds.width()) * MiuiMultiWindowUtils.sScale)), taskBounds.top + ((int) (((float) taskBounds.height()) * MiuiMultiWindowUtils.sScale)));
        }
        this.mTmpRect.offsetTo(0, 0);
        SurfaceControl.ScreenshotGraphicBuffer screenshotBuffer = SurfaceControl.captureLayers(task.getSurfaceControl().getHandle(), this.mTmpRect, scaleFraction);
        if (screenshotBuffer != null) {
            buffer = screenshotBuffer.getGraphicBuffer();
        } else {
            buffer = null;
        }
        if (buffer == null || buffer.getWidth() <= 1 || buffer.getHeight() <= 1) {
            return null;
        }
        return screenshotBuffer;
    }

    private ActivityManager.TaskSnapshot snapshotTask(Task task) {
        AppWindowToken appWindowToken;
        float f;
        Task task2 = task;
        if (!this.mService.mPolicy.isScreenOn() || (appWindowToken = findAppTokenForSnapshot(task)) == null || appWindowToken.hasCommittedReparentToAnimationLeash()) {
            return null;
        }
        boolean isLowRamDevice = ActivityManager.isLowRamDeviceStatic();
        if (isLowRamDevice) {
            f = this.mPersister.getReducedScale();
        } else {
            f = this.mFullSnapshotScale;
        }
        float scaleFraction = f;
        WindowState mainWindow = appWindowToken.findMainWindow();
        if (mainWindow == null) {
            Slog.w("WindowManager", "Failed to take screenshot. No main window for " + task2);
            return null;
        }
        SurfaceControl.ScreenshotGraphicBuffer screenshotBuffer = createTaskSnapshot(task2, scaleFraction);
        if (screenshotBuffer == null) {
            return null;
        }
        WindowState windowState = mainWindow;
        float f2 = scaleFraction;
        return new ActivityManager.TaskSnapshot(appWindowToken.mActivityComponent, screenshotBuffer.getGraphicBuffer(), screenshotBuffer.getColorSpace(), appWindowToken.getTask().getConfiguration().orientation, getInsets(mainWindow), isLowRamDevice, scaleFraction, true, task.getWindowingMode(), getSystemUiVisibility(task), !appWindowToken.fillsParent() || (mainWindow.getAttrs().format != -1));
    }

    private boolean shouldDisableSnapshots() {
        return this.mIsRunningOnWear || this.mIsRunningOnTv || this.mIsRunningOnIoT;
    }

    private Rect getInsets(WindowState state) {
        Rect insets = minRect(state.getContentInsets(), state.getStableInsets());
        InsetUtils.addInsets(insets, state.mAppToken.getLetterboxInsets());
        return insets;
    }

    private Rect minRect(Rect rect1, Rect rect2) {
        return new Rect(Math.min(rect1.left, rect2.left), Math.min(rect1.top, rect2.top), Math.min(rect1.right, rect2.right), Math.min(rect1.bottom, rect2.bottom));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void getClosingTasks(ArraySet<AppWindowToken> closingApps, ArraySet<Task> outClosingTasks) {
        outClosingTasks.clear();
        for (int i = closingApps.size() - 1; i >= 0; i--) {
            Task task = closingApps.valueAt(i).getTask();
            if (task != null && !task.isVisible() && !this.mSkipClosingAppSnapshotTasks.contains(task)) {
                if (task.mUpdateTaskSnapshotByGesture) {
                    this.mSkipClosingAppSnapshotTasks.add(task);
                    task.mUpdateTaskSnapshotByGesture = false;
                } else {
                    outClosingTasks.add(task);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getSnapshotMode(Task task) {
        AppWindowToken topChild = (AppWindowToken) task.getTopChild();
        if (!task.isActivityTypeStandardOrUndefined() && !task.isActivityTypeAssistant()) {
            return 2;
        }
        if (topChild == null || !topChild.shouldUseAppThemeSnapshot()) {
            return 0;
        }
        return 1;
    }

    private ActivityManager.TaskSnapshot drawAppThemeSnapshot(Task task) {
        WindowState mainWindow;
        AppWindowToken topChild = (AppWindowToken) task.getTopChild();
        if (topChild == null || (mainWindow = topChild.findMainWindow()) == null) {
            return null;
        }
        int color = ColorUtils.setAlphaComponent(task.getTaskDescription().getBackgroundColor(), 255);
        WindowManager.LayoutParams attrs = mainWindow.getAttrs();
        TaskSnapshotSurface.SystemBarBackgroundPainter decorPainter = new TaskSnapshotSurface.SystemBarBackgroundPainter(attrs.flags, attrs.privateFlags, attrs.systemUiVisibility, task.getTaskDescription(), this.mFullSnapshotScale);
        int width = (int) (((float) task.getBounds().width()) * this.mFullSnapshotScale);
        int height = (int) (((float) task.getBounds().height()) * this.mFullSnapshotScale);
        RenderNode node = RenderNode.create("TaskSnapshotController", (RenderNode.AnimationHost) null);
        node.setLeftTopRightBottom(0, 0, width, height);
        node.setClipToBounds(false);
        RecordingCanvas c = node.start(width, height);
        c.drawColor(color);
        decorPainter.setInsets(mainWindow.getContentInsets(), mainWindow.getStableInsets());
        decorPainter.drawDecors(c, (Rect) null);
        node.end(c);
        Bitmap hwBitmap = ThreadedRenderer.createHardwareBitmap(node, width, height);
        if (hwBitmap == null) {
            return null;
        }
        ComponentName componentName = topChild.mActivityComponent;
        GraphicBuffer createGraphicBufferHandle = hwBitmap.createGraphicBufferHandle();
        ColorSpace colorSpace = hwBitmap.getColorSpace();
        int i = topChild.getTask().getConfiguration().orientation;
        AppWindowToken appWindowToken = topChild;
        return new ActivityManager.TaskSnapshot(componentName, createGraphicBufferHandle, colorSpace, i, getInsets(mainWindow), ActivityManager.isLowRamDeviceStatic(), this.mFullSnapshotScale, false, task.getWindowingMode(), getSystemUiVisibility(task), false);
    }

    /* access modifiers changed from: package-private */
    public void onAppRemoved(AppWindowToken wtoken) {
        this.mCache.onAppRemoved(wtoken);
    }

    /* access modifiers changed from: package-private */
    public void onAppDied(AppWindowToken wtoken) {
        this.mCache.onAppDied(wtoken);
    }

    /* access modifiers changed from: package-private */
    public void notifyTaskRemovedFromRecents(int taskId, int userId) {
        this.mCache.onTaskRemoved(taskId);
        this.mPersister.onTaskRemovedFromRecents(taskId, userId);
    }

    /* access modifiers changed from: package-private */
    public void removeObsoleteTaskFiles(ArraySet<Integer> persistentTaskIds, int[] runningUserIds) {
        this.mPersister.removeObsoleteFiles(persistentTaskIds, runningUserIds);
    }

    /* access modifiers changed from: package-private */
    public void setPersisterPaused(boolean paused) {
        this.mPersister.setPaused(paused);
    }

    /* access modifiers changed from: package-private */
    public void screenTurningOff(WindowManagerPolicy.ScreenOffListener listener) {
        if (shouldDisableSnapshots()) {
            listener.onScreenOff();
        } else {
            this.mHandler.post(new Runnable(listener) {
                private final /* synthetic */ WindowManagerPolicy.ScreenOffListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TaskSnapshotController.this.lambda$screenTurningOff$2$TaskSnapshotController(this.f$1);
                }
            });
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public /* synthetic */ void lambda$screenTurningOff$2$TaskSnapshotController(WindowManagerPolicy.ScreenOffListener listener) {
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mTmpTasks.clear();
                this.mService.mRoot.forAllTasks(new Consumer() {
                    public final void accept(Object obj) {
                        TaskSnapshotController.this.lambda$screenTurningOff$1$TaskSnapshotController((Task) obj);
                    }
                });
                snapshotTasks(this.mTmpTasks);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            listener.onScreenOff();
        } catch (Throwable th) {
            listener.onScreenOff();
            throw th;
        }
    }

    public /* synthetic */ void lambda$screenTurningOff$1$TaskSnapshotController(Task task) {
        if (task.isVisible()) {
            this.mTmpTasks.add(task);
        }
    }

    private int getSystemUiVisibility(Task task) {
        WindowState topFullscreenWindow;
        AppWindowToken topFullscreenToken = task.getTopFullscreenAppToken();
        if (topFullscreenToken != null) {
            topFullscreenWindow = topFullscreenToken.getTopFullscreenWindow();
        } else {
            topFullscreenWindow = null;
        }
        if (topFullscreenWindow != null) {
            return topFullscreenWindow.getSystemUiVisibility();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "mFullSnapshotScale=" + this.mFullSnapshotScale);
        this.mCache.dump(pw, prefix);
    }
}
