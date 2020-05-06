package com.miui.permcenter;

import android.content.Intent;
import android.view.View;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainAcitivty f6097a;

    e(MainAcitivty mainAcitivty) {
        this.f6097a = mainAcitivty;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.permcenter.MainAcitivty] */
    public void onClick(View view) {
        Intent intent = new Intent(this.f6097a, SettingsAcitivty.class);
        intent.putExtra(Constants.System.EXTRA_SETTINGS_TITLE, this.f6097a.getString(R.string.activity_title_settings));
        intent.putExtra(":miui:starting_window_label", this.f6097a.getString(R.string.activity_title_settings));
        this.f6097a.startActivity(intent);
    }
}
