package androidx.appcompat.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertController;

/* renamed from: androidx.appcompat.app.g  reason: case insensitive filesystem */
class C0085g extends ArrayAdapter<CharSequence> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertController.RecycleListView f303a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AlertController.a f304b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0085g(AlertController.a aVar, Context context, int i, int i2, CharSequence[] charSequenceArr, AlertController.RecycleListView recycleListView) {
        super(context, i, i2, charSequenceArr);
        this.f304b = aVar;
        this.f303a = recycleListView;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        boolean[] zArr = this.f304b.F;
        if (zArr != null && zArr[i]) {
            this.f303a.setItemChecked(i, true);
        }
        return view2;
    }
}
