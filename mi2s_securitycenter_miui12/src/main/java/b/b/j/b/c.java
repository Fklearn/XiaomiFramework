package b.b.j.b;

import android.text.TextUtils;
import android.view.View;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.TitleCardModel;
import com.miui.securitycenter.R;

public class c extends TitleCardModel {

    public static class a extends BaseViewHolder {

        /* renamed from: a  reason: collision with root package name */
        private View f1799a;

        public a(View view) {
            super(view);
            this.f1799a = view.findViewById(R.id.line);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            c cVar = (c) baseCardModel;
            int i2 = 0;
            boolean z = !TextUtils.isEmpty(cVar.summary) && cVar.subVisible;
            this.summaryView.setVisibility(z ? 0 : 8);
            View view2 = this.f1799a;
            if (!z) {
                i2 = 8;
            }
            view2.setVisibility(i2);
        }
    }

    public c() {
        super(R.layout.phone_manager_card_layout_list_title);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view);
    }

    public boolean validate() {
        return true;
    }
}
