package androidx.preference;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public class z {

    /* renamed from: a  reason: collision with root package name */
    private Context f1075a;

    /* renamed from: b  reason: collision with root package name */
    private long f1076b = 0;
    @Nullable

    /* renamed from: c  reason: collision with root package name */
    private SharedPreferences f1077c;
    @Nullable

    /* renamed from: d  reason: collision with root package name */
    private C0159m f1078d;
    @Nullable
    private SharedPreferences.Editor e;
    private boolean f;
    private String g;
    private int h;
    private int i = 0;
    private PreferenceScreen j;
    private d k;
    private c l;
    private a m;
    private b n;

    public interface a {
        void onDisplayPreferenceDialog(Preference preference);
    }

    public interface b {
        void onNavigateToScreen(PreferenceScreen preferenceScreen);
    }

    public interface c {
        boolean onPreferenceTreeClick(Preference preference);
    }

    public static abstract class d {
        public abstract boolean a(Preference preference, Preference preference2);

        public abstract boolean b(Preference preference, Preference preference2);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public z(Context context) {
        this.f1075a = context;
        a(a(context));
    }

    private static String a(Context context) {
        return context.getPackageName() + "_preferences";
    }

    private void a(boolean z) {
        SharedPreferences.Editor editor;
        if (!z && (editor = this.e) != null) {
            editor.apply();
        }
        this.f = z;
    }

    public Context a() {
        return this.f1075a;
    }

    @Nullable
    public <T extends Preference> T a(@NonNull CharSequence charSequence) {
        PreferenceScreen preferenceScreen = this.j;
        if (preferenceScreen == null) {
            return null;
        }
        return preferenceScreen.a(charSequence);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PreferenceScreen a(Context context, int i2, PreferenceScreen preferenceScreen) {
        a(true);
        PreferenceScreen preferenceScreen2 = (PreferenceScreen) new y(context, this).a(i2, (PreferenceGroup) preferenceScreen);
        preferenceScreen2.onAttachedToHierarchy(this);
        a(false);
        return preferenceScreen2;
    }

    public void a(Preference preference) {
        a aVar = this.m;
        if (aVar != null) {
            aVar.onDisplayPreferenceDialog(preference);
        }
    }

    public void a(a aVar) {
        this.m = aVar;
    }

    public void a(b bVar) {
        this.n = bVar;
    }

    public void a(c cVar) {
        this.l = cVar;
    }

    public void a(String str) {
        this.g = str;
        this.f1077c = null;
    }

    public boolean a(PreferenceScreen preferenceScreen) {
        PreferenceScreen preferenceScreen2 = this.j;
        if (preferenceScreen == preferenceScreen2) {
            return false;
        }
        if (preferenceScreen2 != null) {
            preferenceScreen2.onDetached();
        }
        this.j = preferenceScreen;
        return true;
    }

    /* access modifiers changed from: package-private */
    public SharedPreferences.Editor b() {
        if (this.f1078d != null) {
            return null;
        }
        if (!this.f) {
            return i().edit();
        }
        if (this.e == null) {
            this.e = i().edit();
        }
        return this.e;
    }

    /* access modifiers changed from: package-private */
    public long c() {
        long j2;
        synchronized (this) {
            j2 = this.f1076b;
            this.f1076b = 1 + j2;
        }
        return j2;
    }

    public b d() {
        return this.n;
    }

    public c e() {
        return this.l;
    }

    public d f() {
        return this.k;
    }

    @Nullable
    public C0159m g() {
        return this.f1078d;
    }

    public PreferenceScreen h() {
        return this.j;
    }

    public SharedPreferences i() {
        if (g() != null) {
            return null;
        }
        if (this.f1077c == null) {
            this.f1077c = (this.i != 1 ? this.f1075a : androidx.core.content.a.a(this.f1075a)).getSharedPreferences(this.g, this.h);
        }
        return this.f1077c;
    }

    /* access modifiers changed from: package-private */
    public boolean j() {
        return !this.f;
    }
}
