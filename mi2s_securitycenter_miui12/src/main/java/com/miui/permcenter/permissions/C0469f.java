package com.miui.permcenter.permissions;

import android.view.View;
import android.widget.Button;
import androidx.viewpager.widget.ViewPager;
import com.miui.permcenter.permissions.AppPermissionsTabActivity;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

/* renamed from: com.miui.permcenter.permissions.f  reason: case insensitive filesystem */
class C0469f implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppPermissionsTabActivity.a[] f6261a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ViewPager f6262b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ViewPagerIndicator f6263c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ Button f6264d;
    final /* synthetic */ AlertDialog e;
    final /* synthetic */ AppPermissionsTabActivity f;

    C0469f(AppPermissionsTabActivity appPermissionsTabActivity, AppPermissionsTabActivity.a[] aVarArr, ViewPager viewPager, ViewPagerIndicator viewPagerIndicator, Button button, AlertDialog alertDialog) {
        this.f = appPermissionsTabActivity;
        this.f6261a = aVarArr;
        this.f6262b = viewPager;
        this.f6263c = viewPagerIndicator;
        this.f6264d = button;
        this.e = alertDialog;
    }

    public void onClick(View view) {
        if (this.f.f6196a < this.f6261a.length - 1) {
            this.f6262b.setCurrentItem(AppPermissionsTabActivity.b(this.f));
            this.f6263c.setSelected(this.f.f6196a);
            if (this.f.f6196a == this.f6261a.length - 1) {
                this.f6264d.setText(R.string.ok);
            }
        } else if (this.f.f6196a == this.f6261a.length - 1) {
            this.e.dismiss();
        }
    }
}
