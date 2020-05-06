package com.miui.gamebooster.viewPointwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.miui.gamebooster.m.C0377h;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.ca;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;
import com.xiaomi.stat.d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowTextCountTextView extends TextView {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5326a = Application.d().getResources().getString(R.string.ellipsis);

    /* renamed from: b  reason: collision with root package name */
    private String f5327b;

    /* renamed from: c  reason: collision with root package name */
    private String f5328c;

    /* renamed from: d  reason: collision with root package name */
    private int f5329d;
    private boolean e;
    private boolean f;
    private boolean g;
    private String h;
    private float i;
    private float j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private ArrayList<C0377h.a> r;
    private a s;
    private boolean t;

    public interface a {
        void a(boolean z);
    }

    public ShowTextCountTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ShowTextCountTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ShowTextCountTextView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5327b = f5326a;
        this.f5328c = "";
        this.e = false;
        this.f = false;
        this.g = false;
        this.i = 1.0f;
        this.j = 0.0f;
        this.k = 0;
        this.l = 0;
        this.m = 0;
        this.t = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.FolderTextView);
        this.f5328c = obtainStyledAttributes.getString(6);
        this.f5329d = getMaxLines();
        if (this.f5329d >= 1) {
            obtainStyledAttributes.recycle();
            this.o = getResources().getColor(R.color.color_black_trans_30);
            this.p = getResources().getDimensionPixelSize(R.dimen.text_font_size_36);
            return;
        }
        throw new RuntimeException("foldLine must not less than 1");
    }

    private int a(String str, int i2) {
        String str2 = str.substring(0, i2) + this.f5327b + this.f5328c;
        Layout b2 = b(str2);
        Layout b3 = b(str2 + "A");
        int lineCount = b2.getLineCount();
        int lineCount2 = b3.getLineCount();
        if (lineCount == getFoldLine() && lineCount2 == getFoldLine() + 1) {
            return 0;
        }
        return lineCount > getFoldLine() ? 1 : -1;
    }

    private SpannableString a(String str) {
        long currentTimeMillis = System.currentTimeMillis();
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String d2 = d(str);
        if (!TextUtils.isEmpty(this.f5328c)) {
            d2 = d2.substring(0, d2.length() - this.f5328c.length());
            int length = (this.n - d2.length()) - this.q;
            if (length > 0) {
                String quantityString = getResources().getQuantityString(R.plurals.hide_txt_hint_with_blank_2, length, new Object[]{Integer.valueOf(length)});
                if (quantityString.length() > this.f5328c.length()) {
                    String substring = d2.substring(0, (d2.length() - (quantityString.length() - this.f5328c.length())) - 1);
                    this.f5328c = quantityString;
                    d2 = substring + this.f5327b;
                } else {
                    this.f5328c = getResources().getQuantityString(R.plurals.hide_txt_hint_with_blank_2, length, new Object[]{Integer.valueOf(length)});
                }
                d2 = d2 + this.f5328c;
            }
        } else if (!this.t) {
            d2 = d2.substring(0, d2.length()) + this.f5327b;
            this.f5328c = "";
        }
        Log.d("FolderTextView", (System.currentTimeMillis() - currentTimeMillis) + d.H);
        SpannableString spannableString = new SpannableString(d2);
        if (!TextUtils.isEmpty(this.f5328c)) {
            int length2 = d2.length() - this.f5328c.length();
            int length3 = d2.length();
            spannableString.setSpan(new ForegroundColorSpan(this.o), length2, length3, 33);
            spannableString.setSpan(new AbsoluteSizeSpan(this.p), length2, length3, 33);
        }
        return spannableString;
    }

    private void a() {
        MovementMethod instance;
        boolean z = b(this.h).getLineCount() <= getFoldLine();
        a aVar = this.s;
        if (aVar != null) {
            aVar.a(!z);
        }
        if (z) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.h);
            if (!C0393y.a((List<?>) this.r)) {
                Iterator<C0377h.a> it = this.r.iterator();
                while (it.hasNext()) {
                    C0377h.a next = it.next();
                    if (next.a() > spannableStringBuilder.length()) {
                        break;
                    }
                    spannableStringBuilder.setSpan(new ca(getContext(), next.c()), next.b(), next.a(), 33);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#14B9C7")), next.b(), next.a(), 33);
                }
            }
            setText(spannableStringBuilder);
            instance = a.a();
        } else {
            SpannableString spannableString = new SpannableString(this.h);
            if (!this.e) {
                spannableString = a(this.h);
            }
            if (!C0393y.a((List<?>) this.r)) {
                Iterator<C0377h.a> it2 = this.r.iterator();
                while (it2.hasNext()) {
                    C0377h.a next2 = it2.next();
                    if (next2.a() > spannableString.length()) {
                        break;
                    }
                    spannableString.setSpan(new ca(getContext(), next2.c()), next2.b(), next2.a(), 33);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#14B9C7")), next2.b(), next2.a(), 33);
                }
            }
            a((CharSequence) spannableString);
            instance = LinkMovementMethod.getInstance();
        }
        setMovementMethod(instance);
    }

    private void a(CharSequence charSequence) {
        this.g = true;
        setText(charSequence);
    }

    private Layout b(String str) {
        return new StaticLayout(str, getPaint(), (getWidth() - getPaddingLeft()) - getPaddingRight(), Layout.Alignment.ALIGN_NORMAL, this.i, this.j, true);
    }

    private String c(String str) {
        int length = str.length() - 1;
        int i2 = (length + 0) / 2;
        int a2 = a(str, i2);
        int i3 = i2;
        int i4 = 0;
        while (a2 != 0 && length > i4) {
            StringBuilder sb = new StringBuilder();
            sb.append("使用二分法: tailorText() ");
            int i5 = this.k;
            this.k = i5 + 1;
            sb.append(i5);
            Log.d("FolderTextView", sb.toString());
            if (a2 > 0) {
                length = i3 - 1;
            } else if (a2 < 0) {
                i4 = i3 + 1;
            }
            i3 = (i4 + length) / 2;
            a2 = a(str, i3);
        }
        Log.d("FolderTextView", "mid is: " + i3);
        if (a2 != 0) {
            return d(str);
        }
        return str.substring(0, i3) + this.f5327b + this.f5328c;
    }

    private String d(String str) {
        String str2;
        StringBuilder sb = new StringBuilder();
        sb.append("使用备用方法: tailorTextBackUp() ");
        int i2 = this.l;
        this.l = i2 + 1;
        sb.append(i2);
        Log.d("FolderTextView", sb.toString());
        if (this.n - str.length() <= 0 || !this.t) {
            str2 = "";
        } else {
            str2 = getResources().getQuantityString(R.plurals.hide_txt_hint_with_blank_2, this.n - str.length(), new Object[]{Integer.valueOf(this.n - str.length())});
        }
        this.f5328c = str2;
        String str3 = str + this.f5327b + this.f5328c;
        Layout b2 = b(str3);
        int lineCount = b2.getLineCount();
        int i3 = this.f5329d;
        if (lineCount <= i3) {
            return str3;
        }
        int lineEnd = b2.getLineEnd(i3 - 1);
        if (str.length() < lineEnd) {
            lineEnd = str.length();
        }
        if (lineEnd > 1) {
            return c(str.substring(0, lineEnd - 1));
        }
        return this.f5327b + this.f5328c;
    }

    public int getFoldLine() {
        return this.f5329d;
    }

    public String getFullText() {
        return this.h;
    }

    public void invalidate() {
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        StringBuilder sb = new StringBuilder();
        sb.append("onDraw() ");
        int i2 = this.m;
        this.m = i2 + 1;
        sb.append(i2);
        sb.append(", getMeasuredHeight() ");
        sb.append(getMeasuredHeight());
        Log.d("FolderTextView", sb.toString());
        if (!this.f) {
            a();
        }
        super.onDraw(canvas);
        this.f = true;
        this.g = false;
    }

    public void setEllipsize(String str) {
        this.f5327b = str;
    }

    public void setIsShowCount(boolean z) {
        this.t = z;
    }

    public void setLineSpacing(float f2, float f3) {
        this.j = f2;
        this.i = f3;
        super.setLineSpacing(f2, f3);
    }

    public void setListener(a aVar) {
        this.s = aVar;
    }

    public void setTexColor(int i2) {
        this.o = i2;
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        int indexOf;
        this.f5328c = "";
        int i2 = 0;
        this.f = false;
        this.h = String.valueOf(charSequence);
        this.q = 0;
        if (!TextUtils.isEmpty(this.h)) {
            while (i2 < this.h.length() && (indexOf = this.h.indexOf("\n", i2)) != -1) {
                i2 = indexOf + 1;
                this.q++;
            }
            this.h = this.h.replaceAll("\n", "");
        }
        this.r = C0377h.a(this.h);
        this.h = C0377h.c(this.h);
        super.setText(charSequence, bufferType);
    }

    public void setTextSize(int i2) {
        this.p = i2;
    }

    public void setTotalCount(int i2) {
        this.n = i2;
    }

    public void setUnFoldText(String str) {
        this.f5328c = this.f5328c;
    }
}
