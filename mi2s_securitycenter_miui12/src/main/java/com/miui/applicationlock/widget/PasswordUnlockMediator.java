package com.miui.applicationlock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.miui.securitycenter.i;

public class PasswordUnlockMediator extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private C0308a f3420a;

    /* renamed from: b  reason: collision with root package name */
    private Context f3421b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f3422c;

    public PasswordUnlockMediator(Context context) {
        this(context, (AttributeSet) null);
    }

    public PasswordUnlockMediator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PasswordUnlockMediator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f3421b = context;
        a(context, attributeSet);
    }

    private void a(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.PasswordUnlockMediatorAttrs);
        this.f3422c = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(java.lang.String r4) {
        /*
            r3 = this;
            int r0 = r4.hashCode()
            r1 = -2000413939(0xffffffff88c41b0d, float:-1.18026805E-33)
            r2 = 1
            if (r0 == r1) goto L_0x0029
            r1 = -791090288(0xffffffffd0d8eb90, float:-2.91145318E10)
            if (r0 == r1) goto L_0x001f
            r1 = 103910395(0x6318bfb, float:3.339284E-35)
            if (r0 == r1) goto L_0x0015
            goto L_0x0033
        L_0x0015:
            java.lang.String r0 = "mixed"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0033
            r4 = r2
            goto L_0x0034
        L_0x001f:
            java.lang.String r0 = "pattern"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0033
            r4 = 2
            goto L_0x0034
        L_0x0029:
            java.lang.String r0 = "numeric"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0033
            r4 = 0
            goto L_0x0034
        L_0x0033:
            r4 = -1
        L_0x0034:
            if (r4 == 0) goto L_0x004e
            if (r4 == r2) goto L_0x0044
            com.miui.applicationlock.widget.A r4 = new com.miui.applicationlock.widget.A
            android.content.Context r0 = r3.f3421b
            boolean r1 = r3.f3422c
            r4.<init>(r0, r1)
        L_0x0041:
            r3.f3420a = r4
            goto L_0x0058
        L_0x0044:
            com.miui.applicationlock.widget.j r4 = new com.miui.applicationlock.widget.j
            android.content.Context r0 = r3.f3421b
            boolean r1 = r3.f3422c
            r4.<init>(r0, r1)
            goto L_0x0041
        L_0x004e:
            com.miui.applicationlock.widget.x r4 = new com.miui.applicationlock.widget.x
            android.content.Context r0 = r3.f3421b
            boolean r1 = r3.f3422c
            r4.<init>(r0, r1)
            goto L_0x0041
        L_0x0058:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.widget.PasswordUnlockMediator.b(java.lang.String):void");
    }

    public void a(String str) {
        b(str);
        this.f3420a.setVisibility(0);
        addView(this.f3420a, -1, -1);
    }

    public C0308a getUnlockView() {
        return this.f3420a;
    }
}
