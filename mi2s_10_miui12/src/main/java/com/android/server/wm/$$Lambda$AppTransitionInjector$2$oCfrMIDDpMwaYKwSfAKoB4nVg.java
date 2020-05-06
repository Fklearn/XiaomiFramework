package com.android.server.wm;

import android.os.IRemoteCallback;
import java.util.function.Consumer;

/* renamed from: com.android.server.wm.-$$Lambda$AppTransitionInjector$2$oCfrMIDD-pMwaYKwSfAKo-B4nVg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AppTransitionInjector$2$oCfrMIDDpMwaYKwSfAKoB4nVg implements Consumer {
    public static final /* synthetic */ $$Lambda$AppTransitionInjector$2$oCfrMIDDpMwaYKwSfAKoB4nVg INSTANCE = new $$Lambda$AppTransitionInjector$2$oCfrMIDDpMwaYKwSfAKoB4nVg();

    private /* synthetic */ $$Lambda$AppTransitionInjector$2$oCfrMIDDpMwaYKwSfAKoB4nVg() {
    }

    public final void accept(Object obj) {
        AppTransitionInjector.doAnimationCallback((IRemoteCallback) obj);
    }
}
