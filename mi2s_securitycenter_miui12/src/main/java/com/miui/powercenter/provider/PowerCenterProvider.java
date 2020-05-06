package com.miui.powercenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.i;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PowerCenterProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final UriMatcher f7162a = new UriMatcher(-1);

    static {
        f7162a.addURI("com.miui.powercenter.provider", "remainChargeTime", 1);
        f7162a.addURI("com.miui.powercenter.provider", "lowBatteryEnabled", 2);
        f7162a.addURI("com.miui.powercenter.provider", "lockscreenCleanMemory", 3);
    }

    private String b() {
        i.d();
        StringBuilder sb = new StringBuilder();
        for (BatteryData next : i.a()) {
            if (!(next.getPackageName() == null || next.name == null || Constants.System.ANDROID_PACKAGE_NAME.equals(next.getPackageName()))) {
                sb.append(next.getPackageName() + ";");
            }
        }
        return sb.toString();
    }

    private long c() {
        Log.i("PowerCenterProvider", "getRemainChargeTime");
        if (!o.k(getContext())) {
            return 0;
        }
        return C0501e.a(getContext(), C0514s.c().b()).f6879a / 1000;
    }

    public String a() {
        i.d();
        List<BatteryData> a2 = i.a();
        List<BatteryData> b2 = i.b();
        double c2 = i.c();
        JSONObject jSONObject = new JSONObject();
        if (c2 > 0.0d) {
            try {
                jSONObject.put("data_source", "com.miui.securitycenter");
                jSONObject.put("total_consume", c2);
                JSONArray jSONArray = new JSONArray();
                for (BatteryData next : a2) {
                    if (!(next.getPackageName() == null || next.name == null)) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put(next.getPackageName(), next.getValue());
                        jSONArray.put(jSONObject2);
                    }
                }
                jSONObject.put("app_consume_list", jSONArray);
                JSONArray jSONArray2 = new JSONArray();
                for (BatteryData next2 : b2) {
                    if (next2.drainType >= 0) {
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put(String.valueOf(next2.drainType), next2.getValue());
                        jSONArray2.put(jSONObject3);
                    }
                }
                jSONObject.put("hardware_consume_list", jSONArray2);
            } catch (JSONException unused) {
                Log.e("PowerCenterProvider", "getAppAndHardwarePowerConsume parse error");
            }
        }
        return jSONObject.toString();
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        String str3;
        int i;
        Bundle bundle2;
        Bundle bundle3;
        StringBuilder sb;
        if ("getBatteryInfo".equals(str)) {
            Log.i("PowerCenterProvider", "call METHOD_GET_BATTERY_INFO");
            bundle2 = new Bundle();
            boolean k = o.k(getContext());
            List<aa> b2 = C0514s.c().b();
            bundle2.putLong("left_charge_time", k ? C0501e.a(getContext(), b2).f6879a : 0);
            bundle2.putLong("battery_endurance_time", C0520y.a(getContext(), b2));
            bundle2.putLong("last_charged_time", y.b());
            bundle2.putLong("last_drained_time", y.d());
            i = y.c();
            str3 = "last_drained_percent";
        } else if ("getBatteryCurrent".equals(str)) {
            Log.i("PowerCenterProvider", "call METHOD_GET_BATTERY_CURRENT");
            bundle2 = new Bundle();
            i = o.d(getContext());
            str3 = "current_now";
        } else {
            if ("getPowerSupplyInfo".equals(str)) {
                boolean b3 = o.b();
                bundle3 = new Bundle();
                bundle3.putBoolean("quick_charge", b3);
                sb = new StringBuilder();
                sb.append("call METHOD_GET_POWER_SUPPLY_INFO, quick_charge:");
                sb.append(b3);
            } else if ("getAppPowerConsume".equals(str)) {
                String b4 = b();
                bundle3 = new Bundle();
                bundle3.putString("app_consume", b4);
                return bundle3;
            } else if ("getAppAndHardwarePowerConsume".equals(str)) {
                String a2 = a();
                bundle3 = new Bundle();
                bundle3.putString("app_and_hardware_consume", a2);
                sb = new StringBuilder();
                sb.append("call METHOD_GET_APP_AND_HARDWARE_POWER_CONSUME, power consume details: ");
                sb.append(a2);
            } else if ("getSuperpowerSupportXspace".equals(str)) {
                Bundle bundle4 = new Bundle();
                bundle4.putBoolean("superpower_support_xspace", true);
                Log.i("PowerCenterProvider", "call METHOD_GET_SUPERPOWER_SUPPORT_XSPACE, superpower_support_xspace: true");
                return bundle4;
            } else if (!"getSuperpowerSystemuiStatus".equals(str)) {
                return null;
            } else {
                String e = k.e(getContext());
                String f = k.f(getContext());
                String g = k.g(getContext());
                String a3 = k.a(getContext(), (int) R.drawable.superpower_ic_systemui, getCallingPackage());
                Bundle bundle5 = new Bundle();
                bundle5.putString("superpower_systemui_remaining_time", e);
                bundle5.putString("superpower_systemui_remaining_time_unit", f);
                bundle5.putString("superpower_systemui_title", g);
                bundle5.putString("superpower_systemui_icon", a3);
                Log.i("PowerCenterProvider", "call METHOD_GET_SUPERPOWER_REMAINING_TIME, superpower_remaining_time: " + e + " remainingTimeUnit:" + f + " title" + g + " imageUrl" + a3);
                return bundle5;
            }
            Log.i("PowerCenterProvider", sb.toString());
            return bundle3;
        }
        bundle2.putInt(str3, i);
        return bundle2;
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
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor matrixCursor;
        Object[] objArr;
        int match = f7162a.match(uri);
        if (match == 1) {
            matrixCursor = new MatrixCursor(new String[]{"remainChargeTime"});
            objArr = new Object[]{Long.valueOf(c())};
        } else if (match != 3) {
            return null;
        } else {
            matrixCursor = new MatrixCursor(new String[]{"lockscreenCleanMemory"});
            objArr = new Object[]{Integer.valueOf(y.l())};
        }
        matrixCursor.addRow(objArr);
        return matrixCursor;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int match = f7162a.match(uri);
        if (match == 2) {
            return (contentValues == null || !contentValues.containsKey("lowBatteryEnabled")) ? 0 : 1;
        }
        if (match != 3 || contentValues == null || !contentValues.containsKey("lockscreenCleanMemory")) {
            return 0;
        }
        y.d(contentValues.getAsInteger("lockscreenCleanMemory").intValue());
        return 1;
    }
}
