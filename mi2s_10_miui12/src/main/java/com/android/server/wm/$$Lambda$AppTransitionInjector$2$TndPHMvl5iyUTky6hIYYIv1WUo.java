package com.android.server.wm;

import android.os.IRemoteCallback;
import java.util.function.Consumer;

/* renamed from: com.android.server.wm.-$$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky-6hIYYIv1WUo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky6hIYYIv1WUo implements Consumer {
    public static final /* synthetic */ $$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky6hIYYIv1WUo INSTANCE = new $$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky6hIYYIv1WUo();

    private /* synthetic */ $$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky6hIYYIv1WUo() {
    }

    public final void accept(Object obj) {
        AppTransitionInjector.doAnimationCallback((IRemoteCallback) obj);
    }
}
