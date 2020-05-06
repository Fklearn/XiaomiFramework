package com.miui.gamebooster.ui;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.c.a;
import b.b.c.c.b.d;
import b.c.a.b.f.c;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.model.C;
import com.miui.gamebooster.model.C0399e;
import com.miui.gamebooster.model.t;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.List;
import miui.R;
import miui.app.AlertDialog;

public class ib extends d implements View.OnClickListener, I.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public LinearLayout f5074a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ListView f5075b;

    /* renamed from: c  reason: collision with root package name */
    private c f5076c;

    /* renamed from: d  reason: collision with root package name */
    private View f5077d;
    private int e;
    private I f;
    private View g;
    /* access modifiers changed from: private */
    public final List<C0399e> h = new ArrayList();
    /* access modifiers changed from: private */
    public Activity i;
    private TextView j;

    /* access modifiers changed from: private */
    public void a(Context context) {
        C c2;
        int f2;
        ArrayList arrayList = new ArrayList();
        I i2 = this.f;
        if (i2 != null && i2.getCount() > 0) {
            int i3 = 0;
            for (int count = this.f.getCount() - 1; count >= 0; count--) {
                C0399e eVar = (C0399e) this.f.getItem(count);
                if ((eVar instanceof C) && (f2 = c2.f()) > 0) {
                    for (int i4 = f2 - 1; i4 >= 0; i4--) {
                        t tVar = (c2 = (C) eVar).g().get(i4);
                        if (tVar.i()) {
                            i3++;
                            C0378i.a(tVar);
                            C0378i.a(context, tVar);
                            c2.g().remove(tVar);
                        } else {
                            arrayList.add(0, tVar);
                        }
                    }
                }
            }
            List<C0399e> a2 = C0378i.a((List<t>) arrayList);
            if (a2 == null || a2.size() <= 0) {
                this.f.clear();
            } else {
                this.f.clear();
                this.f.addAll(a2);
            }
            this.f.notifyDataSetChanged();
            C0373d.b(f(), i3);
        }
    }

    private void b(Context context) {
        AlertDialog create = new AlertDialog.Builder(context, R.style.Theme_Dark_Dialog_Alert).setMessage(com.miui.securitycenter.R.string.gb_manual_record_dialog_del_video_message).setNegativeButton(context.getResources().getString(com.miui.securitycenter.R.string.cancel), new hb(this)).setPositiveButton(context.getResources().getString(com.miui.securitycenter.R.string.ok), new gb(this, context)).create();
        create.getWindow().setDimAmount(0.0f);
        create.getWindow().setType(2003);
        create.show();
    }

    private void c(boolean z) {
        ListView listView;
        float[] fArr;
        if (this.f5075b != null) {
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(com.miui.securitycenter.R.dimen.gb_wonderful_main_page_gird_margin_top) - getResources().getDimensionPixelOffset(com.miui.securitycenter.R.dimen.gb_wonderful_main_page_gird_margin_top_in_eidt);
            int i2 = 0;
            if (z) {
                listView = this.f5075b;
                fArr = new float[]{0.0f, (float) (-dimensionPixelOffset)};
            } else {
                listView = this.f5075b;
                fArr = new float[]{(float) (-dimensionPixelOffset), 0.0f};
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(listView, "translationY", fArr);
            ofFloat.setDuration(400);
            ofFloat.start();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.f5075b.getLayoutParams();
            int i3 = layoutParams.leftMargin;
            int i4 = layoutParams.topMargin;
            int i5 = layoutParams.rightMargin;
            if (z) {
                i2 = this.i.getResources().getDimensionPixelOffset(com.miui.securitycenter.R.dimen.gb_wonderful_main_page_bottom_function_height) - dimensionPixelOffset;
            }
            layoutParams.setMargins(i3, i4, i5, i2);
            this.f5075b.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    public String f() {
        return this.e == 0 ? "kpl" : "pubg";
    }

    private boolean g() {
        C c2;
        int f2;
        I i2 = this.f;
        if (i2 == null || i2.getCount() <= 0) {
            return false;
        }
        for (int count = this.f.getCount() - 1; count >= 0; count--) {
            C0399e eVar = (C0399e) this.f.getItem(count);
            if ((eVar instanceof C) && (f2 = c2.f()) > 0) {
                for (int i3 = f2 - 1; i3 >= 0; i3--) {
                    if ((c2 = (C) eVar).g().get(i3).i()) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean h() {
        return this.f.getCount() > 0;
    }

    private void i() {
        n.a().b(new fb(this));
    }

    /* access modifiers changed from: private */
    public void j() {
        TextView textView = this.j;
        if (textView != null) {
            textView.setEnabled(g());
        }
    }

    /* access modifiers changed from: private */
    public void k() {
        if (this.h.size() == 0) {
            this.f5074a.setVisibility(0);
            this.f5075b.setVisibility(8);
            return;
        }
        this.f5074a.setVisibility(8);
        this.f5075b.setVisibility(0);
        Activity activity = this.i;
        if (activity instanceof a) {
            this.f = new I(activity, this.h);
            this.f.a((I.a) this);
            this.f5075b.setAdapter(this.f);
        }
    }

    public void a(int i2) {
        j();
    }

    public void a(int i2, boolean z) {
        if (this.f5077d != null && (this.i instanceof a)) {
            if (z) {
                this.g.setVisibility(8);
                this.f5077d.setVisibility(0);
                this.f5077d.startAnimation(AnimationUtils.loadAnimation(this.i, com.miui.securitycenter.R.anim.gb_video_share_del_layout_in));
            } else {
                this.g.setVisibility(0);
                this.f5077d.startAnimation(AnimationUtils.loadAnimation(this.i, com.miui.securitycenter.R.anim.gb_video_share_del_layout_out));
                this.f5077d.setVisibility(8);
            }
            j();
            c(z);
        }
    }

    public void a(Object obj) {
        if (obj instanceof c) {
            this.f5076c = (c) obj;
        }
    }

    public void b(int i2, boolean z) {
        j();
    }

    public int e() {
        View childAt = this.f5075b.getChildAt(0);
        if (childAt == null) {
            return 0;
        }
        return (-childAt.getTop()) + (this.f5075b.getFirstVisiblePosition() * childAt.getHeight());
    }

    public void e(int i2) {
        this.e = i2;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.i = getActivity();
        this.g = findViewById(com.miui.securitycenter.R.id.tv_set);
        this.g.setOnClickListener(this);
        this.j = (TextView) findViewById(com.miui.securitycenter.R.id.btn_del);
        this.j.setOnClickListener(this);
        findViewById(com.miui.securitycenter.R.id.btn_cancel).setOnClickListener(this);
        this.f5077d = findViewById(com.miui.securitycenter.R.id.ll_share_del);
        this.f5074a = (LinearLayout) findViewById(com.miui.securitycenter.R.id.ll_no_data);
        this.f5075b = (ListView) findViewById(com.miui.securitycenter.R.id.listView);
        this.f5075b.setOnScrollListener(new db(this));
        i();
    }

    public void onClick(View view) {
        if (com.miui.securitycenter.R.id.btn_del == view.getId()) {
            b(this.mAppContext);
        } else if (com.miui.securitycenter.R.id.tv_set == view.getId()) {
            c cVar = this.f5076c;
            if (cVar != null) {
                cVar.b();
            }
        } else if (com.miui.securitycenter.R.id.btn_cancel == view.getId()) {
            I i2 = this.f;
            if (i2 != null) {
                i2.a(false);
                this.f.a();
                this.f.notifyDataSetChanged();
            }
            a(-1, false);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return com.miui.securitycenter.R.layout.gb_fragment_wonderful_moment;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
