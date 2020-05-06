package com.miui.gamebooster.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.miui.gamebooster.gbservices.AntiMsgAccessibilityService;
import miui.notification.Gefingerpoken;
import miui.notification.NotificationRowLayout;

/* renamed from: com.miui.gamebooster.customview.h  reason: case insensitive filesystem */
public class C0339h extends NotificationRowLayout {

    /* renamed from: a  reason: collision with root package name */
    private a f4197a;

    /* renamed from: b  reason: collision with root package name */
    private AntiMsgAccessibilityService f4198b;

    /* renamed from: c  reason: collision with root package name */
    private int f4199c;

    /* renamed from: d  reason: collision with root package name */
    private int f4200d;
    private int e;

    /* renamed from: com.miui.gamebooster.customview.h$a */
    private class a implements Gefingerpoken {

        /* renamed from: a  reason: collision with root package name */
        private final float f4201a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4202b;

        /* renamed from: c  reason: collision with root package name */
        private float f4203c;

        /* renamed from: d  reason: collision with root package name */
        private float f4204d;

        public a(float f) {
            this.f4201a = f;
        }

        private void a(MotionEvent motionEvent) {
            if (!this.f4202b) {
                float abs = Math.abs(motionEvent.getX() - this.f4204d);
                float y = this.f4203c - motionEvent.getY();
                if (abs < y && y > this.f4201a) {
                    this.f4202b = true;
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:7:0x000e, code lost:
            if (r0 != 3) goto L_0x0023;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onInterceptTouchEvent(android.view.MotionEvent r4) {
            /*
                r3 = this;
                int r0 = r4.getActionMasked()
                r1 = 0
                if (r0 == 0) goto L_0x0015
                r2 = 1
                if (r0 == r2) goto L_0x0021
                r2 = 2
                if (r0 == r2) goto L_0x0011
                r4 = 3
                if (r0 == r4) goto L_0x0021
                goto L_0x0023
            L_0x0011:
                r3.a(r4)
                goto L_0x0023
            L_0x0015:
                float r0 = r4.getX()
                r3.f4204d = r0
                float r4 = r4.getY()
                r3.f4203c = r4
            L_0x0021:
                r3.f4202b = r1
            L_0x0023:
                boolean r4 = r3.f4202b
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.C0339h.a.onInterceptTouchEvent(android.view.MotionEvent):boolean");
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == 0) {
                return true;
            }
            if (motionEvent.getActionMasked() == 2) {
                a(motionEvent);
            }
            return this.f4202b;
        }
    }

    public C0339h(Context context) {
        this(context, (AttributeSet) null);
    }

    public C0339h(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        setLayoutTransitionsEnabled(false);
        this.f4197a = new a((float) ViewConfiguration.get(getContext()).getScaledTouchSlop());
        this.f4199c = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void a(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4198b = antiMsgAccessibilityService;
    }

    public boolean canChildBeDismissed(View view) {
        return C0339h.super.canChildBeDismissed(view);
    }

    public void onChildDismissed(View view) {
        C0339h.super.onChildDismissed(view);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.f4197a.onInterceptTouchEvent(motionEvent) || C0339h.super.onInterceptTouchEvent(motionEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
        if (r0 != 3) goto L_0x0037;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0030
            if (r0 == r2) goto L_0x0020
            r3 = 2
            if (r0 == r3) goto L_0x0011
            r3 = 3
            if (r0 == r3) goto L_0x0020
            goto L_0x0037
        L_0x0011:
            float r0 = r5.getY()
            int r0 = (int) r0
            int r3 = r4.f4200d
            int r0 = r0 - r3
            int r0 = java.lang.Math.abs(r0)
            r4.e = r0
            goto L_0x0037
        L_0x0020:
            int r0 = r4.e
            int r3 = r4.f4199c
            if (r0 <= r3) goto L_0x002d
            com.miui.gamebooster.gbservices.AntiMsgAccessibilityService r0 = r4.f4198b
            if (r0 == 0) goto L_0x002d
            r0.a((boolean) r2)
        L_0x002d:
            r4.e = r1
            goto L_0x0037
        L_0x0030:
            float r0 = r5.getY()
            int r0 = (int) r0
            r4.f4200d = r0
        L_0x0037:
            com.miui.gamebooster.customview.h$a r0 = r4.f4197a
            boolean r0 = r0.onTouchEvent(r5)
            if (r0 != 0) goto L_0x0045
            boolean r5 = com.miui.gamebooster.customview.C0339h.super.onTouchEvent(r5)
            if (r5 == 0) goto L_0x0046
        L_0x0045:
            r1 = r2
        L_0x0046:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.C0339h.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
