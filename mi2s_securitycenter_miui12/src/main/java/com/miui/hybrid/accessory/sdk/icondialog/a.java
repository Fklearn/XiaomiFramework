package com.miui.hybrid.accessory.sdk.icondialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.security.NetworkSecurityPolicy;
import android.text.TextUtils;
import com.miui.hybrid.accessory.a.a.c;
import com.miui.hybrid.accessory.a.c.b;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5591a = (c.c() ? "https://api.hybrid.intl.xiaomi.com" : "https://api.hybrid.xiaomi.com");

    /* renamed from: b  reason: collision with root package name */
    private static final String f5592b = (f5591a + "/api/native.pkg/query");
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static boolean f5593c = false;

    private static List<String> a(Context context) {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        Intent d2 = d(context);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(d2, 0);
        if (queryIntentActivities != null) {
            com.miui.hybrid.accessory.a.b.a.a("IconDialogLauncher", queryIntentActivities.size() + " app(s) accept " + d2.toUri(0));
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                String str = resolveInfo.activityInfo.packageName;
                if (packageManager.checkPermission("com.miui.hybrid.accessory.SHOW_ICON_DIALOG", str) == 0) {
                    arrayList.add(str);
                }
            }
        }
        com.miui.hybrid.accessory.a.b.a.a("IconDialogLauncher", arrayList.size() + " app(s) left after check permission. ");
        return arrayList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x00ab A[Catch:{ Exception -> 0x016a }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0156 A[Catch:{ Exception -> 0x016a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<com.miui.hybrid.accessory.sdk.icondialog.IconData> a(android.content.Context r8, java.util.List<java.lang.String> r9) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = "IconDialogLauncher"
            if (r9 == 0) goto L_0x0173
            boolean r2 = r9.isEmpty()
            if (r2 == 0) goto L_0x0011
            goto L_0x0173
        L_0x0011:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
            java.lang.Object r3 = r9.get(r3)
            java.lang.String r3 = (java.lang.String) r3
            r2.append(r3)
            r3 = 1
            r4 = r3
        L_0x0022:
            int r5 = r9.size()
            if (r4 >= r5) goto L_0x0045
            java.lang.Object r5 = r9.get(r4)
            java.lang.String r5 = (java.lang.String) r5
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = ","
            r6.append(r7)
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            r2.append(r5)
            int r4 = r4 + 1
            goto L_0x0022
        L_0x0045:
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            java.lang.String r2 = r2.toString()
            java.lang.String r5 = "native_package_names"
            r4.put(r5, r2)
            android.content.res.Resources r2 = r8.getResources()
            if (r2 == 0) goto L_0x009a
            android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
            if (r2 == 0) goto L_0x0097
            float r5 = r2.density
            java.lang.String r5 = java.lang.String.valueOf(r5)
            java.lang.String r6 = "screen_density"
            r4.put(r6, r5)
            int r5 = r2.widthPixels
            java.lang.String r5 = java.lang.String.valueOf(r5)
            java.lang.String r6 = "screen_width"
            r4.put(r6, r5)
            int r2 = r2.heightPixels
            java.lang.String r2 = java.lang.String.valueOf(r2)
            java.lang.String r5 = "screen_height"
            r4.put(r5, r2)
            java.lang.String r2 = r8.getPackageName()
            java.lang.String r5 = "host_app_pkg"
            r4.put(r5, r2)
            int r2 = com.miui.hybrid.accessory.a.a.a.a(r8)
            java.lang.String r2 = java.lang.String.valueOf(r2)
            java.lang.String r5 = "host_app_version"
            r4.put(r5, r2)
            goto L_0x009f
        L_0x0097:
            java.lang.String r2 = "Get null DisplayMetrics, failed to fill screen info to http para."
            goto L_0x009c
        L_0x009a:
            java.lang.String r2 = "Get null resource, failed to fill screen info to http para."
        L_0x009c:
            com.miui.hybrid.accessory.a.b.a.d(r1, r2)
        L_0x009f:
            java.lang.String r2 = f5592b     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.a.c.a r8 = com.miui.hybrid.accessory.a.c.b.a((android.content.Context) r8, (java.lang.String) r2, (java.util.Map<java.lang.String, java.lang.String>) r4, (boolean) r3)     // Catch:{ Exception -> 0x016a }
            boolean r2 = r8.a()     // Catch:{ Exception -> 0x016a }
            if (r2 == 0) goto L_0x0156
            com.miui.hybrid.accessory.sdk.a.d r2 = new com.miui.hybrid.accessory.sdk.a.d     // Catch:{ Exception -> 0x016a }
            r2.<init>()     // Catch:{ Exception -> 0x016a }
            byte[] r8 = r8.b()     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.a.f.e.a(r2, r8)     // Catch:{ Exception -> 0x016a }
            java.util.Map r8 = r2.c()     // Catch:{ Exception -> 0x016a }
            if (r8 == 0) goto L_0x0150
            java.util.Map r8 = r2.c()     // Catch:{ Exception -> 0x016a }
            java.util.Iterator r9 = r9.iterator()     // Catch:{ Exception -> 0x016a }
        L_0x00c5:
            boolean r2 = r9.hasNext()     // Catch:{ Exception -> 0x016a }
            if (r2 == 0) goto L_0x0132
            java.lang.Object r2 = r9.next()     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x016a }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x016a }
            if (r3 == 0) goto L_0x00d8
            goto L_0x00c5
        L_0x00d8:
            java.lang.Object r3 = r8.get(r2)     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.sdk.a.b r3 = (com.miui.hybrid.accessory.sdk.a.b) r3     // Catch:{ Exception -> 0x016a }
            if (r3 != 0) goto L_0x00f5
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
            r3.<init>()     // Catch:{ Exception -> 0x016a }
            java.lang.String r4 = "can not find query result by "
            r3.append(r4)     // Catch:{ Exception -> 0x016a }
            r3.append(r2)     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.a.b.a.e(r1, r2)     // Catch:{ Exception -> 0x016a }
            goto L_0x00c5
        L_0x00f5:
            com.miui.hybrid.accessory.sdk.a.a r3 = r3.a()     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.sdk.icondialog.IconData r4 = new com.miui.hybrid.accessory.sdk.icondialog.IconData     // Catch:{ Exception -> 0x016a }
            r4.<init>()     // Catch:{ Exception -> 0x016a }
            r4.f5587a = r2     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = r3.b()     // Catch:{ Exception -> 0x016a }
            r4.f5589c = r2     // Catch:{ Exception -> 0x016a }
            long r5 = r3.B()     // Catch:{ Exception -> 0x016a }
            r4.e = r5     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = r3.j()     // Catch:{ Exception -> 0x016a }
            r4.f5588b = r2     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = r3.h()     // Catch:{ Exception -> 0x016a }
            r4.f5590d = r2     // Catch:{ Exception -> 0x016a }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
            r2.<init>()     // Catch:{ Exception -> 0x016a }
            java.lang.String r3 = "appData.hybridPkgName : "
            r2.append(r3)     // Catch:{ Exception -> 0x016a }
            java.lang.String r3 = r4.f5590d     // Catch:{ Exception -> 0x016a }
            r2.append(r3)     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x016a }
            com.miui.hybrid.accessory.a.b.a.a(r1, r2)     // Catch:{ Exception -> 0x016a }
            r0.add(r4)     // Catch:{ Exception -> 0x016a }
            goto L_0x00c5
        L_0x0132:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
            r8.<init>()     // Catch:{ Exception -> 0x016a }
            java.lang.String r9 = "Get "
            r8.append(r9)     // Catch:{ Exception -> 0x016a }
            int r9 = r0.size()     // Catch:{ Exception -> 0x016a }
            r8.append(r9)     // Catch:{ Exception -> 0x016a }
            java.lang.String r9 = " app(s) from server."
            r8.append(r9)     // Catch:{ Exception -> 0x016a }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x016a }
        L_0x014c:
            com.miui.hybrid.accessory.a.b.a.c(r1, r8)     // Catch:{ Exception -> 0x016a }
            goto L_0x0172
        L_0x0150:
            java.lang.String r8 = "query result does not contain any item."
            com.miui.hybrid.accessory.a.b.a.e(r1, r8)     // Catch:{ Exception -> 0x016a }
            goto L_0x0172
        L_0x0156:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
            r9.<init>()     // Catch:{ Exception -> 0x016a }
            java.lang.String r2 = "response.isOk() is false, "
            r9.append(r2)     // Catch:{ Exception -> 0x016a }
            int r8 = r8.f5479a     // Catch:{ Exception -> 0x016a }
            r9.append(r8)     // Catch:{ Exception -> 0x016a }
            java.lang.String r8 = r9.toString()     // Catch:{ Exception -> 0x016a }
            goto L_0x014c
        L_0x016a:
            r8 = move-exception
            java.lang.String r9 = r8.getMessage()
            com.miui.hybrid.accessory.a.b.a.b(r1, r9, r8)
        L_0x0172:
            return r0
        L_0x0173:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r2 = "pkgNameList is null or empty. "
            r8.append(r2)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            com.miui.hybrid.accessory.a.b.a.d(r1, r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.sdk.icondialog.a.a(android.content.Context, java.util.List):java.util.List");
    }

    public static void a(Context context, List<String> list, long j, Map<String, String> map) {
        final Context context2 = context;
        final List<String> list2 = list;
        final long j2 = j;
        final Map<String, String> map2 = map;
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            /* renamed from: a */
            public Void doInBackground(Void... voidArr) {
                a.c(context2, list2, j2, map2);
                return null;
            }

            /* access modifiers changed from: protected */
            /* renamed from: a */
            public void onPostExecute(Void voidR) {
                boolean unused = a.f5593c = false;
            }

            /* access modifiers changed from: protected */
            public void onPreExecute() {
                if (a.f5593c) {
                    com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "IconDataFetcher is pulling data from server, ignore repeat show request.");
                    cancel(false);
                    return;
                }
                boolean unused = a.f5593c = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private static void a(final Context context, List<IconData> list, final String str) {
        if (list != null && !TextUtils.isEmpty(str)) {
            HashSet hashSet = new HashSet();
            Iterator<IconData> it = list.iterator();
            while (it.hasNext()) {
                IconData next = it.next();
                final String str2 = next == null ? null : next.f5588b;
                if (!hashSet.contains(str2)) {
                    hashSet.add(str2);
                    new AsyncTask<String, Object, Bitmap>() {
                        /* access modifiers changed from: protected */
                        /* renamed from: a */
                        public Bitmap doInBackground(String... strArr) {
                            String str = strArr[0];
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            b.a(str, (OutputStream) byteArrayOutputStream);
                            return b.a(context, byteArrayOutputStream.toByteArray());
                        }

                        /* access modifiers changed from: protected */
                        /* renamed from: a */
                        public void onPostExecute(Bitmap bitmap) {
                            Intent intent = new Intent();
                            intent.setAction("com.miui.hybrid.accessory.SEND_ICON_BITMAP");
                            intent.setPackage(str);
                            intent.putExtra(MijiaAlertModel.KEY_URL, str2);
                            intent.putExtra("bitmap", bitmap);
                            context.sendBroadcast(intent);
                            com.miui.hybrid.accessory.a.b.a.a("IconDialogLauncher", "send bitmap to " + str + " download by " + str2);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{str2});
                }
            }
        }
    }

    private static int b(Context context) {
        try {
            ServiceInfo serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(HybirdServiceUtil.HYBIRD_PACKAGE_NAME, "com.miui.hybrid.host.CommandService"), 128);
            if (!(serviceInfo == null || serviceInfo.metaData == null)) {
                return serviceInfo.metaData.getInt("version", -1);
            }
        } catch (PackageManager.NameNotFoundException e) {
            com.miui.hybrid.accessory.a.b.a.b("IconDialogLauncher", e.getMessage(), e);
        }
        return -1;
    }

    private static List<IconData> b(Context context, List<IconData> list) {
        String str;
        StringBuilder sb;
        for (int size = list.size() - 1; size >= 0; size--) {
            IconData iconData = list.get(size);
            if (iconData == null || TextUtils.isEmpty(iconData.f5588b) || TextUtils.isEmpty(iconData.f5590d)) {
                sb = new StringBuilder();
                sb.append("remove invalid app at index ");
                sb.append(size);
            } else {
                String str2 = iconData.f5590d;
                String str3 = iconData.f5588b;
                if (Build.VERSION.SDK_INT >= 23 && !NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted() && !str3.toLowerCase().startsWith("https")) {
                    str = "remove " + str2 + ", icon url invalid:" + str3;
                    com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", str);
                    list.remove(size);
                } else if (c.a(context, str2)) {
                    sb = new StringBuilder();
                    sb.append("remove ");
                    sb.append(str2);
                    sb.append(", icon already existed.");
                }
            }
            str = sb.toString();
            com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", str);
            list.remove(size);
        }
        return list;
    }

    /* access modifiers changed from: private */
    public static void c(Context context, List<String> list, long j, Map<String, String> map) {
        List<String> a2 = a(context);
        if (!c(context, a2)) {
            long currentTimeMillis = System.currentTimeMillis();
            List<IconData> a3 = a(context, list);
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            if (currentTimeMillis2 > j) {
                com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "pull data timeOut, duration:" + currentTimeMillis2);
            } else if (a3 == null || a3.size() == 0) {
                com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "pull no data from server by " + com.miui.hybrid.accessory.a.e.a.a(list.toArray(), ":"));
            } else {
                b(context, a3);
                if (a3.size() == 0) {
                    com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", a3.size() + " app(s) has hybrid app, but no app left after remove.");
                    return;
                }
                String str = "com.miui.hybrid.accessory";
                if (!a2.contains(str)) {
                    str = a2.get(0);
                    com.miui.hybrid.accessory.a.b.a.b("IconDialogLauncher", "Fallback to package " + str);
                }
                Intent intent = new Intent().setAction("com.miui.hybrid.accessory.LAUNCH_ICON_DIALOG").addFlags(268435456).addCategory(Constants.System.CATEGORY_DEFALUT).putExtra("SRC_PKGNAME", context.getPackageName()).putExtra("EXTRA", map != null ? new HashMap(map) : null).putExtra("SDK_VERSION", 1).putParcelableArrayListExtra("DATA", new ArrayList(a3)).setPackage(str);
                context.startActivity(intent);
                com.miui.hybrid.accessory.a.b.a.a("IconDialogLauncher", "sending show dialog request, intent:" + intent);
                a(context, a3, str);
            }
        }
    }

    private static boolean c(Context context) {
        long a2 = com.miui.hybrid.accessory.a.a.a(context.getContentResolver());
        com.miui.hybrid.accessory.a.b.a.a("IconDialogLauncher", "checkSettingEnabled: getDisableHybridIconTipTS=" + a2);
        return System.currentTimeMillis() > a2 || Math.abs(System.currentTimeMillis() - a2) > 1209600000;
    }

    private static boolean c(Context context, List<String> list) {
        if (!com.miui.hybrid.accessory.a.a.a.b(context, HybirdServiceUtil.HYBIRD_PACKAGE_NAME)) {
            com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "hybird platform not found.");
            return true;
        }
        int b2 = b(context);
        if (b2 < 1) {
            com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "hybird command service version is " + b2 + ", need " + 1);
            return true;
        } else if (list == null || list.size() == 0) {
            com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "No avaliable Target app found.");
            return true;
        } else if (!c(context)) {
            com.miui.hybrid.accessory.a.b.a.c("IconDialogLauncher", "user disable show dialog.");
            return true;
        } else if (b.d(context)) {
            return false;
        } else {
            com.miui.hybrid.accessory.a.b.a.d("IconDialogLauncher", "No Network detected, show icon dialog failed.");
            return true;
        }
    }

    private static Intent d(Context context) {
        return new Intent().setAction("com.miui.hybrid.accessory.LAUNCH_ICON_DIALOG").addCategory(Constants.System.CATEGORY_DEFALUT).putExtra("SRC_PKGNAME", context.getPackageName()).putExtra("SDK_VERSION", 1);
    }
}
