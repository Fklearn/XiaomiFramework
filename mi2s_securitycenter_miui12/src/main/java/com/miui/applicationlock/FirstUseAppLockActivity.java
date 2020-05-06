package com.miui.applicationlock;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.widget.r;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import miui.os.Build;
import miui.security.SecurityManager;

public class FirstUseAppLockActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    private C0259c f3170a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public VideoView f3171b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public r f3172c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextView f3173d;
    private TextView e;
    private TextView f;
    private String g;
    private RelativeLayout h;
    private String i;

    private static class a implements LoaderManager.LoaderCallbacks<ArrayList<C0257a>> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstUseAppLockActivity> f3174a;

        private a(FirstUseAppLockActivity firstUseAppLockActivity) {
            this.f3174a = new WeakReference<>(firstUseAppLockActivity);
        }

        /* synthetic */ a(FirstUseAppLockActivity firstUseAppLockActivity, C0301ta taVar) {
            this(firstUseAppLockActivity);
        }

        /* renamed from: a */
        public void onLoadFinished(Loader<ArrayList<C0257a>> loader, ArrayList<C0257a> arrayList) {
            FirstUseAppLockActivity firstUseAppLockActivity = (FirstUseAppLockActivity) this.f3174a.get();
            if (firstUseAppLockActivity != null) {
                int size = arrayList.size();
                firstUseAppLockActivity.f3173d.setText(Html.fromHtml(firstUseAppLockActivity.getResources().getQuantityString(R.plurals.applock_guide_app_count_tips, size, new Object[]{"<font color=\"#0099ff\">" + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(size)}) + "</font>"})));
            }
        }

        /* JADX WARNING: type inference failed for: r1v3, types: [android.content.Context, com.miui.applicationlock.FirstUseAppLockActivity] */
        public Loader<ArrayList<C0257a>> onCreateLoader(int i, Bundle bundle) {
            ? r1 = (FirstUseAppLockActivity) this.f3174a.get();
            if (r1 == 0) {
                return null;
            }
            return new C0313ya(this, r1, r1);
        }

        public void onLoaderReset(Loader<ArrayList<C0257a>> loader) {
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.applicationlock.FirstUseAppLockActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void l() {
        Intent intent;
        if (!this.f3170a.d()) {
            intent = new Intent(this, LockChooseAccessControl.class);
            intent.putExtra("extra_data", "forbide");
            if (!TextUtils.isEmpty(this.g)) {
                intent.putExtra("external_app_name", this.g);
            }
        } else {
            intent = new Intent(this, ConfirmAccessControl.class);
            intent.putExtra("extra_data", "HappyCoding");
        }
        startActivityForResult(intent, 1022101);
    }

    private void m() {
        if ("AlarmReceiver".equals(this.i)) {
            o.f(true);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.FirstUseAppLockActivity, miui.app.Activity] */
    private void n() {
        if ("AlarmReceiver".equals(this.i)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(67108864);
            startActivity(intent);
        }
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        FirstUseAppLockActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 1022101) {
            if (i3 == -1) {
                SecurityManager securityManager = (SecurityManager) getSystemService("security");
                if (!TextUtils.isEmpty(this.g)) {
                    securityManager.setApplicationAccessControlEnabled(this.g, true);
                }
                if (intent != null) {
                    startActivity(intent);
                    setResult(-1);
                }
            }
            finish();
        }
    }

    public void onBackPressed() {
        n();
        finish();
        h.k(TtmlNode.LEFT);
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [b.b.c.c.a, android.content.Context, com.miui.applicationlock.FirstUseAppLockActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i2;
        TextView textView;
        StringBuilder sb;
        int i3;
        RelativeLayout relativeLayout;
        View view;
        int i4;
        TextView textView2;
        int i5;
        TextView textView3;
        super.onCreate(bundle);
        setContentView(R.layout.activity_lock);
        if (k.a() >= 10) {
            getActionBar().setExpandState(0);
        }
        this.f3170a = C0259c.b(getApplicationContext());
        this.g = getIntent().getStringExtra("external_app_name");
        boolean j = this.f3170a.j();
        if (!this.f3170a.d() || j) {
            String stringExtra = getIntent().getStringExtra("extra_enterway");
            if (stringExtra != null) {
                h.d(stringExtra);
            }
            getLoaderManager().initLoader(110, (Bundle) null, new a(this, (C0301ta) null));
            Button button = (Button) findViewById(R.id.btn_lock);
            this.f3173d = (TextView) findViewById(R.id.tv_app_count);
            this.e = (TextView) findViewById(R.id.tv_guide_tip1);
            this.f = (TextView) findViewById(R.id.tv_guide_tip3);
            this.h = (RelativeLayout) findViewById(R.id.top_layout);
            boolean d2 = E.a((Context) this).d();
            boolean c2 = C.a(getApplicationContext()).c();
            if (Build.IS_INTERNATIONAL_BUILD) {
                if (d2 && c2) {
                    textView3 = this.e;
                    i5 = R.string.applock_guide_tip1_global;
                } else if (d2) {
                    textView3 = this.e;
                    i5 = R.string.applock_guide_tip1_global_without_face;
                } else if (c2) {
                    textView3 = this.e;
                    i5 = R.string.applock_guide_tip1_global_without_finger;
                } else {
                    textView3 = this.e;
                    i5 = R.string.applock_guide_tip1_global_without_finger_face;
                }
                textView3.setText(i5);
                textView = this.f;
                i2 = R.string.applock_guide_tip3_global;
            } else {
                if (d2 && c2) {
                    textView2 = this.e;
                    i4 = R.string.applock_guide_tip1;
                } else if (d2) {
                    textView2 = this.e;
                    i4 = R.string.applock_guide_tip1_without_face;
                } else if (c2) {
                    textView2 = this.e;
                    i4 = R.string.applock_guide_tip1_without_finger;
                } else {
                    textView2 = this.e;
                    i4 = R.string.applock_guide_tip1_without_finger_face;
                }
                textView2.setText(i4);
                textView = this.f;
                i2 = R.string.applock_guide_tip3;
            }
            textView.setText(i2);
            this.i = getIntent().getStringExtra(AnimatedTarget.STATE_TAG_FROM);
            m();
            button.setOnClickListener(new C0301ta(this));
            if (isDarkModeEnable()) {
                sb = new StringBuilder();
                sb.append("android.resource://");
                sb.append(getPackageName());
                sb.append("/");
                i3 = R.raw.applock_guide_dark;
            } else {
                sb = new StringBuilder();
                sb.append("android.resource://");
                sb.append(getPackageName());
                sb.append("/");
                i3 = R.raw.applock_guide;
            }
            sb.append(i3);
            String uri = Uri.parse(sb.toString()).toString();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.applock_guide_anim_width), -2);
            layoutParams.addRule(13, -1);
            if (Build.VERSION.SDK_INT >= 26) {
                this.f3171b = new VideoView(this);
                this.f3171b.setLayoutParams(layoutParams);
                this.f3171b.setVideoPath(uri);
                this.f3171b.setBackgroundColor(getResources().getColor(R.color.applock_guide_anim_bg_color));
                this.f3171b.setAudioFocusRequest(0);
                relativeLayout = this.h;
                view = this.f3171b;
            } else {
                this.f3172c = new r(this);
                this.f3172c.setLayoutParams(layoutParams);
                this.f3172c.setBackgroundColor(getResources().getColor(R.color.applock_guide_anim_bg_color));
                this.f3172c.setVideoPath(uri);
                relativeLayout = this.h;
                view = this.f3172c;
            }
            relativeLayout.addView(view, 0);
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        FirstUseAppLockActivity.super.onDestroy();
        VideoView videoView = this.f3171b;
        if (videoView != null) {
            videoView.stopPlayback();
        }
        r rVar = this.f3172c;
        if (rVar != null) {
            rVar.a();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            n();
            h.k(TtmlNode.LEFT);
            finish();
        }
        return FirstUseAppLockActivity.super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        VideoView videoView = this.f3171b;
        if (videoView != null) {
            videoView.pause();
        }
        r rVar = this.f3172c;
        if (rVar != null) {
            rVar.pause();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        VideoView videoView = this.f3171b;
        if (videoView != null) {
            videoView.setOnPreparedListener(new C0305va(this));
            this.f3171b.start();
        }
        r rVar = this.f3172c;
        if (rVar != null) {
            rVar.setOnPreparedListener(new C0311xa(this));
            this.f3172c.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        FirstUseAppLockActivity.super.onStop();
        VideoView videoView = this.f3171b;
        if (videoView != null) {
            videoView.setBackgroundColor(getResources().getColor(R.color.applock_guide_anim_bg_color));
        }
        r rVar = this.f3172c;
        if (rVar != null) {
            rVar.setBackgroundColor(getResources().getColor(R.color.applock_guide_anim_bg_color));
        }
    }
}
