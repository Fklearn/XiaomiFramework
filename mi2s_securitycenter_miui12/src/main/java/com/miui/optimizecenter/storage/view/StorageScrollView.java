package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.widget.NestedScrollView;

public class StorageScrollView extends NestedScrollView {
    private a C;

    public interface a {
        void a(View view, int i, int i2, int i3, int i4);
    }

    public StorageScrollView(Context context) {
        super(context);
    }

    public StorageScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StorageScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        a aVar = this.C;
        if (aVar != null) {
            aVar.a(this, i, i2, i3, i4);
        }
    }

    public void setOnScrollListener(a aVar) {
        this.C = aVar;
    }
}
