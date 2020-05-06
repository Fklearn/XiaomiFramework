package b.b.a.d.a;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import b.b.a.d.a.h;
import b.b.a.d.a.l;
import com.miui.securitycenter.R;

class g implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l.a f1347a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ h.a f1348b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f1349c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ h f1350d;

    g(h hVar, l.a aVar, h.a aVar2, int i) {
        this.f1350d = hVar;
        this.f1347a = aVar;
        this.f1348b = aVar2;
        this.f1349c = i;
    }

    public void onClick(View view) {
        h hVar = this.f1350d;
        if (!hVar.e) {
            this.f1347a.f1363b.setText(hVar.f.getString(R.string.log_count, new Object[]{Integer.valueOf(this.f1348b.e)}));
            h hVar2 = this.f1350d;
            Context context = hVar2.f;
            h.a aVar = this.f1348b;
            hVar2.a(context, aVar.f1351a, aVar.f1353c);
            return;
        }
        CheckBox checkBox = this.f1347a.g;
        checkBox.setChecked(!checkBox.isChecked());
        this.f1350d.a(this.f1349c, this.f1347a.g.isChecked(), false);
    }
}
