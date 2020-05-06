package com.miui.applicationlock;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import b.b.c.j.B;
import b.b.c.j.d;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.o;
import com.miui.common.expandableview.WrapPinnedHeaderListView;
import com.miui.luckymoney.config.AppConstants;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import miui.security.SecurityManager;
import miui.util.ArraySet;

public class MaskNotificationActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    public static final ArraySet<String> f3191a = new ArraySet<>();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public SecurityManager f3192b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f3193c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ArrayList<F> f3194d;
    /* access modifiers changed from: private */
    public B e;
    private boolean f;
    private C0259c g;
    private a h;
    /* access modifiers changed from: private */
    public Comparator<C0257a> i = new C0315za(this);

    private static class a implements LoaderManager.LoaderCallbacks<ArrayList<F>> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<MaskNotificationActivity> f3195a;

        private a(MaskNotificationActivity maskNotificationActivity) {
            this.f3195a = new WeakReference<>(maskNotificationActivity);
        }

        /* synthetic */ a(MaskNotificationActivity maskNotificationActivity, C0315za zaVar) {
            this(maskNotificationActivity);
        }

        /* renamed from: a */
        public void onLoadFinished(Loader<ArrayList<F>> loader, ArrayList<F> arrayList) {
            MaskNotificationActivity maskNotificationActivity = (MaskNotificationActivity) this.f3195a.get();
            if (maskNotificationActivity != null) {
                ArrayList unused = maskNotificationActivity.f3194d = arrayList;
                maskNotificationActivity.e.a((List<F>) maskNotificationActivity.f3194d, false);
            }
        }

        /* JADX WARNING: type inference failed for: r1v3, types: [android.content.Context, com.miui.applicationlock.MaskNotificationActivity] */
        public Loader onCreateLoader(int i, Bundle bundle) {
            ? r1 = (MaskNotificationActivity) this.f3195a.get();
            if (r1 == 0) {
                return null;
            }
            return new Ca(this, r1, r1);
        }

        public void onLoaderReset(Loader loader) {
        }
    }

    static {
        f3191a.add(AppConstants.Package.PACKAGE_NAME_MM);
        f3191a.add(AppConstants.Package.PACKAGE_NAME_QQ);
        f3191a.add("com.android.mms");
        f3191a.add(SecurityManager.SKIP_INTERCEPT_PACKAGE);
        f3191a.add("com.android.contacts");
        f3191a.add(AppConstants.Package.PACKAGE_NAME_ALIPAY);
        f3191a.add("jp.naver.line.android");
        f3191a.add("com.whatsapp");
        f3191a.add("com.viber.voip");
        f3191a.add("com.bbm");
        f3191a.add("com.bsb.hike");
        f3191a.add("com.facebook.orca");
        f3191a.add("com.immomo.momo");
        f3191a.add("com.miui.notes");
        f3191a.add("com.android.email");
        f3191a.add("com.facebook.katana");
        f3191a.add("com.wumii.android.mimi");
        f3191a.add("com.instagram.android");
        f3191a.add("com.google.android.youtube");
        f3191a.add("com.facebook.lite");
    }

    private void a(boolean z) {
        for (ApplicationInfo next : o.a(this.f3192b)) {
            this.f3192b.setApplicationMaskNotificationEnabledForUser(next.packageName, z, B.c(next.uid));
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        MaskNotificationActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 1022221) {
            if (i3 == -1) {
                this.f = true;
                return;
            }
            this.f = false;
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.applicationlock.MaskNotificationActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.applock_mask_notification_page);
        String stringExtra = getIntent().getStringExtra("extra_data");
        this.g = C0259c.b(getApplicationContext());
        String stringExtra2 = getIntent().getStringExtra("enter_way");
        h.j(stringExtra2);
        if ("mask_notification_notify".equals(stringExtra2)) {
            o.d(2);
        }
        boolean z = true;
        if ((bundle == null || !bundle.containsKey(AdvancedSlider.STATE)) && stringExtra != null && stringExtra.equals("applock_setting_mask_notification")) {
            this.f = true;
        } else {
            this.f = false;
        }
        if (!this.g.d()) {
            this.f = true;
        }
        WrapPinnedHeaderListView wrapPinnedHeaderListView = (WrapPinnedHeaderListView) findViewById(R.id.listnolockapps);
        if (isDarkModeEnable()) {
            z = false;
        }
        o.a(z, getWindow());
        this.f3194d = new ArrayList<>();
        this.f3193c = getResources().getConfiguration().locale.getLanguage();
        this.f3192b = (SecurityManager) getSystemService("security");
        this.h = new a(this, (C0315za) null);
        getLoaderManager().initLoader(113, (Bundle) null, this.h);
        this.e = new B(this.f3194d, getLayoutInflater(), this);
        wrapPinnedHeaderListView.setAdapter(this.e);
        wrapPinnedHeaderListView.setOnItemClickListener(new Aa(this));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.applock_mask, menu);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MaskNotificationActivity.super.onDestroy();
        d.a(new Ba(this));
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean z;
        int itemId = menuItem.getItemId();
        if (itemId != R.id.mask_all) {
            if (itemId == R.id.unmask_all) {
                z = false;
            }
            return MaskNotificationActivity.super.onOptionsItemSelected(menuItem);
        }
        z = true;
        a(z);
        getLoaderManager().restartLoader(113, (Bundle) null, this.h);
        return MaskNotificationActivity.super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        MaskNotificationActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.f);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.applicationlock.MaskNotificationActivity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        MaskNotificationActivity.super.onStart();
        if (!this.g.d() || this.f) {
            this.f = true;
            return;
        }
        Intent intent = new Intent(this, ConfirmAccessControl.class);
        intent.putExtra("extra_data", "HappyCodingMain");
        startActivityForResult(intent, 1022221);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MaskNotificationActivity.super.onStop();
        if (this.f) {
            this.f = false;
        }
    }

    public void onWindowFocusChanged(boolean z) {
        MaskNotificationActivity.super.onWindowFocusChanged(z);
        if (z) {
            o.a(!isDarkModeEnable(), getWindow());
        }
    }
}
