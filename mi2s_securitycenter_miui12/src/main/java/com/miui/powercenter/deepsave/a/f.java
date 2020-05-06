package com.miui.powercenter.deepsave.a;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import b.b.c.j.l;
import b.b.c.j.x;
import com.miui.powercenter.deepsave.BatterySaveIdeaActivity;
import com.miui.powercenter.deepsave.IdeaModel;
import com.miui.powercenter.deepsave.e;
import com.miui.powercenter.utils.b;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class f extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    private ArrayList<IdeaModel> f7011d = new ArrayList<>();
    private View.OnClickListener e = new e(this);

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        ViewGroup f7012a;

        /* renamed from: b  reason: collision with root package name */
        ViewGroup f7013b;

        /* renamed from: c  reason: collision with root package name */
        ViewGroup f7014c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7015d;

        private a() {
        }

        /* synthetic */ a(e eVar) {
            this();
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context) {
        Intent intent = new Intent(context, BatterySaveIdeaActivity.class);
        intent.putParcelableArrayListExtra("idea_list", this.f7011d);
        context.startActivity(intent);
    }

    private void a(ViewGroup viewGroup, String str) {
        b.a((ImageView) viewGroup.findViewById(16908294), str);
    }

    /* access modifiers changed from: private */
    public void a(IdeaModel ideaModel, Context context) {
        Intent intent = new Intent("miui.intent.action.POWER_CENTER_WEBVIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, ideaModel.url);
        String a2 = b.a(context, ideaModel.packageName);
        if (!TextUtils.isEmpty(a2)) {
            intent.putExtra("title_append", "(" + a2 + ")");
        }
        x.c(context, intent);
    }

    private void a(a aVar, Context context) {
        List<IdeaModel> a2 = e.b().a();
        if (a2 != null) {
            this.f7011d.clear();
            this.f7011d.addAll(a2);
            if (!this.f7011d.isEmpty()) {
                aVar.f7015d.setEnabled(true);
            }
            Collections.shuffle(this.f7011d);
            if (this.f7011d.size() > 0) {
                aVar.f7012a.setVisibility(0);
                aVar.f7012a.setTag(this.f7011d.get(0));
                b(aVar.f7012a, this.f7011d.get(0).title);
                a(aVar.f7012a, this.f7011d.get(0).packageName);
            }
            if (this.f7011d.size() > 1) {
                aVar.f7013b.setVisibility(0);
                aVar.f7013b.setTag(this.f7011d.get(1));
                b(aVar.f7013b, this.f7011d.get(1).title);
                a(aVar.f7013b, this.f7011d.get(1).packageName);
            }
            if (this.f7011d.size() > 2) {
                aVar.f7014c.setVisibility(0);
                aVar.f7014c.setTag(this.f7011d.get(2));
                b(aVar.f7014c, this.f7011d.get(2).title);
                a(aVar.f7014c, this.f7011d.get(2).packageName);
            }
        }
    }

    private void b(ViewGroup viewGroup, String str) {
        ((TextView) viewGroup.findViewById(16908310)).setText(str);
    }

    public int a() {
        return R.layout.pc_list_item_battery_save_idea;
    }

    public void a(int i, View view, Context context, C0191k kVar) {
        a aVar;
        super.a(i, view, context, kVar);
        if (view.getTag() == null) {
            aVar = new a((e) null);
            aVar.f7012a = (ViewGroup) view.findViewById(R.id.item1);
            aVar.f7012a.setOnClickListener(this.e);
            aVar.f7013b = (ViewGroup) view.findViewById(R.id.item2);
            aVar.f7013b.setOnClickListener(this.e);
            aVar.f7014c = (ViewGroup) view.findViewById(R.id.item3);
            aVar.f7014c.setOnClickListener(this.e);
            aVar.f7015d = (TextView) view.findViewById(R.id.more);
            aVar.f7015d.setOnClickListener(this.e);
        } else {
            aVar = (a) view.getTag();
        }
        aVar.f7015d.setEnabled(false);
        aVar.f7012a.setVisibility(8);
        aVar.f7013b.setVisibility(8);
        aVar.f7014c.setVisibility(8);
        l.a(view);
        a(aVar, context);
    }
}
