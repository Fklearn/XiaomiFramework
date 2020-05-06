package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ParceledListSlice;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.DumpUtils;
import com.android.server.accessibility.AccessibilityManagerService;
import com.android.server.accessibility.FingerprintGestureDispatcher;
import com.android.server.accessibility.KeyEventDispatcher;
import com.android.server.wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class AbstractAccessibilityServiceConnection extends IAccessibilityServiceConnection.Stub implements ServiceConnection, IBinder.DeathRecipient, KeyEventDispatcher.KeyEventFilter, FingerprintGestureDispatcher.FingerprintGestureClient {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AbstractAccessibilityServiceConnection";
    protected final AccessibilityServiceInfo mAccessibilityServiceInfo;
    boolean mCaptureFingerprintGestures;
    final ComponentName mComponentName;
    protected final Context mContext;
    public Handler mEventDispatchHandler;
    int mEventTypes;
    int mFeedbackType;
    int mFetchFlags;
    private final GlobalActionPerformer mGlobalActionPerformer;
    final int mId;
    public final InvocationHandler mInvocationHandler;
    boolean mIsDefault;
    boolean mLastAccessibilityButtonCallbackState;
    protected final Object mLock;
    long mNotificationTimeout;
    final IBinder mOverlayWindowToken = new Binder();
    Set<String> mPackageNames = new HashSet();
    final SparseArray<AccessibilityEvent> mPendingEvents = new SparseArray<>();
    boolean mReceivedAccessibilityButtonCallbackSinceBind;
    boolean mRequestAccessibilityButton;
    boolean mRequestFilterKeyEvents;
    boolean mRequestTouchExplorationMode;
    boolean mRetrieveInteractiveWindows;
    protected final AccessibilityManagerService.SecurityPolicy mSecurityPolicy;
    IBinder mService;
    IAccessibilityServiceClient mServiceInterface;
    protected final SystemSupport mSystemSupport;
    boolean mUsesAccessibilityCache = false;
    private final WindowManagerInternal mWindowManagerService;

    public interface SystemSupport {
        void ensureWindowsAvailableTimed();

        MagnificationSpec getCompatibleMagnificationSpecLocked(int i);

        AccessibilityManagerService.RemoteAccessibilityConnection getConnectionLocked(int i);

        int getCurrentUserIdLocked();

        FingerprintGestureDispatcher getFingerprintGestureDispatcher();

        KeyEventDispatcher getKeyEventDispatcher();

        MagnificationController getMagnificationController();

        MotionEventInjector getMotionEventInjectorLocked();

        PendingIntent getPendingIntentActivity(Context context, int i, Intent intent, int i2);

        boolean isAccessibilityButtonShown();

        void onClientChangeLocked(boolean z);

        boolean performAccessibilityAction(int i, long j, int i2, Bundle bundle, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i4, long j2);

        void persistComponentNamesToSettingLocked(String str, Set<ComponentName> set, int i);

        IAccessibilityInteractionConnectionCallback replaceCallbackIfNeeded(IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i, int i2, int i3, long j);
    }

    /* access modifiers changed from: protected */
    public abstract boolean isCalledForCurrentUserLocked();

    public AbstractAccessibilityServiceConnection(Context context, ComponentName componentName, AccessibilityServiceInfo accessibilityServiceInfo, int id, Handler mainHandler, Object lock, AccessibilityManagerService.SecurityPolicy securityPolicy, SystemSupport systemSupport, WindowManagerInternal windowManagerInternal, GlobalActionPerformer globalActionPerfomer) {
        this.mContext = context;
        this.mWindowManagerService = windowManagerInternal;
        this.mId = id;
        this.mComponentName = componentName;
        this.mAccessibilityServiceInfo = accessibilityServiceInfo;
        this.mLock = lock;
        this.mSecurityPolicy = securityPolicy;
        this.mGlobalActionPerformer = globalActionPerfomer;
        this.mSystemSupport = systemSupport;
        this.mInvocationHandler = new InvocationHandler(mainHandler.getLooper());
        this.mEventDispatchHandler = new Handler(mainHandler.getLooper()) {
            public void handleMessage(Message message) {
                AbstractAccessibilityServiceConnection.this.notifyAccessibilityEventInternal(message.what, (AccessibilityEvent) message.obj, message.arg1 != 0);
            }
        };
        setDynamicallyConfigurableProperties(accessibilityServiceInfo);
    }

    public boolean onKeyEvent(KeyEvent keyEvent, int sequenceNumber) {
        if (!this.mRequestFilterKeyEvents || this.mServiceInterface == null || (this.mAccessibilityServiceInfo.getCapabilities() & 8) == 0 || !this.mSecurityPolicy.checkAccessibilityAccess(this)) {
            return false;
        }
        try {
            this.mServiceInterface.onKeyEvent(keyEvent, sequenceNumber);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setDynamicallyConfigurableProperties(AccessibilityServiceInfo info) {
        this.mEventTypes = info.eventTypes;
        this.mFeedbackType = info.feedbackType;
        String[] packageNames = info.packageNames;
        if (packageNames != null) {
            this.mPackageNames.addAll(Arrays.asList(packageNames));
        }
        this.mNotificationTimeout = info.notificationTimeout;
        boolean z = true;
        this.mIsDefault = (info.flags & 1) != 0;
        if (supportsFlagForNotImportantViews(info)) {
            if ((info.flags & 2) != 0) {
                this.mFetchFlags |= 8;
            } else {
                this.mFetchFlags &= -9;
            }
        }
        if ((info.flags & 16) != 0) {
            this.mFetchFlags |= 16;
        } else {
            this.mFetchFlags &= -17;
        }
        this.mRequestTouchExplorationMode = (info.flags & 4) != 0;
        this.mRequestFilterKeyEvents = (info.flags & 32) != 0;
        this.mRetrieveInteractiveWindows = (info.flags & 64) != 0;
        this.mCaptureFingerprintGestures = (info.flags & 512) != 0;
        if ((info.flags & 256) == 0) {
            z = false;
        }
        this.mRequestAccessibilityButton = z;
    }

    /* access modifiers changed from: protected */
    public boolean supportsFlagForNotImportantViews(AccessibilityServiceInfo info) {
        return info.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion >= 16;
    }

    public boolean canReceiveEventsLocked() {
        return (this.mEventTypes == 0 || this.mFeedbackType == 0 || this.mService == null) ? false : true;
    }

    public void setOnKeyEventResult(boolean handled, int sequence) {
        this.mSystemSupport.getKeyEventDispatcher().setOnKeyEventResult(this, handled, sequence);
    }

    public AccessibilityServiceInfo getServiceInfo() {
        AccessibilityServiceInfo accessibilityServiceInfo;
        synchronized (this.mLock) {
            accessibilityServiceInfo = this.mAccessibilityServiceInfo;
        }
        return accessibilityServiceInfo;
    }

    public int getCapabilities() {
        return this.mAccessibilityServiceInfo.getCapabilities();
    }

    /* access modifiers changed from: package-private */
    public int getRelevantEventTypes() {
        int i;
        if (this.mUsesAccessibilityCache) {
            i = 4307005;
        } else {
            i = 32;
        }
        return i | this.mEventTypes;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setServiceInfo(AccessibilityServiceInfo info) {
        long identity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                AccessibilityServiceInfo oldInfo = this.mAccessibilityServiceInfo;
                if (oldInfo != null) {
                    oldInfo.updateDynamicallyConfigurableProperties(info);
                    setDynamicallyConfigurableProperties(oldInfo);
                } else {
                    setDynamicallyConfigurableProperties(info);
                }
                this.mSystemSupport.onClientChangeLocked(true);
            }
            Binder.restoreCallingIdentity(identity);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    public List<AccessibilityWindowInfo> getWindows() {
        this.mSystemSupport.ensureWindowsAvailableTimed();
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return null;
            }
            if (!this.mSecurityPolicy.canRetrieveWindowsLocked(this)) {
                return null;
            }
            if (this.mSecurityPolicy.mWindows == null) {
                return null;
            }
            if (!this.mSecurityPolicy.checkAccessibilityAccess(this)) {
                return null;
            }
            List<AccessibilityWindowInfo> windows = new ArrayList<>();
            int windowCount = this.mSecurityPolicy.mWindows.size();
            for (int i = 0; i < windowCount; i++) {
                AccessibilityWindowInfo windowClone = AccessibilityWindowInfo.obtain(this.mSecurityPolicy.mWindows.get(i));
                windowClone.setConnectionId(this.mId);
                windows.add(windowClone);
            }
            return windows;
        }
    }

    public AccessibilityWindowInfo getWindow(int windowId) {
        this.mSystemSupport.ensureWindowsAvailableTimed();
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return null;
            }
            if (!this.mSecurityPolicy.canRetrieveWindowsLocked(this)) {
                return null;
            }
            if (!this.mSecurityPolicy.checkAccessibilityAccess(this)) {
                return null;
            }
            AccessibilityWindowInfo window = this.mSecurityPolicy.findA11yWindowInfoById(windowId);
            if (window == null) {
                return null;
            }
            AccessibilityWindowInfo windowClone = AccessibilityWindowInfo.obtain(window);
            windowClone.setConnectionId(this.mId);
            return windowClone;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004b, code lost:
        r3 = android.os.Binder.getCallingPid();
        r5 = r1.mSystemSupport.replaceCallbackIfNeeded(r32, r12, r31, r3, r33);
        r6 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r13.getRemote().findAccessibilityNodeInfosByViewId(r28, r30, r2, r31, r5, r1.mFetchFlags, r3, r33, r25);
        r0 = r1.mSecurityPolicy.computeValidReportedPackages(r13.getPackageName(), r13.getUid());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0088, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008b, code lost:
        if (r2 == null) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0095, code lost:
        if (android.os.Binder.isProxy(r13.getRemote()) == false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0097, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009a, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ab, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ae, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b0, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00bf, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c2, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] findAccessibilityNodeInfosByViewId(int r27, long r28, java.lang.String r30, int r31, android.view.accessibility.IAccessibilityInteractionConnectionCallback r32, long r33) throws android.os.RemoteException {
        /*
            r26 = this;
            r1 = r26
            android.graphics.Region r2 = android.graphics.Region.obtain()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            r0 = 1
            r1.mUsesAccessibilityCache = r0     // Catch:{ all -> 0x00c3 }
            boolean r0 = r26.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x00c3 }
            r4 = 0
            if (r0 != 0) goto L_0x0015
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0015:
            int r0 = r26.resolveAccessibilityWindowIdLocked(r27)     // Catch:{ all -> 0x00c3 }
            r12 = r0
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r0 = r0.canGetAccessibilityNodeInfoLocked(r1, r12)     // Catch:{ all -> 0x00c3 }
            if (r0 != 0) goto L_0x0024
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0024:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$RemoteAccessibilityConnection r5 = r5.getConnectionLocked(r12)     // Catch:{ all -> 0x00c3 }
            r13 = r5
            if (r13 != 0) goto L_0x002f
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x002f:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r5 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r5 = r5.computePartialInteractiveRegionForWindowLocked(r12, r2)     // Catch:{ all -> 0x00c3 }
            if (r5 != 0) goto L_0x003b
            r2.recycle()     // Catch:{ all -> 0x00c3 }
            r2 = 0
        L_0x003b:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            android.view.MagnificationSpec r25 = r5.getCompatibleMagnificationSpecLocked(r12)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.checkAccessibilityAccess(r1)
            if (r0 != 0) goto L_0x004b
            return r4
        L_0x004b:
            int r3 = android.os.Binder.getCallingPid()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport
            r6 = r32
            r7 = r12
            r8 = r31
            r9 = r3
            r10 = r33
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r5 = r5.replaceCallbackIfNeeded(r6, r7, r8, r9, r10)
            long r6 = android.os.Binder.clearCallingIdentity()
            android.view.accessibility.IAccessibilityInteractionConnection r14 = r13.getRemote()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r0 = r1.mFetchFlags     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            r15 = r28
            r17 = r30
            r18 = r2
            r19 = r31
            r20 = r5
            r21 = r0
            r22 = r3
            r23 = r33
            r14.findAccessibilityNodeInfosByViewId(r15, r17, r18, r19, r20, r21, r22, r23, r25)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String r8 = r13.getPackageName()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r9 = r13.getUid()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String[] r0 = r0.computeValidReportedPackages(r8, r9)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x009a
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x009a
            r2.recycle()
        L_0x009a:
            return r0
        L_0x009b:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00ae
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x00ae
            r2.recycle()
        L_0x00ae:
            throw r0
        L_0x00af:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00c2
            android.view.accessibility.IAccessibilityInteractionConnection r0 = r13.getRemote()
            boolean r0 = android.os.Binder.isProxy(r0)
            if (r0 == 0) goto L_0x00c2
            r2.recycle()
        L_0x00c2:
            return r4
        L_0x00c3:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.findAccessibilityNodeInfosByViewId(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004b, code lost:
        r3 = android.os.Binder.getCallingPid();
        r5 = r1.mSystemSupport.replaceCallbackIfNeeded(r32, r12, r31, r3, r33);
        r6 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r13.getRemote().findAccessibilityNodeInfosByText(r28, r30, r2, r31, r5, r1.mFetchFlags, r3, r33, r25);
        r0 = r1.mSecurityPolicy.computeValidReportedPackages(r13.getPackageName(), r13.getUid());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0088, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008b, code lost:
        if (r2 == null) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0095, code lost:
        if (android.os.Binder.isProxy(r13.getRemote()) == false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0097, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009a, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ab, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ae, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b0, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00bf, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c2, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] findAccessibilityNodeInfosByText(int r27, long r28, java.lang.String r30, int r31, android.view.accessibility.IAccessibilityInteractionConnectionCallback r32, long r33) throws android.os.RemoteException {
        /*
            r26 = this;
            r1 = r26
            android.graphics.Region r2 = android.graphics.Region.obtain()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            r0 = 1
            r1.mUsesAccessibilityCache = r0     // Catch:{ all -> 0x00c3 }
            boolean r0 = r26.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x00c3 }
            r4 = 0
            if (r0 != 0) goto L_0x0015
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0015:
            int r0 = r26.resolveAccessibilityWindowIdLocked(r27)     // Catch:{ all -> 0x00c3 }
            r12 = r0
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r0 = r0.canGetAccessibilityNodeInfoLocked(r1, r12)     // Catch:{ all -> 0x00c3 }
            if (r0 != 0) goto L_0x0024
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0024:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$RemoteAccessibilityConnection r5 = r5.getConnectionLocked(r12)     // Catch:{ all -> 0x00c3 }
            r13 = r5
            if (r13 != 0) goto L_0x002f
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x002f:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r5 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r5 = r5.computePartialInteractiveRegionForWindowLocked(r12, r2)     // Catch:{ all -> 0x00c3 }
            if (r5 != 0) goto L_0x003b
            r2.recycle()     // Catch:{ all -> 0x00c3 }
            r2 = 0
        L_0x003b:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            android.view.MagnificationSpec r25 = r5.getCompatibleMagnificationSpecLocked(r12)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.checkAccessibilityAccess(r1)
            if (r0 != 0) goto L_0x004b
            return r4
        L_0x004b:
            int r3 = android.os.Binder.getCallingPid()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport
            r6 = r32
            r7 = r12
            r8 = r31
            r9 = r3
            r10 = r33
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r5 = r5.replaceCallbackIfNeeded(r6, r7, r8, r9, r10)
            long r6 = android.os.Binder.clearCallingIdentity()
            android.view.accessibility.IAccessibilityInteractionConnection r14 = r13.getRemote()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r0 = r1.mFetchFlags     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            r15 = r28
            r17 = r30
            r18 = r2
            r19 = r31
            r20 = r5
            r21 = r0
            r22 = r3
            r23 = r33
            r14.findAccessibilityNodeInfosByText(r15, r17, r18, r19, r20, r21, r22, r23, r25)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String r8 = r13.getPackageName()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r9 = r13.getUid()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String[] r0 = r0.computeValidReportedPackages(r8, r9)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x009a
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x009a
            r2.recycle()
        L_0x009a:
            return r0
        L_0x009b:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00ae
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x00ae
            r2.recycle()
        L_0x00ae:
            throw r0
        L_0x00af:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00c2
            android.view.accessibility.IAccessibilityInteractionConnection r0 = r13.getRemote()
            boolean r0 = android.os.Binder.isProxy(r0)
            if (r0 == 0) goto L_0x00c2
            r2.recycle()
        L_0x00c2:
            return r4
        L_0x00c3:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.findAccessibilityNodeInfosByText(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004b, code lost:
        r3 = android.os.Binder.getCallingPid();
        r5 = r1.mSystemSupport.replaceCallbackIfNeeded(r31, r12, r30, r3, r33);
        r6 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r13.getRemote().findAccessibilityNodeInfoByAccessibilityId(r28, r2, r30, r5, r1.mFetchFlags | r32, r3, r33, r24, r35);
        r0 = r1.mSecurityPolicy.computeValidReportedPackages(r13.getPackageName(), r13.getUid());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0088, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008b, code lost:
        if (r2 == null) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0095, code lost:
        if (android.os.Binder.isProxy(r13.getRemote()) == false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0097, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009a, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ab, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ae, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b0, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00bf, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c2, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] findAccessibilityNodeInfoByAccessibilityId(int r27, long r28, int r30, android.view.accessibility.IAccessibilityInteractionConnectionCallback r31, int r32, long r33, android.os.Bundle r35) throws android.os.RemoteException {
        /*
            r26 = this;
            r1 = r26
            android.graphics.Region r2 = android.graphics.Region.obtain()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            r0 = 1
            r1.mUsesAccessibilityCache = r0     // Catch:{ all -> 0x00c3 }
            boolean r0 = r26.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x00c3 }
            r4 = 0
            if (r0 != 0) goto L_0x0015
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0015:
            int r0 = r26.resolveAccessibilityWindowIdLocked(r27)     // Catch:{ all -> 0x00c3 }
            r12 = r0
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r0 = r0.canGetAccessibilityNodeInfoLocked(r1, r12)     // Catch:{ all -> 0x00c3 }
            if (r0 != 0) goto L_0x0024
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x0024:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$RemoteAccessibilityConnection r5 = r5.getConnectionLocked(r12)     // Catch:{ all -> 0x00c3 }
            r13 = r5
            if (r13 != 0) goto L_0x002f
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            return r4
        L_0x002f:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r5 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c3 }
            boolean r5 = r5.computePartialInteractiveRegionForWindowLocked(r12, r2)     // Catch:{ all -> 0x00c3 }
            if (r5 != 0) goto L_0x003b
            r2.recycle()     // Catch:{ all -> 0x00c3 }
            r2 = 0
        L_0x003b:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c3 }
            android.view.MagnificationSpec r24 = r5.getCompatibleMagnificationSpecLocked(r12)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.checkAccessibilityAccess(r1)
            if (r0 != 0) goto L_0x004b
            return r4
        L_0x004b:
            int r3 = android.os.Binder.getCallingPid()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport
            r6 = r31
            r7 = r12
            r8 = r30
            r9 = r3
            r10 = r33
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r5 = r5.replaceCallbackIfNeeded(r6, r7, r8, r9, r10)
            long r6 = android.os.Binder.clearCallingIdentity()
            android.view.accessibility.IAccessibilityInteractionConnection r14 = r13.getRemote()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r0 = r1.mFetchFlags     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            r20 = r0 | r32
            r15 = r28
            r17 = r2
            r18 = r30
            r19 = r5
            r21 = r3
            r22 = r33
            r25 = r35
            r14.findAccessibilityNodeInfoByAccessibilityId(r15, r17, r18, r19, r20, r21, r22, r24, r25)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String r8 = r13.getPackageName()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            int r9 = r13.getUid()     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            java.lang.String[] r0 = r0.computeValidReportedPackages(r8, r9)     // Catch:{ RemoteException -> 0x00af, all -> 0x009b }
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x009a
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x009a
            r2.recycle()
        L_0x009a:
            return r0
        L_0x009b:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00ae
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x00ae
            r2.recycle()
        L_0x00ae:
            throw r0
        L_0x00af:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00c2
            android.view.accessibility.IAccessibilityInteractionConnection r0 = r13.getRemote()
            boolean r0 = android.os.Binder.isProxy(r0)
            if (r0 == 0) goto L_0x00c2
            r2.recycle()
        L_0x00c2:
            return r4
        L_0x00c3:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.findAccessibilityNodeInfoByAccessibilityId(int, long, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, long, android.os.Bundle):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004d, code lost:
        r3 = android.os.Binder.getCallingPid();
        r19 = r1.mSystemSupport.replaceCallbackIfNeeded(r29, r14, r28, r3, r30);
        r20 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0069, code lost:
        r22 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r18.getRemote().findFocus(r25, r27, r2, r28, r19, r1.mFetchFlags, r3, r30, r17);
        r0 = r1.mSecurityPolicy.computeValidReportedPackages(r18.getPackageName(), r18.getUid());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0088, code lost:
        android.os.Binder.restoreCallingIdentity(r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008b, code lost:
        if (r2 == null) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0095, code lost:
        if (android.os.Binder.isProxy(r18.getRemote()) == false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0097, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x009a, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a0, code lost:
        r22 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a2, code lost:
        android.os.Binder.restoreCallingIdentity(r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00b1, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00b4, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b6, code lost:
        r22 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00b8, code lost:
        android.os.Binder.restoreCallingIdentity(r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00c7, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ca, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] findFocus(int r24, long r25, int r27, int r28, android.view.accessibility.IAccessibilityInteractionConnectionCallback r29, long r30) throws android.os.RemoteException {
        /*
            r23 = this;
            r1 = r23
            android.graphics.Region r2 = android.graphics.Region.obtain()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            boolean r0 = r23.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x00cb }
            r4 = 0
            if (r0 != 0) goto L_0x0012
            monitor-exit(r3)     // Catch:{ all -> 0x00cb }
            return r4
        L_0x0012:
            r5 = r24
            r15 = r27
            int r0 = r1.resolveAccessibilityWindowIdForFindFocusLocked(r5, r15)     // Catch:{ all -> 0x00d0 }
            r14 = r0
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x00d0 }
            boolean r0 = r0.canGetAccessibilityNodeInfoLocked(r1, r14)     // Catch:{ all -> 0x00d0 }
            if (r0 != 0) goto L_0x0025
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            return r4
        L_0x0025:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r6 = r1.mSystemSupport     // Catch:{ all -> 0x00d0 }
            com.android.server.accessibility.AccessibilityManagerService$RemoteAccessibilityConnection r6 = r6.getConnectionLocked(r14)     // Catch:{ all -> 0x00d0 }
            r18 = r6
            if (r18 != 0) goto L_0x0031
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            return r4
        L_0x0031:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r6 = r1.mSecurityPolicy     // Catch:{ all -> 0x00d0 }
            boolean r6 = r6.computePartialInteractiveRegionForWindowLocked(r14, r2)     // Catch:{ all -> 0x00d0 }
            if (r6 != 0) goto L_0x003d
            r2.recycle()     // Catch:{ all -> 0x00d0 }
            r2 = 0
        L_0x003d:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r6 = r1.mSystemSupport     // Catch:{ all -> 0x00d0 }
            android.view.MagnificationSpec r17 = r6.getCompatibleMagnificationSpecLocked(r14)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.checkAccessibilityAccess(r1)
            if (r0 != 0) goto L_0x004d
            return r4
        L_0x004d:
            int r3 = android.os.Binder.getCallingPid()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r6 = r1.mSystemSupport
            r7 = r29
            r8 = r14
            r9 = r28
            r10 = r3
            r11 = r30
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r19 = r6.replaceCallbackIfNeeded(r7, r8, r9, r10, r11)
            long r20 = android.os.Binder.clearCallingIdentity()
            android.view.accessibility.IAccessibilityInteractionConnection r6 = r18.getRemote()     // Catch:{ RemoteException -> 0x00b5, all -> 0x009f }
            int r13 = r1.mFetchFlags     // Catch:{ RemoteException -> 0x00b5, all -> 0x009f }
            r7 = r25
            r9 = r27
            r10 = r2
            r11 = r28
            r12 = r19
            r22 = r14
            r14 = r3
            r15 = r30
            r6.findFocus(r7, r9, r10, r11, r12, r13, r14, r15, r17)     // Catch:{ RemoteException -> 0x009d, all -> 0x009b }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ RemoteException -> 0x009d, all -> 0x009b }
            java.lang.String r6 = r18.getPackageName()     // Catch:{ RemoteException -> 0x009d, all -> 0x009b }
            int r7 = r18.getUid()     // Catch:{ RemoteException -> 0x009d, all -> 0x009b }
            java.lang.String[] r0 = r0.computeValidReportedPackages(r6, r7)     // Catch:{ RemoteException -> 0x009d, all -> 0x009b }
            android.os.Binder.restoreCallingIdentity(r20)
            if (r2 == 0) goto L_0x009a
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r18.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x009a
            r2.recycle()
        L_0x009a:
            return r0
        L_0x009b:
            r0 = move-exception
            goto L_0x00a2
        L_0x009d:
            r0 = move-exception
            goto L_0x00b8
        L_0x009f:
            r0 = move-exception
            r22 = r14
        L_0x00a2:
            android.os.Binder.restoreCallingIdentity(r20)
            if (r2 == 0) goto L_0x00b4
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r18.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x00b4
            r2.recycle()
        L_0x00b4:
            throw r0
        L_0x00b5:
            r0 = move-exception
            r22 = r14
        L_0x00b8:
            android.os.Binder.restoreCallingIdentity(r20)
            if (r2 == 0) goto L_0x00ca
            android.view.accessibility.IAccessibilityInteractionConnection r0 = r18.getRemote()
            boolean r0 = android.os.Binder.isProxy(r0)
            if (r0 == 0) goto L_0x00ca
            r2.recycle()
        L_0x00ca:
            return r4
        L_0x00cb:
            r0 = move-exception
            r5 = r24
        L_0x00ce:
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            throw r0
        L_0x00d0:
            r0 = move-exception
            goto L_0x00ce
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.findFocus(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0045, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0048, code lost:
        r3 = android.os.Binder.getCallingPid();
        r5 = r1.mSystemSupport.replaceCallbackIfNeeded(r32, r12, r31, r3, r33);
        r6 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r13.getRemote().focusSearch(r28, r30, r2, r31, r5, r1.mFetchFlags, r3, r33, r25);
        r0 = r1.mSecurityPolicy.computeValidReportedPackages(r13.getPackageName(), r13.getUid());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0085, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0088, code lost:
        if (r2 == null) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0092, code lost:
        if (android.os.Binder.isProxy(r13.getRemote()) == false) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0094, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0097, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0098, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0099, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a8, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ab, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ad, code lost:
        android.os.Binder.restoreCallingIdentity(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00bc, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00bf, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] focusSearch(int r27, long r28, int r30, int r31, android.view.accessibility.IAccessibilityInteractionConnectionCallback r32, long r33) throws android.os.RemoteException {
        /*
            r26 = this;
            r1 = r26
            android.graphics.Region r2 = android.graphics.Region.obtain()
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            boolean r0 = r26.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x00c0 }
            r4 = 0
            if (r0 != 0) goto L_0x0012
            monitor-exit(r3)     // Catch:{ all -> 0x00c0 }
            return r4
        L_0x0012:
            int r0 = r26.resolveAccessibilityWindowIdLocked(r27)     // Catch:{ all -> 0x00c0 }
            r12 = r0
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c0 }
            boolean r0 = r0.canGetAccessibilityNodeInfoLocked(r1, r12)     // Catch:{ all -> 0x00c0 }
            if (r0 != 0) goto L_0x0021
            monitor-exit(r3)     // Catch:{ all -> 0x00c0 }
            return r4
        L_0x0021:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c0 }
            com.android.server.accessibility.AccessibilityManagerService$RemoteAccessibilityConnection r5 = r5.getConnectionLocked(r12)     // Catch:{ all -> 0x00c0 }
            r13 = r5
            if (r13 != 0) goto L_0x002c
            monitor-exit(r3)     // Catch:{ all -> 0x00c0 }
            return r4
        L_0x002c:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r5 = r1.mSecurityPolicy     // Catch:{ all -> 0x00c0 }
            boolean r5 = r5.computePartialInteractiveRegionForWindowLocked(r12, r2)     // Catch:{ all -> 0x00c0 }
            if (r5 != 0) goto L_0x0038
            r2.recycle()     // Catch:{ all -> 0x00c0 }
            r2 = 0
        L_0x0038:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport     // Catch:{ all -> 0x00c0 }
            android.view.MagnificationSpec r25 = r5.getCompatibleMagnificationSpecLocked(r12)     // Catch:{ all -> 0x00c0 }
            monitor-exit(r3)     // Catch:{ all -> 0x00c0 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.checkAccessibilityAccess(r1)
            if (r0 != 0) goto L_0x0048
            return r4
        L_0x0048:
            int r3 = android.os.Binder.getCallingPid()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport
            r6 = r32
            r7 = r12
            r8 = r31
            r9 = r3
            r10 = r33
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r5 = r5.replaceCallbackIfNeeded(r6, r7, r8, r9, r10)
            long r6 = android.os.Binder.clearCallingIdentity()
            android.view.accessibility.IAccessibilityInteractionConnection r14 = r13.getRemote()     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            int r0 = r1.mFetchFlags     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            r15 = r28
            r17 = r30
            r18 = r2
            r19 = r31
            r20 = r5
            r21 = r0
            r22 = r3
            r23 = r33
            r14.focusSearch(r15, r17, r18, r19, r20, r21, r22, r23, r25)     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            java.lang.String r8 = r13.getPackageName()     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            int r9 = r13.getUid()     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            java.lang.String[] r0 = r0.computeValidReportedPackages(r8, r9)     // Catch:{ RemoteException -> 0x00ac, all -> 0x0098 }
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x0097
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x0097
            r2.recycle()
        L_0x0097:
            return r0
        L_0x0098:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00ab
            android.view.accessibility.IAccessibilityInteractionConnection r4 = r13.getRemote()
            boolean r4 = android.os.Binder.isProxy(r4)
            if (r4 == 0) goto L_0x00ab
            r2.recycle()
        L_0x00ab:
            throw r0
        L_0x00ac:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r6)
            if (r2 == 0) goto L_0x00bf
            android.view.accessibility.IAccessibilityInteractionConnection r0 = r13.getRemote()
            boolean r0 = android.os.Binder.isProxy(r0)
            if (r0 == 0) goto L_0x00bf
            r2.recycle()
        L_0x00bf:
            return r4
        L_0x00c0:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c0 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.focusSearch(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):java.lang.String[]");
    }

    public void sendGesture(int sequence, ParceledListSlice gestureSteps) {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0024, code lost:
        if (r1.mSecurityPolicy.checkAccessibilityAccess(r1) != false) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003c, code lost:
        return r1.mSystemSupport.performAccessibilityAction(r0, r18, r20, r21, r22, r23, r1.mFetchFlags, r24);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityAction(int r17, long r18, int r20, android.os.Bundle r21, int r22, android.view.accessibility.IAccessibilityInteractionConnectionCallback r23, long r24) throws android.os.RemoteException {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            boolean r0 = r16.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x003d }
            r4 = 0
            if (r0 != 0) goto L_0x000f
            monitor-exit(r3)     // Catch:{ all -> 0x003d }
            return r4
        L_0x000f:
            int r0 = r16.resolveAccessibilityWindowIdLocked(r17)     // Catch:{ all -> 0x003d }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r5 = r1.mSecurityPolicy     // Catch:{ all -> 0x003d }
            boolean r5 = r5.canGetAccessibilityNodeInfoLocked(r1, r0)     // Catch:{ all -> 0x003d }
            if (r5 != 0) goto L_0x001d
            monitor-exit(r3)     // Catch:{ all -> 0x003d }
            return r4
        L_0x001d:
            monitor-exit(r3)     // Catch:{ all -> 0x003d }
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r3 = r1.mSecurityPolicy
            boolean r3 = r3.checkAccessibilityAccess(r1)
            if (r3 != 0) goto L_0x0027
            return r4
        L_0x0027:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r5 = r1.mSystemSupport
            int r13 = r1.mFetchFlags
            r6 = r0
            r7 = r18
            r9 = r20
            r10 = r21
            r11 = r22
            r12 = r23
            r14 = r24
            boolean r3 = r5.performAccessibilityAction(r6, r7, r9, r10, r11, r12, r13, r14)
            return r3
        L_0x003d:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.performAccessibilityAction(int, long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
    }

    public boolean performGlobalAction(int action) {
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return false;
            }
            return this.mGlobalActionPerformer.performGlobalAction(action);
        }
    }

    public boolean isFingerprintGestureDetectionAvailable() {
        FingerprintGestureDispatcher dispatcher;
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint") && isCapturingFingerprintGestures() && (dispatcher = this.mSystemSupport.getFingerprintGestureDispatcher()) != null && dispatcher.isFingerprintGestureDetectionAvailable()) {
            return true;
        }
        return false;
    }

    public float getMagnificationScale(int displayId) {
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return 1.0f;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                return this.mSystemSupport.getMagnificationController().getScale(displayId);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Region getMagnificationRegion(int r8) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            android.graphics.Region r1 = android.graphics.Region.obtain()     // Catch:{ all -> 0x0037 }
            boolean r2 = r7.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x0037 }
            if (r2 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            return r1
        L_0x000f:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r2 = r7.mSystemSupport     // Catch:{ all -> 0x0037 }
            com.android.server.accessibility.MagnificationController r2 = r2.getMagnificationController()     // Catch:{ all -> 0x0037 }
            boolean r3 = r7.registerMagnificationIfNeeded(r8, r2)     // Catch:{ all -> 0x0037 }
            long r4 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0037 }
            r2.getMagnificationRegion(r8, r1)     // Catch:{ all -> 0x002c }
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0037 }
            if (r3 == 0) goto L_0x002a
            r2.unregister(r8)     // Catch:{ all -> 0x0037 }
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            return r1
        L_0x002c:
            r6 = move-exception
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0037 }
            if (r3 == 0) goto L_0x0035
            r2.unregister(r8)     // Catch:{ all -> 0x0037 }
        L_0x0035:
            throw r6     // Catch:{ all -> 0x0037 }
        L_0x0037:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.getMagnificationRegion(int):android.graphics.Region");
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getMagnificationCenterX(int r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            boolean r1 = r6.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x0034 }
            if (r1 != 0) goto L_0x000c
            r1 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return r1
        L_0x000c:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r1 = r6.mSystemSupport     // Catch:{ all -> 0x0034 }
            com.android.server.accessibility.MagnificationController r1 = r1.getMagnificationController()     // Catch:{ all -> 0x0034 }
            boolean r2 = r6.registerMagnificationIfNeeded(r7, r1)     // Catch:{ all -> 0x0034 }
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0034 }
            float r5 = r1.getCenterX(r7)     // Catch:{ all -> 0x0029 }
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0027
            r1.unregister(r7)     // Catch:{ all -> 0x0034 }
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return r5
        L_0x0029:
            r5 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0032
            r1.unregister(r7)     // Catch:{ all -> 0x0034 }
        L_0x0032:
            throw r5     // Catch:{ all -> 0x0034 }
        L_0x0034:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.getMagnificationCenterX(int):float");
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getMagnificationCenterY(int r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            boolean r1 = r6.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x0034 }
            if (r1 != 0) goto L_0x000c
            r1 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return r1
        L_0x000c:
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r1 = r6.mSystemSupport     // Catch:{ all -> 0x0034 }
            com.android.server.accessibility.MagnificationController r1 = r1.getMagnificationController()     // Catch:{ all -> 0x0034 }
            boolean r2 = r6.registerMagnificationIfNeeded(r7, r1)     // Catch:{ all -> 0x0034 }
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0034 }
            float r5 = r1.getCenterY(r7)     // Catch:{ all -> 0x0029 }
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0027
            r1.unregister(r7)     // Catch:{ all -> 0x0034 }
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return r5
        L_0x0029:
            r5 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0032
            r1.unregister(r7)     // Catch:{ all -> 0x0034 }
        L_0x0032:
            throw r5     // Catch:{ all -> 0x0034 }
        L_0x0034:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.getMagnificationCenterY(int):float");
    }

    private boolean registerMagnificationIfNeeded(int displayId, MagnificationController magnificationController) {
        if (magnificationController.isRegistered(displayId) || !this.mSecurityPolicy.canControlMagnification(this)) {
            return false;
        }
        magnificationController.register(displayId);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
        r0 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r3 = r5.mSystemSupport.getMagnificationController();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        if (r3.reset(r6, r7) != false) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002b, code lost:
        if (r3.isMagnifying(r6) != false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002d, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0032, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0033, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0036, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean resetMagnification(int r6, boolean r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            boolean r1 = r5.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x0037 }
            r2 = 0
            if (r1 != 0) goto L_0x000c
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            return r2
        L_0x000c:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r1 = r5.mSecurityPolicy     // Catch:{ all -> 0x0037 }
            boolean r1 = r1.canControlMagnification(r5)     // Catch:{ all -> 0x0037 }
            if (r1 != 0) goto L_0x0016
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            return r2
        L_0x0016:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r3 = r5.mSystemSupport     // Catch:{ all -> 0x0032 }
            com.android.server.accessibility.MagnificationController r3 = r3.getMagnificationController()     // Catch:{ all -> 0x0032 }
            boolean r4 = r3.reset(r6, r7)     // Catch:{ all -> 0x0032 }
            if (r4 != 0) goto L_0x002d
            boolean r4 = r3.isMagnifying(r6)     // Catch:{ all -> 0x0032 }
            if (r4 != 0) goto L_0x002e
        L_0x002d:
            r2 = 1
        L_0x002e:
            android.os.Binder.restoreCallingIdentity(r0)
            return r2
        L_0x0032:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        L_0x0037:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.resetMagnification(int, boolean):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    public boolean setMagnificationScaleAndCenter(int displayId, float scale, float centerX, float centerY, boolean animate) {
        int i = displayId;
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return false;
            }
            if (!this.mSecurityPolicy.canControlMagnification(this)) {
                return false;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                MagnificationController magnificationController = this.mSystemSupport.getMagnificationController();
                if (!magnificationController.isRegistered(displayId)) {
                    magnificationController.register(displayId);
                }
                boolean scaleAndCenter = magnificationController.setScaleAndCenter(displayId, scale, centerX, centerY, animate, this.mId);
                return scaleAndCenter;
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void setMagnificationCallbackEnabled(int displayId, boolean enabled) {
        this.mInvocationHandler.setMagnificationCallbackEnabled(displayId, enabled);
    }

    public boolean isMagnificationCallbackEnabled(int displayId) {
        return this.mInvocationHandler.isMagnificationCallbackEnabled(displayId);
    }

    public void setSoftKeyboardCallbackEnabled(boolean enabled) {
        this.mInvocationHandler.setSoftKeyboardCallbackEnabled(enabled);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, LOG_TAG, pw)) {
            synchronized (this.mLock) {
                pw.append("Service[label=" + this.mAccessibilityServiceInfo.getResolveInfo().loadLabel(this.mContext.getPackageManager()));
                pw.append(", feedbackType" + AccessibilityServiceInfo.feedbackTypeToString(this.mFeedbackType));
                pw.append(", capabilities=" + this.mAccessibilityServiceInfo.getCapabilities());
                pw.append(", eventTypes=" + AccessibilityEvent.eventTypeToString(this.mEventTypes));
                pw.append(", notificationTimeout=" + this.mNotificationTimeout);
                pw.append("]");
            }
        }
    }

    public void onAdded() {
        long identity = Binder.clearCallingIdentity();
        try {
            this.mWindowManagerService.addWindowToken(this.mOverlayWindowToken, 2032, 0);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void onRemoved() {
        long identity = Binder.clearCallingIdentity();
        try {
            this.mWindowManagerService.removeWindowToken(this.mOverlayWindowToken, true, 0);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void resetLocked() {
        this.mSystemSupport.getKeyEventDispatcher().flush(this);
        try {
            if (this.mServiceInterface != null) {
                this.mServiceInterface.init((IAccessibilityServiceConnection) null, this.mId, (IBinder) null);
            }
        } catch (RemoteException e) {
        }
        IBinder iBinder = this.mService;
        if (iBinder != null) {
            try {
                iBinder.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e2) {
                Slog.e(LOG_TAG, "Death link does not exist!", e2);
            }
            this.mService = null;
        }
        this.mServiceInterface = null;
        this.mReceivedAccessibilityButtonCallbackSinceBind = false;
    }

    public boolean isConnectedLocked() {
        return this.mService != null;
    }

    public void notifyAccessibilityEvent(AccessibilityEvent event) {
        Message message;
        synchronized (this.mLock) {
            int eventType = event.getEventType();
            boolean serviceWantsEvent = wantsEventLocked(event);
            int i = 1;
            boolean requiredForCacheConsistency = this.mUsesAccessibilityCache && (4307005 & eventType) != 0;
            if (!serviceWantsEvent && !requiredForCacheConsistency) {
                return;
            }
            if (this.mSecurityPolicy.checkAccessibilityAccess(this)) {
                AccessibilityEvent newEvent = AccessibilityEvent.obtain(event);
                if (this.mNotificationTimeout <= 0 || eventType == 2048) {
                    message = this.mEventDispatchHandler.obtainMessage(eventType, newEvent);
                } else {
                    AccessibilityEvent oldEvent = this.mPendingEvents.get(eventType);
                    this.mPendingEvents.put(eventType, newEvent);
                    if (oldEvent != null) {
                        this.mEventDispatchHandler.removeMessages(eventType);
                        oldEvent.recycle();
                    }
                    message = this.mEventDispatchHandler.obtainMessage(eventType);
                }
                if (!serviceWantsEvent) {
                    i = 0;
                }
                message.arg1 = i;
                this.mEventDispatchHandler.sendMessageDelayed(message, this.mNotificationTimeout);
            }
        }
    }

    private boolean wantsEventLocked(AccessibilityEvent event) {
        if (!canReceiveEventsLocked()) {
            return false;
        }
        if (event.getWindowId() != -1 && !event.isImportantForAccessibility() && (this.mFetchFlags & 8) == 0) {
            return false;
        }
        int eventType = event.getEventType();
        if ((this.mEventTypes & eventType) != eventType) {
            return false;
        }
        Set<String> packageNames = this.mPackageNames;
        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : null;
        if (packageNames.isEmpty() || packageNames.contains(packageName)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r1.onAccessibilityEvent(r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0040, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        android.util.Slog.e(LOG_TAG, "Error during sending " + r7 + " to " + r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0062, code lost:
        r7.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0065, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyAccessibilityEventInternal(int r6, android.view.accessibility.AccessibilityEvent r7, boolean r8) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            android.accessibilityservice.IAccessibilityServiceClient r1 = r5.mServiceInterface     // Catch:{ all -> 0x0066 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            return
        L_0x0009:
            if (r7 != 0) goto L_0x001d
            android.util.SparseArray<android.view.accessibility.AccessibilityEvent> r2 = r5.mPendingEvents     // Catch:{ all -> 0x0066 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x0066 }
            android.view.accessibility.AccessibilityEvent r2 = (android.view.accessibility.AccessibilityEvent) r2     // Catch:{ all -> 0x0066 }
            r7 = r2
            if (r7 != 0) goto L_0x0018
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            return
        L_0x0018:
            android.util.SparseArray<android.view.accessibility.AccessibilityEvent> r2 = r5.mPendingEvents     // Catch:{ all -> 0x0066 }
            r2.remove(r6)     // Catch:{ all -> 0x0066 }
        L_0x001d:
            com.android.server.accessibility.AccessibilityManagerService$SecurityPolicy r2 = r5.mSecurityPolicy     // Catch:{ all -> 0x0066 }
            boolean r2 = r2.canRetrieveWindowContentLocked(r5)     // Catch:{ all -> 0x0066 }
            if (r2 == 0) goto L_0x002b
            int r2 = r5.mId     // Catch:{ all -> 0x0066 }
            r7.setConnectionId(r2)     // Catch:{ all -> 0x0066 }
            goto L_0x0031
        L_0x002b:
            r2 = 0
            android.view.View r2 = (android.view.View) r2     // Catch:{ all -> 0x0066 }
            r7.setSource(r2)     // Catch:{ all -> 0x0066 }
        L_0x0031:
            r2 = 1
            r7.setSealed(r2)     // Catch:{ all -> 0x0066 }
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            r1.onAccessibilityEvent(r7, r8)     // Catch:{ RemoteException -> 0x0040 }
        L_0x003a:
            r7.recycle()
            goto L_0x0061
        L_0x003e:
            r0 = move-exception
            goto L_0x0062
        L_0x0040:
            r0 = move-exception
            java.lang.String r2 = "AbstractAccessibilityServiceConnection"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x003e }
            r3.<init>()     // Catch:{ all -> 0x003e }
            java.lang.String r4 = "Error during sending "
            r3.append(r4)     // Catch:{ all -> 0x003e }
            r3.append(r7)     // Catch:{ all -> 0x003e }
            java.lang.String r4 = " to "
            r3.append(r4)     // Catch:{ all -> 0x003e }
            r3.append(r1)     // Catch:{ all -> 0x003e }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x003e }
            android.util.Slog.e(r2, r3, r0)     // Catch:{ all -> 0x003e }
            goto L_0x003a
        L_0x0061:
            return
        L_0x0062:
            r7.recycle()
            throw r0
        L_0x0066:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0066 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AbstractAccessibilityServiceConnection.notifyAccessibilityEventInternal(int, android.view.accessibility.AccessibilityEvent, boolean):void");
    }

    public void notifyGesture(int gestureId) {
        this.mInvocationHandler.obtainMessage(1, gestureId, 0).sendToTarget();
    }

    public void notifyClearAccessibilityNodeInfoCache() {
        this.mInvocationHandler.sendEmptyMessage(2);
    }

    public void notifyMagnificationChangedLocked(int displayId, Region region, float scale, float centerX, float centerY) {
        this.mInvocationHandler.notifyMagnificationChangedLocked(displayId, region, scale, centerX, centerY);
    }

    public void notifySoftKeyboardShowModeChangedLocked(int showState) {
        this.mInvocationHandler.notifySoftKeyboardShowModeChangedLocked(showState);
    }

    public void notifyAccessibilityButtonClickedLocked() {
        this.mInvocationHandler.notifyAccessibilityButtonClickedLocked();
    }

    public void notifyAccessibilityButtonAvailabilityChangedLocked(boolean available) {
        this.mInvocationHandler.notifyAccessibilityButtonAvailabilityChangedLocked(available);
    }

    /* access modifiers changed from: private */
    public void notifyMagnificationChangedInternal(int displayId, Region region, float scale, float centerX, float centerY) {
        IAccessibilityServiceClient listener = getServiceInterfaceSafely();
        if (listener != null) {
            try {
                listener.onMagnificationChanged(displayId, region, scale, centerX, centerY);
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Error sending magnification changes to " + this.mService, re);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifySoftKeyboardShowModeChangedInternal(int showState) {
        IAccessibilityServiceClient listener = getServiceInterfaceSafely();
        if (listener != null) {
            try {
                listener.onSoftKeyboardShowModeChanged(showState);
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Error sending soft keyboard show mode changes to " + this.mService, re);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyAccessibilityButtonClickedInternal() {
        IAccessibilityServiceClient listener = getServiceInterfaceSafely();
        if (listener != null) {
            try {
                listener.onAccessibilityButtonClicked();
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Error sending accessibility button click to " + this.mService, re);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyAccessibilityButtonAvailabilityChangedInternal(boolean available) {
        if (!this.mReceivedAccessibilityButtonCallbackSinceBind || this.mLastAccessibilityButtonCallbackState != available) {
            this.mReceivedAccessibilityButtonCallbackSinceBind = true;
            this.mLastAccessibilityButtonCallbackState = available;
            IAccessibilityServiceClient listener = getServiceInterfaceSafely();
            if (listener != null) {
                try {
                    listener.onAccessibilityButtonAvailabilityChanged(available);
                } catch (RemoteException re) {
                    Slog.e(LOG_TAG, "Error sending accessibility button availability change to " + this.mService, re);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyGestureInternal(int gestureId) {
        IAccessibilityServiceClient listener = getServiceInterfaceSafely();
        if (listener != null) {
            try {
                listener.onGesture(gestureId);
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Error during sending gesture " + gestureId + " to " + this.mService, re);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyClearAccessibilityCacheInternal() {
        IAccessibilityServiceClient listener = getServiceInterfaceSafely();
        if (listener != null) {
            try {
                listener.clearAccessibilityCache();
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Error during requesting accessibility info cache to be cleared.", re);
            }
        }
    }

    private IAccessibilityServiceClient getServiceInterfaceSafely() {
        IAccessibilityServiceClient iAccessibilityServiceClient;
        synchronized (this.mLock) {
            iAccessibilityServiceClient = this.mServiceInterface;
        }
        return iAccessibilityServiceClient;
    }

    private int resolveAccessibilityWindowIdLocked(int accessibilityWindowId) {
        if (accessibilityWindowId == Integer.MAX_VALUE) {
            return this.mSecurityPolicy.getActiveWindowId();
        }
        return accessibilityWindowId;
    }

    private int resolveAccessibilityWindowIdForFindFocusLocked(int windowId, int focusType) {
        if (windowId == Integer.MAX_VALUE) {
            return this.mSecurityPolicy.mActiveWindowId;
        }
        if (windowId == -2) {
            if (focusType == 1) {
                return this.mSecurityPolicy.mFocusedWindowId;
            }
            if (focusType == 2) {
                return this.mSecurityPolicy.mAccessibilityFocusedWindowId;
            }
        }
        return windowId;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    private final class InvocationHandler extends Handler {
        public static final int MSG_CLEAR_ACCESSIBILITY_CACHE = 2;
        private static final int MSG_ON_ACCESSIBILITY_BUTTON_AVAILABILITY_CHANGED = 8;
        private static final int MSG_ON_ACCESSIBILITY_BUTTON_CLICKED = 7;
        public static final int MSG_ON_GESTURE = 1;
        private static final int MSG_ON_MAGNIFICATION_CHANGED = 5;
        private static final int MSG_ON_SOFT_KEYBOARD_STATE_CHANGED = 6;
        private boolean mIsSoftKeyboardCallbackEnabled = false;
        @GuardedBy({"mlock"})
        private final SparseArray<Boolean> mMagnificationCallbackState = new SparseArray<>(0);

        public InvocationHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message message) {
            int type = message.what;
            boolean available = true;
            if (type == 1) {
                AbstractAccessibilityServiceConnection.this.notifyGestureInternal(message.arg1);
            } else if (type == 2) {
                AbstractAccessibilityServiceConnection.this.notifyClearAccessibilityCacheInternal();
            } else if (type == 5) {
                SomeArgs args = (SomeArgs) message.obj;
                float scale = ((Float) args.arg2).floatValue();
                float centerX = ((Float) args.arg3).floatValue();
                float centerY = ((Float) args.arg4).floatValue();
                AbstractAccessibilityServiceConnection.this.notifyMagnificationChangedInternal(args.argi1, (Region) args.arg1, scale, centerX, centerY);
                args.recycle();
            } else if (type == 6) {
                AbstractAccessibilityServiceConnection.this.notifySoftKeyboardShowModeChangedInternal(message.arg1);
            } else if (type == 7) {
                AbstractAccessibilityServiceConnection.this.notifyAccessibilityButtonClickedInternal();
            } else if (type == 8) {
                if (message.arg1 == 0) {
                    available = false;
                }
                AbstractAccessibilityServiceConnection.this.notifyAccessibilityButtonAvailabilityChangedInternal(available);
            } else {
                throw new IllegalArgumentException("Unknown message: " + type);
            }
        }

        public void notifyMagnificationChangedLocked(int displayId, Region region, float scale, float centerX, float centerY) {
            synchronized (AbstractAccessibilityServiceConnection.this.mLock) {
                if (this.mMagnificationCallbackState.get(displayId) != null) {
                    SomeArgs args = SomeArgs.obtain();
                    args.arg1 = region;
                    args.arg2 = Float.valueOf(scale);
                    args.arg3 = Float.valueOf(centerX);
                    args.arg4 = Float.valueOf(centerY);
                    args.argi1 = displayId;
                    obtainMessage(5, args).sendToTarget();
                }
            }
        }

        public void setMagnificationCallbackEnabled(int displayId, boolean enabled) {
            synchronized (AbstractAccessibilityServiceConnection.this.mLock) {
                if (enabled) {
                    this.mMagnificationCallbackState.put(displayId, true);
                } else {
                    this.mMagnificationCallbackState.remove(displayId);
                }
            }
        }

        public boolean isMagnificationCallbackEnabled(int displayId) {
            boolean z;
            synchronized (AbstractAccessibilityServiceConnection.this.mLock) {
                z = this.mMagnificationCallbackState.get(displayId) != null;
            }
            return z;
        }

        public void notifySoftKeyboardShowModeChangedLocked(int showState) {
            if (this.mIsSoftKeyboardCallbackEnabled) {
                obtainMessage(6, showState, 0).sendToTarget();
            }
        }

        public void setSoftKeyboardCallbackEnabled(boolean enabled) {
            this.mIsSoftKeyboardCallbackEnabled = enabled;
        }

        public void notifyAccessibilityButtonClickedLocked() {
            obtainMessage(7).sendToTarget();
        }

        public void notifyAccessibilityButtonAvailabilityChangedLocked(boolean available) {
            obtainMessage(8, available, 0).sendToTarget();
        }
    }
}
