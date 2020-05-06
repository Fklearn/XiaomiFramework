package com.android.server.devicepolicy;

import android.app.admin.DeviceAdminService;
import android.app.admin.IDeviceAdminService;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.am.PersistentConnection;
import com.android.server.appbinding.AppBindingUtils;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import java.io.PrintWriter;

public class DeviceAdminServiceController {
    static final boolean DEBUG = false;
    static final String TAG = "DevicePolicyManager";
    @GuardedBy({"mLock"})
    private final SparseArray<DevicePolicyServiceConnection> mConnections = new SparseArray<>();
    /* access modifiers changed from: private */
    public final DevicePolicyConstants mConstants;
    final Context mContext;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final DevicePolicyManagerService.Injector mInjector;
    final Object mLock = new Object();
    private final DevicePolicyManagerService mService;

    static void debug(String format, Object... args) {
    }

    private class DevicePolicyServiceConnection extends PersistentConnection<IDeviceAdminService> {
        final /* synthetic */ DeviceAdminServiceController this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public DevicePolicyServiceConnection(com.android.server.devicepolicy.DeviceAdminServiceController r16, int r17, android.content.ComponentName r18) {
            /*
                r15 = this;
                r0 = r16
                r14 = r15
                r14.this$0 = r0
                android.content.Context r2 = r0.mContext
                android.os.Handler r3 = r16.mHandler
                com.android.server.devicepolicy.DevicePolicyConstants r1 = r16.mConstants
                long r6 = r1.DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC
                com.android.server.devicepolicy.DevicePolicyConstants r1 = r16.mConstants
                double r8 = r1.DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE
                com.android.server.devicepolicy.DevicePolicyConstants r1 = r16.mConstants
                long r10 = r1.DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC
                com.android.server.devicepolicy.DevicePolicyConstants r0 = r16.mConstants
                long r12 = r0.DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC
                java.lang.String r1 = "DevicePolicyManager"
                r0 = r15
                r4 = r17
                r5 = r18
                r0.<init>(r1, r2, r3, r4, r5, r6, r8, r10, r12)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DeviceAdminServiceController.DevicePolicyServiceConnection.<init>(com.android.server.devicepolicy.DeviceAdminServiceController, int, android.content.ComponentName):void");
        }

        /* access modifiers changed from: protected */
        public int getBindFlags() {
            return BroadcastQueueInjector.FLAG_IMMUTABLE;
        }

        /* access modifiers changed from: protected */
        public IDeviceAdminService asInterface(IBinder binder) {
            return IDeviceAdminService.Stub.asInterface(binder);
        }
    }

    public DeviceAdminServiceController(DevicePolicyManagerService service, DevicePolicyConstants constants) {
        this.mService = service;
        this.mInjector = service.mInjector;
        this.mContext = this.mInjector.mContext;
        this.mHandler = new Handler(BackgroundThread.get().getLooper());
        this.mConstants = constants;
    }

    private ServiceInfo findService(String packageName, int userId) {
        return AppBindingUtils.findService(packageName, userId, "android.app.action.DEVICE_ADMIN_SERVICE", "android.permission.BIND_DEVICE_ADMIN", DeviceAdminService.class, this.mInjector.getIPackageManager(), new StringBuilder());
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void startServiceForOwner(String packageName, int userId, String actionForLog) {
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (this.mLock) {
                ServiceInfo service = findService(packageName, userId);
                if (service == null) {
                    debug("Owner package %s on u%d has no service.", packageName, Integer.valueOf(userId));
                    disconnectServiceOnUserLocked(userId, actionForLog);
                    this.mInjector.binderRestoreCallingIdentity(token);
                    return;
                }
                if (this.mConnections.get(userId) != null) {
                    debug("Disconnecting from existing service connection.", packageName, Integer.valueOf(userId));
                    disconnectServiceOnUserLocked(userId, actionForLog);
                }
                debug("Owner package %s on u%d has service %s for %s", packageName, Integer.valueOf(userId), service.getComponentName().flattenToShortString(), actionForLog);
                DevicePolicyServiceConnection conn = new DevicePolicyServiceConnection(this, userId, service.getComponentName());
                this.mConnections.put(userId, conn);
                conn.bind();
                this.mInjector.binderRestoreCallingIdentity(token);
            }
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(token);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void stopServiceForOwner(int userId, String actionForLog) {
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (this.mLock) {
                disconnectServiceOnUserLocked(userId, actionForLog);
            }
            this.mInjector.binderRestoreCallingIdentity(token);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(token);
            throw th;
        }
    }

    @GuardedBy({"mLock"})
    private void disconnectServiceOnUserLocked(int userId, String actionForLog) {
        DevicePolicyServiceConnection conn = this.mConnections.get(userId);
        if (conn != null) {
            debug("Stopping service for u%d if already running for %s.", Integer.valueOf(userId), actionForLog);
            conn.unbind();
            this.mConnections.remove(userId);
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        synchronized (this.mLock) {
            if (this.mConnections.size() != 0) {
                pw.println();
                pw.print(prefix);
                pw.println("Owner Services:");
                for (int i = 0; i < this.mConnections.size(); i++) {
                    int userId = this.mConnections.keyAt(i);
                    pw.print(prefix);
                    pw.print("  ");
                    pw.print("User: ");
                    pw.println(userId);
                    this.mConnections.valueAt(i).dump(prefix + "    ", pw);
                }
                pw.println();
            }
        }
    }
}
