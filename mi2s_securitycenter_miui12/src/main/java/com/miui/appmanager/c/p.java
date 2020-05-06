package com.miui.appmanager.c;

import android.view.View;
import android.widget.TextView;
import com.miui.appmanager.AppManageUtils;
import com.miui.securitycenter.R;

public class p extends k {

    /* renamed from: c  reason: collision with root package name */
    private String f3656c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View.OnClickListener f3657d;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private TextView f3658a;

        /* renamed from: b  reason: collision with root package name */
        private View f3659b;

        /* renamed from: c  reason: collision with root package name */
        private View f3660c;

        public a(View view) {
            super(view);
            this.f3658a = (TextView) view.findViewById(R.id.am_title);
            this.f3659b = view.findViewById(R.id.am_sort);
            this.f3660c = view.findViewById(R.id.am_discription);
            if (this.f3660c == null) {
                return;
            }
            if (AppManageUtils.b() <= 3) {
                this.f3660c.setEnabled(false);
            } else {
                this.f3660c.setVisibility(8);
            }
        }

        public void a(View view, k kVar, int i) {
            super.a(view, kVar, i);
            p pVar = (p) kVar;
            TextView textView = this.f3658a;
            if (textView != null) {
                textView.setText(pVar.b());
            }
            if (this.f3659b != null && pVar.f3657d != null) {
                this.f3659b.setOnClickListener(pVar.f3657d);
            }
        }
    }

    public p() {
        super(R.layout.app_manager_sort_title);
    }

    public void a(View.OnClickListener onClickListener) {
        this.f3657d = onClickListener;
    }

    public void a(String str) {
        this.f3656c = str;
    }

    public String b() {
        return this.f3656c;
    }
}
