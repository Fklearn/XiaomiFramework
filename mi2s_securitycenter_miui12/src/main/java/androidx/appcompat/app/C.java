package androidx.appcompat.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.PermissionChecker;
import com.xiaomi.stat.MiStat;
import java.util.Calendar;

class C {

    /* renamed from: a  reason: collision with root package name */
    private static C f273a;

    /* renamed from: b  reason: collision with root package name */
    private final Context f274b;

    /* renamed from: c  reason: collision with root package name */
    private final LocationManager f275c;

    /* renamed from: d  reason: collision with root package name */
    private final a f276d = new a();

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        boolean f277a;

        /* renamed from: b  reason: collision with root package name */
        long f278b;

        /* renamed from: c  reason: collision with root package name */
        long f279c;

        /* renamed from: d  reason: collision with root package name */
        long f280d;
        long e;
        long f;

        a() {
        }
    }

    @VisibleForTesting
    C(@NonNull Context context, @NonNull LocationManager locationManager) {
        this.f274b = context;
        this.f275c = locationManager;
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    private Location a(String str) {
        try {
            if (this.f275c.isProviderEnabled(str)) {
                return this.f275c.getLastKnownLocation(str);
            }
            return null;
        } catch (Exception e) {
            Log.d("TwilightManager", "Failed to get last known location", e);
            return null;
        }
    }

    static C a(@NonNull Context context) {
        if (f273a == null) {
            Context applicationContext = context.getApplicationContext();
            f273a = new C(applicationContext, (LocationManager) applicationContext.getSystemService(MiStat.Param.LOCATION));
        }
        return f273a;
    }

    private void a(@NonNull Location location) {
        long j;
        a aVar = this.f276d;
        long currentTimeMillis = System.currentTimeMillis();
        B a2 = B.a();
        B b2 = a2;
        b2.a(currentTimeMillis - 86400000, location.getLatitude(), location.getLongitude());
        long j2 = a2.f270b;
        b2.a(currentTimeMillis, location.getLatitude(), location.getLongitude());
        boolean z = true;
        if (a2.f272d != 1) {
            z = false;
        }
        long j3 = a2.f271c;
        long j4 = j2;
        long j5 = a2.f270b;
        long j6 = j3;
        boolean z2 = z;
        a2.a(86400000 + currentTimeMillis, location.getLatitude(), location.getLongitude());
        long j7 = a2.f271c;
        if (j6 == -1 || j5 == -1) {
            j = 43200000 + currentTimeMillis;
        } else {
            j = (currentTimeMillis > j5 ? 0 + j7 : currentTimeMillis > j6 ? 0 + j5 : 0 + j6) + 60000;
        }
        aVar.f277a = z2;
        aVar.f278b = j4;
        aVar.f279c = j6;
        aVar.f280d = j5;
        aVar.e = j7;
        aVar.f = j;
    }

    @SuppressLint({"MissingPermission"})
    private Location b() {
        Location location = null;
        Location a2 = PermissionChecker.a(this.f274b, "android.permission.ACCESS_COARSE_LOCATION") == 0 ? a("network") : null;
        if (PermissionChecker.a(this.f274b, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            location = a("gps");
        }
        return (location == null || a2 == null) ? location != null ? location : a2 : location.getTime() > a2.getTime() ? location : a2;
    }

    private boolean c() {
        return this.f276d.f > System.currentTimeMillis();
    }

    /* access modifiers changed from: package-private */
    public boolean a() {
        a aVar = this.f276d;
        if (c()) {
            return aVar.f277a;
        }
        Location b2 = b();
        if (b2 != null) {
            a(b2);
            return aVar.f277a;
        }
        Log.i("TwilightManager", "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
        int i = Calendar.getInstance().get(11);
        return i < 6 || i >= 22;
    }
}
