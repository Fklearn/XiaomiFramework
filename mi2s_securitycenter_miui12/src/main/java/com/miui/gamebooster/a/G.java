package com.miui.gamebooster.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.r;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.widget.SwitchButton;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import miui.widget.SlidingButton;

public class G extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f4016a;

    /* renamed from: b  reason: collision with root package name */
    private LayoutInflater f4017b;

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<C0398d> f4018c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    private a f4019d;

    public interface a {
        void a(G g, CompoundButton compoundButton, boolean z);
    }

    static class b {

        /* renamed from: a  reason: collision with root package name */
        ImageView f4020a;

        /* renamed from: b  reason: collision with root package name */
        TextView f4021b;

        /* renamed from: c  reason: collision with root package name */
        CompoundButton f4022c;

        b() {
        }
    }

    public G(Context context) {
        this.f4016a = context;
        this.f4017b = LayoutInflater.from(context);
    }

    public void a(a aVar) {
        this.f4019d = aVar;
    }

    public void a(ArrayList<C0398d> arrayList) {
        this.f4018c.clear();
        this.f4018c.addAll(arrayList);
        notifyDataSetChanged();
    }

    public int getCount() {
        ArrayList<C0398d> arrayList = this.f4018c;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public Object getItem(int i) {
        ArrayList<C0398d> arrayList = this.f4018c;
        if (arrayList == null || arrayList.size() <= i) {
            return null;
        }
        return this.f4018c.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        b bVar;
        String str;
        String str2;
        if (view == null) {
            view = this.f4017b.inflate(R.layout.gb_game_select_list_item_view_land, (ViewGroup) null);
            bVar = new b();
            bVar.f4020a = (ImageView) view.findViewById(R.id.icon);
            bVar.f4021b = (TextView) view.findViewById(R.id.title);
            bVar.f4022c = (CompoundButton) view.findViewById(R.id.sliding_button);
            SlidingButton slidingButton = bVar.f4022c;
            if (slidingButton instanceof SlidingButton) {
                slidingButton.setOnPerformCheckedChangeListener(this);
            } else if (slidingButton instanceof SwitchButton) {
                slidingButton.setOnCheckedChangeListener(this);
            }
            view.setTag(bVar);
        } else {
            bVar = (b) view.getTag();
        }
        C0398d dVar = this.f4018c.get(i);
        if (B.c(dVar.b().uid) == 999) {
            str2 = dVar.b().packageName;
            str = "pkg_icon_xspace://";
        } else {
            str2 = dVar.b().packageName;
            str = "pkg_icon://";
        }
        r.a(str.concat(str2), bVar.f4020a, r.f, this.f4016a.getResources().getDrawable(R.drawable.gb_def_icon));
        bVar.f4021b.setText(dVar.d());
        bVar.f4022c.setTag(dVar);
        CompoundButton compoundButton = bVar.f4022c;
        if (compoundButton instanceof SwitchButton) {
            ((SwitchButton) compoundButton).setCheckedImmediatelyNoEvent(dVar.e());
        } else {
            compoundButton.setChecked(dVar.e());
        }
        return view;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        a aVar = this.f4019d;
        if (aVar != null) {
            aVar.a(this, compoundButton, z);
        }
    }
}
