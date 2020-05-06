package com.miui.powercenter.batteryhistory;

import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;
import com.miui.powercenter.utils.i;

class T extends GestureDetector.SimpleOnGestureListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryLevelHistogram.b f6850a;

    T(BatteryLevelHistogram.b bVar) {
        this.f6850a = bVar;
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (this.f6850a.f && this.f6850a.e) {
            this.f6850a.f6823d.forceFinished(true);
            boolean unused = this.f6850a.e = false;
        }
        return true;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (!this.f6850a.f) {
            return super.onFling(motionEvent, motionEvent2, f, f2);
        }
        boolean unused = this.f6850a.e = true;
        BatteryLevelHistogram.b bVar = this.f6850a;
        this.f6850a.f6823d.fling((int) this.f6850a.g, 0, (int) f, 0, bVar.l - bVar.q, 0, 0, 0);
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onScroll(android.view.MotionEvent r2, android.view.MotionEvent r3, float r4, float r5) {
        /*
            r1 = this;
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r0 = r1.f6850a
            boolean r0 = r0.f
            if (r0 == 0) goto L_0x00cc
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            float r3 = r2.g
            float r4 = -r4
            float r3 = r3 + r4
            float unused = r2.g = r3
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            float r2 = r2.g
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r3 = r1.f6850a
            int r4 = r3.l
            int r3 = r3.q
            int r4 = r4 - r3
            float r3 = (float) r4
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x0035
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            int r3 = r2.l
            int r4 = r2.q
            int r3 = r3 - r4
            float r3 = (float) r3
        L_0x0031:
            float unused = r2.g = r3
            goto L_0x0043
        L_0x0035:
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            float r2 = r2.g
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0043
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            goto L_0x0031
        L_0x0043:
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            r2.invalidate()
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            int r2 = r2.s
            if (r2 < 0) goto L_0x00ca
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram r2 = com.miui.powercenter.batteryhistory.BatteryLevelHistogram.this
            com.miui.powercenter.view.ShadowTextView r2 = r2.f6815d
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x00ca
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r2 = r1.f6850a
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram r3 = com.miui.powercenter.batteryhistory.BatteryLevelHistogram.this
            int r2 = r2.s
            float r2 = (float) r2
            r4 = 1056964608(0x3f000000, float:0.5)
            float r2 = r2 + r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            int r4 = r4.o
            float r4 = (float) r4
            float r2 = r2 * r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            int r4 = r4.s
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r5 = r1.f6850a
            int r5 = r5.p
            int r4 = r4 * r5
            float r4 = (float) r4
            float r2 = r2 + r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            int r5 = r4.k
            float r5 = (float) r5
            float r2 = r2 + r5
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram r4 = com.miui.powercenter.batteryhistory.BatteryLevelHistogram.this
            int r4 = r4.f
            float r4 = (float) r4
            r5 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r5
            float r2 = r2 - r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            float r4 = r4.g
            float r2 = r2 + r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            int r5 = r4.j
            float r5 = (float) r5
            java.util.ArrayList r4 = r4.n
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r0 = r1.f6850a
            int r0 = r0.s
            java.lang.Object r4 = r4.get(r0)
            java.lang.Float r4 = (java.lang.Float) r4
            float r4 = r4.floatValue()
            float r5 = r5 - r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram r4 = com.miui.powercenter.batteryhistory.BatteryLevelHistogram.this
            int r4 = r4.g
            float r4 = (float) r4
            float r5 = r5 - r4
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram$b r4 = r1.f6850a
            com.miui.powercenter.batteryhistory.BatteryLevelHistogram r4 = com.miui.powercenter.batteryhistory.BatteryLevelHistogram.this
            int r4 = r4.h
            float r4 = (float) r4
            float r5 = r5 - r4
            r3.a((float) r2, (float) r5)
        L_0x00ca:
            r2 = 1
            return r2
        L_0x00cc:
            boolean r2 = super.onScroll(r2, r3, r4, r5)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.T.onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float):boolean");
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int a2 = this.f6850a.a(motionEvent.getX(), motionEvent.getY());
        if (a2 >= 0 && a2 < this.f6850a.n.size()) {
            Message message = new Message();
            message.what = 10003;
            BatteryLevelHistogram.b bVar = this.f6850a;
            int unused = bVar.r = bVar.s;
            if (this.f6850a.s != a2) {
                int unused2 = this.f6850a.s = a2;
                BatteryLevelHistogram.b bVar2 = this.f6850a;
                BatteryLevelHistogram batteryLevelHistogram = BatteryLevelHistogram.this;
                float f = ((((float) a2) + 0.5f) * ((float) bVar2.o)) + ((float) (this.f6850a.p * a2));
                BatteryLevelHistogram.b bVar3 = this.f6850a;
                float b2 = ((f + ((float) bVar3.k)) - (((float) BatteryLevelHistogram.this.f) / 2.0f)) + this.f6850a.g;
                BatteryLevelHistogram.b bVar4 = this.f6850a;
                batteryLevelHistogram.a(true, b2, ((((float) bVar4.j) - ((Float) bVar4.n.get(a2)).floatValue()) - ((float) BatteryLevelHistogram.this.g)) - ((float) BatteryLevelHistogram.this.h));
                BatteryLevelHistogram.this.d();
                message.arg1 = a2;
                message.arg2 = a2 + 1;
            } else {
                int unused3 = this.f6850a.s = -1;
                BatteryLevelHistogram.this.a(false, 0.0f, 0.0f);
                message.arg1 = -1;
                message.arg2 = -1;
            }
            this.f6850a.b();
            i.a().a(message);
            this.f6850a.invalidate();
        }
        return super.onSingleTapUp(motionEvent);
    }
}
