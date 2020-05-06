package com.miui.antivirus.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.os.Build;
import miui.widget.ProgressBar;

public class MainHandleBar extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private b.b.c.i.b f2930a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f2931b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f2932c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f2933d;
    private ImageView e;
    private ProgressBar f;
    private ProgressBar g;
    private ProgressBar h;
    private ProgressBar i;
    private Button j;
    /* access modifiers changed from: private */
    public TextView k;
    private LinearLayout l;

    public enum a {
        SAFE,
        RISKY,
        OMITTED
    }

    public enum b {
        NETWORK,
        SYSTEM,
        SMS,
        VIRUS
    }

    public MainHandleBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainHandleBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003d, code lost:
        if (com.miui.antivirus.ui.MainHandleBar.a.f2935b == r7) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004e, code lost:
        if (com.miui.antivirus.ui.MainHandleBar.a.f2935b == r7) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0067, code lost:
        if (com.miui.antivirus.ui.MainHandleBar.a.f2935b == r7) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002c, code lost:
        if (com.miui.antivirus.ui.MainHandleBar.a.f2935b == r7) goto L_0x006b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.antivirus.ui.MainHandleBar.b r6, com.miui.antivirus.ui.MainHandleBar.a r7) {
        /*
            r5 = this;
            int[] r0 = com.miui.antivirus.ui.n.f2974a
            int r6 = r6.ordinal()
            r6 = r0[r6]
            r0 = 1
            r1 = 2131232287(0x7f08061f, float:1.808068E38)
            r2 = 2131232281(0x7f080619, float:1.8080667E38)
            r3 = 0
            r4 = 8
            if (r6 == r0) goto L_0x0051
            r0 = 2
            if (r6 == r0) goto L_0x0040
            r0 = 3
            if (r6 == r0) goto L_0x002f
            r0 = 4
            if (r6 == r0) goto L_0x001e
            goto L_0x006e
        L_0x001e:
            miui.widget.ProgressBar r6 = r5.h
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r5.e
            r6.setVisibility(r3)
            android.widget.ImageView r6 = r5.e
            com.miui.antivirus.ui.MainHandleBar$a r0 = com.miui.antivirus.ui.MainHandleBar.a.RISKY
            if (r0 != r7) goto L_0x006a
            goto L_0x006b
        L_0x002f:
            miui.widget.ProgressBar r6 = r5.i
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r5.f2933d
            r6.setVisibility(r3)
            android.widget.ImageView r6 = r5.f2933d
            com.miui.antivirus.ui.MainHandleBar$a r0 = com.miui.antivirus.ui.MainHandleBar.a.RISKY
            if (r0 != r7) goto L_0x006a
            goto L_0x006b
        L_0x0040:
            miui.widget.ProgressBar r6 = r5.g
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r5.f2932c
            r6.setVisibility(r3)
            android.widget.ImageView r6 = r5.f2932c
            com.miui.antivirus.ui.MainHandleBar$a r0 = com.miui.antivirus.ui.MainHandleBar.a.RISKY
            if (r0 != r7) goto L_0x006a
            goto L_0x006b
        L_0x0051:
            miui.widget.ProgressBar r6 = r5.f
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r5.f2931b
            r6.setVisibility(r3)
            android.widget.ImageView r6 = r5.f2931b
            com.miui.antivirus.ui.MainHandleBar$a r0 = com.miui.antivirus.ui.MainHandleBar.a.OMITTED
            if (r0 != r7) goto L_0x0065
            r1 = 2131232289(0x7f080621, float:1.8080683E38)
            goto L_0x006b
        L_0x0065:
            com.miui.antivirus.ui.MainHandleBar$a r0 = com.miui.antivirus.ui.MainHandleBar.a.RISKY
            if (r0 != r7) goto L_0x006a
            goto L_0x006b
        L_0x006a:
            r1 = r2
        L_0x006b:
            r6.setImageResource(r1)
        L_0x006e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.ui.MainHandleBar.a(com.miui.antivirus.ui.MainHandleBar$b, com.miui.antivirus.ui.MainHandleBar$a):void");
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_action) {
            this.f2930a.sendEmptyMessage(1000);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.l = (LinearLayout) findViewById(R.id.handle_item_network);
        this.f2931b = (ImageView) findViewById(R.id.handle_network_state);
        this.f2932c = (ImageView) findViewById(R.id.handle_payment_state);
        this.f2933d = (ImageView) findViewById(R.id.handle_privacy_state);
        this.e = (ImageView) findViewById(R.id.handle_virus_state);
        this.f = findViewById(R.id.handle_network_progress);
        this.g = findViewById(R.id.handle_payment_progress);
        this.h = findViewById(R.id.handle_virus_progress);
        this.i = findViewById(R.id.handle_privacy_progress);
        this.j = (Button) findViewById(R.id.btn_action);
        this.j.setOnClickListener(this);
        this.k = (TextView) findViewById(R.id.support);
        new m(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.l.setVisibility(8);
        }
    }

    public void setActionButtonText(CharSequence charSequence) {
        this.j.setText(charSequence);
    }

    public void setEventHandler(b.b.c.i.b bVar) {
        this.f2930a = bVar;
    }

    public void setHandleActionButtonEnabled(Boolean bool) {
        this.j.setEnabled(bool.booleanValue());
        this.j.setClickable(!bool.booleanValue());
    }
}
