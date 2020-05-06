package androidx.appcompat.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class sa extends ContextWrapper {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f652a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private static ArrayList<WeakReference<sa>> f653b;

    /* renamed from: c  reason: collision with root package name */
    private final Resources f654c;

    /* renamed from: d  reason: collision with root package name */
    private final Resources.Theme f655d;

    private sa(@NonNull Context context) {
        super(context);
        if (Ia.b()) {
            this.f654c = new Ia(this, context.getResources());
            this.f655d = this.f654c.newTheme();
            this.f655d.setTo(context.getTheme());
            return;
        }
        this.f654c = new ua(this, context.getResources());
        this.f655d = null;
    }

    public static Context a(@NonNull Context context) {
        if (!b(context)) {
            return context;
        }
        synchronized (f652a) {
            if (f653b == null) {
                f653b = new ArrayList<>();
            } else {
                for (int size = f653b.size() - 1; size >= 0; size--) {
                    WeakReference weakReference = f653b.get(size);
                    if (weakReference == null || weakReference.get() == null) {
                        f653b.remove(size);
                    }
                }
                for (int size2 = f653b.size() - 1; size2 >= 0; size2--) {
                    WeakReference weakReference2 = f653b.get(size2);
                    sa saVar = weakReference2 != null ? (sa) weakReference2.get() : null;
                    if (saVar != null && saVar.getBaseContext() == context) {
                        return saVar;
                    }
                }
            }
            sa saVar2 = new sa(context);
            f653b.add(new WeakReference(saVar2));
            return saVar2;
        }
    }

    private static boolean b(@NonNull Context context) {
        if ((context instanceof sa) || (context.getResources() instanceof ua) || (context.getResources() instanceof Ia)) {
            return false;
        }
        return Build.VERSION.SDK_INT < 21 || Ia.b();
    }

    public AssetManager getAssets() {
        return this.f654c.getAssets();
    }

    public Resources getResources() {
        return this.f654c;
    }

    public Resources.Theme getTheme() {
        Resources.Theme theme = this.f655d;
        return theme == null ? super.getTheme() : theme;
    }

    public void setTheme(int i) {
        Resources.Theme theme = this.f655d;
        if (theme == null) {
            super.setTheme(i);
        } else {
            theme.applyStyle(i, true);
        }
    }
}
