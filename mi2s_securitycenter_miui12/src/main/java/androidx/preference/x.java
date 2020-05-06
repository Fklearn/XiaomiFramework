package androidx.preference;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.recyclerview.widget.C0174o;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class x extends RecyclerView.a<A> implements Preference.a, PreferenceGroup.b {

    /* renamed from: a  reason: collision with root package name */
    private PreferenceGroup f1064a;

    /* renamed from: b  reason: collision with root package name */
    private List<Preference> f1065b;

    /* renamed from: c  reason: collision with root package name */
    private List<Preference> f1066c;

    /* renamed from: d  reason: collision with root package name */
    private List<a> f1067d;
    private Handler e;
    private Runnable f = new u(this);

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        int f1068a;

        /* renamed from: b  reason: collision with root package name */
        int f1069b;

        /* renamed from: c  reason: collision with root package name */
        String f1070c;

        a(Preference preference) {
            this.f1070c = preference.getClass().getName();
            this.f1068a = preference.getLayoutResource();
            this.f1069b = preference.getWidgetLayoutResource();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof a)) {
                return false;
            }
            a aVar = (a) obj;
            return this.f1068a == aVar.f1068a && this.f1069b == aVar.f1069b && TextUtils.equals(this.f1070c, aVar.f1070c);
        }

        public int hashCode() {
            return ((((527 + this.f1068a) * 31) + this.f1069b) * 31) + this.f1070c.hashCode();
        }
    }

    public x(PreferenceGroup preferenceGroup) {
        this.f1064a = preferenceGroup;
        this.e = new Handler();
        this.f1064a.setOnPreferenceChangeInternalListener(this);
        this.f1065b = new ArrayList();
        this.f1066c = new ArrayList();
        this.f1067d = new ArrayList();
        PreferenceGroup preferenceGroup2 = this.f1064a;
        setHasStableIds(preferenceGroup2 instanceof PreferenceScreen ? ((PreferenceScreen) preferenceGroup2).g() : true);
        b();
    }

    private C0150d a(PreferenceGroup preferenceGroup, List<Preference> list) {
        C0150d dVar = new C0150d(preferenceGroup.getContext(), list, preferenceGroup.getId());
        dVar.setOnPreferenceClickListener(new w(this, preferenceGroup));
        return dVar;
    }

    private List<Preference> a(PreferenceGroup preferenceGroup) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int c2 = preferenceGroup.c();
        int i = 0;
        for (int i2 = 0; i2 < c2; i2++) {
            Preference a2 = preferenceGroup.a(i2);
            if (a2.isVisible()) {
                if (!b(preferenceGroup) || i < preferenceGroup.a()) {
                    arrayList.add(a2);
                } else {
                    arrayList2.add(a2);
                }
                if (!(a2 instanceof PreferenceGroup)) {
                    i++;
                } else {
                    PreferenceGroup preferenceGroup2 = (PreferenceGroup) a2;
                    if (!preferenceGroup2.d()) {
                        continue;
                    } else if (!b(preferenceGroup) || !b(preferenceGroup2)) {
                        for (Preference next : a(preferenceGroup2)) {
                            if (!b(preferenceGroup) || i < preferenceGroup.a()) {
                                arrayList.add(next);
                            } else {
                                arrayList2.add(next);
                            }
                            i++;
                        }
                    } else {
                        throw new IllegalStateException("Nesting an expandable group inside of another expandable group is not supported!");
                    }
                }
            }
        }
        if (b(preferenceGroup) && i > preferenceGroup.a()) {
            arrayList.add(a(preferenceGroup, (List<Preference>) arrayList2));
        }
        return arrayList;
    }

    private void a(List<Preference> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.f();
        int c2 = preferenceGroup.c();
        for (int i = 0; i < c2; i++) {
            Preference a2 = preferenceGroup.a(i);
            list.add(a2);
            a aVar = new a(a2);
            if (!this.f1067d.contains(aVar)) {
                this.f1067d.add(aVar);
            }
            if (a2 instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup2 = (PreferenceGroup) a2;
                if (preferenceGroup2.d()) {
                    a(list, preferenceGroup2);
                }
            }
            a2.setOnPreferenceChangeInternalListener(this);
        }
    }

    private boolean b(PreferenceGroup preferenceGroup) {
        return preferenceGroup.a() != Integer.MAX_VALUE;
    }

    public int a(String str) {
        int size = this.f1066c.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(str, this.f1066c.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }

    public Preference a(int i) {
        if (i < 0 || i >= getItemCount()) {
            return null;
        }
        return this.f1066c.get(i);
    }

    public void a(@NonNull A a2, int i) {
        a(i).onBindViewHolder(a2);
    }

    public void a(Preference preference) {
        d(preference);
    }

    public int b(Preference preference) {
        int size = this.f1066c.size();
        for (int i = 0; i < size; i++) {
            Preference preference2 = this.f1066c.get(i);
            if (preference2 != null && preference2.equals(preference)) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        for (Preference onPreferenceChangeInternalListener : this.f1065b) {
            onPreferenceChangeInternalListener.setOnPreferenceChangeInternalListener((Preference.a) null);
        }
        this.f1065b = new ArrayList(this.f1065b.size());
        a(this.f1065b, this.f1064a);
        List<Preference> list = this.f1066c;
        List<Preference> a2 = a(this.f1064a);
        this.f1066c = a2;
        z preferenceManager = this.f1064a.getPreferenceManager();
        if (preferenceManager == null || preferenceManager.f() == null) {
            notifyDataSetChanged();
        } else {
            C0174o.a(new v(this, list, a2, preferenceManager.f())).a((RecyclerView.a) this);
        }
        for (Preference clearWasDetached : this.f1065b) {
            clearWasDetached.clearWasDetached();
        }
    }

    public void c(Preference preference) {
        int indexOf = this.f1066c.indexOf(preference);
        if (indexOf != -1) {
            notifyItemChanged(indexOf, preference);
        }
    }

    public void d(Preference preference) {
        this.e.removeCallbacks(this.f);
        this.e.post(this.f);
    }

    public int getItemCount() {
        return this.f1066c.size();
    }

    public long getItemId(int i) {
        if (!hasStableIds()) {
            return -1;
        }
        return a(i).getId();
    }

    public int getItemViewType(int i) {
        a aVar = new a(a(i));
        int indexOf = this.f1067d.indexOf(aVar);
        if (indexOf != -1) {
            return indexOf;
        }
        int size = this.f1067d.size();
        this.f1067d.add(aVar);
        return size;
    }

    @NonNull
    public A onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        a aVar = this.f1067d.get(i);
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes((AttributeSet) null, I.BackgroundStyle);
        Drawable drawable = obtainStyledAttributes.getDrawable(I.BackgroundStyle_android_selectableItemBackground);
        if (drawable == null) {
            drawable = a.a.a.a.a.b(viewGroup.getContext(), 17301602);
        }
        obtainStyledAttributes.recycle();
        View inflate = from.inflate(aVar.f1068a, viewGroup, false);
        if (inflate.getBackground() == null) {
            ViewCompat.a(inflate, drawable);
        }
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(16908312);
        if (viewGroup2 != null) {
            int i2 = aVar.f1069b;
            if (i2 != 0) {
                from.inflate(i2, viewGroup2);
            } else {
                viewGroup2.setVisibility(8);
            }
        }
        return new A(inflate);
    }
}
