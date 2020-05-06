package com.miui.gamebooster.customview.b;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public final class g extends RecyclerView.u {

    /* renamed from: a  reason: collision with root package name */
    private final Context f4186a;

    /* renamed from: b  reason: collision with root package name */
    private final SparseArray<View> f4187b = new SparseArray<>();

    /* renamed from: c  reason: collision with root package name */
    private Object f4188c = null;

    private g(Context context, View view) {
        super(view);
        this.f4186a = context;
    }

    public static g a(Context context, View view) {
        return new g(context, view);
    }

    public static g a(Context context, ViewGroup viewGroup, int i, boolean z) {
        View inflate = LayoutInflater.from(context).inflate(i, viewGroup, false);
        if (!z) {
            return new g(context, inflate);
        }
        if ((inflate instanceof FrameLayout) || (inflate instanceof RelativeLayout)) {
            return new g(context, inflate);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(inflate);
        return new g(context, frameLayout);
    }

    public <T extends View> T a() {
        return this.itemView;
    }

    public g a(int i, String str) {
        ((TextView) b(i)).setText(str);
        return this;
    }

    public Context b() {
        return this.f4186a;
    }

    public <T extends View> T b(int i) {
        T t = (View) this.f4187b.get(i);
        if (t != null) {
            return t;
        }
        T findViewById = this.itemView.findViewById(i);
        this.f4187b.put(i, findViewById);
        return findViewById;
    }
}
