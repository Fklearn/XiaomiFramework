package com.android.server.role;

import java.util.function.Consumer;

/* renamed from: com.android.server.role.-$$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co implements Consumer {
    public static final /* synthetic */ $$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co INSTANCE = new $$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co();

    private /* synthetic */ $$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co() {
    }

    public final void accept(Object obj) {
        ((RoleUserState) obj).writeFile();
    }
}
