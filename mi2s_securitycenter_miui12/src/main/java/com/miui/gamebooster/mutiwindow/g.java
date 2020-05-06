package com.miui.gamebooster.mutiwindow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0374e;
import com.miui.gamebooster.provider.a;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import miui.util.IOUtils;
import org.json.JSONObject;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static g f4639a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4640b;

    /* renamed from: c  reason: collision with root package name */
    private PackageManager f4641c;

    private g(Context context) {
        this.f4640b = context.getApplicationContext();
        try {
            this.f4641c = this.f4640b.getPackageManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized g a(Context context) {
        g gVar;
        synchronized (g.class) {
            if (f4639a == null) {
                f4639a = new g(context);
            }
            gVar = f4639a;
        }
        return gVar;
    }

    private void a(String str) {
        ArrayList<String> a2 = b.a("gb_added_games", (ArrayList<String>) new ArrayList());
        ArrayList<String> a3 = f.a((ArrayList<String>) new ArrayList());
        if (!a2.contains(str) && !a3.contains(str)) {
            a2.add(str);
            b.b("gb_added_games", a2);
            try {
                LocalBroadcastManager.getInstance(this.f4640b).sendBroadcast(new Intent("gb.action.update_game_list"));
            } catch (Exception unused) {
            }
        }
    }

    private boolean b(String str, int i) {
        Context context;
        String charSequence;
        int i2;
        boolean z;
        String a2 = C0374e.a(this.f4640b, "top_200_games.json");
        if (a2 != null && a2.length() > 0 && a2.contains(str)) {
            try {
                a(str);
                if (((Boolean) e.a(Class.forName("miui.securityspace.XSpaceUserHandle"), Boolean.TYPE, "isUidBelongtoXSpace", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).booleanValue()) {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = false;
                } else {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = true;
                }
                a.a(context, charSequence, str, i, i2, z);
                Log.i("GameBoosterNewApp", str + " isGameFromJson true");
                return true;
            } catch (Exception e) {
                Log.e("GameBoosterReflectUtils", e.toString());
            }
        }
        Log.i("GameBoosterNewApp", str + " isGameFromJson false");
        return false;
    }

    private boolean c(String str, int i) {
        Context context;
        String charSequence;
        int i2;
        boolean z;
        Cursor cursor = null;
        try {
            cursor = a.c(this.f4640b, str);
            if (cursor == null) {
                IOUtils.closeQuietly(cursor);
                return false;
            }
            if (cursor.moveToFirst()) {
                a(str);
                if (((Boolean) e.a(Class.forName("miui.securityspace.XSpaceUserHandle"), Boolean.TYPE, "isUidBelongtoXSpace", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).booleanValue()) {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = false;
                } else {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = true;
                }
                a.a(context, charSequence, str, i, i2, z);
                Log.i("GameBoosterNewApp", str + " isGameFromLocal true");
                IOUtils.closeQuietly(cursor);
                return true;
            }
            IOUtils.closeQuietly(cursor);
            Log.i("GameBoosterNewApp", str + " isGameFromLocal false");
            return false;
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    private boolean d(String str, int i) {
        Context context;
        String charSequence;
        int i2;
        boolean z;
        try {
            String b2 = b.b.c.h.e.b(this.f4640b, com.miui.gamebooster.d.a.f4249d, new JSONObject().put("pkgs", str), DeviceUtil.getImeiMd5(), new j("gamebooster_gameboosternewapphandler"));
            if (!TextUtils.isEmpty(b2) && new JSONObject(b2).optJSONArray("result").optInt(0) == 1) {
                a(str);
                if (((Boolean) e.a(Class.forName("miui.securityspace.XSpaceUserHandle"), Boolean.TYPE, "isUidBelongtoXSpace", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).booleanValue()) {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = false;
                } else {
                    context = this.f4640b;
                    charSequence = x.j(this.f4640b, str).toString();
                    i2 = 0;
                    z = true;
                }
                a.a(context, charSequence, str, i, i2, z);
                Log.i("GameBoosterNewApp", str + " isGameFromNet true");
                return true;
            }
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        }
        return false;
    }

    public boolean a(String str, int i) {
        PackageManager packageManager;
        boolean k = com.miui.gamebooster.c.a.a(this.f4640b).k(true);
        Log.i("GameBoosterNewApp", "addAppToGameBox: isGameBoosterOpen=" + k);
        if (k && (packageManager = this.f4641c) != null) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 8192);
                if (applicationInfo == null || !x.a(applicationInfo)) {
                    return false;
                }
                if (b(str, i) || c(str, i)) {
                    return true;
                }
                if (h.i()) {
                    return d(str, i);
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("GameBoosterNewApp", e.toString());
            }
        }
        return false;
    }
}
