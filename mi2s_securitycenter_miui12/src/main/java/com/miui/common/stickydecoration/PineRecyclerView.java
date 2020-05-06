package com.miui.common.stickydecoration;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import miuix.recyclerview.widget.RecyclerView;

public class PineRecyclerView extends RecyclerView {
    private c Ta;

    public PineRecyclerView(Context context) {
        super(context);
    }

    public PineRecyclerView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PineRecyclerView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a(RecyclerView.f fVar) {
        if (fVar != null && (fVar instanceof c)) {
            this.Ta = (c) fVar;
        }
        super.a(fVar);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.Ta != null) {
            int action = motionEvent.getAction();
            if (action == 0) {
                this.Ta.a(motionEvent);
            } else if (action == 1 && this.Ta.b(motionEvent)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
