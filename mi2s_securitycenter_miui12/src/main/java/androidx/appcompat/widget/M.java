package androidx.appcompat.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.s;
import androidx.core.view.D;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public interface M {
    D a(int i, long j);

    void a(int i);

    void a(Menu menu, s.a aVar);

    void a(C0090ba baVar);

    void a(boolean z);

    boolean a();

    void b(int i);

    void b(boolean z);

    boolean b();

    void c(int i);

    boolean c();

    void collapseActionView();

    boolean d();

    void e();

    boolean f();

    boolean g();

    CharSequence getTitle();

    int h();

    ViewGroup i();

    Context j();

    void k();

    void l();

    int m();

    void n();

    void setIcon(int i);

    void setIcon(Drawable drawable);

    void setWindowCallback(Window.Callback callback);

    void setWindowTitle(CharSequence charSequence);
}
