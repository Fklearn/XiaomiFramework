package androidx.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertController;

public class k extends z implements DialogInterface {

    /* renamed from: c  reason: collision with root package name */
    final AlertController f314c = new AlertController(getContext(), this, getWindow());

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private final AlertController.a f315a;

        /* renamed from: b  reason: collision with root package name */
        private final int f316b;

        public a(@NonNull Context context) {
            this(context, k.a(context, 0));
        }

        public a(@NonNull Context context, @StyleRes int i) {
            this.f315a = new AlertController.a(new ContextThemeWrapper(context, k.a(context, i)));
            this.f316b = i;
        }

        public a a(DialogInterface.OnKeyListener onKeyListener) {
            this.f315a.u = onKeyListener;
            return this;
        }

        public a a(@Nullable Drawable drawable) {
            this.f315a.f240d = drawable;
            return this;
        }

        public a a(@Nullable View view) {
            this.f315a.g = view;
            return this;
        }

        public a a(ListAdapter listAdapter, int i, DialogInterface.OnClickListener onClickListener) {
            AlertController.a aVar = this.f315a;
            aVar.w = listAdapter;
            aVar.x = onClickListener;
            aVar.I = i;
            aVar.H = true;
            return this;
        }

        public a a(ListAdapter listAdapter, DialogInterface.OnClickListener onClickListener) {
            AlertController.a aVar = this.f315a;
            aVar.w = listAdapter;
            aVar.x = onClickListener;
            return this;
        }

        public a a(@Nullable CharSequence charSequence) {
            this.f315a.f = charSequence;
            return this;
        }

        @NonNull
        public k a() {
            k kVar = new k(this.f315a.f237a, this.f316b);
            this.f315a.a(kVar.f314c);
            kVar.setCancelable(this.f315a.r);
            if (this.f315a.r) {
                kVar.setCanceledOnTouchOutside(true);
            }
            kVar.setOnCancelListener(this.f315a.s);
            kVar.setOnDismissListener(this.f315a.t);
            DialogInterface.OnKeyListener onKeyListener = this.f315a.u;
            if (onKeyListener != null) {
                kVar.setOnKeyListener(onKeyListener);
            }
            return kVar;
        }

        @NonNull
        public Context b() {
            return this.f315a.f237a;
        }
    }

    protected k(@NonNull Context context, @StyleRes int i) {
        super(context, a(context, i));
    }

    static int a(@NonNull Context context, @StyleRes int i) {
        if (((i >>> 24) & 255) >= 1) {
            return i;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(a.a.a.alertDialogTheme, typedValue, true);
        return typedValue.resourceId;
    }

    public ListView b() {
        return this.f314c.a();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f314c.b();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.f314c.a(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (this.f314c.b(i, keyEvent)) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        this.f314c.b(charSequence);
    }
}
