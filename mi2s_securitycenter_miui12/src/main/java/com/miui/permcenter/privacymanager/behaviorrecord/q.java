package com.miui.permcenter.privacymanager.behaviorrecord;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;

class q extends RecyclerView.l {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6459a;

    q(PrivacyDetailActivity privacyDetailActivity) {
        this.f6459a = privacyDetailActivity;
    }

    public void a(@NonNull RecyclerView recyclerView, int i) {
        super.a(recyclerView, i);
        if (i == 0) {
            int H = this.f6459a.o.H();
            if (this.f6459a.D && H >= this.f6459a.o.j() - 1) {
                Log.i("BehaviorRecord-SINGLE", "Loading More...");
                this.f6459a.y.a(true);
                PrivacyDetailActivity privacyDetailActivity = this.f6459a;
                PrivacyDetailActivity.f unused = privacyDetailActivity.E = new PrivacyDetailActivity.f(privacyDetailActivity);
                this.f6459a.E.execute(new Void[0]);
            }
        }
    }

    public void a(@NonNull RecyclerView recyclerView, int i, int i2) {
        super.a(recyclerView, i, i2);
    }
}
