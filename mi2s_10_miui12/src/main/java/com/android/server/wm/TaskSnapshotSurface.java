package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.GraphicBuffer;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.MergedConfiguration;
import android.view.DisplayCutout;
import android.view.IWindowSession;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowManagerGlobal;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.DecorView;
import com.android.internal.view.BaseIWindow;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.policy.WindowManagerPolicy;
import java.util.Objects;

class TaskSnapshotSurface implements WindowManagerPolicy.StartingSurface {
    private static final int FLAG_INHERIT_EXCLUDES = 830922808;
    private static final int MSG_REPORT_DRAW = 0;
    private static final int PRIVATE_FLAG_INHERITS = 131072;
    private static final long SIZE_MISMATCH_MINIMUM_TIME_MS = 450;
    private static final String TAG = "WindowManager";
    private static final String TITLE_FORMAT = "SnapshotStartingWindow for taskId=%s";
    /* access modifiers changed from: private */
    public static Handler sHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            boolean hasDrawn;
            if (msg.what == 0) {
                TaskSnapshotSurface surface = (TaskSnapshotSurface) msg.obj;
                synchronized (surface.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        hasDrawn = surface.mHasDrawn;
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                if (hasDrawn) {
                    surface.reportDrawn();
                }
            }
        }
    };
    private final Paint mBackgroundPaint = new Paint();
    private SurfaceControl mChildSurfaceControl;
    private final Rect mContentInsets = new Rect();
    private final Rect mFrame = new Rect();
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public boolean mHasDrawn;
    /* access modifiers changed from: private */
    public final int mOrientationOnCreation;
    /* access modifiers changed from: private */
    public final WindowManagerService mService;
    private final IWindowSession mSession;
    private long mShownTime;
    private boolean mSizeMismatch;
    private ActivityManager.TaskSnapshot mSnapshot;
    private final Rect mStableInsets = new Rect();
    private final int mStatusBarColor;
    private final Surface mSurface;
    private SurfaceControl mSurfaceControl;
    @VisibleForTesting
    final SystemBarBackgroundPainter mSystemBarBackgroundPainter;
    private final Rect mTaskBounds;
    private final CharSequence mTitle;
    private final Window mWindow;

    /* JADX WARNING: Code restructure failed: missing block: B:59:0x01a0, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01af, code lost:
        r50 = r5;
        r51 = r6;
        r52 = r17;
        r55 = r8;
        r53 = r18;
        r54 = r21;
        r56 = r10;
        r57 = r11;
        r58 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        r0 = r11.addToDisplay(r13, r13.mSeq, r15, 8, r60.getDisplayContent().getDisplayId(), r10, r9, r9, r9, r12, (android.view.InputChannel) null, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01d0, code lost:
        if (r0 >= 0) goto L_0x01ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01d2, code lost:
        android.util.Slog.w("WindowManager", "Failed to add snapshot starting window res=" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x01e8, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x01ed, code lost:
        r50 = r5;
        r51 = r6;
        r55 = r8;
        r56 = r10;
        r57 = r11;
        r58 = r13;
        r52 = r17;
        r53 = r18;
        r54 = r21;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.wm.TaskSnapshotSurface create(com.android.server.wm.WindowManagerService r59, com.android.server.wm.AppWindowToken r60, android.app.ActivityManager.TaskSnapshot r61) {
        /*
            r1 = r60
            android.view.WindowManager$LayoutParams r0 = new android.view.WindowManager$LayoutParams
            r0.<init>()
            r15 = r0
            com.android.server.wm.TaskSnapshotSurface$Window r0 = new com.android.server.wm.TaskSnapshotSurface$Window
            r0.<init>()
            r13 = r0
            android.view.IWindowSession r11 = android.view.WindowManagerGlobal.getWindowSession()
            r13.setSession(r11)
            android.view.SurfaceControl r39 = new android.view.SurfaceControl
            r39.<init>()
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            android.view.DisplayCutout$ParcelableWrapper r12 = new android.view.DisplayCutout$ParcelableWrapper
            r12.<init>()
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r10 = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r8 = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7 = r0
            android.view.InsetsState r14 = new android.view.InsetsState
            r14.<init>()
            android.util.MergedConfiguration r33 = new android.util.MergedConfiguration
            r33.<init>()
            android.app.ActivityManager$TaskDescription r0 = new android.app.ActivityManager$TaskDescription
            r0.<init>()
            r6 = r0
            r0 = -1
            r6.setBackgroundColor(r0)
            r5 = r59
            com.android.server.wm.WindowManagerGlobalLock r2 = r5.mGlobalLock
            monitor-enter(r2)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x02a2 }
            com.android.server.wm.WindowState r3 = r60.findMainWindow()     // Catch:{ all -> 0x02a2 }
            com.android.server.wm.Task r4 = r60.getTask()     // Catch:{ all -> 0x02a2 }
            r16 = 0
            if (r4 != 0) goto L_0x0092
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0087 }
            r5.<init>()     // Catch:{ all -> 0x0087 }
            r17 = r7
            java.lang.String r7 = "TaskSnapshotSurface.create: Failed to find task for token="
            r5.append(r7)     // Catch:{ all -> 0x007b }
            r5.append(r1)     // Catch:{ all -> 0x007b }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x007b }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x007b }
            monitor-exit(r2)     // Catch:{ all -> 0x007b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r16
        L_0x007b:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            goto L_0x02ab
        L_0x0087:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r7
            goto L_0x02ab
        L_0x0092:
            r17 = r7
            com.android.server.wm.Task r5 = r60.getTask()     // Catch:{ all -> 0x0297 }
            com.android.server.wm.AppWindowToken r5 = r5.getTopFullscreenAppToken()     // Catch:{ all -> 0x0297 }
            if (r5 != 0) goto L_0x00d5
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c9 }
            r7.<init>()     // Catch:{ all -> 0x00c9 }
            r18 = r8
            java.lang.String r8 = "TaskSnapshotSurface.create: Failed to find top fullscreen for task="
            r7.append(r8)     // Catch:{ all -> 0x00bb }
            r7.append(r4)     // Catch:{ all -> 0x00bb }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00bb }
            android.util.Slog.w(r0, r7)     // Catch:{ all -> 0x00bb }
            monitor-exit(r2)     // Catch:{ all -> 0x00bb }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r16
        L_0x00bb:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            r8 = r18
            goto L_0x02ab
        L_0x00c9:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            goto L_0x02ab
        L_0x00d5:
            r18 = r8
            com.android.server.wm.WindowState r7 = r5.getTopFullscreenWindow()     // Catch:{ all -> 0x028a }
            if (r3 == 0) goto L_0x0260
            if (r7 != 0) goto L_0x00f0
            r22 = r3
            r20 = r5
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            r8 = r18
            goto L_0x026f
        L_0x00f0:
            android.content.res.Configuration r8 = r7.getConfiguration()     // Catch:{ all -> 0x028a }
            int r8 = r8.orientation     // Catch:{ all -> 0x028a }
            int r0 = r61.getOrientation()     // Catch:{ all -> 0x028a }
            if (r8 == r0) goto L_0x011b
            java.lang.String r0 = "WindowManager"
            r20 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bb }
            r5.<init>()     // Catch:{ all -> 0x00bb }
            r21 = r8
            java.lang.String r8 = "TaskSnapshotSurface.create: Orientation is not the same for token="
            r5.append(r8)     // Catch:{ all -> 0x00bb }
            r5.append(r1)     // Catch:{ all -> 0x00bb }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00bb }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x00bb }
            monitor-exit(r2)     // Catch:{ all -> 0x00bb }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r16
        L_0x011b:
            r20 = r5
            r21 = r8
            int r0 = r7.getSystemUiVisibility()     // Catch:{ all -> 0x028a }
            r8 = r0
            android.view.WindowManager$LayoutParams r0 = r7.getAttrs()     // Catch:{ all -> 0x028a }
            int r0 = r0.flags     // Catch:{ all -> 0x028a }
            r48 = r0
            android.view.WindowManager$LayoutParams r0 = r7.getAttrs()     // Catch:{ all -> 0x028a }
            int r0 = r0.privateFlags     // Catch:{ all -> 0x028a }
            r49 = r0
            android.view.WindowManager$LayoutParams r0 = r3.getAttrs()     // Catch:{ all -> 0x028a }
            java.lang.String r0 = r0.packageName     // Catch:{ all -> 0x028a }
            r15.packageName = r0     // Catch:{ all -> 0x028a }
            android.view.WindowManager$LayoutParams r0 = r3.getAttrs()     // Catch:{ all -> 0x028a }
            int r0 = r0.windowAnimations     // Catch:{ all -> 0x028a }
            r15.windowAnimations = r0     // Catch:{ all -> 0x028a }
            android.view.WindowManager$LayoutParams r0 = r3.getAttrs()     // Catch:{ all -> 0x028a }
            float r0 = r0.dimAmount     // Catch:{ all -> 0x028a }
            r15.dimAmount = r0     // Catch:{ all -> 0x028a }
            r0 = 3
            r15.type = r0     // Catch:{ all -> 0x028a }
            android.graphics.GraphicBuffer r0 = r61.getSnapshot()     // Catch:{ all -> 0x028a }
            int r0 = r0.getFormat()     // Catch:{ all -> 0x028a }
            r15.format = r0     // Catch:{ all -> 0x028a }
            r0 = -830922809(0xffffffffce791fc7, float:-1.04490234E9)
            r0 = r48 & r0
            r0 = r0 | 8
            r0 = r0 | 16
            r15.flags = r0     // Catch:{ all -> 0x028a }
            r0 = 131072(0x20000, float:1.83671E-40)
            r0 = r49 & r0
            r15.privateFlags = r0     // Catch:{ all -> 0x028a }
            android.os.IBinder r0 = r1.token     // Catch:{ all -> 0x028a }
            r15.token = r0     // Catch:{ all -> 0x028a }
            r0 = -1
            r15.width = r0     // Catch:{ all -> 0x028a }
            r15.height = r0     // Catch:{ all -> 0x028a }
            r15.systemUiVisibility = r8     // Catch:{ all -> 0x028a }
            java.lang.String r0 = "SnapshotStartingWindow for taskId=%s"
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x028a }
            r19 = 0
            r22 = r3
            int r3 = r4.mTaskId     // Catch:{ all -> 0x028a }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x028a }
            r5[r19] = r3     // Catch:{ all -> 0x028a }
            java.lang.String r0 = java.lang.String.format(r0, r5)     // Catch:{ all -> 0x028a }
            r15.setTitle(r0)     // Catch:{ all -> 0x028a }
            android.app.ActivityManager$TaskDescription r0 = r4.getTaskDescription()     // Catch:{ all -> 0x028a }
            if (r0 == 0) goto L_0x0196
            r6.copyFrom(r0)     // Catch:{ all -> 0x00bb }
        L_0x0196:
            android.graphics.Rect r3 = new android.graphics.Rect     // Catch:{ all -> 0x028a }
            r3.<init>()     // Catch:{ all -> 0x028a }
            r5 = r3
            r4.getBounds(r5)     // Catch:{ all -> 0x028a }
            monitor-exit(r2)     // Catch:{ all -> 0x028a }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            int r4 = r13.mSeq     // Catch:{ RemoteException -> 0x01ec }
            r0 = 8
            com.android.server.wm.DisplayContent r2 = r60.getDisplayContent()     // Catch:{ RemoteException -> 0x01ec }
            int r7 = r2.getDisplayId()     // Catch:{ RemoteException -> 0x01ec }
            r19 = 0
            r2 = r11
            r3 = r13
            r50 = r5
            r5 = r15
            r51 = r6
            r6 = r0
            r52 = r17
            r55 = r8
            r53 = r18
            r54 = r21
            r8 = r10
            r56 = r10
            r10 = r9
            r57 = r11
            r11 = r9
            r58 = r13
            r13 = r19
            int r0 = r2.addToDisplay(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ RemoteException -> 0x01ea }
            if (r0 >= 0) goto L_0x01e9
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x01ea }
            r3.<init>()     // Catch:{ RemoteException -> 0x01ea }
            java.lang.String r4 = "Failed to add snapshot starting window res="
            r3.append(r4)     // Catch:{ RemoteException -> 0x01ea }
            r3.append(r0)     // Catch:{ RemoteException -> 0x01ea }
            java.lang.String r3 = r3.toString()     // Catch:{ RemoteException -> 0x01ea }
            android.util.Slog.w(r2, r3)     // Catch:{ RemoteException -> 0x01ea }
            return r16
        L_0x01e9:
            goto L_0x01ff
        L_0x01ea:
            r0 = move-exception
            goto L_0x01ff
        L_0x01ec:
            r0 = move-exception
            r50 = r5
            r51 = r6
            r55 = r8
            r56 = r10
            r57 = r11
            r58 = r13
            r52 = r17
            r53 = r18
            r54 = r21
        L_0x01ff:
            com.android.server.wm.TaskSnapshotSurface r0 = new com.android.server.wm.TaskSnapshotSurface
            java.lang.CharSequence r41 = r15.getTitle()
            r36 = r0
            r37 = r59
            r38 = r58
            r40 = r61
            r42 = r51
            r43 = r55
            r44 = r48
            r45 = r49
            r46 = r50
            r47 = r54
            r36.<init>(r37, r38, r39, r40, r41, r42, r43, r44, r45, r46, r47)
            r2 = r0
            r3 = r58
            r3.setOuter(r2)
            int r0 = r3.mSeq     // Catch:{ RemoteException -> 0x0251 }
            r19 = -1
            r20 = -1
            r21 = 0
            r22 = 0
            r23 = -1
            r5 = r15
            r15 = r57
            r16 = r3
            r17 = r0
            r18 = r5
            r25 = r56
            r26 = r9
            r27 = r53
            r28 = r9
            r29 = r52
            r30 = r9
            r31 = r9
            r32 = r12
            r34 = r39
            r35 = r14
            r15.relayout(r16, r17, r18, r19, r20, r21, r22, r23, r25, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35)     // Catch:{ RemoteException -> 0x024f }
            goto L_0x0253
        L_0x024f:
            r0 = move-exception
            goto L_0x0253
        L_0x0251:
            r0 = move-exception
            r5 = r15
        L_0x0253:
            r10 = r52
            r8 = r53
            r6 = r56
            r2.setFrames(r6, r8, r10)
            r2.drawSnapshot()
            return r2
        L_0x0260:
            r22 = r3
            r20 = r5
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            r8 = r18
        L_0x026f:
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b0 }
            r11.<init>()     // Catch:{ all -> 0x02b0 }
            java.lang.String r13 = "TaskSnapshotSurface.create: Failed to find main window for token="
            r11.append(r13)     // Catch:{ all -> 0x02b0 }
            r11.append(r1)     // Catch:{ all -> 0x02b0 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x02b0 }
            android.util.Slog.w(r0, r11)     // Catch:{ all -> 0x02b0 }
            monitor-exit(r2)     // Catch:{ all -> 0x02b0 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r16
        L_0x028a:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            r8 = r18
            goto L_0x02ab
        L_0x0297:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r17
            goto L_0x02ab
        L_0x02a2:
            r0 = move-exception
            r51 = r6
            r6 = r10
            r57 = r11
            r3 = r13
            r5 = r15
            r10 = r7
        L_0x02ab:
            monitor-exit(r2)     // Catch:{ all -> 0x02b0 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x02b0:
            r0 = move-exception
            goto L_0x02ab
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskSnapshotSurface.create(com.android.server.wm.WindowManagerService, com.android.server.wm.AppWindowToken, android.app.ActivityManager$TaskSnapshot):com.android.server.wm.TaskSnapshotSurface");
    }

    @VisibleForTesting
    TaskSnapshotSurface(WindowManagerService service, Window window, SurfaceControl surfaceControl, ActivityManager.TaskSnapshot snapshot, CharSequence title, ActivityManager.TaskDescription taskDescription, int sysUiVis, int windowFlags, int windowPrivateFlags, Rect taskBounds, int currentOrientation) {
        this.mService = service;
        this.mSurface = new Surface();
        this.mHandler = new Handler(this.mService.mH.getLooper());
        this.mSession = WindowManagerGlobal.getWindowSession();
        this.mWindow = window;
        this.mSurfaceControl = surfaceControl;
        this.mSnapshot = snapshot;
        this.mTitle = title;
        int backgroundColor = taskDescription.getBackgroundColor();
        this.mBackgroundPaint.setColor(backgroundColor != 0 ? backgroundColor : -1);
        this.mTaskBounds = taskBounds;
        this.mSystemBarBackgroundPainter = new SystemBarBackgroundPainter(windowFlags, windowPrivateFlags, sysUiVis, taskDescription, 1.0f);
        this.mStatusBarColor = taskDescription.getStatusBarColor();
        this.mOrientationOnCreation = currentOrientation;
    }

    public void remove() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long now = SystemClock.uptimeMillis();
                if (!this.mSizeMismatch || now - this.mShownTime >= SIZE_MISMATCH_MINIMUM_TIME_MS) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    try {
                        this.mSession.remove(this.mWindow);
                    } catch (RemoteException e) {
                    }
                } else {
                    this.mHandler.postAtTime(new Runnable() {
                        public final void run() {
                            TaskSnapshotSurface.this.remove();
                        }
                    }, this.mShownTime + SIZE_MISMATCH_MINIMUM_TIME_MS);
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setFrames(Rect frame, Rect contentInsets, Rect stableInsets) {
        this.mFrame.set(frame);
        this.mContentInsets.set(contentInsets);
        this.mStableInsets.set(stableInsets);
        this.mSizeMismatch = (this.mFrame.width() == this.mSnapshot.getSnapshot().getWidth() && this.mFrame.height() == this.mSnapshot.getSnapshot().getHeight()) ? false : true;
        this.mSystemBarBackgroundPainter.setInsets(contentInsets, stableInsets);
    }

    private void drawSnapshot() {
        GraphicBuffer buffer = this.mSnapshot.getSnapshot();
        this.mSurface.copyFrom(this.mSurfaceControl);
        if (this.mSizeMismatch) {
            drawSizeMismatchSnapshot(buffer);
        } else {
            drawSizeMatchSnapshot(buffer);
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mShownTime = SystemClock.uptimeMillis();
                this.mHasDrawn = true;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        reportDrawn();
        this.mSnapshot = null;
    }

    private void drawSizeMatchSnapshot(GraphicBuffer buffer) {
        this.mSurface.attachAndQueueBuffer(buffer);
        this.mSurface.release();
    }

    /* JADX INFO: finally extract failed */
    private void drawSizeMismatchSnapshot(GraphicBuffer buffer) {
        Rect frame;
        if (this.mSurface.isValid()) {
            SurfaceSession session = new SurfaceSession();
            boolean aspectRatioMismatch = Math.abs((((float) buffer.getWidth()) / ((float) buffer.getHeight())) - (((float) this.mFrame.width()) / ((float) this.mFrame.height()))) > 0.01f;
            this.mChildSurfaceControl = new SurfaceControl.Builder(session).setName(this.mTitle + " - task-snapshot-surface").setBufferSize(buffer.getWidth(), buffer.getHeight()).setFormat(buffer.getFormat()).setParent(this.mSurfaceControl).build();
            Surface surface = new Surface();
            surface.copyFrom(this.mChildSurfaceControl);
            SurfaceControl.openTransaction();
            try {
                this.mChildSurfaceControl.show();
                if (aspectRatioMismatch) {
                    Rect crop = calculateSnapshotCrop();
                    frame = calculateSnapshotFrame(crop);
                    this.mChildSurfaceControl.setWindowCrop(crop);
                    this.mChildSurfaceControl.setPosition((float) frame.left, (float) frame.top);
                    frame.offset(crop.left, crop.top);
                } else {
                    frame = null;
                }
                float scale = 1.0f / this.mSnapshot.getScale();
                this.mChildSurfaceControl.setMatrix(scale, 0.0f, 0.0f, scale);
                SurfaceControl.closeTransaction();
                surface.attachAndQueueBuffer(buffer);
                surface.release();
                if (aspectRatioMismatch) {
                    Canvas c = this.mSurface.lockCanvas((Rect) null);
                    drawBackgroundAndBars(c, frame);
                    this.mSurface.unlockCanvasAndPost(c);
                    this.mSurface.release();
                }
            } catch (Throwable th) {
                SurfaceControl.closeTransaction();
                throw th;
            }
        } else {
            throw new IllegalStateException("mSurface does not hold a valid surface.");
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Rect calculateSnapshotCrop() {
        Rect rect = new Rect();
        int i = 0;
        rect.set(0, 0, this.mSnapshot.getSnapshot().getWidth(), this.mSnapshot.getSnapshot().getHeight());
        Rect insets = this.mSnapshot.getContentInsets();
        boolean isTop = this.mTaskBounds.top == 0 && this.mFrame.top == 0;
        int scale = (int) (((float) insets.left) * this.mSnapshot.getScale());
        if (!isTop) {
            i = (int) (((float) insets.top) * this.mSnapshot.getScale());
        }
        rect.inset(scale, i, (int) (((float) insets.right) * this.mSnapshot.getScale()), (int) (((float) insets.bottom) * this.mSnapshot.getScale()));
        return rect;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Rect calculateSnapshotFrame(Rect crop) {
        Rect frame = new Rect(crop);
        float scale = this.mSnapshot.getScale();
        frame.scale(1.0f / scale);
        frame.offsetTo((int) (((float) (-crop.left)) / scale), (int) (((float) (-crop.top)) / scale));
        frame.offset(DecorView.getColorViewLeftInset(this.mStableInsets.left, this.mContentInsets.left), 0);
        return frame;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void drawBackgroundAndBars(Canvas c, Rect frame) {
        float f;
        Rect rect = frame;
        int statusBarHeight = this.mSystemBarBackgroundPainter.getStatusBarColorViewHeight();
        boolean z = true;
        boolean fillHorizontally = c.getWidth() > rect.right;
        if (c.getHeight() <= rect.bottom) {
            z = false;
        }
        boolean fillVertically = z;
        if (fillHorizontally) {
            float f2 = (float) rect.right;
            float f3 = Color.alpha(this.mStatusBarColor) == 255 ? (float) statusBarHeight : 0.0f;
            float width = (float) c.getWidth();
            if (fillVertically) {
                f = (float) rect.bottom;
            } else {
                f = (float) c.getHeight();
            }
            c.drawRect(f2, f3, width, f, this.mBackgroundPaint);
        }
        if (fillVertically) {
            c.drawRect(0.0f, (float) rect.bottom, (float) c.getWidth(), (float) c.getHeight(), this.mBackgroundPaint);
        }
        this.mSystemBarBackgroundPainter.drawDecors(c, rect);
    }

    /* access modifiers changed from: private */
    public void reportDrawn() {
        try {
            this.mSession.finishDrawing(this.mWindow);
        } catch (RemoteException e) {
        }
    }

    @VisibleForTesting
    static class Window extends BaseIWindow {
        private TaskSnapshotSurface mOuter;

        Window() {
        }

        public void setOuter(TaskSnapshotSurface outer) {
            this.mOuter = outer;
        }

        public void resized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, Rect stableInsets, Rect outsets, boolean reportDraw, MergedConfiguration mergedConfiguration, Rect backDropFrame, boolean forceLayout, boolean alwaysConsumeSystemBars, int displayId, DisplayCutout.ParcelableWrapper displayCutout) {
            TaskSnapshotSurface taskSnapshotSurface;
            if (!(mergedConfiguration == null || (taskSnapshotSurface = this.mOuter) == null || taskSnapshotSurface.mOrientationOnCreation == mergedConfiguration.getMergedConfiguration().orientation)) {
                Handler access$400 = TaskSnapshotSurface.sHandler;
                TaskSnapshotSurface taskSnapshotSurface2 = this.mOuter;
                Objects.requireNonNull(taskSnapshotSurface2);
                access$400.post(new Runnable() {
                    public final void run() {
                        TaskSnapshotSurface.this.remove();
                    }
                });
            }
            if (reportDraw) {
                TaskSnapshotSurface.sHandler.obtainMessage(0, this.mOuter).sendToTarget();
            }
        }
    }

    static class SystemBarBackgroundPainter {
        private final Rect mContentInsets = new Rect();
        private final int mNavigationBarColor;
        private final Paint mNavigationBarPaint = new Paint();
        private final float mScale;
        private final Rect mStableInsets = new Rect();
        private final int mStatusBarColor;
        private final Paint mStatusBarPaint = new Paint();
        private final int mSysUiVis;
        private final int mWindowFlags;
        private final int mWindowPrivateFlags;

        SystemBarBackgroundPainter(int windowFlags, int windowPrivateFlags, int sysUiVis, ActivityManager.TaskDescription taskDescription, float scale) {
            this.mWindowFlags = windowFlags;
            this.mWindowPrivateFlags = windowPrivateFlags;
            this.mSysUiVis = sysUiVis;
            this.mScale = scale;
            Context context = ActivityThread.currentActivityThread().getSystemUiContext();
            int semiTransparent = context.getColor(17171011);
            this.mStatusBarColor = DecorView.calculateBarColor(windowFlags, BroadcastQueueInjector.FLAG_IMMUTABLE, semiTransparent, taskDescription.getStatusBarColor(), sysUiVis, 8192, taskDescription.getEnsureStatusBarContrastWhenTransparent());
            this.mNavigationBarColor = DecorView.calculateBarColor(windowFlags, 134217728, semiTransparent, taskDescription.getNavigationBarColor(), sysUiVis, 16, taskDescription.getEnsureNavigationBarContrastWhenTransparent() && context.getResources().getBoolean(17891486));
            this.mStatusBarPaint.setColor(this.mStatusBarColor);
            this.mNavigationBarPaint.setColor(this.mNavigationBarColor);
        }

        /* access modifiers changed from: package-private */
        public void setInsets(Rect contentInsets, Rect stableInsets) {
            this.mContentInsets.set(contentInsets);
            this.mStableInsets.set(stableInsets);
        }

        /* access modifiers changed from: package-private */
        public int getStatusBarColorViewHeight() {
            if (DecorView.STATUS_BAR_COLOR_VIEW_ATTRIBUTES.isVisible(this.mSysUiVis, this.mStatusBarColor, this.mWindowFlags, (this.mWindowPrivateFlags & 131072) != 0)) {
                return (int) (((float) DecorView.getColorViewTopInset(this.mStableInsets.top, this.mContentInsets.top)) * this.mScale);
            }
            return 0;
        }

        private boolean isNavigationBarColorViewVisible() {
            return DecorView.NAVIGATION_BAR_COLOR_VIEW_ATTRIBUTES.isVisible(this.mSysUiVis, this.mNavigationBarColor, this.mWindowFlags, (this.mWindowPrivateFlags & 131072) != 0);
        }

        /* access modifiers changed from: package-private */
        public void drawDecors(Canvas c, Rect alreadyDrawnFrame) {
            drawStatusBarBackground(c, alreadyDrawnFrame, getStatusBarColorViewHeight());
            drawNavigationBarBackground(c);
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public void drawStatusBarBackground(Canvas c, Rect alreadyDrawnFrame, int statusBarHeight) {
            if (statusBarHeight > 0 && Color.alpha(this.mStatusBarColor) != 0) {
                if (alreadyDrawnFrame == null || c.getWidth() > alreadyDrawnFrame.right) {
                    c.drawRect((float) (alreadyDrawnFrame != null ? alreadyDrawnFrame.right : 0), 0.0f, (float) (c.getWidth() - ((int) (((float) DecorView.getColorViewRightInset(this.mStableInsets.right, this.mContentInsets.right)) * this.mScale))), (float) statusBarHeight, this.mStatusBarPaint);
                }
            }
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public void drawNavigationBarBackground(Canvas c) {
            Rect navigationBarRect = new Rect();
            DecorView.getNavigationBarRect(c.getWidth(), c.getHeight(), this.mStableInsets, this.mContentInsets, navigationBarRect, this.mScale);
            if (isNavigationBarColorViewVisible() && Color.alpha(this.mNavigationBarColor) != 0 && !navigationBarRect.isEmpty()) {
                c.drawRect(navigationBarRect, this.mNavigationBarPaint);
            }
        }
    }
}
