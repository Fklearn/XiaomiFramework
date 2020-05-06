package com.android.server;

import android.app.PendingIntent;
import android.content.Context;
import android.location.IGnssStatusListener;
import android.location.ILocationListener;
import android.location.ILocationPolicyListener;
import android.location.ILocationPolicyManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.location.ProviderRequest;
import com.android.server.LocationManagerService;
import com.android.server.location.BaseAbstractLocationProvider;
import com.android.server.location.GnssStatusListenerHelper;
import com.android.server.location.MetokProxy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocationManagerServiceInjector {
    /* access modifiers changed from: private */
    public static String TAG = "LocationManagerInjector";
    private static ProviderRequest mLastProviderRequest;
    private static MetokProxy mMetok;
    private static final HashMap<Integer, ArrayList<WeakReference<IGnssStatusListener>>> sBackupGpsListeners = new HashMap<>();
    private static final HashMap<Object, LocationManagerService.Receiver> sBackupReceivers = new HashMap<>();
    private static Context sContext;
    private static GnssStatusListenerHelper sGnssStatusProvider;
    private static final HashMap<Integer, ArrayList<WeakReference<IGnssStatusListener>>> sGpsStatusListeners = new HashMap<>();
    private static LocationManagerService sLocationManagerService;
    /* access modifiers changed from: private */
    public static Object sLock;
    private static MiuiFakeGpsProvider sMiuiFakeGpsProvider;
    private static ILocationPolicyListener sPolicyListener = new ILocationPolicyListener.Stub() {
        public void onUidRulesChanged(int uid, int uidRules) {
            synchronized (LocationManagerServiceInjector.sLock) {
                if (LocationManagerServiceInjector.sUidRules.get(uid, 0) != uidRules) {
                    LocationManagerServiceInjector.sUidRules.put(uid, uidRules);
                    LocationManagerServiceInjector.checkCurrentLocationRequest(uid, uidRules);
                }
            }
        }

        public void onRestrictBackgroundChanged(boolean restrictBackground) {
            String access$300 = LocationManagerServiceInjector.TAG;
            Slog.d(access$300, "onRestrictBackgroundChanged(restrictBackground=" + restrictBackground + ")");
            synchronized (LocationManagerServiceInjector.sLock) {
                boolean unused = LocationManagerServiceInjector.sRestrictBackgroundAll = restrictBackground;
            }
        }
    };
    private static ILocationPolicyManager sPolicyManager = null;
    private static List<BaseAbstractLocationProvider.LocationProviderManager> sRealProviders;
    private static HashMap<Object, LocationManagerService.Receiver> sReceivers;
    /* access modifiers changed from: private */
    public static boolean sRestrictBackgroundAll = false;
    /* access modifiers changed from: private */
    public static final SparseIntArray sUidRules = new SparseIntArray();

    public static void init(LocationManagerService lms, Object lmsLock, Context context, ArrayList<?> realProviders, HashMap<Object, LocationManagerService.Receiver> allReceivers) {
        sLock = lmsLock;
        sContext = context;
        sRealProviders = List.class.cast(realProviders);
        sReceivers = allReceivers;
        sLocationManagerService = lms;
    }

    public static void updateGpsStatusProvider(GnssStatusListenerHelper helper) {
        sGnssStatusProvider = helper;
    }

    public static void bindLocationPolicyService(ILocationPolicyManager policyManager) {
        sPolicyManager = policyManager;
        sMiuiFakeGpsProvider = new MiuiFakeGpsProvider(sContext, sLocationManagerService, sPolicyManager);
        try {
            sPolicyManager.registerListener(sPolicyListener);
        } catch (RemoteException e) {
        }
    }

    private static boolean isRequestBlockedByPolicy(int uid) {
        synchronized (sLock) {
            if (sUidRules.get(uid, 0) != 0) {
                return true;
            }
            return false;
        }
    }

    public static boolean checkIfRequestBlockedByPolicy(int uid, String packageName, LocationRequest request) {
        if (!isRequestBlockedByPolicy(uid)) {
            return false;
        }
        String str = TAG;
        Slog.i(str, packageName + "(" + uid + ") " + request.toString() + " is blocked by policy");
        return true;
    }

    public static boolean checkIfRequestBlockedByPolicy(int uid, String packageName, IGnssStatusListener listener) {
        if (isRequestBlockedByPolicy(uid)) {
            String str = TAG;
            Slog.i(str, packageName + " addGpsStatusListener(" + listener.toString() + ") is blocked by policy");
            return true;
        }
        synchronized (sLock) {
            ArrayList<WeakReference<IGnssStatusListener>> listeners = sGpsStatusListeners.get(Integer.valueOf(uid));
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            Iterator<WeakReference<IGnssStatusListener>> it = listeners.iterator();
            while (it.hasNext()) {
                WeakReference<IGnssStatusListener> l = it.next();
                if (l.get() != null) {
                    if (((IGnssStatusListener) l.get()).asBinder().equals(listener.asBinder())) {
                        return false;
                    }
                }
            }
            listeners.add(new WeakReference(listener));
            sGpsStatusListeners.put(Integer.valueOf(uid), listeners);
            return false;
        }
    }

    public static boolean checkWhenRemoveLocationRequestLocked(ILocationListener listener, PendingIntent intent) {
        IBinder key;
        LocationManagerService.Receiver receiver;
        if (intent == null && listener == null) {
            throw new IllegalArgumentException("need either listener or intent");
        } else if (intent == null || listener == null) {
            if (intent != null) {
                receiver = sBackupReceivers.get(intent);
                key = intent;
            } else {
                IBinder binder = listener.asBinder();
                key = binder;
                receiver = sBackupReceivers.get(binder);
            }
            if (receiver == null) {
                return false;
            }
            sBackupReceivers.remove(key);
            return true;
        } else {
            throw new IllegalArgumentException("cannot register both listener and intent");
        }
    }

    public static boolean checkWhenRemoveListenerLocked(int uid, IGnssStatusListener listener) {
        ArrayList<WeakReference<IGnssStatusListener>> listeners = sGpsStatusListeners.get(Integer.valueOf(uid));
        if (!(listeners == null || listeners.size() == 0)) {
            ArrayList<WeakReference<IGnssStatusListener>> tr = new ArrayList<>();
            Iterator<WeakReference<IGnssStatusListener>> it = listeners.iterator();
            while (it.hasNext()) {
                WeakReference<IGnssStatusListener> l = it.next();
                if (l.get() == null) {
                    tr.add(l);
                } else if (((IGnssStatusListener) l.get()).asBinder().equals(listener.asBinder())) {
                    tr.add(l);
                }
            }
            Iterator<WeakReference<IGnssStatusListener>> it2 = tr.iterator();
            while (it2.hasNext()) {
                listeners.remove(it2.next());
            }
            if (listeners.size() == 0) {
                sGpsStatusListeners.remove(Integer.valueOf(uid));
            }
        }
        if (sBackupGpsListeners.get(Integer.valueOf(uid)) == null) {
            return false;
        }
        sBackupGpsListeners.remove(Integer.valueOf(uid));
        return true;
    }

    private static void removeAndBackupLocationRequestIfNeeded(int uid) {
        synchronized (sLock) {
            ArrayList<Object> backupList = new ArrayList<>();
            for (Map.Entry<Object, LocationManagerService.Receiver> entry : sReceivers.entrySet()) {
                if (entry.getValue().mCallerIdentity.mUid == uid) {
                    backupList.add(entry.getKey());
                }
            }
            if (backupList.size() > 0) {
                Iterator<Object> it = backupList.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    String str = TAG;
                    Slog.d(str, "backup receiver:" + sReceivers.get(o).toString());
                    sBackupReceivers.put(o, sReceivers.get(o));
                    sLocationManagerService.removeUpdatesLocked(sReceivers.get(o));
                }
            }
        }
    }

    private static void restoreBlockedLocationRequestIfNeeded(int uid) {
        synchronized (sLock) {
            ArrayList<Object> restoreList = new ArrayList<>();
            for (Map.Entry<Object, LocationManagerService.Receiver> entry : sBackupReceivers.entrySet()) {
                if (entry.getValue().mCallerIdentity.mUid == uid) {
                    restoreList.add(entry.getKey());
                }
            }
            if (restoreList.size() > 0) {
                Iterator<Object> it = restoreList.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    LocationManagerService.Receiver r = sBackupReceivers.get(o);
                    try {
                        if (r.mListener != null) {
                            r.getListener().asBinder().linkToDeath(r, 0);
                        }
                        if (r.mListener != null) {
                            String str = TAG;
                            Slog.d(str, "restore receiver:" + r);
                            sReceivers.put(o, r);
                            for (String p : r.mUpdateRecords.keySet()) {
                                sLocationManagerService.requestLocationUpdatesLocked(r.mUpdateRecords.get(p).mRealRequest, r, r.mCallerIdentity.mUid, r.mCallerIdentity.mPackageName);
                            }
                        }
                        sBackupReceivers.remove(o);
                    } catch (DeadObjectException e) {
                        sBackupReceivers.remove(o);
                    } catch (RemoteException e2) {
                        Slog.e(TAG, "linkToDeath failed:", e2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static void checkCurrentLocationRequest(int uid, int uidRules) {
        if (uidRules != 0) {
            synchronized (sLock) {
                ArrayList<WeakReference<IGnssStatusListener>> listeners = sGpsStatusListeners.get(Integer.valueOf(uid));
                if (listeners != null) {
                    Slog.d(TAG, "remove gps listener from GpsStatusProvider");
                    Iterator<WeakReference<IGnssStatusListener>> it = listeners.iterator();
                    while (it.hasNext()) {
                        if (it.next().get() == null) {
                        }
                    }
                    Slog.d(TAG, "backup gps listener in mBackupGpsListeners");
                    if (listeners.size() > 0) {
                        sBackupGpsListeners.put(Integer.valueOf(uid), listeners);
                    }
                }
                removeAndBackupLocationRequestIfNeeded(uid);
            }
            return;
        }
        synchronized (sLock) {
            ArrayList<WeakReference<IGnssStatusListener>> listeners2 = sBackupGpsListeners.get(Integer.valueOf(uid));
            if (listeners2 != null) {
                Slog.d(TAG, "add gps listener to GpsStatusProvider");
                Iterator<WeakReference<IGnssStatusListener>> it2 = listeners2.iterator();
                while (it2.hasNext()) {
                    if (it2.next().get() == null) {
                    }
                }
                Slog.d(TAG, "remove gps listener in mBackupGpsListeners");
                sBackupGpsListeners.remove(Integer.valueOf(uid));
            }
            restoreBlockedLocationRequestIfNeeded(uid);
        }
    }

    static ArrayList<Integer> getCurrentLocationRequestUids() {
        ArrayList<Integer> currentLrUids = new ArrayList<>();
        synchronized (sLock) {
            for (Integer k : sGpsStatusListeners.keySet()) {
                currentLrUids.add(k);
            }
            for (Map.Entry<Object, LocationManagerService.Receiver> entry : sReceivers.entrySet()) {
                LocationManagerService.Receiver r = entry.getValue();
                if (!currentLrUids.contains(Integer.valueOf(r.mCallerIdentity.mUid))) {
                    currentLrUids.add(Integer.valueOf(r.mCallerIdentity.mUid));
                }
            }
        }
        return currentLrUids;
    }

    public static void createAndBindLP(String action, Handler handler) {
        try {
            mMetok = MetokProxy.createAndBind(sContext, action, handler);
            if (mMetok != null) {
                Slog.d(TAG, "bind to metok LP");
                BaseAbstractLocationProvider.LocationProviderManager gpsProvider = getRealProvider("gps");
                if (gpsProvider != null) {
                    mMetok.setGpsProvider(gpsProvider);
                }
                BaseAbstractLocationProvider.LocationProviderManager networkProvider = getRealProvider("network");
                if (networkProvider != null) {
                    mMetok.setNetworkProvider(networkProvider);
                }
                BaseAbstractLocationProvider.LocationProviderManager fusedProvider = getRealProvider("fused");
                if (fusedProvider != null) {
                    mMetok.setFusedProvider(fusedProvider);
                }
            }
        } catch (Exception e) {
            Slog.d(TAG, "failed to bind to metok LP");
            mMetok = null;
        }
    }

    private static BaseAbstractLocationProvider.LocationProviderManager getRealProvider(String name) {
        for (int i = 0; i < sRealProviders.size(); i++) {
            BaseAbstractLocationProvider.LocationProviderManager provider = sRealProviders.get(i);
            if (provider != null && name.equals(provider.getName())) {
                return provider;
            }
        }
        return null;
    }

    public static ArrayList<LocationManagerService.UpdateRecord> takeOverLP(String provider, ArrayList<LocationManagerService.UpdateRecord> records) {
        if (provider != null && provider.equals("gps")) {
            return takeOverGpsLP(records);
        }
        if (mMetok == null || provider == null) {
            return records;
        }
        ArrayList<LocationManagerService.UpdateRecord> retRecords = null;
        WorkSource worksource = new WorkSource();
        ProviderRequest providerRequest = new ProviderRequest();
        if (records != null) {
            retRecords = new ArrayList<>();
            Iterator<LocationManagerService.UpdateRecord> it = records.iterator();
            while (it.hasNext()) {
                LocationManagerService.UpdateRecord record = it.next();
                if (!sLocationManagerService.callIsCurrentProfile(UserHandle.getUserId(record.mReceiver.mCallerIdentity.mUid)) || !LocationManagerServiceFacade.checkLocationAccess(sLocationManagerService, record.mReceiver.mCallerIdentity.mPid, record.mReceiver.mCallerIdentity.mUid, record.mReceiver.mCallerIdentity.mPackageName, record.mReceiver.mAllowedResolutionLevel) || !mMetok.canTakeOver(provider, record.mReceiver.mCallerIdentity.mPackageName)) {
                    retRecords.add(record);
                } else {
                    String str = TAG;
                    Slog.d(str, "takeover LP of " + provider + " from " + record.mReceiver.mCallerIdentity.mPackageName);
                    LocationRequest locationRequest = record.mRequest;
                    providerRequest.locationRequests.add(locationRequest);
                    worksource.add(record.mReceiver.mCallerIdentity.mUid, record.mReceiver.mCallerIdentity.mPackageName);
                    if (locationRequest.getInterval() < providerRequest.interval) {
                        providerRequest.reportLocation = true;
                        providerRequest.interval = locationRequest.getInterval();
                    }
                }
            }
        }
        if (!(mLastProviderRequest != null && providerRequest.reportLocation == mLastProviderRequest.reportLocation && providerRequest.interval == mLastProviderRequest.interval)) {
            mMetok.setRequest(providerRequest, worksource);
            mLastProviderRequest = providerRequest;
            String str2 = TAG;
            Slog.d(str2, "takeover LP : " + provider + ": " + providerRequest.toString());
        }
        return retRecords;
    }

    public static void updateLpStatus(ArrayList<BaseAbstractLocationProvider.LocationProviderManager> arrayList) {
        MetokProxy metokProxy = mMetok;
    }

    public static Location locationSanitized(Location location) {
        MetokProxy metokProxy = mMetok;
        if (metokProxy != null) {
            return metokProxy.locationSanitized(location);
        }
        return location;
    }

    public static void dumpLp(FileDescriptor fd, PrintWriter pw, String[] args) {
        MetokProxy metokProxy = mMetok;
        if (metokProxy != null) {
            metokProxy.dump(fd, pw, args);
        }
    }

    static boolean isFakeGpsOn() {
        LocationPolicyManagerService locationPolicyManagerService = sPolicyManager;
        if (locationPolicyManagerService != null) {
            return locationPolicyManagerService.getFakeGpsFeatureOnState();
        }
        return false;
    }

    static boolean isPhoneStationary() {
        LocationPolicyManagerService locationPolicyManagerService = sPolicyManager;
        if (locationPolicyManagerService != null) {
            return locationPolicyManagerService.getPhoneStationary();
        }
        return false;
    }

    static ArrayList<LocationManagerService.UpdateRecord> takeOverGpsLP(ArrayList<LocationManagerService.UpdateRecord> records) {
        if (!isFakeGpsOn() || records == null || !isPhoneStationary()) {
            return records;
        }
        ProviderRequest providerRequest = new ProviderRequest();
        Iterator<LocationManagerService.UpdateRecord> it = records.iterator();
        while (it.hasNext()) {
            LocationManagerService.UpdateRecord record = it.next();
            if (sLocationManagerService.callIsCurrentProfile(UserHandle.getUserId(record.mReceiver.mCallerIdentity.mUid)) && LocationManagerServiceFacade.checkLocationAccess(sLocationManagerService, record.mReceiver.mCallerIdentity.mPid, record.mReceiver.mCallerIdentity.mUid, record.mReceiver.mCallerIdentity.mPackageName, record.mReceiver.mAllowedResolutionLevel)) {
                LocationRequest locationRequest = record.mRequest;
                providerRequest.locationRequests.add(locationRequest);
                if (locationRequest.getInterval() < providerRequest.interval) {
                    providerRequest.reportLocation = true;
                    providerRequest.interval = locationRequest.getInterval();
                }
            }
        }
        sMiuiFakeGpsProvider.setRequest(providerRequest, (WorkSource) null);
        return null;
    }
}
