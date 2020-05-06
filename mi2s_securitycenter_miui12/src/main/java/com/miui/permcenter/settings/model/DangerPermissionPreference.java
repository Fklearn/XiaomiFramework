package com.miui.permcenter.settings.model;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.permcenter.settings.model.d;
import com.miui.securitycenter.R;
import d.a.a.a;
import d.a.b;
import d.a.j;

public class DangerPermissionPreference extends Preference {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public TextView f6533a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f6534b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f6535c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextView f6536d;
    /* access modifiers changed from: private */
    public TextView e;
    private LinearLayout f;
    private LinearLayout g;
    private LinearLayout h;
    private LinearLayout i;
    private LinearLayout j;
    private Button k;
    private View.OnClickListener mClickListener;
    /* access modifiers changed from: private */
    public Context mContext;

    public DangerPermissionPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public DangerPermissionPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public DangerPermissionPreference(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.mClickListener = new c(this);
        this.mContext = context;
    }

    public void a() {
        d dVar = new d();
        dVar.a((d.a) new b(this));
        dVar.execute(new String[0]);
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        a2.itemView.setBackgroundResource(R.drawable.pm_slogan_bg_color);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_44);
        ((RecyclerView.h) a2.itemView.getLayoutParams()).setMargins(dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_40), dimensionPixelSize, this.mContext.getResources().getDimensionPixelSize(R.dimen.view_dimen_20));
        this.f6533a = (TextView) a2.b((int) R.id.summary_location);
        this.f6534b = (TextView) a2.b((int) R.id.summary_contacts);
        this.f6535c = (TextView) a2.b((int) R.id.summary_call);
        this.f6536d = (TextView) a2.b((int) R.id.summary_record);
        this.e = (TextView) a2.b((int) R.id.summary_storage);
        this.f = (LinearLayout) a2.b((int) R.id.container_location);
        this.g = (LinearLayout) a2.b((int) R.id.container_contacts);
        this.h = (LinearLayout) a2.b((int) R.id.container_call);
        this.i = (LinearLayout) a2.b((int) R.id.container_record);
        this.j = (LinearLayout) a2.b((int) R.id.container_storage);
        this.k = (Button) a2.b((int) R.id.look_all);
        this.f.setOnClickListener(this.mClickListener);
        this.g.setOnClickListener(this.mClickListener);
        this.h.setOnClickListener(this.mClickListener);
        this.i.setOnClickListener(this.mClickListener);
        this.j.setOnClickListener(this.mClickListener);
        this.k.setOnClickListener(this.mClickListener);
        if (b.b.c.j.A.a()) {
            try {
                j jVar = b.a(this.f).touch();
                jVar.a(1.0f, j.a.DOWN);
                jVar.b(this.f, new a[0]);
                j jVar2 = b.a(this.g).touch();
                jVar2.a(1.0f, j.a.DOWN);
                jVar2.b(this.g, new a[0]);
                j jVar3 = b.a(this.h).touch();
                jVar3.a(1.0f, j.a.DOWN);
                jVar3.b(this.h, new a[0]);
                j jVar4 = b.a(this.i).touch();
                jVar4.a(1.0f, j.a.DOWN);
                jVar4.b(this.i, new a[0]);
                j jVar5 = b.a(this.j).touch();
                jVar5.a(1.0f, j.a.DOWN);
                jVar5.b(this.j, new a[0]);
            } catch (Throwable unused) {
                Log.e("DangerPermission", "not support folme");
            }
        }
        a();
    }
}
