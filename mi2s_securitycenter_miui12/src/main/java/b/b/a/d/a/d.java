package b.b.a.d.a;

import android.view.View;
import android.widget.CheckBox;
import b.b.a.d.a.e;

class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e.b f1334a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f1335b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f1336c;

    d(e eVar, e.b bVar, int i) {
        this.f1336c = eVar;
        this.f1334a = bVar;
        this.f1335b = i;
    }

    public void onClick(View view) {
        if (!this.f1336c.e) {
            view.showContextMenu();
            return;
        }
        CheckBox checkBox = this.f1334a.e;
        checkBox.setChecked(!checkBox.isChecked());
        this.f1336c.a(this.f1335b, this.f1334a.e.isChecked(), false);
    }
}
