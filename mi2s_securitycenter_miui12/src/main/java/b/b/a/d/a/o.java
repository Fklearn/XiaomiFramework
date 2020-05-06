package b.b.a.d.a;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.widget.TextView;
import androidx.annotation.NonNull;
import b.b.a.d.a.l;
import b.b.a.e.n;
import com.google.android.mms.pdu.EncodedStringValue;
import com.miui.securitycenter.R;
import java.io.UnsupportedEncodingException;

public final class o extends l {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f1372a;

        /* renamed from: b  reason: collision with root package name */
        public String f1373b;

        /* renamed from: c  reason: collision with root package name */
        public int f1374c;

        /* renamed from: d  reason: collision with root package name */
        public int f1375d;
        public String e;
        public int f;
        public long g;
        public int h;
        public String i;

        public a(int i2, String str, int i3, int i4, String str2, int i5, long j, int i6, String str3) {
            this.f1372a = i2;
            this.f1373b = str;
            this.f1374c = i3;
            this.f1375d = i4;
            this.e = str2;
            this.f = i5;
            this.g = j;
            this.h = i6;
            this.i = str3;
        }
    }

    public o(Context context) {
        super(context);
    }

    /* access modifiers changed from: private */
    public void a(Context context, String str, String str2) {
        n.a(context, str, str2);
        b.b.a.a.a.a("check_sms");
    }

    public static byte[] b(String str) {
        try {
            return str.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            Log.e("SmsGroupLogAdapter", "ISO_8859_1 must be supported!", e);
            return new byte[0];
        }
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull l.a aVar, int i) {
        Object[] objArr;
        Context context;
        TextView textView;
        int i2;
        super.a(aVar, i);
        a aVar2 = (a) this.j.get(i);
        if (!TextUtils.isEmpty(aVar2.f1373b)) {
            aVar.f1362a.setText(n.b(aVar2.f1373b));
            aVar.f1362a.setTag(aVar2.f1373b);
            Pair<String, String> a2 = this.g.a(aVar2.f1373b, new m(this, aVar));
            if (a2 != null && !TextUtils.isEmpty((CharSequence) a2.first)) {
                aVar.f1362a.setText((CharSequence) a2.first);
            }
            if (this.i || aVar2.f1375d <= 0) {
                aVar.f1362a.setTextColor(this.f.getResources().getColor(R.color.tab_sort_item_text));
                aVar.f1363b.setTextColor(this.f.getResources().getColor(R.color.tab_sort_item_text));
                textView = aVar.f1363b;
                context = this.f;
                objArr = new Object[]{Integer.valueOf(aVar2.f1374c)};
            } else {
                aVar.f1362a.setTextColor(Color.parseColor("#F22424"));
                aVar.f1363b.setTextColor(Color.parseColor("#F22424"));
                textView = aVar.f1363b;
                context = this.f;
                objArr = new Object[]{Integer.valueOf(aVar2.f1375d)};
            }
            textView.setText(context.getString(R.string.log_count, objArr));
            aVar.f1364c.setText(n.a(this.f, aVar2.g, true));
            if (!TextUtils.isEmpty(aVar2.e) && (i2 = aVar2.f) != 0) {
                aVar2.e = new EncodedStringValue(i2, b(aVar2.e)).getString();
            }
            aVar.f1365d.setText(aVar2.e);
            aVar.itemView.setOnClickListener(new n(this, aVar, aVar2, i));
            aVar.g.setChecked(b(i));
            int i3 = aVar2.h;
            int i4 = R.string.sms_filter;
            switch (i3) {
                case 3:
                case 5:
                    i4 = R.string.sms_blacklist;
                    break;
                case 6:
                    i4 = R.string.sms_prefix;
                    break;
                case 7:
                    i4 = R.string.sms_stranger_block;
                    break;
                case 8:
                    i4 = R.string.sms_malicious_url;
                    break;
                case 9:
                    i4 = R.string.sms_contact_block;
                    break;
                case 10:
                    i4 = R.string.sms_service;
                    break;
                case 12:
                    i4 = R.string.sms_keywords;
                    break;
                case 13:
                    i4 = R.string.sms_address;
                    break;
                case 16:
                    i4 = R.string.sms_cloud_block;
                    break;
            }
            aVar.e.setText(i4);
        }
    }

    public int getItemCount() {
        return this.j.size();
    }

    public long[] i() {
        if (this.j.size() == 0) {
            return null;
        }
        long[] jArr = new long[this.j.size()];
        for (int i = 0; i < this.j.size(); i++) {
            jArr[i] = (long) ((a) this.j.get(i)).f1372a;
        }
        return jArr;
    }

    public long[] j() {
        SparseBooleanArray g = g();
        if (g.size() == 0) {
            return null;
        }
        long[] jArr = new long[g.size()];
        for (int i = 0; i < jArr.length; i++) {
            jArr[i] = (long) ((a) a(g.keyAt(i))).f1372a;
        }
        return jArr;
    }
}
