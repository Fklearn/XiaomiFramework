package b.b.c.d;

import android.view.View;
import com.miui.securitycenter.R;

/* renamed from: b.b.c.d.j  reason: case insensitive filesystem */
class C0190j implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0191k f1683a;

    C0190j(C0191k kVar) {
        this.f1683a = kVar;
    }

    public void onClick(View view) {
        C0185e eVar = (C0185e) view.getTag(R.id.tag_first);
        if (eVar != null) {
            eVar.onClick(view);
        }
    }
}
