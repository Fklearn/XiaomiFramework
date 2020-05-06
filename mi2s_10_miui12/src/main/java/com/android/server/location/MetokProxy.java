package com.android.server.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import com.android.internal.location.ILocationProvider;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.server.LocationManagerService;
import com.android.server.location.BaseAbstractLocationProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class MetokProxy implements BaseAbstractLocationProvider.LocationProviderManager {
    private static final String CMD_CAN_TAKEOVER = "metok_takeover";
    /* access modifiers changed from: private */
    public static final boolean D = LocationManagerService.D;
    private static final String EXTRA_CLIENT = "metok_client";
    private static final String EXTRA_MAGIC = "metok_magic";
    private static final String EXTRA_PROVIDER = "metok_provider";
    private static final String MAGIC_METOK = "metok";
    private static final String METOK_LP_PACKAGE = "com.xiaomi.metok";
    private static final String METOK_PROVIDER = "network";
    private static final String TAG = "MetokProxy";
    private final Context mContext;
    /* access modifiers changed from: private */
    public boolean mEnabled = false;
    private BaseAbstractLocationProvider.LocationProviderManager mFused;
    private BaseAbstractLocationProvider.LocationProviderManager mGps;
    /* access modifiers changed from: private */
    public Object mLock = new Object();
    private final String mName;
    private Runnable mNewServiceWork = new Runnable() {
        public void run() {
            boolean enabled;
            ProviderRequest request;
            WorkSource source;
            ILocationProvider service;
            if (MetokProxy.D) {
                Log.d(MetokProxy.TAG, "applying state to connected service");
            }
            synchronized (MetokProxy.this.mLock) {
                enabled = MetokProxy.this.mEnabled;
                request = MetokProxy.this.mRequest;
                source = MetokProxy.this.mWorksource;
                service = MetokProxy.this.getService();
            }
            if (service != null) {
                if (0 == 0) {
                    try {
                        Log.e(MetokProxy.TAG, MetokProxy.this.mServiceWatcher.getPackageName() + " has invalid locatino provider properties");
                    } catch (RemoteException e) {
                        Log.w(MetokProxy.TAG, e);
                    } catch (Exception e2) {
                        Log.e(MetokProxy.TAG, "Exception from " + MetokProxy.this.mServiceWatcher.getPackageName(), e2);
                    }
                }
                if (enabled && request != null) {
                    service.setRequest(request, source);
                }
                synchronized (MetokProxy.this.mLock) {
                    ProviderProperties unused = MetokProxy.this.mProperties = null;
                }
            }
        }
    };
    private BaseAbstractLocationProvider.LocationProviderManager mNlp;
    /* access modifiers changed from: private */
    public ProviderProperties mProperties;
    private String mProviderToCalled;
    /* access modifiers changed from: private */
    public ProviderRequest mRequest = null;
    /* access modifiers changed from: private */
    public final MetokWatcher mServiceWatcher;
    /* access modifiers changed from: private */
    public WorkSource mWorksource = new WorkSource();

    public static MetokProxy createAndBind(Context context, String action, Handler handler) {
        MetokProxy proxy = new MetokProxy(context, action, handler);
        if (proxy.bind()) {
            return proxy;
        }
        Log.w(TAG, "failed to bind metok");
        return null;
    }

    private MetokProxy(Context context, String action, Handler handler) {
        this.mContext = context;
        this.mName = METOK_PROVIDER;
        this.mServiceWatcher = new MetokWatcher(this.mContext, action, this.mNewServiceWork, handler);
    }

    public void setNetworkProvider(BaseAbstractLocationProvider.LocationProviderManager nlp) {
        this.mNlp = nlp;
    }

    public void setGpsProvider(BaseAbstractLocationProvider.LocationProviderManager gps) {
        this.mGps = gps;
    }

    public void setFusedProvider(BaseAbstractLocationProvider.LocationProviderManager fused) {
        this.mFused = fused;
    }

    public String getConnectedPackageName() {
        return this.mServiceWatcher.getPackageName();
    }

    public boolean canTakeOver(String provider, String clientPackage) {
        if (provider == null || clientPackage == null || provider.equals("passive")) {
            return false;
        }
        Bundle extras = new Bundle();
        try {
            extras.putByteArray(EXTRA_MAGIC, MAGIC_METOK.getBytes("utf-8"));
            extras.putByteArray(EXTRA_CLIENT, clientPackage.getBytes("utf-8"));
            extras.putByteArray(EXTRA_PROVIDER, provider.getBytes("utf-8"));
            return false;
        } catch (Exception ex) {
            Log.w(TAG, "create bundle error: " + ex);
            return false;
        }
    }

    public Location locationSanitized(Location location) {
        if (location == null) {
            return null;
        }
        Bundle extras = location.getExtras();
        if (extras != null && extras.containsKey(EXTRA_MAGIC) && D) {
            Log.d(TAG, "location from metok");
        }
        return location;
    }

    public void updateStatus(boolean enable) {
        if ((!enable || this.mEnabled) && !enable && this.mEnabled) {
            disable();
        }
    }

    private boolean bind() {
        return this.mServiceWatcher.start();
    }

    /* access modifiers changed from: private */
    public ILocationProvider getService() {
        return ILocationProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
    }

    public String getName() {
        return this.mName;
    }

    public ProviderProperties getProperties() {
        ProviderProperties providerProperties;
        synchronized (this.mLock) {
            providerProperties = this.mProperties;
        }
        return providerProperties;
    }

    public void setEnabled() {
        synchronized (this.mLock) {
            this.mEnabled = true;
        }
        if (getService() != null) {
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            this.mEnabled = false;
        }
        if (getService() != null) {
        }
    }

    public boolean isEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEnabled;
        }
        return z;
    }

    private void setRequestToOtherProvider(ProviderRequest request, WorkSource source) {
        String str = this.mProviderToCalled;
        if (str != null) {
            try {
                if (str.equals(METOK_PROVIDER) && this.mNlp != null) {
                    ((AbstractLocationProvider) this.mNlp).setRequest(request, source);
                    this.mProviderToCalled = null;
                } else if (!this.mProviderToCalled.equals("gps") || this.mGps == null) {
                    if (this.mFused != null) {
                        ((AbstractLocationProvider) this.mFused).setRequest(request, source);
                    }
                    this.mProviderToCalled = null;
                } else {
                    ((AbstractLocationProvider) this.mGps).setRequest(request, source);
                    this.mProviderToCalled = null;
                }
            } catch (Exception ex) {
                Log.e(TAG, this.mProviderToCalled + "locaiton request failed: " + ex);
            }
        }
    }

    public void setRequest(ProviderRequest request, WorkSource source) {
        synchronized (this.mLock) {
            this.mRequest = request;
            this.mWorksource = source;
        }
        ILocationProvider service = getService();
        if (service == null) {
            setRequestToOtherProvider(request, source);
            return;
        }
        boolean exception = false;
        try {
            service.setRequest(request, source);
        } catch (RemoteException e) {
            Log.w(TAG, e);
            exception = true;
        } catch (Exception e2) {
            Log.e(TAG, "Exception from " + this.mServiceWatcher.getPackageName(), e2);
            exception = true;
        }
        if (exception) {
            setRequestToOtherProvider(request, source);
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.append("REMOTE SERVICE");
        pw.append(" name=").append(this.mName);
        pw.append(" pkg=").append(this.mServiceWatcher.getPackageName());
        PrintWriter append = pw.append(" version=");
        append.append("" + this.mServiceWatcher.getVersion());
        pw.append(10);
        ILocationProvider service = getService();
        if (service == null) {
            pw.println("service down (null)");
            return;
        }
        pw.flush();
        try {
            service.asBinder().dump(fd, args);
        } catch (RemoteException e) {
            pw.println("service down (RemoteException)");
            Log.w(TAG, e);
        } catch (Exception e2) {
            pw.println("service down (Exception)");
            Log.e(TAG, "Exception from " + this.mServiceWatcher.getPackageName(), e2);
        }
    }

    public int getStatus(Bundle extras) {
        ILocationProvider service = getService();
        if (service == null) {
            return 1;
        }
        try {
            return service.getStatus(extras);
        } catch (RemoteException e) {
            Log.w(TAG, e);
            return 1;
        } catch (Exception e2) {
            Log.e(TAG, "Exception from " + this.mServiceWatcher.getPackageName(), e2);
            return 1;
        }
    }

    public long getStatusUpdateTime() {
        ILocationProvider service = getService();
        if (service == null) {
            return 0;
        }
        try {
            return service.getStatusUpdateTime();
        } catch (RemoteException e) {
            Log.w(TAG, e);
            return 0;
        } catch (Exception e2) {
            Log.e(TAG, "Exception from " + this.mServiceWatcher.getPackageName(), e2);
            return 0;
        }
    }

    public void sendExtraCommand(String command, Bundle extras) {
        ILocationProvider service = getService();
        if (service != null) {
            try {
                service.sendExtraCommand(command, extras);
            } catch (RemoteException e) {
                Log.w(TAG, e);
            } catch (Exception e2) {
                Log.e(TAG, "Exception from " + this.mServiceWatcher.getPackageName(), e2);
            }
        }
    }
}
