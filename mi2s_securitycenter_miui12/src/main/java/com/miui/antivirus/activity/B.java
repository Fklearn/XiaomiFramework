package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.b;
import com.miui.antivirus.activity.SettingsActivity;
import java.util.List;

class B implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.c f2651a;

    B(SettingsActivity.c cVar) {
        this.f2651a = cVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        String str = ((b.a) this.f2651a.y.get(i)).f1472a;
        SettingsActivity.c cVar = this.f2651a;
        cVar.a(str, (List<b.a>) cVar.y);
        dialogInterface.dismiss();
    }
}
