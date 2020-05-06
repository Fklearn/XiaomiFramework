package com.miui.maml.util.net;

public class CipherException extends Exception {
    public CipherException(String str) {
        super(str);
    }

    public CipherException(String str, Throwable th) {
        super(str, th);
    }
}
