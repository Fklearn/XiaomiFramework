package com.miui.powercenter.autotask;

import android.content.DialogInterface;
import com.miui.powercenter.autotask.ChooseConditionActivity;

class I implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseConditionActivity.a f6713a;

    I(ChooseConditionActivity.a aVar) {
        this.f6713a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (-1 == i) {
            this.f6713a.getActivity().finish();
        }
    }
}
