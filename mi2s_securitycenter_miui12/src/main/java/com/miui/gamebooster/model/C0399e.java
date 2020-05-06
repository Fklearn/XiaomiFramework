package com.miui.gamebooster.model;

import android.util.SparseIntArray;
import android.view.View;
import com.miui.gamebooster.a.C0328f;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.model.e  reason: case insensitive filesystem */
public abstract class C0399e {

    /* renamed from: a  reason: collision with root package name */
    private static final SparseIntArray f4557a = new SparseIntArray();

    /* renamed from: b  reason: collision with root package name */
    protected transient int f4558b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f4559c;

    static {
        f4557a.put(R.layout.gb_wonderful_moment_video_list_header, 0);
        f4557a.put(R.layout.gb_wonderful_moment_video_list_item, 1);
    }

    public C0399e(int i) {
        this.f4558b = i;
    }

    public static int c() {
        return f4557a.size();
    }

    public int a() {
        return this.f4558b;
    }

    public abstract C0328f a(View view);

    public void a(boolean z) {
        this.f4559c = z;
    }

    public int b() {
        return f4557a.get(this.f4558b);
    }

    public boolean d() {
        return this.f4559c;
    }
}
