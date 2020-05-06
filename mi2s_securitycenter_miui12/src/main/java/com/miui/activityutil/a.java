package com.miui.activityutil;

import java.io.File;
import java.io.FileOutputStream;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    private final File f2252a;

    /* renamed from: b  reason: collision with root package name */
    private final File f2253b;

    public a(File file) {
        this.f2252a = file;
        this.f2253b = new File(file.getPath() + ".bak");
    }

    private File d() {
        return this.f2252a;
    }

    public final void a() {
        this.f2252a.delete();
        this.f2253b.delete();
    }

    public final FileOutputStream b() {
        if (this.f2252a.exists()) {
            this.f2252a.delete();
        }
        if (this.f2253b.exists()) {
            this.f2253b.delete();
        }
        return new FileOutputStream(this.f2253b);
    }

    public final boolean c() {
        boolean renameTo = this.f2253b.renameTo(this.f2252a);
        this.f2253b.delete();
        return renameTo;
    }
}
