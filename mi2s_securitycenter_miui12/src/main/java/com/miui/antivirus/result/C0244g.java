package com.miui.antivirus.result;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.miui.antivirus.result.C0238a;
import com.miui.securityscan.i.i;
import java.io.Serializable;
import miui.os.Build;

/* renamed from: com.miui.antivirus.result.g  reason: case insensitive filesystem */
public abstract class C0244g extends C0238a implements View.OnClickListener, Serializable {

    /* renamed from: a  reason: collision with root package name */
    protected int f2833a = -1;

    /* renamed from: b  reason: collision with root package name */
    private boolean f2834b = false;

    /* renamed from: c  reason: collision with root package name */
    private String f2835c;

    /* renamed from: d  reason: collision with root package name */
    protected boolean f2836d = true;
    protected boolean e = true;

    public C0244g() {
        setBaseCardType(C0238a.C0040a.GUIDE);
    }

    public static boolean a(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.startsWith("#Intent") && str.endsWith(TtmlNode.END)) {
            try {
                Intent parseUri = Intent.parseUri(str, 0);
                if (i.a(context, parseUri)) {
                    context.startActivity(parseUri);
                    return true;
                }
            } catch (Exception e2) {
                Log.e("AntivirusBaseModel", "intent parseUri error : ", e2);
            }
            return false;
        } else if (str.startsWith("http")) {
            try {
                if (Build.IS_INTERNATIONAL_BUILD && i.a(context, str, "com.mi.globalbrowser")) {
                    i.b(context, str, "com.mi.globalbrowser");
                } else if (i.a(context, str, "com.android.browser")) {
                    i.b(context, str, "com.android.browser");
                } else {
                    i.c(context, str);
                }
                return true;
            } catch (Exception unused) {
            }
        } else {
            i.c(context, str);
            return true;
        }
    }

    public void a(int i, View view, Context context, t tVar) {
        this.f2833a = i;
    }

    public void a(int i, View view, Context context, t tVar, ViewGroup viewGroup) {
        a(i, view, context, tVar);
    }

    public void a(boolean z) {
        this.e = z;
    }

    public boolean a() {
        return this.e;
    }

    public void b(boolean z) {
        this.f2836d = z;
    }

    public boolean b() {
        return this.f2836d;
    }

    public abstract int getLayoutId();

    public void onClick(View view) {
    }

    public void setTemporary(boolean z) {
        this.f2834b = z;
    }

    public void setTestKey(String str) {
        this.f2835c = str;
    }
}
