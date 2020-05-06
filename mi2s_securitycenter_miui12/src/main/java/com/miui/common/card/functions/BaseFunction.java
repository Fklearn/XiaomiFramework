package com.miui.common.card.functions;

import android.view.View;

public class BaseFunction implements View.OnClickListener {
    private String action;

    public BaseFunction() {
        this((String) null);
    }

    public BaseFunction(String str) {
        this.action = str;
    }

    public String getAction() {
        return this.action;
    }

    public void onClick(View view) {
    }

    public void onNegativeButtonClick() {
    }
}
