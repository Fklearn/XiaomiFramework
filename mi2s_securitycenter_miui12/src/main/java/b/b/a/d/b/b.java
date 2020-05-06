package b.b.a.d.b;

import miui.widget.DropDownSingleChoiceMenu;

class b implements DropDownSingleChoiceMenu.OnMenuListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f1377a;

    b(c cVar) {
        this.f1377a = cVar;
    }

    public void onDismiss() {
    }

    public void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i) {
        c cVar = this.f1377a;
        cVar.r = cVar.p.get(i).intValue();
        int unused = this.f1377a.s = i;
        this.f1377a.e();
        c cVar2 = this.f1377a;
        cVar2.f1381d.c(cVar2.r == -1);
        c cVar3 = this.f1377a;
        cVar3.g.setText((CharSequence) cVar3.q.get(i));
    }

    public void onShow() {
    }
}
