package com.miui.appmanager;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: com.miui.appmanager.e  reason: case insensitive filesystem */
public class C0322e {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final Object f3670a = new Object();

    public static Bundle a(Context context, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        synchronized (f3670a) {
            try {
                JSONArray c2 = c(context);
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < c2.length(); i++) {
                    arrayList.add(c2.get(i).toString());
                }
                bundle.putStringArrayList("installer_pkg_info", arrayList);
            } catch (JSONException e) {
                Log.e("AMInstallerUtils", "JSONException when openInstallerFile ", e);
            }
        }
        return bundle;
    }

    public static String a(Context context, String str) {
        try {
            return context.getPackageManager().getInstallerPackageName(str);
        } catch (Exception e) {
            Log.e("AMInstallerUtils", "getInstallerPackageName error", e);
            return null;
        }
    }

    public static void a(Context context, String str, String str2, boolean z) {
        new C0320c(str2, context, str, z).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private static boolean a(String str) {
        return new File(str).exists();
    }

    /* access modifiers changed from: private */
    public static int b(JSONArray jSONArray, String str) {
        if (jSONArray == null || jSONArray.length() <= 0) {
            return -1;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                JSONObject jSONObject = (JSONObject) jSONArray.get(i);
                if (jSONObject != null && str.equals(jSONObject.optString("pkg_name"))) {
                    return i;
                }
            } catch (JSONException e) {
                Log.e("AMInstallerUtils", "JSONException when findInstallerPkgIndex", e);
            }
        }
        return -1;
    }

    private static Bundle b(Context context, Bundle bundle) {
        return context.getContentResolver().call(Uri.parse("content://com.miui.securitycenter.provider"), "openInstallerFile", (String) null, bundle);
    }

    public static JSONObject b(Context context, String str) {
        ArrayList<String> stringArrayList;
        Bundle b2 = b(context, (Bundle) null);
        if (!(b2 == null || (stringArrayList = b2.getStringArrayList("installer_pkg_info")) == null || stringArrayList.isEmpty())) {
            int i = 0;
            while (i < stringArrayList.size()) {
                try {
                    JSONObject jSONObject = new JSONObject(stringArrayList.get(i));
                    if (str.equals(jSONObject.optString("pkg_name"))) {
                        return jSONObject;
                    }
                    i++;
                } catch (JSONException e) {
                    Log.e("AMInstallerUtils", "JSONException getInstallerPkg error", e);
                }
            }
        }
        return null;
    }

    private static void b(Context context) {
        File file = new File(context.getFilesDir() + "/appmanager/" + "appmanager_installer_pkg");
        if (file.exists()) {
            file.delete();
        }
    }

    /* access modifiers changed from: private */
    public static JSONArray c(Context context) {
        JSONArray jSONArray = new JSONArray();
        String str = context.getFilesDir() + "/appmanager/" + "appmanager_installer_pkg";
        if (!a(str)) {
            return jSONArray;
        }
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(str);
            try {
                JSONArray jSONArray2 = new JSONArray(IOUtils.toString(fileInputStream2));
                try {
                    IOUtils.closeQuietly(fileInputStream2);
                } catch (Exception e) {
                    Log.e("AMInstallerUtils", "Exception close inputstream", e);
                }
                return jSONArray2;
            } catch (FileNotFoundException e2) {
                e = e2;
                fileInputStream = fileInputStream2;
                Log.e("AMInstallerUtils", "FileNotFoundException when openInstallerFileLocked ", e);
                IOUtils.closeQuietly(fileInputStream);
                return jSONArray;
            } catch (JSONException e3) {
                e = e3;
                fileInputStream = fileInputStream2;
                b(context);
                Log.e("AMInstallerUtils", "JSONException when openInstallerFileLocked ", e);
                IOUtils.closeQuietly(fileInputStream);
                return jSONArray;
            } catch (IOException e4) {
                e = e4;
                fileInputStream = fileInputStream2;
                try {
                    Log.e("AMInstallerUtils", "IOException when openInstallerFileLocked", e);
                    try {
                        IOUtils.closeQuietly(fileInputStream);
                        return jSONArray;
                    } catch (Exception e5) {
                        Log.e("AMInstallerUtils", "Exception close inputstream", e5);
                        return jSONArray;
                    }
                } catch (Throwable th) {
                    th = th;
                    try {
                        IOUtils.closeQuietly(fileInputStream);
                    } catch (Exception e6) {
                        Log.e("AMInstallerUtils", "Exception close inputstream", e6);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = fileInputStream2;
                IOUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (FileNotFoundException e7) {
            e = e7;
            Log.e("AMInstallerUtils", "FileNotFoundException when openInstallerFileLocked ", e);
            IOUtils.closeQuietly(fileInputStream);
            return jSONArray;
        } catch (JSONException e8) {
            e = e8;
            b(context);
            Log.e("AMInstallerUtils", "JSONException when openInstallerFileLocked ", e);
            IOUtils.closeQuietly(fileInputStream);
            return jSONArray;
        } catch (IOException e9) {
            e = e9;
            Log.e("AMInstallerUtils", "IOException when openInstallerFileLocked", e);
            IOUtils.closeQuietly(fileInputStream);
            return jSONArray;
        }
    }

    public static void c(Context context, String str) {
        new C0321d(context, str).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
