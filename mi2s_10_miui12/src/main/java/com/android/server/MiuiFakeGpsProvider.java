package com.android.server;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.location.ILocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.Log;
import android.util.Slog;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.server.LocationPolicyManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.location.BaseAbstractLocationProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class MiuiFakeGpsProvider implements BaseAbstractLocationProvider.LocationProviderManager {
    private static final String ACTION_FAKE_GPS_STOP = "action.fakegps.stop";
    private static final int CANCEL_FAKE_REQUEST = 1;
    public static final boolean D = Log.isLoggable(TAG, 3);
    private static final int MIN_INTERVAL = 1000;
    private static final String PACKAGE_NAME = "com.miui.powerkeeper";
    private static final int SET_REQUEST = 0;
    private static final String TAG = "MiuiGpsProvider";
    /* access modifiers changed from: private */
    public Context mContext;
    private LocationPolicyManagerService.FakeGpsStationaryListener mFakeGpsStatusListener = new LocationPolicyManagerService.FakeGpsStationaryListener() {
        public void onStationaryChanged(boolean stationary) {
            Slog.i(MiuiFakeGpsProvider.TAG, "status changed stationary: " + stationary);
            MiuiFakeGpsProvider.this.changeProviderIfNecessary(stationary);
            if (!stationary) {
                MiuiFakeGpsProvider.this.stopNavigating();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mFixIntervalMs = Integer.MAX_VALUE;
    private ProviderHandler mHandler = null;
    private final ILocationManager mILocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    /* access modifiers changed from: private */
    public Object mLock = new Object();
    private NLPProxy mNLPProxy;
    private ProviderRequest mProviderRequest = null;
    /* access modifiers changed from: private */
    public boolean mRptLocThreadRunning = false;
    private boolean mSingleShot;
    private boolean mStarted;
    /* access modifiers changed from: private */
    public ReportLocationThread mThread;
    private LocationPolicyManagerService sLocationPolicy;

    public MiuiFakeGpsProvider(Context context, ILocationManager locationManager, LocationPolicyManagerService locationPolicy) {
        this.mContext = context;
        this.mHandler = new ProviderHandler();
        this.mILocationManager = locationManager;
        this.sLocationPolicy = locationPolicy;
        this.mNLPProxy = new NLPProxy();
        registerFakeGpsStatus();
        Slog.i(TAG, "create miui gps provider");
    }

    public String getName() {
        return TAG;
    }

    public boolean isEnabled() {
        return false;
    }

    public void setRequest(ProviderRequest request, WorkSource source) {
        Slog.i(TAG, "setRequest: " + request);
        this.mHandler.obtainMessage(0, request).sendToTarget();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    }

    public ProviderProperties getProperties() {
        return null;
    }

    public int getStatus(Bundle extras) {
        return 0;
    }

    public long getStatusUpdateTime() {
        return 0;
    }

    public boolean sendExtraCommand(String command, Bundle extras) {
        return false;
    }

    private final class ProviderHandler extends Handler {
        private ProviderHandler() {
        }

        public void handleMessage(Message msg) {
            int message = msg.what;
            if (message == 0) {
                MiuiFakeGpsProvider.this.handleSetRequest((ProviderRequest) msg.obj);
            } else if (message == 1) {
                MiuiFakeGpsProvider.this.cancelNlpLocationRequest();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleSetRequest(ProviderRequest request) {
        boolean singleShot;
        this.mProviderRequest = request;
        ProviderRequest providerRequest = this.mProviderRequest;
        if (providerRequest == null) {
            stopNavigating();
            return;
        }
        if (providerRequest.locationRequests == null || this.mProviderRequest.locationRequests.size() <= 0) {
            singleShot = false;
        } else {
            singleShot = true;
            for (LocationRequest lr : this.mProviderRequest.locationRequests) {
                if (lr.getNumUpdates() != 1) {
                    singleShot = false;
                }
            }
        }
        this.mSingleShot = singleShot;
        if (D) {
            Slog.d(TAG, "setRequest: " + this.mProviderRequest);
        }
        if (this.mProviderRequest.reportLocation) {
            this.mFixIntervalMs = (int) this.mProviderRequest.interval;
            if (((long) this.mFixIntervalMs) != this.mProviderRequest.interval) {
                Slog.w(TAG, "interval overflow: " + this.mProviderRequest.interval);
                this.mFixIntervalMs = Integer.MAX_VALUE;
            }
            if (this.mFixIntervalMs < 1000) {
                this.mFixIntervalMs = 1000;
            }
            if (this.mStarted) {
                Slog.i(TAG, "already started");
                scheduleReportLocation(singleShot);
            } else {
                startNavigating(singleShot);
            }
            this.mNLPProxy.cancelRequest();
            LocationPolicyManagerService locationPolicyManagerService = this.sLocationPolicy;
            if (locationPolicyManagerService != null && locationPolicyManagerService.getPhoneStationary()) {
                this.mNLPProxy.requestLocation(this.mFixIntervalMs);
                return;
            }
            return;
        }
        stopNavigating();
        notifyFakeGpsProviderStop();
    }

    private void notifyFakeGpsProviderStop() {
        Intent fakegpsStopIntent = new Intent(ACTION_FAKE_GPS_STOP);
        fakegpsStopIntent.setPackage(PACKAGE_NAME);
        this.mContext.sendBroadcastAsUser(fakegpsStopIntent, UserHandle.OWNER);
    }

    private void startNavigating(boolean singleShot) {
        if (!this.mStarted) {
            Slog.i(TAG, "startNavigating, singleShot is " + singleShot);
            this.mStarted = true;
            this.mSingleShot = singleShot;
            scheduleReportLocation(this.mSingleShot);
        }
    }

    /* access modifiers changed from: private */
    public void stopNavigating() {
        Slog.i(TAG, "stopNavigating, mStarted: " + this.mStarted);
        this.mNLPProxy.cancelRequest();
        if (this.mStarted) {
            this.mStarted = false;
            this.mSingleShot = false;
            ReportLocationThread reportLocationThread = this.mThread;
            if (reportLocationThread != null) {
                reportLocationThread.letStop();
                this.mThread = null;
            }
        }
    }

    private void scheduleReportLocation(boolean singleShot) {
        if (this.mThread == null) {
            synchronized (this.mLock) {
                this.mRptLocThreadRunning = false;
                this.mThread = new ReportLocationThread(TAG);
                this.mThread.start();
                while (!this.mRptLocThreadRunning) {
                    try {
                        this.mLock.wait();
                    } catch (InterruptedException e) {
                        Log.d(TAG, "InterruptedException: " + e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        this.mThread.setOnceRun(singleShot);
        this.mThread.reschedule();
    }

    private void registerFakeGpsStatus() {
        if (this.sLocationPolicy != null && this.mFakeGpsStatusListener != null) {
            Slog.i(TAG, "register provider status");
            this.sLocationPolicy.registerFakeGpsStatus(this.mFakeGpsStatusListener);
        }
    }

    /* access modifiers changed from: private */
    public void reportLocation(Location location) {
        if (this.mSingleShot) {
            stopNavigating();
        }
    }

    /* access modifiers changed from: private */
    public void changeProviderIfNecessary(boolean isFakeGpsON) {
        if (isProviderNeedChange(isFakeGpsON)) {
            LocationManager manager = (LocationManager) this.mContext.getSystemService("location");
            LocationRequest request = LocationRequest.createFromDeprecatedProvider("gps", 60000, 100.0f, true);
            request.setHideFromAppOps(true);
            long token = Binder.clearCallingIdentity();
            try {
                manager.requestLocationUpdates(request, this.mLocationListener, this.mHandler.getLooper());
            } catch (Exception e) {
                Log.d(TAG, "error in changeProviderIfNecessary: " + e);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
            Binder.restoreCallingIdentity(token);
            Log.d(TAG, "changeProvider send request:" + request);
            ProviderHandler providerHandler = this.mHandler;
            providerHandler.sendMessage(providerHandler.obtainMessage(1));
        }
    }

    private boolean isProviderNeedChange(boolean isFakeGpsOn) {
        List<AppOpsManager.PackageOps> appOps = ((AppOpsManager) this.mContext.getSystemService("appops")).getPackagesForOps(isFakeGpsOn ? new int[]{42} : new int[]{2, 42});
        if (appOps == null) {
            return false;
        }
        for (AppOpsManager.PackageOps pkgops : appOps) {
            List<AppOpsManager.OpEntry> entries = pkgops.getOps();
            if (entries != null) {
                for (AppOpsManager.OpEntry entry : entries) {
                    if (entry.isRunning()) {
                        if (D) {
                            Slog.d(TAG, "isProviderNeedChange: true, isMiuiGpsOn: " + isFakeGpsOn);
                        }
                        return true;
                    }
                }
                continue;
            }
        }
        if (D) {
            Slog.d(TAG, "isProviderNeedChange: false, isMiuiGpsOn: " + isFakeGpsOn);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void cancelNlpLocationRequest() {
        ((LocationManager) this.mContext.getSystemService("location")).removeUpdates(this.mLocationListener);
        if (D) {
            Slog.d(TAG, "cancel miui gps request");
        }
    }

    private class ReportLocationThread extends Thread {
        private Location mLocation = null;
        private boolean mOnceRun = false;
        private boolean mPause = true;
        private boolean mStop = false;

        public ReportLocationThread(String name) {
            super(name);
        }

        public void setLocationLocked(Location location) {
            boolean needInterrupt = false;
            if (this.mLocation == null) {
                needInterrupt = true;
            }
            this.mLocation = location;
            if (!this.mPause || !this.mStop || needInterrupt) {
                interrupt();
            }
        }

        public void reschedule() {
            if (MiuiFakeGpsProvider.D) {
                Slog.d(MiuiFakeGpsProvider.TAG, "location thread reschedule");
            }
            this.mPause = false;
            interrupt();
        }

        public void letStop() {
            this.mStop = true;
            interrupt();
        }

        public void setOnceRun(boolean onceRun) {
            this.mOnceRun = onceRun;
        }

        public void run() {
            Location location;
            synchronized (MiuiFakeGpsProvider.this.mLock) {
                boolean unused = MiuiFakeGpsProvider.this.mRptLocThreadRunning = true;
                MiuiFakeGpsProvider.this.mLock.notify();
            }
            while (!this.mStop) {
                while (!this.mStop && !this.mPause && (location = this.mLocation) != null) {
                    MiuiFakeGpsProvider.this.reportLocation(location);
                    if (!this.mOnceRun) {
                        try {
                            Thread.sleep((long) MiuiFakeGpsProvider.this.mFixIntervalMs);
                        } catch (Exception e) {
                        }
                    } else {
                        return;
                    }
                }
                if (!this.mStop) {
                    try {
                        Log.d(MiuiFakeGpsProvider.TAG, "report location pause");
                        Thread.sleep(JobStatus.NO_LATEST_RUNTIME);
                    } catch (Exception e2) {
                    }
                } else {
                    return;
                }
            }
        }
    }

    private class NLPProxy implements LocationListener {
        private int mProxyInterval = 0;
        private boolean mProxyRequested = false;

        public NLPProxy() {
        }

        public void onLocationChanged(Location location) {
            if (MiuiFakeGpsProvider.D) {
                Slog.d(MiuiFakeGpsProvider.TAG, "get location from nlp: " + location);
            }
            if (MiuiFakeGpsProvider.this.mThread != null) {
                synchronized (MiuiFakeGpsProvider.this.mLock) {
                    MiuiFakeGpsProvider.this.mThread.setLocationLocked(location);
                }
            }
            LocationManager manager = (LocationManager) MiuiFakeGpsProvider.this.mContext.getSystemService("location");
            if (MiuiFakeGpsProvider.D) {
                Slog.d(MiuiFakeGpsProvider.TAG, "get loc and remove updates");
            }
            manager.removeUpdates(this);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void requestLocation(int interval) {
            if (!this.mProxyRequested || this.mProxyInterval != interval) {
                LocationManager manager = (LocationManager) MiuiFakeGpsProvider.this.mContext.getSystemService("location");
                if (manager.isProviderEnabled("network")) {
                    if (this.mProxyInterval != interval) {
                        manager.removeUpdates(this);
                    }
                    if (MiuiFakeGpsProvider.D) {
                        Slog.d(MiuiFakeGpsProvider.TAG, "requestLocation");
                    }
                    manager.requestLocationUpdates("network", (long) interval, 10.0f, this);
                    this.mProxyInterval = interval;
                    this.mProxyRequested = true;
                }
            }
        }

        public void cancelRequest() {
            if (MiuiFakeGpsProvider.D) {
                Slog.d(MiuiFakeGpsProvider.TAG, "cancelRequest");
            }
            ((LocationManager) MiuiFakeGpsProvider.this.mContext.getSystemService("location")).removeUpdates(this);
            this.mProxyRequested = false;
            this.mProxyInterval = 0;
        }
    }
}
