package com.miui.earthquakewarning.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.xiaomi.stat.MiStat;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private static final int CHECK_DELAY = 10000;
    private static final String TAG = "LocationUtils";

    public interface AreaResultListener {
        void areaFail();

        void areaSuccess(Address address);
    }

    public interface LocationResultListener {
        void locationFail();

        void locationSuccess(Location location);
    }

    public static void getAdminAreaLocation2(Context context, LocationResultListener locationResultListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(MiStat.Param.LOCATION);
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation("network");
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("gps");
            }
            if (lastKnownLocation != null) {
                locationResultListener.locationSuccess(lastKnownLocation);
            } else {
                requestLocationOnce2(context, locationResultListener);
            }
        } catch (Exception unused) {
            Log.e(TAG, "getAdminAreaLocation failed to get location");
        }
    }

    public static void getAdminAreaLocation3(Context context, AreaResultListener areaResultListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(MiStat.Param.LOCATION);
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation("network");
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("gps");
            }
            if (lastKnownLocation != null) {
                getGeoArea(context, lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), areaResultListener);
                return;
            }
            requestLocationOnce3(context, areaResultListener);
        } catch (Exception unused) {
            Log.e(TAG, "getAdminAreaLocation failed to get location");
        }
    }

    public static void getGeoArea(Context context, double d2, double d3, AreaResultListener areaResultListener) {
        final Context context2 = context;
        final double d4 = d2;
        final double d5 = d3;
        final AreaResultListener areaResultListener2 = areaResultListener;
        new Thread() {
            public void run() {
                try {
                    List<Address> fromLocation = new Geocoder(context2, Locale.getDefault()).getFromLocation(d4, d5, 1);
                    if (fromLocation != null) {
                        areaResultListener2.areaSuccess(fromLocation.get(0));
                        return;
                    }
                    areaResultListener2.areaFail();
                } catch (Exception unused) {
                    areaResultListener2.areaFail();
                    Log.e(LocationUtils.TAG, "getAddress exp ");
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public static void quitLooper() {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            myLooper.quit();
        }
    }

    @SuppressLint({"MissingPermission"})
    public static void requestLocationOnce2(final Context context, final LocationResultListener locationResultListener) {
        if (context != null && ((LocationManager) context.getSystemService(MiStat.Param.LOCATION)) != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        final LocationManager locationManager = (LocationManager) context.getSystemService(MiStat.Param.LOCATION);
                        if (Looper.myLooper() == null) {
                            Looper.prepare();
                        }
                        final Handler handler = new Handler();
                        final AnonymousClass1 r9 = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                handler.removeCallbacksAndMessages((Object) null);
                                LocationUtils.quitLooper();
                                locationResultListener.locationSuccess(location);
                                locationManager.removeUpdates(this);
                            }

                            public void onProviderDisabled(String str) {
                            }

                            public void onProviderEnabled(String str) {
                            }

                            public void onStatusChanged(String str, int i, Bundle bundle) {
                            }
                        };
                        locationManager.requestLocationUpdates("network", 0, 0.0f, r9, Looper.myLooper());
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                LocationUtils.quitLooper();
                                Log.i(LocationUtils.TAG, "requestLocationOnce reach time limit");
                                locationManager.removeUpdates(r9);
                            }
                        }, 10000);
                        Looper.loop();
                    } catch (Exception unused) {
                        Log.e(LocationUtils.TAG, "requestLocationOnce error");
                    }
                }
            }).start();
        }
    }

    @SuppressLint({"MissingPermission"})
    public static void requestLocationOnce3(final Context context, final AreaResultListener areaResultListener) {
        if (context != null && ((LocationManager) context.getSystemService(MiStat.Param.LOCATION)) != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        final LocationManager locationManager = (LocationManager) context.getSystemService(MiStat.Param.LOCATION);
                        if (Looper.myLooper() == null) {
                            Looper.prepare();
                        }
                        final Handler handler = new Handler();
                        final AnonymousClass1 r9 = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                handler.removeCallbacksAndMessages((Object) null);
                                LocationUtils.quitLooper();
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                AnonymousClass2 r9 = AnonymousClass2.this;
                                LocationUtils.getGeoArea(context, latitude, longitude, areaResultListener);
                                locationManager.removeUpdates(this);
                            }

                            public void onProviderDisabled(String str) {
                            }

                            public void onProviderEnabled(String str) {
                            }

                            public void onStatusChanged(String str, int i, Bundle bundle) {
                            }
                        };
                        locationManager.requestLocationUpdates("network", 0, 0.0f, r9, Looper.myLooper());
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                LocationUtils.quitLooper();
                                Log.i(LocationUtils.TAG, "requestLocationOnce reach time limit");
                                locationManager.removeUpdates(r9);
                            }
                        }, 10000);
                        Looper.loop();
                    } catch (Exception unused) {
                        Log.e(LocationUtils.TAG, "requestLocationOnce");
                    }
                }
            }).start();
        }
    }
}
