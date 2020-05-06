package com.miui.permcenter.privacymanager.behaviorrecord;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import miui.util.Log;

class c extends RecyclerView.l {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6440a;

    c(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6440a = appBehaviorRecordActivity;
    }

    public void a(@NonNull RecyclerView recyclerView, int i) {
        super.a(recyclerView, i);
        if (i == 0) {
            int H = this.f6440a.j.H();
            if (this.f6440a.p && H >= this.f6440a.j.j() - 1) {
                Log.i("BehaviorRecord-ALL", "Loading More...");
                this.f6440a.k.a(true);
                AppBehaviorRecordActivity appBehaviorRecordActivity = this.f6440a;
                AppBehaviorRecordActivity.c unused = appBehaviorRecordActivity.q = new AppBehaviorRecordActivity.c(appBehaviorRecordActivity);
                this.f6440a.q.execute(new Void[0]);
            }
        }
    }

    public void a(@NonNull RecyclerView recyclerView, int i, int i2) {
        super.a(recyclerView, i, i2);
    }
}
