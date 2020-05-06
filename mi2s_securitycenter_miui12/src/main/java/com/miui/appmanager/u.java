package com.miui.appmanager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class u extends RecyclerView.l {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3689a;

    u(AppManagerMainActivity appManagerMainActivity) {
        this.f3689a = appManagerMainActivity;
    }

    public void a(@NonNull RecyclerView recyclerView, int i) {
        if (i == 0) {
            boolean unused = this.f3689a.O = false;
            this.f3689a.D();
            if (this.f3689a.P != -1) {
                if (!(this.f3689a.P == 0 && this.f3689a.G == 3 && !this.f3689a.T) && (this.f3689a.P != 1 || (!(this.f3689a.G == 0 || this.f3689a.G == 2) || this.f3689a.U))) {
                    this.f3689a.l.notifyDataSetChanged();
                } else {
                    this.f3689a.F();
                }
                int unused2 = this.f3689a.P = -1;
            }
        } else if (i == 1 || i == 2) {
            boolean unused3 = this.f3689a.O = true;
            this.f3689a.z();
        }
    }
}
