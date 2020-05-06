package androidx.appcompat.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.a;
import androidx.core.widget.c;

/* renamed from: androidx.appcompat.widget.m  reason: case insensitive filesystem */
class C0110m {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private final CompoundButton f623a;

    /* renamed from: b  reason: collision with root package name */
    private ColorStateList f624b = null;

    /* renamed from: c  reason: collision with root package name */
    private PorterDuff.Mode f625c = null;

    /* renamed from: d  reason: collision with root package name */
    private boolean f626d = false;
    private boolean e = false;
    private boolean f;

    C0110m(@NonNull CompoundButton compoundButton) {
        this.f623a = compoundButton;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = androidx.core.widget.c.a(r2.f623a);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int a(int r3) {
        /*
            r2 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 17
            if (r0 >= r1) goto L_0x0013
            android.widget.CompoundButton r0 = r2.f623a
            android.graphics.drawable.Drawable r0 = androidx.core.widget.c.a(r0)
            if (r0 == 0) goto L_0x0013
            int r0 = r0.getIntrinsicWidth()
            int r3 = r3 + r0
        L_0x0013:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.C0110m.a(int):int");
    }

    /* access modifiers changed from: package-private */
    public void a() {
        Drawable a2 = c.a(this.f623a);
        if (a2 == null) {
            return;
        }
        if (this.f626d || this.e) {
            Drawable mutate = a.h(a2).mutate();
            if (this.f626d) {
                a.a(mutate, this.f624b);
            }
            if (this.e) {
                a.a(mutate, this.f625c);
            }
            if (mutate.isStateful()) {
                mutate.setState(this.f623a.getDrawableState());
            }
            this.f623a.setButtonDrawable(mutate);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(ColorStateList colorStateList) {
        this.f624b = colorStateList;
        this.f626d = true;
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable PorterDuff.Mode mode) {
        this.f625c = mode;
        this.e = true;
        a();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0049 A[SYNTHETIC, Splitter:B:14:0x0049] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0070 A[Catch:{ all -> 0x0098 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0083 A[Catch:{ all -> 0x0098 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(@androidx.annotation.Nullable android.util.AttributeSet r12, int r13) {
        /*
            r11 = this;
            android.widget.CompoundButton r0 = r11.f623a
            android.content.Context r0 = r0.getContext()
            int[] r1 = a.a.j.CompoundButton
            r2 = 0
            androidx.appcompat.widget.va r0 = androidx.appcompat.widget.va.a(r0, r12, r1, r13, r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 29
            if (r1 < r3) goto L_0x0025
            android.widget.CompoundButton r4 = r11.f623a
            android.content.Context r5 = r4.getContext()
            int[] r6 = a.a.j.CompoundButton
            android.content.res.TypedArray r8 = r0.a()
            r10 = 0
            r7 = r12
            r9 = r13
            r4.saveAttributeDataForStyleable(r5, r6, r7, r8, r9, r10)
        L_0x0025:
            int r12 = a.a.j.CompoundButton_buttonCompat     // Catch:{ all -> 0x0098 }
            boolean r12 = r0.g(r12)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x0046
            int r12 = a.a.j.CompoundButton_buttonCompat     // Catch:{ all -> 0x0098 }
            int r12 = r0.g(r12, r2)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x0046
            android.widget.CompoundButton r13 = r11.f623a     // Catch:{ NotFoundException -> 0x0046 }
            android.widget.CompoundButton r1 = r11.f623a     // Catch:{ NotFoundException -> 0x0046 }
            android.content.Context r1 = r1.getContext()     // Catch:{ NotFoundException -> 0x0046 }
            android.graphics.drawable.Drawable r12 = a.a.a.a.a.b(r1, r12)     // Catch:{ NotFoundException -> 0x0046 }
            r13.setButtonDrawable(r12)     // Catch:{ NotFoundException -> 0x0046 }
            r12 = 1
            goto L_0x0047
        L_0x0046:
            r12 = r2
        L_0x0047:
            if (r12 != 0) goto L_0x0068
            int r12 = a.a.j.CompoundButton_android_button     // Catch:{ all -> 0x0098 }
            boolean r12 = r0.g(r12)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x0068
            int r12 = a.a.j.CompoundButton_android_button     // Catch:{ all -> 0x0098 }
            int r12 = r0.g(r12, r2)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x0068
            android.widget.CompoundButton r13 = r11.f623a     // Catch:{ all -> 0x0098 }
            android.widget.CompoundButton r1 = r11.f623a     // Catch:{ all -> 0x0098 }
            android.content.Context r1 = r1.getContext()     // Catch:{ all -> 0x0098 }
            android.graphics.drawable.Drawable r12 = a.a.a.a.a.b(r1, r12)     // Catch:{ all -> 0x0098 }
            r13.setButtonDrawable(r12)     // Catch:{ all -> 0x0098 }
        L_0x0068:
            int r12 = a.a.j.CompoundButton_buttonTint     // Catch:{ all -> 0x0098 }
            boolean r12 = r0.g(r12)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x007b
            android.widget.CompoundButton r12 = r11.f623a     // Catch:{ all -> 0x0098 }
            int r13 = a.a.j.CompoundButton_buttonTint     // Catch:{ all -> 0x0098 }
            android.content.res.ColorStateList r13 = r0.a(r13)     // Catch:{ all -> 0x0098 }
            androidx.core.widget.c.a((android.widget.CompoundButton) r12, (android.content.res.ColorStateList) r13)     // Catch:{ all -> 0x0098 }
        L_0x007b:
            int r12 = a.a.j.CompoundButton_buttonTintMode     // Catch:{ all -> 0x0098 }
            boolean r12 = r0.g(r12)     // Catch:{ all -> 0x0098 }
            if (r12 == 0) goto L_0x0094
            android.widget.CompoundButton r12 = r11.f623a     // Catch:{ all -> 0x0098 }
            int r13 = a.a.j.CompoundButton_buttonTintMode     // Catch:{ all -> 0x0098 }
            r1 = -1
            int r13 = r0.d(r13, r1)     // Catch:{ all -> 0x0098 }
            r1 = 0
            android.graphics.PorterDuff$Mode r13 = androidx.appcompat.widget.N.a(r13, r1)     // Catch:{ all -> 0x0098 }
            androidx.core.widget.c.a((android.widget.CompoundButton) r12, (android.graphics.PorterDuff.Mode) r13)     // Catch:{ all -> 0x0098 }
        L_0x0094:
            r0.b()
            return
        L_0x0098:
            r12 = move-exception
            r0.b()
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.C0110m.a(android.util.AttributeSet, int):void");
    }

    /* access modifiers changed from: package-private */
    public ColorStateList b() {
        return this.f624b;
    }

    /* access modifiers changed from: package-private */
    public PorterDuff.Mode c() {
        return this.f625c;
    }

    /* access modifiers changed from: package-private */
    public void d() {
        if (this.f) {
            this.f = false;
            return;
        }
        this.f = true;
        a();
    }
}
