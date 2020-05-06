package b.b.a.d.a;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.TextView;
import b.b.a.d.a.l;
import b.b.a.e.n;
import com.miui.securitycenter.R;
import miui.telephony.PhoneNumberUtils;

public final class h extends l {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public String f1351a;

        /* renamed from: b  reason: collision with root package name */
        public String f1352b;

        /* renamed from: c  reason: collision with root package name */
        public int f1353c;

        /* renamed from: d  reason: collision with root package name */
        public int f1354d;
        public int e;
        public long f;
        public int g;

        public a(String str, String str2, int i, int i2, int i3, long j, int i4) {
            this.f1351a = str;
            this.f1352b = str2;
            this.f1353c = i;
            this.f1354d = i2;
            this.e = i3;
            this.f = j;
            this.g = i4;
        }
    }

    public h(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    public void a(Context context, String str, int i) {
        n.b(context, str, i);
        b.b.a.a.a.a("check_call");
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull l.a aVar, int i) {
        Object[] objArr;
        Context context;
        TextView textView;
        int i2;
        super.a(aVar, i);
        a aVar2 = (a) this.j.get(i);
        if (aVar2.f1353c != 1) {
            aVar.f1362a.setText(R.string.tab_block_log_unKnowNumber);
        } else {
            aVar.f1362a.setText(aVar2.f1351a);
            aVar.f1362a.setTag(aVar2.f1351a);
            Pair<String, String> a2 = this.g.a(aVar2.f1351a, new f(this, aVar));
            if (a2 != null && !TextUtils.isEmpty((CharSequence) a2.first)) {
                aVar.f1362a.setText((CharSequence) a2.first);
            }
        }
        if (aVar2.f1354d > 0) {
            aVar.f1362a.setTextColor(Color.parseColor("#F22424"));
            aVar.f1363b.setTextColor(Color.parseColor("#F22424"));
            textView = aVar.f1363b;
            context = this.f;
            objArr = new Object[]{Integer.valueOf(aVar2.f1354d)};
        } else {
            aVar.f1362a.setTextColor(this.f.getResources().getColor(R.color.tab_sort_item_text));
            aVar.f1363b.setTextColor(this.f.getResources().getColor(R.color.tab_sort_item_text));
            textView = aVar.f1363b;
            context = this.f;
            objArr = new Object[]{Integer.valueOf(aVar2.e)};
        }
        textView.setText(context.getString(R.string.log_count, objArr));
        String parseTelocationString = PhoneNumberUtils.parseTelocationString(this.f, n.c(aVar2.f1351a));
        if (!TextUtils.isEmpty(parseTelocationString)) {
            aVar.f1365d.setText(parseTelocationString);
        }
        aVar.f1364c.setText(n.a(this.f, aVar2.f, true));
        aVar.itemView.setOnClickListener(new g(this, aVar, aVar2, i));
        aVar.g.setChecked(b(i));
        int i3 = aVar2.g;
        int i4 = R.string.call_blacklist;
        switch (i3) {
            case 4:
                i4 = R.string.call_private;
                break;
            case 6:
                i4 = R.string.call_prefix;
                break;
            case 7:
                i4 = R.string.call_stranger_block;
                break;
            case 8:
                if (!n.c()) {
                    i2 = R.string.mark_fraud_block;
                    break;
                } else {
                    i2 = R.string.mark_fraud_block_large;
                    break;
                }
            case 9:
                i4 = R.string.call_contact_block;
                break;
            case 10:
                if (!n.c()) {
                    i2 = R.string.mark_agent_block;
                    break;
                } else {
                    i2 = R.string.mark_agent_block_large;
                    break;
                }
            case 12:
                if (!n.c()) {
                    i2 = R.string.mark_sell_block;
                    break;
                } else {
                    i2 = R.string.mark_sell_block_large;
                    break;
                }
            case 13:
                i4 = R.string.call_address;
                break;
            case 14:
                if (!n.c()) {
                    i2 = R.string.mark_harass_block;
                    break;
                } else {
                    i2 = R.string.mark_harass_block_large;
                    break;
                }
            case 15:
                i4 = R.string.call_transfer_block;
                break;
            case 16:
                i4 = R.string.call_cloud_block;
                break;
            case 17:
                i4 = R.string.call_oversea_block;
                break;
        }
        i4 = i2;
        aVar.e.setText(i4);
    }

    public int getItemCount() {
        return this.j.size();
    }
}
