package b.b.c.c.b;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import miui.app.ProgressDialog;

public class k {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ProgressDialog f1628a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Activity f1629b;

    /* renamed from: c  reason: collision with root package name */
    private CharSequence f1630c;

    /* renamed from: d  reason: collision with root package name */
    private Handler f1631d;
    private boolean e = true;

    public k(Activity activity) {
        this.f1629b = activity;
    }

    private void a(Context context) {
        this.f1628a = ProgressDialog.show(context, (CharSequence) null, (CharSequence) null, true, false);
        this.f1628a.setMessage(this.f1630c);
        c();
    }

    private void b() {
        if (this.f1628a == null) {
            a((Context) this.f1629b);
        }
    }

    private void c() {
        j jVar;
        ProgressDialog progressDialog;
        if (this.e) {
            progressDialog = this.f1628a;
            jVar = new j(this);
        } else {
            progressDialog = this.f1628a;
            jVar = null;
        }
        progressDialog.setOnKeyListener(jVar);
    }

    public void a() {
        Activity activity = this.f1629b;
        if (activity != null && !activity.isFinishing()) {
            b();
            ProgressDialog progressDialog = this.f1628a;
            if (progressDialog != null && !progressDialog.isShowing()) {
                this.f1628a.show();
            }
        }
    }

    public void a(int i) {
        a((CharSequence) this.f1629b.getResources().getString(i));
    }

    public void a(CharSequence charSequence) {
        this.f1630c = charSequence;
        ProgressDialog progressDialog = this.f1628a;
        if (progressDialog != null) {
            progressDialog.setMessage(charSequence);
        }
    }

    public void a(boolean z) {
        ProgressDialog progressDialog = this.f1628a;
        if (progressDialog != null && progressDialog.isShowing()) {
            if (this.f1631d == null) {
                this.f1631d = new Handler();
            }
            this.f1631d.postDelayed(new i(this), 200);
        }
    }

    public void b(boolean z) {
        this.e = z;
        if (this.f1628a != null) {
            c();
        }
    }
}
