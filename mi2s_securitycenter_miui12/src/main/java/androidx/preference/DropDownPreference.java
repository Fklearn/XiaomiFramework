package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropDownPreference extends ListPreference {
    private final ArrayAdapter k;
    private Spinner l;
    private final AdapterView.OnItemSelectedListener m;
    private final Context mContext;

    public DropDownPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, B.dropdownPreferenceStyle);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.m = new C0147a(this);
        this.mContext = context;
        this.k = k();
        l();
    }

    private int c(String str) {
        CharSequence[] i = i();
        if (str == null || i == null) {
            return -1;
        }
        for (int length = i.length - 1; length >= 0; length--) {
            if (i[length].equals(str)) {
                return length;
            }
        }
        return -1;
    }

    private void l() {
        this.k.clear();
        if (g() != null) {
            for (CharSequence charSequence : g()) {
                this.k.add(charSequence.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public ArrayAdapter k() {
        return new ArrayAdapter(this.mContext, 17367049);
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        super.notifyChanged();
        ArrayAdapter arrayAdapter = this.k;
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void onBindViewHolder(A a2) {
        this.l = (Spinner) a2.itemView.findViewById(E.spinner);
        this.l.setAdapter(this.k);
        this.l.setOnItemSelectedListener(this.m);
        this.l.setSelection(c(j()));
        super.onBindViewHolder(a2);
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        this.l.performClick();
    }
}
