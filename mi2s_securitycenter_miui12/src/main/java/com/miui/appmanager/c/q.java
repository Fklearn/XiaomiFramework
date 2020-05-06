package com.miui.appmanager.c;

import android.view.View;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class q extends k {
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f3661c;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private TextView f3662a;

        public a(View view) {
            super(view);
            this.f3662a = (TextView) view.findViewById(R.id.header_title);
        }

        public void a(View view, k kVar, int i) {
            super.a(view, kVar, i);
            q qVar = (q) kVar;
            TextView textView = this.f3662a;
            if (textView != null) {
                textView.setText(qVar.f3661c);
            }
        }
    }

    public q() {
        super(R.layout.app_manager_listitem_header_view);
    }

    public void a(String str) {
        this.f3661c = str;
    }
}
