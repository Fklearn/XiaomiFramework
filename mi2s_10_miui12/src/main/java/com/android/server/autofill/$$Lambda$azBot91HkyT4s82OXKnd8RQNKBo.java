package com.android.server.autofill;

import java.util.function.Consumer;

/* renamed from: com.android.server.autofill.-$$Lambda$azBot91HkyT4s82OXKnd8RQNKBo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$azBot91HkyT4s82OXKnd8RQNKBo implements Consumer {
    public static final /* synthetic */ $$Lambda$azBot91HkyT4s82OXKnd8RQNKBo INSTANCE = new $$Lambda$azBot91HkyT4s82OXKnd8RQNKBo();

    private /* synthetic */ $$Lambda$azBot91HkyT4s82OXKnd8RQNKBo() {
    }

    public final void accept(Object obj) {
        ((RemoteFillService) obj).handleUnbind();
    }
}
