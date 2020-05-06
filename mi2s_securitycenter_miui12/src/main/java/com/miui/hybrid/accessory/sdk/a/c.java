package com.miui.hybrid.accessory.sdk.a;

public enum c {
    Beta(1),
    Auditing(2),
    Rejected(3),
    Online(4),
    Offline(5);
    
    private final int f;

    private c(int i) {
        this.f = i;
    }

    public static c a(int i) {
        if (i == 1) {
            return Beta;
        }
        if (i == 2) {
            return Auditing;
        }
        if (i == 3) {
            return Rejected;
        }
        if (i == 4) {
            return Online;
        }
        if (i != 5) {
            return null;
        }
        return Offline;
    }
}
