package androidx.viewpager.widget;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class a {

    /* renamed from: a  reason: collision with root package name */
    private final DataSetObservable f1297a = new DataSetObservable();

    /* renamed from: b  reason: collision with root package name */
    private DataSetObserver f1298b;

    public float a(int i) {
        return 1.0f;
    }

    public abstract int a();

    public int a(@NonNull Object obj) {
        return -1;
    }

    @NonNull
    public abstract Object a(@NonNull ViewGroup viewGroup, int i);

    /* access modifiers changed from: package-private */
    public void a(DataSetObserver dataSetObserver) {
        synchronized (this) {
            this.f1298b = dataSetObserver;
        }
    }

    public void a(@Nullable Parcelable parcelable, @Nullable ClassLoader classLoader) {
    }

    @Deprecated
    public void a(@NonNull View view) {
    }

    @Deprecated
    public void a(@NonNull View view, int i, @NonNull Object obj) {
    }

    public void a(@NonNull ViewGroup viewGroup) {
        a((View) viewGroup);
    }

    public abstract void a(@NonNull ViewGroup viewGroup, int i, @NonNull Object obj);

    public abstract boolean a(@NonNull View view, @NonNull Object obj);

    @Nullable
    public Parcelable b() {
        return null;
    }

    @Deprecated
    public void b(@NonNull View view) {
    }

    public void b(@NonNull ViewGroup viewGroup) {
        b((View) viewGroup);
    }

    public void b(@NonNull ViewGroup viewGroup, int i, @NonNull Object obj) {
        a((View) viewGroup, i, obj);
    }
}
