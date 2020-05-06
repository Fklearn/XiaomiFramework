package com.miui.powercenter.batteryhistory;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.ArrayList;

public class BatteryHistoryProvider extends ContentProvider {
    @Nullable
    public Bundle call(@NonNull String str, @Nullable String str2, @Nullable Bundle bundle) {
        Bundle bundle2;
        ArrayList n;
        String str3;
        if ("getBatteryHistory".equals(str)) {
            bundle2 = new Bundle();
            bundle2.putLong("key_batteryhistory_resettime", C0513q.a(getContext()).k());
            bundle2.putLong("key_batteryhistory_firsttime", C0513q.a(getContext()).f());
            bundle2.putLong("key_batteryhistory_lasttime", C0513q.a(getContext()).g());
            bundle2.setClassLoader(BatteryData.class.getClassLoader());
            bundle2.putParcelableArrayList("key_batteryhistory_firsthistory", new ArrayList(C0513q.a(getContext()).i()));
            n = new ArrayList(C0513q.a(getContext()).j());
            str3 = "key_batteryhistory_lasthistory";
        } else if ("getBatteryHistogram".equals(str)) {
            bundle2 = new Bundle();
            bundle2.setClassLoader(BatteryHistogramItem.class.getClassLoader());
            n = C0513q.a(getContext()).b();
            str3 = "key_batteryhistory_histogram";
        } else if ("getBatteryShutDown".equals(str)) {
            bundle2 = new Bundle();
            bundle2.setClassLoader(BatteryShutdownItem.class.getClassLoader());
            n = C0513q.a(getContext()).n();
            str3 = "key_batteryhistory_shutdown";
        } else {
            if ("checkReset".equals(str)) {
                C0516u.a(getContext()).b();
            } else if ("checkInvalid".equals(str)) {
                boolean z = false;
                if (bundle.containsKey("key_batteryhistory_forceinvalid")) {
                    z = bundle.getBoolean("key_batteryhistory_forceinvalid");
                }
                C0516u.a(getContext()).a(z);
            }
            return super.call(str, str2, bundle);
        }
        bundle2.putParcelableArrayList(str3, n);
        return bundle2;
    }

    public int delete(@NonNull Uri uri, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] strArr, @Nullable String str, @Nullable String[] strArr2, @Nullable String str2) {
        return null;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }
}
