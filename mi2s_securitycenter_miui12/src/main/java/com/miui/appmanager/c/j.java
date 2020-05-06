package com.miui.appmanager.c;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.appmanager.E;
import com.miui.securitycenter.R;
import java.util.Locale;

public class j extends k {

    /* renamed from: c  reason: collision with root package name */
    private int f3644c;

    /* renamed from: d  reason: collision with root package name */
    private String f3645d;
    /* access modifiers changed from: private */
    public String e;
    /* access modifiers changed from: private */
    public String f;
    /* access modifiers changed from: private */
    public String g;
    /* access modifiers changed from: private */
    public String h;
    /* access modifiers changed from: private */
    public String i;
    private long j = -1;
    private long k = -1;
    private long l;
    /* access modifiers changed from: private */
    public boolean m;
    private boolean n;
    /* access modifiers changed from: private */
    public boolean o = true;
    private E p;
    private ApplicationInfo q;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private ImageView f3646a;

        /* renamed from: b  reason: collision with root package name */
        private ImageView f3647b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f3648c;

        /* renamed from: d  reason: collision with root package name */
        private TextView f3649d;
        private TextView e;
        private TextView f;
        private TextView g;
        private ImageView h;
        private ImageView i;
        private Context j;
        private boolean k = "zh_CN".equals(Locale.getDefault().toString());

        public a(View view) {
            super(view);
            this.j = view.getContext();
            this.f3646a = (ImageView) view.findViewById(R.id.am_icon);
            this.f3648c = (TextView) view.findViewById(R.id.am_label);
            this.f3649d = (TextView) view.findViewById(R.id.am_isRunning);
            this.e = (TextView) view.findViewById(R.id.am_isDisable);
            this.f = (TextView) view.findViewById(R.id.am_usage);
            this.g = (TextView) view.findViewById(R.id.am_storage);
            this.f3647b = (ImageView) view.findViewById(R.id.am_usage_img);
            this.h = (ImageView) view.findViewById(R.id.am_isRunning_icon);
            this.i = (ImageView) view.findViewById(R.id.am_isDisable_icon);
            this.f3646a.setColorFilter(this.j.getResources().getColor(R.color.app_manager_image_bg_color));
        }

        private void a(TextView textView, String str, String str2) {
            TextView textView2 = textView;
            String str3 = str;
            if (str2 != null && !TextUtils.isEmpty(str2) && str.toLowerCase().contains(str2.toLowerCase())) {
                int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
                String substring = str3.substring(indexOf, str2.length() + indexOf);
                boolean z = true;
                String format = String.format(this.j.getString(R.string.search_input_txt_na), new Object[]{substring});
                String[] strArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        z = false;
                        break;
                    } else if (format.contains(strArr[i2])) {
                        textView2.setText(Html.fromHtml(str3.replace(substring, format)));
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z) {
                    Spanned fromHtml = Html.fromHtml(str3.replaceFirst(substring, format));
                } else {
                    return;
                }
            }
            textView.setText(str);
        }

        public void a(View view, k kVar, int i2) {
            super.a(view, kVar, i2);
            j jVar = (j) kVar;
            if (this.f3646a != null) {
                r.a(jVar.e, this.f3646a, r.f, (int) R.drawable.card_icon_default);
            }
            TextView textView = this.f3648c;
            if (textView != null) {
                a(textView, jVar.f, jVar.i);
            }
            TextView textView2 = this.f3649d;
            int i3 = 8;
            int i4 = 0;
            if (textView2 != null) {
                textView2.setVisibility(jVar.m ? 0 : 8);
            }
            TextView textView3 = this.e;
            if (textView3 != null) {
                textView3.setVisibility(jVar.o ? 4 : 0);
            }
            TextView textView4 = this.f;
            if (textView4 != null) {
                textView4.setText(jVar.g);
            }
            if (this.g != null) {
                if (!this.k || jVar.h != null) {
                    this.g.setText(jVar.h);
                } else {
                    this.g.setText(R.string.app_manager_app_storage);
                }
            }
            if (this.f3647b != null) {
                this.f3647b.setVisibility(!this.k && jVar.m ? 0 : 8);
            }
            ImageView imageView = this.h;
            if (imageView != null) {
                if (jVar.m) {
                    i3 = 0;
                }
                imageView.setVisibility(i3);
            }
            ImageView imageView2 = this.i;
            if (imageView2 != null) {
                if (jVar.o) {
                    i4 = 4;
                }
                imageView2.setVisibility(i4);
            }
        }
    }

    public j() {
        super(R.layout.app_manager_list_item);
    }

    public void a(long j2) {
        this.k = j2;
    }

    public void a(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        this.q = applicationInfo;
        this.f3644c = applicationInfo.uid;
        this.f3645d = packageInfo.packageName;
        this.l = packageInfo.firstInstallTime;
    }

    public void a(E e2) {
        this.p = e2;
    }

    public void a(String str) {
        this.e = str;
    }

    public void a(boolean z) {
        this.o = z;
    }

    public ApplicationInfo b() {
        return this.q;
    }

    public void b(long j2) {
        this.j = j2;
    }

    public void b(String str) {
        this.f = str;
    }

    public void b(boolean z) {
        this.m = z;
    }

    public long c() {
        return this.l;
    }

    public void c(String str) {
        this.i = str;
    }

    public void c(boolean z) {
        this.n = z;
    }

    public String d() {
        String str = this.f;
        return str != null ? str : "";
    }

    public void d(String str) {
        this.h = str;
    }

    public String e() {
        return this.f3645d;
    }

    public void e(String str) {
        this.g = str;
    }

    public E f() {
        return this.p;
    }

    public String g() {
        return this.i;
    }

    public long h() {
        return this.k;
    }

    public int i() {
        return this.f3644c;
    }

    public long j() {
        return this.j;
    }

    public boolean k() {
        return this.o;
    }

    public boolean l() {
        return this.m;
    }
}
