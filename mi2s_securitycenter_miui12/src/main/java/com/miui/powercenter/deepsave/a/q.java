package com.miui.powercenter.deepsave.a;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.j.C;
import com.miui.powercenter.utils.j;
import com.miui.securitycenter.R;
import java.util.List;
import java.util.Locale;

public class q extends C0185e {
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public long f7037d = 0;
    private View.OnClickListener e = new p(this);

    private class a extends AsyncTask<Void, Void, List<com.miui.powercenter.f.a>> {

        /* renamed from: a  reason: collision with root package name */
        Context f7038a;

        /* renamed from: b  reason: collision with root package name */
        b[] f7039b;

        /* renamed from: c  reason: collision with root package name */
        int f7040c;

        a(Context context, b[] bVarArr, int i) {
            this.f7038a = context.getApplicationContext();
            this.f7039b = bVarArr;
            this.f7040c = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.powercenter.f.a> doInBackground(Void... voidArr) {
            return com.miui.powercenter.f.b.a(this.f7038a);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.powercenter.f.a> list) {
            super.onPostExecute(list);
            if (this.f7040c == q.this.f1673a) {
                q.this.a(this.f7039b, this.f7038a, list);
            }
        }
    }

    private static class b {

        /* renamed from: a  reason: collision with root package name */
        ViewGroup f7042a;

        /* renamed from: b  reason: collision with root package name */
        ImageView f7043b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7044c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7045d;

        private b() {
        }

        /* synthetic */ b(p pVar) {
            this();
        }
    }

    private void a(b[] bVarArr, Context context) {
        new a(context, bVarArr, this.f1673a).execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void a(b[] bVarArr, Context context, List<com.miui.powercenter.f.a> list) {
        ImageView imageView;
        Drawable drawable;
        for (b bVar : bVarArr) {
            bVar.f7042a.setVisibility(8);
        }
        for (int i = 0; i < list.size(); i++) {
            bVarArr[i].f7042a.setVisibility(0);
            bVarArr[i].f7044c.setText(list.get(i).f7063b);
            bVarArr[i].f7045d.setText(String.format(Locale.getDefault(), "%.1f%%", new Object[]{Double.valueOf(list.get(i).f7064c)}));
            if (list.get(i).f7065d > 0) {
                com.miui.powercenter.utils.b.a(bVarArr[i].f7043b, list.get(i).f7065d);
            } else {
                if (TextUtils.isEmpty(list.get(i).f7062a)) {
                    PackageManager packageManager = context.getPackageManager();
                    imageView = bVarArr[i].f7043b;
                    drawable = packageManager.getDefaultActivityIcon();
                } else if (C.b(j.a(list.get(i).e))) {
                    drawable = C.a(context, new BitmapDrawable(context.getResources(), com.miui.powercenter.utils.b.a(list.get(i).f7062a)), list.get(i).e);
                    imageView = bVarArr[i].f7043b;
                } else {
                    com.miui.powercenter.utils.b.a(bVarArr[i].f7043b, list.get(i).f7062a);
                }
                imageView.setImageDrawable(drawable);
            }
        }
    }

    public int a() {
        return R.layout.pc_list_item_recent_consume;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: com.miui.powercenter.deepsave.a.q$b[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r9, android.view.View r10, android.content.Context r11, b.b.c.d.C0191k r12) {
        /*
            r8 = this;
            super.a(r9, r10, r11, r12)
            java.lang.Object r9 = r10.getTag()
            if (r9 != 0) goto L_0x00c4
            r9 = 2131297067(0x7f09032b, float:1.8212068E38)
            android.view.View r9 = r10.findViewById(r9)
            android.view.ViewGroup r9 = (android.view.ViewGroup) r9
            r12 = 2131297068(0x7f09032c, float:1.821207E38)
            android.view.View r12 = r10.findViewById(r12)
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            r0 = 2131297069(0x7f09032d, float:1.8212073E38)
            android.view.View r0 = r10.findViewById(r0)
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 3
            com.miui.powercenter.deepsave.a.q$b[] r1 = new com.miui.powercenter.deepsave.a.q.b[r1]
            com.miui.powercenter.deepsave.a.q$b r2 = new com.miui.powercenter.deepsave.a.q$b
            r3 = 0
            r2.<init>(r3)
            r4 = 0
            r1[r4] = r2
            r2 = r1[r4]
            r2.f7042a = r9
            r2 = r1[r4]
            r5 = 2131296975(0x7f0902cf, float:1.8211882E38)
            android.view.View r6 = r9.findViewById(r5)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r2.f7043b = r6
            r2 = r1[r4]
            r6 = 2131297844(0x7f090634, float:1.8213644E38)
            android.view.View r7 = r9.findViewById(r6)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r2.f7044c = r7
            r2 = r1[r4]
            r4 = 2131297446(0x7f0904a6, float:1.8212837E38)
            android.view.View r9 = r9.findViewById(r4)
            android.widget.TextView r9 = (android.widget.TextView) r9
            r2.f7045d = r9
            com.miui.powercenter.deepsave.a.q$b r9 = new com.miui.powercenter.deepsave.a.q$b
            r9.<init>(r3)
            r2 = 1
            r1[r2] = r9
            r9 = r1[r2]
            r9.f7042a = r12
            r9 = r1[r2]
            android.view.View r7 = r12.findViewById(r5)
            android.widget.ImageView r7 = (android.widget.ImageView) r7
            r9.f7043b = r7
            r9 = r1[r2]
            android.view.View r7 = r12.findViewById(r6)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r9.f7044c = r7
            r9 = r1[r2]
            android.view.View r12 = r12.findViewById(r4)
            android.widget.TextView r12 = (android.widget.TextView) r12
            r9.f7045d = r12
            com.miui.powercenter.deepsave.a.q$b r9 = new com.miui.powercenter.deepsave.a.q$b
            r9.<init>(r3)
            r12 = 2
            r1[r12] = r9
            r9 = r1[r12]
            r9.f7042a = r0
            r9 = r1[r12]
            android.view.View r2 = r0.findViewById(r5)
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            r9.f7043b = r2
            r9 = r1[r12]
            android.view.View r2 = r0.findViewById(r6)
            android.widget.TextView r2 = (android.widget.TextView) r2
            r9.f7044c = r2
            r9 = r1[r12]
            android.view.View r12 = r0.findViewById(r4)
            android.widget.TextView r12 = (android.widget.TextView) r12
            r9.f7045d = r12
            r10.setTag(r1)
            android.view.View$OnClickListener r9 = r8.e
            r10.setOnClickListener(r9)
            r9 = 16908313(0x1020019, float:2.38773E-38)
            android.view.View r9 = r10.findViewById(r9)
            android.view.View$OnClickListener r12 = r8.e
            r9.setOnClickListener(r12)
            goto L_0x00cb
        L_0x00c4:
            java.lang.Object r9 = r10.getTag()
            r1 = r9
            com.miui.powercenter.deepsave.a.q$b[] r1 = (com.miui.powercenter.deepsave.a.q.b[]) r1
        L_0x00cb:
            b.b.c.j.l.a(r10)
            r8.a((com.miui.powercenter.deepsave.a.q.b[]) r1, (android.content.Context) r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.deepsave.a.q.a(int, android.view.View, android.content.Context, b.b.c.d.k):void");
    }
}
