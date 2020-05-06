package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.miui.networkassistant.ui.view.RadioCheckable;
import java.util.HashMap;

public class RadioGroup extends LinearLayout {
    /* access modifiers changed from: private */
    public int mCheckedId = -1;
    /* access modifiers changed from: private */
    public RadioCheckable.OnCheckedChangeListener mChildOnCheckedChangeListener;
    /* access modifiers changed from: private */
    public HashMap<Integer, View> mChildViewsMap = new HashMap<>();
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    /* access modifiers changed from: private */
    public boolean mProtectFromCheckedChange = false;

    private class CheckedStateTracker implements RadioCheckable.OnCheckedChangeListener {
        private CheckedStateTracker() {
        }

        public void onCheckedChanged(View view, boolean z) {
            if (!RadioGroup.this.mProtectFromCheckedChange) {
                boolean unused = RadioGroup.this.mProtectFromCheckedChange = true;
                if (RadioGroup.this.mCheckedId != -1) {
                    RadioGroup radioGroup = RadioGroup.this;
                    radioGroup.setCheckedStateForView(radioGroup.mCheckedId, false);
                }
                boolean unused2 = RadioGroup.this.mProtectFromCheckedChange = false;
                RadioGroup.this.setCheckedId(view.getId(), true);
            }
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, View view2, boolean z, int i);
    }

    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        /* access modifiers changed from: private */
        public ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        private PassThroughHierarchyChangeListener() {
        }

        public void onChildViewAdded(View view, View view2) {
            if (view == RadioGroup.this && (view2 instanceof RadioCheckable)) {
                int id = view2.getId();
                if (id == -1) {
                    id = View.generateViewId();
                    view2.setId(id);
                }
                ((RadioCheckable) view2).addOnCheckChangeListener(RadioGroup.this.mChildOnCheckedChangeListener);
                RadioGroup.this.mChildViewsMap.put(Integer.valueOf(id), view2);
            }
            ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener = this.mOnHierarchyChangeListener;
            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewAdded(view, view2);
            }
        }

        public void onChildViewRemoved(View view, View view2) {
            RadioGroup radioGroup = RadioGroup.this;
            if (view == radioGroup && (view2 instanceof RadioCheckable)) {
                ((RadioCheckable) view2).removeOnCheckChangeListener(radioGroup.mChildOnCheckedChangeListener);
            }
            RadioGroup.this.mChildViewsMap.remove(Integer.valueOf(view2.getId()));
            ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener = this.mOnHierarchyChangeListener;
            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewRemoved(view, view2);
            }
        }
    }

    public RadioGroup(Context context) {
        super(context);
        setupView();
    }

    public RadioGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupView();
    }

    public RadioGroup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupView();
    }

    public RadioGroup(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setupView();
    }

    /* access modifiers changed from: private */
    public void setCheckedId(int i, boolean z) {
        this.mCheckedId = i;
        OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, this.mChildViewsMap.get(Integer.valueOf(i)), z, this.mCheckedId);
        }
    }

    /* access modifiers changed from: private */
    public void setCheckedStateForView(int i, boolean z) {
        View view = this.mChildViewsMap.get(Integer.valueOf(i));
        if (view == null && (view = findViewById(i)) != null) {
            this.mChildViewsMap.put(Integer.valueOf(i), view);
        }
        if (view != null && (view instanceof RadioCheckable)) {
            ((RadioCheckable) view).setChecked(z);
        }
    }

    private void setupView() {
        this.mChildOnCheckedChangeListener = new CheckedStateTracker();
        this.mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(this.mPassThroughListener);
    }

    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if ((view instanceof RadioCheckable) && ((RadioCheckable) view).isChecked()) {
            this.mProtectFromCheckedChange = true;
            int i2 = this.mCheckedId;
            if (i2 != -1) {
                setCheckedStateForView(i2, false);
            }
            this.mProtectFromCheckedChange = false;
            setCheckedId(view.getId(), true);
        }
        super.addView(view, i, layoutParams);
    }

    public void check(int i) {
        if (i == -1 || i != this.mCheckedId) {
            int i2 = this.mCheckedId;
            if (i2 != -1) {
                setCheckedStateForView(i2, false);
            }
            if (i != -1) {
                setCheckedStateForView(i, true);
            }
            setCheckedId(i, true);
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LinearLayout.LayoutParams;
    }

    public void clearCheck() {
        check(-1);
    }

    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LinearLayout.LayoutParams(getContext(), attributeSet);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return this.mOnCheckedChangeListener;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int i = this.mCheckedId;
        if (i != -1) {
            this.mProtectFromCheckedChange = true;
            setCheckedStateForView(i, true);
            this.mProtectFromCheckedChange = false;
            setCheckedId(this.mCheckedId, true);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener) {
        ViewGroup.OnHierarchyChangeListener unused = this.mPassThroughListener.mOnHierarchyChangeListener = onHierarchyChangeListener;
    }
}
