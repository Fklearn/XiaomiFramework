package miuix.preference;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Ja;
import androidx.preference.A;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.TwoStatePreference;
import androidx.preference.x;
import androidx.recyclerview.widget.RecyclerView;
import d.a.b;
import d.a.j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.external.graphics.TaggingDrawable;
import miui.util.AttributeResolver;

class u extends x {
    private static final int[] g = {16842915, 16842916, 16842917, 16842918, v.state_no_title};
    private static final int[] h = {16842915};
    private static final int[] i = {16842916};
    private static final int[] j = {16842917};
    private static final int[] k = {16842918};
    private static final int[] l = {v.state_no_title};
    /* access modifiers changed from: private */
    public a[] m = new a[getItemCount()];
    private RecyclerView.c n = new t(this);
    private int o;
    private int p;
    private int q;
    private RecyclerView r;

    class a {

        /* renamed from: a  reason: collision with root package name */
        int[] f8915a;

        /* renamed from: b  reason: collision with root package name */
        int f8916b;

        a() {
        }
    }

    static {
        Arrays.sort(g);
    }

    public u(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        this.o = preferenceGroup.getContext().getResources().getDimensionPixelSize(w.miuix_preference_item_padding_start);
        this.p = AttributeResolver.resolveColor(preferenceGroup.getContext(), v.checkablePreferenceItemColorFilterChecked);
        this.q = AttributeResolver.resolveColor(preferenceGroup.getContext(), v.checkablePreferenceItemColorFilterNormal);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0022  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(androidx.preference.Preference r6, int r7) {
        /*
            r5 = this;
            if (r7 < 0) goto L_0x0019
            miuix.preference.u$a[] r0 = r5.m
            int r1 = r0.length
            if (r7 >= r1) goto L_0x0019
            r1 = r0[r7]
            if (r1 != 0) goto L_0x0012
            miuix.preference.u$a r1 = new miuix.preference.u$a
            r1.<init>()
            r0[r7] = r1
        L_0x0012:
            miuix.preference.u$a[] r0 = r5.m
            r0 = r0[r7]
            int[] r0 = r0.f8915a
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            if (r0 != 0) goto L_0x008d
            androidx.preference.PreferenceGroup r0 = r6.getParent()
            if (r0 == 0) goto L_0x008d
            java.util.List r0 = r5.c((androidx.preference.PreferenceGroup) r0)
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x002d
            return
        L_0x002d:
            int r1 = r0.size()
            r2 = 1
            r3 = 0
            if (r1 != r2) goto L_0x0038
            int[] r0 = h
            goto L_0x0060
        L_0x0038:
            java.lang.Object r1 = r0.get(r3)
            androidx.preference.Preference r1 = (androidx.preference.Preference) r1
            int r1 = r6.compareTo((androidx.preference.Preference) r1)
            if (r1 != 0) goto L_0x0048
            int[] r0 = i
            r2 = 2
            goto L_0x0060
        L_0x0048:
            int r1 = r0.size()
            int r1 = r1 - r2
            java.lang.Object r0 = r0.get(r1)
            androidx.preference.Preference r0 = (androidx.preference.Preference) r0
            int r0 = r6.compareTo((androidx.preference.Preference) r0)
            if (r0 != 0) goto L_0x005d
            int[] r0 = k
            r2 = 4
            goto L_0x0060
        L_0x005d:
            int[] r0 = j
            r2 = 3
        L_0x0060:
            boolean r1 = r6 instanceof androidx.preference.PreferenceCategory
            if (r1 == 0) goto L_0x0083
            androidx.preference.PreferenceCategory r6 = (androidx.preference.PreferenceCategory) r6
            java.lang.CharSequence r6 = r6.getTitle()
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0083
            int[] r6 = l
            int r1 = r6.length
            int r4 = r0.length
            int r1 = r1 + r4
            int[] r1 = new int[r1]
            int r4 = r6.length
            java.lang.System.arraycopy(r6, r3, r1, r3, r4)
            int[] r6 = l
            int r6 = r6.length
            int r4 = r0.length
            java.lang.System.arraycopy(r0, r3, r1, r6, r4)
            r0 = r1
        L_0x0083:
            miuix.preference.u$a[] r6 = r5.m
            r1 = r6[r7]
            r1.f8915a = r0
            r6 = r6[r7]
            r6.f8916b = r2
        L_0x008d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.preference.u.a(androidx.preference.Preference, int):void");
    }

    private List<Preference> c(PreferenceGroup preferenceGroup) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < preferenceGroup.c(); i2++) {
            Preference a2 = preferenceGroup.a(i2);
            if (a2.isVisible()) {
                arrayList.add(a2);
            }
        }
        return arrayList;
    }

    private boolean e(Preference preference) {
        return !(preference instanceof PreferenceCategory) && !(preference instanceof DropDownPreference) && (!(preference instanceof j) || ((j) preference).a());
    }

    private boolean f(Preference preference) {
        return (preference.getIntent() == null && preference.getFragment() == null && (preference.getOnPreferenceClickListener() == null || (preference instanceof TwoStatePreference)) && !(preference instanceof DialogPreference)) ? false : true;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull A a2, int i2) {
        super.a(a2, i2);
        Preference a3 = a(i2);
        a(a3, i2);
        int[] iArr = this.m[i2].f8915a;
        Drawable background = a2.itemView.getBackground();
        if ((background instanceof StateListDrawable) && TaggingDrawable.containsTagState((StateListDrawable) background, g)) {
            TaggingDrawable taggingDrawable = new TaggingDrawable(background);
            a2.itemView.setBackground(taggingDrawable);
            background = taggingDrawable;
        }
        if (background instanceof TaggingDrawable) {
            TaggingDrawable taggingDrawable2 = (TaggingDrawable) background;
            if (iArr != null) {
                taggingDrawable2.setTaggingState(iArr);
            }
            Rect rect = new Rect();
            if (taggingDrawable2.getPadding(rect)) {
                if (a3.getParent() instanceof RadioSetPreferenceCategory) {
                    taggingDrawable2.setColorFilter(((RadioSetPreferenceCategory) a3.getParent()).isChecked() ? this.p : this.q, PorterDuff.Mode.SRC_OVER);
                    RecyclerView recyclerView = this.r;
                    if (recyclerView != null) {
                        if (Ja.a(recyclerView)) {
                            rect.right += this.o;
                        } else {
                            rect.left += this.o;
                        }
                    }
                } else {
                    taggingDrawable2.setColorFilter((ColorFilter) null);
                }
                a2.itemView.setPadding(rect.left, rect.top, rect.right, rect.bottom);
            }
            if ((a3 instanceof RadioButtonPreference) && ((RadioButtonPreference) a3).isChecked()) {
                taggingDrawable2.setTaggingState(new int[]{16842912});
            }
        }
        View findViewById = a2.itemView.findViewById(x.arrow_right);
        if (findViewById != null) {
            findViewById.setVisibility(f(a3) ? 0 : 8);
        }
        if (e(a3)) {
            j jVar = b.a(a2.itemView).touch();
            jVar.a(1.0f, new j.a[0]);
            jVar.b(a2.itemView, new d.a.a.a[0]);
        }
    }

    /* access modifiers changed from: package-private */
    public int b(int i2) {
        return this.m[i2].f8916b;
    }

    public void c(Preference preference) {
        Preference a2;
        super.c(preference);
        String dependency = preference.getDependency();
        if (!TextUtils.isEmpty(dependency) && (a2 = preference.getPreferenceManager().a((CharSequence) dependency)) != null) {
            preference.setVisible(preference instanceof PreferenceCategory ? a2 instanceof TwoStatePreference ? ((TwoStatePreference) a2).isChecked() : a2.isEnabled() : preference.isEnabled());
        }
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(this.n);
        this.r = recyclerView;
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(this.n);
        this.r = null;
    }
}
