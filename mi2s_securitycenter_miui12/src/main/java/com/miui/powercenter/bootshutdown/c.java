package com.miui.powercenter.bootshutdown;

import android.content.Context;
import com.miui.powercenter.utils.h;
import com.miui.securitycenter.R;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static int[] f6948a = {2, 3, 4, 5, 6, 7, 1};

    /* renamed from: b  reason: collision with root package name */
    private int f6949b;

    public c(int i) {
        this.f6949b = i;
    }

    private boolean a(int i) {
        return ((1 << i) & this.f6949b) > 0;
    }

    public String a(Context context, boolean z) {
        int i;
        StringBuilder sb = new StringBuilder();
        int i2 = this.f6949b;
        if (i2 == 0) {
            return z ? context.getText(R.string.never).toString() : "";
        }
        if (i2 == 127) {
            i = R.string.every_day;
        } else if (i2 == 128) {
            i = R.string.legal_workday;
            if (h.a(context)) {
                i = R.string.legal_workday_invalidate;
            }
        } else {
            int i3 = 0;
            while (i2 > 0) {
                if ((i2 & 1) == 1) {
                    i3++;
                }
                i2 >>= 1;
            }
            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            String[] shortWeekdays = i3 > 1 ? dateFormatSymbols.getShortWeekdays() : dateFormatSymbols.getWeekdays();
            for (int i4 = 0; i4 < 7; i4++) {
                if ((this.f6949b & (1 << i4)) != 0) {
                    sb.append(shortWeekdays[f6948a[i4]]);
                    i3--;
                    if (i3 > 0) {
                        sb.append(" ");
                    }
                }
            }
            return sb.toString();
        }
        return context.getText(i).toString();
    }

    public void a(int i, boolean z) {
        int i2;
        if (z) {
            i2 = (1 << i) | this.f6949b;
        } else {
            i2 = (~(1 << i)) & this.f6949b;
        }
        this.f6949b = i2;
    }

    public void a(c cVar) {
        this.f6949b = cVar.f6949b;
    }

    public void a(boolean z) {
        if (z) {
            this.f6949b = 0;
            a(7, true);
            return;
        }
        a(7, false);
    }

    public void a(int[] iArr) {
        boolean z;
        int i = 0;
        while (i < 7) {
            int i2 = i + 1;
            int i3 = i2;
            while (true) {
                if (i3 >= 7) {
                    z = false;
                    break;
                } else if ((this.f6949b & (1 << i3)) != 0) {
                    iArr[i] = i3 - i;
                    z = true;
                    break;
                } else {
                    i3++;
                }
            }
            if (!z) {
                int i4 = 0;
                while (true) {
                    if (i4 >= 7) {
                        break;
                    } else if ((this.f6949b & (1 << i4)) != 0) {
                        iArr[i] = (7 - i) + i4;
                        break;
                    } else {
                        i4++;
                    }
                }
            }
            i = i2;
        }
    }

    public boolean[] a() {
        boolean[] zArr = new boolean[7];
        for (int i = 0; i < 7; i++) {
            zArr[i] = a(i);
        }
        return zArr;
    }

    public int b() {
        return this.f6949b;
    }

    public boolean c() {
        Calendar instance = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            if ((this.f6949b & (1 << i)) != 0) {
                if (i == 6) {
                    if (instance.get(7) == 1) {
                        return true;
                    }
                } else if (instance.get(7) == i + 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
