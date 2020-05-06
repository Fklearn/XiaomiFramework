package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private int f829a;

    /* renamed from: b  reason: collision with root package name */
    private int f830b;

    public p(@NonNull ViewGroup viewGroup) {
    }

    public int a() {
        return this.f829a | this.f830b;
    }

    public void a(@NonNull View view, int i) {
        if (i == 1) {
            this.f830b = 0;
        } else {
            this.f829a = 0;
        }
    }

    public void a(@NonNull View view, @NonNull View view2, int i) {
        a(view, view2, i, 0);
    }

    public void a(@NonNull View view, @NonNull View view2, int i, int i2) {
        if (i2 == 1) {
            this.f830b = i;
        } else {
            this.f829a = i;
        }
    }
}
