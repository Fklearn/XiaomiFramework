package com.miui.permcenter.privacymanager.b;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.v;
import b.b.o.g.d;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.process.IForegroundInfoListener;

public class o {

    /* renamed from: a  reason: collision with root package name */
    static final boolean f6385a = Log.isLoggable("NotifyReminder", 2);

    /* renamed from: b  reason: collision with root package name */
    private static List<String> f6386b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private static o f6387c = null;

    /* renamed from: d  reason: collision with root package name */
    private final int f6388d = 1;
    private final String e = "curr_pkg";
    private final String f = "prev_pkg";
    private Context g;
    private PackageManager h;
    private ActivityManager i;
    private NotificationManager j;
    /* access modifiers changed from: private */
    public Handler k;
    private HandlerThread l;
    private q m;
    private String n;
    private IForegroundInfoListener.Stub o = new n(this);

    final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            Bundle data;
            if (message.what == 1 && (data = message.getData()) != null) {
                String string = data.getString("curr_pkg", (String) null);
                String string2 = data.getString("prev_pkg", (String) null);
                if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
                    o.this.a(string, string2);
                }
            }
        }
    }

    static {
        f6386b.add("com.google.android.packageinstaller");
        f6386b.add("com.google.android.permissioncontroller");
    }

    private o(Context context) {
        this.g = context;
        this.h = this.g.getPackageManager();
        this.i = (ActivityManager) this.g.getSystemService("activity");
        this.j = (NotificationManager) this.g.getSystemService("notification");
        this.l = new HandlerThread(o.class.getSimpleName());
        this.l.start();
        this.k = new a(this.l.getLooper());
        this.m = q.f();
    }

    private Notification a(@NonNull Context context, @NonNull String str) {
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon_securitycenter);
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
        intent.putExtra("extra_pkgname", str);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 134217728);
        Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
        a2.setSmallIcon(R.drawable.security_small_icon).setWhen(System.currentTimeMillis()).setLargeIcon(decodeResource).setContentTitle(context.getString(R.string.intl_perm_notification_title)).setContentText(context.getString(R.string.intl_perm_notification_content)).setAutoCancel(true).setPriority(2).setShowWhen(true).setSound(Uri.EMPTY, (AudioAttributes) null).setContentIntent(activity);
        return a2.build();
    }

    public static o a(Context context) {
        synchronized (o.class) {
            if (f6387c == null) {
                f6387c = new o(context);
            }
        }
        return f6387c;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|(1:4)|5|6|(1:8)|9|10|(3:12|13|15)(1:17)) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        return r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x000b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0015 */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0019 A[Catch:{ Exception -> 0x0020 }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x000f A[Catch:{ Exception -> 0x0015 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(@android.support.annotation.NonNull android.app.ActivityManager.RecentTaskInfo r3) {
        /*
            r2 = this;
            r0 = 0
            android.content.ComponentName r1 = r3.origActivity     // Catch:{ Exception -> 0x000b }
            if (r1 == 0) goto L_0x000b
            android.content.ComponentName r1 = r3.origActivity     // Catch:{ Exception -> 0x000b }
            java.lang.String r0 = r1.getPackageName()     // Catch:{ Exception -> 0x000b }
        L_0x000b:
            android.content.ComponentName r1 = r3.baseActivity     // Catch:{ Exception -> 0x0015 }
            if (r1 == 0) goto L_0x0015
            android.content.ComponentName r1 = r3.baseActivity     // Catch:{ Exception -> 0x0015 }
            java.lang.String r0 = r1.getPackageName()     // Catch:{ Exception -> 0x0015 }
        L_0x0015:
            android.content.ComponentName r1 = r3.topActivity     // Catch:{ Exception -> 0x0020 }
            if (r1 == 0) goto L_0x0020
            android.content.ComponentName r3 = r3.topActivity     // Catch:{ Exception -> 0x0020 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ Exception -> 0x0020 }
            r0 = r3
        L_0x0020:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.b.o.a(android.app.ActivityManager$RecentTaskInfo):java.lang.String");
    }

    private void a(String str) {
        if (f6385a) {
            Log.d("NotifyReminder", str);
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, String str2) {
        a("--> CurrPkg: " + str + " PrevPkg: " + str2);
        StringBuilder sb = new StringBuilder();
        sb.append("  * mWaitingShowNotificationPackage: ");
        sb.append(this.n);
        a(sb.toString());
        a("  * isRunningTask: " + c(this.n));
        if (!f6386b.contains(str)) {
            if (!TextUtils.isEmpty(this.n) && this.n.equalsIgnoreCase(str2) && !c(this.n)) {
                e(this.n);
            }
            if (!d(str)) {
                a("CurrPkg is not sensitiveSupportedPackage");
                this.n = null;
                return;
            }
            this.n = str;
        }
    }

    private void a(String str, Throwable th) {
        if (f6385a) {
            Log.d("NotifyReminder", str, th);
        }
    }

    private PackageInfo b(@NonNull String str) {
        try {
            return this.h.getPackageInfo(str, MpegAudioHeader.MAX_FRAME_SIZE_BYTES);
        } catch (PackageManager.NameNotFoundException e2) {
            a("NameNotFoundException", (Throwable) e2);
            return null;
        }
    }

    private List<String> b() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ActivityManager.RecentTaskInfo a2 : this.i.getRecentTasks(20, 0)) {
            String a3 = a(a2);
            if (!TextUtils.isEmpty(a3)) {
                arrayList.add(a3);
            }
        }
        if (f6385a) {
            for (String str : arrayList) {
                a("task is running: " + str);
            }
        }
        return arrayList;
    }

    private boolean c(@NonNull String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return b().contains(str);
    }

    private boolean d(@NonNull String str) {
        boolean z;
        String str2;
        PackageInfo b2 = b(str);
        if (b2 == null) {
            str2 = "allowShowPermNotify PackageInfo is null";
        } else if (b2.requestedPermissions == null) {
            str2 = "allowShowPermNotify PackageInfo.permissions is null";
        } else {
            boolean z2 = b2.applicationInfo.uid < 10000;
            boolean z3 = (b2.applicationInfo.flags & 1) != 0;
            String[] strArr = b2.requestedPermissions;
            int length = strArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = false;
                    break;
                }
                if (p.a().contains(strArr[i2])) {
                    z = true;
                    break;
                }
                i2++;
            }
            a("allowShowPermNotify includingSensitivePerm: " + z);
            a("allowShowPermNotify uidSmallThan10000: " + z2);
            a("allowShowPermNotify systemFlag: " + z3);
            return z && !z3 && !z2;
        }
        a(str2);
        return false;
    }

    private boolean e(String str) {
        long currentTimeMillis = System.currentTimeMillis();
        long c2 = this.m.c();
        int b2 = this.m.b();
        int a2 = this.m.a(str);
        int b3 = s.b();
        int a3 = s.a();
        boolean e2 = this.m.e();
        boolean contains = s.c().contains(str);
        StringBuilder sb = new StringBuilder();
        sb.append("*** show Notify: ");
        sb.append(str);
        sb.append(" (curr: ");
        sb.append(currentTimeMillis);
        sb.append(" prevShow: ");
        sb.append(c2);
        sb.append(" timeDiff:");
        long j2 = currentTimeMillis - c2;
        sb.append(j2);
        sb.append(" totalCount:");
        sb.append(b2);
        sb.append(" pkgCount:");
        sb.append(a2);
        sb.append(" isValid:");
        sb.append(e2);
        sb.append(" maximumTotalCount:");
        sb.append(b3);
        sb.append(" maximumPkgCount:");
        sb.append(a3);
        sb.append(" isBelongWhileList:");
        sb.append(contains);
        sb.append(")");
        a(sb.toString());
        if (contains || !e2 || a2 >= a3 || b2 >= b3) {
            return false;
        }
        if (c2 == 0 || j2 > 86400000) {
            this.j.notify(0, a(this.g, str));
            this.m.a(str, currentTimeMillis);
            if (this.m.b() < b3) {
                return true;
            }
            this.m.a(false);
            return true;
        }
        a("*** show Notify skip due in time window");
        return false;
    }

    public void a() {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            a("skip monitor");
            return;
        }
        a("startMonitor");
        try {
            d.a("NotifyReminder", Class.forName("miui.process.ProcessManager"), "registerForegroundInfoListener", (Class<?>[]) new Class[]{IForegroundInfoListener.class}, this.o);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }
}
