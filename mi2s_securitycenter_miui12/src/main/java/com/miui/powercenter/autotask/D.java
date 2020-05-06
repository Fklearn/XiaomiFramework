package com.miui.powercenter.autotask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.miui.powercenter.autotask.X;
import com.miui.powercenter.utils.n;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class D {

    /* renamed from: a  reason: collision with root package name */
    private Context f6702a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public AutoTask f6703b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public SeekBar f6704c = ((SeekBar) this.f6705d.findViewById(R.id.seekbar));

    /* renamed from: d  reason: collision with root package name */
    private ViewGroup f6705d;

    public D(Context context, AutoTask autoTask) {
        this.f6702a = context;
        this.f6703b = autoTask;
        this.f6705d = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pc_brightness_view, (ViewGroup) null);
        this.f6704c.setMax(n.a(this.f6702a).e());
        ((TextView) this.f6705d.findViewById(R.id.txt_percent1)).setText(this.f6702a.getResources().getString(R.string.percentage, new Object[]{0}));
        ((TextView) this.f6705d.findViewById(R.id.txt_percent2)).setText(this.f6702a.getResources().getString(R.string.percentage, new Object[]{100}));
        if (this.f6703b.hasOperation("brightness")) {
            this.f6704c.setProgress(((Integer) this.f6703b.getOperation("brightness")).intValue());
        }
    }

    public void a(X.a aVar) {
        C c2 = new C(this, aVar);
        new AlertDialog.Builder(this.f6702a).setTitle(R.string.auto_task_operation_brightness).setView(this.f6705d).setNegativeButton(R.string.auto_task_operation_no_op, c2).setPositiveButton(R.string.auto_task_dialog_button_close, c2).show();
    }
}
