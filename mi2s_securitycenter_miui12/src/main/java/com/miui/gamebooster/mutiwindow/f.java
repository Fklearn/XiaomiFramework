package com.miui.gamebooster.mutiwindow;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.f.a;
import b.b.c.h.j;
import b.b.o.g.e;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.ui.QuickReplySettingsActivity;
import com.miui.securitycenter.n;
import com.miui.securityscan.i.k;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class f {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4633a = (Build.IS_INTERNATIONAL_BUILD ? "https://adv.sec.intl.miui.com/game/fast_reply" : "https://adv.sec.miui.com/game/fast_reply");

    /* renamed from: b  reason: collision with root package name */
    private static final Object f4634b = new Object();

    public static class a extends AsyncTask<Void, Void, List<String>> {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f4635a;

        /* renamed from: b  reason: collision with root package name */
        private String f4636b;

        /* renamed from: c  reason: collision with root package name */
        private int f4637c;

        public a(Context context, String str, int i) {
            this.f4635a = context.getApplicationContext();
            this.f4636b = str;
            this.f4637c = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<String> doInBackground(Void... voidArr) {
            if (f.b(this.f4635a).contains(this.f4636b)) {
                com.miui.gamebooster.provider.a.a(this.f4635a, this.f4636b, this.f4637c);
            }
            return QuickReplySettingsActivity.a(this.f4635a);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<String> list) {
            super.onPostExecute(list);
            if (!list.isEmpty()) {
                a.a(this.f4635a).a((a.C0027a) new e(this, list));
            }
        }
    }

    public static class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private Context f4638a;

        public b(Context context) {
            this.f4638a = context.getApplicationContext();
        }

        public void run() {
            if (!f.c(this.f4638a)) {
                Log.i("FreeformWindowUtils", "quick reply close, no need update apps");
                return;
            }
            try {
                String b2 = k.b(f.f4633a, new j("gamebooster_freeformwindowutils"));
                Log.i("FreeformWindowUtils", "update=" + TextUtils.isEmpty(b2));
                if (!TextUtils.isEmpty(b2)) {
                    JSONArray optJSONArray = new JSONObject(b2).optJSONArray(DataSchemeDataSource.SCHEME_DATA);
                    if (optJSONArray.length() > 0) {
                        f.b(this.f4638a, optJSONArray);
                    }
                }
            } catch (Exception e) {
                Log.e("FreeformWindowUtils", "updateDefaultApps error", e);
            }
        }
    }

    public static void a(Context context) {
        C0384o.b(context.getContentResolver(), "quick_reply", 0, -2);
    }

    public static void a(Context context, String str, int i) {
        if (!c() && c(context) && !TextUtils.isEmpty(str)) {
            new a(context, str, i).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static void a(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "quick_reply_is_first_use", z ? 1 : 0);
    }

    public static List<String> b(Context context) {
        List<String> h = h(context);
        return h.isEmpty() ? g(context) : h;
    }

    public static void b() {
        try {
            e.a(Class.forName("android.util.MiuiMultiWindowUtils"), "exitFreeFormWindowIfNeeded", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            Log.i("GameBoosterUtils", e.toString());
        }
    }

    /* access modifiers changed from: private */
    public static void b(Context context, JSONArray jSONArray) {
        FileOutputStream fileOutputStream;
        synchronized (f4634b) {
            File file = new File(context.getFilesDir(), "quick_reply_default_pkgs");
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                Log.e("FreeformWindowUtils", "create file error", e);
            }
            PrintWriter printWriter = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                try {
                    PrintWriter printWriter2 = new PrintWriter(fileOutputStream);
                    try {
                        printWriter2.write(jSONArray.toString());
                        IOUtils.closeQuietly(printWriter2);
                    } catch (Exception e2) {
                        e = e2;
                        printWriter = printWriter2;
                        try {
                            Log.e("FreeformWindowUtils", "write in file error", e);
                            IOUtils.closeQuietly(printWriter);
                            IOUtils.closeQuietly(fileOutputStream);
                            return;
                        } catch (Throwable th) {
                            th = th;
                            IOUtils.closeQuietly(printWriter);
                            IOUtils.closeQuietly(fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        printWriter = printWriter2;
                        IOUtils.closeQuietly(printWriter);
                        IOUtils.closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.e("FreeformWindowUtils", "write in file error", e);
                    IOUtils.closeQuietly(printWriter);
                    IOUtils.closeQuietly(fileOutputStream);
                    return;
                }
            } catch (Exception e4) {
                e = e4;
                fileOutputStream = null;
                Log.e("FreeformWindowUtils", "write in file error", e);
                IOUtils.closeQuietly(printWriter);
                IOUtils.closeQuietly(fileOutputStream);
                return;
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = null;
                IOUtils.closeQuietly(printWriter);
                IOUtils.closeQuietly(fileOutputStream);
                throw th;
            }
            IOUtils.closeQuietly(fileOutputStream);
        }
        return;
    }

    public static void b(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "quick_reply_enable", z ? 1 : 0);
    }

    public static boolean c() {
        try {
            return ((Boolean) e.a(Class.forName("android.view.Display"), "hasSmallFreeformFeature", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("FreeformWindowUtils", "reflect error when get hasSmallFreeformFeature", e);
            return false;
        }
    }

    public static boolean c(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "quick_reply_enable", 0) == 1;
    }

    public static boolean d() {
        try {
            return ((Boolean) e.a(Class.forName("android.util.MiuiMultiWindowUtils"), Boolean.TYPE, "supportQuickReply", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("FreeformWindowUtils", "supportQuickReply!", e);
            return false;
        }
    }

    public static boolean d(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "quick_reply_is_first_use", 1) == 1;
    }

    public static void e(Context context) {
        C0384o.b(context.getContentResolver(), "quick_reply", 1, -2);
    }

    public static void f(Context context) {
        n.a().b(new b(context));
    }

    private static List<String> g(Context context) {
        ArrayList arrayList;
        synchronized (f4634b) {
            InputStream inputStream = null;
            arrayList = new ArrayList();
            try {
                inputStream = context.getResources().getAssets().open("quickreply_default_list");
                JSONArray jSONArray = new JSONArray(IOUtils.toString(inputStream));
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.get(i).toString());
                }
            } catch (Exception e) {
                try {
                    Log.e("FreeformWindowUtils", "openNativeDefaultFile error", e);
                } catch (Throwable th) {
                    IOUtils.closeQuietly((InputStream) null);
                    throw th;
                }
            }
            IOUtils.closeQuietly(inputStream);
        }
        return arrayList;
    }

    private static List<String> h(Context context) {
        ArrayList arrayList;
        synchronized (f4634b) {
            File file = new File(context.getFilesDir(), "quick_reply_default_pkgs");
            arrayList = new ArrayList();
            if (file.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        JSONArray jSONArray = new JSONArray(IOUtils.toString(fileInputStream2));
                        for (int i = 0; i < jSONArray.length(); i++) {
                            arrayList.add(jSONArray.get(i).toString());
                        }
                        IOUtils.closeQuietly(fileInputStream2);
                    } catch (Exception e) {
                        e = e;
                        fileInputStream = fileInputStream2;
                        try {
                            Log.e("FreeformWindowUtils", "read pkgs from file error", e);
                            IOUtils.closeQuietly(fileInputStream);
                            return arrayList;
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream2 = fileInputStream;
                            IOUtils.closeQuietly(fileInputStream2);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly(fileInputStream2);
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    Log.e("FreeformWindowUtils", "read pkgs from file error", e);
                    IOUtils.closeQuietly(fileInputStream);
                    return arrayList;
                }
            }
        }
        return arrayList;
    }
}
