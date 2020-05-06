package com.miui.firstaidkit.b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class b extends BaseCardModel {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f3899a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f3900b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public boolean f3901c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f3902d;
    /* access modifiers changed from: private */
    public boolean e;

    public static class a extends BaseViewHolder {

        /* renamed from: a  reason: collision with root package name */
        Context f3903a;

        /* renamed from: b  reason: collision with root package name */
        LayoutInflater f3904b = LayoutInflater.from(this.f3903a);

        /* renamed from: c  reason: collision with root package name */
        LinearLayout f3905c;

        /* renamed from: d  reason: collision with root package name */
        LinearLayout f3906d;
        LinearLayout e;
        LinearLayout f;
        LinearLayout g;

        public a(View view) {
            super(view);
            this.f3903a = view.getContext();
            initView(view);
        }

        private void initView(View view) {
            this.f3905c = (LinearLayout) view.findViewById(R.id.ll1);
            this.f3906d = (LinearLayout) view.findViewById(R.id.ll2);
            this.e = (LinearLayout) view.findViewById(R.id.ll3);
            this.f = (LinearLayout) view.findViewById(R.id.ll4);
            this.g = (LinearLayout) view.findViewById(R.id.ll5);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            LinearLayout linearLayout;
            LinearLayout.LayoutParams layoutParams;
            super.fillData(view, baseCardModel, i);
            b bVar = (b) baseCardModel;
            ArrayList arrayList = new ArrayList();
            if (!bVar.f3899a) {
                arrayList.add(this.f3905c);
                this.f3905c.setVisibility(0);
            } else {
                this.f3905c.setVisibility(8);
            }
            if (!bVar.f3900b) {
                arrayList.add(this.f3906d);
                this.f3906d.setVisibility(0);
            } else {
                this.f3906d.setVisibility(8);
            }
            if (!bVar.f3901c) {
                arrayList.add(this.e);
                this.e.setVisibility(0);
            } else {
                this.e.setVisibility(8);
            }
            if (!bVar.f3902d) {
                arrayList.add(this.f);
                this.f.setVisibility(0);
            } else {
                this.f.setVisibility(8);
            }
            if (!bVar.e) {
                arrayList.add(this.g);
                this.g.setVisibility(0);
            } else {
                this.g.setVisibility(8);
            }
            if (arrayList.size() == 1) {
                linearLayout = (LinearLayout) arrayList.get(0);
                layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                layoutParams.topMargin = this.f3903a.getResources().getDimensionPixelSize(R.dimen.firstaidkit_item_padding_bottom);
            } else if (arrayList.size() > 1) {
                LinearLayout linearLayout2 = (LinearLayout) arrayList.get(0);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
                layoutParams2.topMargin = this.f3903a.getResources().getDimensionPixelSize(R.dimen.firstaidkit_item_padding_bottom);
                linearLayout2.setLayoutParams(layoutParams2);
                linearLayout = (LinearLayout) arrayList.get(arrayList.size() - 1);
                layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            } else {
                return;
            }
            layoutParams.bottomMargin = this.f3903a.getResources().getDimensionPixelSize(R.dimen.firstaidkit_item_padding_bottom);
            linearLayout.setLayoutParams(layoutParams);
        }
    }

    public b() {
        super(R.layout.firstaidkit_scanresult_card_layout_bottom);
    }

    public void a(boolean z) {
        this.f3902d = z;
    }

    public void b(boolean z) {
        this.f3900b = z;
    }

    public void c(boolean z) {
        this.f3901c = z;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view);
    }

    public void d(boolean z) {
        this.e = z;
    }

    public void e(boolean z) {
        this.f3899a = z;
    }

    public boolean validate() {
        return true;
    }
}
