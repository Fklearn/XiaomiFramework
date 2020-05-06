package com.miui.powercenter.utils;

import android.os.Message;
import java.util.Observable;

public class i extends Observable {

    /* renamed from: a  reason: collision with root package name */
    private static volatile i f7308a;

    public static i a() {
        if (f7308a == null) {
            synchronized (i.class) {
                if (f7308a == null) {
                    f7308a = new i();
                }
            }
        }
        return f7308a;
    }

    public final void a(int i, Object obj) {
        Message obtain = Message.obtain();
        obtain.what = i;
        obtain.obj = obj;
        a(obtain);
    }

    public void a(Message message) {
        setChanged();
        notifyObservers(message);
    }
}
