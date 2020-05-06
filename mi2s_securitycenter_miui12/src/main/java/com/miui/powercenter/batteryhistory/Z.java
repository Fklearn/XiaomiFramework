package com.miui.powercenter.batteryhistory;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Z {

    /* renamed from: a  reason: collision with root package name */
    int[] f6858a;

    /* renamed from: b  reason: collision with root package name */
    Paint[] f6859b;

    /* renamed from: c  reason: collision with root package name */
    int f6860c;

    /* renamed from: d  reason: collision with root package name */
    int[] f6861d;
    int e;
    int f;

    public void a(int i) {
        if (this.e != 0) {
            a(i, 0);
        }
    }

    public void a(int i, int i2) {
        int i3;
        int[] iArr = this.f6861d;
        if (iArr != null && i2 != this.e && (i3 = this.f6860c) < iArr.length) {
            if (i == this.f && i3 > 0) {
                this.f6860c = i3 - 1;
            }
            int[] iArr2 = this.f6861d;
            int i4 = this.f6860c;
            iArr2[i4] = (i2 << 16) | i;
            this.f6860c = i4 + 1;
            this.e = i2;
            this.f = i;
        }
    }

    public void a(Canvas canvas, int i, int i2, int i3, boolean z) {
        int i4 = i2;
        int i5 = i4 + i3;
        int i6 = 0;
        int i7 = i;
        int i8 = 0;
        while (i6 < this.f6860c) {
            int i9 = this.f6861d[i6];
            int i10 = 65535 & i9;
            if (z) {
                i10 = i - i10;
            }
            int i11 = (i9 & -65536) >> 16;
            if (i8 != 0) {
                Paint[] paintArr = this.f6859b;
                if (i8 >= paintArr.length) {
                    i8 = paintArr.length - 1;
                }
                canvas.drawRect((float) i7, (float) i4, (float) i10, (float) i5, this.f6859b[i8]);
            }
            i6++;
            i8 = i11;
            i7 = i10;
        }
    }

    public void a(int[] iArr) {
        this.f6858a = iArr;
        this.f6859b = new Paint[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            this.f6859b[i] = new Paint();
            this.f6859b[i].setColor(iArr[i]);
            this.f6859b[i].setStyle(Paint.Style.FILL);
        }
    }

    public void b(int i) {
        this.f6861d = i > 0 ? new int[(i * 2)] : null;
        this.f6860c = 0;
        this.e = 0;
        this.f = -1;
    }
}
