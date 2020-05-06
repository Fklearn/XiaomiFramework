package com.miui.support.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import b.b.o.g.c;
import java.util.List;
import org.json.JSONObject;

public final class MiuiSettingsCompat$SettingsCloudData {

    /* renamed from: a  reason: collision with root package name */
    private static final Uri f8230a = Uri.parse("content://com.android.settings.cloud.CloudSettings/cloud_all_data");

    /* renamed from: b  reason: collision with root package name */
    private static final Uri f8231b = Uri.parse("content://com.android.settings.cloud.CloudSettings/cloud_all_data/single");

    /* renamed from: c  reason: collision with root package name */
    private static final Uri f8232c = Uri.parse("content://com.android.settings.cloud.CloudSettings/cloud_all_data/notify");

    public static class CloudData implements Parcelable {
        public static final Parcelable.Creator<CloudData> CREATOR = new d();
        private String data;
        private JSONObject json;

        public CloudData(String str) {
            this.data = str;
        }

        private boolean hasKey(String str) {
            if (this.json == null) {
                this.json = new JSONObject(this.data);
            }
            return this.json.has(str);
        }

        public int describeContents() {
            return 0;
        }

        public boolean getBoolean(String str, boolean z) {
            try {
                if (hasKey(str)) {
                    return this.json.getBoolean(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return z;
        }

        public int getInt(String str, int i) {
            try {
                if (hasKey(str)) {
                    return this.json.getInt(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return i;
        }

        public long getLong(String str, long j) {
            try {
                if (hasKey(str)) {
                    return this.json.getLong(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return j;
        }

        public String getString(String str, String str2) {
            try {
                if (hasKey(str)) {
                    return this.json.getString(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str2;
        }

        public String toString() {
            return this.data.toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.data);
        }
    }

    public static int a(ContentResolver contentResolver, String str, String str2, int i) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$SettingsCloudData");
        a2.b("getCloudDataInt", new Class[]{ContentResolver.class, String.class, String.class, Integer.TYPE}, contentResolver, str, str2, Integer.valueOf(i));
        return a2.c();
    }

    public static Uri a() {
        return f8232c;
    }

    public static String a(ContentResolver contentResolver, String str, String str2, String str3) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$SettingsCloudData");
        a2.b("getCloudDataString", new Class[]{ContentResolver.class, String.class, String.class, String.class}, contentResolver, str, str2, str3);
        return a2.f();
    }

    public static List<CloudData> a(ContentResolver contentResolver, String str) {
        c.a a2 = c.a.a("android.provider.MiuiSettings$SettingsCloudData");
        a2.b("getCloudDataList", new Class[]{ContentResolver.class, String.class}, contentResolver, str);
        return (List) a2.d();
    }
}
