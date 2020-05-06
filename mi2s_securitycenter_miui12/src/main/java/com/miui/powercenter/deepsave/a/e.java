package com.miui.powercenter.deepsave.a;

import android.text.TextUtils;
import android.view.View;
import com.miui.powercenter.a.a;
import com.miui.powercenter.deepsave.IdeaModel;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f7010a;

    e(f fVar) {
        this.f7010a = fVar;
    }

    public void onClick(View view) {
        if (view.getTag() == null) {
            this.f7010a.a(view.getContext());
        } else {
            IdeaModel ideaModel = (IdeaModel) view.getTag();
            if (!TextUtils.isEmpty(ideaModel.url)) {
                this.f7010a.a(ideaModel, view.getContext());
            }
        }
        a.d("save_idea");
    }
}
