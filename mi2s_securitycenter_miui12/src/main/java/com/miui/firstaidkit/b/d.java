package com.miui.firstaidkit.b;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.l;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.FirstAidKitActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.lang.ref.WeakReference;
import java.util.List;

public class d extends BaseCardModel {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public String f3909a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public List<AbsModel> f3910b;

    /* renamed from: c  reason: collision with root package name */
    private FirstAidKitActivity f3911c;

    public static class a extends BaseViewHolder {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public WeakReference<FirstAidKitActivity> f3912a;

        /* renamed from: b  reason: collision with root package name */
        private ImageView f3913b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f3914c;

        /* renamed from: d  reason: collision with root package name */
        private TextView f3915d;
        private Button e;

        public a(View view, FirstAidKitActivity firstAidKitActivity) {
            super(view);
            this.f3912a = new WeakReference<>(firstAidKitActivity);
            initView(view);
            l.a(view);
        }

        private void initView(View view) {
            this.f3913b = (ImageView) view.findViewById(R.id.tv_icon);
            this.f3914c = (TextView) view.findViewById(R.id.tv_title);
            this.f3915d = (TextView) view.findViewById(R.id.tv_summary);
            this.e = (Button) view.findViewById(R.id.btn_action);
            this.f3913b.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            d dVar = (d) baseCardModel;
            List a2 = dVar.f3910b;
            if (a2 != null && !a2.isEmpty()) {
                AbsModel absModel = (AbsModel) a2.get(0);
                r.a(dVar.getIcon(), this.f3913b, r.g, (int) R.drawable.card_icon_default);
                this.f3914c.setText(dVar.f3909a);
                this.f3915d.setText(absModel.getTitle());
                this.e.setText(absModel.getButtonTitle());
                c cVar = new c(this, absModel);
                this.e.setOnClickListener(cVar);
                view.setOnClickListener(cVar);
            }
        }
    }

    public d() {
        super(R.layout.firstaidkit_scanresult_card_layout_feedback);
    }

    public void a(FirstAidKitActivity firstAidKitActivity) {
        this.f3911c = firstAidKitActivity;
    }

    public void a(String str) {
        this.f3909a = str;
    }

    public void a(List<AbsModel> list) {
        this.f3910b = list;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view, this.f3911c);
    }

    public boolean validate() {
        return true;
    }
}
