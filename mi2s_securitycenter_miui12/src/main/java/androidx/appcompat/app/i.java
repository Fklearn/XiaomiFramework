package androidx.appcompat.app;

import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AlertController;

class i implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertController f309a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AlertController.a f310b;

    i(AlertController.a aVar, AlertController alertController) {
        this.f310b = aVar;
        this.f309a = alertController;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.f310b.x.onClick(this.f309a.f232b, i);
        if (!this.f310b.H) {
            this.f309a.f232b.dismiss();
        }
    }
}
