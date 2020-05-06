package com.miui.antivirus.model;

import com.miui.antivirus.model.e;

public class j extends e {
    private boolean w;
    private boolean x;

    public j(e.b bVar) {
        this.mCardType = bVar;
        this.i = e.a.SYSTEM;
    }

    public void g(boolean z) {
        this.x = z;
    }

    public void h(boolean z) {
        this.w = z;
    }

    public boolean x() {
        return this.x;
    }

    public boolean y() {
        return this.w;
    }
}
