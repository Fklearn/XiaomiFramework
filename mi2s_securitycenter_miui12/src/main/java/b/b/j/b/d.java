package b.b.j.b;

import android.view.View;
import android.widget.TextView;
import b.b.j.b.h;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.List;

public class d extends h {

    public static class a extends h.a {
        private TextView A = ((TextView) this.f1809a.findViewById(R.id.summary));
        private TextView B = ((TextView) this.e.findViewById(R.id.summary));
        private TextView C = ((TextView) this.i.findViewById(R.id.summary));
        private TextView D = ((TextView) this.m.findViewById(R.id.summary));
        private TextView[] E = {this.A, this.B, this.C, this.D};

        public a(View view) {
            super(view);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            List<GridFunctionData> gridFunctionDataList = ((h) baseCardModel).getGridFunctionDataList();
            if (gridFunctionDataList != null && !gridFunctionDataList.isEmpty()) {
                for (int i2 = 0; i2 < this.E.length; i2++) {
                    if (i2 < gridFunctionDataList.size()) {
                        GridFunctionData gridFunctionData = gridFunctionDataList.get(i2);
                        this.s[i2].setText(gridFunctionData.getSummary());
                        this.E[i2].setText(gridFunctionData.getTitle());
                    }
                }
            }
        }
    }

    public d() {
        this((AbsModel) null);
    }

    public d(AbsModel absModel) {
        super(R.layout.phone_manage_recommend_item_card, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new a(view);
    }
}
