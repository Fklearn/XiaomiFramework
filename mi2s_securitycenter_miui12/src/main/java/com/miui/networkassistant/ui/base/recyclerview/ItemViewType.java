package com.miui.networkassistant.ui.base.recyclerview;

import android.view.ViewGroup;
import com.miui.networkassistant.ui.base.recyclerview.BaseEntity;
import com.miui.networkassistant.ui.base.recyclerview.MultiTypeAdapter;

public interface ItemViewType<T extends BaseEntity> {
    boolean checkType(T t, int i);

    void onBindViewHolder(MultiTypeAdapter.ViewHolder viewHolder, T t, int i);

    MultiTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup);
}
