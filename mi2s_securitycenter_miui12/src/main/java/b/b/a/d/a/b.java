package b.b.a.d.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class b extends BaseExpandableListAdapter implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Map<Integer, Set<Integer>> f1329a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    protected int f1330b = 0;

    /* renamed from: c  reason: collision with root package name */
    protected Context f1331c;

    public b(Context context) {
        this.f1331c = context;
    }

    private void b(int i, boolean z) {
        if (!z) {
            for (Integer intValue : this.f1329a.get(Integer.valueOf(i))) {
                this.f1330b--;
                a(i, intValue.intValue(), z);
            }
            this.f1329a.remove(Integer.valueOf(i));
        } else {
            HashSet hashSet = new HashSet();
            if (!this.f1329a.containsKey(Integer.valueOf(i))) {
                this.f1329a.put(Integer.valueOf(i), hashSet);
            }
            for (int i2 = 0; i2 < getChildrenCount(i); i2++) {
                this.f1330b++;
                hashSet.add(Integer.valueOf(i2));
                a(i, i2, z);
            }
        }
        notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public abstract int a();

    /* access modifiers changed from: protected */
    public abstract View a(int i, int i2, boolean z, View view, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract View a(int i, boolean z, View view, ViewGroup viewGroup);

    public void a(int i) {
        if (getChildrenCount(i) == 0) {
            if (this.f1329a.containsKey(Integer.valueOf(i))) {
                this.f1330b--;
                this.f1329a.remove(Integer.valueOf(i));
                a(i, false);
            } else {
                this.f1330b++;
                this.f1329a.put(Integer.valueOf(i), new HashSet());
                a(i, true);
            }
            notifyDataSetChanged();
        }
    }

    public void a(int i, int i2) {
        if (this.f1329a.get(Integer.valueOf(i)) == null) {
            this.f1329a.put(Integer.valueOf(i), new HashSet());
        }
        Set set = this.f1329a.get(Integer.valueOf(i));
        if (set.contains(Integer.valueOf(i2))) {
            this.f1330b--;
            set.remove(Integer.valueOf(i2));
            if (set.size() == 0) {
                this.f1329a.remove(Integer.valueOf(i));
            }
            a(i, i2, false);
        } else {
            this.f1330b++;
            set.add(Integer.valueOf(i2));
            a(i, i2, true);
        }
        notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public abstract void a(int i, int i2, boolean z);

    /* access modifiers changed from: protected */
    public abstract void a(int i, boolean z);

    /* access modifiers changed from: protected */
    public abstract int b();

    /* access modifiers changed from: protected */
    public boolean b(int i, int i2) {
        return this.f1329a.containsKey(Integer.valueOf(i)) && this.f1329a.get(Integer.valueOf(i)).contains(Integer.valueOf(i2));
    }

    /* access modifiers changed from: protected */
    public abstract int c();

    /* access modifiers changed from: protected */
    public abstract int d();

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(this.f1331c).inflate(b(), (ViewGroup) null);
        ((CheckBox) inflate.findViewById(a())).setChecked(b(i, i2));
        a(i, i2, z, inflate, viewGroup);
        return inflate;
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(this.f1331c).inflate(d(), (ViewGroup) null);
        CheckBox checkBox = (CheckBox) inflate.findViewById(c());
        checkBox.setTag(Integer.valueOf(i));
        checkBox.setOnClickListener(this);
        getChildrenCount(i);
        boolean z2 = false;
        if (getChildrenCount(i) == 0) {
            checkBox.setClickable(false);
            checkBox.setChecked(this.f1329a.containsKey(Integer.valueOf(i)));
        } else {
            if (this.f1329a.containsKey(Integer.valueOf(i)) && this.f1329a.get(Integer.valueOf(i)).size() > 0) {
                z2 = true;
            }
            checkBox.setChecked(z2);
        }
        a(i, z, inflate, viewGroup);
        return inflate;
    }

    public void onClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        int intValue = ((Integer) checkBox.getTag()).intValue();
        a(intValue, checkBox.isChecked());
        b(intValue, checkBox.isChecked());
    }
}
