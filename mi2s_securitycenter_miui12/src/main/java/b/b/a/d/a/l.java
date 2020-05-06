package b.b.a.d.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.a.e.i;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public abstract class l extends RecyclerViewExt.c<a> {
    protected Context f;
    protected i g;
    private boolean h = true;
    protected boolean i = false;
    protected List<Object> j = new ArrayList();

    public static class a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        public final TextView f1362a;

        /* renamed from: b  reason: collision with root package name */
        public final TextView f1363b;

        /* renamed from: c  reason: collision with root package name */
        public final TextView f1364c;

        /* renamed from: d  reason: collision with root package name */
        public final TextView f1365d;
        public final TextView e;
        public final LinearLayout f;
        public final CheckBox g;

        public a(@NonNull View view) {
            super(view);
            this.f1362a = (TextView) view.findViewById(R.id.title);
            this.f1363b = (TextView) view.findViewById(R.id.count);
            this.f1364c = (TextView) view.findViewById(R.id.time);
            this.f1365d = (TextView) view.findViewById(R.id.data1);
            this.e = (TextView) view.findViewById(R.id.reason);
            this.f = (LinearLayout) view.findViewById(R.id.moreInfoLayout);
            this.g = (CheckBox) view.findViewById(16908289);
        }
    }

    public l(Context context) {
        this.g = i.a(context);
        this.f = context;
    }

    public Object a(int i2) {
        return this.j.get(i2);
    }

    public void a(@NonNull a aVar, int i2) {
        super.onBindViewHolder(aVar, i2);
        int i3 = 0;
        if (!this.e) {
            aVar.f.setVisibility(0);
            TextView textView = aVar.e;
            if (!this.h) {
                i3 = 8;
            }
            textView.setVisibility(i3);
            aVar.g.setVisibility(8);
            return;
        }
        aVar.f.setVisibility(8);
        aVar.g.setVisibility(0);
    }

    public void b(boolean z) {
        this.i = z;
        if (z) {
            notifyDataSetChanged();
        }
    }

    public void c(boolean z) {
        this.h = z;
        notifyDataSetChanged();
    }

    @NonNull
    public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i2) {
        return new a(LayoutInflater.from(this.f).inflate(R.layout.fw_log_group_listitem, viewGroup, false));
    }

    public void setData(List<Object> list) {
        this.j.clear();
        if (list != null) {
            this.j.addAll(list);
            notifyDataSetChanged();
        }
    }
}
