package com.miui.gamebooster.ui;

import android.text.TextUtils;
import android.widget.CompoundButton;
import com.miui.gamebooster.i.a.b;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.k;
import com.miui.gamebooster.model.l;
import com.miui.securitycenter.R;
import java.util.Iterator;

class Ja implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4919a;

    Ja(SelectGameActivity selectGameActivity) {
        this.f4919a = selectGameActivity;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        String str;
        C0398d dVar = (C0398d) compoundButton.getTag();
        dVar.a(z);
        if (dVar != null) {
            if (dVar.b() != null && !TextUtils.isEmpty(dVar.b().packageName)) {
                b.a(this.f4919a.getApplicationContext(), dVar.b().packageName, z);
            }
            SelectGameActivity selectGameActivity = this.f4919a;
            selectGameActivity.a(selectGameActivity, z, dVar);
            Iterator it = this.f4919a.f4984b.iterator();
            int i = 0;
            int i2 = 0;
            while (it.hasNext()) {
                Iterator<C0398d> it2 = ((k) it.next()).a().iterator();
                while (it2.hasNext()) {
                    if (it2.next().e()) {
                        i++;
                    } else {
                        i2++;
                    }
                }
            }
            Iterator it3 = this.f4919a.f4984b.iterator();
            while (it3.hasNext()) {
                k kVar = (k) it3.next();
                if (kVar.c() == l.ENABLED) {
                    str = this.f4919a.getResources().getQuantityString(R.plurals.install_game_count_title, i, new Object[]{Integer.valueOf(i)});
                } else {
                    str = this.f4919a.getResources().getQuantityString(R.plurals.uninstall_game_count_title, i2, new Object[]{Integer.valueOf(i2)});
                }
                kVar.a(str);
            }
            this.f4919a.o();
        }
    }
}
