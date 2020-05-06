package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import b.b.c.c.b.d;
import b.b.o.g.e;
import com.miui.gamebooster.e.c;
import com.miui.gamebooster.m.C0370a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.view.SeekBarLinearLayout;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.FourSwitchSelector;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.g;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import miui.app.AlertDialog;
import miui.util.IOUtils;

public class AdvancedSettingsDetailFragment extends d implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBarLinearLayout.a, q, FourSwitchSelector.a {

    /* renamed from: a  reason: collision with root package name */
    private static final float[] f4845a = {0.0f, 0.333f, 0.666f, 1.0f};
    private FourSwitchSelector A;
    private FourSwitchSelector B;

    /* renamed from: b  reason: collision with root package name */
    private SeekBar f4846b;

    /* renamed from: c  reason: collision with root package name */
    private SeekBar f4847c;

    /* renamed from: d  reason: collision with root package name */
    private int f4848d = -1;
    private int e = -1;
    private int f = -1;
    private int g = -1;
    private CompoundButton h;
    private List<String> i = new ArrayList();
    private String j;
    private int k = -1;
    private SeekBar l;
    private SeekBar m;
    private AlertDialog n;
    private int o;
    private int p;
    private int q;
    private boolean r;
    private View s;
    private ImageView t;
    private ImageView u;
    private LinearLayout v;
    private LinearLayout w;
    private View x;
    private r y;
    private String z;

    private int a(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress % 100 >= 50) {
            progress += 50;
        }
        int i2 = progress / 100;
        seekBar.setProgress(i2 * 100);
        return i2;
    }

    public static AdvancedSettingsDetailFragment a(String str, String str2, int i2) {
        AdvancedSettingsDetailFragment advancedSettingsDetailFragment = new AdvancedSettingsDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("label", str);
        bundle.putString("pkg", str2);
        bundle.putInt("pkg_uid", i2);
        advancedSettingsDetailFragment.setArguments(bundle);
        return advancedSettingsDetailFragment;
    }

    private void a(SeekBar seekBar, int i2) {
        if (Build.VERSION.SDK_INT >= 28) {
            Class<AbsSeekBar> cls = AbsSeekBar.class;
            try {
                e.a((Class<? extends Object>) cls, (Object) seekBar, "setMin", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i2));
            } catch (Exception e2) {
                Log.e("AdvanceSettingsDetail", e2.toString());
            }
        }
    }

    private static int e(int i2) {
        if (i2 == 1) {
            return 1;
        }
        if (i2 != 2) {
            return i2 != 3 ? 0 : 3;
        }
        return 2;
    }

    private void e() {
        Activity activity = getActivity();
        if (activity != null) {
            this.n = g.a(activity, getString(R.string.gb_advance_settings_reset_dialog_title), getString(R.string.gb_advance_settings_reset_dialog_content), getString(17039370), getString(17039360), new C0427h(this), new C0429i(this));
        }
    }

    private static int f(int i2) {
        if (i2 == 1) {
            return 1;
        }
        if (i2 != 2) {
            return i2 != 3 ? 0 : 3;
        }
        return 2;
    }

    private void f() {
        g();
        this.h.setChecked(this.r);
        if (C0388t.q()) {
            this.o = C0370a.a().a(C0370a.f4470c);
            int b2 = C0370a.a().b(C0370a.f4470c);
            int c2 = C0370a.a().c(C0370a.f4470c);
            if (c2 < b2) {
                a(this.f4846b, c2);
            }
            this.f4846b.setMax(b2);
            SeekBar seekBar = this.f4846b;
            int i2 = this.f4848d;
            if (i2 == -1) {
                i2 = this.o;
            }
            seekBar.setProgress(i2);
            this.p = C0370a.a().a(C0370a.f4471d);
            int b3 = C0370a.a().b(C0370a.f4471d);
            int c3 = C0370a.a().c(C0370a.f4471d);
            if (c3 < b3) {
                a(this.f4847c, c3);
            }
            this.f4847c.setMax(b3);
            SeekBar seekBar2 = this.f4847c;
            int i3 = this.e;
            if (i3 == -1) {
                i3 = this.p;
            }
            seekBar2.setProgress(i3);
            this.q = C0370a.a().a(C0370a.e);
            int i4 = this.f;
            if (i4 == -1) {
                i4 = this.q;
            }
            this.l.setProgress(i4 * 100);
            i(i4);
            FourSwitchSelector fourSwitchSelector = this.A;
            if (fourSwitchSelector != null) {
                fourSwitchSelector.setOption(e(i4));
            }
        }
        if (C0388t.h()) {
            int i5 = this.g;
            if (i5 == -1) {
                i5 = 0;
            }
            this.m.setProgress(i5 * 100);
            j(i5);
            FourSwitchSelector fourSwitchSelector2 = this.B;
            if (fourSwitchSelector2 != null) {
                fourSwitchSelector2.setOption(e(i5));
            }
        }
    }

    private void g() {
        Activity activity = getActivity();
        if (activity != null) {
            Cursor cursor = null;
            try {
                cursor = C0391w.a(activity.getApplicationContext(), this.j, 0, this.k);
                if (cursor != null && cursor.moveToFirst()) {
                    this.f4848d = cursor.getInt(cursor.getColumnIndex("settings_gs"));
                    this.e = cursor.getInt(cursor.getColumnIndex("settings_ts"));
                    this.f = cursor.getInt(cursor.getColumnIndex("settings_edge"));
                    this.g = cursor.getInt(cursor.getColumnIndex("settings_hdr"));
                    boolean z2 = true;
                    if (cursor.getInt(cursor.getColumnIndex("settings_4d")) != 1) {
                        z2 = false;
                    }
                    this.r = z2;
                    Log.d("AdvanceSettingsDetail", "data from db : gs =  " + this.f4848d + ",  ts = " + this.e + ",  edge = " + this.f + ",   HDR = " + this.g + ",   4D = " + this.r);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } catch (Throwable th) {
                IOUtils.closeQuietly((Closeable) null);
                throw th;
            }
            IOUtils.closeQuietly(cursor);
        }
    }

    private void g(int i2) {
        Activity activity = getActivity();
        if (activity != null) {
            C0391w.a(activity.getApplicationContext(), this.j, this.k, "settings_edge", i2);
            i(i2);
            C0373d.w(this.j, String.valueOf(i2 * 100));
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        Activity activity = getActivity();
        if (activity != null) {
            C0391w.a(activity.getApplicationContext(), this.j, this.k);
            g();
            i();
        }
    }

    private void h(int i2) {
        Activity activity = getActivity();
        if (activity != null) {
            C0391w.a(activity.getApplicationContext(), this.j, this.k, "settings_hdr", i2);
            j(i2);
            C0373d.v(this.j, String.valueOf(i2 * 100));
        }
    }

    private void i() {
        this.f4846b.setProgress(this.o);
        this.f4847c.setProgress(this.p);
        this.l.setProgress(this.q * 100);
        i(this.q);
        this.m.setProgress(0);
        j(0);
        this.h.setChecked(this.r);
        FourSwitchSelector fourSwitchSelector = this.A;
        if (fourSwitchSelector != null) {
            fourSwitchSelector.setOption(e(this.q));
        }
        FourSwitchSelector fourSwitchSelector2 = this.B;
        if (fourSwitchSelector2 != null) {
            fourSwitchSelector2.setOption(e(0));
        }
    }

    private void i(int i2) {
        ImageView imageView;
        int i3;
        if (i2 == 0) {
            imageView = this.t;
            i3 = R.drawable.gb_advance_settings_screen_edge_none;
        } else if (i2 == 1) {
            imageView = this.t;
            i3 = R.drawable.gb_advance_settings_screen_edge_1;
        } else if (i2 == 2) {
            imageView = this.t;
            i3 = R.drawable.gb_advance_settings_screen_edge_2;
        } else if (i2 == 3) {
            imageView = this.t;
            i3 = R.drawable.gb_advance_settings_screen_edge_3;
        } else {
            return;
        }
        imageView.setImageResource(i3);
    }

    private void j(int i2) {
        ImageView imageView;
        int i3;
        if (i2 == 0) {
            imageView = this.u;
            i3 = R.drawable.gb_advance_settings_screen_hdr_img_normal;
        } else if (i2 == 1) {
            imageView = this.u;
            i3 = R.drawable.gb_advance_settings_screen_hdr_img_1;
        } else if (i2 == 2) {
            imageView = this.u;
            i3 = R.drawable.gb_advance_settings_screen_hdr_img_2;
        } else if (i2 == 3) {
            imageView = this.u;
            i3 = R.drawable.gb_advance_settings_screen_hdr_img_3;
        } else {
            return;
        }
        imageView.setImageResource(i3);
    }

    public void a(SeekBarLinearLayout seekBarLinearLayout, float f2) {
        SeekBar seekBar;
        int layoutDirection = this.s.getLayoutDirection();
        int i2 = 0;
        while (true) {
            float[] fArr = f4845a;
            if (i2 < fArr.length) {
                if (((double) Math.abs(fArr[i2] - f2)) < 0.1665d) {
                    switch (seekBarLinearLayout.getId()) {
                        case R.id.sbll_edge /*2131297583*/:
                            SeekBar seekBar2 = this.l;
                            seekBar2.setProgress(layoutDirection == 1 ? seekBar2.getMax() - (i2 * 100) : i2 * 100);
                            seekBar = this.l;
                            break;
                        case R.id.sbll_hdr /*2131297584*/:
                            SeekBar seekBar3 = this.m;
                            seekBar3.setProgress(layoutDirection == 1 ? seekBar3.getMax() - (i2 * 100) : i2 * 100);
                            seekBar = this.m;
                            break;
                    }
                    onStopTrackingTouch(seekBar);
                }
                i2++;
            } else {
                return;
            }
        }
    }

    public void a(r rVar) {
        this.y = rVar;
    }

    public void a(FourSwitchSelector fourSwitchSelector, int i2) {
        if (fourSwitchSelector == this.A) {
            g(f(i2));
        } else if (fourSwitchSelector == this.B) {
            h(f(i2));
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        int i2;
        Resources resources;
        this.s = this.mView;
        this.v = (LinearLayout) findViewById(R.id.touch_settings);
        this.x = findViewById(R.id.screen_hrd_title);
        this.w = (LinearLayout) findViewById(R.id.screen_hdr_container);
        int i3 = 8;
        if (!C0388t.q()) {
            this.v.setVisibility(8);
        }
        if (!C0388t.h()) {
            this.w.setVisibility(8);
            this.x.setVisibility(8);
        }
        this.f4846b = (SeekBar) findViewById(R.id.seekbar_follow_up);
        this.f4846b.setOnSeekBarChangeListener(this);
        this.f4847c = (SeekBar) findViewById(R.id.seekbar_finger_up);
        this.f4847c.setOnSeekBarChangeListener(this);
        this.h = (CompoundButton) findViewById(R.id.sb_vibration_4d);
        this.h.setOnCheckedChangeListener(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.vibration_4d_container);
        if (this.i.contains(this.j) && c.b()) {
            i3 = 0;
        }
        linearLayout.setVisibility(i3);
        TextView textView = (TextView) findViewById(R.id.tv_title);
        if (textView != null) {
            if ("com.tencent.tmgp.sgamece".equals(this.j) || "com.tencent.tmgp.sgame".equals(this.j)) {
                resources = getResources();
                i2 = R.string.gb_advance_settings_vibration_4d_summary2;
            } else {
                resources = getResources();
                i2 = R.string.gb_advance_settings_vibration_4d_summary;
            }
            textView.setText(resources.getString(i2));
        }
        findViewById(R.id.ll_reset_clickable_area).setOnClickListener(this);
        this.m = (SeekBar) findViewById(R.id.seekbar_screen_hdr);
        this.m.setOnSeekBarChangeListener(this);
        this.l = (SeekBar) findViewById(R.id.seekbar_edge_suppression);
        this.l.setOnSeekBarChangeListener(this);
        ((SeekBarLinearLayout) findViewById(R.id.sbll_edge)).setOnLinearLayoutClickListener(this);
        ((SeekBarLinearLayout) findViewById(R.id.sbll_hdr)).setOnLinearLayoutClickListener(this);
        this.t = (ImageView) findViewById(R.id.iv_screen_edge);
        this.t.setColorFilter(Application.d().getResources().getColor(R.color.gb_advanced_settings_edge_mask_color));
        this.u = (ImageView) findViewById(R.id.iv_screen_hdr);
        View findViewById = findViewById(R.id.backBtn);
        if (findViewById != null) {
            findViewById.setOnClickListener(this);
        }
        TextView textView2 = (TextView) findViewById(R.id.actionBarTitleTv);
        if (textView2 != null && !TextUtils.isEmpty(this.z)) {
            textView2.setText(this.z);
        }
        this.A = (FourSwitchSelector) findViewById(R.id.edgeSwitchSelector);
        FourSwitchSelector fourSwitchSelector = this.A;
        if (fourSwitchSelector != null) {
            fourSwitchSelector.setListener(this);
        }
        this.B = (FourSwitchSelector) findViewById(R.id.hdrSwitchSelector);
        FourSwitchSelector fourSwitchSelector2 = this.B;
        if (fourSwitchSelector2 != null) {
            fourSwitchSelector2.setListener(this);
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        f();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
        Activity activity;
        if (compoundButton.getId() == R.id.sb_vibration_4d && (activity = getActivity()) != null) {
            C0391w.a(activity.getApplicationContext(), this.j, this.k, "settings_4d", z2 ? 1 : 0);
            C0373d.u(this.j, String.valueOf(z2));
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backBtn) {
            r rVar = this.y;
            if (rVar != null) {
                rVar.pop();
            }
        } else if (id == R.id.ll_reset_clickable_area) {
            e();
        }
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.z = arguments.getString("label");
            this.j = arguments.getString("pkg");
            this.k = arguments.getInt("pkg_uid", -1);
        } else {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                this.z = intent.getStringExtra("label");
                this.j = intent.getStringExtra("pkg");
                this.k = intent.getIntExtra("pkg_uid", -1);
                setTitle(this.z);
            }
        }
        this.i = c.a();
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_advanced_settings_detail;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.n;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.n.dismiss();
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i2, boolean z2) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Activity activity;
        int progress;
        StringBuilder sb;
        String str;
        if (this.k != -1 && (activity = getActivity()) != null) {
            Context applicationContext = activity.getApplicationContext();
            if (seekBar == this.f4846b) {
                progress = seekBar.getProgress();
                C0391w.a(applicationContext, this.j, this.k, "settings_gs", progress);
                C0373d.y(this.j, String.valueOf(seekBar.getProgress()));
                sb = new StringBuilder();
                str = "跟手；";
            } else if (seekBar == this.f4847c) {
                progress = seekBar.getProgress();
                C0391w.a(applicationContext, this.j, this.k, "settings_ts", progress);
                C0373d.x(this.j, String.valueOf(seekBar.getProgress()));
                sb = new StringBuilder();
                str = "抬手；";
            } else if (seekBar == this.m) {
                h(a(seekBar));
                return;
            } else if (seekBar == this.l) {
                g(a(seekBar));
                return;
            } else {
                return;
            }
            sb.append(str);
            sb.append(progress);
            Log.i("AdvanceSettingsDetail", sb.toString());
        }
    }
}
