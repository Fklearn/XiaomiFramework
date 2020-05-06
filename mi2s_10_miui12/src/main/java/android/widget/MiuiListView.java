package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import miui.reflect.Method;

public class MiuiListView extends ListView {
    public MiuiListView(Context context) {
        super(context);
    }

    public MiuiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiuiListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private View superObtainView(int position, boolean[] isScrap) {
        Class[] clsArr = {Integer.TYPE, boolean[].class};
        return (View) Method.of(ListView.class, "obtainView", View.class, clsArr).invokeObject(ListView.class, this, new Object[]{Integer.valueOf(position), isScrap});
    }

    /* access modifiers changed from: protected */
    public View obtainView(int position, boolean[] isScrap) {
        return superObtainView(position, isScrap);
    }
}
