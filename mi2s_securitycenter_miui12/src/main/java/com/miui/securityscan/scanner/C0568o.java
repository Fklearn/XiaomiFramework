package com.miui.securityscan.scanner;

import com.miui.securitycenter.R;

/* renamed from: com.miui.securityscan.scanner.o  reason: case insensitive filesystem */
public enum C0568o {
    SYSTEM_CONFIG(R.string.optmizingbar_title_system),
    CLEAR_ACCELERATION(R.string.optmizingbar_title_clear),
    SYSTEM_APP(R.string.optmizingbar_title_security);
    
    private String e;
    private int f;

    private C0568o(int i) {
        this.f = i;
    }

    public int a() {
        return this.f;
    }

    public void a(String str) {
        this.e = str;
    }
}
