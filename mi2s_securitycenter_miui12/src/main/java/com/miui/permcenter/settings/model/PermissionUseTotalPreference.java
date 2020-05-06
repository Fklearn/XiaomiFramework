package com.miui.permcenter.settings.model;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.permcenter.privacymanager.a.d;
import com.miui.permcenter.settings.model.e;
import com.miui.permcenter.settings.view.FlashView;
import com.miui.permcenter.settings.view.PermissionTotalView;
import com.miui.permcenter.settings.view.c;
import com.miui.securitycenter.R;
import d.a.b;
import d.a.e.k;
import d.a.f;
import d.a.l;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class PermissionUseTotalPreference extends Preference {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public PermissionTotalView f6537a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ImageView f6538b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ImageView f6539c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ImageView f6540d;
    /* access modifiers changed from: private */
    public ImageView e;
    /* access modifiers changed from: private */
    public ImageView f;
    /* access modifiers changed from: private */
    public ImageView g;
    private TextView h;
    private LinearLayout i;
    private LinearLayout j;
    /* access modifiers changed from: private */
    public LinearLayout k;
    /* access modifiers changed from: private */
    public LinearLayout l;
    /* access modifiers changed from: private */
    public FlashView m;
    private View.OnClickListener mClickListener;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public HashMap<Long, ArrayList<d>> n;
    /* access modifiers changed from: private */
    public WeakReference<Activity> o;
    /* access modifiers changed from: private */
    public long p;
    private boolean q;
    private ValueAnimator r;
    /* access modifiers changed from: private */
    public c s;

    private static class a extends k {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PermissionUseTotalPreference> f6541a;

        private a(PermissionUseTotalPreference permissionUseTotalPreference) {
            this.f6541a = new WeakReference<>(permissionUseTotalPreference);
        }

        /* synthetic */ a(PermissionUseTotalPreference permissionUseTotalPreference, f fVar) {
            this(permissionUseTotalPreference);
        }

        public void onComplete(Object obj) {
            PermissionUseTotalPreference permissionUseTotalPreference = (PermissionUseTotalPreference) this.f6541a.get();
            if (permissionUseTotalPreference != null) {
                permissionUseTotalPreference.d();
                permissionUseTotalPreference.k.setVisibility(8);
                permissionUseTotalPreference.l.setVisibility(8);
            }
        }
    }

    public PermissionUseTotalPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public PermissionUseTotalPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public PermissionUseTotalPreference(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public PermissionUseTotalPreference(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.p = -1;
        this.q = false;
        this.mClickListener = new h(this);
        this.mContext = context;
    }

    /* access modifiers changed from: private */
    public void a() {
        this.f6538b.setSelected(false);
        this.f6539c.setSelected(false);
        this.f6540d.setSelected(false);
        this.e.setSelected(false);
        this.f.setSelected(false);
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        ArrayList arrayList;
        ArrayList arrayList2 = new ArrayList();
        HashMap<Long, ArrayList<d>> hashMap = this.n;
        if (!(hashMap == null || (arrayList = hashMap.get(Long.valueOf(j2))) == null)) {
            int size = arrayList.size();
            if (size > 6) {
                size = 6;
            }
            for (int i2 = 0; i2 < size; i2++) {
                arrayList2.add(arrayList.get(i2));
            }
        }
        this.f6537a.setValues(arrayList2);
        this.f6537a.setPermissionType(j2);
    }

    /* access modifiers changed from: private */
    public void b() {
        this.m.setVisibility(8);
        this.m.b();
        this.j.setVisibility(0);
        try {
            f a2 = b.a(this.j);
            d.a.a.a aVar = new d.a.a.a();
            aVar.a(d.a.i.b.a(16, 160.0f));
            aVar.a(new a(this, (f) null));
            l visible = a2.visible();
            visible.setShowDelay(0);
            visible.a(1.0f, l.a.SHOW);
            visible.setHide();
            visible.b(aVar);
        } catch (Throwable unused) {
            Log.e("PermissionUseTotal", "not support folme");
            d();
            this.k.setVisibility(8);
            this.l.setVisibility(8);
        }
    }

    private void c() {
        this.r = ValueAnimator.ofInt(new int[]{0, 1});
        this.r.setDuration(800);
        this.r.setRepeatCount(1);
        this.r.addListener(new g(this));
        this.r.setStartDelay(300);
        this.r.start();
    }

    /* access modifiers changed from: private */
    public void d() {
        ValueAnimator valueAnimator = this.r;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void a(Activity activity) {
        this.o = new WeakReference<>(activity);
    }

    public void a(Context context) {
        this.s = c.a.a(context).a((View) this.i).a(context.getResources().getColor(R.color.color_black_trans_30)).a((c.b) new i(this)).a();
        this.s.c();
        com.miui.permcenter.settings.view.a aVar = new com.miui.permcenter.settings.view.a(getContext());
        aVar.setArrowMode(0);
        aVar.show(this.i, 0, 0);
        aVar.setOnDismissListener(new j(this));
    }

    public void a(boolean z) {
        e eVar = new e();
        eVar.a((e.a) new f(this, z));
        eVar.execute(new String[0]);
        if (!this.q) {
            this.q = true;
            c();
        }
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        a2.itemView.setBackgroundResource(R.drawable.pm_slogan_bg_color);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_44);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_40), dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_20));
        this.m = (FlashView) a2.itemView.findViewById(R.id.flash_view);
        this.g = (ImageView) a2.b((int) R.id.settings);
        this.f6537a = (PermissionTotalView) a2.b((int) R.id.permission_total_view);
        this.f6538b = (ImageView) a2.b((int) R.id.icon_location);
        this.f6539c = (ImageView) a2.b((int) R.id.icon_call);
        this.f6540d = (ImageView) a2.b((int) R.id.icon_contacts);
        this.e = (ImageView) a2.b((int) R.id.icon_record);
        this.f = (ImageView) a2.b((int) R.id.icon_storage);
        this.h = (TextView) a2.b((int) R.id.look_all);
        this.i = (LinearLayout) a2.b((int) R.id.root_view);
        this.j = (LinearLayout) a2.b((int) R.id.ll_icons_enable);
        this.k = (LinearLayout) a2.b((int) R.id.ll_icons_unable);
        this.l = (LinearLayout) a2.b((int) R.id.ll_icons_unable_select);
        this.g.setOnClickListener(this.mClickListener);
        this.f6538b.setOnClickListener(this.mClickListener);
        this.f6539c.setOnClickListener(this.mClickListener);
        this.f6540d.setOnClickListener(this.mClickListener);
        this.e.setOnClickListener(this.mClickListener);
        this.f.setOnClickListener(this.mClickListener);
        this.h.setOnClickListener(this.mClickListener);
        if (b.b.c.j.A.a()) {
            try {
                b.a(this.f6538b).touch().b(this.f6538b, new d.a.a.a[0]);
                b.a(this.f6539c).touch().b(this.f6539c, new d.a.a.a[0]);
                b.a(this.f6540d).touch().b(this.f6540d, new d.a.a.a[0]);
                b.a(this.e).touch().b(this.e, new d.a.a.a[0]);
                b.a(this.f).touch().b(this.f, new d.a.a.a[0]);
            } catch (Throwable unused) {
                Log.e("PermissionUseTotal", "not support folme");
            }
        }
        a(true);
        this.m.setImage(R.drawable.pm_setting_bg_empty_data);
    }

    public void onDetached() {
        super.onDetached();
        d();
        this.f6537a.b();
        FlashView flashView = this.m;
        if (flashView != null) {
            flashView.b();
        }
    }
}
