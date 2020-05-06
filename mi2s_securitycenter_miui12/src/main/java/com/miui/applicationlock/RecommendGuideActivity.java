package com.miui.applicationlock;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.c.a;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.o;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.powercenter.utils.b;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class RecommendGuideActivity extends a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public AlertDialog f3211a;

    /* renamed from: b  reason: collision with root package name */
    private String f3212b;

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, miui.app.Activity, com.miui.applicationlock.RecommendGuideActivity] */
    private void l() {
        int i;
        Resources resources;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.applock_recommend_guide_dialog, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.app_icon);
        TextView textView = (TextView) inflate.findViewById(R.id.app_name);
        TextView textView2 = (TextView) inflate.findViewById(R.id.recommend_msg);
        if (TextUtils.isEmpty(this.f3212b)) {
            imageView.setImageBitmap(b.a("com.miui.securitycenter"));
            textView.setText(b.a((Context) this, "com.miui.securitycenter"));
            resources = getResources();
            i = R.string.ac_recommend_guide_dialog_content;
        } else {
            imageView.setImageBitmap(b.a(this.f3212b));
            textView.setText(b.a((Context) this, this.f3212b));
            resources = getResources();
            i = R.string.ac_recommend_guide_dialog_app_content;
        }
        textView2.setText(resources.getString(i));
        builder.setView(inflate);
        if (o.k() > 0) {
            builder.setCheckBox(false, getResources().getString(R.string.ac_recommend_guide_dialog_ignore));
        }
        builder.setOnDismissListener(new Ea(this));
        builder.setNegativeButton(R.string.cancel, new Fa(this));
        builder.setPositiveButton(R.string.ac_recommend_guide_dialog_open, new Ga(this));
        this.f3211a = builder.create();
        this.f3211a.setCanceledOnTouchOutside(false);
        this.f3211a.getWindow().getDecorView().setHapticFeedbackEnabled(false);
        if (!isFinishing()) {
            this.f3211a.show();
            h.j();
            o.a(System.currentTimeMillis());
            o.e(o.k() + 1);
            o.d("");
            o.e(false);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.applicationlock.RecommendGuideActivity] */
    /* access modifiers changed from: private */
    public void m() {
        Intent intent = new Intent(this, TransitionHelper.class);
        intent.putExtra(AnimatedTarget.STATE_TAG_FROM, "AlarmReceiver");
        intent.putExtra("enter_way", "000013");
        intent.putExtra("external_app_name", this.f3212b);
        intent.addFlags(268435456);
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        this.f3212b = extras.getString("packageName");
        l();
    }
}
