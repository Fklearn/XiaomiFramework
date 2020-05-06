package com.miui.securityscan.scanner;

import android.os.Handler;
import android.os.Message;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.L;
import com.miui.securityscan.b.n;
import java.lang.ref.WeakReference;

public class w extends Handler {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7934a;

    public w(L l) {
        this.f7934a = new WeakReference<>(l);
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        L l = (L) this.f7934a.get();
        if (l != null) {
            int i = message.what;
            if (i != 102) {
                switch (i) {
                    case 106:
                        l.c();
                        return;
                    case 107:
                        CardViewAdapter cardViewAdapter = l.G;
                        if (cardViewAdapter != null) {
                            cardViewAdapter.notifyDataSetChanged();
                            l.q();
                            return;
                        }
                        return;
                    case 108:
                        l.h();
                        return;
                    case 109:
                        l.a((BaseCardModel) message.obj);
                        return;
                    case 110:
                        l.s();
                        return;
                    default:
                        return;
                }
            } else {
                l.Y.f7623b = false;
                O.a(l.getContext()).a((n) l.Y);
            }
        }
    }
}
