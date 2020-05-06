package com.miui.superpower.statusbar.button;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.h;

public class VibrateButton extends a {
    /* access modifiers changed from: private */
    public AudioManager h;
    private a i;

    private class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.media.RINGER_MODE_CHANGED");
        }

        public void onReceive(Context context, Intent intent) {
            if (VibrateButton.this.h != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && action.equals("android.media.RINGER_MODE_CHANGED")) {
                    VibrateButton.this.b();
                }
            }
        }
    }

    public VibrateButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public VibrateButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VibrateButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.h = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.i = new a(context);
    }

    private boolean c() {
        return this.h != null && Settings.System.getInt(this.f8160b.getContentResolver(), "vibrate_in_silent", 0) == 1;
    }

    public void b() {
        setChecked(c());
    }

    public Drawable getEnableDrawableImpl() {
        return h.b(this.f8160b, "ic_qs_vibrate_on", R.drawable.ic_qs_vibrate_on);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.i.a();
        super.onAttachedToWindow();
    }

    public void onClick(View view) {
        if (this.h != null) {
            toggle();
            int i2 = 1;
            boolean z = !c();
            if (Settings.Global.getInt(this.f8160b.getContentResolver(), "mode_ringer", 0) != 2) {
                if (z) {
                    this.h.setRingerMode(1);
                } else {
                    this.h.setRingerMode(0);
                }
            }
            ContentResolver contentResolver = this.f8160b.getContentResolver();
            if (!z) {
                i2 = 0;
            }
            Settings.System.putInt(contentResolver, "vibrate_in_silent", i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.i.b();
        super.onDetachedFromWindow();
    }
}
