package com.miui.permcenter.privacymanager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import b.b.o.g.d;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.permcenter.privacymanager.a.c;
import com.miui.permcenter.privacymanager.a.e;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permcenter.settings.PrivacyMonitorOpenActivity;
import com.miui.permission.PermissionContract;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.process.IForegroundInfoListener;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static f f6479a;

    /* renamed from: b  reason: collision with root package name */
    private Context f6480b;

    /* renamed from: c  reason: collision with root package name */
    private HandlerThread f6481c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Handler f6482d;
    private List<String> e = new CopyOnWriteArrayList();
    private List<e> f = new CopyOnWriteArrayList();
    private e g;
    /* access modifiers changed from: private */
    public String h;
    private final Map<Long, Integer> i;
    private final Map<Long, Integer> j;
    /* access modifiers changed from: private */
    public List<StatusBar> k;
    private b l;
    /* access modifiers changed from: private */
    public StatusBar m;
    private volatile boolean n = false;
    /* access modifiers changed from: private */
    public StatusBar o = new StatusBar();
    private IForegroundInfoListener.Stub p = new e(this);

    private class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            switch (i) {
                case 257:
                    f.this.b();
                    f.this.c();
                    o.c();
                    f.this.b("Privacy Monitor Register");
                    return;
                case 258:
                    break;
                case 259:
                    String unused = f.this.h = null;
                    return;
                default:
                    switch (i) {
                        case 2455:
                            break;
                        case 2456:
                            Bundle data = message.getData();
                            f.this.a(data.getLong(PermissionContract.Method.GetUsingPermissionList.EXTRA_PERMISSIONID), data.getInt(PermissionContract.Method.GetUsingPermissionList.EXTRA_TYPE), data.getStringArray("extra_data"));
                            return;
                        case 2457:
                            Bundle data2 = message.getData();
                            long j = data2.getLong(PermissionContract.Method.GetUsingPermissionList.EXTRA_PERMISSIONID);
                            int i2 = data2.getInt(PermissionContract.Method.GetUsingPermissionList.EXTRA_TYPE);
                            f.this.a(j, data2.getStringArray("extra_data"), i2);
                            return;
                        default:
                            return;
                    }
            }
            f.this.e();
        }
    }

    class b implements Comparator<StatusBar> {
        b() {
        }

        /* renamed from: a */
        public int compare(StatusBar statusBar, StatusBar statusBar2) {
            return f.this.b(statusBar.permId) - f.this.b(statusBar2.permId);
        }
    }

    private f(Context context) {
        this.f6480b = context;
        this.i = new HashMap();
        Map<Long, Integer> map = this.i;
        Long valueOf = Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER);
        map.put(valueOf, Integer.valueOf(R.drawable.icon_camera_occupy));
        Map<Long, Integer> map2 = this.i;
        Long valueOf2 = Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER);
        map2.put(valueOf2, Integer.valueOf(R.drawable.icon_audio_occupy));
        this.i.put(32L, Integer.valueOf(R.drawable.icon_location_occupy));
        this.j = new HashMap();
        this.j.put(valueOf, Integer.valueOf(this.f6480b.getResources().getColor(R.color.bg_camera_occupy)));
        this.j.put(valueOf2, Integer.valueOf(this.f6480b.getResources().getColor(R.color.bg_audio_occupy)));
        this.j.put(32L, Integer.valueOf(this.f6480b.getResources().getColor(R.color.bg_location_occupy)));
        this.k = new CopyOnWriteArrayList();
        this.l = new b();
        this.f6481c = new HandlerThread("PrivacyMonitorManagerService");
        this.f6481c.start();
        this.f6482d = new a(this.f6481c.getLooper());
        this.f6482d.sendEmptyMessage(257);
    }

    private StatusBar a() {
        if (o.d(this.f6480b) && this.k.size() > 0) {
            for (int size = this.k.size() - 1; size >= 0; size--) {
                StatusBar statusBar = this.k.get(size);
                if (!TextUtils.equals("com.lbe.security.miui", this.o.pkgName) && !this.o.isSameInfo(statusBar) && !a(statusBar.pkgName, statusBar.mUserId, statusBar.permId)) {
                    return statusBar;
                }
            }
        }
        return null;
    }

    public static f a(Context context) {
        synchronized (f.class) {
            if (f6479a == null) {
                f6479a = new f(context);
            }
        }
        return f6479a;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(int r6, java.lang.String r7, long r8, int r10) {
        /*
            r5 = this;
            com.miui.permcenter.privacymanager.StatusBar r0 = new com.miui.permcenter.privacymanager.StatusBar
            r0.<init>(r6, r7, r8)
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r6 = r5.k
            boolean r6 = r6.remove(r0)
            java.lang.String r1 = "BehaviorRecord-Monitor"
            r2 = 1
            r3 = 2
            if (r10 != r3) goto L_0x0019
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r6 = r5.k
            r6.add(r0)
        L_0x0016:
            r5.n = r2
            goto L_0x004f
        L_0x0019:
            r4 = 3
            if (r10 != r4) goto L_0x004f
            com.miui.permcenter.privacymanager.StatusBar r4 = r5.m
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0031
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r0 = r5.k
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0031
            com.miui.permcenter.privacymanager.StatusBar r0 = r5.m
            r5.a((com.miui.permcenter.privacymanager.StatusBar) r0)
        L_0x0031:
            if (r6 != 0) goto L_0x0016
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r0 = "Finish not match start, maybe error. Or check "
            r6.append(r0)
            r6.append(r7)
            java.lang.String r0 = " has launcher?"
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            android.util.Log.e(r1, r6)
            r5.a((long) r8)
        L_0x004f:
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r6 = r5.k
            com.miui.permcenter.privacymanager.f$b r0 = r5.l
            java.util.Collections.sort(r6, r0)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            java.lang.String r7 = " , perm: "
            r6.append(r7)
            r6.append(r8)
            java.lang.String r7 = " , operation "
            r6.append(r7)
            r6.append(r10)
            java.lang.String r7 = " , current size: "
            r6.append(r7)
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r7 = r5.k
            int r7 = r7.size()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Log.i(r1, r6)
            java.util.List<com.miui.permcenter.privacymanager.StatusBar> r6 = r5.k
            int r6 = r6.size()
            if (r6 <= 0) goto L_0x00a4
            android.os.Handler r6 = r5.f6482d
            r7 = 258(0x102, float:3.62E-43)
            r6.removeMessages(r7)
            android.os.Handler r6 = r5.f6482d
            android.os.Message r6 = r6.obtainMessage(r7)
            android.os.Handler r7 = r5.f6482d
            if (r10 != r3) goto L_0x009f
            r8 = 1000(0x3e8, double:4.94E-321)
            goto L_0x00a1
        L_0x009f:
            r8 = 0
        L_0x00a1:
            r7.sendMessageDelayed(r6, r8)
        L_0x00a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.f.a(int, java.lang.String, long, int):void");
    }

    private void a(@NonNull long j2) {
        try {
            d.a("BehaviorRecord-Monitor", Class.forName("android.app.MiuiStatusBarManager"), "clearState", (Class<?>[]) new Class[]{Context.class, String.class}, this.f6480b, String.valueOf(j2));
            this.n = true;
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void a(long j2, int i2, String[] strArr) {
        if (this.i.containsKey(Long.valueOf(j2)) && strArr != null) {
            for (String split : strArr) {
                String[] split2 = split.split("@");
                if (split2.length < 2) {
                    b("Parsing failed for don't Recognize: " + split2);
                } else {
                    int parseInt = Integer.parseInt(split2[0]);
                    String str = split2[1];
                    if (!o.a(this.f6480b, str, parseInt, j2) && ((j2 != PermissionManager.PERM_ID_AUDIO_RECORDER || !a(str)) && (o.c(this.f6480b, str) || (this.k.size() > 0 && i2 == 3)))) {
                        a(parseInt, str, j2, i2);
                    }
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r12v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r12v2 */
    /* access modifiers changed from: private */
    public void a(long j2, String[] strArr, int i2) {
        long j3 = j2;
        String[] strArr2 = strArr;
        int i3 = i2;
        if (strArr2 != null) {
            int length = strArr2.length;
            ? r12 = 0;
            int i4 = 0;
            while (i4 < length) {
                String[] split = strArr2[i4].split("@");
                if (split == null || split.length < 2) {
                    b("Parsing failed for don't recognize the data ");
                } else {
                    int parseInt = Integer.parseInt(split[r12]);
                    String str = split[1];
                    if (!a(str, j3, parseInt) && !a(str, parseInt)) {
                        b(str + " is using " + j3 + " , its operationType: " + i3);
                        ArrayList<String> a2 = com.miui.common.persistence.b.a("PrivacyList", (ArrayList<String>) new ArrayList());
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append("@");
                        sb.append(parseInt);
                        String sb2 = sb.toString();
                        if (!a2.contains(sb2)) {
                            a2.add(sb2);
                            com.miui.common.persistence.b.b("PrivacyList", a2);
                        }
                        if (i3 == 1) {
                            e eVar = this.g;
                            if (eVar == null || eVar.f6342d != -1) {
                                e eVar2 = r1;
                                e eVar3 = this.g;
                                Object obj = "AuthManager@";
                                e eVar4 = new e(str, parseInt, j2, i2);
                                a(eVar2);
                                if (this.g != null) {
                                    b(str + " notification is showing more than 2 items, don't record");
                                    c.a(this.f6480b, this.f);
                                    boolean z = eVar3 == null || (eVar3.f6342d == 0 && this.g.f6342d == -1);
                                    if (d() || !this.e.contains(obj)) {
                                        c.a(this.f6480b, this.g, z);
                                    }
                                } else {
                                    boolean z2 = true;
                                    if (eVar2.f6342d != 1 || this.f.contains(eVar2)) {
                                        z2 = false;
                                    } else {
                                        this.f.add(eVar2);
                                    }
                                    if (o.h(eVar2.a())) {
                                        z2 = false;
                                    }
                                    if (!TextUtils.equals(this.h, eVar2.c()) && (d() || !this.e.contains(eVar2.c()))) {
                                        c.a(this.f6480b, eVar2, z2);
                                        if (z2) {
                                            this.h = eVar2.c();
                                            this.f6482d.sendEmptyMessageDelayed(259, 1000);
                                        }
                                    }
                                }
                            } else {
                                b(str + "notification is showing with group, don't record");
                                if (d() || !this.e.contains("AuthManager@")) {
                                    c.a(this.f6480b, this.g, r12);
                                }
                            }
                        }
                    }
                }
                i4++;
                j3 = j2;
                r12 = 0;
            }
        }
    }

    private void a(@NonNull StatusBar statusBar) {
        a(statusBar.permId);
        this.m = null;
    }

    private boolean a(String str) {
        return Settings.Secure.getString(this.f6480b.getContentResolver(), "default_input_method").startsWith(str);
    }

    /* access modifiers changed from: private */
    public int b(long j2) {
        if (j2 == 32) {
            return 1;
        }
        if (j2 == PermissionManager.PERM_ID_AUDIO_RECORDER) {
            return 2;
        }
        return j2 == PermissionManager.PERM_ID_VIDEO_RECORDER ? 3 : 0;
    }

    /* access modifiers changed from: private */
    public void b() {
        for (Map.Entry<Long, Integer> key : this.i.entrySet()) {
            a(((Long) key.getKey()).longValue());
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PermissionContract.ACTION_USING_PERMISSION_CHANGE);
        intentFilter.addAction(PermissionContract.ACTION_USING_STATUS_BAR_PERMISSION);
        intentFilter.addAction("com.miui.action.sync_status_bar");
        this.f6480b.registerReceiver(new g(this.f6482d), intentFilter, "miui.permission.READ_AND_WIRTE_PERMISSION_MANAGER", (Handler) null);
    }

    /* access modifiers changed from: private */
    public void b(String str) {
        Log.i("BehaviorRecord-Monitor", str);
    }

    /* access modifiers changed from: private */
    public void c() {
        try {
            d.a("BehaviorRecord-Monitor", Class.forName("miui.process.ProcessManager"), "registerForegroundInfoListener", (Class<?>[]) new Class[]{IForegroundInfoListener.class}, this.p);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    private boolean d() {
        return !ScreenUtil.isScreenLocked(this.f6480b) && i.i(this.f6480b);
    }

    /* access modifiers changed from: private */
    public void e() {
        StatusBar a2 = a();
        StatusBar statusBar = this.m;
        if (statusBar != null && !statusBar.equals(a2)) {
            a(this.m);
        }
        this.m = a2;
        if (this.m == null) {
            this.n = true;
        } else if (this.n) {
            try {
                Intent intent = new Intent(this.f6480b, PrivacyMonitorOpenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("DATA", (Serializable) this.k);
                intent.putExtras(bundle);
                PendingIntent a3 = g.a(this.f6480b, 0, intent, 134217728, (Bundle) null, B.b());
                Object newInstance = Class.forName("android.app.MiuiStatusBarState$MiniStateViewBuilder").getConstructor(new Class[]{Context.class}).newInstance(new Object[]{this.f6480b});
                d.a("BehaviorRecord-Monitor", (Object) newInstance, "setAppIcon", (Class<?>[]) new Class[]{Integer.TYPE}, this.i.get(Long.valueOf(this.m.permId)));
                d.a("BehaviorRecord-Monitor", (Object) newInstance, "setBackgroundColor", (Class<?>[]) new Class[]{Integer.TYPE}, this.j.get(Long.valueOf(this.m.permId)));
                if (a3 != null) {
                    d.a("BehaviorRecord-Monitor", (Object) newInstance, "setPendingIntent", (Class<?>[]) new Class[]{PendingIntent.class}, a3);
                }
                RemoteViews remoteViews = (RemoteViews) d.a("BehaviorRecord-Monitor", (Object) newInstance, "build", (Class<?>[]) new Class[0], new Object[0]);
                Class<?> cls = Class.forName("android.app.MiuiStatusBarState");
                Object newInstance2 = cls.getConstructor(new Class[]{String.class, RemoteViews.class, RemoteViews.class, Integer.TYPE}).newInstance(new Object[]{String.valueOf(this.m.permId), null, remoteViews, Integer.valueOf(((Integer) d.a("BehaviorRecord-Monitor", cls, "PRIORITY_LOW")).intValue())});
                d.a("BehaviorRecord-Monitor", Class.forName("android.app.MiuiStatusBarManager"), "applyState", (Class<?>[]) new Class[]{Context.class, cls.cast(newInstance2).getClass()}, this.f6480b, cls.cast(newInstance2));
                this.n = false;
            } catch (Exception e2) {
                Log.e("BehaviorRecord-Monitor", "Create StatusBar error: " + e2);
            }
        }
    }

    public void a(StatusBarNotification statusBarNotification) {
        String tag = statusBarNotification.getTag();
        if (tag != null && tag.startsWith("AuthManager")) {
            b("on Remove: " + tag);
            this.e.remove(tag);
            String[] split = tag.split("@");
            if (split.length < 4) {
                b("Parsing result length no more than 4: " + tag);
                this.g = null;
                return;
            }
            this.f.remove(new e(split[2], Integer.parseInt(split[1]), Long.parseLong(split[3])));
        }
    }

    public void a(e eVar) {
        e eVar2 = this.g;
        if (eVar2 == null) {
            int i2 = 0;
            for (e next : this.f) {
                if (!next.a(eVar)) {
                    this.g = new e(-1);
                    return;
                } else if (next.a(eVar) && !next.equals(eVar)) {
                    i2++;
                }
            }
            if (i2 >= 2) {
                this.g = new e(eVar.b(), eVar.d(), 0);
            } else {
                this.g = null;
            }
        } else if (!eVar2.a(eVar)) {
            this.g = new e(-1);
        }
    }

    public boolean a(String str, int i2) {
        c cVar = new c(str, i2);
        cVar.d(com.miui.common.persistence.b.a(cVar.a(), 0));
        return true ^ cVar.a(1);
    }

    public boolean a(String str, int i2, long j2) {
        c cVar = new c(str, i2);
        cVar.d(com.miui.common.persistence.b.a(cVar.a(), 0));
        if (j2 == PermissionManager.PERM_ID_AUDIO_RECORDER) {
            return !cVar.a(3);
        }
        if (j2 == 32) {
            return !cVar.a(2);
        }
        if (j2 == PermissionManager.PERM_ID_VIDEO_RECORDER) {
            return !cVar.a(4);
        }
        return false;
    }

    public boolean a(String str, long j2, int i2) {
        return !o.f(j2) || o.a(this.f6480b, str, i2, j2);
    }

    public void b(StatusBarNotification statusBarNotification) {
        String tag = statusBarNotification.getTag();
        if (tag != null && tag.startsWith("AuthManager") && !this.e.contains(tag)) {
            this.e.add(tag);
        }
    }
}
