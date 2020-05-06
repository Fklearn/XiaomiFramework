package com.miui.hybrid.accessory.a.f;

import com.miui.hybrid.accessory.a.f.b.k;

public class e {
    public static <T extends a<T, ?>> void a(T t, byte[] bArr) {
        if (bArr != null) {
            new c(new k.a(true, true, bArr.length)).a(t, bArr);
            return;
        }
        throw new d("the message byte is empty.");
    }
}
