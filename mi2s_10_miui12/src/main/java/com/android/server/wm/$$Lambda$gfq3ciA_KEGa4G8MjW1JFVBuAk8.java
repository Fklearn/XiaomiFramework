package com.android.server.wm;

import android.app.ActivityManagerInternal;
import android.os.Bundle;
import java.util.function.BiConsumer;

/* renamed from: com.android.server.wm.-$$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8 implements BiConsumer {
    public static final /* synthetic */ $$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8 INSTANCE = new $$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8();

    private /* synthetic */ $$Lambda$gfq3ciA_KEGa4G8MjW1JFVBuAk8() {
    }

    public final void accept(Object obj, Object obj2) {
        ((ActivityManagerInternal) obj).startActivityAsUserEmpty((Bundle) obj2);
    }
}
