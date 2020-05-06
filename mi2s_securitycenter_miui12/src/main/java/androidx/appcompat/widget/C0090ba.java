package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.LinearLayoutCompat;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
/* renamed from: androidx.appcompat.widget.ba  reason: case insensitive filesystem */
public class C0090ba extends HorizontalScrollView implements AdapterView.OnItemSelectedListener {

    /* renamed from: a  reason: collision with root package name */
    private static final Interpolator f586a = new DecelerateInterpolator();

    /* renamed from: b  reason: collision with root package name */
    Runnable f587b;

    /* renamed from: c  reason: collision with root package name */
    private b f588c;

    /* renamed from: d  reason: collision with root package name */
    LinearLayoutCompat f589d;
    private Spinner e;
    private boolean f;
    int g;
    int h;
    private int i;
    private int j;

    /* renamed from: androidx.appcompat.widget.ba$a */
    private class a extends BaseAdapter {
        a() {
        }

        public int getCount() {
            return C0090ba.this.f589d.getChildCount();
        }

        public Object getItem(int i) {
            return ((c) C0090ba.this.f589d.getChildAt(i)).a();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                return C0090ba.this.a((ActionBar.c) getItem(i), true);
            }
            ((c) view).a((ActionBar.c) getItem(i));
            return view;
        }
    }

    /* renamed from: androidx.appcompat.widget.ba$b */
    private class b implements View.OnClickListener {
        b() {
        }

        public void onClick(View view) {
            ((c) view).a().e();
            int childCount = C0090ba.this.f589d.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = C0090ba.this.f589d.getChildAt(i);
                childAt.setSelected(childAt == view);
            }
        }
    }

    /* renamed from: androidx.appcompat.widget.ba$c */
    private class c extends LinearLayout {

        /* renamed from: a  reason: collision with root package name */
        private final int[] f592a = {16842964};

        /* renamed from: b  reason: collision with root package name */
        private ActionBar.c f593b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f594c;

        /* renamed from: d  reason: collision with root package name */
        private ImageView f595d;
        private View e;

        public c(Context context, ActionBar.c cVar, boolean z) {
            super(context, (AttributeSet) null, a.a.a.actionBarTabStyle);
            this.f593b = cVar;
            va a2 = va.a(context, (AttributeSet) null, this.f592a, a.a.a.actionBarTabStyle, 0);
            if (a2.g(0)) {
                setBackgroundDrawable(a2.b(0));
            }
            a2.b();
            if (z) {
                setGravity(8388627);
            }
            b();
        }

        public ActionBar.c a() {
            return this.f593b;
        }

        public void a(ActionBar.c cVar) {
            this.f593b = cVar;
            b();
        }

        public void b() {
            ActionBar.c cVar = this.f593b;
            View b2 = cVar.b();
            CharSequence charSequence = null;
            if (b2 != null) {
                ViewParent parent = b2.getParent();
                if (parent != this) {
                    if (parent != null) {
                        ((ViewGroup) parent).removeView(b2);
                    }
                    addView(b2);
                }
                this.e = b2;
                TextView textView = this.f594c;
                if (textView != null) {
                    textView.setVisibility(8);
                }
                ImageView imageView = this.f595d;
                if (imageView != null) {
                    imageView.setVisibility(8);
                    this.f595d.setImageDrawable((Drawable) null);
                    return;
                }
                return;
            }
            View view = this.e;
            if (view != null) {
                removeView(view);
                this.e = null;
            }
            Drawable c2 = cVar.c();
            CharSequence d2 = cVar.d();
            if (c2 != null) {
                if (this.f595d == null) {
                    AppCompatImageView appCompatImageView = new AppCompatImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                    layoutParams.gravity = 16;
                    appCompatImageView.setLayoutParams(layoutParams);
                    addView(appCompatImageView, 0);
                    this.f595d = appCompatImageView;
                }
                this.f595d.setImageDrawable(c2);
                this.f595d.setVisibility(0);
            } else {
                ImageView imageView2 = this.f595d;
                if (imageView2 != null) {
                    imageView2.setVisibility(8);
                    this.f595d.setImageDrawable((Drawable) null);
                }
            }
            boolean z = !TextUtils.isEmpty(d2);
            if (z) {
                if (this.f594c == null) {
                    I i = new I(getContext(), (AttributeSet) null, a.a.a.actionBarTabTextStyle);
                    i.setEllipsize(TextUtils.TruncateAt.END);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                    layoutParams2.gravity = 16;
                    i.setLayoutParams(layoutParams2);
                    addView(i);
                    this.f594c = i;
                }
                this.f594c.setText(d2);
                this.f594c.setVisibility(0);
            } else {
                TextView textView2 = this.f594c;
                if (textView2 != null) {
                    textView2.setVisibility(8);
                    this.f594c.setText((CharSequence) null);
                }
            }
            ImageView imageView3 = this.f595d;
            if (imageView3 != null) {
                imageView3.setContentDescription(cVar.a());
            }
            if (!z) {
                charSequence = cVar.a();
            }
            Da.a(this, charSequence);
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName("androidx.appcompat.app.ActionBar$Tab");
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("androidx.appcompat.app.ActionBar$Tab");
        }

        public void onMeasure(int i, int i2) {
            int i3;
            super.onMeasure(i, i2);
            if (C0090ba.this.g > 0 && getMeasuredWidth() > (i3 = C0090ba.this.g)) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), i2);
            }
        }

        public void setSelected(boolean z) {
            boolean z2 = isSelected() != z;
            super.setSelected(z);
            if (z2 && z) {
                sendAccessibilityEvent(4);
            }
        }
    }

    private Spinner a() {
        AppCompatSpinner appCompatSpinner = new AppCompatSpinner(getContext(), (AttributeSet) null, a.a.a.actionDropDownStyle);
        appCompatSpinner.setLayoutParams(new LinearLayoutCompat.a(-2, -1));
        appCompatSpinner.setOnItemSelectedListener(this);
        return appCompatSpinner;
    }

    private boolean b() {
        Spinner spinner = this.e;
        return spinner != null && spinner.getParent() == this;
    }

    private void c() {
        if (!b()) {
            if (this.e == null) {
                this.e = a();
            }
            removeView(this.f589d);
            addView(this.e, new ViewGroup.LayoutParams(-2, -1));
            if (this.e.getAdapter() == null) {
                this.e.setAdapter(new a());
            }
            Runnable runnable = this.f587b;
            if (runnable != null) {
                removeCallbacks(runnable);
                this.f587b = null;
            }
            this.e.setSelection(this.j);
        }
    }

    private boolean d() {
        if (!b()) {
            return false;
        }
        removeView(this.e);
        addView(this.f589d, new ViewGroup.LayoutParams(-2, -1));
        setTabSelected(this.e.getSelectedItemPosition());
        return false;
    }

    /* access modifiers changed from: package-private */
    public c a(ActionBar.c cVar, boolean z) {
        c cVar2 = new c(getContext(), cVar, z);
        if (z) {
            cVar2.setBackgroundDrawable((Drawable) null);
            cVar2.setLayoutParams(new AbsListView.LayoutParams(-1, this.i));
        } else {
            cVar2.setFocusable(true);
            if (this.f588c == null) {
                this.f588c = new b();
            }
            cVar2.setOnClickListener(this.f588c);
        }
        return cVar2;
    }

    public void a(int i2) {
        View childAt = this.f589d.getChildAt(i2);
        Runnable runnable = this.f587b;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        this.f587b = new C0088aa(this, childAt);
        post(this.f587b);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Runnable runnable = this.f587b;
        if (runnable != null) {
            post(runnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        a.a.d.a a2 = a.a.d.a.a(getContext());
        setContentHeight(a2.e());
        this.h = a2.d();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.f587b;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j2) {
        ((c) view).a().e();
    }

    public void onMeasure(int i2, int i3) {
        int i4;
        int mode = View.MeasureSpec.getMode(i2);
        boolean z = true;
        boolean z2 = mode == 1073741824;
        setFillViewport(z2);
        int childCount = this.f589d.getChildCount();
        if (childCount <= 1 || !(mode == 1073741824 || mode == Integer.MIN_VALUE)) {
            i4 = -1;
        } else {
            if (childCount > 2) {
                this.g = (int) (((float) View.MeasureSpec.getSize(i2)) * 0.4f);
            } else {
                this.g = View.MeasureSpec.getSize(i2) / 2;
            }
            i4 = Math.min(this.g, this.h);
        }
        this.g = i4;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.i, 1073741824);
        if (z2 || !this.f) {
            z = false;
        }
        if (z) {
            this.f589d.measure(0, makeMeasureSpec);
            if (this.f589d.getMeasuredWidth() > View.MeasureSpec.getSize(i2)) {
                c();
                int measuredWidth = getMeasuredWidth();
                super.onMeasure(i2, makeMeasureSpec);
                int measuredWidth2 = getMeasuredWidth();
                if (z2 && measuredWidth != measuredWidth2) {
                    setTabSelected(this.j);
                    return;
                }
            }
        }
        d();
        int measuredWidth3 = getMeasuredWidth();
        super.onMeasure(i2, makeMeasureSpec);
        int measuredWidth22 = getMeasuredWidth();
        if (z2) {
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void setAllowCollapse(boolean z) {
        this.f = z;
    }

    public void setContentHeight(int i2) {
        this.i = i2;
        requestLayout();
    }

    public void setTabSelected(int i2) {
        this.j = i2;
        int childCount = this.f589d.getChildCount();
        int i3 = 0;
        while (i3 < childCount) {
            View childAt = this.f589d.getChildAt(i3);
            boolean z = i3 == i2;
            childAt.setSelected(z);
            if (z) {
                a(i2);
            }
            i3++;
        }
        Spinner spinner = this.e;
        if (spinner != null && i2 >= 0) {
            spinner.setSelection(i2);
        }
    }
}
