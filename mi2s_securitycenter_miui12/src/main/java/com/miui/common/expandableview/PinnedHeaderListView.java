package com.miui.common.expandableview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class PinnedHeaderListView extends ListView implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    private AbsListView.OnScrollListener f3823a;

    /* renamed from: b  reason: collision with root package name */
    private d f3824b;

    /* renamed from: c  reason: collision with root package name */
    private View f3825c;

    /* renamed from: d  reason: collision with root package name */
    private int f3826d = 0;
    private float e;
    private boolean f = true;
    private int g = 0;
    private int h;
    private int i;
    private b j;
    private a k;

    public interface a {
        void a(String str, int i, boolean z, int i2);
    }

    public interface b {
        void a(String str, int i, boolean z);
    }

    public static abstract class c implements AdapterView.OnItemClickListener {
        public abstract void a(AdapterView<?> adapterView, View view, int i, int i2, long j);

        public abstract void a(AdapterView<?> adapterView, View view, int i, long j);

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            a aVar = (a) (adapterView.getAdapter().getClass().equals(HeaderViewListAdapter.class) ? ((HeaderViewListAdapter) adapterView.getAdapter()).getWrappedAdapter() : adapterView.getAdapter());
            int sectionForPosition = aVar.getSectionForPosition(i);
            int positionInSectionForPosition = aVar.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition == -1) {
                a(adapterView, view, sectionForPosition, j);
            } else {
                a(adapterView, view, sectionForPosition, positionInSectionForPosition, j);
            }
        }
    }

    public interface d {
        int getCount();

        int getSectionForPosition(int i);

        View getSectionHeaderView(int i, View view, ViewGroup viewGroup);

        int getSectionHeaderViewType(int i);

        boolean isSectionHeader(int i);
    }

    public PinnedHeaderListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        super.setOnScrollListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        super.setOnScrollListener(this);
    }

    private View a(int i2, View view) {
        boolean z = i2 != this.g || view == null;
        View sectionHeaderView = this.f3824b.getSectionHeaderView(i2, view, this);
        if (z) {
            a(sectionHeaderView);
            this.g = i2;
        }
        return sectionHeaderView;
    }

    private void a(View view) {
        if (view.isLayoutRequested()) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), this.h);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(-2, -2);
                view.setLayoutParams(layoutParams);
            }
            int i2 = layoutParams.height;
            view.measure(makeMeasureSpec, i2 > 0 ? View.MeasureSpec.makeMeasureSpec(i2, 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    private String b(View view) {
        StringBuilder sb;
        List<CharSequence> c2 = c(view);
        String str = "";
        for (int i2 = 0; i2 < c2.size(); i2++) {
            if (i2 == 0) {
                sb = new StringBuilder();
            } else {
                str = str + ",";
                sb = new StringBuilder();
            }
            sb.append(str);
            sb.append(c2.get(i2));
            str = sb.toString();
        }
        return str;
    }

    private List<CharSequence> c(View view) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain();
        try {
            obtain.setClassName(getClass().getName());
            obtain.setPackageName(view.getContext().getPackageName());
            view.dispatchPopulateAccessibilityEvent(obtain);
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(obtain.getText());
            return arrayList;
        } finally {
            obtain.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.f3824b != null && this.f && this.f3825c != null) {
            int save = canvas.save();
            canvas.translate(0.0f, this.e);
            canvas.clipRect(0, 0, getWidth(), this.f3825c.getMeasuredHeight());
            this.f3825c.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    public View getCurrentHeader() {
        return this.f3825c;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        this.h = View.MeasureSpec.getMode(i2);
        this.i = View.MeasureSpec.getMode(i3);
    }

    public void onScroll(AbsListView absListView, int i2, int i3, int i4) {
        AbsListView.OnScrollListener onScrollListener = this.f3823a;
        if (onScrollListener != null) {
            onScrollListener.onScroll(absListView, i2, i3, i4);
        }
        d dVar = this.f3824b;
        View view = null;
        if (dVar == null || dVar.getCount() == 0 || !this.f || i2 < getHeaderViewsCount()) {
            this.f3825c = null;
            this.e = 0.0f;
            for (int i5 = i2; i5 < i2 + i3; i5++) {
                View childAt = getChildAt(i5);
                if (childAt != null) {
                    childAt.setVisibility(0);
                }
            }
            return;
        }
        int headerViewsCount = i2 - getHeaderViewsCount();
        int sectionForPosition = this.f3824b.getSectionForPosition(headerViewsCount);
        int sectionHeaderViewType = this.f3824b.getSectionHeaderViewType(sectionForPosition);
        if (this.f3826d == sectionHeaderViewType) {
            view = this.f3825c;
        }
        this.f3825c = a(sectionForPosition, view);
        a(this.f3825c);
        this.f3826d = sectionHeaderViewType;
        if (this.j != null) {
            this.j.a(b(this.f3825c), this.f3825c.getMeasuredHeight(), true);
        }
        this.e = 0.0f;
        for (int i6 = headerViewsCount; i6 < headerViewsCount + i3; i6++) {
            if (this.f3824b.isSectionHeader(i6)) {
                View childAt2 = getChildAt(i6 - headerViewsCount);
                float top = (float) childAt2.getTop();
                childAt2.setVisibility(0);
                if (((float) this.f3825c.getMeasuredHeight()) >= top && top > 0.0f) {
                    this.e = top - ((float) this.f3825c.getHeight());
                } else if (top <= 0.0f) {
                    childAt2.setVisibility(4);
                }
            }
        }
        if (this.k != null) {
            this.k.a(b(this.f3825c), this.f3825c.getMeasuredHeight(), true, sectionForPosition);
        }
        invalidate();
    }

    public void onScrollStateChanged(AbsListView absListView, int i2) {
        AbsListView.OnScrollListener onScrollListener = this.f3823a;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(absListView, i2);
        }
    }

    public void setAdapter(ListAdapter listAdapter) {
        this.f3825c = null;
        this.f3824b = (d) listAdapter;
        super.setAdapter(listAdapter);
    }

    public void setOnApplockHeaderViewUpdateListener(a aVar) {
        this.k = aVar;
    }

    public void setOnHeaderViewUpdateListener(b bVar) {
        this.j = bVar;
    }

    public void setOnItemClickListener(c cVar) {
        super.setOnItemClickListener(cVar);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.f3823a = onScrollListener;
    }

    public void setPinHeaders(boolean z) {
        this.f = z;
    }
}
