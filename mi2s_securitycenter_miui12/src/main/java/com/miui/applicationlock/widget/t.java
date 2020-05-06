package com.miui.applicationlock.widget;

import android.widget.ImageView;
import com.miui.applicationlock.widget.MiuiNumericInputView;
import com.miui.securitycenter.R;

class t implements MiuiNumericInputView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f3455a;

    t(x xVar) {
        this.f3455a = xVar;
    }

    public void a(int i) {
        if (i == 10) {
            if (this.f3455a.f != null && this.f3455a.f.length() - 1 >= 0) {
                ((ImageView) this.f3455a.e.getChildAt(this.f3455a.f.length() - 1)).setImageResource(this.f3455a.f3459a ? R.drawable.numeric_dot_empty_light : R.drawable.numeric_dot_empty);
                this.f3455a.f.deleteCharAt(this.f3455a.f.length() - 1);
            }
        } else if (this.f3455a.f.length() < 4) {
            ((ImageView) this.f3455a.e.getChildAt(this.f3455a.f.length())).setImageResource(this.f3455a.f3459a ? R.drawable.numeric_dot_occupied_light : R.drawable.numeric_dot_occupied);
            this.f3455a.f.append((char) (i + 48));
            if (this.f3455a.f.length() == 4) {
                x xVar = this.f3455a;
                xVar.a(xVar.f.toString());
            }
        }
    }
}
