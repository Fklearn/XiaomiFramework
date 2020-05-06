package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.c.a;
import b.b.c.j.r;
import b.c.a.b.d;
import b.c.a.b.d.d;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0378i;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.Q;
import com.miui.gamebooster.m.fa;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.t;
import com.miui.securitycenter.R;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class GameVideoActivity extends a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private LinearLayout f4904a;

    /* renamed from: b  reason: collision with root package name */
    private d f4905b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f4906c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f4907d;
    /* access modifiers changed from: private */
    public List<t> e = new ArrayList();

    public GameVideoActivity() {
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(false);
        aVar.c((int) R.drawable.gb_wonderful_video_loading_no_corners);
        aVar.a(Bitmap.Config.RGB_565);
        aVar.c(true);
        this.f4905b = aVar.a();
    }

    /* access modifiers changed from: private */
    public String a(t tVar) {
        if (!TextUtils.isEmpty(tVar.c()) && C0382m.a(tVar.c()) > 0) {
            return tVar.c();
        }
        if (TextUtils.isEmpty(tVar.g()) || C0382m.a(tVar.g()) <= 0) {
            return null;
        }
        return tVar.g();
    }

    private void a(View view, t tVar) {
        if (view != null && tVar != null) {
            TextView textView = (TextView) view.findViewById(R.id.tv_duration);
            TextView textView2 = (TextView) view.findViewById(R.id.btn_save);
            a(textView2, TextUtils.isEmpty(tVar.g()) || C0382m.a(tVar.g()) == 0);
            textView.setText(tVar.a());
            r.a(d.a.VIDEO_FILE.c(a(tVar)), (ImageView) view.findViewById(R.id.iv_video), this.f4905b);
            view.setOnClickListener(new C0442oa(this, tVar));
            view.findViewById(R.id.btn_play).setOnClickListener(new C0444pa(this, tVar));
            textView2.setOnClickListener(new C0446qa(this, tVar));
        }
    }

    /* access modifiers changed from: private */
    public void a(TextView textView, t tVar) {
        n.a().b(new C0449sa(this, tVar, textView));
    }

    /* access modifiers changed from: private */
    public void a(TextView textView, boolean z) {
        if (textView != null) {
            textView.setEnabled(z);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, z ? R.drawable.ic_gb_wonderful_video_download_normal : R.drawable.ic_gb_wonderful_video_download_press, 0);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity, android.view.View$OnClickListener, miui.app.Activity] */
    private void initView() {
        findViewById(R.id.btn_close).setOnClickListener(this);
        this.f4904a = (LinearLayout) findViewById(R.id.container);
        this.f4907d = getIntent().getStringExtra("match_md5");
        this.f4906c = C0378i.b((Context) this, this.f4907d);
    }

    private void l() {
        n.a().b(new C0440na(this));
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void m() {
        LinearLayout linearLayout = this.f4904a;
        if (linearLayout != null) {
            linearLayout.removeAllViews();
            List<t> list = this.e;
            if (list != null && list.size() > 0) {
                for (int i = 0; i < this.e.size(); i++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                    layoutParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_video_item_margin);
                    t tVar = this.e.get(i);
                    if (tVar != null) {
                        View inflate = LayoutInflater.from(this).inflate(R.layout.gb_wonderful_video_item, (ViewGroup) null);
                        a(inflate, tVar);
                        this.f4904a.addView(inflate, layoutParams);
                    }
                }
            }
        }
    }

    public void onClick(View view) {
        if (R.id.btn_close == view.getId()) {
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, com.miui.gamebooster.ui.GameVideoActivity, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(R.style.GBWonderfulVideo);
        if (Build.IS_INTERNATIONAL_BUILD || !C0388t.t()) {
            finish();
            return;
        }
        super.onCreate(bundle);
        Q.a((Activity) this);
        setContentView(R.layout.gb_activity_wonderful_video);
        na.a((Activity) this);
        fa.a(this);
        initView();
        l();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        C0373d.b();
    }
}
