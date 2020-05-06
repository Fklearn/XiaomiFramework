package androidx.core.view.a;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

public final class a extends ClickableSpan {

    /* renamed from: a  reason: collision with root package name */
    private final int f800a;

    /* renamed from: b  reason: collision with root package name */
    private final c f801b;

    /* renamed from: c  reason: collision with root package name */
    private final int f802c;

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public a(int i, c cVar, int i2) {
        this.f800a = i;
        this.f801b = cVar;
        this.f802c = i2;
    }

    public void onClick(@NonNull View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", this.f800a);
        this.f801b.a(this.f802c, bundle);
    }
}
