package com.miui.common.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import b.b.o.g.e;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import miui.security.SecurityManager;

public class RemoteProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final UriMatcher f3830a = new UriMatcher(-1);

    /* renamed from: b  reason: collision with root package name */
    private a f3831b;

    /* renamed from: c  reason: collision with root package name */
    private SharedPreferences f3832c;

    static {
        f3830a.addURI("com.miui.securitycenter.remoteprovider", "whitelist", 1);
    }

    private Bundle a(String str, Bundle bundle) {
        if (!bundle.containsKey("type")) {
            return null;
        }
        int i = bundle.getInt("type");
        if (i == 0) {
            return d(str, bundle);
        }
        if (i == 1) {
            return b(str, bundle);
        }
        if (i == 2) {
            return f(str, bundle);
        }
        if (i == 3) {
            return c(str, bundle);
        }
        if (i == 4) {
            return e(str, bundle);
        }
        if (i != 5) {
            return null;
        }
        return g(str, bundle);
    }

    private void a(String str, boolean z) {
        if ("security_adb_install_enable".equals(str) && z) {
            int callingPid = Binder.getCallingPid();
            Context context = getContext();
            SecurityManager securityManager = (SecurityManager) context.getSystemService("security");
            try {
                String str2 = (String) e.a((Object) securityManager, "getPackageNameByPid", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(callingPid));
                if (context.getPackageName().equals(str2) || "com.miui.klo.bugreport".equals(str2)) {
                    return;
                }
            } catch (Exception e) {
                Log.e("RemoteProvider", "getPackageNameByPid error", e);
            }
            throw new SecurityException("set security_adb_install_enable permssion denied");
        }
    }

    private boolean a() {
        return !this.f3832c.getBoolean("key_default_preferences_saved", false);
    }

    private Bundle b(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                boolean z = bundle.getBoolean(MiStat.Param.VALUE);
                a(string, z);
                this.f3832c.edit().putBoolean(string, z).apply();
                return null;
            } else if ("GET".equals(str)) {
                boolean z2 = bundle.getBoolean("default");
                bundle.clear();
                bundle.putBoolean(string, this.f3832c.getBoolean(string, z2));
                return bundle;
            }
        }
        return null;
    }

    private void b() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = this.f3832c.edit();
        for (Map.Entry next : defaultSharedPreferences.getAll().entrySet()) {
            Object value = next.getValue();
            String str = (String) next.getKey();
            if (value instanceof Integer) {
                edit.putInt(str, ((Integer) value).intValue());
            } else if (value instanceof String) {
                edit.putString(str, (String) value);
            } else if (value instanceof Long) {
                edit.putLong(str, ((Long) value).longValue());
            } else if (value instanceof Boolean) {
                edit.putBoolean(str, ((Boolean) value).booleanValue());
            } else if (value instanceof Float) {
                edit.putFloat(str, ((Float) value).floatValue());
            }
        }
        edit.putBoolean("key_default_preferences_saved", true);
        edit.apply();
    }

    private Bundle c(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                this.f3832c.edit().putFloat(string, bundle.getFloat(MiStat.Param.VALUE)).apply();
                return null;
            } else if ("GET".equals(str)) {
                float f = bundle.getFloat("default");
                bundle.clear();
                bundle.putFloat(string, this.f3832c.getFloat(string, f));
                return bundle;
            }
        }
        return null;
    }

    private Bundle d(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                this.f3832c.edit().putInt(string, bundle.getInt(MiStat.Param.VALUE)).apply();
                return null;
            } else if ("GET".equals(str)) {
                int i = bundle.getInt("default");
                bundle.clear();
                bundle.putInt(string, this.f3832c.getInt(string, i));
                return bundle;
            }
        }
        return null;
    }

    private Bundle e(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                this.f3832c.edit().putLong(string, bundle.getLong(MiStat.Param.VALUE)).apply();
                return null;
            } else if ("GET".equals(str)) {
                long j = bundle.getLong("default");
                bundle.clear();
                bundle.putLong(string, this.f3832c.getLong(string, j));
                return bundle;
            }
        }
        return null;
    }

    private Bundle f(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                this.f3832c.edit().putString(string, bundle.getString(MiStat.Param.VALUE)).apply();
                return null;
            } else if ("GET".equals(str)) {
                String string2 = bundle.getString("default");
                bundle.clear();
                bundle.putString(string, this.f3832c.getString(string, string2));
                return bundle;
            }
        }
        return null;
    }

    private Bundle g(String str, Bundle bundle) {
        String string = bundle.getString("key");
        if (string != null) {
            if ("SET".equals(str)) {
                this.f3832c.edit().putStringSet(string, new HashSet(bundle.getStringArrayList(MiStat.Param.VALUE))).apply();
                return null;
            } else if ("GET".equals(str)) {
                HashSet hashSet = new HashSet(bundle.getStringArrayList("default"));
                bundle.clear();
                bundle.putStringArrayList(string, new ArrayList(this.f3832c.getStringSet(string, hashSet)));
                return bundle;
            }
        }
        return null;
    }

    public void attachInfo(Context context, ProviderInfo providerInfo) {
        super.attachInfo(context, providerInfo);
        this.f3831b = a.a(context);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if ("callPreference".equals(str)) {
            return a(str2, bundle);
        }
        return null;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        if (f3830a.match(uri) != 1) {
            return -1;
        }
        return this.f3831b.a(str, strArr);
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return ContentUris.withAppendedId(uri, f3830a.match(uri) != 1 ? -1 : this.f3831b.a(contentValues));
    }

    public boolean onCreate() {
        this.f3832c = getContext().getSharedPreferences("remote_provider_preferences", 0);
        if (!a()) {
            return true;
        }
        b();
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (f3830a.match(uri) != 1) {
            return null;
        }
        return this.f3831b.a(strArr, str, strArr2, str2);
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
