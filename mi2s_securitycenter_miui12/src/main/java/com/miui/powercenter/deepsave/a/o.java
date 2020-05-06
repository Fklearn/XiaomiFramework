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

public class o extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7032a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7033b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7034c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7035d;

        private a() {
        }

        /* synthetic */ a(n nVar) {
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
            aVar = new a((n) null);
            aVar.f7032a = (ImageView) view.findViewById(16908294);
            aVar.f7033b = (TextView) view.findViewById(16908310);
            aVar.f7034c = (TextView) view.findViewById(16908308);
            aVar.f7035d = (TextView) view.findViewById(16908313);
        } else {
            aVar = (a) view.getTag();
        }
        a(context, view, aVar);
    }

    /* access modifiers changed from: protected */
    public void a(Context context, View view, a aVar) {
        TextView textView;
        int i;
        b.a(aVar.f7032a, (int) R.drawable.icon_save_mode);
        aVar.f7033b.setText(R.string.power_save_title_text);
        if (!com.miui.powercenter.utils.o.l(context.getApplicationContext())) {
            textView = aVar.f7034c;
            i = R.string.power_save_summary_text;
        } else {
            textView = aVar.f7034c;
            i = R.string.power_save_started_summary_text;
        }
        textView.setText(i);
        aVar.f7035d.setText(R.string.btn_text_goto_setup);
        l.a(view);
        n nVar = new n(this);
        view.setOnClickListener(nVar);
        aVar.f7035d.setOnClickListener(nVar);
    }
}
