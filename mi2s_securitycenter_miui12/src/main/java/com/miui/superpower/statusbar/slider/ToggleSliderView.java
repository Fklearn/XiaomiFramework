package com.miui.superpower.statusbar.slider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.miui.powercenter.utils.n;
import com.miui.securitycenter.R;

public class ToggleSliderView extends RelativeLayout implements b {

    /* renamed from: a  reason: collision with root package name */
    private ToggleSeekBar f8219a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f8220b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public boolean f8221c;

    /* renamed from: d  reason: collision with root package name */
    private a f8222d;
    private SeekBar.OnSeekBarChangeListener e;

    private class a extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private final Uri f8223a;

        /* renamed from: b  reason: collision with root package name */
        private final Uri f8224b;

        private a(Handler handler) {
            super(handler);
            this.f8223a = Settings.System.getUriFor("screen_brightness_mode");
            this.f8224b = Settings.System.getUriFor("screen_brightness");
        }

        /* synthetic */ a(ToggleSliderView toggleSliderView, Handler handler, c cVar) {
            this(handler);
        }

        /* access modifiers changed from: private */
        public void a() {
            ContentResolver contentResolver = ToggleSliderView.this.getContext().getContentResolver();
            contentResolver.unregisterContentObserver(this);
            contentResolver.registerContentObserver(this.f8223a, false, this);
            contentResolver.registerContentObserver(this.f8224b, false, this);
        }

        /* access modifiers changed from: private */
        public void b() {
            ToggleSliderView.this.getContext().getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean z) {
            onChange(z, (Uri) null);
        }

        public void onChange(boolean z, Uri uri) {
            if (!z) {
                ToggleSliderView.this.a();
            }
        }
    }

    public ToggleSliderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ToggleSliderView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ToggleSliderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.e = new c(this);
        View.inflate(context, R.layout.superpower_statusbar_brightness_slider, this);
        this.f8219a = (ToggleSeekBar) findViewById(R.id.slider);
        setMax(n.a(context).e() - 1);
        a();
        this.f8219a.setOnSeekBarChangeListener(this.e);
        this.f8222d = new a(this, new Handler(), (c) null);
    }

    /* access modifiers changed from: private */
    public void a() {
        setValue(Settings.System.getInt(getContext().getContentResolver(), "screen_brightness", 100));
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.f8220b = motionEvent.getActionMasked();
        if (motionEvent.getActionMasked() == 0) {
            this.f8221c = false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public int getValue() {
        return this.f8219a.getProgress();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.f8222d.a();
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.f8222d.b();
        super.onDetachedFromWindow();
    }

    public void setMax(int i) {
        if (i != this.f8219a.getMax()) {
            this.f8219a.setMax(i);
        }
    }

    public void setValue(int i) {
        this.f8219a.setProgress(i);
    }
}
