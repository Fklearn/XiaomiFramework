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

public class k extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7022a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7023b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7024c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7025d;

        private a() {
        }

        /* synthetic */ a(j jVar) {
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
            aVar = new a((j) null);
            aVar.f7022a = (ImageView) view.findViewById(16908294);
            aVar.f7023b = (TextView) view.findViewById(16908310);
            aVar.f7024c = (TextView) view.findViewById(16908308);
            aVar.f7025d = (TextView) view.findViewById(16908313);
        } else {
            aVar = (a) view.getTag();
        }
        l.a(view);
        a(view, aVar);
    }

    /* access modifiers changed from: protected */
    public void a(View view, a aVar) {
        b.a(aVar.f7022a, (int) R.drawable.extreme_power_save_notify_icon);
        aVar.f7023b.setText(R.string.extreme_power_save_mode_title);
        aVar.f7024c.setText(R.string.extreme_power_save_mode_card_summary);
        aVar.f7025d.setText(R.string.btn_text_goto_setup);
        j jVar = new j(this);
        aVar.f7025d.setOnClickListener(jVar);
        view.setOnClickListener(jVar);
    }
}
