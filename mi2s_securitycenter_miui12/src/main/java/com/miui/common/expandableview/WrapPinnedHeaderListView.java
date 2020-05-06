package com.miui.common.expandableview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import com.miui.securitycenter.R;

public class WrapPinnedHeaderListView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private PinnedHeaderListView f3827a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public View f3828b;

    public WrapPinnedHeaderListView(Context context) {
        super(context);
        a(context, (AttributeSet) null, 0);
    }

    public WrapPinnedHeaderListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet, 0);
    }

    public WrapPinnedHeaderListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context, attributeSet, i);
    }

    private void a(Context context, AttributeSet attributeSet, int i) {
        this.f3827a = new PinnedHeaderListView(context);
        addView(this.f3827a);
        this.f3828b = new FrameLayout(context);
        addView(this.f3828b, new FrameLayout.LayoutParams(-1, getResources().getDimensionPixelOffset(R.dimen.applock_list_view_header_height)));
        this.f3828b.setBackgroundColor(Color.parseColor("#00000000"));
        this.f3828b.setFocusableInTouchMode(true);
        this.f3828b.setClickable(true);
        this.f3828b.setImportantForAccessibility(2);
        this.f3827a.setOnHeaderViewUpdateListener(new b(this));
    }

    /* access modifiers changed from: private */
    public void setPlaceContentDescription(CharSequence charSequence) {
        this.f3828b.setContentDescription(charSequence);
    }

    /* access modifiers changed from: private */
    public void setPlaceViewVisibility(boolean z) {
        if (z) {
            this.f3828b.setVisibility(0);
            this.f3828b.setImportantForAccessibility(1);
            return;
        }
        this.f3828b.setVisibility(8);
    }

    public PinnedHeaderListView getListView() {
        return this.f3827a;
    }

    public void setAdapter(ListAdapter listAdapter) {
        this.f3827a.setAdapter(listAdapter);
    }

    public void setEmptyView(View view) {
        this.f3827a.setEmptyView(view);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.f3827a.setOnItemClickListener(onItemClickListener);
    }

    public void setPinHeaders(boolean z) {
        this.f3827a.setPinHeaders(z);
    }
}
