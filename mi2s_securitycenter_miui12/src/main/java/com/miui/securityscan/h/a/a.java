package com.miui.securityscan.h.a;

import android.widget.Toast;
import com.miui.securitycenter.Application;

public class a {
    public static void a(int i) {
        c cVar = new c(Application.d());
        cVar.setIntegral(i);
        Toast toast = new Toast(Application.d());
        toast.setDuration(1);
        toast.setView(cVar);
        toast.show();
    }
}
