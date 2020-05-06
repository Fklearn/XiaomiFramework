package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.view.s;
import com.miui.securitycenter.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ManualRecordSettingsTabFragment extends d implements View.OnClickListener, s {

    /* renamed from: a  reason: collision with root package name */
    private a f4935a;

    /* renamed from: b  reason: collision with root package name */
    private int f4936b = -1;

    /* renamed from: c  reason: collision with root package name */
    private View f4937c;

    /* renamed from: d  reason: collision with root package name */
    private View f4938d;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Tab {
    }

    public interface a {
        void b(int i);
    }

    private void e(int i) {
        if (this.f4936b != i) {
            for (View view : new View[]{this.f4937c, this.f4938d}) {
                if (view != null) {
                    view.setSelected(false);
                }
            }
            View view2 = null;
            if (i == 0) {
                view2 = this.f4937c;
            } else if (1 == i) {
                view2 = this.f4938d;
            }
            if (view2 != null) {
                view2.setSelected(true);
            }
            this.f4936b = i;
            a aVar = this.f4935a;
            if (aVar != null) {
                aVar.b(i);
            }
        }
    }

    public void a(Object obj) {
        if (obj instanceof a) {
            this.f4935a = (a) obj;
        }
    }

    public void a(boolean z) {
        View[] viewArr = {this.f4938d};
        float f = !z ? 0.2f : 1.0f;
        for (View view : viewArr) {
            if (view != null) {
                view.setEnabled(z);
                view.setAlpha(f);
            }
        }
    }

    public int e() {
        return this.f4936b;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f4937c = findViewById(R.id.tabKing);
        this.f4938d = findViewById(R.id.tabPeace);
        for (View onClickListener : new View[]{this.f4937c, this.f4938d}) {
            onClickListener.setOnClickListener(this);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            e(TextUtils.equals("com.tencent.tmgp.sgame", arguments.getString("gamePkg")) ^ true ? 1 : 0);
        } else {
            e(0);
        }
    }

    public void onClick(View view) {
        e(R.id.tabKing == view.getId() ? 0 : R.id.tabPeace == view.getId() ? 1 : -1);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_manual_record_settings_tab;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
