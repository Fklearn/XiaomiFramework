package com.miui.superpower.statusbar.icon;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;
import com.miui.superpower.b.k;
import com.miui.superpower.statusbar.h;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;

public class SimSignalView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private TelephonyManager f8186a;

    /* renamed from: b  reason: collision with root package name */
    private a f8187b;

    /* renamed from: c  reason: collision with root package name */
    private b f8188c;

    /* renamed from: d  reason: collision with root package name */
    private ContentResolver f8189d;
    /* access modifiers changed from: private */
    public ServiceState e;
    private int f;
    private Drawable[] g;

    public class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction(Constants.System.ACTION_SIM_STATE_CHANGED);
            this.f8155c.addAction("android.intent.action.SERVICE_STATE");
            this.f8155c.addAction("android.intent.action.AIRPLANE_MODE");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constants.System.ACTION_SIM_STATE_CHANGED) || action.equals("android.intent.action.AIRPLANE_MODE") || action.equals("android.intent.action.SERVICE_STATE")) {
                    SimSignalView.this.a();
                }
            }
        }
    }

    private class b extends PhoneStateListener {

        /* renamed from: a  reason: collision with root package name */
        private int f8191a;

        private b(int i) {
            this.f8191a = 0;
            try {
                e.a((Object) this, (Class<?>) PhoneStateListener.class, "mSubId", (Object) Integer.valueOf(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            ServiceState unused = SimSignalView.this.e = serviceState;
            SimSignalView.this.b(this.f8191a);
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int i = Build.VERSION.SDK_INT;
            if (i < 23) {
                try {
                    this.f8191a = ((Integer) e.a((Object) signalStrength, "getLevel", (Class<?>[]) null, (Object[]) null)).intValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (i > 28) {
                    try {
                        this.f8191a = ((Integer) e.a((Object) signalStrength, "getMiuiLevel", (Class<?>[]) null, (Object[]) null)).intValue();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                this.f8191a = signalStrength.getLevel();
            }
            SimSignalView.this.b(this.f8191a);
        }
    }

    public SimSignalView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SimSignalView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SimSignalView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f = 0;
        this.g = new Drawable[6];
        a(context, attributeSet);
    }

    private SubscriptionInfo a(int i) {
        return SubscriptionManager.getDefault().getSubscriptionInfoForSlot(i);
    }

    /* access modifiers changed from: private */
    public void a() {
        SubscriptionInfo a2 = a(this.f);
        boolean a3 = k.a(this.f8189d);
        if (a2 == null || a3 || !a2.isActivated()) {
            b();
        } else {
            a(a2);
        }
    }

    @TargetApi(22)
    private void a(Context context, AttributeSet attributeSet) {
        if (Build.VERSION.SDK_INT < 22) {
            setVisibility(8);
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.SimSignalView);
        this.f = obtainStyledAttributes.getInt(0, this.f);
        obtainStyledAttributes.recycle();
        this.g[0] = h.b(context, "stat_sys_signal_0", R.drawable.superpower_stat_sys_signal_0);
        this.g[1] = h.b(context, "stat_sys_signal_1", R.drawable.superpower_stat_sys_signal_1);
        this.g[2] = h.b(context, "stat_sys_signal_2", R.drawable.superpower_stat_sys_signal_2);
        this.g[3] = h.b(context, "stat_sys_signal_3", R.drawable.superpower_stat_sys_signal_3);
        this.g[4] = h.b(context, "stat_sys_signal_4", R.drawable.superpower_stat_sys_signal_4);
        this.g[5] = h.b(context, "stat_sys_signal_5", R.drawable.superpower_stat_sys_signal_5);
        this.f8186a = (TelephonyManager) context.getSystemService("phone");
        this.f8187b = new a(context);
        this.f8189d = context.getContentResolver();
        a();
    }

    @TargetApi(22)
    private void a(SubscriptionInfo subscriptionInfo) {
        int i;
        if (this.f8186a == null) {
            i = 8;
        } else {
            if (this.f8188c == null) {
                this.f8188c = new b(subscriptionInfo.getSubscriptionId());
                this.f8186a.listen(this.f8188c, 257);
            }
            i = 0;
        }
        setVisibility(i);
    }

    @TargetApi(22)
    private void b() {
        TelephonyManager telephonyManager;
        b bVar = this.f8188c;
        if (!(bVar == null || (telephonyManager = this.f8186a) == null)) {
            telephonyManager.listen(bVar, 0);
            this.f8188c = null;
        }
        setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void b(int i) {
        if (i >= 0 && i <= this.g.length - 1) {
            if (this.e.getState() != 0) {
                i = 0;
            }
            setImageDrawable(this.g[i]);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        a aVar = this.f8187b;
        if (aVar != null) {
            aVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        a aVar = this.f8187b;
        if (aVar != null) {
            aVar.b();
        }
    }
}
