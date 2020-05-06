package com.miui.antivirus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import b.b.b.o;
import com.miui.antivirus.model.e;
import com.miui.securitycenter.R;

public class ButtonResultView extends e {

    /* renamed from: d  reason: collision with root package name */
    private Button f2916d;
    private e e;

    public ButtonResultView(Context context) {
        super(context);
    }

    public ButtonResultView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void a(e eVar) {
        this.e = eVar;
        if (f.f2963a[eVar.g().ordinal()] == 1) {
            this.f2916d.setText(R.string.apps_item_virus_cleanup_text);
        }
    }

    public void onClick(View view) {
        o a2 = o.a(this.f2962c);
        boolean z = false;
        for (e next : a2.u()) {
            if (next.v()) {
                z = true;
                a2.c(next);
                a2.e(next);
            }
        }
        if (z) {
            this.f2961b.a(1012, this.e);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2916d = (Button) findViewById(R.id.button);
        this.f2916d.setOnClickListener(this);
    }
}
