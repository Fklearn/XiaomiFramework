package b.b.a.d.a;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.a.e.n;
import com.miui.antispam.ui.view.AntiSpamEditorTitleView;
import com.miui.permcenter.permissions.C0466c;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class a extends b {

    /* renamed from: d  reason: collision with root package name */
    public List<String> f1324d = new ArrayList();
    public List<C0021a> e = new ArrayList();
    private HashMap<String, String> f = new HashMap<>();
    public List<Integer> g = new ArrayList();
    public List<Integer> h = new ArrayList();
    public SparseBooleanArray i = new SparseBooleanArray();
    private int j;
    private AntiSpamEditorTitleView k;

    /* renamed from: b.b.a.d.a.a$a  reason: collision with other inner class name */
    public class C0021a {

        /* renamed from: a  reason: collision with root package name */
        public String f1325a;

        /* renamed from: b  reason: collision with root package name */
        public int f1326b;

        /* renamed from: c  reason: collision with root package name */
        public String f1327c;

        public C0021a(String str, int i, String str2) {
            this.f1325a = str;
            this.f1326b = i;
            this.f1327c = str2;
        }
    }

    public a(Context context, AntiSpamEditorTitleView antiSpamEditorTitleView) {
        super(context);
        this.j = 0;
        if (antiSpamEditorTitleView != null) {
            this.k = antiSpamEditorTitleView;
            this.k.getOk().setEnabled(false);
        }
        JSONArray a2 = n.a(context);
        if (a2 != null) {
            for (int i2 = 0; i2 < a2.length(); i2++) {
                JSONObject optJSONObject = a2.optJSONObject(i2);
                this.g.add(Integer.valueOf(this.j));
                this.f1324d.add(optJSONObject.optString("n"));
                this.h.add(Integer.valueOf(a(optJSONObject.optJSONArray("l"), optJSONObject.optString("n"))));
            }
        }
    }

    /* access modifiers changed from: protected */
    public int a() {
        return 16908289;
    }

    public int a(JSONArray jSONArray, String str) {
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i2);
            String optString = optJSONObject.optString("n");
            this.e.add(new C0021a(optString, optJSONObject.optInt(C0466c.f6254a), str));
            this.f.put(optString, str);
            this.j++;
        }
        return jSONArray.length();
    }

    public View a(int i2, int i3, boolean z, View view, ViewGroup viewGroup) {
        ((TextView) view.findViewById(R.id.name)).setText(((C0021a) getChild(i2, i3)).f1325a);
        return view;
    }

    /* access modifiers changed from: protected */
    public View a(int i2, boolean z, View view, ViewGroup viewGroup) {
        View view2;
        String string;
        TextView textView = (TextView) view.findViewById(R.id.hint);
        ((TextView) view.findViewById(R.id.name)).setText((String) getGroup(i2));
        if (z) {
            view.findViewById(R.id.indicator_close).setVisibility(8);
            view2 = view.findViewById(R.id.indicator_open);
        } else {
            view.findViewById(R.id.indicator_open).setVisibility(8);
            view2 = view.findViewById(R.id.indicator_close);
        }
        view2.setVisibility(0);
        ArrayList arrayList = new ArrayList();
        int i3 = 0;
        for (int i4 = 0; i4 < this.h.get(i2).intValue(); i4++) {
            if (this.i.get(this.g.get(i2).intValue() + i4)) {
                i3++;
                arrayList.add(this.e.get(this.g.get(i2).intValue() + i4));
            }
        }
        if (i3 == 0) {
            textView.setVisibility(8);
        } else {
            if (i3 == this.h.get(i2).intValue()) {
                string = this.f1331c.getString(R.string.all_address_choose_hint);
            } else if (i3 == 1) {
                string = this.f1331c.getString(R.string.one_address_choose_hint, new Object[]{((C0021a) arrayList.get(0)).f1325a});
            } else if (i3 == 2) {
                string = this.f1331c.getString(R.string.two_address_choose_hint, new Object[]{((C0021a) arrayList.get(0)).f1325a, ((C0021a) arrayList.get(1)).f1325a});
            } else {
                string = this.f1331c.getString(R.string.address_choose_hint, new Object[]{((C0021a) arrayList.get(0)).f1325a, ((C0021a) arrayList.get(1)).f1325a, Integer.valueOf(i3)});
            }
            textView.setText(string);
        }
        return view;
    }

    public String a(String str) {
        return this.f.get(str);
    }

    /* access modifiers changed from: protected */
    public void a(int i2, int i3, boolean z) {
        int intValue = this.g.get(i2).intValue();
        this.i.put(i3 + intValue, z);
        boolean z2 = false;
        int i4 = 0;
        while (true) {
            if (i4 >= this.h.get(i2).intValue()) {
                break;
            } else if (this.i.get(intValue + i4)) {
                z2 = true;
                break;
            } else {
                i4++;
            }
        }
        this.i.put(i2 + this.e.size(), z2);
        e();
    }

    /* access modifiers changed from: protected */
    public void a(int i2, boolean z) {
        if (getChildrenCount(i2) == 0) {
            this.i.put(this.g.get(i2).intValue(), z);
            this.i.put(this.e.size() + i2, z);
        }
        e();
    }

    /* access modifiers changed from: protected */
    public int b() {
        return R.layout.antispam_adress_child;
    }

    /* access modifiers changed from: protected */
    public int c() {
        return 16908289;
    }

    /* access modifiers changed from: protected */
    public int d() {
        return R.layout.antispam_adress_group;
    }

    public void e() {
        this.k.getOk().setEnabled(this.f1330b > 0);
        if (this.f1330b > 0) {
            this.k.getTitle().setText(this.f1331c.getResources().getString(R.string.st_title_adress_num, new Object[]{Integer.valueOf(this.f1330b)}));
        } else {
            this.k.getTitle().setText(R.string.st_title_adress);
        }
        notifyDataSetChanged();
    }

    public Object getChild(int i2, int i3) {
        return this.e.get(this.g.get(i2).intValue() + i3);
    }

    public long getChildId(int i2, int i3) {
        return (long) (this.g.get(i2).intValue() + i3);
    }

    public int getChildrenCount(int i2) {
        if (this.h.get(i2).intValue() == 1) {
            return 0;
        }
        return this.h.get(i2).intValue();
    }

    public Object getGroup(int i2) {
        return this.f1324d.get(i2);
    }

    public int getGroupCount() {
        return this.f1324d.size();
    }

    public long getGroupId(int i2) {
        return (long) (this.e.size() + i2);
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int i2, int i3) {
        return true;
    }
}
