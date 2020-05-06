package com.miui.powercenter.batteryhistory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.b;
import com.miui.powercenter.legacypowerrank.g;
import com.miui.powercenter.legacypowerrank.h;
import com.miui.powercenter.view.NoScrollListView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.miui.powercenter.batteryhistory.d  reason: case insensitive filesystem */
public class C0500d extends LinearLayout implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f6873a;

    /* renamed from: b  reason: collision with root package name */
    private NoScrollListView f6874b;

    /* renamed from: c  reason: collision with root package name */
    private final String f6875c;

    /* renamed from: d  reason: collision with root package name */
    private final String f6876d;
    private final String e;
    private final String f;
    private final String g;
    private a h;
    /* access modifiers changed from: private */
    public List<BatteryData> i;
    /* access modifiers changed from: private */
    public double j;
    /* access modifiers changed from: private */
    public List<BatteryHistogramItem> k;
    /* access modifiers changed from: private */
    public h l;
    private Comparator<BatteryData> m;

    /* renamed from: com.miui.powercenter.batteryhistory.d$a */
    private class a extends C0497a {
        private a() {
        }

        /* synthetic */ a(C0500d dVar, C0498b bVar) {
            this();
        }

        public void a(C0514s.a aVar, List<BatteryData> list, List<BatteryHistogramItem> list2) {
            C0500d dVar = C0500d.this;
            List unused = dVar.i = dVar.b(list);
            C0500d dVar2 = C0500d.this;
            double unused2 = dVar2.j = dVar2.a(list);
            List unused3 = C0500d.this.k = list2;
            new Handler(Looper.getMainLooper()).post(new C0499c(this));
        }
    }

    public C0500d(Context context) {
        this(context, (AttributeSet) null);
    }

    public C0500d(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public C0500d(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f6875c = "com.miui.securitycenter";
        this.f6876d = "com.miui.powerkeeper";
        this.e = "com.android.settings";
        this.f = "com.android.systemui";
        this.g = "com.miui.aod";
        this.m = new C0498b(this);
        this.f6873a = context;
        a();
    }

    private double a(BatteryData batteryData, double d2) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        return (batteryData.getValue() / d2) * 100.0d;
    }

    /* access modifiers changed from: private */
    public double a(List<BatteryData> list) {
        double d2 = 0.0d;
        if (list != null) {
            for (BatteryData batteryData : list) {
                d2 += batteryData.value;
            }
        }
        return d2;
    }

    private String a(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    @TargetApi(26)
    private void a() {
        LayoutInflater.from(this.f6873a).inflate(R.layout.pc_battery_history_rank_list, this);
        this.f6874b = (NoScrollListView) findViewById(R.id.listview);
        this.l = new h();
        this.f6874b.setAdapter(this.l);
        this.f6874b.setOnItemClickListener(this);
        this.h = new a(this, (C0498b) null);
        Context context = this.f6873a;
        if (context instanceof BatteryHistoryDetailActivity) {
            ((BatteryHistoryDetailActivity) context).l().a((ca) this.h);
        }
    }

    /* access modifiers changed from: private */
    public List<BatteryData> b(List<BatteryData> list) {
        BatteryData batteryData;
        ArrayList arrayList;
        String str;
        double d2;
        BatteryData batteryData2;
        BatteryData batteryData3;
        BatteryData batteryData4;
        double d3;
        BatteryData batteryData5;
        BatteryData batteryData6;
        BatteryData batteryData7;
        BatteryData batteryData8;
        BatteryData batteryData9;
        BatteryData batteryData10;
        BatteryData batteryData11;
        ArrayList arrayList2 = new ArrayList(20);
        List<BatteryData> arrayList3 = list == null ? new ArrayList<>() : list;
        double a2 = a(arrayList3);
        Iterator<BatteryData> it = arrayList3.iterator();
        BatteryData batteryData12 = null;
        BatteryData batteryData13 = null;
        BatteryData batteryData14 = null;
        BatteryData batteryData15 = null;
        BatteryData batteryData16 = null;
        BatteryData batteryData17 = null;
        BatteryData batteryData18 = null;
        BatteryData batteryData19 = null;
        BatteryData batteryData20 = null;
        BatteryData batteryData21 = null;
        double d4 = 0.0d;
        double d5 = 0.0d;
        double d6 = 0.0d;
        double d7 = 0.0d;
        double d8 = 0.0d;
        double d9 = 0.0d;
        while (true) {
            batteryData = batteryData12;
            arrayList = arrayList2;
            str = "com.miui.aod";
            d2 = d7;
            if (!it.hasNext()) {
                break;
            }
            Iterator<BatteryData> it2 = it;
            BatteryData next = it.next();
            String str2 = "com.android.systemui";
            String a3 = b.a(this.f6873a, next);
            if (a(next, a2) >= 0.5d && !TextUtils.isEmpty(a3)) {
                String str3 = next.defaultPackageName;
                if (str3 == null || !str3.startsWith("com.miui.securitycenter")) {
                    String str4 = next.defaultPackageName;
                    if (str4 == null || !str4.startsWith("com.miui.powerkeeper")) {
                        String str5 = next.defaultPackageName;
                        if (str5 == null || !str5.startsWith("com.android.settings")) {
                            String str6 = next.defaultPackageName;
                            if (str6 != null) {
                                String str7 = str2;
                                if (str6.startsWith(str7)) {
                                    if (str7.equals(next.defaultPackageName)) {
                                        batteryData7 = new BatteryData(next);
                                    } else {
                                        batteryData19 = new BatteryData(next);
                                        batteryData7 = batteryData18;
                                    }
                                    d7 = d2 + next.getValue();
                                    batteryData5 = batteryData13;
                                    batteryData18 = batteryData7;
                                    d3 = a2;
                                    batteryData12 = batteryData;
                                    arrayList2 = arrayList;
                                    batteryData13 = batteryData5;
                                    it = it2;
                                    a2 = d3;
                                }
                            }
                            String str8 = next.defaultPackageName;
                            if (str8 != null) {
                                String str9 = str;
                                if (str8.startsWith(str9)) {
                                    if (str9.equals(next.defaultPackageName)) {
                                        batteryData6 = new BatteryData(next);
                                    } else {
                                        batteryData21 = new BatteryData(next);
                                        batteryData6 = batteryData20;
                                    }
                                    d3 = a2;
                                    d9 += next.getValue();
                                    batteryData5 = batteryData13;
                                    batteryData20 = batteryData6;
                                    batteryData12 = batteryData;
                                    arrayList2 = arrayList;
                                    d7 = d2;
                                    batteryData13 = batteryData5;
                                    it = it2;
                                    a2 = d3;
                                }
                            }
                            d3 = a2;
                            double d10 = d9;
                            if (next.drainType == 10) {
                                d6 += next.getValue();
                                batteryData5 = batteryData13;
                                d9 = d10;
                                batteryData12 = batteryData;
                                arrayList2 = arrayList;
                                d7 = d2;
                                batteryData13 = batteryData5;
                                it = it2;
                                a2 = d3;
                            } else {
                                arrayList2 = arrayList;
                                arrayList2.add(next);
                                batteryData5 = batteryData13;
                                d9 = d10;
                                batteryData12 = batteryData;
                                d7 = d2;
                                batteryData13 = batteryData5;
                                it = it2;
                                a2 = d3;
                            }
                        } else {
                            if ("com.android.settings".equals(next.defaultPackageName)) {
                                batteryData14 = new BatteryData(next);
                                batteryData8 = batteryData15;
                            } else {
                                batteryData8 = new BatteryData(next);
                            }
                            d8 += next.getValue();
                            batteryData5 = batteryData13;
                            batteryData15 = batteryData8;
                        }
                    } else {
                        if ("com.miui.powerkeeper".equals(next.defaultPackageName)) {
                            batteryData10 = new BatteryData(next);
                            batteryData9 = batteryData13;
                        } else {
                            batteryData9 = new BatteryData(next);
                            batteryData10 = batteryData17;
                        }
                        d4 += next.getValue();
                        batteryData17 = batteryData10;
                        d3 = a2;
                        batteryData5 = batteryData9;
                        batteryData12 = batteryData;
                        arrayList2 = arrayList;
                        d7 = d2;
                        batteryData13 = batteryData5;
                        it = it2;
                        a2 = d3;
                    }
                } else {
                    if ("com.miui.securitycenter".equals(next.defaultPackageName)) {
                        batteryData12 = new BatteryData(next);
                        batteryData11 = batteryData16;
                    } else {
                        batteryData11 = new BatteryData(next);
                        batteryData12 = batteryData;
                    }
                    d5 += next.getValue();
                    batteryData5 = batteryData13;
                    batteryData16 = batteryData11;
                    d3 = a2;
                    arrayList2 = arrayList;
                    d7 = d2;
                    batteryData13 = batteryData5;
                    it = it2;
                    a2 = d3;
                }
            } else {
                d6 += next.getValue();
                batteryData5 = batteryData13;
            }
            d3 = a2;
            batteryData12 = batteryData;
            arrayList2 = arrayList;
            d7 = d2;
            batteryData13 = batteryData5;
            it = it2;
            a2 = d3;
        }
        double d11 = d6;
        double d12 = d9;
        String str10 = str;
        String str11 = "com.android.systemui";
        ArrayList arrayList4 = arrayList;
        if (d5 > 0.0d) {
            if (batteryData != null) {
                batteryData4 = batteryData;
                batteryData4.value = d5;
            } else if (batteryData16 != null) {
                batteryData4 = batteryData16;
                batteryData4.value = d5;
                batteryData4.defaultPackageName = "com.miui.securitycenter";
                batteryData4.name = a(this.f6873a, "com.miui.securitycenter");
            }
            arrayList4.add(batteryData4);
        }
        if (d4 > 0.0d) {
            if (batteryData17 != null) {
                batteryData3 = batteryData17;
                batteryData3.value = d4;
            } else if (batteryData13 != null) {
                batteryData3 = batteryData13;
                batteryData3.value = d4;
                batteryData3.defaultPackageName = "com.miui.powerkeeper";
                batteryData3.name = a(this.f6873a, "com.miui.powerkeeper");
            }
            arrayList4.add(batteryData3);
        }
        if (d8 > 0.0d) {
            if (batteryData14 != null) {
                batteryData2 = batteryData14;
                batteryData2.value = d8;
            } else {
                batteryData2 = batteryData15;
                if (batteryData2 != null) {
                    batteryData2.value = d8;
                    batteryData2.defaultPackageName = "com.android.settings";
                    batteryData2.name = a(this.f6873a, "com.android.settings");
                }
            }
            arrayList4.add(batteryData2);
        }
        if (d2 > 0.0d) {
            BatteryData batteryData22 = batteryData18;
            if (batteryData22 != null) {
                batteryData22.value = d2;
            } else {
                batteryData22 = batteryData19;
                double d13 = d2;
                if (batteryData22 != null) {
                    batteryData22.value = d13;
                    batteryData22.defaultPackageName = str11;
                    batteryData22.name = a(this.f6873a, str11);
                }
            }
            arrayList4.add(batteryData22);
        }
        if (d12 > 0.0d) {
            BatteryData batteryData23 = batteryData20;
            if (batteryData23 != null) {
                batteryData23.value = d12;
            } else {
                batteryData23 = batteryData21;
                if (batteryData23 != null) {
                    batteryData23.value = d12;
                    batteryData23.defaultPackageName = str10;
                    batteryData23.name = a(this.f6873a, str10);
                }
            }
            arrayList4.add(batteryData23);
        }
        Collections.sort(arrayList4, this.m);
        if (d11 > 0.0d) {
            BatteryData batteryData24 = new BatteryData();
            batteryData24.drainType = 10;
            batteryData24.value = d11;
            arrayList4.add(batteryData24);
        }
        return arrayList4;
    }

    public void a(int i2, int i3) {
        if (this.i != null) {
            if (i2 < 0) {
                this.l.b();
                this.l.a(this.j);
                this.l.a(this.i);
            } else if (i2 >= 0 && i2 < this.k.size() && i3 >= 0 && i3 < this.k.size() + 1) {
                List<BatteryData> list = null;
                double d2 = 0.0d;
                for (BatteryHistogramItem next : this.k.subList(i2, i3)) {
                    if (next.type != 1) {
                        d2 += next.totalConsume;
                        list = list == null ? new ArrayList<>(next.batteryDataList) : com.miui.powercenter.batteryhistory.b.a.b(list, next.batteryDataList);
                    }
                }
                this.l.b();
                this.l.a(d2);
                this.l.a(b(list));
            }
        }
    }

    public void a(boolean z) {
        h hVar = this.l;
        if (hVar != null) {
            hVar.a(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NoScrollListView noScrollListView = this.f6874b;
        if (noScrollListView != null) {
            noScrollListView.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.h != null) {
            Context context = this.f6873a;
            if (context instanceof BatteryHistoryDetailActivity) {
                ((BatteryHistoryDetailActivity) context).l().b(this.h);
            }
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j2) {
        BatteryData batteryData = (BatteryData) this.l.getItem(i2);
        g.a(this.f6873a, batteryData, (batteryData.getValue() / this.l.a()) * 100.0d);
    }
}
