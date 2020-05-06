package com.miui.networkassistant.ui.view;

import android.view.View;
import android.widget.Checkable;

public interface RadioCheckable extends Checkable {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean z);
    }

    void addOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener);

    void removeOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener);
}
