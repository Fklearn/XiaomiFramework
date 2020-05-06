package com.miui.permcenter.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;
import b.b.b.d.k;
import b.b.c.c.a;
import b.b.c.j.e;
import com.miui.securitycenter.R;
import java.util.Locale;

public class PrivacyProvisionActivity extends a implements View.OnClickListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public VideoView f6508a;

    public static boolean a(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }

    public void onBackPressed() {
        setResult(0);
        PrivacyProvisionActivity.super.onBackPressed();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.settings.PrivacyProvisionActivity] */
    public void onClick(View view) {
        Intent intent;
        int i;
        switch (view.getId()) {
            case R.id.privacy_more /*2131297480*/:
                if (e.b() < 10) {
                    intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://privacy.miui.com"));
                } else {
                    intent = new Intent("miui.intent.action.VIEW_LICENSE");
                    if (!a(this, intent)) {
                        intent.setAction("android.intent.action.VIEW_LICENSE");
                    }
                    intent.putExtra("android.intent.extra.LICENSE_TYPE", 18);
                }
                startActivity(intent);
                return;
            case R.id.setup_btn_back /*2131297653*/:
                i = 0;
                break;
            case R.id.setup_next /*2131297654*/:
                i = -1;
                break;
            default:
                return;
        }
        setResult(i);
        finish();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.permcenter.settings.PrivacyProvisionActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_setting_provision);
        TextView textView = (TextView) findViewById(R.id.privacy_more);
        this.f6508a = (VideoView) findViewById(R.id.video_view);
        ((TextView) findViewById(R.id.setup_btn_back)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setup_next)).setOnClickListener(this);
        textView.setOnClickListener(this);
        String locale = Locale.getDefault().toString();
        if (k.a(this) && "zh_CN".equals(locale)) {
            textView.setVisibility(0);
        }
        this.f6508a.setVideoPath(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.privacy_provision_video).toString());
        this.f6508a.setBackgroundColor(-1);
        if (Build.VERSION.SDK_INT >= 26) {
            this.f6508a.setAudioFocusRequest(0);
        }
        this.f6508a.setOnClickListener(new u(this));
        this.f6508a.setImportantForAccessibility(2);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PrivacyProvisionActivity.super.onDestroy();
        VideoView videoView = this.f6508a;
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        VideoView videoView = this.f6508a;
        if (videoView != null) {
            videoView.pause();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        VideoView videoView = this.f6508a;
        if (videoView != null) {
            videoView.setOnPreparedListener(new w(this));
            this.f6508a.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        PrivacyProvisionActivity.super.onStop();
        VideoView videoView = this.f6508a;
        if (videoView != null) {
            videoView.setBackgroundColor(getResources().getColor(R.color.applock_guide_anim_bg_color));
        }
    }
}
