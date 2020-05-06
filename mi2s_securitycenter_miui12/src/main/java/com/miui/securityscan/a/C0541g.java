package com.miui.securityscan.a;

import android.content.Context;
import com.miui.common.card.models.ActivityCardModel;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.common.card.models.NewsCardModel;

/* renamed from: com.miui.securityscan.a.g  reason: case insensitive filesystem */
class C0541g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BaseCardModel f7584a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f7585b;

    C0541g(BaseCardModel baseCardModel, Context context) {
        this.f7584a = baseCardModel;
        this.f7585b = context;
    }

    public void run() {
        BaseCardModel baseCardModel = this.f7584a;
        if (baseCardModel instanceof AdvCardModel) {
            AdvCardModel advCardModel = (AdvCardModel) baseCardModel;
            G.G(advCardModel.isLocal() ? advCardModel.getDataId() : String.valueOf(advCardModel.getId()));
        } else if (baseCardModel instanceof NewsCardModel) {
            G.H(((NewsCardModel) baseCardModel).getDataId());
        } else if (baseCardModel instanceof ActivityCardModel) {
            G.F(((ActivityCardModel) baseCardModel).getDataId());
        } else if (baseCardModel instanceof FunctionCardModel) {
            G.b(this.f7585b, (FunctionCardModel) baseCardModel);
        }
    }
}
