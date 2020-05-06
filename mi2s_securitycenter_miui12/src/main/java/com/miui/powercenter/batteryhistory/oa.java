package com.miui.powercenter.batteryhistory;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;
import com.miui.powercenter.batteryhistory.C0508l;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.utils.i;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.view.HistoryCheckGroup;
import com.miui.securitycenter.R;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class oa extends C0508l.a implements HistoryCheckGroup.a {

    /* renamed from: a  reason: collision with root package name */
    private boolean f6911a = false;

    /* renamed from: b  reason: collision with root package name */
    private da f6912b;

    /* renamed from: c  reason: collision with root package name */
    private TextSwitcher f6913c;

    /* renamed from: d  reason: collision with root package name */
    private TextSwitcher f6914d;
    private TextSwitcher e;
    private TextSwitcher f;
    private TextSwitcher g;
    private View h;
    private HistoryCheckGroup i;
    /* access modifiers changed from: private */
    public List<BatteryHistogramItem> j;
    /* access modifiers changed from: private */
    public int k = -1;
    /* access modifiers changed from: private */
    public int l = 0;
    private int m;
    private a n;
    /* access modifiers changed from: private */
    public BatteryHistoryDetailActivity o;
    private ViewSwitcher.ViewFactory p = new ia(this);
    private ViewSwitcher.ViewFactory q = new ja(this);
    private ViewSwitcher.ViewFactory r = new ka(this);
    private ViewSwitcher.ViewFactory s = new la(this);
    private ViewSwitcher.ViewFactory t = new ma(this);

    private class a extends C0497a {
        private a() {
        }

        /* synthetic */ a(oa oaVar, ia iaVar) {
            this();
        }

        public void a(C0514s.a aVar, List<BatteryData> list, List<BatteryHistogramItem> list2) {
            List unused = oa.this.j = list2;
            new Handler(Looper.getMainLooper()).post(new na(this));
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public oa(android.view.ViewGroup r4, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r5) {
        /*
            r3 = this;
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r5)
            r1 = 0
            r2 = 2131493319(0x7f0c01c7, float:1.8610115E38)
            android.view.View r4 = r0.inflate(r2, r4, r1)
            r3.<init>(r4)
            r3.f6911a = r1
            r4 = -1
            r3.k = r4
            r3.l = r1
            com.miui.powercenter.batteryhistory.ia r4 = new com.miui.powercenter.batteryhistory.ia
            r4.<init>(r3)
            r3.p = r4
            com.miui.powercenter.batteryhistory.ja r4 = new com.miui.powercenter.batteryhistory.ja
            r4.<init>(r3)
            r3.q = r4
            com.miui.powercenter.batteryhistory.ka r4 = new com.miui.powercenter.batteryhistory.ka
            r4.<init>(r3)
            r3.r = r4
            com.miui.powercenter.batteryhistory.la r4 = new com.miui.powercenter.batteryhistory.la
            r4.<init>(r3)
            r3.s = r4
            com.miui.powercenter.batteryhistory.ma r4 = new com.miui.powercenter.batteryhistory.ma
            r4.<init>(r3)
            r3.t = r4
            r3.o = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.oa.<init>(android.view.ViewGroup, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity):void");
    }

    private TypefaceSpan a(Typeface typeface) {
        try {
            Constructor<TypefaceSpan> constructor = TypefaceSpan.class.getConstructor(new Class[]{Typeface.class});
            if (constructor == null) {
                return null;
            }
            return constructor.newInstance(new Object[]{typeface});
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return null;
        } catch (InstantiationException e4) {
            e4.printStackTrace();
            return null;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    private void c() {
        int i2;
        Resources resources;
        TextSwitcher textSwitcher;
        if (this.i.getCurrentCheckItem() == 1) {
            this.f6913c.setText(this.o.getResources().getString(R.string.pc_battery_history_item_histogram));
            this.f6914d.setText(this.o.getResources().getString(R.string.pc_battery_history_total_consumption_title));
            textSwitcher = this.f;
            resources = this.o.getResources();
            i2 = R.string.pc_battery_history_consumption_percent_title;
        } else {
            this.f6913c.setText(this.o.getResources().getString(R.string.pc_battery_history_item_chart));
            this.f6914d.setText(this.o.getResources().getString(R.string.pc_battery_history_usetime_title));
            textSwitcher = this.f;
            resources = this.o.getResources();
            i2 = R.string.pc_battery_history_idletime_title;
        }
        textSwitcher.setText(resources.getString(i2));
    }

    /* JADX WARNING: type inference failed for: r0v8, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    public void a() {
        super.a();
        if (this.f6914d == null) {
            this.f6913c = (TextSwitcher) this.itemView.findViewById(R.id.history_title);
            this.h = this.itemView.findViewById(R.id.title_container);
            this.f6914d = (TextSwitcher) this.itemView.findViewById(R.id.txt_batterytitle1);
            this.e = (TextSwitcher) this.itemView.findViewById(R.id.txt_batteryvalue1);
            this.f = (TextSwitcher) this.itemView.findViewById(R.id.txt_batterytitle2);
            this.g = (TextSwitcher) this.itemView.findViewById(R.id.txt_batteryvalue2);
            this.f6913c.setFactory(this.p);
            this.f6914d.setFactory(this.q);
            this.f.setFactory(this.r);
            this.e.setFactory(this.s);
            this.g.setFactory(this.t);
            this.i = (HistoryCheckGroup) this.itemView.findViewById(R.id.history_check);
            this.i.setOnCheckChangeListener(this);
        }
        c();
        if (!this.f6911a) {
            this.f6911a = true;
            this.m = o.c(this.o);
            this.n = new a(this, (ia) null);
            this.o.l().a((ca) this.n);
        } else {
            a(this.k, this.l);
        }
        if (!this.o.l().c()) {
            View view = this.h;
            View view2 = this.itemView;
            view.setVisibility(8);
        }
    }

    public void a(int i2) {
        Message message = new Message();
        message.what = 10004;
        int i3 = 1;
        if (this.i.getCurrentCheckItem() == 1) {
            i3 = 2;
        }
        message.arg1 = i3;
        i.a().a(message);
        Message message2 = new Message();
        message2.what = 10003;
        message2.arg1 = -1;
        message2.arg2 = -1;
        i.a().a(message2);
        if (this.f6912b != null) {
            a(false);
            this.f6912b.a();
            c();
        }
    }

    /* JADX WARNING: type inference failed for: r3v5, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x02ca  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r23, int r24) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r3 = r0.j
            if (r3 != 0) goto L_0x000b
            return
        L_0x000b:
            int r3 = r3.size()
            if (r1 < r3) goto L_0x0012
            return
        L_0x0012:
            if (r2 >= r1) goto L_0x0015
            return
        L_0x0015:
            r0.k = r1
            r0.l = r2
            r1 = 0
            int r3 = r0.k
            r4 = 0
            if (r3 >= 0) goto L_0x002a
            r0.k = r4
            java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r3 = r0.j
            int r3 = r3.size()
            r0.l = r3
        L_0x002a:
            int r3 = r0.k
            r7 = 0
            r9 = 0
        L_0x0030:
            int r11 = r0.l
            r12 = 60
            r14 = 1
            if (r3 >= r11) goto L_0x006a
            java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r11 = r0.j
            java.lang.Object r11 = r11.get(r3)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r11 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r11
            double r5 = r11.totalConsume
            double r1 = r1 + r5
            long r5 = r11.screenUsageTime
            long r7 = r7 + r5
            long r5 = r11.idleUsageTime
            r5 = 3600000(0x36ee80, double:1.7786363E-317)
            if (r3 != 0) goto L_0x0057
            long r12 = r11.endTime
            long r14 = r11.startTime
            long r12 = r12 - r14
            long r5 = java.lang.Math.min(r5, r12)
        L_0x0055:
            long r9 = r9 + r5
            goto L_0x0067
        L_0x0057:
            java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r15 = r0.j
            int r15 = r15.size()
            int r15 = r15 - r14
            if (r3 != r15) goto L_0x0055
            long r5 = r11.minLastItemHold
            long r5 = r5 * r12
            r11 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 * r11
            goto L_0x0055
        L_0x0067:
            int r3 = r3 + 1
            goto L_0x0030
        L_0x006a:
            com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r3 = r0.o
            android.graphics.Typeface r3 = com.miui.powercenter.utils.t.a(r3)
            com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r5 = r0.o
            android.content.res.Resources r5 = r5.getResources()
            r6 = 2131167371(0x7f07088b, float:1.7949014E38)
            int r5 = r5.getDimensionPixelSize(r6)
            com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r6 = r0.o
            android.content.res.Resources r6 = r6.getResources()
            r11 = 2131167389(0x7f07089d, float:1.794905E38)
            int r6 = r6.getDimensionPixelSize(r11)
            com.miui.powercenter.view.HistoryCheckGroup r11 = r0.i
            int r11 = r11.getCurrentCheckItem()
            r15 = 28
            r16 = 3
            r17 = 2
            r12 = 4
            r13 = 18
            if (r11 != r14) goto L_0x0145
            java.util.Locale r7 = java.util.Locale.getDefault()
            java.lang.Object[] r8 = new java.lang.Object[r14]
            long r9 = (long) r1
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            r8[r4] = r9
            java.lang.String r9 = "%d mAh"
            java.lang.String r7 = java.lang.String.format(r7, r9, r8)
            android.text.SpannableString r8 = new android.text.SpannableString
            r8.<init>(r7)
            android.text.style.AbsoluteSizeSpan r9 = new android.text.style.AbsoluteSizeSpan
            r9.<init>(r5)
            android.text.style.AbsoluteSizeSpan r5 = new android.text.style.AbsoluteSizeSpan
            r5.<init>(r6)
            int r6 = r7.length()
            int r6 = r6 + -3
            int r10 = r7.length()
            r8.setSpan(r9, r6, r10, r13)
            int r6 = r7.length()
            int r6 = r6 - r12
            int r10 = r7.length()
            int r10 = r10 + -3
            r8.setSpan(r5, r6, r10, r13)
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r15) goto L_0x00ea
            android.text.style.TypefaceSpan r6 = r0.a((android.graphics.Typeface) r3)
            if (r6 == 0) goto L_0x00ea
            int r7 = r7.length()
            int r7 = r7 - r12
            r8.setSpan(r6, r4, r7, r13)
        L_0x00ea:
            android.widget.TextSwitcher r6 = r0.e
            r6.setText(r8)
            java.util.Locale r6 = java.util.Locale.getDefault()
            java.lang.Object[] r7 = new java.lang.Object[r14]
            int r8 = r0.m
            double r10 = (double) r8
            double r1 = r1 / r10
            r10 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r1 = r1 * r10
            long r1 = java.lang.Math.round(r1)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r7[r4] = r1
            java.lang.String r1 = "%d %%"
            java.lang.String r1 = java.lang.String.format(r6, r1, r7)
            android.text.SpannableString r2 = new android.text.SpannableString
            r2.<init>(r1)
            int r6 = r1.length()
            int r6 = r6 - r14
            int r7 = r1.length()
            r2.setSpan(r9, r6, r7, r13)
            int r6 = r1.length()
            int r6 = r6 + -2
            int r7 = r1.length()
            int r7 = r7 - r14
            r2.setSpan(r5, r6, r7, r13)
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r15) goto L_0x013e
            android.text.style.TypefaceSpan r3 = r0.a((android.graphics.Typeface) r3)
            if (r3 == 0) goto L_0x013e
            int r1 = r1.length()
            int r1 = r1 + -2
            r2.setSpan(r3, r4, r1, r13)
        L_0x013e:
            android.widget.TextSwitcher r1 = r0.g
            r1.setText(r2)
            goto L_0x02f9
        L_0x0145:
            r1 = 60000(0xea60, double:2.9644E-319)
            long r9 = r9 / r1
            long r7 = r7 / r1
            long r9 = r9 - r7
            r1 = 0
            long r1 = java.lang.Math.max(r1, r9)
            r9 = 60
            long r13 = r7 / r9
            int r11 = (int) r13
            int r13 = r11 * 60
            long r13 = (long) r13
            long r7 = r7 - r13
            long r7 = r7 % r9
            int r7 = (int) r7
            long r13 = r1 / r9
            int r8 = (int) r13
            int r13 = r8 * 60
            long r13 = (long) r13
            long r1 = r1 - r13
            long r1 = r1 % r9
            int r1 = (int) r1
            com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r2 = r0.o
            android.content.res.Resources r2 = r2.getResources()
            r9 = 2131624018(0x7f0e0052, float:1.8875204E38)
            java.lang.String r2 = r2.getQuantityString(r9, r11)
            com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r9 = r0.o
            android.content.res.Resources r9 = r9.getResources()
            r10 = 2131624019(0x7f0e0053, float:1.8875206E38)
            java.lang.String r9 = r9.getQuantityString(r10, r7)
            java.util.Locale r10 = java.util.Locale.getDefault()
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r13[r4] = r11
            r11 = 1
            r13[r11] = r2
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r13[r17] = r7
            r13[r16] = r9
            java.lang.String r7 = "%d %s %d %s"
            java.lang.String r10 = java.lang.String.format(r10, r7, r13)
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r10)
            android.text.style.AbsoluteSizeSpan r13 = new android.text.style.AbsoluteSizeSpan
            r13.<init>(r5)
            android.text.style.AbsoluteSizeSpan r14 = new android.text.style.AbsoluteSizeSpan
            r14.<init>(r5)
            android.text.style.AbsoluteSizeSpan r5 = new android.text.style.AbsoluteSizeSpan
            r5.<init>(r6)
            android.text.style.AbsoluteSizeSpan r12 = new android.text.style.AbsoluteSizeSpan
            r12.<init>(r6)
            android.text.style.AbsoluteSizeSpan r4 = new android.text.style.AbsoluteSizeSpan
            r4.<init>(r6)
            int r6 = r10.indexOf(r2)
            int r18 = r10.indexOf(r2)
            int r19 = r2.length()
            int r15 = r18 + r19
            r18 = r7
            r7 = 17
            r11.setSpan(r13, r6, r15, r7)
            int r6 = r10.indexOf(r9)
            int r15 = r10.length()
            r7 = 18
            r11.setSpan(r14, r6, r15, r7)
            int r6 = r10.indexOf(r2)
            r15 = 1
            int r6 = r6 - r15
            int r15 = r10.indexOf(r2)
            r11.setSpan(r5, r6, r15, r7)
            int r6 = r10.indexOf(r2)
            int r15 = r2.length()
            int r6 = r6 + r15
            int r15 = r10.indexOf(r2)
            int r20 = r2.length()
            int r15 = r15 + r20
            r20 = 1
            int r15 = r15 + 1
            r11.setSpan(r12, r6, r15, r7)
            int r6 = r10.indexOf(r9)
            int r6 = r6 + -1
            int r15 = r10.indexOf(r9)
            r11.setSpan(r4, r6, r15, r7)
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 28
            if (r6 < r7) goto L_0x0245
            android.text.style.TypefaceSpan r6 = r0.a((android.graphics.Typeface) r3)
            android.text.style.TypefaceSpan r7 = r0.a((android.graphics.Typeface) r3)
            if (r6 == 0) goto L_0x0245
            if (r7 == 0) goto L_0x0245
            int r15 = r10.indexOf(r2)
            r20 = r3
            r21 = r4
            r3 = 17
            r4 = 0
            r11.setSpan(r6, r4, r15, r3)
            int r3 = r10.indexOf(r2)
            int r4 = r2.length()
            int r3 = r3 + r4
            r4 = 1
            int r3 = r3 + r4
            int r4 = r10.indexOf(r9)
            r6 = 18
            r11.setSpan(r7, r3, r4, r6)
            goto L_0x0249
        L_0x0245:
            r20 = r3
            r21 = r4
        L_0x0249:
            android.widget.TextSwitcher r3 = r0.e
            r3.setText(r11)
            java.util.Locale r3 = java.util.Locale.getDefault()
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)
            r7 = 0
            r4[r7] = r6
            r6 = 1
            r4[r6] = r2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4[r17] = r1
            r4[r16] = r9
            r1 = r18
            java.lang.String r1 = java.lang.String.format(r3, r1, r4)
            android.text.SpannableString r3 = new android.text.SpannableString
            r3.<init>(r1)
            int r4 = r1.indexOf(r2)
            int r4 = r4 - r6
            int r7 = r1.indexOf(r2)
            int r8 = r2.length()
            int r7 = r7 + r8
            int r7 = r7 + r6
            r8 = 17
            r3.setSpan(r13, r4, r7, r8)
            int r4 = r1.indexOf(r9)
            int r4 = r4 - r6
            int r7 = r1.length()
            r8 = 18
            r3.setSpan(r14, r4, r7, r8)
            int r4 = r1.indexOf(r2)
            int r4 = r4 - r6
            int r7 = r1.indexOf(r2)
            r3.setSpan(r5, r4, r7, r8)
            int r4 = r1.indexOf(r2)
            int r5 = r2.length()
            int r4 = r4 + r5
            int r5 = r1.indexOf(r2)
            int r7 = r2.length()
            int r5 = r5 + r7
            int r5 = r5 + r6
            r3.setSpan(r12, r4, r5, r8)
            int r4 = r1.indexOf(r9)
            int r4 = r4 - r6
            int r5 = r1.indexOf(r9)
            r6 = r21
            r3.setSpan(r6, r4, r5, r8)
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 28
            if (r4 < r5) goto L_0x02f4
            r4 = r20
            android.text.style.TypefaceSpan r5 = r0.a((android.graphics.Typeface) r4)
            android.text.style.TypefaceSpan r4 = r0.a((android.graphics.Typeface) r4)
            if (r5 == 0) goto L_0x02f4
            if (r4 == 0) goto L_0x02f4
            int r6 = r1.indexOf(r2)
            r7 = 17
            r8 = 0
            r3.setSpan(r5, r8, r6, r7)
            int r5 = r1.indexOf(r2)
            int r2 = r2.length()
            int r5 = r5 + r2
            r2 = 1
            int r5 = r5 + r2
            int r1 = r1.indexOf(r9)
            r3.setSpan(r4, r5, r1, r7)
        L_0x02f4:
            android.widget.TextSwitcher r1 = r0.g
            r1.setText(r3)
        L_0x02f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.oa.a(int, int):void");
    }

    public void a(da daVar) {
        this.f6912b = daVar;
    }

    public void a(boolean z) {
        HistoryCheckGroup historyCheckGroup = this.i;
        if (historyCheckGroup != null) {
            historyCheckGroup.setEnabled(z);
        }
    }

    public void b() {
        BatteryHistoryDetailActivity batteryHistoryDetailActivity;
        super.b();
        if (this.n != null && (batteryHistoryDetailActivity = this.o) != null) {
            batteryHistoryDetailActivity.l().b(this.n);
        }
    }
}
