package androidx.appcompat.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.widget.va;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public final class ExpandedMenuView extends ListView implements j.b, t, AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f353a = {16842964, 16843049};

    /* renamed from: b  reason: collision with root package name */
    private j f354b;

    /* renamed from: c  reason: collision with root package name */
    private int f355c;

    public ExpandedMenuView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842868);
    }

    public ExpandedMenuView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        setOnItemClickListener(this);
        va a2 = va.a(context, attributeSet, f353a, i, 0);
        if (a2.g(0)) {
            setBackgroundDrawable(a2.b(0));
        }
        if (a2.g(1)) {
            setDivider(a2.b(1));
        }
        a2.b();
    }

    public void a(j jVar) {
        this.f354b = jVar;
    }

    public boolean a(n nVar) {
        return this.f354b.a((MenuItem) nVar, 0);
    }

    public int getWindowAnimations() {
        return this.f355c;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setChildrenDrawingCacheEnabled(false);
    }

    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        a((n) getAdapter().getItem(i));
    }
}
