package com.miui.applicationlock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import b.b.o.g.c;
import miui.R;

public class WrapMaml extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f3429a;

    public WrapMaml(Context context) {
        this(context, (AttributeSet) null);
    }

    public WrapMaml(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WrapMaml(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f3429a = context;
    }

    public void setLocalResourcePath(String str) {
        try {
            Object a2 = c.a(Class.forName("miui.maml.util.ZipResourceLoader"), (Class<?>[]) new Class[]{String.class}, str);
            Class<?> cls = Class.forName("miui.maml.ResourceManager");
            Class[] clsArr = new Class[1];
            clsArr[0] = Class.forName("miui.maml.ResourceLoader");
            Object a3 = c.a(cls, (Class<?>[]) clsArr, a2);
            Class<?> cls2 = Class.forName("miui.maml.ScreenContext");
            Class[] clsArr2 = new Class[2];
            clsArr2[0] = Context.class;
            clsArr2[1] = Class.forName("miui.maml.ResourceManager");
            Object a4 = c.a(cls2, (Class<?>[]) clsArr2, new ContextThemeWrapper(this.f3429a, R.style.Theme_Light), a3);
            Class<?> cls3 = Class.forName("miui.maml.ScreenElementRoot");
            Class[] clsArr3 = new Class[1];
            clsArr3[0] = Class.forName("miui.maml.ScreenContext");
            Object a5 = c.a(cls3, (Class<?>[]) clsArr3, a4);
            if (((Boolean) c.a(a5, Boolean.TYPE, "load", (Class<?>[]) null, new Object[0])).booleanValue()) {
                Class<?> cls4 = Class.forName("miui.maml.component.MamlView");
                Class[] clsArr4 = new Class[2];
                clsArr4[0] = Context.class;
                clsArr4[1] = Class.forName("miui.maml.ScreenElementRoot");
                addView((View) c.a(cls4, (Class<?>[]) clsArr4, this.f3429a, a5), -1, -1);
            }
        } catch (Exception e) {
            Log.e("wrapMaml", "setLocalResourcePath exception: ", e);
        }
    }
}
