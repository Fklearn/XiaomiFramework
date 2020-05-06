package com.miui.powercenter.deepsave.a;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import com.miui.powercenter.utils.l;
import com.miui.securitycenter.R;

public class h extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7017a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7018b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7019c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7020d;

        private a() {
        }

        /* synthetic */ a(g gVar) {
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
            aVar = new a((g) null);
            aVar.f7017a = (ImageView) view.findViewById(16908294);
            aVar.f7018b = (TextView) view.findViewById(16908310);
            aVar.f7019c = (TextView) view.findViewById(16908308);
            aVar.f7020d = (TextView) view.findViewById(16908313);
        } else {
            aVar = (a) view.getTag();
        }
        a(view, aVar, context);
    }

    /* access modifiers changed from: protected */
    public void a(View view, a aVar, Context context) {
        aVar.f7017a.setImageBitmap(l.b(context));
        aVar.f7018b.setText(R.string.power_center_auto_shutdown);
        aVar.f7019c.setText(R.string.deep_save_ontime_boot_shutdown_summary);
        aVar.f7020d.setText(R.string.btn_text_goto_setup);
        b.b.c.j.l.a(view);
        g gVar = new g(this);
        aVar.f7020d.setOnClickListener(gVar);
        view.setOnClickListener(gVar);
    }
}
