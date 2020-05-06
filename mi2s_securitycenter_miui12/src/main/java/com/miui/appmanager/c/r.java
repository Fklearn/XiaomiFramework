package com.miui.appmanager.c;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class r extends k {
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public SpannableString f3663c;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private TextView f3664a;

        public a(View view) {
            super(view);
            this.f3664a = (TextView) view.findViewById(R.id.am_show_all_apps);
            this.f3664a.setMovementMethod(LinkMovementMethod.getInstance());
        }

        public void a(View view, k kVar, int i) {
            super.a(view, kVar, i);
            r rVar = (r) kVar;
            TextView textView = this.f3664a;
            if (textView != null) {
                textView.setText(rVar.f3663c);
            }
        }
    }

    public r() {
        super(R.layout.app_manager_search_prompt_layout);
    }

    public void a(SpannableString spannableString) {
        this.f3663c = spannableString;
    }
}
