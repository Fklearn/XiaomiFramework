package com.miui.powercenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import com.miui.antivirus.result.C0238a;
import com.miui.common.customview.ActionBarContainer;
import com.miui.common.customview.ScoreTextView;
import com.miui.common.ui.MediaTextureView;
import com.miui.powercenter.abnormalscan.AbScanModel;
import com.miui.powercenter.c.a;
import com.miui.powercenter.c.f;
import com.miui.powercenter.mainui.MainActivityView;
import com.miui.powercenter.quickoptimize.B;
import com.miui.powercenter.quickoptimize.C0531j;
import com.miui.powercenter.quickoptimize.m;
import com.miui.powercenter.quickoptimize.n;
import com.miui.powercenter.quickoptimize.r;
import com.miui.powercenter.quickoptimize.v;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.view.MainHandleBar;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.os.Build;

public class PowerCenter extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    protected static boolean f6626a = false;

    /* renamed from: b  reason: collision with root package name */
    private ArrayList<C0238a> f6627b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    private Context f6628c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public MainActivityView f6629d;
    /* access modifiers changed from: private */
    public g e = g.NORMAL;
    /* access modifiers changed from: private */
    public d f;
    /* access modifiers changed from: private */
    public List<com.miui.powercenter.c.a> g = Collections.synchronizedList(new ArrayList());
    /* access modifiers changed from: private */
    public boolean h = false;
    /* access modifiers changed from: private */
    public boolean i = false;
    private int j = 0;
    /* access modifiers changed from: private */
    public a k = new a(this);
    private r l;
    private com.miui.powercenter.abnormalscan.e m;
    private ActionBarContainer n;
    private MediaTextureView o;
    private ScoreTextView p;
    /* access modifiers changed from: private */
    public boolean q;
    private BroadcastReceiver r = new b(this, (a) null);
    private List<m> s = new ArrayList();
    private List<m> t = new ArrayList();

    private static class a extends b.b.c.i.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6630a;

        public a(PowerCenter powerCenter) {
            this.f6630a = new WeakReference<>(powerCenter);
        }

        public void handleMessage(Message message) {
            PowerCenter powerCenter = (PowerCenter) this.f6630a.get();
            if (powerCenter != null) {
                int i = message.what;
                if (i == 1000) {
                    powerCenter.q();
                } else if (i == 1027) {
                    powerCenter.a(((Float) message.obj).floatValue());
                } else if (i == 1035) {
                    g unused = powerCenter.e = g.SCANNED;
                } else if (i == 1062) {
                    powerCenter.u();
                } else if (i == 1014) {
                    powerCenter.y();
                } else if (i == 1015) {
                    powerCenter.r();
                } else if (i == 1024) {
                    PowerCenter.f6626a = true;
                    powerCenter.l();
                } else if (i == 1025) {
                    powerCenter.f6629d.a();
                } else if (i == 1032) {
                    powerCenter.t();
                } else if (i != 1033) {
                    switch (i) {
                        case 1049:
                            powerCenter.b((com.miui.powercenter.c.a) message.obj);
                            return;
                        case 1050:
                            powerCenter.a((com.miui.powercenter.c.a) message.obj);
                            return;
                        case 1051:
                            powerCenter.s();
                            com.miui.powercenter.a.a.g("finish_scan");
                            return;
                        default:
                            switch (i) {
                                case 1053:
                                    powerCenter.a((C0531j) message.obj);
                                    return;
                                case 1054:
                                    powerCenter.f6629d.e();
                                    return;
                                case 1055:
                                    powerCenter.b(((Float) message.obj).floatValue());
                                    return;
                                default:
                                    return;
                            }
                    }
                } else {
                    powerCenter.a((a.C0063a) message.obj);
                }
            }
        }
    }

    private static class b extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6631a;

        private b(PowerCenter powerCenter) {
            this.f6631a = new WeakReference<>(powerCenter);
        }

        /* synthetic */ b(PowerCenter powerCenter, a aVar) {
            this(powerCenter);
        }

        public void onReceive(Context context, Intent intent) {
            PowerCenter powerCenter = (PowerCenter) this.f6631a.get();
            if (powerCenter != null && intent.getAction().equals("com.miui.powercenter.action.LOAD_OPTIMIZE_TASK")) {
                powerCenter.v();
            }
        }
    }

    private static class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6632a;

        private c(PowerCenter powerCenter) {
            this.f6632a = new WeakReference<>(powerCenter);
        }

        /* synthetic */ c(PowerCenter powerCenter, a aVar) {
            this(powerCenter);
        }

        public void run() {
            PowerCenter powerCenter = (PowerCenter) this.f6632a.get();
            if (powerCenter != null && !powerCenter.i) {
                int i = 0;
                com.miui.powercenter.c.a aVar = new com.miui.powercenter.c.a();
                while (true) {
                    if (powerCenter.e == g.SCANNING || powerCenter.g.size() - 1 >= i) {
                        if (!powerCenter.h && !powerCenter.isFinishing()) {
                            if (powerCenter.g.size() - 1 >= i) {
                                int i2 = i + 1;
                                com.miui.powercenter.c.a aVar2 = (com.miui.powercenter.c.a) powerCenter.g.get(i);
                                Message message = new Message();
                                message.obj = aVar2;
                                message.what = aVar2 instanceof com.miui.powercenter.c.e ? 1049 : 1050;
                                powerCenter.k.sendMessage(message);
                                aVar = aVar2;
                                i = i2;
                            }
                            try {
                                if (a.C0063a.APP == aVar.a()) {
                                    Thread.sleep(80);
                                } else {
                                    Thread.sleep(200);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    } else if (powerCenter.e == g.SCANNED) {
                        Message message2 = new Message();
                        message2.what = 1051;
                        powerCenter.k.sendMessage(message2);
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private static class d implements v.a {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6633a;

        public d(PowerCenter powerCenter) {
            this.f6633a = new WeakReference<>(powerCenter);
        }

        public void a(a.C0063a aVar) {
            PowerCenter powerCenter = (PowerCenter) this.f6633a.get();
            if (powerCenter != null) {
                powerCenter.k.a(1033, aVar);
            }
        }

        public void a(f.a aVar, boolean z) {
            PowerCenter powerCenter = (PowerCenter) this.f6633a.get();
            if (powerCenter != null) {
                powerCenter.k.a(1053, C0531j.a(z, aVar));
            }
        }

        public void c() {
            PowerCenter powerCenter = (PowerCenter) this.f6633a.get();
            if (powerCenter != null) {
                powerCenter.k.a(1035, (Object) null);
            }
        }

        public void d() {
            PowerCenter powerCenter = (PowerCenter) this.f6633a.get();
            if (powerCenter != null) {
                powerCenter.k.a(1038, (Object) null);
            }
        }

        public boolean isCancelled() {
            PowerCenter powerCenter = (PowerCenter) this.f6633a.get();
            if (powerCenter != null) {
                return powerCenter.h;
            }
            return false;
        }
    }

    private static class e implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6634a;

        private e(PowerCenter powerCenter) {
            this.f6634a = new WeakReference<>(powerCenter);
        }

        /* synthetic */ e(PowerCenter powerCenter, a aVar) {
            this(powerCenter);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PowerCenter powerCenter = (PowerCenter) this.f6634a.get();
            if (powerCenter != null && !powerCenter.i) {
                boolean unused = powerCenter.h = true;
                g unused2 = powerCenter.e = g.NORMAL;
                powerCenter.f6629d.setHandleActionButtonEnabled(true);
                powerCenter.f6629d.setActionButtonText(powerCenter.getString(R.string.btn_text_quick_scan));
                powerCenter.f6629d.setContentSummary(powerCenter.getString(R.string.descx_quick_scan_cancel));
                powerCenter.s();
                com.miui.powercenter.a.a.g("stop");
            }
        }
    }

    private static class f implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerCenter> f6635a;

        private f(PowerCenter powerCenter) {
            this.f6635a = new WeakReference<>(powerCenter);
        }

        /* synthetic */ f(PowerCenter powerCenter, a aVar) {
            this(powerCenter);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Activity activity = (PowerCenter) this.f6635a.get();
            if (activity != null) {
                boolean unused = activity.h = true;
                g unused2 = activity.e = g.NORMAL;
                activity.f6629d.setActionButtonText(activity.getString(R.string.btn_text_quick_scan));
                activity.f6629d.setContentSummary(activity.getString(R.string.descx_quick_scan_cancel));
                activity.f.d();
                activity.finish();
                com.miui.powercenter.a.a.g("back");
                if (activity.q) {
                    o.a((android.app.Activity) activity);
                }
            }
        }
    }

    public enum g {
        NORMAL,
        SCANNING,
        SCANNED,
        CLEANING,
        CLEANED
    }

    /* access modifiers changed from: private */
    public void a(float f2) {
        this.f6629d.setContentAlpha(f2);
    }

    /* access modifiers changed from: private */
    public void a(a.C0063a aVar) {
        this.g.add(new com.miui.powercenter.c.e(aVar, (String) null, (String) null, 0));
    }

    /* access modifiers changed from: private */
    public void a(C0531j jVar) {
        com.miui.powercenter.c.f fVar;
        com.miui.powercenter.c.f fVar2;
        switch (b.f6778b[jVar.a().ordinal()]) {
            case 1:
                fVar = new com.miui.powercenter.c.f(a.C0063a.SYSTEM, getResources().getString(R.string.power_center_scan_item_title_clean_memory), (String) null, jVar.b());
                break;
            case 2:
                fVar = new com.miui.powercenter.c.f(a.C0063a.SYSTEM, getResources().getString(R.string.power_center_scan_item_title_brightness), (String) null, jVar.b());
                break;
            case 3:
                fVar = new com.miui.powercenter.c.f(a.C0063a.SYSTEM, getResources().getString(R.string.power_center_scan_item_title_GPS), (String) null, jVar.b());
                break;
            case 4:
                fVar = new com.miui.powercenter.c.f(a.C0063a.ABNORMAL, getResources().getString(R.string.power_center_scan_item_title_abnormal), (String) null, jVar.b());
                break;
            case 5:
                fVar = new com.miui.powercenter.c.f(a.C0063a.ABNORMAL, getResources().getString(R.string.power_center_scan_item_title_abnormal_apps), (String) null, jVar.b());
                break;
            case 6:
                fVar = new com.miui.powercenter.c.f(a.C0063a.APP, getResources().getString(R.string.power_center_scan_item_title_auto_start), (String) null, jVar.b());
                break;
            case 7:
                fVar = new com.miui.powercenter.c.f(a.C0063a.APP, getResources().getString(R.string.power_center_scan_item_title_auto_invoke), (String) null, jVar.b());
                break;
            case 8:
                fVar = new com.miui.powercenter.c.f(a.C0063a.DETAILS, getString(R.string.power_center_scan_item_title_battery_details), (String) null, jVar.b());
                break;
            case 9:
                fVar = new com.miui.powercenter.c.f(a.C0063a.APP, getString(R.string.power_center_scan_item_title_running_app), (String) null, jVar.b());
                break;
            case 10:
                fVar2 = new com.miui.powercenter.c.f(a.C0063a.BLANK, (String) null, (String) null, false);
                break;
            case 11:
                fVar2 = new com.miui.powercenter.c.f(a.C0063a.BLANK, (String) null, (String) null, false);
                break;
            case 12:
                fVar = new com.miui.powercenter.c.f(a.C0063a.SYSTEM, getString(R.string.power_center_scan_item_title_5g), (String) null, jVar.b());
                break;
            default:
                fVar = null;
                break;
        }
        fVar = fVar2;
        this.g.add(fVar);
    }

    /* access modifiers changed from: private */
    public void b(float f2) {
        this.f6629d.setFinalResultAlpha(f2);
    }

    /* access modifiers changed from: private */
    public void b(com.miui.powercenter.c.a aVar) {
        MainActivityView mainActivityView;
        MainHandleBar.b bVar;
        MainActivityView mainActivityView2;
        MainHandleBar.b bVar2;
        MainHandleBar.a aVar2;
        int i2 = b.f6777a[aVar.a().ordinal()];
        if (i2 != 1) {
            if (i2 != 2) {
                if (i2 != 3) {
                    if (i2 != 4) {
                        return;
                    }
                } else if (v.b().k()) {
                    this.f6629d.a(MainHandleBar.b.SYSTEM, MainHandleBar.a.SAFE);
                } else {
                    this.f6629d.a(MainHandleBar.b.SYSTEM, MainHandleBar.a.RISKY);
                    this.f6629d.d();
                }
                if (v.b().i()) {
                    mainActivityView = this.f6629d;
                    bVar = MainHandleBar.b.DETAILS;
                } else {
                    mainActivityView2 = this.f6629d;
                    bVar2 = MainHandleBar.b.DETAILS;
                    aVar2 = MainHandleBar.a.RISKY;
                    mainActivityView2.a(bVar2, aVar2);
                }
            } else if (v.b().h()) {
                mainActivityView2 = this.f6629d;
                bVar2 = MainHandleBar.b.ABNORMAL;
                aVar2 = MainHandleBar.a.RISKY;
                mainActivityView2.a(bVar2, aVar2);
            } else {
                mainActivityView = this.f6629d;
                bVar = MainHandleBar.b.ABNORMAL;
            }
            aVar2 = MainHandleBar.a.SAFE;
            mainActivityView2.a(bVar2, aVar2);
        }
    }

    private void m() {
        this.f6629d.a(this.l.getSectionCount() == 0 ? 0 : n(), true);
        this.k.a(1025, new Object(), 800);
    }

    private int n() {
        return v.b().c();
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [com.miui.powercenter.PowerCenter, miui.app.Activity, android.app.Activity] */
    private void o() {
        Resources resources;
        int i2;
        this.n = (ActionBarContainer) findViewById(R.id.abc_action_bar);
        this.n.setTitle(getString(R.string.power_center_title));
        int a2 = b.b.c.j.e.a(this);
        int b2 = b.b.c.j.e.b();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.o.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.p.getLayoutParams();
        if (a2 <= 1920) {
            this.n.setIsShowSecondTitle(false);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.activity_actionbar_margin_top);
            resources = getResources();
            i2 = R.dimen.pc_scan_score_margin_top;
        } else if (b2 <= 9) {
            this.n.setIsShowSecondTitle(false);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.pc_mainactivity_number_margin_top);
            resources = getResources();
            i2 = R.dimen.pc_scan_score_margin_top_v11;
        } else {
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.pc_mainactivity_number_margin_top_v12);
            resources = getResources();
            i2 = R.dimen.pc_scan_score_margin_top_v12;
        }
        layoutParams2.topMargin = resources.getDimensionPixelSize(i2);
        this.o.setLayoutParams(layoutParams);
        this.p.setLayoutParams(layoutParams2);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerCenter] */
    private void p() {
        v.b().a((Context) this, (v.a) this.f);
    }

    /* access modifiers changed from: private */
    public void q() {
        int i2 = b.f6779c[this.e.ordinal()];
        if (i2 == 1) {
            this.e = g.SCANNING;
            this.h = false;
            t();
            p();
        } else if (i2 == 2 || i2 == 3) {
            x();
        }
    }

    /* access modifiers changed from: private */
    public void r() {
        if (this.i) {
            PowerCenter.super.onBackPressed();
        } else {
            w();
        }
    }

    /* access modifiers changed from: private */
    public void s() {
        if (!this.i) {
            this.i = true;
            this.f6629d.a(this.l, this.m, this.f6627b);
            if (this.m != null) {
                this.k.sendEmptyMessage(1024);
                this.f6629d.setHandleBarVisibility(8);
                return;
            }
            m();
        }
    }

    /* access modifiers changed from: private */
    public void t() {
        this.i = false;
        b.b.c.j.d.a(new c(this, (a) null));
    }

    /* access modifiers changed from: private */
    public void u() {
        B.a(this.f6628c);
    }

    /* access modifiers changed from: private */
    public void v() {
        this.s.clear();
        this.s.addAll(v.b().d());
        this.t.clear();
        this.t.addAll(v.b().a());
        ArrayList arrayList = new ArrayList();
        n nVar = new n();
        nVar.a(new m());
        arrayList.add(nVar);
        if (!this.s.isEmpty()) {
            n nVar2 = new n();
            nVar2.b(R.string.power_optimize_auto_catagory_title);
            for (m next : this.s) {
                nVar2.a(next);
                com.miui.powercenter.a.a.h(next.a());
            }
            arrayList.add(nVar2);
        }
        if (!this.t.isEmpty()) {
            n nVar3 = new n();
            nVar3.b(R.string.power_optimize_catagory_fixed_title);
            nVar3.a(true);
            for (m a2 : this.t) {
                nVar3.a(a2);
            }
            arrayList.add(nVar3);
        }
        this.l.updateData(arrayList);
        this.l.notifyDataSetChanged();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.powercenter.PowerCenter] */
    private void w() {
        new AlertDialog.Builder(this).setTitle(R.string.power_center_stop_scan_title).setMessage(R.string.dialog_msg_stop_virus_scan).setPositiveButton(R.string.ok, new f(this, (a) null)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.powercenter.PowerCenter] */
    private void x() {
        new AlertDialog.Builder(this).setTitle(R.string.power_center_stop_scan_title).setMessage(R.string.dialog_msg_stop_virus_scan).setPositiveButton(R.string.ok, new e(this, (a) null)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerCenter, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void y() {
        com.miui.powercenter.a.a.g("setting");
        startActivity(new Intent(this, PowerSettings.class));
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.powercenter.c.a r5) {
        /*
            r4 = this;
            int[] r0 = com.miui.powercenter.b.f6777a
            com.miui.powercenter.c.a$a r1 = r5.a()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            r1 = 1
            if (r0 == r1) goto L_0x003a
            r2 = 2
            if (r0 == r2) goto L_0x002f
            r2 = 3
            if (r0 == r2) goto L_0x0024
            r2 = 4
            if (r0 == r2) goto L_0x0019
            goto L_0x0049
        L_0x0019:
            boolean r0 = r5.c()
            if (r0 == 0) goto L_0x0049
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            com.miui.powercenter.view.MainHandleBar$b r2 = com.miui.powercenter.view.MainHandleBar.b.APP
            goto L_0x0044
        L_0x0024:
            boolean r0 = r5.c()
            if (r0 == 0) goto L_0x0049
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            com.miui.powercenter.view.MainHandleBar$b r2 = com.miui.powercenter.view.MainHandleBar.b.DETAILS
            goto L_0x0044
        L_0x002f:
            boolean r0 = r5.c()
            if (r0 == 0) goto L_0x0049
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            com.miui.powercenter.view.MainHandleBar$b r2 = com.miui.powercenter.view.MainHandleBar.b.SYSTEM
            goto L_0x0044
        L_0x003a:
            boolean r0 = r5.c()
            if (r0 == 0) goto L_0x0049
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            com.miui.powercenter.view.MainHandleBar$b r2 = com.miui.powercenter.view.MainHandleBar.b.ABNORMAL
        L_0x0044:
            com.miui.powercenter.view.MainHandleBar$a r3 = com.miui.powercenter.view.MainHandleBar.a.RISKY
            r0.a((com.miui.powercenter.view.MainHandleBar.b) r2, (com.miui.powercenter.view.MainHandleBar.a) r3)
        L_0x0049:
            int r0 = r4.j
            int r0 = r0 + r1
            r4.j = r0
            boolean r0 = r5.c()
            if (r0 == 0) goto L_0x0059
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            r0.d()
        L_0x0059:
            int r0 = r4.j
            r1 = 100
            int r0 = r0 * r1
            int r2 = com.miui.powercenter.quickoptimize.v.f()
            int r0 = r0 / r2
            if (r0 <= r1) goto L_0x0066
            r0 = r1
        L_0x0066:
            com.miui.powercenter.mainui.MainActivityView r1 = r4.f6629d
            java.lang.String r0 = com.miui.powercenter.utils.u.a((int) r0)
            r1.setContentProgressText(r0)
            java.lang.String r0 = r5.b()
            if (r0 == 0) goto L_0x0094
            com.miui.powercenter.mainui.MainActivityView r0 = r4.f6629d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 2131756586(0x7f10062a, float:1.9144084E38)
            java.lang.String r2 = r4.getString(r2)
            r1.append(r2)
            java.lang.String r5 = r5.b()
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            r0.setContentSummary(r5)
        L_0x0094:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.PowerCenter.a(com.miui.powercenter.c.a):void");
    }

    public void l() {
        if (f6626a) {
            f6626a = false;
            this.f6629d.c();
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.powercenter.PowerCenter, android.app.Activity] */
    public void onBackPressed() {
        r();
        if (this.q) {
            o.a((android.app.Activity) this);
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [b.b.c.c.a, android.content.Context, com.miui.powercenter.PowerCenter, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pc_activity_main);
        k.a((Activity) this);
        String stringExtra = getIntent().getStringExtra("enter_homepage_way");
        boolean booleanExtra = getIntent().getBooleanExtra("overried_transition", false);
        this.f6628c = getApplicationContext();
        this.f = new d(this);
        this.f6629d = (MainActivityView) findViewById(R.id.main_view);
        this.o = (MediaTextureView) findViewById(R.id.animation_view);
        this.p = (ScoreTextView) findViewById(R.id.number);
        this.f6629d.setEventHandler(this.k);
        this.f6629d.setScanResult(getString(R.string.quick_optimize_checking));
        this.f6629d.setContentSummary(getString(R.string.descx_quick_scan_preparation));
        if (!TextUtils.isEmpty(stringExtra)) {
            com.miui.powercenter.a.a.c(stringExtra);
        }
        o();
        this.n.setActionBarEventListener(new a(this));
        if ("00004".equals(stringExtra)) {
            ArrayList parcelableArrayList = getIntent().getBundleExtra("abnormal_model").getParcelableArrayList("abnormal_list");
            ArrayList arrayList = new ArrayList();
            com.miui.powercenter.abnormalscan.f fVar = new com.miui.powercenter.abnormalscan.f();
            fVar.a(getResources().getString(R.string.power_center_scan_item_title_abnormal_apps));
            fVar.a((List<AbScanModel>) parcelableArrayList);
            fVar.a(com.miui.powercenter.abnormalscan.g.CHECKED);
            com.miui.powercenter.abnormalscan.f fVar2 = new com.miui.powercenter.abnormalscan.f();
            fVar2.a((List<AbScanModel>) new ArrayList());
            arrayList.add(fVar2);
            arrayList.add(fVar);
            this.m = new com.miui.powercenter.abnormalscan.e(arrayList, getLayoutInflater(), this);
            s();
            this.f6629d.a(true, parcelableArrayList.size());
            return;
        }
        q();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.r, new IntentFilter("com.miui.powercenter.action.LOAD_OPTIMIZE_TASK"));
        this.l = new r(this);
        this.l.a((b.b.c.i.b) this.k);
        if (Build.IS_INTERNATIONAL_BUILD) {
            b.b.c.d.o.a("", "");
        }
        this.k.sendEmptyMessageDelayed(1062, 1000);
        if (booleanExtra) {
            this.q = true;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerCenter, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        PowerCenter.super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.r);
        this.f6629d.b();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.powercenter.PowerCenter, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        PowerCenter.super.onNewIntent(intent);
        finish();
        String stringExtra = intent.getStringExtra("enter_homepage_way");
        Intent intent2 = new Intent(this, PowerCenter.class);
        if ("00004".equals(stringExtra)) {
            intent2.putExtra("abnormal_model", intent.getBundleExtra("abnormal_model"));
            intent2.putExtra("enter_homepage_way", "00004");
        }
        startActivity(intent2);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [b.b.c.c.a, android.content.Context, com.miui.powercenter.PowerCenter] */
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        com.miui.powercenter.utils.m.a(this);
    }
}
