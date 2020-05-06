package com.miui.powercenter.deepsave.a;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import b.b.c.j.l;
import com.miui.powercenter.utils.b;
import com.miui.securitycenter.R;

public class m extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7027a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7028b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7029c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7030d;

        private a() {
        }

        /* synthetic */ a(l lVar) {
            this();
        }
    }

    public int a() {
        return R.layout.pc_list_item_goto_view;
    }

    public void a(int i, View view, Context context, C0191k kVar) {
        a aVar;
        super.a(i, view, context, kVar);
        if (view.getTag() == null) {
            aVar = new a((l) null);
            aVar.f7027a = (ImageView) view.findViewById(16908294);
            aVar.f7028b = (TextView) view.findViewById(16908310);
            aVar.f7029c = (TextView) view.findViewById(16908308);
            aVar.f7030d = (TextView) view.findViewById(16908313);
        } else {
            aVar = (a) view.getTag();
        }
        a(view, aVar);
    }

    /* access modifiers changed from: protected */
    public void a(View view, a aVar) {
        b.a(aVar.f7027a, (int) R.drawable.icon_hide);
        aVar.f7028b.setText(R.string.hide_mode_app_background_setting_text);
        aVar.f7029c.setText(R.string.hide_mode_result_summary_text);
        aVar.f7030d.setText(R.string.btn_text_goto_setup);
        l.a(view);
        l lVar = new l(this);
        aVar.f7030d.setOnClickListener(lVar);
        view.setOnClickListener(lVar);
    }
}
