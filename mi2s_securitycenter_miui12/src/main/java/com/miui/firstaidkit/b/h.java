package com.miui.firstaidkit.b;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import b.b.c.j.l;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.FirstAidKitActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.lang.ref.WeakReference;

public class h extends BaseCardModel {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public AbsModel f3924a;

    /* renamed from: b  reason: collision with root package name */
    private FirstAidKitActivity f3925b;

    private static class a extends BaseViewHolder {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f3926a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public WeakReference<FirstAidKitActivity> f3927b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f3928c;

        public a(View view, FirstAidKitActivity firstAidKitActivity) {
            super(view);
            this.f3926a = view.getContext();
            this.f3927b = new WeakReference<>(firstAidKitActivity);
            this.f3928c = (TextView) view.findViewById(R.id.ignore);
            this.f3928c.setVisibility(0);
            l.a(view);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            h hVar = (h) baseCardModel;
            this.titleView.setText(hVar.f3924a.getTitle());
            this.summaryView.setText(hVar.f3924a.getSummary());
            this.actionButton.setText(hVar.f3924a.getButtonTitle());
            e eVar = new e(this, hVar);
            this.actionButton.setOnClickListener(eVar);
            view.setOnClickListener(eVar);
            this.f3928c.setOnClickListener(new f(this, hVar, baseCardModel));
            view.setOnLongClickListener(new g(this, hVar, baseCardModel));
        }
    }

    public h() {
        super(R.layout.scanresult_card_layout_normal_new);
    }

    public AbsModel a() {
        return this.f3924a;
    }

    public void a(FirstAidKitActivity firstAidKitActivity) {
        this.f3925b = firstAidKitActivity;
    }

    public void a(AbsModel absModel) {
        this.f3924a = absModel;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view, this.f3925b);
    }

    public boolean validate() {
        return true;
    }
}
