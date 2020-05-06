package com.miui.securityscan.a;

import com.miui.common.card.models.ActivityCardModel;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.common.card.models.ListTitleCheckboxCardModel;
import com.miui.common.card.models.ListTitleConsumePowerRankCardModel;
import com.miui.common.card.models.ListTitleFlowRankCardModel;
import com.miui.common.card.models.NewsCardModel;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import java.util.List;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BaseCardModel f7586a;

    h(BaseCardModel baseCardModel) {
        this.f7586a = baseCardModel;
    }

    public void run() {
        AbsModel absModel;
        List<AbsModel> modelList;
        BaseCardModel baseCardModel = this.f7586a;
        if (baseCardModel instanceof AdvCardModel) {
            AdvCardModel advCardModel = (AdvCardModel) baseCardModel;
            G.C(advCardModel.isLocal() ? advCardModel.getDataId() : String.valueOf(advCardModel.getId()));
        } else if (baseCardModel instanceof NewsCardModel) {
            G.E(((NewsCardModel) baseCardModel).getDataId());
        } else if (baseCardModel instanceof ActivityCardModel) {
            G.B(((ActivityCardModel) baseCardModel).getDataId());
        } else {
            if (baseCardModel instanceof FunctionCardModel) {
                absModel = ((FunctionCardModel) baseCardModel).getCurModel();
                if (absModel == null) {
                    return;
                }
            } else if (baseCardModel instanceof ListTitleCheckboxCardModel) {
                GroupModel group = ((ListTitleCheckboxCardModel) baseCardModel).getGroup();
                if (group != null && (modelList = group.getModelList()) != null) {
                    G.b(modelList);
                    return;
                }
                return;
            } else if (baseCardModel instanceof ListTitleConsumePowerRankCardModel) {
                absModel = ((ListTitleConsumePowerRankCardModel) baseCardModel).getCurModel();
                if (absModel == null) {
                    return;
                }
            } else if (!(baseCardModel instanceof ListTitleFlowRankCardModel) || (absModel = ((ListTitleFlowRankCardModel) baseCardModel).getCurModel()) == null) {
                return;
            }
            G.D(absModel.getTrackStr());
        }
    }
}
