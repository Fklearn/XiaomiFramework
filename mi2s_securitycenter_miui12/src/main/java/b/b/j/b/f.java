package b.b.j.b;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.TitleCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;

public class f extends TitleCardModel {

    public static class a extends BaseViewHolder {

        /* renamed from: a  reason: collision with root package name */
        private ImageView f1802a;

        /* renamed from: b  reason: collision with root package name */
        private TextView f1803b;

        public a(View view) {
            super(view);
            this.f1802a = (ImageView) view.findViewById(R.id.close);
            this.f1803b = (TextView) view.findViewById(R.id.title);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            f fVar = (f) baseCardModel;
            this.f1803b.setText(fVar.getTitle());
            this.f1802a.setOnClickListener(new e(this, fVar));
        }
    }

    public f() {
        super(R.layout.phone_manager_recommend_layout_title);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view);
    }

    public void onClick(View view) {
        if (R.id.close == view.getId()) {
            ((MainActivity) view.getContext()).a((BaseCardModel) this, getSubCardModelList());
        }
    }
}
