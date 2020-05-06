package androidx.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class K extends ToggleButton {

    /* renamed from: a  reason: collision with root package name */
    private final H f509a;

    public K(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 16842827);
    }

    public K(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        qa.a((View) this, getContext());
        this.f509a = new H(this);
        this.f509a.a(attributeSet, i);
    }
}
