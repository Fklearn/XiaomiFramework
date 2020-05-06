package com.miui.gamebooster.n.d;

import android.util.Log;
import android.view.View;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.a;

public class c extends b {

    /* renamed from: c  reason: collision with root package name */
    private int f4689c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f4690d;
    private int e;
    private int f;

    public c(int i, int i2, int i3) {
        super(i);
        this.e = i3;
        this.f4689c = i2;
        int i4 = 2;
        if (i3 != 2) {
            i4 = 3;
            if (i3 == 3) {
                i4 = 1;
            } else if (i3 != 4) {
                if (i3 != 5) {
                    i4 = 0;
                } else {
                    this.f = 4;
                    return;
                }
            }
        }
        this.f = i4;
    }

    public void a(View view) {
        Log.i("DispalyStyleDetailModel", "onClick: " + this + "\tdisplay=" + this.f + "\tstatus=" + a.a());
        a(true);
        f.a(this.f);
        a.a(this.f);
    }

    public void a(boolean z) {
        this.f4690d = z;
    }

    public int c() {
        return this.e;
    }

    public int d() {
        return this.f4689c;
    }

    public boolean e() {
        return f.b() == this.f;
    }
}
