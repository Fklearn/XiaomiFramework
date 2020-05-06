package com.miui.earthquakewarning.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import b.b.c.j.A;
import miui.animation.Folme;

public class FolmeListView extends ListView {
    public FolmeListView(Context context) {
        super(context);
        init();
    }

    public FolmeListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FolmeListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (A.a()) {
            try {
                Folme.onListViewTouchEvent(this, motionEvent);
            } catch (Throwable unused) {
                Log.e("FolmeListView", "no support folme");
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
