package com.android.server.wm;

import android.app.ActivityManagerInternal;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import com.android.internal.util.function.HeptConsumer;

/* renamed from: com.android.server.wm.-$$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0 implements HeptConsumer {
    public static final /* synthetic */ $$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0 INSTANCE = new $$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0();

    private /* synthetic */ $$Lambda$YnRx7DG_XWEmLQfCe6Y23Ju81c0() {
    }

    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        ((ActivityManagerInternal) obj).startProcess((String) obj2, (ApplicationInfo) obj3, ((Boolean) obj4).booleanValue(), (String) obj5, (ComponentName) obj6, (String) obj7);
    }
}
