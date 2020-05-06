package com.miui.antivirus.result;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import b.b.b.a.c;
import b.b.c.i.b;
import com.miui.antivirus.model.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;

public class t extends ArrayAdapter<C0238a> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final HashMap<Integer, Integer> f2851a = new HashMap<>();

    /* renamed from: b  reason: collision with root package name */
    private Resources f2852b;

    /* renamed from: c  reason: collision with root package name */
    private LayoutInflater f2853c;

    /* renamed from: d  reason: collision with root package name */
    private b f2854d;
    private Context e;

    static {
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_2), 0);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_3), 1);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_4), 2);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_5), 3);
        f2851a.put(Integer.valueOf(R.layout.result_ad_template_3), 4);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_18), 5);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_19), 6);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_20), 7);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_21), 8);
        f2851a.put(Integer.valueOf(R.layout.result_ad_template_4), 9);
        f2851a.put(Integer.valueOf(R.layout.result_ad_template_40), 10);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_25), 11);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_26), 12);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_27), 13);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_28), 14);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_29), 15);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_30), 16);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_31), 17);
        f2851a.put(Integer.valueOf(R.layout.result_ad_template_25), 18);
        f2851a.put(Integer.valueOf(R.layout.result_ad_template_31), 19);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_empty), 20);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_line), 21);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_bottom), 22);
        f2851a.put(Integer.valueOf(R.layout.v_result_item_template_card_title_1), 23);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_layout_top), 24);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_header), 25);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_line), 26);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_wifi), 27);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_view), 28);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_button), 29);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_safe_system), 30);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_safe_sms), 31);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_safe_app), 32);
        f2851a.put(Integer.valueOf(R.layout.sp_scan_result_safe_monitor), 33);
        f2851a.put(Integer.valueOf(R.layout.result_template_ad_fb), 34);
        f2851a.put(Integer.valueOf(R.layout.result_template_ad_admob_context), 35);
        f2851a.put(Integer.valueOf(R.layout.result_template_ad_admob_install), 36);
        f2851a.put(Integer.valueOf(R.layout.result_template_ad_global_empty), 37);
        f2851a.put(Integer.valueOf(R.layout.result_template_ad_columbus), 38);
    }

    public t(Context context, ArrayList<C0238a> arrayList, b bVar) {
        super(context, 0, arrayList);
        this.f2852b = context.getResources();
        this.f2853c = LayoutInflater.from(context);
        this.e = context;
        this.f2854d = bVar;
    }

    public Drawable a() {
        return new c(this.f2852b.getDrawable(R.drawable.big_backgroud_def));
    }

    public Drawable b() {
        return new c(this.f2852b.getDrawable(R.drawable.small_backgroud_def));
    }

    public int getItemViewType(int i) {
        HashMap<Integer, Integer> hashMap;
        int layoutId;
        if (i + 1 > getCount()) {
            return -1;
        }
        C0238a aVar = (C0238a) getItem(i);
        int i2 = s.f2849a[aVar.getBaseCardType().ordinal()];
        if (i2 == 1) {
            hashMap = f2851a;
            layoutId = ((e) aVar).getLayoutId();
        } else if (i2 != 2) {
            return -1;
        } else {
            hashMap = f2851a;
            layoutId = ((C0244g) aVar).getLayoutId();
        }
        return hashMap.get(Integer.valueOf(layoutId)).intValue();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v23, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v24, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v26, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v27, resolved type: com.miui.antivirus.result.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: com.miui.antivirus.result.F} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v38, resolved type: com.miui.antivirus.result.w} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: com.miui.antivirus.result.w} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: com.miui.antivirus.result.u} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: com.miui.antivirus.result.x} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: com.miui.antivirus.result.B} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: com.miui.antivirus.result.G} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v46, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: com.miui.antivirus.result.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v49, resolved type: com.miui.antivirus.result.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v50, resolved type: com.miui.antivirus.result.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: com.miui.antivirus.result.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: com.miui.antivirus.result.A} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: com.miui.antivirus.result.z} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v58, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v61, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: com.miui.antivirus.result.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v64, resolved type: com.miui.antivirus.result.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: com.miui.antivirus.result.C} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: com.miui.antivirus.result.H} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x023d, code lost:
        r4.f2869b = (android.widget.TextView) r3.findViewById(com.miui.securitycenter.R.id.title);
        r4.f2870c = (android.widget.TextView) r3.findViewById(com.miui.securitycenter.R.id.summary);
        r4.f = (android.widget.Button) r3.findViewById(com.miui.securitycenter.R.id.button);
        r4.f.setOnClickListener(r6);
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0315, code lost:
        r4.f2794d = (android.widget.ImageView) r5;
        r4.f2792b = (android.widget.TextView) r3.findViewById(com.miui.securitycenter.R.id.title);
        r4.f2793c = (android.widget.TextView) r3.findViewById(com.miui.securitycenter.R.id.summary);
        r4.e = r3.findViewById(com.miui.securitycenter.R.id.close);
        r5 = r4.e;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0331, code lost:
        r5.setOnClickListener(r6);
        r4.f2795a = r3.findViewById(com.miui.securitycenter.R.id.inner);
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x033a, code lost:
        r3.setOnClickListener(r6);
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0388, code lost:
        r3.setTag(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x038b, code lost:
        r3.setOnClickListener(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x03f7, code lost:
        if (r1 != null) goto L_0x0443;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0407, code lost:
        if (r1 != null) goto L_0x0443;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0412, code lost:
        r1.setTag(r0);
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r17, android.view.View r18, android.view.ViewGroup r19) {
        /*
            r16 = this;
            r6 = r16
            java.lang.Object r0 = r16.getItem(r17)
            com.miui.antivirus.result.a r0 = (com.miui.antivirus.result.C0238a) r0
            int[] r1 = com.miui.antivirus.result.s.f2849a
            com.miui.antivirus.result.a$a r2 = r0.getBaseCardType()
            int r2 = r2.ordinal()
            r1 = r1[r2]
            r2 = 2131297049(0x7f090319, float:1.8212032E38)
            r3 = 0
            r4 = 3
            r5 = 2
            r7 = 1
            r8 = 0
            if (r1 == r7) goto L_0x04dd
            if (r1 == r5) goto L_0x0024
            r1 = r18
            goto L_0x05e8
        L_0x0024:
            com.miui.antivirus.result.g r0 = (com.miui.antivirus.result.C0244g) r0
            int r1 = r0.getLayoutId()
            if (r18 != 0) goto L_0x03c1
            android.content.Context r9 = r6.e
            android.view.View r3 = android.view.View.inflate(r9, r1, r3)
            r9 = 2131493537(0x7f0c02a1, float:1.8610557E38)
            r10 = 2131297844(0x7f090634, float:1.8213644E38)
            if (r1 == r9) goto L_0x038f
            r9 = 2131297005(0x7f0902ed, float:1.8211943E38)
            r11 = 2131297048(0x7f090318, float:1.821203E38)
            r12 = 2131296975(0x7f0902cf, float:1.8211882E38)
            r13 = 2131296561(0x7f090131, float:1.8211042E38)
            r14 = 2131296620(0x7f09016c, float:1.8211162E38)
            r15 = 2131297740(0x7f0905cc, float:1.8213433E38)
            switch(r1) {
                case 2131493444: goto L_0x0342;
                case 2131493445: goto L_0x0309;
                case 2131493446: goto L_0x02c0;
                case 2131493447: goto L_0x02b6;
                case 2131493448: goto L_0x026e;
                default: goto L_0x004f;
            }
        L_0x004f:
            switch(r1) {
                case 2131493453: goto L_0x025c;
                case 2131493454: goto L_0x025c;
                case 2131493455: goto L_0x025c;
                case 2131493456: goto L_0x025c;
                case 2131493457: goto L_0x025c;
                default: goto L_0x0052;
            }
        L_0x0052:
            switch(r1) {
                case 2131493520: goto L_0x0230;
                case 2131493521: goto L_0x0217;
                case 2131493522: goto L_0x01db;
                case 2131493523: goto L_0x01db;
                case 2131493524: goto L_0x01ac;
                case 2131493525: goto L_0x0139;
                case 2131493526: goto L_0x00e6;
                case 2131493527: goto L_0x01db;
                case 2131493528: goto L_0x01db;
                case 2131493529: goto L_0x01db;
                case 2131493530: goto L_0x0217;
                case 2131493531: goto L_0x01ac;
                case 2131493532: goto L_0x00cf;
                case 2131493533: goto L_0x009a;
                case 2131493534: goto L_0x0066;
                case 2131493535: goto L_0x0057;
                default: goto L_0x0055;
            }
        L_0x0055:
            goto L_0x03bc
        L_0x0057:
            com.miui.antivirus.result.G r4 = new com.miui.antivirus.result.G
            r4.<init>()
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2799a = r5
            goto L_0x033d
        L_0x0066:
            com.miui.antivirus.result.C r4 = new com.miui.antivirus.result.C
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2789b = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2790c = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2791d = r5
            android.view.View r5 = r3.findViewById(r13)
            android.widget.Button r5 = (android.widget.Button) r5
            r4.e = r5
            android.widget.Button r5 = r4.e
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r14)
            r4.f = r5
            android.view.View r5 = r4.f
            goto L_0x0331
        L_0x009a:
            com.miui.antivirus.result.B r4 = new com.miui.antivirus.result.B
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2785a = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2786b = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2787c = r5
            r5 = 2131297308(0x7f09041c, float:1.8212557E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2788d = r5
            r5 = 2131298039(0x7f0906f7, float:1.821404E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.e = r5
            goto L_0x0388
        L_0x00cf:
            com.miui.antivirus.result.x r4 = new com.miui.antivirus.result.x
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2867a = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2868b = r5
            goto L_0x0388
        L_0x00e6:
            com.miui.antivirus.result.v r9 = new com.miui.antivirus.result.v
            r9.<init>()
            android.view.View r10 = r3.findViewById(r10)
            android.widget.TextView r10 = (android.widget.TextView) r10
            r9.f2860b = r10
            android.view.View r10 = r3.findViewById(r13)
            android.widget.Button r10 = (android.widget.Button) r10
            r9.f2861c = r10
            android.widget.ImageView[] r10 = r9.f2862d
            r11 = 2131296976(0x7f0902d0, float:1.8211884E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r10[r8] = r11
            android.widget.ImageView[] r10 = r9.f2862d
            r11 = 2131296977(0x7f0902d1, float:1.8211886E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r10[r7] = r11
            android.widget.ImageView[] r10 = r9.f2862d
            r11 = 2131296978(0x7f0902d2, float:1.8211888E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r10[r5] = r11
            android.widget.ImageView[] r5 = r9.f2862d
            r10 = 2131296979(0x7f0902d3, float:1.821189E38)
            android.view.View r10 = r3.findViewById(r10)
            android.widget.ImageView r10 = (android.widget.ImageView) r10
            r5[r4] = r10
            android.widget.Button r4 = r9.f2861c
            r4.setOnClickListener(r6)
            r3.setTag(r9)
            goto L_0x038b
        L_0x0139:
            com.miui.antivirus.result.u r4 = new com.miui.antivirus.result.u
            r4.<init>()
            r5 = 2131296984(0x7f0902d8, float:1.82119E38)
            android.view.View r5 = r3.findViewById(r5)
            r4.f2855a = r5
            android.view.View r5 = r4.f2855a
            r9 = 2131297735(0x7f0905c7, float:1.8213423E38)
            android.view.View r5 = r5.findViewById(r9)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2858d = r5
            android.view.View r5 = r4.f2855a
            r10 = 2131297734(0x7f0905c6, float:1.8213421E38)
            android.view.View r5 = r5.findViewById(r10)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.g = r5
            android.view.View r5 = r4.f2855a
            r5.setOnClickListener(r6)
            r5 = 2131296985(0x7f0902d9, float:1.8211902E38)
            android.view.View r5 = r3.findViewById(r5)
            r4.f2856b = r5
            android.view.View r5 = r4.f2856b
            android.view.View r5 = r5.findViewById(r9)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.e = r5
            android.view.View r5 = r4.f2856b
            android.view.View r5 = r5.findViewById(r10)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.h = r5
            android.view.View r5 = r4.f2856b
            r5.setOnClickListener(r6)
            r5 = 2131296986(0x7f0902da, float:1.8211904E38)
            android.view.View r5 = r3.findViewById(r5)
            r4.f2857c = r5
            android.view.View r5 = r4.f2857c
            android.view.View r5 = r5.findViewById(r9)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f = r5
            android.view.View r5 = r4.f2857c
            android.view.View r5 = r5.findViewById(r10)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.i = r5
            android.view.View r5 = r4.f2857c
            r5.setOnClickListener(r6)
            goto L_0x033d
        L_0x01ac:
            com.miui.antivirus.result.w r4 = new com.miui.antivirus.result.w
            r4.<init>()
            r5 = 2131297019(0x7f0902fb, float:1.8211971E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2865c = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2863a = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2864b = r5
            android.view.View r5 = r3.findViewById(r14)
            r4.e = r5
            android.view.View r5 = r4.e
            if (r5 == 0) goto L_0x033a
            r5.setOnClickListener(r6)
            goto L_0x033a
        L_0x01db:
            com.miui.antivirus.result.w r4 = new com.miui.antivirus.result.w
            r4.<init>()
            r5 = 2131297019(0x7f0902fb, float:1.8211971E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2865c = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2863a = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2864b = r5
            android.view.View r5 = r3.findViewById(r13)
            android.widget.Button r5 = (android.widget.Button) r5
            r4.f2866d = r5
            android.widget.Button r5 = r4.f2866d
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r14)
            r4.e = r5
            android.view.View r5 = r4.e
            if (r5 == 0) goto L_0x0388
            r5.setOnClickListener(r6)
            goto L_0x0388
        L_0x0217:
            com.miui.antivirus.result.z r4 = new com.miui.antivirus.result.z
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2871d = r5
            r5 = 2131296520(0x7f090108, float:1.821096E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.e = r5
            goto L_0x023d
        L_0x0230:
            com.miui.antivirus.result.z r4 = new com.miui.antivirus.result.z
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2871d = r5
        L_0x023d:
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2869b = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2870c = r5
            android.view.View r5 = r3.findViewById(r13)
            android.widget.Button r5 = (android.widget.Button) r5
            r4.f = r5
            android.widget.Button r5 = r4.f
            r5.setOnClickListener(r6)
            goto L_0x033a
        L_0x025c:
            b.b.g.b r4 = com.miui.antivirus.result.C0251n.a((android.view.View) r3, (com.miui.antivirus.result.C0244g) r0)
            r3.setTag(r4)
            boolean r5 = r4.j
            if (r5 == 0) goto L_0x03bc
            android.view.View r4 = r4.g
            r4.setTag(r0)
            goto L_0x03bc
        L_0x026e:
            com.miui.antivirus.result.A r4 = new com.miui.antivirus.result.A
            r4.<init>()
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2782b = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2783c = r5
            r5 = 2131296730(0x7f0901da, float:1.8211385E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2784d = r5
            android.view.View r5 = r3.findViewById(r9)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.e = r5
            r5 = 2131297006(0x7f0902ee, float:1.8211945E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f = r5
            r5 = 2131297007(0x7f0902ef, float:1.8211947E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.g = r5
            android.view.View r5 = r3.findViewById(r14)
            r4.h = r5
            android.view.View r5 = r4.h
            goto L_0x0331
        L_0x02b6:
            com.miui.antivirus.result.D r4 = new com.miui.antivirus.result.D
            r4.<init>()
            android.view.View r5 = r3.findViewById(r9)
            goto L_0x0315
        L_0x02c0:
            com.miui.antivirus.result.F r4 = new com.miui.antivirus.result.F
            r4.<init>()
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2796b = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2797c = r5
            android.view.View r5 = r3.findViewById(r13)
            android.widget.Button r5 = (android.widget.Button) r5
            r4.g = r5
            android.widget.Button r5 = r4.g
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r9)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2798d = r5
            r5 = 2131297006(0x7f0902ee, float:1.8211945E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.e = r5
            r5 = 2131297007(0x7f0902ef, float:1.8211947E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f = r5
            android.view.View r5 = r3.findViewById(r14)
            r4.h = r5
            android.view.View r5 = r4.h
            goto L_0x0331
        L_0x0309:
            com.miui.antivirus.result.D r4 = new com.miui.antivirus.result.D
            r4.<init>()
            r5 = 2131296521(0x7f090109, float:1.8210961E38)
            android.view.View r5 = r3.findViewById(r5)
        L_0x0315:
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2794d = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2792b = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2793c = r5
            android.view.View r5 = r3.findViewById(r14)
            r4.e = r5
            android.view.View r5 = r4.e
        L_0x0331:
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r11)
            r4.f2795a = r5
        L_0x033a:
            r3.setOnClickListener(r6)
        L_0x033d:
            r3.setTag(r4)
            goto L_0x03bc
        L_0x0342:
            com.miui.antivirus.result.y r4 = new com.miui.antivirus.result.y
            r4.<init>()
            android.view.View r5 = r3.findViewById(r12)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.f2871d = r5
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2869b = r5
            r5 = 2131296521(0x7f090109, float:1.8210961E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.e = r5
            android.view.View r5 = r3.findViewById(r15)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2870c = r5
            android.view.View r5 = r3.findViewById(r13)
            android.widget.Button r5 = (android.widget.Button) r5
            r4.f = r5
            android.widget.Button r5 = r4.f
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r14)
            r4.g = r5
            android.view.View r5 = r4.g
            r5.setOnClickListener(r6)
            android.view.View r5 = r3.findViewById(r11)
            r4.f2795a = r5
        L_0x0388:
            r3.setTag(r4)
        L_0x038b:
            r3.setOnClickListener(r6)
            goto L_0x03bc
        L_0x038f:
            com.miui.antivirus.result.H r4 = new com.miui.antivirus.result.H
            r4.<init>()
            android.view.View r5 = r3.findViewById(r10)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2800a = r5
            r5 = 2131296676(0x7f0901a4, float:1.8211275E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2801b = r5
            r5 = 2131296499(0x7f0900f3, float:1.8210916E38)
            android.view.View r5 = r3.findViewById(r5)
            r4.f2802c = r5
            r5 = 2131296594(0x7f090152, float:1.821111E38)
            android.view.View r5 = r3.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r4.f2803d = r5
            goto L_0x033d
        L_0x03bc:
            b.b.c.j.l.a(r3)
            r9 = r3
            goto L_0x03c3
        L_0x03c1:
            r9 = r18
        L_0x03c3:
            r3 = 2131493534(0x7f0c029e, float:1.861055E38)
            if (r1 == r3) goto L_0x0447
            switch(r1) {
                case 2131493444: goto L_0x0436;
                case 2131493445: goto L_0x042d;
                case 2131493446: goto L_0x041f;
                case 2131493447: goto L_0x042d;
                case 2131493448: goto L_0x0416;
                default: goto L_0x03cb;
            }
        L_0x03cb:
            switch(r1) {
                case 2131493453: goto L_0x0456;
                case 2131493454: goto L_0x0456;
                case 2131493455: goto L_0x0456;
                case 2131493456: goto L_0x0456;
                case 2131493457: goto L_0x0456;
                default: goto L_0x03ce;
            }
        L_0x03ce:
            switch(r1) {
                case 2131493520: goto L_0x040a;
                case 2131493521: goto L_0x040a;
                case 2131493522: goto L_0x03fa;
                case 2131493523: goto L_0x03fa;
                case 2131493524: goto L_0x03ef;
                case 2131493525: goto L_0x03dc;
                case 2131493526: goto L_0x03d3;
                case 2131493527: goto L_0x03fa;
                case 2131493528: goto L_0x03fa;
                case 2131493529: goto L_0x03fa;
                case 2131493530: goto L_0x040a;
                case 2131493531: goto L_0x03ef;
                default: goto L_0x03d1;
            }
        L_0x03d1:
            goto L_0x0455
        L_0x03d3:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.v r1 = (com.miui.antivirus.result.v) r1
            android.widget.Button r1 = r1.f2861c
            goto L_0x0412
        L_0x03dc:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.u r1 = (com.miui.antivirus.result.u) r1
            android.view.View r3 = r1.f2855a
            r3.setTag(r0)
            android.view.View r3 = r1.f2856b
            r3.setTag(r0)
            android.view.View r1 = r1.f2857c
            goto L_0x0443
        L_0x03ef:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.w r1 = (com.miui.antivirus.result.w) r1
            android.view.View r1 = r1.e
            if (r1 == 0) goto L_0x0455
            goto L_0x0409
        L_0x03fa:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.w r1 = (com.miui.antivirus.result.w) r1
            android.widget.Button r3 = r1.f2866d
            r3.setTag(r0)
            android.view.View r1 = r1.e
            if (r1 == 0) goto L_0x0455
        L_0x0409:
            goto L_0x0443
        L_0x040a:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.z r1 = (com.miui.antivirus.result.z) r1
            android.widget.Button r1 = r1.f
        L_0x0412:
            r1.setTag(r0)
            goto L_0x0455
        L_0x0416:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.A r1 = (com.miui.antivirus.result.A) r1
            android.view.View r1 = r1.h
            goto L_0x0443
        L_0x041f:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.F r1 = (com.miui.antivirus.result.F) r1
            android.widget.Button r3 = r1.g
            r3.setTag(r0)
            android.view.View r1 = r1.h
            goto L_0x0443
        L_0x042d:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.D r1 = (com.miui.antivirus.result.D) r1
            android.view.View r1 = r1.e
            goto L_0x0443
        L_0x0436:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.y r1 = (com.miui.antivirus.result.y) r1
            android.widget.Button r3 = r1.f
            r3.setTag(r0)
            android.view.View r1 = r1.g
        L_0x0443:
            r1.setTag(r0)
            goto L_0x0455
        L_0x0447:
            java.lang.Object r1 = r9.getTag()
            com.miui.antivirus.result.C r1 = (com.miui.antivirus.result.C) r1
            android.widget.Button r3 = r1.e
            r3.setTag(r0)
            android.view.View r1 = r1.f
            goto L_0x0443
        L_0x0455:
            r7 = r8
        L_0x0456:
            r1 = 2131297778(0x7f0905f2, float:1.821351E38)
            r9.setTag(r1, r0)
            android.view.View r1 = r9.findViewById(r2)
            r2 = 2131296725(0x7f0901d5, float:1.8211375E38)
            android.view.View r2 = r9.findViewById(r2)
            if (r1 == 0) goto L_0x04c1
            if (r7 != 0) goto L_0x04c1
            android.content.Context r3 = r6.e
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131165589(0x7f070195, float:1.79454E38)
            int r3 = r3.getDimensionPixelSize(r4)
            boolean r4 = r0.b()
            if (r4 == 0) goto L_0x049e
            int r4 = r9.getPaddingStart()
            int r5 = r9.getPaddingEnd()
            boolean r7 = r0.a()
            if (r7 == 0) goto L_0x048d
            r8 = r3
        L_0x048d:
            r9.setPaddingRelative(r4, r3, r5, r8)
            boolean r3 = r0.a()
            if (r3 == 0) goto L_0x049a
            r3 = 2131232365(0x7f08066d, float:1.8080837E38)
            goto L_0x04be
        L_0x049a:
            r3 = 2131232364(0x7f08066c, float:1.8080835E38)
            goto L_0x04be
        L_0x049e:
            int r4 = r9.getPaddingStart()
            int r5 = r9.getPaddingEnd()
            boolean r7 = r0.a()
            if (r7 == 0) goto L_0x04ad
            goto L_0x04ae
        L_0x04ad:
            r3 = r8
        L_0x04ae:
            r9.setPaddingRelative(r4, r8, r5, r3)
            boolean r3 = r0.a()
            if (r3 == 0) goto L_0x04bb
            r3 = 2131232360(0x7f080668, float:1.8080827E38)
            goto L_0x04be
        L_0x04bb:
            r3 = 2131232361(0x7f080669, float:1.808083E38)
        L_0x04be:
            r1.setBackgroundResource(r3)
        L_0x04c1:
            if (r2 == 0) goto L_0x04ce
            boolean r1 = r0.a()
            if (r1 == 0) goto L_0x04ce
            r1 = 8
            r2.setVisibility(r1)
        L_0x04ce:
            android.content.Context r3 = r6.e
            r1 = r17
            r2 = r9
            r4 = r16
            r5 = r19
            r0.a(r1, r2, r3, r4, r5)
            r1 = r9
            goto L_0x05e8
        L_0x04dd:
            com.miui.antivirus.model.e r0 = (com.miui.antivirus.model.e) r0
            if (r18 != 0) goto L_0x04ec
            android.view.LayoutInflater r1 = r6.f2853c
            int r9 = r0.getLayoutId()
            android.view.View r1 = r1.inflate(r9, r3)
            goto L_0x04ee
        L_0x04ec:
            r1 = r18
        L_0x04ee:
            int[] r3 = com.miui.antivirus.result.s.f2850b
            com.miui.antivirus.model.e$b r9 = r0.j()
            int r9 = r9.ordinal()
            r3 = r3[r9]
            if (r3 == r7) goto L_0x05da
            if (r3 == r5) goto L_0x059c
            if (r3 == r4) goto L_0x0590
            r4 = 4
            if (r3 == r4) goto L_0x0582
            r4 = 5
            if (r3 == r4) goto L_0x0508
            goto L_0x05e8
        L_0x0508:
            r3 = r1
            com.miui.antivirus.ui.AppResultView r3 = (com.miui.antivirus.ui.AppResultView) r3
            b.b.c.i.b r4 = r6.f2854d
            r3.setEventHandler(r4)
            r3.a(r0)
            android.view.View r2 = r1.findViewById(r2)
            r3 = 2131296725(0x7f0901d5, float:1.8211375E38)
            android.view.View r3 = r1.findViewById(r3)
            android.content.Context r4 = r6.e
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131165589(0x7f070195, float:1.79454E38)
            int r4 = r4.getDimensionPixelSize(r5)
            boolean r5 = r0.f()
            if (r5 == 0) goto L_0x0551
            int r5 = r1.getPaddingStart()
            int r7 = r1.getPaddingEnd()
            boolean r9 = r0.d()
            if (r9 == 0) goto L_0x0540
            r8 = r4
        L_0x0540:
            r1.setPaddingRelative(r5, r4, r7, r8)
            boolean r4 = r0.d()
            if (r4 == 0) goto L_0x054d
            r4 = 2131232365(0x7f08066d, float:1.8080837E38)
            goto L_0x0571
        L_0x054d:
            r4 = 2131232364(0x7f08066c, float:1.8080835E38)
            goto L_0x0571
        L_0x0551:
            int r5 = r1.getPaddingStart()
            int r7 = r1.getPaddingEnd()
            boolean r9 = r0.d()
            if (r9 == 0) goto L_0x0560
            goto L_0x0561
        L_0x0560:
            r4 = r8
        L_0x0561:
            r1.setPaddingRelative(r5, r8, r7, r4)
            boolean r4 = r0.d()
            if (r4 == 0) goto L_0x056e
            r4 = 2131230998(0x7f080116, float:1.8078065E38)
            goto L_0x0571
        L_0x056e:
            r4 = 2131232361(0x7f080669, float:1.808083E38)
        L_0x0571:
            r2.setBackgroundResource(r4)
            if (r3 == 0) goto L_0x05e8
            boolean r0 = r0.d()
            if (r0 == 0) goto L_0x05e8
            r0 = 8
            r3.setVisibility(r0)
            goto L_0x05e8
        L_0x0582:
            r2 = r1
            com.miui.antivirus.ui.WifiResultView r2 = (com.miui.antivirus.ui.WifiResultView) r2
            b.b.c.i.b r3 = r6.f2854d
            r2.setEventHandler(r3)
            com.miui.antivirus.model.l r0 = (com.miui.antivirus.model.l) r0
            r2.setWifiStatusInfo(r0)
            goto L_0x05e8
        L_0x0590:
            r2 = r1
            com.miui.antivirus.ui.ButtonResultView r2 = (com.miui.antivirus.ui.ButtonResultView) r2
            r2.a(r0)
            b.b.c.i.b r0 = r6.f2854d
            r2.setEventHandler(r0)
            goto L_0x05e8
        L_0x059c:
            r2 = 2131296953(0x7f0902b9, float:1.8211837E38)
            android.view.View r2 = r1.findViewById(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            java.lang.String r3 = r0.r()
            r2.setText(r3)
            android.content.Context r3 = r6.e
            r4 = 2131757779(0x7f100ad3, float:1.9146503E38)
            java.lang.String r3 = r3.getString(r4)
            java.lang.String r0 = r0.k()
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x05c9
            android.content.Context r0 = r6.e
            android.content.res.Resources r0 = r0.getResources()
            r3 = 2131100595(0x7f0603b3, float:1.7813576E38)
            goto L_0x05d2
        L_0x05c9:
            android.content.Context r0 = r6.e
            android.content.res.Resources r0 = r0.getResources()
            r3 = 2131099724(0x7f06004c, float:1.781181E38)
        L_0x05d2:
            int r0 = r0.getColor(r3)
            r2.setTextColor(r0)
            goto L_0x05e8
        L_0x05da:
            com.miui.antivirus.model.e$c r0 = r0.n()
            com.miui.antivirus.model.e$c r2 = com.miui.antivirus.model.e.c.MONITOR
            if (r0 != r2) goto L_0x05e8
            r0 = r1
            com.miui.antivirus.ui.MonitorSafeResultView r0 = (com.miui.antivirus.ui.MonitorSafeResultView) r0
            r0.a()
        L_0x05e8:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.t.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public int getViewTypeCount() {
        return f2851a.size();
    }

    public boolean isEnabled(int i) {
        getItem(i);
        return true;
    }

    public void onClick(View view) {
        ((View.OnClickListener) (view.getTag(R.id.tag_first) != null ? view.getTag(R.id.tag_first) : view.getTag())).onClick(view);
    }
}
