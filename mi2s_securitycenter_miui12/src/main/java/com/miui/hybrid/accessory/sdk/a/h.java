package com.miui.hybrid.accessory.sdk.a;

public enum h {
    OK(0),
    NO_UPDATE(1),
    NO_AVAILABLE_PACKAGE(2),
    NO_ANY_PACKAGE(3),
    NEED_CHECK_UPDATE(4),
    OFFLINE(5);
    
    private final int g;

    private h(int i) {
        this.g = i;
    }

    public static h a(int i) {
        if (i == 0) {
            return OK;
        }
        if (i == 1) {
            return NO_UPDATE;
        }
        if (i == 2) {
            return NO_AVAILABLE_PACKAGE;
        }
        if (i == 3) {
            return NO_ANY_PACKAGE;
        }
        if (i == 4) {
            return NEED_CHECK_UPDATE;
        }
        if (i != 5) {
            return null;
        }
        return OFFLINE;
    }
}
