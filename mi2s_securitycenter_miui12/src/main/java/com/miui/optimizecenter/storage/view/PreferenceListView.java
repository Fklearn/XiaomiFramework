package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.miui.optimizecenter.storage.d.a;
import com.miui.optimizecenter.storage.d.c;
import com.miui.optimizecenter.storage.d.d;
import com.miui.optimizecenter.storage.view.PreferenceCategoryView;
import java.util.List;

public class PreferenceListView extends LinearLayout {
    public PreferenceListView(Context context) {
        super(context);
    }

    public PreferenceListView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PreferenceListView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof PreferenceCategoryView) {
                ((PreferenceCategoryView) childAt).a();
            }
        }
    }

    public void a(List<d> list, PreferenceCategoryView.a aVar) {
        removeAllViews();
        if (list != null && !list.isEmpty()) {
            for (d next : list) {
                a a2 = next.f() == 0 ? c.a(getContext()).a(next.b()) : null;
                if (a2 != null) {
                    PreferenceCategoryView a3 = PreferenceCategoryView.a(getContext(), (ViewGroup) this);
                    a3.a(next, a2, aVar);
                    addView(a3);
                }
            }
        }
    }
}
