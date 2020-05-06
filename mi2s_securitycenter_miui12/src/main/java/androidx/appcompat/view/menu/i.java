package androidx.appcompat.view.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.t;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class i extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    j f384a;

    /* renamed from: b  reason: collision with root package name */
    private int f385b = -1;

    /* renamed from: c  reason: collision with root package name */
    private boolean f386c;

    /* renamed from: d  reason: collision with root package name */
    private final boolean f387d;
    private final LayoutInflater e;
    private final int f;

    public i(j jVar, LayoutInflater layoutInflater, boolean z, int i) {
        this.f387d = z;
        this.e = layoutInflater;
        this.f384a = jVar;
        this.f = i;
        a();
    }

    /* access modifiers changed from: package-private */
    public void a() {
        n f2 = this.f384a.f();
        if (f2 != null) {
            ArrayList<n> j = this.f384a.j();
            int size = j.size();
            for (int i = 0; i < size; i++) {
                if (j.get(i) == f2) {
                    this.f385b = i;
                    return;
                }
            }
        }
        this.f385b = -1;
    }

    public void a(boolean z) {
        this.f386c = z;
    }

    public j b() {
        return this.f384a;
    }

    public int getCount() {
        ArrayList<n> j = this.f387d ? this.f384a.j() : this.f384a.n();
        return this.f385b < 0 ? j.size() : j.size() - 1;
    }

    public n getItem(int i) {
        ArrayList<n> j = this.f387d ? this.f384a.j() : this.f384a.n();
        int i2 = this.f385b;
        if (i2 >= 0 && i >= i2) {
            i++;
        }
        return j.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.e.inflate(this.f, viewGroup, false);
        }
        int groupId = getItem(i).getGroupId();
        int i2 = i - 1;
        ListMenuItemView listMenuItemView = (ListMenuItemView) view;
        listMenuItemView.setGroupDividerEnabled(this.f384a.o() && groupId != (i2 >= 0 ? getItem(i2).getGroupId() : groupId));
        t.a aVar = (t.a) view;
        if (this.f386c) {
            listMenuItemView.setForceShowIcon(true);
        }
        aVar.a(getItem(i), 0);
        return view;
    }

    public void notifyDataSetChanged() {
        a();
        super.notifyDataSetChanged();
    }
}
