package b.b.a.d.a;

import android.widget.TextView;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ TextView f1355a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CharSequence f1356b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ k f1357c;

    i(k kVar, TextView textView, CharSequence charSequence) {
        this.f1357c = kVar;
        this.f1355a = textView;
        this.f1356b = charSequence;
    }

    public void run() {
        this.f1355a.setVisibility(0);
        this.f1355a.setText(this.f1356b);
    }
}
