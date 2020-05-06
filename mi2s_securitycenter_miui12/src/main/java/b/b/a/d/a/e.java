package b.b.a.d.a;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.a.e.i;
import com.miui.antispam.ui.view.AntiSpamEditorTitleView;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public final class e extends RecyclerViewExt.c<b> {
    private i f;
    public a g;
    private boolean h;
    protected List<Object> i = new ArrayList();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public long f1337a;

        /* renamed from: b  reason: collision with root package name */
        public int f1338b;

        /* renamed from: c  reason: collision with root package name */
        public String f1339c;

        /* renamed from: d  reason: collision with root package name */
        public int f1340d;
        public String e;
        public int f;

        public a(long j, int i, String str, int i2, String str2, int i3) {
            this.f1337a = j;
            this.f1338b = i;
            this.f1339c = str;
            this.f1340d = i2;
            this.e = str2;
            this.f = i3;
        }
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        public final TextView f1341a;

        /* renamed from: b  reason: collision with root package name */
        public final TextView f1342b;

        /* renamed from: c  reason: collision with root package name */
        public final TextView f1343c;

        /* renamed from: d  reason: collision with root package name */
        public final TextView f1344d;
        public final CheckBox e;

        public b(@NonNull View view) {
            super(view);
            this.f1341a = (TextView) view.findViewById(R.id.name);
            this.f1342b = (TextView) view.findViewById(R.id.tag);
            this.f1343c = (TextView) view.findViewById(R.id.info);
            this.f1344d = (TextView) view.findViewById(R.id.number);
            this.e = (CheckBox) view.findViewById(16908289);
        }
    }

    public e(Context context, boolean z) {
        this.h = z;
        this.f = i.a(context);
        this.g = new a(context, (AntiSpamEditorTitleView) null);
    }

    public Object a(int i2) {
        return this.i.get(i2);
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull b bVar, int i2) {
        TextView textView;
        int i3;
        String str;
        Pair<String, String> a2;
        super.onBindViewHolder(bVar, i2);
        a aVar = (a) this.i.get(i2);
        bVar.f1343c.setVisibility(!this.e ? 0 : 8);
        bVar.e.setVisibility(this.e ? 0 : 8);
        bVar.f1341a.setText(aVar.f1339c);
        bVar.f1342b.setText("");
        bVar.f1344d.setVisibility(8);
        if (!aVar.f1339c.contains("*") && (a2 = this.f.a(aVar.f1339c, new c(this, bVar))) != null) {
            if (!TextUtils.isEmpty((CharSequence) a2.first)) {
                bVar.f1341a.setText((CharSequence) a2.first);
                bVar.f1344d.setVisibility(0);
                bVar.f1344d.setText(aVar.f1339c);
            }
            if (!TextUtils.isEmpty((CharSequence) a2.second)) {
                bVar.f1342b.setText((CharSequence) a2.second);
            }
        }
        if (aVar.f1339c.indexOf("***") == 0) {
            String a3 = this.g.a(aVar.e);
            TextView textView2 = bVar.f1341a;
            if (!aVar.e.equals(a3) || aVar.e.equals("吉林")) {
                str = a3 + " - " + aVar.e;
            } else {
                str = aVar.e;
            }
            textView2.setText(str);
        }
        int i4 = aVar.f1340d;
        if (i4 == 0) {
            textView = bVar.f1343c;
            i3 = this.h ? R.string.info_antispam_phone_sms : R.string.info_unantispam_phone_sms;
        } else if (i4 == 1) {
            textView = bVar.f1343c;
            i3 = this.h ? R.string.info_antispam_sms : R.string.info_unantispam_sms;
        } else {
            if (i4 == 2) {
                textView = bVar.f1343c;
                i3 = this.h ? R.string.info_antispam_phone : R.string.info_unantispam_phone;
            }
            bVar.itemView.setOnClickListener(new d(this, bVar, i2));
            bVar.e.setChecked(b(i2));
        }
        textView.setText(i3);
        bVar.itemView.setOnClickListener(new d(this, bVar, i2));
        bVar.e.setChecked(b(i2));
    }

    public int getItemCount() {
        return this.i.size();
    }

    @NonNull
    public b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i2) {
        return new b(LayoutInflater.from(d()).inflate(R.layout.fw_blacklist_listitem, viewGroup, false));
    }

    public void setData(List<Object> list) {
        this.i.clear();
        if (list != null) {
            this.i.addAll(list);
            notifyDataSetChanged();
        }
    }
}
