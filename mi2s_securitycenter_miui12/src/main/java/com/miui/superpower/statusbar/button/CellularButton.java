package com.miui.superpower.statusbar.button;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.miui.superpower.statusbar.h;

public class CellularButton extends a {
    private b.b.o.f.c.a h;
    /* access modifiers changed from: private */
    public TelephonyManager i;
    /* access modifiers changed from: private */
    public ContentResolver j;
    private a k;
    /* access modifiers changed from: private */
    public boolean l;
    /* access modifiers changed from: private */
    public boolean m;
    private final ContentObserver n;

    public class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction(Constants.System.ACTION_SIM_STATE_CHANGED);
            this.f8155c.addAction("android.intent.action.AIRPLANE_MODE");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constants.System.ACTION_SIM_STATE_CHANGED)) {
                    if (CellularButton.this.i.getSimState() != 5) {
                        boolean unused = CellularButton.this.m = false;
                        CellularButton.this.e();
                        CellularButton.this.setChecked(false);
                        return;
                    }
                    boolean unused2 = CellularButton.this.m = true;
                } else if (action.equals("android.intent.action.AIRPLANE_MODE")) {
                    CellularButton cellularButton = CellularButton.this;
                    boolean unused3 = cellularButton.l = k.a(cellularButton.j);
                } else {
                    return;
                }
                CellularButton.this.e();
            }
        }
    }

    public CellularButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public CellularButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CellularButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.l = false;
        this.m = false;
        this.n = new b(this, this.g);
        this.i = (TelephonyManager) context.getSystemService("phone");
        this.h = b.b.o.f.c.a.a(context);
        this.j = context.getContentResolver();
        this.k = new a(context);
    }

    /* access modifiers changed from: private */
    public void e() {
        setEnabled(!this.l && this.m);
    }

    private boolean getSimStatus() {
        return this.i.getSimState() == 5;
    }

    public void b() {
        if (isEnabled()) {
            if (!c()) {
                setEnabled(false);
                setBackground(this.f8162d);
                setImageDrawable(this.f);
                return;
            }
            setChecked(d());
        }
    }

    public boolean c() {
        TelephonyManager telephonyManager = this.i;
        return telephonyManager != null && telephonyManager.getSimState() == 5;
    }

    public boolean d() {
        b.b.o.f.c.a aVar = this.h;
        if (aVar == null) {
            return false;
        }
        return aVar.a();
    }

    public Drawable getDisableDrawableImpl() {
        return h.b(this.f8160b, "ic_qs_data_off", R.drawable.ic_qs_data_off);
    }

    public Drawable getEnableDrawableImpl() {
        return h.b(this.f8160b, "ic_qs_data_on", R.drawable.ic_qs_data_on);
    }

    public boolean isEnabled() {
        return super.isEnabled() && !this.l && this.m;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.l = k.a(this.j);
        this.m = getSimStatus();
        this.k.a();
        e();
        this.j.registerContentObserver(Settings.Secure.getUriFor(Constants.System.MOBILE_POLICY), false, this.n);
        super.onAttachedToWindow();
    }

    public void onClick(View view) {
        if (isEnabled() && this.h != null) {
            toggle();
            this.h.a(!d());
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.k.b();
        this.j.unregisterContentObserver(this.n);
        super.onDetachedFromWindow();
    }
}
