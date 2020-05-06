package a.a.d;

import a.a.i;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import androidx.annotation.StyleRes;

public class d extends ContextWrapper {

    /* renamed from: a  reason: collision with root package name */
    private int f9a;

    /* renamed from: b  reason: collision with root package name */
    private Resources.Theme f10b;

    /* renamed from: c  reason: collision with root package name */
    private LayoutInflater f11c;

    /* renamed from: d  reason: collision with root package name */
    private Configuration f12d;
    private Resources e;

    public d() {
        super((Context) null);
    }

    public d(Context context, @StyleRes int i) {
        super(context);
        this.f9a = i;
    }

    public d(Context context, Resources.Theme theme) {
        super(context);
        this.f10b = theme;
    }

    private Resources b() {
        Resources resources;
        if (this.e == null) {
            Configuration configuration = this.f12d;
            if (configuration == null) {
                resources = super.getResources();
            } else if (Build.VERSION.SDK_INT >= 17) {
                resources = createConfigurationContext(configuration).getResources();
            }
            this.e = resources;
        }
        return this.e;
    }

    private void c() {
        boolean z = this.f10b == null;
        if (z) {
            this.f10b = getResources().newTheme();
            Resources.Theme theme = getBaseContext().getTheme();
            if (theme != null) {
                this.f10b.setTo(theme);
            }
        }
        a(this.f10b, this.f9a, z);
    }

    public int a() {
        return this.f9a;
    }

    /* access modifiers changed from: protected */
    public void a(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(i, true);
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    public Resources getResources() {
        return b();
    }

    public Object getSystemService(String str) {
        if (!"layout_inflater".equals(str)) {
            return getBaseContext().getSystemService(str);
        }
        if (this.f11c == null) {
            this.f11c = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return this.f11c;
    }

    public Resources.Theme getTheme() {
        Resources.Theme theme = this.f10b;
        if (theme != null) {
            return theme;
        }
        if (this.f9a == 0) {
            this.f9a = i.Theme_AppCompat_Light;
        }
        c();
        return this.f10b;
    }

    public void setTheme(int i) {
        if (this.f9a != i) {
            this.f9a = i;
            c();
        }
    }
}
