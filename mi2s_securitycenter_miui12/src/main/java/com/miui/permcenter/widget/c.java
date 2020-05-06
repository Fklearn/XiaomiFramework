package com.miui.permcenter.widget;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class c extends RecyclerView.f {

    /* renamed from: a  reason: collision with root package name */
    private int f6608a;

    /* renamed from: b  reason: collision with root package name */
    private int f6609b;

    public c(int i, int i2) {
        this.f6609b = i;
        this.f6608a = i2;
    }

    public void a(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.r rVar) {
        super.a(rect, view, recyclerView, rVar);
        rect.top = recyclerView.f(view) == 0 ? this.f6609b : this.f6608a;
    }
}
