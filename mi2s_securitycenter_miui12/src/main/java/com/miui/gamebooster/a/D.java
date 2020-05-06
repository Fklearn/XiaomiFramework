package com.miui.gamebooster.a;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import com.miui.gamebooster.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class D extends RecyclerView.a<RecyclerView.u> {

    /* renamed from: a  reason: collision with root package name */
    private Context f4003a;

    /* renamed from: b  reason: collision with root package name */
    private List<e> f4004b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f4005c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f4006d = false;

    public interface a {
        void onItemClick(int i);
    }

    public D(Context context, ArrayList<e> arrayList) {
        this.f4003a = context;
        this.f4004b = arrayList;
    }

    public e a(int i) {
        return this.f4004b.get(i);
    }

    public void a(a aVar) {
        this.f4005c = aVar;
    }

    public void a(List<e> list) {
        this.f4004b = list;
    }

    public void a(boolean z) {
        this.f4006d = z;
    }

    public int getItemCount() {
        return this.f4004b.size();
    }

    public int getItemViewType(int i) {
        return this.f4004b.get(i).f();
    }

    public void onBindViewHolder(@NonNull RecyclerView.u uVar, int i) {
        View view;
        View.OnClickListener onClickListener;
        Resources resources;
        TextView textView;
        Resources resources2;
        TextView textView2;
        int itemViewType = getItemViewType(i);
        e a2 = a(i);
        int i2 = R.color.title_enable_color;
        if (itemViewType == 0) {
            z zVar = (z) uVar;
            boolean d2 = a2.d();
            zVar.f4084a.setChecked(d2);
            if (d2) {
                textView2 = zVar.f4085b;
                resources2 = this.f4003a.getResources();
                i2 = R.color.app_manager_category_title_color;
            } else {
                textView2 = zVar.f4085b;
                resources2 = this.f4003a.getResources();
            }
            textView2.setTextColor(resources2.getColor(i2));
            view = zVar.f4086c;
            onClickListener = new B(this, i);
        } else {
            A a3 = (A) uVar;
            r.a(a2.b(), a3.f3996a, r.f, (int) R.drawable.card_icon_default);
            a3.f3997b.setText(a2.c());
            a3.f3998c.setEnabled(this.f4006d);
            a3.f3998c.setChecked(a2.a());
            if (this.f4006d) {
                textView = a3.f3997b;
                resources = this.f4003a.getResources();
                i2 = R.color.qr_title_text_color;
            } else {
                textView = a3.f3997b;
                resources = this.f4003a.getResources();
            }
            textView.setTextColor(resources.getColor(i2));
            view = a3.itemView;
            onClickListener = new C(this, i);
        }
        view.setOnClickListener(onClickListener);
    }

    @NonNull
    public RecyclerView.u onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return i == 0 ? new z(LayoutInflater.from(this.f4003a).inflate(R.layout.quick_replay_list_header_layout, viewGroup, false)) : new A(LayoutInflater.from(this.f4003a).inflate(R.layout.quick_replay_setting_list_item_layout, viewGroup, false));
    }
}
