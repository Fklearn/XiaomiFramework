package com.miui.gamebooster.gbservices;

import android.accessibilityservice.AccessibilityService;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.customview.C0339h;
import com.miui.gamebooster.customview.InCallNotificationView;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;
import com.miui.gamebooster.service.NotificationListener;
import com.miui.gamebooster.service.NotificationListenerCallback;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.utils.SettingsUtil;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class AntiMsgAccessibilityService extends AccessibilityService {

    /* renamed from: a  reason: collision with root package name */
    private static final ArrayList<String> f4311a = new C0359b();

    /* renamed from: b  reason: collision with root package name */
    private InCallNotificationView f4312b;

    /* renamed from: c  reason: collision with root package name */
    private FrameLayout f4313c;

    /* renamed from: d  reason: collision with root package name */
    private C0339h f4314d;
    private Intent e;
    private Intent f;
    private String g;
    private int h;
    private boolean i = false;
    private boolean j = false;
    /* access modifiers changed from: private */
    public boolean k = false;
    private WindowManager l;
    /* access modifiers changed from: private */
    public Handler m;
    /* access modifiers changed from: private */
    public ISecurityCenterNotificationListener n;
    private Map<String, String> o = new C0360c(this);
    private BroadcastReceiver p = new C0363f(this);
    private final int q = 1;
    private final int r = 2;
    private final int s = 3;
    public final int t = 200;
    ObjectAnimator u = new ObjectAnimator();
    /* access modifiers changed from: private */
    public NotificationListenerCallback v = new C0367j(this);
    private ServiceConnection w = new C0368k(this);

    private void a(int i2) {
        ObjectAnimator objectAnimator;
        long j2 = 200;
        if (i2 == 1) {
            C0339h hVar = this.f4314d;
            this.u = ObjectAnimator.ofFloat(hVar, "translationY", new float[]{(float) (-hVar.getHeight()), 0.0f});
        } else if (i2 != 2) {
            if (i2 == 3) {
                this.u = ObjectAnimator.ofFloat(this.f4314d, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
                this.u.addListener(new C0365h(this));
                objectAnimator = this.u;
                j2 = 100;
                objectAnimator.setDuration(j2);
            }
            this.u.start();
        } else {
            C0339h hVar2 = this.f4314d;
            this.u = ObjectAnimator.ofFloat(hVar2, "translationY", new float[]{0.0f, (float) (-hVar2.getHeight())});
            this.u.addListener(new C0364g(this));
        }
        objectAnimator = this.u;
        objectAnimator.setDuration(j2);
        this.u.start();
    }

    /* access modifiers changed from: private */
    public void a(Notification notification) {
        if (notification != null) {
            CharSequence charSequence = null;
            Bundle bundle = notification.extras;
            if (bundle != null) {
                charSequence = bundle.getCharSequence("android.text");
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.tickerText;
            }
            Iterator<String> it = f4311a.iterator();
            while (it.hasNext()) {
                String next = it.next();
                if (charSequence != null && charSequence.toString().contains(next)) {
                    a(true);
                }
            }
        }
    }

    private void a(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getParcelableData() != null) {
            a((Notification) accessibilityEvent.getParcelableData());
        }
    }

    private boolean a(Intent intent, String str) {
        String className = intent.getComponent().getClassName();
        if (className == null) {
            return false;
        }
        if ("com.tencent.av.ui.VChatActivity".equals(className)) {
            this.f = intent;
            return false;
        }
        String str2 = this.o.get(className);
        if (str2 == null) {
            if (AppConstants.Package.PACKAGE_NAME_QQ.equals(str)) {
                str2 = "QQ电话";
            } else if (AppConstants.Package.PACKAGE_NAME_MM.equals(str)) {
                str2 = "微信电话";
            }
        }
        this.f4312b.a(str2, (String) null);
        return true;
    }

    private void b() {
        this.f4314d.removeAllViews();
    }

    /* access modifiers changed from: private */
    public void b(Intent intent) {
        this.g = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        this.e = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
        this.h = intent.getIntExtra("originating_uid", -1);
        if (this.f4312b == null) {
            this.f4312b = (InCallNotificationView) LayoutInflater.from(this).inflate(R.layout.gb_notification_float_incall, (ViewGroup) null);
            this.f4312b.a(this);
        }
        if (a(this.e, this.g)) {
            this.f4312b.b();
            f();
            b();
            this.f4314d.addView(this.f4312b);
            this.f4313c.setVisibility(0);
            this.f4314d.setBackgroundResource(R.drawable.float_incall_notification_bg);
            a(1);
            this.j = true;
            b.b("gb_show_window", true);
        }
    }

    private boolean c() {
        FrameLayout frameLayout = this.f4313c;
        return frameLayout != null && frameLayout.getVisibility() == 0;
    }

    private int d() {
        return getResources().getDimensionPixelSize(R.dimen.float_notification_panel_width);
    }

    /* access modifiers changed from: private */
    public void e() {
        FrameLayout frameLayout = this.f4313c;
        if (frameLayout != null) {
            frameLayout.setVisibility(8);
        }
        b();
        InCallNotificationView inCallNotificationView = this.f4312b;
        if (inCallNotificationView != null) {
            inCallNotificationView.a();
        }
    }

    /* JADX WARNING: type inference failed for: r1v5, types: [com.miui.gamebooster.customview.h, android.view.View] */
    private void f() {
        if (this.f4313c == null) {
            this.f4313c = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.gb_status_bar_float_notification_container, (ViewGroup) null);
            this.l.addView(this.f4313c, a(new ViewGroup.LayoutParams(d(), -2)));
            this.f4314d = new C0339h(this);
            this.f4314d.a(this);
            this.f4314d.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            this.f4314d.setGravity(17);
            this.f4313c.addView(this.f4314d);
        }
    }

    /* access modifiers changed from: protected */
    public WindowManager.LayoutParams a(ViewGroup.LayoutParams layoutParams) {
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(layoutParams.width, layoutParams.height, 2014, 8389128, -2);
        layoutParams2.flags |= 16777216;
        layoutParams2.gravity = 49;
        layoutParams2.setTitle("FloatNotificationPanel");
        return layoutParams2;
    }

    public void a() {
        a(this.f);
        a(this.e);
        this.f = null;
    }

    public void a(Intent intent) {
        if (intent != null) {
            try {
                Bundle bundle = ActivityOptions.makeCustomAnimation(this, R.anim.activity_open_enter, R.anim.activity_open_exit).toBundle();
                if (this.h != -1) {
                    e.a((Class<? extends Object>) ContextWrapper.class, (Object) this, "startActivityAsUser", (Class<?>[]) new Class[]{Intent.class, Bundle.class, UserHandle.class}, intent, bundle, B.e(this.h));
                    return;
                }
                intent.addFlags(268435456);
                getApplication().startActivity(intent, bundle);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void a(boolean z) {
        if (c()) {
            if (z) {
                a(2);
            } else {
                e();
            }
            this.j = false;
            b.b("gb_show_window", false);
        }
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        CharSequence packageName = accessibilityEvent.getPackageName();
        if (!TextUtils.isEmpty(packageName)) {
            if (packageName.equals(AppConstants.Package.PACKAGE_NAME_QQ)) {
                if (accessibilityEvent.getEventType() != 64) {
                    return;
                }
            } else if (!packageName.equals(AppConstants.Package.PACKAGE_NAME_MM) || accessibilityEvent.getEventType() != 64) {
                return;
            }
            a(accessibilityEvent);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (!this.i) {
            this.i = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("miui.intent.action.gb_show_window");
            intentFilter.addAction("action_toast_booster_success");
            intentFilter.addAction("action_toast_wonderful_moment");
            intentFilter.addAction("action_toast_booster_fail");
            registerReceiver(this.p, intentFilter, "com.miui.securitycenter.permission.GB_SHOW_WINDOW", (Handler) null);
        }
        this.l = (WindowManager) getSystemService("window");
        this.m = new Handler(Looper.myLooper());
        SettingsUtil.enableNotificationListener(this, NotificationListener.class);
        SettingsUtil.enableAccessibility(this, NotificationListener.class);
        g.a((Context) this, new Intent(this, NotificationListener.class), this.w, 1, B.k());
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.i) {
            this.i = false;
            unregisterReceiver(this.p);
        }
        FrameLayout frameLayout = this.f4313c;
        if (frameLayout != null) {
            this.l.removeView(frameLayout);
        }
        if (this.j) {
            a();
        }
        b.b("gb_show_window", false);
        this.j = false;
        try {
            this.n.a(this.v);
        } catch (Exception e2) {
            Log.e("AntiMsgAccessibilityService", "mNoticationListenerBinder:" + e2);
        }
        SettingsUtil.closeNotificationListener(this, NotificationListener.class);
        SettingsUtil.closeAccessibility(this, NotificationListener.class);
        unbindService(this.w);
    }

    public void onInterrupt() {
    }
}
