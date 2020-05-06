package com.miui.earthquakewarning.model;

import java.util.List;

public class WarningResult {
    private int code;
    private List<WarningModel> data;

    public int getCode() {
        return this.code;
    }

    public List<WarningModel> getData() {
        return this.data;
    }

    public void setCode(int i) {
        this.code = i;
    }

    public void setData(List<WarningModel> list) {
        this.data = list;
    }
}
