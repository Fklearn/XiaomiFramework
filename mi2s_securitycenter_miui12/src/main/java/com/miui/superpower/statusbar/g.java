package com.miui.superpower.statusbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.miui.superpower.statusbar.panel.NotificationPanelLayout;
import java.lang.ref.WeakReference;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static volatile g f8169a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f8170b;

    /* renamed from: c  reason: collision with root package name */
    private WindowManager f8171c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View f8172d;
    private View e;
    /* access modifiers changed from: private */
    public NotificationPanelLayout f;
    private WindowManager.LayoutParams g;
    private int h;
    private int i;
    private int j;
    /* access modifiers changed from: private */
    public boolean k = true;
    /* access modifiers changed from: private */
    public boolean l = false;
    /* access modifiers changed from: private */
    public boolean m = false;
    /* access modifiers changed from: private */
    public boolean n = false;
    /* access modifiers changed from: private */
    public b o;
    private a p;
    private DisplayManager q;
    private Display r;
    private Point s;
    private DisplayManager.DisplayListener t = new e(this);
    private final String u = "typefrom_status_bar_expansion";
    private final Runnable v = new f(this);

    public static final class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private static a f8173a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<g> f8174b;

        private a(Looper looper, g gVar) {
            super(looper);
            this.f8174b = new WeakReference<>(gVar);
        }

        /* access modifiers changed from: private */
        public static a b(g gVar) {
            if (f8173a == null) {
                f8173a = new a(Looper.getMainLooper(), gVar);
            }
            return f8173a;
        }

        public void handleMessage(Message message) {
            g gVar = (g) this.f8174b.get();
            if (gVar != null) {
                int i = message.what;
                if (i != 2) {
                    int i2 = 0;
                    if (i == 3) {
                        gVar.c();
                        gVar.o.a();
                        boolean unused = gVar.l = false;
                        boolean unused2 = gVar.m = false;
                    } else if (i == 4) {
                        gVar.g();
                        gVar.o.b();
                    } else if (i == 5 && gVar.f8172d != null && gVar.n) {
                        View b2 = gVar.f8172d;
                        if (!gVar.k || gVar.m) {
                            i2 = 8;
                        }
                        b2.setVisibility(i2);
                    }
                } else {
                    gVar.d();
                }
            }
        }
    }

    private class b extends a {
        public b(Context context) {
            super(context);
            this.f8155c.addAction(Constants.System.ACTION_SCREEN_OFF);
            this.f8155c.addAction(Constants.System.ACTION_USER_PRESENT);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.System.ACTION_SCREEN_OFF.equals(action)) {
                g.this.e();
            } else if (Constants.System.ACTION_USER_PRESENT.equals(action)) {
                g.this.f();
            }
        }
    }

    private g(Context context) {
        this.f8170b = context;
        this.f8171c = (WindowManager) context.getSystemService("window");
        this.o = new b(context);
        this.p = b();
        this.q = (DisplayManager) context.getSystemService("display");
        this.r = this.f8171c.getDefaultDisplay();
        this.s = new Point();
        this.r.getRealSize(this.s);
        Point point = this.s;
        this.h = Math.max(point.x, point.y);
        this.i = k.d(context);
        this.j = context.getResources().getDimensionPixelOffset(R.dimen.superpower_notification_padding_top);
    }

    public static g a(Context context) {
        if (f8169a == null) {
            synchronized (g.class) {
                if (f8169a == null) {
                    f8169a = new g(context);
                }
            }
        }
        return f8169a;
    }

    /* access modifiers changed from: private */
    public void a(Context context, boolean z) {
        Intent intent = new Intent();
        intent.setPackage("com.android.systemui");
        intent.setAction("com.android.systemui.fsgesture");
        intent.putExtra("typeFrom", "typefrom_status_bar_expansion");
        intent.putExtra("isEnter", z);
        intent.addFlags(67108864);
        context.sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public void c() {
        if (!this.n) {
            this.g = new WindowManager.LayoutParams(-1, this.i, 0, 0, 2014, 8651336, -3);
            WindowManager.LayoutParams layoutParams = this.g;
            layoutParams.gravity = 48;
            layoutParams.packageName = "SuperPowerStatusbar";
            if (Build.VERSION.SDK_INT > 27) {
                try {
                    e.a((Object) this.g, "layoutInDisplayCutoutMode", (Object) Integer.valueOf(((Integer) e.a((Class<?>) WindowManager.LayoutParams.class, "LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES")).intValue()));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            this.f8172d = View.inflate(this.f8170b, R.layout.superpower_statusbar, (ViewGroup) null);
            int i2 = 0;
            if (Build.VERSION.SDK_INT >= 29) {
                d(false);
            }
            this.f = (NotificationPanelLayout) this.f8172d.findViewById(R.id.window_notification_panel);
            this.e = this.f8172d.findViewById(R.id.window_notification_content);
            View findViewById = this.f8172d.findViewById(R.id.touch_space);
            ViewGroup.LayoutParams layoutParams2 = findViewById.getLayoutParams();
            layoutParams2.height = this.i;
            findViewById.setLayoutParams(layoutParams2);
            this.f8171c.addView(this.f8172d, this.g);
            View view = this.f8172d;
            if (!this.k) {
                i2 = 8;
            }
            view.setVisibility(i2);
            this.f.setPanelHeight(this.i);
            this.f.a((NotificationPanelLayout.d) new d(this));
            this.q.registerDisplayListener(this.t, this.p);
            this.n = true;
        }
    }

    /* access modifiers changed from: private */
    public void c(boolean z) {
        WindowManager.LayoutParams layoutParams;
        int i2;
        if (this.l != z) {
            this.l = z;
            this.g.height = z ? this.h : this.i;
            if (z) {
                layoutParams = this.g;
                i2 = layoutParams.flags & -9;
            } else {
                layoutParams = this.g;
                i2 = layoutParams.flags | 8;
            }
            layoutParams.flags = i2;
            this.f8171c.updateViewLayout(this.f8172d, this.g);
            if (k.l(this.f8170b)) {
                b().removeCallbacks(this.v);
                b().postDelayed(this.v, 10);
            }
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        NotificationPanelLayout notificationPanelLayout = this.f;
        if (notificationPanelLayout != null) {
            notificationPanelLayout.a();
        }
    }

    private void d(boolean z) {
        try {
            this.f8172d.getClass().getMethod("setForceDarkAllowed", new Class[]{Boolean.TYPE}).invoke(this.f8172d, new Object[]{Boolean.valueOf(z)});
        } catch (Exception e2) {
            Log.e("SuperPowerSaveManager", "reflect error when setForceDark", e2);
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        if (!this.m) {
            this.m = true;
            c(false);
            this.f.setPanelState(NotificationPanelLayout.e.COLLAPSED);
            this.f8172d.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        int i2 = 0;
        this.m = false;
        View view = this.f8172d;
        if (!this.k) {
            i2 = 8;
        }
        view.setVisibility(i2);
    }

    /* access modifiers changed from: private */
    public void g() {
        View view = this.f8172d;
        if (view != null && this.n) {
            this.n = false;
            this.f8171c.removeView(view);
            this.q.unregisterDisplayListener(this.t);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void h() {
        /*
            r11 = this;
            android.view.View r0 = r11.e
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int r1 = r11.j
            int r0 = r0.getPaddingBottom()
            android.view.Display r2 = r11.r
            int r2 = r2.getRotation()
            r3 = 3
            r4 = 1
            r5 = 0
            if (r2 == r4) goto L_0x002c
            if (r2 != r3) goto L_0x0019
            goto L_0x002c
        L_0x0019:
            android.view.WindowManager$LayoutParams r6 = r11.g
            android.graphics.Point r7 = r11.s
            int r8 = r7.x
            int r7 = r7.y
            int r7 = java.lang.Math.min(r8, r7)
            r6.width = r7
            r7 = r1
            r1 = r5
            r6 = r1
            r8 = r6
            goto L_0x004d
        L_0x002c:
            android.graphics.Point r1 = r11.s
            int r6 = r1.x
            int r1 = r1.y
            int r6 = r6 - r1
            int r1 = java.lang.Math.abs(r6)
            int r1 = r1 / r3
            int r6 = r11.j
            int r6 = r6 / 2
            android.view.WindowManager$LayoutParams r7 = r11.g
            android.graphics.Point r8 = r11.s
            int r9 = r8.x
            int r8 = r8.y
            int r8 = java.lang.Math.max(r9, r8)
            r7.width = r8
            r8 = r4
            r7 = r6
            r6 = r1
        L_0x004d:
            android.content.Context r9 = r11.f8170b
            boolean r9 = com.miui.superpower.b.k.i(r9)
            if (r9 == 0) goto L_0x00a2
            android.content.Context r9 = r11.f8170b
            boolean r9 = com.miui.superpower.b.k.l(r9)
            if (r9 != 0) goto L_0x00a2
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 27
            if (r9 <= r10) goto L_0x0079
            if (r2 != r3) goto L_0x006e
            android.view.WindowManager$LayoutParams r2 = r11.g
            android.content.Context r3 = r11.f8170b
            int r3 = com.miui.superpower.b.k.c(r3)
            goto L_0x008a
        L_0x006e:
            if (r2 != r4) goto L_0x00a2
            android.view.WindowManager$LayoutParams r2 = r11.g
            android.content.Context r3 = r11.f8170b
            int r3 = com.miui.superpower.b.k.c(r3)
            goto L_0x008b
        L_0x0079:
            if (r2 != r3) goto L_0x0090
            android.view.WindowManager$LayoutParams r2 = r11.g
            android.content.Context r3 = r11.f8170b
            int r3 = com.miui.superpower.b.k.c(r3)
            android.content.Context r4 = r11.f8170b
            int r4 = com.miui.superpower.b.k.d(r4)
            int r3 = r3 - r4
        L_0x008a:
            int r3 = -r3
        L_0x008b:
            int r3 = r3 / 2
            r2.x = r3
            goto L_0x00a6
        L_0x0090:
            if (r2 != r4) goto L_0x00a2
            android.view.WindowManager$LayoutParams r2 = r11.g
            android.content.Context r3 = r11.f8170b
            int r3 = com.miui.superpower.b.k.c(r3)
            android.content.Context r4 = r11.f8170b
            int r4 = com.miui.superpower.b.k.d(r4)
            int r3 = r3 - r4
            goto L_0x008b
        L_0x00a2:
            android.view.WindowManager$LayoutParams r2 = r11.g
            r2.x = r5
        L_0x00a6:
            android.view.WindowManager r2 = r11.f8171c
            android.view.View r3 = r11.f8172d
            android.view.WindowManager$LayoutParams r4 = r11.g
            r2.updateViewLayout(r3, r4)
            if (r8 == 0) goto L_0x00b5
            int r0 = r11.j
            int r0 = r0 / 2
        L_0x00b5:
            android.view.View r2 = r11.e
            r2.setPadding(r1, r7, r6, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.superpower.statusbar.g.h():void");
    }

    public void a() {
        this.p.sendEmptyMessage(4);
    }

    public void a(boolean z) {
        this.k = z;
        this.p.sendEmptyMessage(3);
    }

    public a b() {
        return a.b(this);
    }

    public void b(boolean z) {
        this.p.removeMessages(5);
        this.k = z;
        this.p.sendEmptyMessage(5);
    }
}
