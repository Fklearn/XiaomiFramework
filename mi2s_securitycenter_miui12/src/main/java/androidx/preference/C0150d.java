package androidx.preference;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/* renamed from: androidx.preference.d  reason: case insensitive filesystem */
final class C0150d extends Preference {
    private long mId;

    C0150d(Context context, List<Preference> list, long j) {
        super(context);
        a();
        a(list);
        this.mId = j + 1000000;
    }

    private void a() {
        setLayoutResource(F.expand_button);
        setIcon(D.ic_arrow_down_24dp);
        setTitle(G.expand_button_title);
        setOrder(999);
    }

    private void a(List<Preference> list) {
        ArrayList arrayList = new ArrayList();
        CharSequence charSequence = null;
        for (Preference next : list) {
            CharSequence title = next.getTitle();
            boolean z = next instanceof PreferenceGroup;
            if (z && !TextUtils.isEmpty(title)) {
                arrayList.add((PreferenceGroup) next);
            }
            if (arrayList.contains(next.getParent())) {
                if (z) {
                    arrayList.add((PreferenceGroup) next);
                }
            } else if (!TextUtils.isEmpty(title)) {
                if (charSequence == null) {
                    charSequence = title;
                } else {
                    charSequence = getContext().getString(G.summary_collapsed_preference_list, new Object[]{charSequence, title});
                }
            }
        }
        setSummary(charSequence);
    }

    /* access modifiers changed from: package-private */
    public long getId() {
        return this.mId;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        a2.a(false);
    }
}
