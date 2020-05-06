package com.miui.networkassistant.ui.base.recyclerview;

public abstract class BaseEntity {
    private Object mGroup;

    public Object getGroup() {
        return this.mGroup;
    }

    public void setGroup(Object obj) {
        this.mGroup = obj;
    }
}
