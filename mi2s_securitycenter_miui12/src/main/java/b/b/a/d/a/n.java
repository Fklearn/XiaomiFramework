package b.b.a.d.a;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import b.b.a.d.a.l;
import b.b.a.d.a.o;
import com.miui.securitycenter.R;

class n implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l.a f1368a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ o.a f1369b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f1370c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ o f1371d;

    n(o oVar, l.a aVar, o.a aVar2, int i) {
        this.f1371d = oVar;
        this.f1368a = aVar;
        this.f1369b = aVar2;
        this.f1370c = i;
    }

    public void onClick(View view) {
        o oVar = this.f1371d;
        if (!oVar.e) {
            this.f1368a.f1363b.setText(oVar.f.getString(R.string.log_count, new Object[]{Integer.valueOf(this.f1369b.f1374c)}));
            o oVar2 = this.f1371d;
            Context context = oVar2.f;
            o.a aVar = this.f1369b;
            oVar2.a(context, aVar.f1373b, aVar.i);
            return;
        }
        CheckBox checkBox = this.f1368a.g;
        checkBox.setChecked(!checkBox.isChecked());
        this.f1371d.a(this.f1370c, this.f1368a.g.isChecked(), false);
    }
}
