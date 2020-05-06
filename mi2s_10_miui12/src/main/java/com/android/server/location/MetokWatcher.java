package com.android.server.location;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import java.util.List;
import java.util.Objects;

public class MetokWatcher implements ServiceConnection {
    private static final boolean D = false;
    public static final String EXTRA_SERVICE_IS_MULTIUSER = "serviceIsMultiuser";
    public static final String EXTRA_SERVICE_VERSION = "serviceVersion";
    public static final String SERVICE_PACKAGE_NAME = "com.xiaomi.metok";
    private static final String TAG = "MetokWatcher";
    private final String mAction;
    @GuardedBy({"mLock"})
    private ComponentName mBoundComponent;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public String mBoundPackageName;
    @GuardedBy({"mLock"})
    private IBinder mBoundService;
    @GuardedBy({"mLock"})
    private int mBoundUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    @GuardedBy({"mLock"})
    private int mBoundVersion = Integer.MIN_VALUE;
    private final Context mContext;
    @GuardedBy({"mLock"})
    private int mCurrentUserId = 0;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final Runnable mNewServiceWork;
    private final PackageMonitor mPackageMonitor = new PackageMonitor() {
        public void onPackageUpdateFinished(String packageName, int uid) {
            synchronized (MetokWatcher.this.mLock) {
                boolean unused = MetokWatcher.this.bindBestPackageLocked((String) null, Objects.equals(packageName, MetokWatcher.this.mBoundPackageName));
            }
        }

        public void onPackageAdded(String packageName, int uid) {
            synchronized (MetokWatcher.this.mLock) {
                boolean unused = MetokWatcher.this.bindBestPackageLocked((String) null, Objects.equals(packageName, MetokWatcher.this.mBoundPackageName));
            }
        }

        public void onPackageRemoved(String packageName, int uid) {
            synchronized (MetokWatcher.this.mLock) {
                boolean unused = MetokWatcher.this.bindBestPackageLocked((String) null, Objects.equals(packageName, MetokWatcher.this.mBoundPackageName));
            }
        }

        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            synchronized (MetokWatcher.this.mLock) {
                boolean unused = MetokWatcher.this.bindBestPackageLocked((String) null, Objects.equals(packageName, MetokWatcher.this.mBoundPackageName));
            }
            return MetokWatcher.super.onPackageChanged(packageName, uid, components);
        }
    };
    private final PackageManager mPm;
    private final String mServicePackageName;

    public MetokWatcher(Context context, String action, Runnable newServiceWork, Handler handler) {
        this.mContext = context;
        this.mAction = action;
        this.mPm = this.mContext.getPackageManager();
        this.mNewServiceWork = newServiceWork;
        this.mHandler = handler;
        Resources resources = context.getResources();
        this.mServicePackageName = SERVICE_PACKAGE_NAME;
    }

    public boolean start() {
        synchronized (this.mLock) {
            if (!bindBestPackageLocked(this.mServicePackageName, false)) {
                Log.w(TAG, "failed to bind metok, when we are booting now");
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                int userId = intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    MetokWatcher.this.switchUser(userId);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    MetokWatcher.this.unlockUser(userId);
                }
            }
        }, UserHandle.ALL, intentFilter, (String) null, this.mHandler);
        if (this.mServicePackageName == null) {
            this.mPackageMonitor.register(this.mContext, (Looper) null, UserHandle.ALL, true);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean bindBestPackageLocked(String justCheckThisPackage, boolean forceRebind) {
        String str = justCheckThisPackage;
        Intent intent = new Intent(this.mAction);
        if (str != null) {
            intent.setPackage(str);
        }
        List<ResolveInfo> rInfos = this.mPm.queryIntentServicesAsUser(intent, 268435584, this.mCurrentUserId);
        int bestVersion = Integer.MIN_VALUE;
        ComponentName bestComponent = null;
        boolean bestIsMultiuser = false;
        if (rInfos != null) {
            for (ResolveInfo rInfo : rInfos) {
                ComponentName component = rInfo.serviceInfo.getComponentName();
                String packageName = component.getPackageName();
                int version = Integer.MIN_VALUE;
                boolean isMultiuser = false;
                if (rInfo.serviceInfo.metaData != null) {
                    version = rInfo.serviceInfo.metaData.getInt("serviceVersion", Integer.MIN_VALUE);
                    isMultiuser = rInfo.serviceInfo.metaData.getBoolean("serviceIsMultiuser");
                }
                if (version > bestVersion) {
                    bestVersion = version;
                    bestComponent = component;
                    bestIsMultiuser = isMultiuser;
                }
            }
        }
        boolean alreadyBound = false;
        if (bestComponent == null) {
            Log.w(TAG, "Odd, no component found for service " + this.mAction);
            unbindLocked();
            return false;
        }
        int userId = bestIsMultiuser ? 0 : this.mCurrentUserId;
        if (Objects.equals(bestComponent, this.mBoundComponent) && bestVersion == this.mBoundVersion && userId == this.mBoundUserId) {
            alreadyBound = true;
        }
        if (forceRebind || !alreadyBound) {
            unbindLocked();
            bindToPackageLocked(bestComponent, bestVersion, userId);
        }
        return true;
    }

    private void unbindLocked() {
        ComponentName component = this.mBoundComponent;
        this.mBoundComponent = null;
        this.mBoundPackageName = null;
        this.mBoundVersion = Integer.MIN_VALUE;
        this.mBoundUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        if (component != null) {
            this.mContext.unbindService(this);
        }
    }

    private void bindToPackageLocked(ComponentName component, int version, int userId) {
        Intent intent = new Intent(this.mAction);
        intent.setComponent(component);
        this.mBoundComponent = component;
        this.mBoundPackageName = component.getPackageName();
        this.mBoundVersion = version;
        this.mBoundUserId = userId;
        this.mContext.bindServiceAsUser(intent, this, 1073741829, new UserHandle(userId));
    }

    public void onServiceConnected(ComponentName component, IBinder binder) {
        synchronized (this.mLock) {
            if (component.equals(this.mBoundComponent)) {
                this.mBoundService = binder;
                if (!(this.mHandler == null || this.mNewServiceWork == null)) {
                    this.mHandler.post(this.mNewServiceWork);
                }
            } else {
                Log.w(TAG, "unexpected onServiceConnected: " + component);
            }
        }
    }

    public void onServiceDisconnected(ComponentName component) {
        synchronized (this.mLock) {
            if (component.equals(this.mBoundComponent)) {
                this.mBoundService = null;
            }
        }
    }

    public String getPackageName() {
        String str;
        synchronized (this.mLock) {
            str = this.mBoundPackageName;
        }
        return str;
    }

    public int getVersion() {
        int i;
        synchronized (this.mLock) {
            i = this.mBoundVersion;
        }
        return i;
    }

    public IBinder getBinder() {
        IBinder iBinder;
        synchronized (this.mLock) {
            iBinder = this.mBoundService;
        }
        return iBinder;
    }

    public void switchUser(int userId) {
        synchronized (this.mLock) {
            this.mCurrentUserId = userId;
            bindBestPackageLocked(this.mServicePackageName, false);
        }
    }

    public void unlockUser(int userId) {
        synchronized (this.mLock) {
            if (userId == this.mCurrentUserId) {
                bindBestPackageLocked(this.mServicePackageName, false);
            }
        }
    }
}
