package com.miui.securityscan.f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import b.b.n.e;
import b.b.n.g;
import b.b.n.l;
import b.b.p.b;
import com.miui.securitycenter.R;
import com.miui.systemAdSolution.common.AdTrackType;
import org.json.JSONArray;
import org.json.JSONObject;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    public static final SparseIntArray f7695a = new SparseIntArray();

    /* renamed from: b  reason: collision with root package name */
    public static final SparseIntArray f7696b = new SparseIntArray();

    static {
        f7696b.put(0, 116);
        f7696b.put(1, 117);
        f7696b.put(2, 118);
        f7696b.put(3, 119);
        f7695a.put(0, 108);
        f7695a.put(1, 109);
        f7695a.put(2, 110);
        f7695a.put(3, 111);
        f7695a.put(4, 112);
        f7695a.put(5, 113);
    }

    private static Bitmap a(Context context, Bitmap bitmap) {
        float dimensionPixelSize = (((float) context.getResources().getDimensionPixelSize(R.dimen.skinpage_topbutton_height)) * 1.0f) / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(dimensionPixelSize, dimensionPixelSize);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void a(Context context) {
        if (context != null) {
            e.a(context.getApplicationContext()).a("securitycenterScan", "com.miui.securitycenter_skinindex", AdTrackType.Type.TRACK_VIEW, -1);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004f, code lost:
        if (b.b.n.l.a(r7, r2, r6, (int) com.miui.securitycenter.R.dimen.skinpage_topbutton_height, 1) != false) goto L_0x0051;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r6, com.miui.common.customview.gif.GifImageView r7, com.miui.securityscan.f.b r8) {
        /*
            if (r7 != 0) goto L_0x0003
            return
        L_0x0003:
            r0 = 100
            b.b.n.g$a r8 = r8.a((long) r0)
            r0 = 8
            if (r8 == 0) goto L_0x0058
            java.lang.String r1 = r8.c()
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0018
            goto L_0x0058
        L_0x0018:
            java.lang.String r1 = r8.c()
            java.io.File r2 = new java.io.File
            r2.<init>(r1)
            boolean r3 = r2.exists()
            r4 = 1
            r5 = 0
            if (r3 == 0) goto L_0x0052
            boolean r8 = r8.e()
            if (r8 != 0) goto L_0x0048
            b.c.a.b.d.d$a r8 = b.c.a.b.d.d.a.FILE
            java.lang.String r8 = r8.c(r1)
            b.c.a.b.d r1 = b.b.c.j.r.f1758b
            android.graphics.Bitmap r8 = b.b.c.j.r.a((java.lang.String) r8, (b.c.a.b.d) r1)
            if (r8 == 0) goto L_0x0052
            r7.setVisibility(r5)
            android.graphics.Bitmap r6 = a((android.content.Context) r6, (android.graphics.Bitmap) r8)
            r7.setImageBitmap(r6)
            goto L_0x0051
        L_0x0048:
            r8 = 2131166925(0x7f0706cd, float:1.794811E38)
            boolean r6 = b.b.n.l.a((com.miui.common.customview.gif.GifImageView) r7, (java.io.File) r2, (android.content.Context) r6, (int) r8, (int) r4)
            if (r6 == 0) goto L_0x0052
        L_0x0051:
            r4 = r5
        L_0x0052:
            if (r4 == 0) goto L_0x0057
            r7.setVisibility(r0)
        L_0x0057:
            return
        L_0x0058:
            r7.setVisibility(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.f.a.a(android.content.Context, com.miui.common.customview.gif.GifImageView, com.miui.securityscan.f.b):void");
    }

    public static void a(Context context, boolean z) {
        if (context != null) {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                int i = 9;
                jSONObject.put(l.f1872a, z ? 9 : 1);
                JSONObject jSONObject2 = new JSONObject();
                String str = l.f1873b;
                if (!z) {
                    i = 1;
                }
                jSONObject2.put(str, i);
                jSONArray.put(jSONObject);
                jSONArray.put(jSONObject2);
                b.a(context.getApplicationContext(), jSONArray.toString());
            } catch (Exception unused) {
            }
        }
    }

    public static void a(View view) {
        if (view != null) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, -10.0f, 0.0f, 0.0f);
            translateAnimation.setStartOffset(0);
            translateAnimation.setDuration(50);
            translateAnimation.setInterpolator(new DecelerateInterpolator());
            TranslateAnimation translateAnimation2 = new TranslateAnimation(-10.0f, 10.0f, 0.0f, 0.0f);
            translateAnimation2.setStartOffset(50);
            translateAnimation2.setDuration(80);
            translateAnimation2.setInterpolator(new AccelerateDecelerateInterpolator());
            TranslateAnimation translateAnimation3 = new TranslateAnimation(10.0f, 0.0f, 0.0f, 0.0f);
            translateAnimation3.setStartOffset(130);
            translateAnimation3.setDuration(50);
            translateAnimation3.setInterpolator(new AccelerateDecelerateInterpolator());
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setRepeatCount(2);
            animationSet.setRepeatMode(2);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(translateAnimation2);
            animationSet.addAnimation(translateAnimation3);
            view.startAnimation(animationSet);
        }
    }

    public static boolean a(b bVar) {
        boolean b2 = bVar.b();
        g.a a2 = bVar.a(100);
        return b2 && (a2 != null && !a2.e());
    }
}
