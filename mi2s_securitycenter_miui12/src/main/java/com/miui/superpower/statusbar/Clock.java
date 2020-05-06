package com.miui.superpower.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;
import com.miui.securitycenter.i;
import java.util.TimeZone;
import miui.date.Calendar;

public class Clock extends TextView {

    /* renamed from: a  reason: collision with root package name */
    private a f8141a;

    /* renamed from: b  reason: collision with root package name */
    private Calendar f8142b;

    /* renamed from: c  reason: collision with root package name */
    private CharSequence f8143c;

    /* renamed from: d  reason: collision with root package name */
    private CharSequence f8144d;

    private static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Clock f8145a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public boolean f8146b;

        /* renamed from: c  reason: collision with root package name */
        private final BroadcastReceiver f8147c;

        private a() {
            this.f8147c = new c(this);
        }

        private void a(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_TICK");
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            context.registerReceiver(this.f8147c, intentFilter, (String) null, (Handler) null);
        }

        /* access modifiers changed from: private */
        public void a(Clock clock) {
            boolean z = this.f8145a == null;
            this.f8145a = clock;
            if (z) {
                a(clock.getContext().getApplicationContext());
            }
            clock.a();
        }

        /* access modifiers changed from: private */
        public void a(boolean z) {
            this.f8146b = z;
        }

        /* access modifiers changed from: private */
        public boolean a() {
            return this.f8146b;
        }

        private void b(Context context) {
            context.unregisterReceiver(this.f8147c);
        }

        /* access modifiers changed from: private */
        public void b(Clock clock) {
            this.f8145a = null;
            b(clock.getContext().getApplicationContext());
        }
    }

    public Clock(Context context) {
        this(context, (AttributeSet) null);
    }

    public Clock(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Clock(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.Clock);
            this.f8143c = obtainStyledAttributes.getText(0);
            this.f8144d = obtainStyledAttributes.getText(1);
            obtainStyledAttributes.recycle();
        }
    }

    /* access modifiers changed from: package-private */
    public final void a() {
        if (this.f8142b == null) {
            this.f8142b = new Calendar();
        }
        this.f8142b.setTimeZone(TimeZone.getDefault());
        this.f8142b.setTimeInMillis(System.currentTimeMillis());
        setText(this.f8142b.format((this.f8141a.a() ? this.f8144d : this.f8143c).toString()));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.f8141a == null) {
            this.f8141a = new a();
        }
        this.f8141a.a(DateFormat.is24HourFormat(getContext()));
        this.f8141a.a(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        a aVar = this.f8141a;
        if (aVar != null) {
            aVar.b(this);
        }
    }
}
