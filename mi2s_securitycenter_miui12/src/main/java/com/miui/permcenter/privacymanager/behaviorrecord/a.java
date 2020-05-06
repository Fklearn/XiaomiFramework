package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class a extends RecyclerView.a<RecyclerView.u> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f6430a;

    /* renamed from: b  reason: collision with root package name */
    private Resources f6431b;

    /* renamed from: c  reason: collision with root package name */
    private List<com.miui.permcenter.privacymanager.a.a> f6432c;

    /* renamed from: d  reason: collision with root package name */
    private int f6433d;
    private com.miui.permcenter.b.c e;
    private com.miui.permcenter.b.b f;
    private int g = 0;
    private int h = 1;
    private boolean i = false;

    /* renamed from: com.miui.permcenter.privacymanager.behaviorrecord.a$a  reason: collision with other inner class name */
    public static class C0059a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        TextView f6434a;

        public C0059a(View view) {
            super(view);
            this.f6434a = (TextView) view.findViewById(R.id.app_behavior_item_foot);
        }
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6435a;

        /* renamed from: b  reason: collision with root package name */
        TimeLineView f6436b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6437c;

        /* renamed from: d  reason: collision with root package name */
        TextView f6438d;
        TextView e;
        ImageView f;

        public b(@NonNull View view) {
            super(view);
            this.f6435a = (ImageView) view.findViewById(R.id.app_behavior_icon);
            this.f6437c = (TextView) view.findViewById(R.id.app_behavior_perm_name);
            this.f6438d = (TextView) view.findViewById(R.id.app_behavior_event_name);
            this.e = (TextView) view.findViewById(R.id.app_behavior_time);
            this.f = (ImageView) view.findViewById(R.id.am_arrow_right);
            this.f6436b = (TimeLineView) view.findViewById(R.id.app_behavior_timeline);
        }

        public void a(Resources resources, boolean z, boolean z2) {
            TextView textView = this.e;
            int i = R.color.app_behavior_record_warn_color;
            textView.setTextColor(resources.getColor(z ? R.color.tx_runtime_behavior : z2 ? R.color.app_behavior_record_warn_color : R.color.app_behavior_record_normal_color));
            TextView textView2 = this.f6437c;
            if (!z2) {
                i = R.color.app_behavior_record_normal_color;
            }
            textView2.setTextColor(resources.getColor(i));
            this.f6436b.a(z, z2);
        }
    }

    public static class c extends RecyclerView.u {
        public c(View view) {
            super(view);
        }
    }

    public a(Context context, int i2) {
        this.f6430a = context;
        this.f6432c = new ArrayList();
        this.f6433d = i2;
        this.f6431b = context.getResources();
    }

    public void a(int i2) {
        int i3;
        if (i2 <= 0) {
            i3 = 0;
            this.i = false;
        } else {
            i3 = 1;
        }
        this.h = i3;
        notifyDataSetChanged();
    }

    public void a(com.miui.permcenter.b.b bVar) {
        this.f = bVar;
    }

    public void a(com.miui.permcenter.b.c cVar) {
        this.e = cVar;
    }

    public void a(List<com.miui.permcenter.privacymanager.a.a> list) {
        if (list != null) {
            this.f6432c = list;
            notifyDataSetChanged();
        }
    }

    public void a(boolean z) {
        this.i = z;
        notifyItemChanged(c());
    }

    public int b() {
        return this.f6432c.size();
    }

    public int c() {
        return getItemCount() - 1;
    }

    public int getItemCount() {
        return this.g + b() + this.h;
    }

    public int getItemViewType(int i2) {
        int i3 = this.g;
        if (i3 == 0 || i2 >= i3) {
            return (this.h == 0 || i2 < this.g + b()) ? 1 : 2;
        }
        return 0;
    }

    public void onBindViewHolder(@NonNull RecyclerView.u uVar, int i2) {
        int i3;
        Resources resources;
        TextView textView;
        ArrayList<Integer> a2;
        String str;
        String str2;
        if (!(uVar instanceof c)) {
            int i4 = 8;
            if (uVar instanceof C0059a) {
                C0059a aVar = (C0059a) uVar;
                aVar.f6434a.setText(R.string.app_behavior_list_load_more);
                TextView textView2 = aVar.f6434a;
                if (this.i) {
                    i4 = 0;
                }
                textView2.setVisibility(i4);
            } else if (uVar instanceof b) {
                b bVar = (b) uVar;
                com.miui.permcenter.privacymanager.a.a aVar2 = this.f6432c.get(i2);
                if (aVar2 != null) {
                    boolean z = aVar2.d() == 0;
                    View view = bVar.itemView;
                    if (!z) {
                        i4 = 0;
                    }
                    view.setVisibility(i4);
                    if (!z) {
                        bVar.itemView.setTag(Integer.valueOf(i2));
                        bVar.f6437c.setText(aVar2.g());
                        bVar.f6438d.setText(aVar2.a(this.f6431b));
                        bVar.e.setText(aVar2.c());
                        if (i2 <= 0 || !TextUtils.equals(this.f6432c.get(i2 - 1).c(), aVar2.c()) || aVar2.b(com.miui.permcenter.privacymanager.a.b.f)) {
                            bVar.e.setVisibility(0);
                        } else {
                            bVar.e.setVisibility(4);
                        }
                        bVar.a(this.f6431b, aVar2.b(com.miui.permcenter.privacymanager.a.b.e), aVar2.b(com.miui.permcenter.privacymanager.a.b.f6332d));
                        if (aVar2.n()) {
                            textView = bVar.f6438d;
                            resources = this.f6431b;
                            i3 = R.color.tx_perm_selected;
                        } else {
                            textView = bVar.f6438d;
                            resources = this.f6431b;
                            i3 = R.color.app_behavior_record_desc_color;
                        }
                        textView.setTextColor(resources.getColor(i3));
                        int i5 = this.f6433d;
                        if (i5 == 0) {
                            bVar.f6435a.setVisibility(0);
                            if (aVar2.l() == 999) {
                                str2 = aVar2.f();
                                str = "pkg_icon_xspace://";
                            } else {
                                str2 = aVar2.f();
                                str = "pkg_icon://";
                            }
                            r.a(str.concat(str2), bVar.f6435a, r.f);
                        } else if (i5 == 1) {
                            if ((!aVar2.b(com.miui.permcenter.privacymanager.a.b.f6330b) || aVar2.k() != null) && (!aVar2.m() || aVar2.b(com.miui.permcenter.privacymanager.a.b.f6329a))) {
                                bVar.f.setVisibility(0);
                            } else {
                                bVar.f.setVisibility(4);
                            }
                        }
                        com.miui.permcenter.b.b bVar2 = this.f;
                        if (!(bVar2 == null || (a2 = bVar2.a(i2 - this.g)) == null)) {
                            int indexOf = a2.indexOf(Integer.valueOf(i2 - this.g));
                            if (a2.size() == 1) {
                                bVar.f6436b.setSpecialLine(0);
                                return;
                            } else if (indexOf == 0) {
                                bVar.f6436b.setSpecialLine(1);
                                return;
                            } else if (indexOf == a2.size() - 1) {
                                bVar.f6436b.setSpecialLine(16);
                                return;
                            }
                        }
                        bVar.f6436b.setSpecialLine(17);
                    }
                }
            }
        }
    }

    public void onClick(View view) {
        com.miui.permcenter.b.c cVar = this.e;
        if (cVar != null) {
            cVar.a(view);
        }
    }

    @NonNull
    public RecyclerView.u onCreateViewHolder(@NonNull ViewGroup viewGroup, int i2) {
        if (i2 == 0) {
            return new c(LayoutInflater.from(this.f6430a).inflate(R.layout.pm_app_behavior_loading, viewGroup, false));
        }
        if (i2 == 2) {
            return new C0059a(LayoutInflater.from(this.f6430a).inflate(R.layout.pm_app_behavior_loading, viewGroup, false));
        }
        View inflate = LayoutInflater.from(this.f6430a).inflate(R.layout.listitem_app_behavior, viewGroup, false);
        inflate.setOnClickListener(this);
        return new b(inflate);
    }
}
