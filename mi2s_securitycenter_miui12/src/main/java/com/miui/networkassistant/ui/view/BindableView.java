package com.miui.networkassistant.ui.view;

public interface BindableView<D> {
    void fillData(D d2);

    void fillData(D d2, String str);
}
