package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.accessibility.AbstractAccessibilityServiceConnection;
import com.android.server.accessibility.AccessibilityManagerService;
import com.android.server.pm.DumpState;
import com.android.server.wm.WindowManagerInternal;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

class AccessibilityServiceConnection extends AbstractAccessibilityServiceConnection {
    private static final String LOG_TAG = "AccessibilityServiceConnection";
    final Intent mIntent = new Intent().setComponent(this.mComponentName);
    private final Handler mMainHandler;
    final WeakReference<AccessibilityManagerService.UserState> mUserStateWeakReference;
    private boolean mWasConnectedAndDied;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AccessibilityServiceConnection(AccessibilityManagerService.UserState userState, Context context, ComponentName componentName, AccessibilityServiceInfo accessibilityServiceInfo, int id, Handler mainHandler, Object lock, AccessibilityManagerService.SecurityPolicy securityPolicy, AbstractAccessibilityServiceConnection.SystemSupport systemSupport, WindowManagerInternal windowManagerInternal, GlobalActionPerformer globalActionPerfomer) {
        super(context, componentName, accessibilityServiceInfo, id, mainHandler, lock, securityPolicy, systemSupport, windowManagerInternal, globalActionPerfomer);
        AccessibilityManagerService.UserState userState2 = userState;
        this.mUserStateWeakReference = new WeakReference<>(userState);
        this.mMainHandler = mainHandler;
        this.mIntent.putExtra("android.intent.extra.client_label", 17039442);
        long identity = Binder.clearCallingIdentity();
        try {
            this.mIntent.putExtra("android.intent.extra.client_intent", this.mSystemSupport.getPendingIntentActivity(this.mContext, 0, new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 0));
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void bindLocked() {
        AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
        if (userState != null) {
            long identity = Binder.clearCallingIdentity();
            int flags = 34603009;
            try {
                if (userState.getBindInstantServiceAllowed()) {
                    flags = 34603009 | DumpState.DUMP_CHANGES;
                }
                if (this.mService == null && this.mContext.bindServiceAsUser(this.mIntent, this, flags, new UserHandle(userState.mUserId))) {
                    userState.getBindingServicesLocked().add(this.mComponentName);
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void unbindLocked() {
        this.mContext.unbindService(this);
        AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
        if (userState != null) {
            userState.removeServiceLocked(this);
            this.mSystemSupport.getMagnificationController().resetAllIfNeeded(this.mId);
            resetLocked();
        }
    }

    public boolean canRetrieveInteractiveWindowsLocked() {
        return this.mSecurityPolicy.canRetrieveWindowContentLocked(this) && this.mRetrieveInteractiveWindows;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void disableSelf() {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            java.lang.ref.WeakReference<com.android.server.accessibility.AccessibilityManagerService$UserState> r1 = r8.mUserStateWeakReference     // Catch:{ all -> 0x003e }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x003e }
            com.android.server.accessibility.AccessibilityManagerService$UserState r1 = (com.android.server.accessibility.AccessibilityManagerService.UserState) r1     // Catch:{ all -> 0x003e }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x000f:
            java.util.Set r2 = r1.getEnabledServicesLocked()     // Catch:{ all -> 0x003e }
            android.content.ComponentName r3 = r8.mComponentName     // Catch:{ all -> 0x003e }
            boolean r2 = r2.remove(r3)     // Catch:{ all -> 0x003e }
            if (r2 == 0) goto L_0x003c
            long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x003e }
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r4 = r8.mSystemSupport     // Catch:{ all -> 0x0037 }
            java.lang.String r5 = "enabled_accessibility_services"
            java.util.Set r6 = r1.getEnabledServicesLocked()     // Catch:{ all -> 0x0037 }
            int r7 = r1.mUserId     // Catch:{ all -> 0x0037 }
            r4.persistComponentNamesToSettingLocked(r5, r6, r7)     // Catch:{ all -> 0x0037 }
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x003e }
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r4 = r8.mSystemSupport     // Catch:{ all -> 0x003e }
            r5 = 0
            r4.onClientChangeLocked(r5)     // Catch:{ all -> 0x003e }
            goto L_0x003c
        L_0x0037:
            r4 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x003e }
            throw r4     // Catch:{ all -> 0x003e }
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x003e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityServiceConnection.disableSelf():void");
    }

    public void onServiceConnected(ComponentName componentName, IBinder service) {
        synchronized (this.mLock) {
            if (this.mService != service) {
                if (this.mService != null) {
                    this.mService.unlinkToDeath(this, 0);
                }
                this.mService = service;
                try {
                    this.mService.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Slog.e(LOG_TAG, "Failed registering death link");
                    try {
                        binderDied();
                    } catch (NoSuchElementException e2) {
                        Slog.e(LOG_TAG, "Death link does not exist, service is " + service, e2);
                    }
                    return;
                }
            }
            this.mServiceInterface = IAccessibilityServiceClient.Stub.asInterface(service);
            AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
            if (userState != null) {
                userState.addServiceLocked(this);
                this.mSystemSupport.onClientChangeLocked(false);
                this.mMainHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AccessibilityServiceConnection$ASP9bmSvpeD7ZE_uJ8sm9hCwiU.INSTANCE, this));
            }
        }
    }

    public AccessibilityServiceInfo getServiceInfo() {
        this.mAccessibilityServiceInfo.crashed = this.mWasConnectedAndDied;
        return this.mAccessibilityServiceInfo;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
        if (r0 != null) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0043, code lost:
        binderDied();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0046, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r0.init(r7, r7.mId, r7.mOverlayWindowToken);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0050, code lost:
        android.util.Slog.w(LOG_TAG, "Error while setting connection for service: " + r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        binderDied();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006b, code lost:
        android.util.Slog.e(LOG_TAG, "Death link does not exist, service is " + r0, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initializeService() {
        /*
            r7 = this;
            r0 = 0
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            java.lang.ref.WeakReference<com.android.server.accessibility.AccessibilityManagerService$UserState> r2 = r7.mUserStateWeakReference     // Catch:{ all -> 0x0082 }
            java.lang.Object r2 = r2.get()     // Catch:{ all -> 0x0082 }
            com.android.server.accessibility.AccessibilityManagerService$UserState r2 = (com.android.server.accessibility.AccessibilityManagerService.UserState) r2     // Catch:{ all -> 0x0082 }
            if (r2 != 0) goto L_0x0010
            monitor-exit(r1)     // Catch:{ all -> 0x0082 }
            return
        L_0x0010:
            java.util.Set r3 = r2.getBindingServicesLocked()     // Catch:{ all -> 0x0082 }
            android.content.ComponentName r4 = r7.mComponentName     // Catch:{ all -> 0x0082 }
            boolean r4 = r3.contains(r4)     // Catch:{ all -> 0x0082 }
            r5 = 0
            if (r4 != 0) goto L_0x0021
            boolean r4 = r7.mWasConnectedAndDied     // Catch:{ all -> 0x0082 }
            if (r4 == 0) goto L_0x002b
        L_0x0021:
            android.content.ComponentName r4 = r7.mComponentName     // Catch:{ all -> 0x0082 }
            r3.remove(r4)     // Catch:{ all -> 0x0082 }
            r7.mWasConnectedAndDied = r5     // Catch:{ all -> 0x0082 }
            android.accessibilityservice.IAccessibilityServiceClient r4 = r7.mServiceInterface     // Catch:{ all -> 0x0082 }
            r0 = r4
        L_0x002b:
            if (r0 == 0) goto L_0x0040
            java.util.Set r4 = r2.getEnabledServicesLocked()     // Catch:{ all -> 0x0082 }
            android.content.ComponentName r6 = r7.mComponentName     // Catch:{ all -> 0x0082 }
            boolean r4 = r4.contains(r6)     // Catch:{ all -> 0x0082 }
            if (r4 != 0) goto L_0x0040
            com.android.server.accessibility.AbstractAccessibilityServiceConnection$SystemSupport r4 = r7.mSystemSupport     // Catch:{ all -> 0x0082 }
            r4.onClientChangeLocked(r5)     // Catch:{ all -> 0x0082 }
            monitor-exit(r1)     // Catch:{ all -> 0x0082 }
            return
        L_0x0040:
            monitor-exit(r1)     // Catch:{ all -> 0x0082 }
            if (r0 != 0) goto L_0x0047
            r7.binderDied()
            return
        L_0x0047:
            int r1 = r7.mId     // Catch:{ RemoteException -> 0x004f }
            android.os.IBinder r2 = r7.mOverlayWindowToken     // Catch:{ RemoteException -> 0x004f }
            r0.init(r7, r1, r2)     // Catch:{ RemoteException -> 0x004f }
            goto L_0x0081
        L_0x004f:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error while setting connection for service: "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AccessibilityServiceConnection"
            android.util.Slog.w(r3, r2, r1)
            r7.binderDied()     // Catch:{ NoSuchElementException -> 0x006a }
            goto L_0x0081
        L_0x006a:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Death link does not exist, service is "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AccessibilityServiceConnection"
            android.util.Slog.e(r4, r3, r2)
        L_0x0081:
            return
        L_0x0082:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0082 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityServiceConnection.initializeService():void");
    }

    public void onServiceDisconnected(ComponentName componentName) {
        binderDied();
    }

    /* access modifiers changed from: protected */
    public boolean isCalledForCurrentUserLocked() {
        return this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(-2) == this.mSystemSupport.getCurrentUserIdLocked();
    }

    public boolean setSoftKeyboardShowMode(int showMode) {
        synchronized (this.mLock) {
            if (!isCalledForCurrentUserLocked()) {
                return false;
            }
            AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
            if (userState == null) {
                return false;
            }
            boolean softKeyboardModeLocked = userState.setSoftKeyboardModeLocked(showMode, this.mComponentName);
            return softKeyboardModeLocked;
        }
    }

    public int getSoftKeyboardShowMode() {
        AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
        if (userState != null) {
            return userState.getSoftKeyboardShowMode();
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isAccessibilityButtonAvailable() {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            boolean r1 = r4.isCalledForCurrentUserLocked()     // Catch:{ all -> 0x001f }
            r2 = 0
            if (r1 != 0) goto L_0x000c
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return r2
        L_0x000c:
            java.lang.ref.WeakReference<com.android.server.accessibility.AccessibilityManagerService$UserState> r1 = r4.mUserStateWeakReference     // Catch:{ all -> 0x001f }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x001f }
            com.android.server.accessibility.AccessibilityManagerService$UserState r1 = (com.android.server.accessibility.AccessibilityManagerService.UserState) r1     // Catch:{ all -> 0x001f }
            if (r1 == 0) goto L_0x001d
            boolean r3 = r4.isAccessibilityButtonAvailableLocked(r1)     // Catch:{ all -> 0x001f }
            if (r3 == 0) goto L_0x001d
            r2 = 1
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return r2
        L_0x001f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityServiceConnection.isAccessibilityButtonAvailable():boolean");
    }

    public void binderDied() {
        synchronized (this.mLock) {
            if (isConnectedLocked()) {
                this.mWasConnectedAndDied = true;
                AccessibilityManagerService.UserState userState = (AccessibilityManagerService.UserState) this.mUserStateWeakReference.get();
                if (userState != null) {
                    userState.serviceDisconnectedLocked(this);
                }
                resetLocked();
                this.mSystemSupport.getMagnificationController().resetAllIfNeeded(this.mId);
                this.mSystemSupport.onClientChangeLocked(false);
            }
        }
    }

    public boolean isAccessibilityButtonAvailableLocked(AccessibilityManagerService.UserState userState) {
        if (!this.mRequestAccessibilityButton || !this.mSystemSupport.isAccessibilityButtonShown()) {
            return false;
        }
        if (userState.mIsNavBarMagnificationEnabled && userState.mIsNavBarMagnificationAssignedToAccessibilityButton) {
            return false;
        }
        int requestingServices = 0;
        for (int i = userState.mBoundServices.size() - 1; i >= 0; i--) {
            if (userState.mBoundServices.get(i).mRequestAccessibilityButton) {
                requestingServices++;
            }
        }
        if (requestingServices == 1 || userState.mServiceAssignedToAccessibilityButton == null) {
            return true;
        }
        return this.mComponentName.equals(userState.mServiceAssignedToAccessibilityButton);
    }

    public boolean isCapturingFingerprintGestures() {
        return this.mServiceInterface != null && this.mSecurityPolicy.canCaptureFingerprintGestures(this) && this.mCaptureFingerprintGestures;
    }

    public void onFingerprintGestureDetectionActiveChanged(boolean active) {
        IAccessibilityServiceClient serviceInterface;
        if (isCapturingFingerprintGestures()) {
            synchronized (this.mLock) {
                serviceInterface = this.mServiceInterface;
            }
            if (serviceInterface != null) {
                try {
                    this.mServiceInterface.onFingerprintCapturingGesturesChanged(active);
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void onFingerprintGesture(int gesture) {
        IAccessibilityServiceClient serviceInterface;
        if (isCapturingFingerprintGestures()) {
            synchronized (this.mLock) {
                serviceInterface = this.mServiceInterface;
            }
            if (serviceInterface != null) {
                try {
                    this.mServiceInterface.onFingerprintGesture(gesture);
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void sendGesture(int sequence, ParceledListSlice gestureSteps) {
        synchronized (this.mLock) {
            if (this.mSecurityPolicy.canPerformGestures(this)) {
                MotionEventInjector motionEventInjector = this.mSystemSupport.getMotionEventInjectorLocked();
                if (motionEventInjector != null) {
                    motionEventInjector.injectEvents(gestureSteps.getList(), this.mServiceInterface, sequence);
                } else {
                    try {
                        this.mServiceInterface.onPerformGestureResult(sequence, false);
                    } catch (RemoteException re) {
                        Slog.e(LOG_TAG, "Error sending motion event injection failure to " + this.mServiceInterface, re);
                    }
                }
            }
        }
    }
}
