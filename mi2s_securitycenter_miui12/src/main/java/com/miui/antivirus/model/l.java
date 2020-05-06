package com.miui.antivirus.model;

import com.miui.antivirus.model.e;

public class l extends e {
    private boolean A = false;
    private boolean w = false;
    private boolean x = true;
    private boolean y = false;
    private boolean z = false;

    public enum a {
        CONNECTION,
        ENCRYPTION,
        FAKE,
        DNS,
        ARP
    }

    public l() {
        this.mCardType = e.b.WIFI;
    }

    public boolean A() {
        return this.z;
    }

    public boolean B() {
        return this.x;
    }

    public boolean C() {
        return this.y;
    }

    public boolean e() {
        return x() > 0;
    }

    public void g(boolean z2) {
        this.A = z2;
    }

    public void h(boolean z2) {
        this.w = z2;
    }

    public void i(boolean z2) {
        this.z = z2;
    }

    public void j(boolean z2) {
        this.x = z2;
    }

    public void k(boolean z2) {
        this.y = z2;
    }

    public int x() {
        return (this.x ^ true ? 1 : 0) + (this.y ? 1 : 0) + (this.z ? 1 : 0) + (this.A ? 1 : 0);
    }

    public boolean y() {
        return this.A;
    }

    public boolean z() {
        return this.w;
    }
}
