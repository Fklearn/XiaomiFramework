package com.miui.firstaidkit;

import com.miui.securitycenter.R;

public enum n {
    PERFORMANCE(R.string.first_aid_item1_content),
    INTERNET(R.string.first_aid_item2_content),
    OPERATION(R.string.first_aid_item3_content),
    CONSUME_POWER(R.string.first_aid_item4_content),
    OTHER(R.string.first_aid_item5_content);
    
    private int g;

    private n(int i) {
        this.g = i;
    }
}
