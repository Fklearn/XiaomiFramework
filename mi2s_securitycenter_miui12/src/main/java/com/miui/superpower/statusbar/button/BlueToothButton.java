package com.miui.superpower.statusbar.button;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.h;

public class BlueToothButton extends a {
    private BluetoothAdapter h;
    private a i;

    public class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
                if (intExtra == 10) {
                    BlueToothButton.this.setChecked(false);
                } else if (intExtra == 12) {
                    BlueToothButton.this.setChecked(true);
                }
            }
        }
    }

    public BlueToothButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public BlueToothButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BlueToothButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.h = BluetoothAdapter.getDefaultAdapter();
        this.i = new a(context);
    }

    private boolean c() {
        BluetoothAdapter bluetoothAdapter = this.h;
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    public void b() {
        setChecked(c());
    }

    public Drawable getEnableDrawableImpl() {
        return h.b(this.f8160b, "ic_qs_bluetooth_on", R.drawable.ic_qs_bluetooth_on);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.i.a();
    }

    public void onClick(View view) {
        boolean z;
        toggle();
        if (c()) {
            BluetoothAdapter bluetoothAdapter = this.h;
            if (bluetoothAdapter != null && bluetoothAdapter.disable()) {
                z = false;
            } else {
                return;
            }
        } else {
            BluetoothAdapter bluetoothAdapter2 = this.h;
            if (bluetoothAdapter2 != null && bluetoothAdapter2.enable()) {
                z = true;
            } else {
                return;
            }
        }
        setChecked(z);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.i.b();
    }
}
