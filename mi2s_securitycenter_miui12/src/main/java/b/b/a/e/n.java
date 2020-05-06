package b.b.a.e;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import b.b.a.b.a;
import b.b.c.j.v;
import b.b.o.g.d;
import b.b.o.g.e;
import com.miui.activityutil.o;
import com.miui.antispam.ui.activity.AddAntiSpamActivity;
import com.miui.antispam.ui.activity.CallLogListActivity;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.provider.ExtraTelephony;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.util.IOUtils;

public class n {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1454a = Application.c().getString(R.string.activity_title_antispam);

    /* renamed from: b  reason: collision with root package name */
    public static HashMap<String, String> f1455b = new j();

    public static int a() {
        try {
            String str = SystemProperties.get("ro.miui.ui.version.code");
            if (!TextUtils.isEmpty(str)) {
                return Integer.parseInt(str);
            }
            return 0;
        } catch (Exception unused) {
            return 0;
        }
    }

    public static int a(int i) {
        if (i == 1 || i == 2) {
            return i;
        }
        return 1;
    }

    public static int a(Context context, int i) {
        int i2 = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"count(*)"}, "type = ? AND sync_dirty <> ? AND sim_id = ? ", new String[]{o.f2310b, String.valueOf(1), String.valueOf(i)}, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                i2 = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("AntiSpamUtils", "cursor error when get blacklist count. ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return i2;
    }

    public static int a(Context context, int i, int i2) {
        int i3 = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ExtraTelephony.Keyword.CONTENT_URI, new String[]{"count(*)"}, "type = ? AND sim_id = ? ", new String[]{String.valueOf(i), String.valueOf(i2)}, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                i3 = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("AntiSpamUtils", "cursor error when get keyword count. ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return i3;
    }

    public static int a(Context context, String str) {
        Cursor query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, (String[]) null, "PHONE_NUMBERS_EQUAL(number, '" + str + "', 0) AND " + "firewalltype" + " <> 0 ", (String[]) null, (String) null);
        if (query == null) {
            return 0;
        }
        try {
            return query.getCount();
        } finally {
            query.close();
        }
    }

    public static String a(Activity activity) {
        try {
            return (String) e.a((Object) activity, (Class<?>) Activity.class, "mReferrer");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String a(Context context, long j, boolean z) {
        return !z ? DateUtils.formatDateTime(context, j, 524305) : DateUtils.getRelativeTimeSpanString(context, j, false).toString();
    }

    public static String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.length() <= 7) {
            return "short phone number!";
        }
        return str.substring(0, 3) + "****" + str.substring(7);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: java.io.InputStream} */
    /* JADX WARNING: type inference failed for: r4v1, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r4v9 */
    /* JADX WARNING: type inference failed for: r4v13 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.json.JSONArray a(android.content.Context r4) {
        /*
            r0 = 0
            android.content.res.AssetManager r4 = r4.getAssets()     // Catch:{ IOException -> 0x0027, JSONException -> 0x0021, all -> 0x001c }
            java.lang.String r1 = "address.json"
            java.io.InputStream r4 = r4.open(r1)     // Catch:{ IOException -> 0x0027, JSONException -> 0x0021, all -> 0x001c }
            org.json.JSONArray r1 = new org.json.JSONArray     // Catch:{ IOException -> 0x001a, JSONException -> 0x0018 }
            java.lang.String r2 = miui.util.IOUtils.toString(r4)     // Catch:{ IOException -> 0x001a, JSONException -> 0x0018 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x001a, JSONException -> 0x0018 }
            miui.util.IOUtils.closeQuietly(r4)
            return r1
        L_0x0018:
            r1 = move-exception
            goto L_0x0023
        L_0x001a:
            r1 = move-exception
            goto L_0x0029
        L_0x001c:
            r4 = move-exception
            r3 = r0
            r0 = r4
            r4 = r3
            goto L_0x0031
        L_0x0021:
            r1 = move-exception
            r4 = r0
        L_0x0023:
            r1.printStackTrace()     // Catch:{ all -> 0x0030 }
            goto L_0x002c
        L_0x0027:
            r1 = move-exception
            r4 = r0
        L_0x0029:
            r1.printStackTrace()     // Catch:{ all -> 0x0030 }
        L_0x002c:
            miui.util.IOUtils.closeQuietly(r4)
            return r0
        L_0x0030:
            r0 = move-exception
        L_0x0031:
            miui.util.IOUtils.closeQuietly(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.e.n.a(android.content.Context):org.json.JSONArray");
    }

    public static void a(Context context, String str, int i) {
        Intent intent = new Intent((String) d.a("AntiSpamUtils", (Class<?>) Intent.class, "ACTION_CALL_PRIVILEGED"), Uri.fromParts("tel", str, (String) null));
        a.a(context, intent, a.f1316a);
        if (i != -1) {
            SubscriptionManager.putSlotIdExtra(intent, i);
            intent.putExtra("com.android.phone.extra.slot", i);
        }
        intent.setFlags(335544320);
        context.startActivity(intent);
    }

    public static void a(Context context, String str, int i, int i2) {
        Notification build;
        int i3;
        Context context2 = context;
        int i4 = i;
        int i5 = i2;
        NotificationManager notificationManager = (NotificationManager) context2.getSystemService("notification");
        if (!MiuiSettings.System.getBoolean(context.getContentResolver(), "extra_show_security_notification", false)) {
            Resources resources = context.getResources();
            Intent intent = new Intent("miui.intent.action.SET_FIREWALL");
            intent.addFlags(67108864);
            intent.putExtra("notification_intercept_content", i4);
            intent.putExtra(":miui:starting_window_label", "");
            intent.putExtra("is_from_intercept_notification", true);
            intent.putExtra("notification_block_type", i5);
            v.a(notificationManager, "com.miui.antispam", f1454a, 2);
            if (i4 == 2) {
                int d2 = d();
                build = v.a(context2, "com.miui.antispam").setTicker(context2.getString(R.string.fw_blocked)).setWhen(System.currentTimeMillis()).setContentTitle(context2.getString(R.string.fw_blocked)).setContentText(context2.getString(R.string.fw_blocked_count) + resources.getQuantityString(R.plurals.fw_blocked_count_sms, d2, new Object[]{Integer.valueOf(d2)})).setContentIntent(PendingIntent.getActivity(context2, 0, intent, 134217728)).setSmallIcon(R.drawable.antispam_small).setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_anti_spam)).build();
                build.flags = build.flags | 16;
                Object a2 = d.a("AntiSpamUtils", (Object) build, "extraNotification");
                d.a("AntiSpamUtils", a2, "setEnableFloat", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
                d.a("AntiSpamUtils", a2, "setEnableKeyguard", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
                b.b.o.a.a.a(build, true);
                b.b.o.a.a.a(build, 0);
                i3 = 798;
            } else {
                int e = e();
                String c2 = c(context, str);
                Notification.Builder contentTitle = v.a(context2, "com.miui.antispam").setTicker(context2.getString(R.string.fw_blocked)).setWhen(System.currentTimeMillis()).setContentTitle(context2.getString(R.string.fw_blocked));
                StringBuilder sb = new StringBuilder();
                sb.append(context2.getString(R.string.fw_blocked_count));
                int i6 = e == 1 ? R.plurals.fw_blocked_count_call_one : R.plurals.fw_blocked_count_call;
                Object[] objArr = new Object[2];
                NotificationManager notificationManager2 = notificationManager;
                objArr[0] = Integer.valueOf(e);
                if (TextUtils.isEmpty(c2)) {
                    c2 = str;
                }
                objArr[1] = c2;
                sb.append(resources.getQuantityString(i6, e, objArr));
                build = contentTitle.setContentText(sb.toString()).setContentIntent(PendingIntent.getActivity(context2, 0, intent, 134217728)).setSmallIcon(R.drawable.antispam_small).setPriority(2).setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_anti_spam)).build();
                build.flags |= 16;
                Object a3 = d.a("AntiSpamUtils", (Object) build, "extraNotification");
                d.a("AntiSpamUtils", a3, "setEnableFloat", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
                d.a("AntiSpamUtils", a3, "setEnableKeyguard", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
                b.b.o.a.a.a(build, true);
                b.b.o.a.a.a(build, 0);
                i3 = 797;
                notificationManager = notificationManager2;
            }
            notificationManager.notify(i3, build);
            b.b.a.a.a.a("antispam_noti_action", i2 == 17 ? "overseas" : "mainland", "show");
            b.b.a.a.a.a("antispam_notification", i == 2 ? "sms" : "call", "show");
        }
    }

    public static void a(Context context, String str, int i, String str2) {
        new l(str, context, i, str2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static void a(Context context, String str, String str2) {
        b.b.c.j.d.a(new k(context.getApplicationContext(), str));
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClassName("com.android.mms", "com.android.mms.ui.BlockedConversationActivity");
        intent.putExtra("number", str);
        intent.putExtra("extraData", str2);
        context.startActivity(intent);
    }

    public static void a(Context context, String[] strArr, int i, Integer[] numArr, int i2, int i3) {
        Intent intent = new Intent("miui.intent.action.ADD_FIREWALL");
        intent.setType("vnd.android.cursor.item/firewall-blacklist");
        intent.putExtra("numbers", strArr);
        intent.putExtra(AddAntiSpamActivity.e, i);
        intent.putExtra(AddAntiSpamActivity.g, i2);
        intent.putExtra(AddAntiSpamActivity.f2508d, i3);
        intent.putExtra(AddAntiSpamActivity.f, c.a(numArr));
        context.startActivity(intent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
        r11 = f(r11);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r10, java.lang.String r11, int r12, int r13, int r14) {
        /*
            java.lang.String r0 = "sync_dirty"
            java.lang.String r1 = "state"
            boolean r2 = android.text.TextUtils.isEmpty(r11)
            r3 = 0
            if (r2 == 0) goto L_0x000c
            return r3
        L_0x000c:
            java.lang.String r11 = f(r11)
            android.content.ContentResolver r4 = r10.getContentResolver()
            android.net.Uri r5 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI
            r6 = 0
            r2 = 4
            java.lang.String[] r8 = new java.lang.String[r2]
            r8[r3] = r11
            java.lang.String r11 = java.lang.String.valueOf(r13)
            r13 = 1
            r8[r13] = r11
            java.lang.String r11 = java.lang.String.valueOf(r14)
            r14 = 2
            r8[r14] = r11
            java.lang.String r11 = java.lang.String.valueOf(r13)
            r2 = 3
            r8[r2] = r11
            r9 = 0
            java.lang.String r7 = "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? "
            android.database.Cursor r11 = r4.query(r5, r6, r7, r8, r9)
            if (r11 == 0) goto L_0x0096
            boolean r4 = r11.moveToNext()     // Catch:{ Exception -> 0x0089 }
            if (r4 == 0) goto L_0x0083
            int r4 = r11.getColumnIndex(r1)     // Catch:{ Exception -> 0x0089 }
            int r4 = r11.getInt(r4)     // Catch:{ Exception -> 0x0089 }
            int r5 = r11.getColumnIndex(r0)     // Catch:{ Exception -> 0x0089 }
            int r5 = r11.getInt(r5)     // Catch:{ Exception -> 0x0089 }
            if (r4 == 0) goto L_0x007f
            if (r4 == r12) goto L_0x007f
            android.content.ContentValues r12 = new android.content.ContentValues     // Catch:{ Exception -> 0x0089 }
            r12.<init>()     // Catch:{ Exception -> 0x0089 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0089 }
            r12.put(r1, r4)     // Catch:{ Exception -> 0x0089 }
            if (r5 != r2) goto L_0x0069
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0089 }
            r12.put(r0, r14)     // Catch:{ Exception -> 0x0089 }
        L_0x0069:
            android.content.ContentResolver r10 = r10.getContentResolver()     // Catch:{ Exception -> 0x0089 }
            android.net.Uri r14 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI     // Catch:{ Exception -> 0x0089 }
            long r0 = r11.getLong(r3)     // Catch:{ Exception -> 0x0089 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ Exception -> 0x0089 }
            android.net.Uri r14 = android.net.Uri.withAppendedPath(r14, r0)     // Catch:{ Exception -> 0x0089 }
            r0 = 0
            r10.update(r14, r12, r0, r0)     // Catch:{ Exception -> 0x0089 }
        L_0x007f:
            r11.close()
            return r13
        L_0x0083:
            r11.close()
            goto L_0x0096
        L_0x0087:
            r10 = move-exception
            goto L_0x0092
        L_0x0089:
            r10 = move-exception
            java.lang.String r12 = "AntiSpamUtils"
            java.lang.String r13 = "Exception when check number in phoneList. "
            android.util.Log.e(r12, r13, r10)     // Catch:{ all -> 0x0087 }
            goto L_0x0083
        L_0x0092:
            r11.close()
            throw r10
        L_0x0096:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.e.n.a(android.content.Context, java.lang.String, int, int, int):boolean");
    }

    public static int b(Context context, int i) {
        if (c.e(context)) {
            return 1;
        }
        return a(i + 1);
    }

    public static int b(Context context, String str) {
        Cursor query = context.getContentResolver().query(ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI, new String[]{"message_count"}, "PHONE_NUMBERS_EQUAL(address, '" + str + "', 0)", (String[]) null, (String) null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getInt(0);
                }
                query.close();
            } finally {
                query.close();
            }
        }
        return 0;
    }

    public static String b(Context context) {
        Bundle bundle;
        try {
            bundle = context.getContentResolver().call(Uri.parse("content://com.miui.virtualsim.provider.virtualsimInfo"), "getCarrierName", (String) null, (Bundle) null);
        } catch (Exception e) {
            Log.e("AntiSpamUtils", "getVirtualSimCarrierName e" + e);
            bundle = null;
        }
        if (bundle == null) {
            return null;
        }
        return bundle.getString("carrierName");
    }

    public static String b(String str) {
        try {
            if (str.startsWith("+")) {
                return String.format(Locale.getDefault(), "+%d", new Object[]{Long.valueOf(str.substring(1))});
            }
            return String.format(Locale.getDefault(), "%d", new Object[]{Long.valueOf(str)});
        } catch (Exception unused) {
            return str;
        }
    }

    public static List<SubscriptionInfo> b() {
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        if (subscriptionInfoList == null || subscriptionInfoList.size() == 0) {
            return null;
        }
        if (subscriptionInfoList.size() > 1) {
            Collections.sort(subscriptionInfoList, new m());
        }
        return subscriptionInfoList;
    }

    public static void b(Context context, String str, int i) {
        Intent intent = new Intent(context, CallLogListActivity.class);
        intent.putExtra("number", str);
        intent.putExtra("number_presentation", i);
        context.startActivity(intent);
    }

    public static void b(Context context, String str, int i, int i2, int i3) {
        ContentValues contentValues;
        ContentResolver contentResolver;
        Uri withAppendedPath;
        String f = f(str);
        if (!TextUtils.isEmpty(f)) {
            if (!c.b(context, i3)) {
                c.a(context, i3, true);
            }
            Cursor query = context.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, (String[]) null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ?", new String[]{f, String.valueOf(i2), String.valueOf(i3), String.valueOf(1)}, (String) null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        long j = query.getLong(0);
                        int i4 = query.getInt(query.getColumnIndex(AdvancedSlider.STATE));
                        int i5 = query.getInt(query.getColumnIndex("sync_dirty"));
                        if (i4 == 0 && i != 0) {
                            contentValues = new ContentValues();
                            contentValues.put(AdvancedSlider.STATE, Integer.valueOf(i == 1 ? 2 : 1));
                            if (i5 == 3) {
                                contentValues.put("sync_dirty", 2);
                            }
                            contentResolver = context.getContentResolver();
                            withAppendedPath = Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(j));
                        } else if (i == 0 || i4 == i) {
                            if (i5 != 3) {
                                if (i5 != 2) {
                                    context.getContentResolver().delete(Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(j)), (String) null, (String[]) null);
                                }
                            }
                            contentValues = new ContentValues();
                            contentValues.put("sync_dirty", 1);
                            contentResolver = context.getContentResolver();
                            withAppendedPath = Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(j));
                        }
                        contentResolver.update(withAppendedPath, contentValues, (String) null, (String[]) null);
                    } catch (Exception e) {
                        Log.e("AntiSpamUtils", "Cursor exception in removeBlacklist(): ", e);
                    } catch (Throwable th) {
                        query.close();
                        throw th;
                    }
                }
                query.close();
            }
        }
    }

    public static boolean b(Activity activity) {
        String a2 = a(activity);
        if (TextUtils.isEmpty(a2)) {
            return true;
        }
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(a2, 0);
            return packageInfo == null || (packageInfo.applicationInfo.flags & 1) == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int c(Context context, int i) {
        int i2 = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"count(*)"}, "type = ? AND sync_dirty <> ? AND sim_id = ? ", new String[]{"2", String.valueOf(1), String.valueOf(i)}, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                i2 = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("AntiSpamUtils", "cursor error when get whitelist count. ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return i2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v1 */
    /* JADX WARNING: type inference failed for: r0v2, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String c(android.content.Context r7, java.lang.String r8) {
        /*
            boolean r0 = e((java.lang.String) r8)
            if (r0 == 0) goto L_0x000f
            int r7 = miui.telephony.PhoneNumberUtils.getPresentation(r8)
            java.lang.String r7 = miui.telephony.PhoneNumberUtils.getPresentationString(r7)
            return r7
        L_0x000f:
            r0 = 0
            android.content.ContentResolver r1 = r7.getContentResolver()     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            android.net.Uri r7 = android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            int r2 = b.b.a.e.q.a()     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            android.net.Uri r7 = b.b.a.e.q.a(r7, r2)     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            android.net.Uri r2 = android.net.Uri.withAppendedPath(r7, r8)     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            java.lang.String r7 = "display_name"
            java.lang.String[] r3 = new java.lang.String[]{r7}     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            r4 = 0
            r5 = 0
            r6 = 0
            android.database.Cursor r7 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0042, all -> 0x0040 }
            if (r7 == 0) goto L_0x004b
            boolean r8 = r7.moveToFirst()     // Catch:{ Exception -> 0x003e }
            if (r8 == 0) goto L_0x004b
            r8 = 0
            java.lang.String r8 = r7.getString(r8)     // Catch:{ Exception -> 0x003e }
            r0 = r8
            goto L_0x004b
        L_0x003e:
            r8 = move-exception
            goto L_0x0044
        L_0x0040:
            r8 = move-exception
            goto L_0x0051
        L_0x0042:
            r8 = move-exception
            r7 = r0
        L_0x0044:
            java.lang.String r1 = "AntiSpamUtils"
            java.lang.String r2 = "Cursor err in queryContactName(): "
            android.util.Log.e(r1, r2, r8)     // Catch:{ all -> 0x004f }
        L_0x004b:
            miui.util.IOUtils.closeQuietly(r7)
            return r0
        L_0x004f:
            r8 = move-exception
            r0 = r7
        L_0x0051:
            miui.util.IOUtils.closeQuietly(r0)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.e.n.c(android.content.Context, java.lang.String):java.lang.String");
    }

    public static String c(String str) {
        return (Build.IS_INTERNATIONAL_BUILD || !d(str)) ? str : str.substring(6);
    }

    public static boolean c() {
        int scaleMode = MiuiConfiguration.getScaleMode();
        return scaleMode == 14 || scaleMode == 15 || scaleMode == 11;
    }

    public static boolean c(Context context) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            return true;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SENDTO");
        intent.setData(Uri.parse("smsto:"));
        return "com.android.mms".equals(context.getPackageManager().resolveActivity(intent, 0).activityInfo.packageName);
    }

    private static int d() {
        com.miui.antispam.db.d.d(com.miui.antispam.db.d.b() + 1);
        return com.miui.antispam.db.d.b();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002c, code lost:
        if (android.text.TextUtils.equals(r1, r3.getTag()) == false) goto L_0x002e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.util.Pair<java.lang.String, java.lang.String> d(android.content.Context r3, java.lang.String r4) {
        /*
            r0 = 0
            if (r4 != 0) goto L_0x0009
            android.util.Pair r3 = new android.util.Pair
            r3.<init>(r0, r0)
            return r3
        L_0x0009:
            java.lang.String r1 = c((android.content.Context) r3, (java.lang.String) r4)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 == 0) goto L_0x0032
            r2 = 0
            miui.yellowpage.YellowPagePhone r3 = miui.yellowpage.YellowPageUtils.getPhoneInfo(r3, r4, r2)
            if (r3 == 0) goto L_0x0032
            boolean r4 = r3.isYellowPage()
            if (r4 == 0) goto L_0x002e
            java.lang.String r1 = r3.getYellowPageName()
            java.lang.String r4 = r3.getTag()
            boolean r4 = android.text.TextUtils.equals(r1, r4)
            if (r4 != 0) goto L_0x0032
        L_0x002e:
            java.lang.String r0 = r3.getTag()
        L_0x0032:
            android.util.Pair r3 = new android.util.Pair
            r3.<init>(r1, r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.e.n.d(android.content.Context, java.lang.String):android.util.Pair");
    }

    public static void d(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_read", 1);
        context.getContentResolver().update(CallLog.Calls.CONTENT_URI, contentValues, "firewalltype <> 0 ", (String[]) null);
    }

    public static boolean d(Context context, int i) {
        return c.g(context) && i == c.b(context);
    }

    public static boolean d(String str) {
        return !TextUtils.isEmpty(str) && str.length() > 6 && (str.startsWith("125831") || str.startsWith("125832") || str.startsWith("125833"));
    }

    private static int e() {
        com.miui.antispam.db.d.e(com.miui.antispam.db.d.c() + 1);
        return com.miui.antispam.db.d.c();
    }

    public static void e(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("read", 1);
        contentValues.put("seen", 1);
        context.getContentResolver().update(ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI, contentValues, (String) null, (String[]) null);
    }

    public static boolean e(Context context, String str) {
        Cursor query = context.getContentResolver().query(Uri.withAppendedPath(q.a(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, q.a()), str), new String[]{"display_name"}, (String) null, (String[]) null, (String) null);
        boolean z = false;
        if (query != null) {
            try {
                if (query.getCount() > 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                Log.e("AntiSpamUtils", "Cursor err in queryContactName(): ", e);
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean e(String str) {
        return str.equals("-3") || str.equals("-2") || str.equals("-1");
    }

    public static String f(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (str.matches("[a-zA-Z]*-[a-zA-Z]*")) {
            return str.substring(str.indexOf("-"));
        }
        String normalizeNumber = str.contains("*") ? ExtraTelephony.normalizeNumber(str) : PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true);
        return TextUtils.isEmpty(normalizeNumber) ? str : normalizeNumber;
    }

    public static void f(Context context, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("read", 1);
        contentValues.put("seen", 1);
        context.getContentResolver().update(g(str), contentValues, (String) null, (String[]) null);
    }

    private static Uri g(String str) {
        return ContentUris.withAppendedId(ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI.buildUpon().appendQueryParameter("blocked_conv_addr", str).build(), 0);
    }

    public static void g(Context context, String str) {
        Intent intent = new Intent("miui.intent.action.MARK_ANTISPAM");
        intent.putExtra("com.miui.yellowpage.extra.number", str);
        context.startActivity(intent);
    }

    public static void h(Context context, String str) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("smsto", str, (String) null));
        intent.setFlags(335544320);
        context.startActivity(intent);
    }
}
