package com.miui.powercenter.deepsave.a;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import b.b.c.j.l;
import com.miui.securitycenter.R;

public class b extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7002a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7003b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7004c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7005d;

        private a() {
        }

        /* synthetic */ a(a aVar) {
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
            aVar = new a((a) null);
            aVar.f7002a = (ImageView) view.findViewById(16908294);
            aVar.f7003b = (TextView) view.findViewById(16908310);
            aVar.f7004c = (TextView) view.findViewById(16908308);
            aVar.f7005d = (TextView) view.findViewById(16908313);
        } else {
            aVar = (a) view.getTag();
        }
        a(view, aVar);
    }

    /* access modifiers changed from: protected */
    public void a(View view, a aVar) {
        com.miui.powercenter.utils.b.a(aVar.f7002a, (int) R.drawable.ic_auto_task);
        aVar.f7003b.setText(R.string.auto_task_main_title);
        aVar.f7004c.setText(R.string.auto_task_result_summary_text);
        aVar.f7005d.setText(R.string.btn_text_goto_setup);
        l.a(view);
        a aVar2 = new a(this);
        aVar.f7005d.setOnClickListener(aVar2);
        view.setOnClickListener(aVar2);
    }
}
