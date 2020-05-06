package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.view.s;
import com.miui.securitycenter.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SettingsTabFragment extends d implements View.OnClickListener, s {

    /* renamed from: a  reason: collision with root package name */
    private a f5003a;

    /* renamed from: b  reason: collision with root package name */
    private int f5004b = -1;

    /* renamed from: c  reason: collision with root package name */
    private View f5005c;

    /* renamed from: d  reason: collision with root package name */
    private View f5006d;
    private View e;
    private View f;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Tab {
    }

    public interface a {
        void b(int i);
    }

    private void e(int i) {
        if (this.f5004b != i) {
            for (View view : new View[]{this.f5005c, this.f5006d, this.e, this.f}) {
                if (view != null) {
                    view.setSelected(false);
                }
            }
            View view2 = null;
            if (i == 0) {
                view2 = this.f5005c;
            } else if (i == 1) {
                view2 = this.f5006d;
            } else if (i == 2) {
                view2 = this.e;
            } else if (i == 3) {
                view2 = this.f;
            }
            if (view2 != null) {
                view2.setSelected(true);
            }
            this.f5004b = i;
            a aVar = this.f5003a;
            if (aVar != null) {
                aVar.b(i);
            }
        }
    }

    public void a(Object obj) {
        if (obj instanceof a) {
            this.f5003a = (a) obj;
        }
    }

    public void a(boolean z) {
        View[] viewArr = {this.f5006d, this.e, this.f};
        float f2 = !z ? 0.2f : 1.0f;
        for (View view : viewArr) {
            if (view != null) {
                view.setEnabled(z);
                view.setAlpha(f2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f5005c = findViewById(R.id.tabGlobal);
        this.f5006d = findViewById(R.id.tabPerformance);
        this.e = findViewById(R.id.tabDnd);
        this.f = findViewById(R.id.tabOthers);
        for (View onClickListener : new View[]{this.f5005c, this.f5006d, this.e, this.f}) {
            onClickListener.setOnClickListener(this);
        }
        if (!C0388t.d()) {
            this.f.setVisibility(8);
        }
        e(0);
    }

    public void onClick(View view) {
        int i;
        switch (view.getId()) {
            case R.id.tabDnd /*2131297761*/:
                i = 2;
                break;
            case R.id.tabGlobal /*2131297763*/:
                i = 0;
                break;
            case R.id.tabOthers /*2131297766*/:
                i = 3;
                break;
            case R.id.tabPerformance /*2131297768*/:
                i = 1;
                break;
            default:
                i = -1;
                break;
        }
        e(i);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_settings_tab;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
