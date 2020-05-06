package com.miui.gamebooster.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.x;
import b.b.c.j.y;
import b.b.c.j.z;
import com.miui.activityutil.o;
import com.miui.common.persistence.b;
import com.miui.gamebooster.a.E;
import com.miui.gamebooster.a.F;
import com.miui.gamebooster.d.c;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.h.a;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.ga;
import com.miui.gamebooster.model.g;
import com.miui.gamebooster.model.j;
import com.miui.gamebooster.p.r;
import com.miui.gamebooster.widget.GbSlideOutLayout;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.utils.TypefaceHelper;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.os.Build;
import miui.util.FeatureParser;

public class GameBoxView extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private static ArrayMap<Integer, j> f4130a = new ArrayMap<>();

    /* renamed from: b  reason: collision with root package name */
    private static ArrayMap<Integer, j> f4131b = new ArrayMap<>();

    /* renamed from: c  reason: collision with root package name */
    private static ArrayMap<Integer, Integer> f4132c = new ArrayMap<>();

    /* renamed from: d  reason: collision with root package name */
    private static HashMap<String, Integer> f4133d = new HashMap<>();
    /* access modifiers changed from: private */
    public String A;
    /* access modifiers changed from: private */
    public String B;
    /* access modifiers changed from: private */
    public String C;
    private String D;
    /* access modifiers changed from: private */
    public int E;
    /* access modifiers changed from: private */
    public boolean F;
    private int G;
    private boolean H;
    /* access modifiers changed from: private */
    public boolean I;
    private boolean J;
    private boolean K;
    private boolean L;
    /* access modifiers changed from: private */
    public boolean M = true;
    private boolean N = true;
    private boolean O;
    private boolean P;
    private boolean Q;
    private int R;
    private int S;
    private int T;
    private int U;
    private int V;
    private int W;
    /* access modifiers changed from: private */
    public int[] aa = {R.drawable.gamebox_battery_5, R.drawable.gamebox_battery_10, R.drawable.gamebox_battery_15, R.drawable.gamebox_battery_20, R.drawable.gamebox_battery_25, R.drawable.gamebox_battery_30, R.drawable.gamebox_battery_35, R.drawable.gamebox_battery_40, R.drawable.gamebox_battery_45, R.drawable.gamebox_battery_50, R.drawable.gamebox_battery_55, R.drawable.gamebox_battery_60, R.drawable.gamebox_battery_65, R.drawable.gamebox_battery_70, R.drawable.gamebox_battery_75, R.drawable.gamebox_battery_80, R.drawable.gamebox_battery_85, R.drawable.gamebox_battery_90, R.drawable.gamebox_battery_95, R.drawable.gamebox_battery_100};
    private int ba;
    private GbSlideOutLayout ca;
    private final Runnable da = new C0344m(this);
    /* access modifiers changed from: private */
    public Context e;
    /* access modifiers changed from: private */
    public Runnable ea = new C0348q(this);
    private Handler f;
    private TextView g;
    /* access modifiers changed from: private */
    public TextView h;
    /* access modifiers changed from: private */
    public TextView i;
    /* access modifiers changed from: private */
    public TextView j;
    /* access modifiers changed from: private */
    public TextView k;
    /* access modifiers changed from: private */
    public ImageView l;
    /* access modifiers changed from: private */
    public LinearLayout m;
    /* access modifiers changed from: private */
    public AdapterView<ListAdapter> n;
    /* access modifiers changed from: private */
    public F o;
    /* access modifiers changed from: private */
    public LinearLayout p;
    /* access modifiers changed from: private */
    public LinearLayout q;
    /* access modifiers changed from: private */
    public AdapterView<ListAdapter> r;
    /* access modifiers changed from: private */
    public E s;
    private GameBoxFunctionItemView t;
    private GameBoxFunctionItemView u;
    /* access modifiers changed from: private */
    public String v = o.f2309a;
    /* access modifiers changed from: private */
    public String w = o.f2309a;
    /* access modifiers changed from: private */
    public String x = o.f2309a;
    /* access modifiers changed from: private */
    public String y;
    private String z;

    static {
        j jVar;
        ArrayMap<Integer, j> arrayMap;
        ArrayMap<Integer, j> arrayMap2;
        j jVar2;
        f4130a.put(Integer.valueOf(R.id.first_level_left_arrow), new j(c.FUNCTION, (ResolveInfo) null, new g(d.LEFT_ARROW, R.drawable.gamebox_arrow_shrink_v), R.layout.gamebox_function_item));
        boolean z2 = Build.IS_INTERNATIONAL_BUILD;
        Integer valueOf = Integer.valueOf(R.id.first_level_wechat);
        Integer valueOf2 = Integer.valueOf(R.id.first_level_qq);
        if (!z2) {
            f4130a.put(valueOf2, new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKQQ, R.drawable.gamebox_qq_color_button), R.layout.gamebox_function_item));
            arrayMap2 = f4130a;
            jVar2 = new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKWEIXIN, R.drawable.gamebox_wechat_color_button), R.layout.gamebox_function_item);
        } else {
            if (Locale.getDefault().getLanguage() == null || !Locale.getDefault().getLanguage().equals("ru")) {
                arrayMap = f4130a;
                jVar = new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKWHATSAPP, R.drawable.gamebox_whatsapp_button), R.layout.gamebox_function_item);
            } else {
                arrayMap = f4130a;
                jVar = new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKVK, R.drawable.gamebox_vk_button), R.layout.gamebox_function_item);
            }
            arrayMap.put(valueOf2, jVar);
            arrayMap2 = f4130a;
            jVar2 = new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKFACEBOOK, R.drawable.gamebox_facebook_button), R.layout.gamebox_function_item);
        }
        arrayMap2.put(valueOf, jVar2);
        f4130a.put(Integer.valueOf(R.id.first_level_browser), new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKBROWSER, R.drawable.gamebox_browser_button), R.layout.gamebox_function_item));
        f4130a.put(Integer.valueOf(R.id.first_level_accelerate), new j(c.FUNCTION, (ResolveInfo) null, new g(d.ONEKEYCLEAN, R.drawable.gamebox_accelerate_button), R.layout.gamebox_function_item));
        f4130a.put(Integer.valueOf(R.id.first_level_screenshot), new j(c.FUNCTION, (ResolveInfo) null, new g(d.QUICKSCREENSHOT, R.drawable.gamebox_screenshot_button), R.layout.gamebox_function_item));
        f4130a.put(Integer.valueOf(R.id.first_level_screenrecord), new j(c.FUNCTION, (ResolveInfo) null, new g(d.RECORD, R.drawable.gamebox_screenrecord_button), R.layout.gamebox_function_item));
        f4130a.put(Integer.valueOf(R.id.first_level_right_arrow), new j(c.FUNCTION, (ResolveInfo) null, new g(d.RIGHT_ARROW, R.drawable.gamebox_arrow_shrink_v), R.layout.gamebox_function_item));
        f4133d.put("phoenix", 120);
        f4133d.put("phoenixin", 120);
        f4133d.put("picasso", 120);
        f4133d.put("picassoin", 120);
        f4133d.put("cmi", 90);
        f4133d.put("umi", 90);
    }

    public GameBoxView(Context context) {
        super(context);
        a(context);
    }

    public GameBoxView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public GameBoxView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context);
    }

    private ValueAnimator a(View view, int i2, int i3) {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i2, i3});
        ofInt.setDuration(300);
        ofInt.addUpdateListener(new C0350t(this, view));
        return ofInt;
    }

    private void a(int i2, View view) {
        ValueAnimator a2 = a(view, i2, 0);
        a2.addListener(new C0349s(this, view));
        a2.start();
    }

    private void a(Context context) {
        this.e = context;
        this.f = new Handler(Looper.myLooper());
    }

    private void a(View view, float f2) {
        ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ROTATION, new float[]{0.0f, f2, f2}).start();
    }

    /* access modifiers changed from: private */
    public void a(View view, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (this.q == view) {
            view.setVisibility(i2 < this.ba ? 4 : 0);
        }
        if (this.I) {
            layoutParams.height = i2;
        } else {
            layoutParams.width = i2;
        }
        view.setLayoutParams(layoutParams);
    }

    private void b(View view, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = i2;
        view.setLayoutParams(layoutParams);
    }

    private void b(View view, int i2, int i3) {
        view.setVisibility(0);
        a(view, i2, i3).start();
    }

    private void c() {
        this.g = (TextView) findViewById(R.id.booster_pkg);
        this.h = (TextView) findViewById(R.id.gpu_parameter);
        this.i = (TextView) findViewById(R.id.cpu_parameter);
        this.j = (TextView) findViewById(R.id.fps_parameter);
        this.k = (TextView) findViewById(R.id.gamebox_time);
        this.l = (ImageView) findViewById(R.id.gamebox_battery);
        this.p = (LinearLayout) findViewById(R.id.gamebox_first_level);
        this.m = (LinearLayout) findViewById(R.id.gamebox_second_level_function);
        this.n = (AdapterView) findViewById(R.id.gb_second_level_function_list);
        this.q = (LinearLayout) findViewById(R.id.gamebox_second_level_recent);
        this.r = (AdapterView) findViewById(R.id.gb_recommend_apps_listview);
        this.ca = (GbSlideOutLayout) findViewById(R.id.gbsol);
        this.t = (GameBoxFunctionItemView) findViewById(R.id.first_level_left_arrow);
        this.u = (GameBoxFunctionItemView) findViewById(R.id.first_level_right_arrow);
        String a2 = b.a("key_currentbooster_pkg_uid", (String) null);
        if (a2 != null) {
            this.D = a2.split(",")[0];
        }
        this.g.setTypeface(TypefaceHelper.getMiuiTypefaceForSemiBold(this.e));
        this.h.setTypeface(TypefaceHelper.getMiuiTypefaceForSemiBold(this.e));
        this.i.setTypeface(TypefaceHelper.getMiuiTypefaceForSemiBold(this.e));
        this.j.setTypeface(TypefaceHelper.getMiuiTypefaceForSemiBold(this.e));
        this.k.setTypeface(TypefaceHelper.getMiuiTypefaceForSemiBold(this.e));
        this.z = ga.a("MI GAME GT ");
        this.B = ga.a("CPU ");
        this.A = ga.a("GPU ");
        this.C = ga.a(" FPS");
        String str = this.D;
        if (str != null) {
            this.g.setText(ga.a(x.j(this.e, str).toString()));
        }
        if (!C0388t.g()) {
            this.h.setVisibility(8);
        } else {
            this.h.setText(this.A + this.v + " %");
        }
        this.i.setText(this.B + this.w + " %");
        this.j.setText(this.x + this.C);
        this.k.setText(z.a(System.currentTimeMillis(), WarningCenterAlertAdapter.FORMAT_TIME));
        int e2 = com.miui.powercenter.utils.o.e(this.e);
        if (e2 == 100) {
            e2--;
        }
        this.l.setImageDrawable(this.e.getResources().getDrawable(this.aa[e2 / 5]));
        this.R = this.q.getMinimumHeight();
        if (this.R == 0) {
            this.R = this.e.getResources().getDrawable(R.drawable.gamebox_second_level_function).getMinimumHeight();
        }
        this.ba = -this.e.getResources().getDimensionPixelSize(R.dimen.gamebox_second_level_function_minus);
    }

    private void d() {
        this.M = false;
        this.f.removeCallbacks(this.da);
        this.f.postDelayed(this.da, 200);
        this.W = 0;
        this.K = false;
        this.L = false;
    }

    /* access modifiers changed from: private */
    public int getMaxFps() {
        if (this.G == 0) {
            int[] intArray = FeatureParser.getIntArray("fpsList");
            if (intArray != null && intArray.length > 0) {
                int i2 = intArray[0];
                for (int i3 = 1; i3 < intArray.length; i3++) {
                    if (intArray[i3] > i2) {
                        i2 = intArray[i3];
                    }
                }
                this.G = Math.min(i2, y.a("persist.vendor.dfps.level", 0));
            }
            if (this.G == 0) {
                Integer num = f4133d.get(android.os.Build.DEVICE);
                this.G = (num == null || num.intValue() == 0) ? 60 : num.intValue();
            }
        }
        return this.G;
    }

    public void a() {
        GbSlideOutLayout gbSlideOutLayout = this.ca;
        if (gbSlideOutLayout != null) {
            gbSlideOutLayout.setVisibility(8);
        }
    }

    public void a(int i2) {
        if (i2 <= this.R) {
            this.m.setVisibility(0);
            b((View) this.m, i2);
        }
    }

    public void a(int i2, boolean z2, boolean z3) {
        if (z2) {
            if (this.q.getVisibility() != 8) {
                a(z3 ? i2 : this.R - i2, (View) this.q);
            }
            b((View) this.m, i2, this.R);
            this.t.a(false, true);
            this.t.setmLeftExpand(false);
            this.Q = true;
            this.P = false;
            return;
        }
        if (i2 == 0) {
            i2 = this.R;
        }
        this.Q = false;
        a(i2, (View) this.m);
    }

    public void a(Handler handler) {
        this.F = false;
        handler.removeCallbacks(this.ea);
    }

    public void a(r rVar) {
        ImageView redPointView;
        Integer num;
        for (Map.Entry next : f4130a.entrySet()) {
            GameBoxFunctionItemView gameBoxFunctionItemView = (GameBoxFunctionItemView) findViewById(((Integer) next.getKey()).intValue());
            gameBoxFunctionItemView.a(rVar);
            if (!Build.IS_INTERNATIONAL_BUILD && (num = f4132c.get(next.getKey())) != null) {
                gameBoxFunctionItemView.setTextView(num.intValue());
            }
            gameBoxFunctionItemView.a((j) next.getValue(), this.I);
        }
        for (Map.Entry<Integer, j> key : f4131b.entrySet()) {
            ((GameBoxFunctionItemView) findViewById(((Integer) key.getKey()).intValue())).setVisibility(8);
        }
        this.o = new F(a.a(this.e, this.D), this.I);
        this.n.setAdapter(this.o);
        this.n.setOnItemClickListener(new C0345n(this, rVar));
        List<j> a2 = com.miui.gamebooster.f.c.a().a(this.e);
        if (a2.isEmpty()) {
            this.t.getmImageView().setImageResource(R.drawable.gamebox_arrow_shrink_v_disable);
        } else {
            this.t.getmImageView().setImageResource(R.drawable.gamebox_arrow_shrink_v);
            this.s = new E(a2, this.I);
            this.r.setAdapter(this.s);
            this.r.setOnItemClickListener(new C0346o(this, rVar));
        }
        this.p.getViewTreeObserver().addOnGlobalLayoutListener(new C0347p(this, a2));
        GameBoxFunctionItemView gameBoxFunctionItemView2 = (GameBoxFunctionItemView) findViewById(R.id.first_level_browser);
        if (b.b.l.b.b().c(this.D) && !b.b.l.b.b().j(this.D) && (redPointView = gameBoxFunctionItemView2.getRedPointView()) != null) {
            redPointView.setVisibility(0);
        }
        if (rVar != null && b.b.l.b.b().b(this.D) && !b.b.l.b.b().i(this.D)) {
            rVar.a(this.I, this.J, (View) gameBoxFunctionItemView2);
        }
    }

    public void a(boolean z2) {
        float f2 = (float) (z2 ? -90 : 90);
        a((View) this.t, f2);
        a((View) this.u, f2);
    }

    public boolean a(GbSlideOutLayout.a aVar, int i2, int i3) {
        GbSlideOutLayout gbSlideOutLayout = this.ca;
        if (gbSlideOutLayout != null) {
            return gbSlideOutLayout.a(aVar, i2, i3);
        }
        return false;
    }

    public void b(int i2) {
        if (i2 <= this.R) {
            this.q.setVisibility(0);
            a((View) this.q, i2);
        }
    }

    public void b(int i2, boolean z2, boolean z3) {
        if (z2) {
            a(z3 ? i2 : this.R - i2, (View) this.m);
            b((View) this.q, i2, this.R);
            this.u.a(false, false);
            this.u.setmRightExpand(false);
            this.P = true;
            this.Q = false;
            return;
        }
        if (i2 == 0) {
            i2 = this.R;
        }
        this.P = false;
        a(i2, (View) this.q);
    }

    public void b(Handler handler) {
        this.F = true;
        b.b.c.j.d.a(new r(this, C0388t.g(), handler));
    }

    public boolean b() {
        return this.I;
    }

    public LinearLayout getmGameBoosterFirstLevel() {
        return this.p;
    }

    public GameBoxFunctionItemView getmLeftArrow() {
        return this.t;
    }

    public GameBoxFunctionItemView getmRightArrow() {
        return this.u;
    }

    public int getmSecondHeight() {
        return this.R;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        c();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
        if (r0 != 3) goto L_0x01e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0074, code lost:
        r7 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            boolean r0 = r12.I
            if (r0 == 0) goto L_0x01e4
            boolean r0 = r12.O
            if (r0 == 0) goto L_0x01e4
            int r0 = r13.getAction()
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x01ae
            r4 = 100
            if (r0 == r3) goto L_0x0120
            if (r0 == r1) goto L_0x001c
            r1 = 3
            if (r0 == r1) goto L_0x0120
            goto L_0x01e4
        L_0x001c:
            boolean r0 = r12.N
            if (r0 == 0) goto L_0x011f
            float r0 = r13.getX()
            int r0 = (int) r0
            float r5 = r13.getY()
            int r5 = (int) r5
            int r6 = r12.V
            int r6 = r0 - r6
            int r6 = java.lang.Math.abs(r6)
            double r6 = (double) r6
            r8 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r6 = java.lang.Math.pow(r6, r8)
            int r10 = r12.T
            int r10 = r5 - r10
            int r10 = java.lang.Math.abs(r10)
            double r10 = (double) r10
            double r8 = java.lang.Math.pow(r10, r8)
            double r6 = r6 + r8
            r8 = 0
            double r6 = r6 + r8
            double r6 = java.lang.Math.sqrt(r6)
            int r6 = (int) r6
            int r7 = r12.T
            int r8 = r5 - r7
            int r9 = r12.V
            int r9 = r0 - r9
            int r10 = r12.W
            if (r10 > 0) goto L_0x005f
            int r7 = r5 - r7
            if (r7 <= 0) goto L_0x0119
        L_0x005f:
            boolean r7 = r12.H
            if (r7 == 0) goto L_0x0083
            if (r8 < 0) goto L_0x0076
            if (r9 >= 0) goto L_0x0074
            int r7 = java.lang.Math.abs(r9)
            int r8 = java.lang.Math.abs(r8)
            if (r7 > r8) goto L_0x0072
            goto L_0x0074
        L_0x0072:
            r7 = r2
            goto L_0x009f
        L_0x0074:
            r7 = r3
            goto L_0x009f
        L_0x0076:
            if (r9 <= 0) goto L_0x0072
            int r7 = java.lang.Math.abs(r9)
            int r8 = java.lang.Math.abs(r8)
            if (r7 <= r8) goto L_0x0072
            goto L_0x0074
        L_0x0083:
            if (r8 < 0) goto L_0x0092
            if (r9 <= 0) goto L_0x0074
            int r7 = java.lang.Math.abs(r9)
            int r8 = java.lang.Math.abs(r8)
            if (r7 > r8) goto L_0x0072
            goto L_0x0074
        L_0x0092:
            if (r9 >= 0) goto L_0x0072
            int r7 = java.lang.Math.abs(r9)
            int r8 = java.lang.Math.abs(r8)
            if (r7 <= r8) goto L_0x0072
            goto L_0x0074
        L_0x009f:
            if (r7 == 0) goto L_0x00af
            int r7 = r12.W
            int r6 = r6 / r1
            int r7 = r7 + r6
            r12.W = r7
            int r1 = r12.W
            int r6 = r12.R
            if (r1 <= r6) goto L_0x00ba
            r1 = r6
            goto L_0x00ba
        L_0x00af:
            int r7 = r12.W
            int r6 = r6 / r1
            int r7 = r7 - r6
            r12.W = r7
            int r1 = r12.W
            if (r1 >= 0) goto L_0x00ba
            r1 = r2
        L_0x00ba:
            r12.W = r1
            boolean r1 = r12.H
            if (r1 == 0) goto L_0x00fe
            boolean r1 = r12.K
            if (r1 == 0) goto L_0x00e3
            int r1 = r12.W
            if (r1 <= r4) goto L_0x0119
            boolean r1 = r12.L
            if (r1 != 0) goto L_0x0119
            r12.L = r3
            android.content.Context r1 = r12.e
            android.content.res.Resources r3 = r1.getResources()
            r4 = 2131756376(0x7f100558, float:1.9143658E38)
            java.lang.String r3 = r3.getString(r4)
            android.widget.Toast r1 = android.widget.Toast.makeText(r1, r3, r2)
            r1.show()
            goto L_0x0119
        L_0x00e3:
            boolean r1 = r12.Q
            if (r1 == 0) goto L_0x00f5
            int r1 = r12.W
            r12.b((int) r1)
            int r1 = r12.R
            int r2 = r12.W
            int r1 = r1 - r2
        L_0x00f1:
            r12.a((int) r1)
            goto L_0x0119
        L_0x00f5:
            if (r1 != 0) goto L_0x0119
            boolean r1 = r12.P
            if (r1 != 0) goto L_0x0119
            int r1 = r12.W
            goto L_0x010c
        L_0x00fe:
            boolean r1 = r12.P
            if (r1 == 0) goto L_0x0110
            int r1 = r12.W
            r12.a((int) r1)
            int r1 = r12.R
            int r2 = r12.W
            int r1 = r1 - r2
        L_0x010c:
            r12.b((int) r1)
            goto L_0x0119
        L_0x0110:
            boolean r2 = r12.Q
            if (r2 != 0) goto L_0x0119
            if (r1 != 0) goto L_0x0119
            int r1 = r12.W
            goto L_0x00f1
        L_0x0119:
            r12.V = r0
            r12.T = r5
            goto L_0x01e4
        L_0x011f:
            return r3
        L_0x0120:
            boolean r0 = r12.N
            if (r0 == 0) goto L_0x01ad
            int r0 = r12.W
            if (r0 <= r4) goto L_0x016e
            boolean r13 = r12.H
            if (r13 == 0) goto L_0x014a
            boolean r13 = r12.K
            if (r13 != 0) goto L_0x014a
            boolean r13 = r12.P
            if (r13 != 0) goto L_0x014a
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmLeftArrow()
            int r0 = r12.W
            r13.j = r0
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmLeftArrow()
            boolean r0 = r12.Q
            r13.setmRightExpand(r0)
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmLeftArrow()
            goto L_0x0167
        L_0x014a:
            boolean r13 = r12.H
            if (r13 != 0) goto L_0x016a
            boolean r13 = r12.Q
            if (r13 != 0) goto L_0x016a
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmRightArrow()
            int r0 = r12.W
            r13.j = r0
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmRightArrow()
            boolean r0 = r12.P
            r13.setmLeftExpand(r0)
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r12.getmRightArrow()
        L_0x0167:
            r13.callOnClick()
        L_0x016a:
            r12.d()
            return r3
        L_0x016e:
            if (r0 <= 0) goto L_0x01a9
            boolean r13 = r12.H
            if (r13 == 0) goto L_0x018c
            boolean r13 = r12.K
            if (r13 != 0) goto L_0x018c
            boolean r13 = r12.P
            if (r13 != 0) goto L_0x018c
            r12.b((int) r0, (boolean) r2, (boolean) r3)
            boolean r13 = r12.Q
            if (r13 == 0) goto L_0x01a5
            int r13 = r12.R
            int r0 = r12.W
            int r13 = r13 - r0
            r12.a((int) r13, (boolean) r3, (boolean) r2)
            goto L_0x01a5
        L_0x018c:
            boolean r13 = r12.H
            if (r13 != 0) goto L_0x01a5
            boolean r13 = r12.Q
            if (r13 != 0) goto L_0x01a5
            int r13 = r12.W
            r12.a((int) r13, (boolean) r2, (boolean) r3)
            boolean r13 = r12.P
            if (r13 == 0) goto L_0x01a5
            int r13 = r12.R
            int r0 = r12.W
            int r13 = r13 - r0
            r12.b((int) r13, (boolean) r3, (boolean) r2)
        L_0x01a5:
            r12.d()
            return r3
        L_0x01a9:
            r12.d()
            goto L_0x01e4
        L_0x01ad:
            return r3
        L_0x01ae:
            boolean r0 = r12.M
            r12.N = r0
            boolean r0 = r12.N
            if (r0 == 0) goto L_0x01e4
            float r0 = r13.getX()
            int r0 = (int) r0
            r12.U = r0
            float r0 = r13.getY()
            int r0 = (int) r0
            r12.S = r0
            int r0 = r12.U
            android.content.Context r4 = r12.e
            int r4 = com.miui.gamebooster.m.na.e(r4)
            int r4 = r4 / r1
            if (r0 >= r4) goto L_0x01d0
            r2 = r3
        L_0x01d0:
            r12.H = r2
            int r0 = r12.U
            r12.V = r0
            int r0 = r12.S
            r12.T = r0
            com.miui.gamebooster.f.c r0 = com.miui.gamebooster.f.c.a()
            boolean r0 = r0.b()
            r12.K = r0
        L_0x01e4:
            boolean r13 = super.onTouchEvent(r13)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.GameBoxView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setIsLeftShow(boolean z2) {
        this.J = z2;
    }

    public void setmFirstExpand(boolean z2) {
        this.O = z2;
    }

    public void setmHorzontal(boolean z2) {
        this.I = z2;
    }

    public void setmLeftArrow(GameBoxFunctionItemView gameBoxFunctionItemView) {
        this.t = gameBoxFunctionItemView;
    }

    public void setmRightArrow(GameBoxFunctionItemView gameBoxFunctionItemView) {
        this.u = gameBoxFunctionItemView;
    }

    public void setmSecondFunctionExpand(boolean z2) {
        this.Q = z2;
    }

    public void setmSecondHeight(int i2) {
        this.R = i2;
    }

    public void setmSecondRecentExpand(boolean z2) {
        this.P = z2;
    }
}
