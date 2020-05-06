package com.miui.powercenter.powersaver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.miui.powercenter.d.a;
import com.miui.powercenter.d.b;
import com.miui.powercenter.d.c;
import com.miui.powercenter.d.d;
import com.miui.powercenter.d.e;
import com.miui.powercenter.d.f;
import com.miui.powercenter.d.g;
import com.miui.powercenter.utils.o;
import com.miui.superpower.b.h;
import java.util.ArrayList;
import java.util.List;

public class PowerSaverProvider extends ContentProvider {
    private List<d> a() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new b());
        arrayList.add(new e());
        arrayList.add(new a());
        arrayList.add(new f());
        arrayList.add(new g());
        return arrayList;
    }

    private void a(boolean z) {
        a(z, false);
    }

    private void a(boolean z, boolean z2) {
        Intent intent = new Intent("miui.intent.action.POWER_SAVE_MODE_CHANGED");
        intent.addFlags(1073741824);
        List<d> a2 = a();
        List<d> b2 = b();
        if (!z) {
            c.c(getContext(), false);
            c.d(getContext(), false);
            c.a(getContext(), false);
            c.b(getContext(), false);
            for (d a3 : b2) {
                a3.a(getContext());
            }
            Settings.System.putInt(getContext().getContentResolver(), "POWER_SAVE_MODE_OPEN", 0);
            intent.putExtra("POWER_SAVE_MODE_OPEN", false);
            int e = o.e(getContext());
            Log.i("PowerSaverProvider", "Close power save mode, battery percent " + e);
            b.a(0);
            h.a(e);
            com.miui.superpower.b.f.a(getContext()).a();
            if (!z2) {
                for (d a4 : a2) {
                    a4.a(getContext());
                }
            }
        } else if (!o.l(getContext())) {
            c.c(getContext(), true);
            c.d(getContext(), true);
            c.a(getContext(), true);
            c.b(getContext(), true);
            for (d b3 : a2) {
                b3.b(getContext());
            }
            for (d b4 : b2) {
                b4.b(getContext());
            }
            Settings.System.putInt(getContext().getContentResolver(), "POWER_SAVE_MODE_OPEN", 1);
            intent.putExtra("POWER_SAVE_MODE_OPEN", true);
            int e2 = o.e(getContext());
            b.a(e2);
            Log.i("PowerSaverProvider", "Open power save mode, battery percent " + e2);
            o.p(getContext());
            h.b(e2);
            com.miui.superpower.b.f.a(getContext()).a();
        } else {
            return;
        }
        getContext().sendBroadcast(intent);
    }

    private List<d> b() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new c());
        return arrayList;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if ("changePowerMode".equals(str)) {
            if (o.m(getContext())) {
                return null;
            }
            boolean z = bundle.getBoolean("POWER_SAVE_MODE_OPEN");
            a(z);
            if (z && bundle.getBoolean("LOW_BATTERY_DIALOG")) {
                int e = o.e(getContext());
                if (e > 10 && e < 20) {
                    com.miui.powercenter.a.a.f();
                } else if (e < 10) {
                    com.miui.powercenter.a.a.g();
                }
                com.miui.powercenter.a.a.c(e);
            }
        } else if ("showLowBatteryDialog".equals(str)) {
            int e2 = o.e(getContext());
            if (e2 > 10 && e2 < 20) {
                com.miui.powercenter.a.a.h();
            } else if (e2 >= 10 || e2 <= 5) {
                com.miui.powercenter.a.a.i();
            } else {
                com.miui.powercenter.a.a.j();
            }
        } else if ("changeSuperPowerMode".equals(str)) {
            boolean z2 = bundle.getBoolean("POWER_SUPERSAVE_MODE_OPEN");
            boolean z3 = bundle.getBoolean("POWER_SUPERSAVE_MODE_FROMUSER");
            if (z2 && o.l(getContext())) {
                a(false, true);
            }
            com.miui.superpower.o.a(getContext()).a(z2, z3);
        } else if ("cleanMemory".equals(str)) {
            com.miui.superpower.b.b(bundle.getStringArrayList("CLEAN_MEMORY_EXCEPTION_LIST"));
        }
        return super.call(str, str2, bundle);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
