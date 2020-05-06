package com.miui.common.card.functions;

import android.view.View;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.O;

public class OptimizeFunction extends BaseFunction {
    private O.a amoListener;
    private AbsModel curModel;

    public OptimizeFunction(O.a aVar, AbsModel absModel) {
        this.amoListener = aVar;
        this.curModel = absModel;
    }

    public void onClick(View view) {
        O.a aVar;
        AbsModel absModel = this.curModel;
        if (absModel != null && (aVar = this.amoListener) != null) {
            aVar.b(absModel);
            this.amoListener.a(this.curModel.getScore(), this.curModel.isDelayOptimized());
        }
    }

    public void onNegativeButtonClick() {
        O.a aVar;
        AbsModel absModel = this.curModel;
        if (absModel != null && (aVar = this.amoListener) != null) {
            aVar.a(absModel);
        }
    }
}
