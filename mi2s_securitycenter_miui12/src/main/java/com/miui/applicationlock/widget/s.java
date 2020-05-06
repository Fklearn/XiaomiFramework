package com.miui.applicationlock.widget;

import android.content.Context;
import android.widget.EditText;
import com.miui.applicationlock.b.b;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.widget.LockPatternView;

public interface s {
    void a();

    void a(Context context, b bVar);

    void b();

    boolean c();

    void d();

    void e();

    EditText f();

    void g();

    void setAppPage(boolean z);

    void setApplockUnlockCallback(p pVar);

    void setDisplayMode(LockPatternView.b bVar);

    void setLightMode(boolean z);
}
