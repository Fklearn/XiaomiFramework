package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.CompoundButton;
import b.b.c.j.x;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.k;
import com.miui.gamebooster.model.l;
import com.miui.securitycenter.R;
import java.util.Iterator;

class Xa implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListFragment f5035a;

    Xa(WhiteListFragment whiteListFragment) {
        this.f5035a = whiteListFragment;
    }

    private void a(int i, String str) {
        int intValue;
        if (this.f5035a.h() && !this.f5035a.l.isEmpty() && i >= 0 && i < this.f5035a.l.size() && (intValue = ((Integer) this.f5035a.l.get(Integer.valueOf(i))).intValue()) >= 0 && intValue < this.f5035a.g.getItemCount()) {
            ((C0398d) this.f5035a.g.a(intValue)).a(str);
            this.f5035a.g.notifyItemChanged(intValue);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        String str;
        Activity activity = this.f5035a.getActivity();
        if (activity != null) {
            C0398d dVar = (C0398d) compoundButton.getTag();
            dVar.a(z);
            if (dVar != null) {
                String str2 = dVar.b().packageName;
                String str3 = (String) x.j(activity, str2);
                int i = dVar.b().uid;
                if (z) {
                    C0391w.a((Context) activity, str3, str2, i, 1);
                } else {
                    C0391w.a((Context) activity, str2, i, false, 1);
                }
                Iterator it = this.f5035a.f5025b.iterator();
                int i2 = 0;
                int i3 = 0;
                while (it.hasNext()) {
                    Iterator<C0398d> it2 = ((k) it.next()).a().iterator();
                    while (it2.hasNext()) {
                        if (it2.next().e()) {
                            i2++;
                        } else {
                            i3++;
                        }
                    }
                }
                for (int i4 = 0; i4 < this.f5035a.f5025b.size(); i4++) {
                    k kVar = (k) this.f5035a.f5025b.get(i4);
                    if (kVar.c() == l.ENABLED) {
                        a(i4, this.f5035a.getResources().getQuantityString(R.plurals.install_game_count_title, i2, new Object[]{Integer.valueOf(i2)}));
                        str = this.f5035a.getResources().getQuantityString(R.plurals.install_game_count_title, i2, new Object[]{Integer.valueOf(i2)});
                    } else {
                        a(i4, this.f5035a.getResources().getQuantityString(R.plurals.uninstall_game_count_title, i3, new Object[]{Integer.valueOf(i3)}));
                        str = this.f5035a.getResources().getQuantityString(R.plurals.uninstall_game_count_title, i3, new Object[]{Integer.valueOf(i3)});
                    }
                    kVar.a(str);
                }
                this.f5035a.f();
            }
        }
    }
}
