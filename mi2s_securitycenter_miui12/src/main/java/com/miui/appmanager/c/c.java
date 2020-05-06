package com.miui.appmanager.c;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import b.b.c.j.r;
import b.b.c.j.x;
import b.b.g.a;
import b.c.a.b.d;
import com.miui.activityutil.o;
import com.miui.applicationlock.c.y;
import com.miui.appmanager.AppManagerMainActivity;
import com.miui.appmanager.C;
import com.miui.common.customview.AdImageView;
import com.miui.securitycenter.R;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class c extends k implements a.C0028a {
    private boolean A;
    private boolean B;
    private boolean C;
    private String D;
    private int E;
    private String F;
    /* access modifiers changed from: private */
    public boolean G = false;
    private boolean H = true;
    /* access modifiers changed from: private */
    public boolean I;
    private String J;
    private String K;
    private transient Object L;
    private transient View M;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f3621c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f3622d;
    private String e;
    private String f;
    private String g;
    /* access modifiers changed from: private */
    public String h;
    private String i;
    private int j;
    private long k;
    private String l;
    private String m;
    private String n;
    private String o;
    private long p;
    private String q;
    private String r;
    private String s;
    private String t;
    private String u;
    private String v;
    private String w;
    private int x;
    private String[] y;
    private String[] z;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        d f3623a = r.g;

        /* renamed from: b  reason: collision with root package name */
        private View f3624b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f3625c;

        /* renamed from: d  reason: collision with root package name */
        private TextView f3626d;
        protected ImageView e;
        protected Button f;
        protected View g;
        private View h;
        k i;
        g j;

        public a(View view) {
            super(view);
            this.f3624b = view.findViewById(R.id.close);
            this.f3625c = (TextView) view.findViewById(R.id.tv_title);
            this.f3626d = (TextView) view.findViewById(R.id.tv_summary);
            this.e = (ImageView) view.findViewById(R.id.iv_banner);
            this.f = (Button) view.findViewById(R.id.btn_action);
            this.g = view.findViewById(R.id.am_ad_divider);
            this.h = view.findViewById(R.id.button_layout);
            this.i = k.a(view.getContext());
            this.j = g.a(view.getContext());
        }

        private void a(View view, int i2, c cVar, View.OnClickListener onClickListener) {
            if (this.f != null) {
                if (cVar instanceof c) {
                    cVar.a(view.getContext(), this.f, this.h, cVar, this.i, this.j);
                }
                this.f.setOnClickListener(onClickListener);
            }
            View view2 = this.h;
            if (view2 != null) {
                view2.setOnClickListener(onClickListener);
            }
            View view3 = this.f3624b;
            if (view3 != null) {
                view3.setVisibility((!cVar.k() || (!cVar.I && cVar.f3621c <= 0)) ? 4 : 0);
                this.f3624b.setOnClickListener(onClickListener);
            }
            String h2 = cVar.h();
            if (this.f3625c != null && !TextUtils.isEmpty(h2)) {
                this.f3625c.setText(h2);
            }
            String g2 = cVar.g();
            if (this.f3626d != null && !TextUtils.isEmpty(g2)) {
                this.f3626d.setText(g2);
            }
            if (this.e != null && !TextUtils.isEmpty(cVar.h)) {
                r.a(cVar.h, this.e, this.f3623a, (int) R.drawable.card_icon_default);
                if ((this.e instanceof AdImageView) && !cVar.G) {
                    ((AppManagerMainActivity) view.getContext()).a("VIEW", cVar);
                    com.miui.appmanager.a.a.a("ad_show", cVar.I ? cVar.f3622d : String.valueOf(cVar.f3621c));
                    boolean unused = cVar.G = true;
                }
            }
        }

        public void a(View view, k kVar, int i2) {
            super.a(view, kVar, i2);
            c cVar = (c) kVar;
            b bVar = new b(this, cVar);
            view.setOnClickListener(bVar);
            a(view, i2, cVar, bVar);
        }
    }

    private static class b extends IAdFeedbackListener.Stub {

        /* renamed from: a  reason: collision with root package name */
        private Context f3627a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3628b;

        public b(AppManagerMainActivity appManagerMainActivity) {
            this.f3627a = appManagerMainActivity.getApplicationContext();
            this.f3628b = new WeakReference<>(appManagerMainActivity);
        }

        public void onFinished(int i) {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3628b.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing() && !appManagerMainActivity.isDestroyed()) {
                if (i > 0) {
                    c.d(appManagerMainActivity);
                }
                y.b().b(this.f3627a);
            }
        }
    }

    /* renamed from: com.miui.appmanager.c.c$c  reason: collision with other inner class name */
    private static class C0043c implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3629a;

        /* renamed from: b  reason: collision with root package name */
        private PopupWindow f3630b;

        public C0043c(AppManagerMainActivity appManagerMainActivity, PopupWindow popupWindow) {
            this.f3629a = new WeakReference<>(appManagerMainActivity);
            this.f3630b = popupWindow;
        }

        public void onClick(View view) {
            this.f3630b.dismiss();
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3629a.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing() && !appManagerMainActivity.isDestroyed()) {
                c.d(appManagerMainActivity);
            }
        }
    }

    public c(int i2, JSONObject jSONObject, String str) {
        super(i2);
        if (!TextUtils.isEmpty(str)) {
            this.w = str.charAt(2) + "";
        }
        a(jSONObject);
    }

    private void a(Context context, Button button, int i2) {
        if (i2 != -1) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(context.getResources().getDimension(R.dimen.am_action_btn_corner_radius));
            if (TextUtils.equals(o.f2309a, this.w)) {
                gradientDrawable.setStroke(1, i2);
                button.setTextColor(i2);
            } else {
                gradientDrawable.setColor(i2);
                button.setTextColor(-1);
            }
            button.setBackground(gradientDrawable);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.content.Context r8, android.widget.Button r9, android.view.View r10, com.miui.appmanager.c.c r11, com.miui.securityscan.cards.k r12, com.miui.securityscan.cards.g r13) {
        /*
            r7 = this;
            java.lang.String r0 = r11.m
            boolean r12 = r12.a((java.lang.String) r0)
            android.content.res.Resources r0 = r8.getResources()
            r1 = 2131099719(0x7f060047, float:1.78118E38)
            int r0 = r0.getColor(r1)
            r1 = 1
            r2 = 0
            r3 = 2131230953(0x7f0800e9, float:1.8077973E38)
            if (r12 == 0) goto L_0x0033
            java.lang.String r8 = r11.D
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0027
            r8 = 2131757046(0x7f1007f6, float:1.9145017E38)
            r9.setText(r8)
            goto L_0x002c
        L_0x0027:
            java.lang.String r8 = r11.D
            r9.setText(r8)
        L_0x002c:
            r9.setTextColor(r0)
            r9.setBackgroundResource(r3)
            goto L_0x0067
        L_0x0033:
            java.lang.String r12 = r11.m
            int r12 = r13.b((java.lang.String) r12)
            r4 = 2131755998(0x7f1003de, float:1.9142891E38)
            r5 = -1
            if (r12 == r5) goto L_0x0091
            r6 = 5
            if (r12 == r6) goto L_0x0074
            r6 = 10
            if (r12 == r6) goto L_0x0070
            if (r12 == r1) goto L_0x0091
            r6 = 2
            if (r12 == r6) goto L_0x0074
            r13 = 3
            if (r12 == r13) goto L_0x0069
            java.lang.String r12 = r11.g
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 != 0) goto L_0x005c
            java.lang.String r12 = r11.g
            r9.setText(r12)
            goto L_0x0062
        L_0x005c:
            r12 = 2131756615(0x7f100647, float:1.9144143E38)
            r9.setText(r12)
        L_0x0062:
            int r11 = r11.E
            r7.a(r8, r9, r11)
        L_0x0067:
            r2 = r1
            goto L_0x009a
        L_0x0069:
            r8 = 2131756618(0x7f10064a, float:1.9144149E38)
        L_0x006c:
            r9.setText(r8)
            goto L_0x0094
        L_0x0070:
            r8 = 2131755815(0x7f100327, float:1.914252E38)
            goto L_0x006c
        L_0x0074:
            java.lang.String r8 = r11.m
            int r8 = r13.a((java.lang.String) r8)
            if (r8 == r5) goto L_0x0091
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            java.lang.String r8 = "%"
            r11.append(r8)
            java.lang.String r8 = r11.toString()
            r9.setText(r8)
            goto L_0x0094
        L_0x0091:
            r9.setText(r4)
        L_0x0094:
            r9.setBackgroundResource(r3)
            r9.setTextColor(r0)
        L_0x009a:
            r9.setEnabled(r2)
            if (r10 == 0) goto L_0x00a2
            r10.setEnabled(r2)
        L_0x00a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.c.c.a(android.content.Context, android.widget.Button, android.view.View, com.miui.appmanager.c.c, com.miui.securityscan.cards.k, com.miui.securityscan.cards.g):void");
    }

    private void a(View view) {
        this.M = view;
        C.a(view.getContext(), this.L);
    }

    private void a(AppManagerMainActivity appManagerMainActivity, View view) {
        View inflate = appManagerMainActivity.getLayoutInflater().inflate(R.layout.result_unlike_pop_window_right, (ViewGroup) null);
        Resources resources = appManagerMainActivity.getResources();
        int i2 = resources.getDisplayMetrics().widthPixels;
        PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
        inflate.measure(View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(10, Integer.MIN_VALUE));
        inflate.setOnClickListener(new C0043c(appManagerMainActivity, popupWindow));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        popupWindow.showAtLocation(view, 0, iArr[0] + view.getWidth(), iArr[1] - resources.getDimensionPixelOffset(R.dimen.result_popwindow_offset));
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(com.miui.appmanager.AppManagerMainActivity r6) {
        /*
            r5 = this;
            boolean r0 = r5.I
            if (r0 == 0) goto L_0x0007
            java.lang.String r0 = r5.f3622d
            goto L_0x000d
        L_0x0007:
            int r0 = r5.f3621c
            java.lang.String r0 = java.lang.String.valueOf(r0)
        L_0x000d:
            java.lang.String r1 = "ad_click"
            com.miui.appmanager.a.a.a((java.lang.String) r1, (java.lang.String) r0)
            java.lang.String r0 = r5.n
            boolean r0 = com.miui.securityscan.i.i.b(r6, r0)
            if (r0 == 0) goto L_0x001b
            return
        L_0x001b:
            java.lang.String r0 = r5.m
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0029
            java.lang.String r0 = r5.i
            com.miui.securityscan.i.i.b(r6, r0)
            return
        L_0x0029:
            com.miui.securityscan.cards.k r0 = com.miui.securityscan.cards.k.a((android.content.Context) r6)
            java.lang.String r1 = r5.m
            boolean r0 = r0.a((java.lang.String) r1)
            if (r0 == 0) goto L_0x0043
            android.content.pm.PackageManager r0 = r6.getPackageManager()
            java.lang.String r1 = r5.m
            android.content.Intent r0 = r0.getLaunchIntentForPackage(r1)
            r6.startActivity(r0)
            goto L_0x0085
        L_0x0043:
            boolean r0 = com.miui.securityscan.i.c.f(r6)
            if (r0 != 0) goto L_0x0050
            r0 = 2131758379(0x7f100d2b, float:1.914772E38)
            com.miui.securityscan.i.c.a((android.content.Context) r6, (int) r0)
            return
        L_0x0050:
            java.lang.String r0 = r5.l
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0068
            java.lang.String r0 = r5.l
            java.lang.String r1 = "migamecenter:"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0068
            java.lang.String r0 = r5.l     // Catch:{ Exception -> 0x0085 }
            com.miui.securityscan.i.i.c(r6, r0)     // Catch:{ Exception -> 0x0085 }
            goto L_0x0085
        L_0x0068:
            r5.a((android.content.Context) r6)
            android.content.res.Resources r0 = r6.getResources()
            r1 = 2131758030(0x7f100bce, float:1.9147012E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.e
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.getString(r1, r2)
            android.widget.Toast r6 = android.widget.Toast.makeText(r6, r0, r4)
            r6.show()
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.c.c.b(com.miui.appmanager.AppManagerMainActivity):void");
    }

    private void b(AppManagerMainActivity appManagerMainActivity, View view) {
        y b2 = y.b();
        b bVar = new b(appManagerMainActivity);
        if (b2.a(appManagerMainActivity.getApplicationContext())) {
            b2.a(appManagerMainActivity.getApplicationContext(), (IAdFeedbackListener) bVar, "com.miui.securitycenter", "com.miui.securitycenter_appmanager", c());
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(com.miui.appmanager.AppManagerMainActivity r4) {
        /*
            r3 = this;
            java.lang.String r0 = "AMAdvCardModel"
            boolean r1 = r3.I
            if (r1 == 0) goto L_0x0009
            java.lang.String r1 = r3.f3622d
            goto L_0x000f
        L_0x0009:
            int r1 = r3.f3621c
            java.lang.String r1 = java.lang.String.valueOf(r1)
        L_0x000f:
            java.lang.String r2 = "ad_click"
            com.miui.appmanager.a.a.a((java.lang.String) r2, (java.lang.String) r1)
            java.lang.String r1 = r3.n
            boolean r1 = com.miui.securityscan.i.i.b(r4, r1)
            if (r1 == 0) goto L_0x001d
            return
        L_0x001d:
            java.lang.String r1 = r3.m
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x002b
            java.lang.String r0 = r3.i
            com.miui.securityscan.i.i.b(r4, r0)
            return
        L_0x002b:
            java.lang.String r1 = r3.i     // Catch:{ Exception -> 0x003f }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x003f }
            if (r1 == 0) goto L_0x0039
            java.lang.String r4 = "landingPageUrl is empty"
            android.util.Log.d(r0, r4)     // Catch:{ Exception -> 0x003f }
            return
        L_0x0039:
            java.lang.String r1 = r3.i     // Catch:{ Exception -> 0x003f }
            com.miui.securityscan.i.i.c(r4, r1)     // Catch:{ Exception -> 0x003f }
            goto L_0x0045
        L_0x003f:
            r4 = move-exception
            java.lang.String r1 = "onAdvContentClick"
            android.util.Log.e(r0, r1, r4)
        L_0x0045:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.c.c.c(com.miui.appmanager.AppManagerMainActivity):void");
    }

    /* access modifiers changed from: private */
    public static void d(AppManagerMainActivity appManagerMainActivity) {
        new Handler(Looper.getMainLooper()).post(new a(appManagerMainActivity));
    }

    public void a(Context context) {
        x.a(context, this.m, this.r, this.q, this.s, this.t, this.u, this.v, this.K);
        g a2 = g.a(context);
        a2.a(this.m, j());
        a2.a(this.m, 10);
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0140 A[LOOP:0: B:22:0x013a->B:24:0x0140, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(org.json.JSONObject r6) {
        /*
            r5 = this;
            if (r6 != 0) goto L_0x0003
            return
        L_0x0003:
            java.lang.String r0 = "id"
            int r0 = r6.optInt(r0)
            r5.f3621c = r0
            java.lang.String r0 = "dataId"
            java.lang.String r0 = r6.optString(r0)
            r5.f3622d = r0
            java.lang.String r0 = "appName"
            java.lang.String r0 = r6.optString(r0)
            r5.e = r0
            java.lang.String r0 = r5.e
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x002b
            java.lang.String r0 = "title"
            java.lang.String r0 = r6.optString(r0)
            r5.e = r0
        L_0x002b:
            java.lang.String r0 = "summary"
            java.lang.String r0 = r6.optString(r0)
            r5.f = r0
            java.lang.String r0 = "landingPageUrl"
            java.lang.String r0 = r6.optString(r0)
            r5.i = r0
            java.lang.String r0 = "template"
            int r0 = r6.optInt(r0)
            r5.j = r0
            java.lang.String r0 = "apkSize"
            long r0 = r6.optLong(r0)
            r5.p = r0
            java.lang.String r0 = "categoryName"
            java.lang.String r0 = r6.optString(r0)
            r5.o = r0
            java.lang.String r0 = "allDownloadNum"
            int r0 = r6.optInt(r0)
            long r0 = (long) r0
            r5.k = r0
            java.lang.String r0 = "iconUrl"
            java.lang.String r0 = r6.optString(r0)
            r5.h = r0
            java.lang.String r0 = "actionUrl"
            java.lang.String r0 = r6.optString(r0)
            r5.l = r0
            java.lang.String r0 = "deeplink"
            java.lang.String r0 = r6.optString(r0)
            r5.n = r0
            java.lang.String r0 = "packageName"
            java.lang.String r0 = r6.optString(r0)
            r5.m = r0
            java.lang.String r0 = "ex"
            java.lang.String r0 = r6.optString(r0)
            r5.q = r0
            java.lang.String r0 = "appRef"
            java.lang.String r0 = r6.optString(r0)
            r5.r = r0
            java.lang.String r0 = "appClientId"
            java.lang.String r0 = r6.optString(r0)
            r5.s = r0
            java.lang.String r0 = "appSignature"
            java.lang.String r0 = r6.optString(r0)
            r5.t = r0
            java.lang.String r0 = "nonce"
            java.lang.String r0 = r6.optString(r0)
            r5.u = r0
            java.lang.String r0 = "appChannel"
            java.lang.String r0 = r6.optString(r0)
            r5.v = r0
            java.lang.String r0 = "floatCardData"
            java.lang.String r0 = r6.optString(r0)
            r5.K = r0
            java.lang.String r0 = "local"
            boolean r0 = r6.optBoolean(r0)
            r5.I = r0
            java.lang.String r0 = "cta"
            java.lang.String r0 = r6.optString(r0)
            r5.J = r0
            java.lang.String r0 = "parameters"
            org.json.JSONObject r0 = r6.optJSONObject(r0)
            if (r0 == 0) goto L_0x00e4
            java.lang.String r1 = "autoDownload"
            boolean r1 = r0.optBoolean(r1)
            r5.A = r1
            java.lang.String r1 = "autoActive"
            boolean r1 = r0.optBoolean(r1)
            r5.B = r1
            java.lang.String r1 = "appDownloadUrl"
            java.lang.String r0 = r0.optString(r1)
            r5.F = r0
        L_0x00e4:
            java.lang.String r0 = "extra"
            org.json.JSONObject r0 = r6.optJSONObject(r0)
            if (r0 == 0) goto L_0x0104
            java.lang.String r1 = "button"
            java.lang.String r1 = r0.optString(r1)
            r5.g = r1
            java.lang.String r1 = "buttonOpen"
            java.lang.String r1 = r0.optString(r1)
            r5.D = r1
            java.lang.String r1 = "autoOpen"
            boolean r1 = r0.optBoolean(r1)
            r5.C = r1
        L_0x0104:
            java.lang.String r1 = "buttonColor"
            java.lang.String r0 = r0.optString(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            r2 = -1
            if (r1 != 0) goto L_0x0118
            int r0 = android.graphics.Color.parseColor(r0)     // Catch:{ Exception -> 0x0118 }
            r5.E = r0     // Catch:{ Exception -> 0x0118 }
            goto L_0x011a
        L_0x0118:
            r5.E = r2
        L_0x011a:
            java.lang.String r0 = "targetType"
            int r0 = r6.optInt(r0)
            r5.x = r0
            java.lang.String r0 = "viewMonitorUrls"
            org.json.JSONArray r0 = r6.optJSONArray(r0)
            r1 = 0
            if (r0 == 0) goto L_0x014b
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x014b
            int r2 = r0.length()
            java.lang.String[] r2 = new java.lang.String[r2]
            r5.y = r2
            r2 = r1
        L_0x013a:
            int r3 = r0.length()
            if (r2 >= r3) goto L_0x014b
            java.lang.String[] r3 = r5.y
            java.lang.String r4 = r0.optString(r2)
            r3[r2] = r4
            int r2 = r2 + 1
            goto L_0x013a
        L_0x014b:
            java.lang.String r0 = "clickMonitorUrls"
            org.json.JSONArray r6 = r6.optJSONArray(r0)
            if (r6 == 0) goto L_0x0172
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x0172
            int r0 = r6.length()
            java.lang.String[] r0 = new java.lang.String[r0]
            r5.z = r0
        L_0x0161:
            int r0 = r6.length()
            if (r1 >= r0) goto L_0x0172
            java.lang.String[] r0 = r5.z
            java.lang.String r2 = r6.optString(r1)
            r0[r1] = r2
            int r1 = r1 + 1
            goto L_0x0161
        L_0x0172:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.c.c.a(org.json.JSONObject):void");
    }

    public void a(boolean z2) {
        this.H = z2;
    }

    public String[] b() {
        return this.z;
    }

    public String c() {
        return this.q;
    }

    public int d() {
        return this.f3621c;
    }

    public Object e() {
        return this.L;
    }

    public String f() {
        return this.m;
    }

    public String g() {
        return this.f;
    }

    public String h() {
        return this.e;
    }

    public String[] i() {
        return this.y;
    }

    public boolean j() {
        return this.B || (this.I && this.C);
    }

    public boolean k() {
        return this.H;
    }

    public boolean l() {
        return this.I;
    }

    public void onClick(View view) {
        AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) view.getContext();
        if (this.j != 10014) {
            int id = view.getId();
            if (id == R.id.btn_action || id == R.id.button_layout) {
                b(appManagerMainActivity);
            } else if (id != R.id.close) {
                c(appManagerMainActivity);
            } else if (this.I) {
                a(appManagerMainActivity, view);
            } else {
                b(appManagerMainActivity, view);
            }
            if (view.getId() != R.id.close) {
                appManagerMainActivity.a("CLICK", this);
            }
        } else if (C.a(view)) {
            a(view);
        }
    }
}
