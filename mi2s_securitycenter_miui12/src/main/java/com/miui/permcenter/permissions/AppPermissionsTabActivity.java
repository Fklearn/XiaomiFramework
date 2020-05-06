package com.miui.permcenter.permissions;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import b.b.c.j.f;
import b.b.o.g.d;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import miui.app.ActionBar;
import miui.app.AlertDialog;
import miui.os.Build;

public class AppPermissionsTabActivity extends b.b.c.c.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public int f6196a = 0;

    private class a {

        /* renamed from: a  reason: collision with root package name */
        int f6197a;

        /* renamed from: b  reason: collision with root package name */
        int f6198b;

        /* renamed from: c  reason: collision with root package name */
        int f6199c;

        public a(int i, int i2, int i3) {
            this.f6197a = i;
            this.f6198b = i2;
            this.f6199c = i3;
        }
    }

    private class b extends androidx.viewpager.widget.a {

        /* renamed from: c  reason: collision with root package name */
        private Context f6201c;

        /* renamed from: d  reason: collision with root package name */
        private a[] f6202d;

        public b(Context context, @NonNull a[] aVarArr) {
            this.f6201c = context;
            this.f6202d = aVarArr;
        }

        public int a() {
            return this.f6202d.length;
        }

        /* JADX WARNING: type inference failed for: r1v10, types: [android.content.Context, com.miui.permcenter.permissions.AppPermissionsTabActivity] */
        @NonNull
        public Object a(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(this.f6201c).inflate(R.layout.pm_item_dialog_upgrade_tip, (ViewGroup) null);
            ((ImageView) inflate.findViewById(R.id.pm_tip_img)).setBackground(this.f6201c.getDrawable(this.f6202d[i].f6197a));
            ((TextView) inflate.findViewById(R.id.pm_tip_text)).setText(this.f6202d[i].f6198b);
            if (this.f6202d[i].f6199c != 0 && f.b(AppPermissionsTabActivity.this)) {
                TextView textView = (TextView) inflate.findViewById(R.id.pm_tip_link);
                AppPermissionsTabActivity appPermissionsTabActivity = AppPermissionsTabActivity.this;
                String string = appPermissionsTabActivity.getString(R.string.permission_privacy_link, new Object[]{"https://privacy.miui.com", appPermissionsTabActivity.getString(R.string.permission_upgrade_more)});
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(Html.fromHtml(string));
                textView.setVisibility(0);
            }
            ((ViewPager) viewGroup).addView(inflate, i);
            return inflate;
        }

        public void a(@NonNull ViewGroup viewGroup, int i, @NonNull Object obj) {
            ((ViewPager) viewGroup).removeView((View) obj);
        }

        public boolean a(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [android.content.Context, com.miui.permcenter.permissions.AppPermissionsTabActivity] */
    /* access modifiers changed from: private */
    public void a(int i) {
        if (i == 1 && !com.miui.common.persistence.b.a("key_upgrade_tip", false)) {
            com.miui.common.persistence.b.b("key_upgrade_tip", true);
            a[] aVarArr = {new a(R.drawable.perm_upgrade_tip1, R.string.permission_upgrade_privacy_title, 0), new a(R.drawable.perm_upgrade_tip2, R.string.permission_upgrade_runtime, 0), new a(R.drawable.perm_upgrade_tip3, R.string.permission_upgrade_virtual, R.string.permission_upgrade_more)};
            View inflate = LayoutInflater.from(this).inflate(R.layout.pm_layout_dialog_upgrade_tip, (ViewGroup) null, false);
            AlertDialog show = new AlertDialog.Builder(this).setTitle(R.string.permission_upgrade_title).setView(inflate).setPositiveButton(R.string.button_text_next_step, (DialogInterface.OnClickListener) null).show();
            show.setOnDismissListener(new C0468e(this));
            Button button = show.getButton(-1);
            ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.pm_tip_pager);
            viewPager.setAdapter(new b(this, aVarArr));
            ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) inflate.findViewById(R.id.pm_tip_indicator);
            viewPagerIndicator.a(aVarArr.length, this.f6196a);
            button.setOnClickListener(new C0469f(this, aVarArr, viewPager, viewPagerIndicator, button, show));
            viewPager.a((ViewPager.e) new C0470g(this, viewPagerIndicator, aVarArr, button));
        }
    }

    static /* synthetic */ int b(AppPermissionsTabActivity appPermissionsTabActivity) {
        int i = appPermissionsTabActivity.f6196a + 1;
        appPermissionsTabActivity.f6196a = i;
        return i;
    }

    private boolean l() {
        return !Build.IS_INTERNATIONAL_BUILD && getResources().getConfiguration().locale.getCountry().equals("CN") && TextUtils.equals(getResources().getConfiguration().locale.getLanguage(), "zh");
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [b.b.c.c.a, android.content.Context, com.miui.permcenter.permissions.AppPermissionsTabActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("select_navi_item", -1);
        ActionBar actionBar = getActionBar();
        actionBar.setFragmentViewPagerMode(this, getFragmentManager(), false);
        actionBar.addFragmentTab(C0466c.f6254a, actionBar.newTab().setText(R.string.activity_title_apps_manager), C0466c.class, (Bundle) null, false);
        actionBar.addFragmentTab(D.f6221a, actionBar.newTab().setText(R.string.activity_title_permissions_manager), D.class, (Bundle) null, false);
        if (intExtra >= 0 && intExtra < actionBar.getTabCount()) {
            actionBar.setSelectedNavigationItem(intExtra);
            if (l()) {
                a(intExtra);
            }
        }
        if (l() && !com.miui.common.persistence.b.a("key_upgrade_tip", false)) {
            actionBar.addOnFragmentViewPagerChangeListener(new C0467d(this));
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AppPermissionsTabActivity.super.onDestroy();
        d.a("AppPermTabActivity", (Object) (InputMethodManager) getApplicationContext().getSystemService("input_method"), "windowDismissed", (Class<?>[]) new Class[]{IBinder.class}, getWindow().getDecorView().getWindowToken());
    }
}
