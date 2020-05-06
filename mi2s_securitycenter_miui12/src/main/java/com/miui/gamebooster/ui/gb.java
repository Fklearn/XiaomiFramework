package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.miui.securitycenter.R;

class gb implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5067a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ib f5068b;

    gb(ib ibVar, Context context) {
        this.f5068b = ibVar;
        this.f5067a = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Toast.makeText(this.f5067a, this.f5068b.getString(R.string.gb_game_video_page_delete_success), 0).show();
        this.f5068b.a(this.f5067a);
        this.f5068b.j();
        if (!this.f5068b.h()) {
            this.f5068b.f5075b.setVisibility(8);
            this.f5068b.f5074a.setVisibility(0);
        }
    }
}
