package androidx.appcompat.app;

import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AlertController;

class j implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertController.RecycleListView f311a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AlertController f312b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController.a f313c;

    j(AlertController.a aVar, AlertController.RecycleListView recycleListView, AlertController alertController) {
        this.f313c = aVar;
        this.f311a = recycleListView;
        this.f312b = alertController;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        boolean[] zArr = this.f313c.F;
        if (zArr != null) {
            zArr[i] = this.f311a.isItemChecked(i);
        }
        this.f313c.J.onClick(this.f312b.f232b, i, this.f311a.isItemChecked(i));
    }
}
