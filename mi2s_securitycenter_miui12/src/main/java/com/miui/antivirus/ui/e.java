package com.miui.antivirus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import b.b.c.i.b;
import com.miui.antivirus.model.a;
import com.miui.securitycenter.R;

public class e extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    public AlertDialog f2960a;

    /* renamed from: b  reason: collision with root package name */
    protected b f2961b;

    /* renamed from: c  reason: collision with root package name */
    protected Context f2962c;

    public e(Context context) {
        super(context);
        this.f2962c = context;
    }

    public e(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2962c = context;
    }

    /* access modifiers changed from: private */
    public void b(a aVar, Context context) {
        this.f2960a = new AlertDialog.Builder(context).setTitle(R.string.sp_button_text_ignore_alert_title).setPositiveButton(17039370, new d(this, aVar)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    public void a(a aVar, Context context) {
        this.f2960a = new AlertDialog.Builder(context).setTitle(aVar.c()).setSingleChoiceItems(new String[]{context.getString(R.string.button_text_ignore)}, -1, new c(this, aVar, context)).show();
    }

    public void onClick(View view) {
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        setOnLongClickListener(this);
    }

    public boolean onLongClick(View view) {
        return true;
    }

    public void setEventHandler(b bVar) {
        this.f2961b = bVar;
    }
}
