package com.miui.internal.vip.protocol;

public class ErrResult {
    public int code;
    public String err;

    public String toString() {
        return "ErrResult{err='" + this.err + '\'' + ", code=" + this.code + '}';
    }
}
