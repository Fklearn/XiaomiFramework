package com.miui.earthquakewarning.utils;

import android.os.Message;
import java.util.Observable;

public class MsgObservable extends Observable {
    private static volatile MsgObservable instance;

    public static MsgObservable getInstance() {
        if (instance == null) {
            synchronized (MsgObservable.class) {
                if (instance == null) {
                    instance = new MsgObservable();
                }
            }
        }
        return instance;
    }

    public void destroy() {
        deleteObservers();
    }

    public void notifyMsgObservers(Message message) {
        setChanged();
        notifyObservers(message);
    }

    public final void sendMessage(int i, Object obj) {
        Message obtain = Message.obtain();
        obtain.what = i;
        obtain.obj = obj;
        notifyMsgObservers(obtain);
    }
}
