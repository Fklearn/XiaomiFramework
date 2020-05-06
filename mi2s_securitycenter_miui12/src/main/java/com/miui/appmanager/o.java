package com.miui.appmanager;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;

class o extends ClickableSpan {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3683a;

    o(AppManagerMainActivity appManagerMainActivity) {
        this.f3683a = appManagerMainActivity;
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    public void onClick(View view) {
        this.f3683a.a(false);
        c.a((Context) this.f3683a, (int) R.string.app_manager_show_all_apps_opened);
        AppManagerMainActivity appManagerMainActivity = this.f3683a;
        appManagerMainActivity.d(appManagerMainActivity.sa.getSearchInput().getText().toString());
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setColor(this.f3683a.getResources().getColor(R.color.app_manager_search_prompt_text_color));
    }
}
