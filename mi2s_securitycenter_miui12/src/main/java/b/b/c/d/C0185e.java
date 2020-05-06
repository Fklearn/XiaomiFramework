package b.b.c.d;

import android.content.Context;
import android.view.View;
import java.io.Serializable;

/* renamed from: b.b.c.d.e  reason: case insensitive filesystem */
public abstract class C0185e implements View.OnClickListener, Serializable {

    /* renamed from: a  reason: collision with root package name */
    protected int f1673a = -1;

    /* renamed from: b  reason: collision with root package name */
    protected C0191k f1674b;

    /* renamed from: c  reason: collision with root package name */
    protected String f1675c;

    public abstract int a();

    public void a(int i, View view, Context context, C0191k kVar) {
        this.f1673a = i;
        this.f1674b = kVar;
    }

    public void a(String str) {
        this.f1675c = str;
    }

    public void onClick(View view) {
    }
}
