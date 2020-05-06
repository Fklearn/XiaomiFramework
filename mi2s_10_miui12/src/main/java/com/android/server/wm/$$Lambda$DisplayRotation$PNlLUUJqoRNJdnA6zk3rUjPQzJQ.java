package com.android.server.wm;

import java.util.function.Consumer;

/* renamed from: com.android.server.wm.-$$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ implements Consumer {
    public static final /* synthetic */ $$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ INSTANCE = new $$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ();

    private /* synthetic */ $$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ() {
    }

    public final void accept(Object obj) {
        ((DisplayRotation) obj).unregisterReceiver();
    }
}
