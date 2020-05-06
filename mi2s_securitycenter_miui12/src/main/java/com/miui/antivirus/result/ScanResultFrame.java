package com.miui.antivirus.result;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import b.b.c.i.b;
import b.b.g.a;
import com.miui.activityutil.o;
import com.miui.common.customview.AutoPasteListView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONObject;

public class ScanResultFrame extends RelativeLayout implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, a.b {

    /* renamed from: a  reason: collision with root package name */
    private Context f2817a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public b f2818b;

    /* renamed from: c  reason: collision with root package name */
    private AutoPasteListView f2819c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f2820d;
    /* access modifiers changed from: private */
    public boolean e;
    /* access modifiers changed from: private */
    public C0247j f;
    private C0247j g;
    /* access modifiers changed from: private */
    public t h;
    /* access modifiers changed from: private */
    public List<C0244g> i = new ArrayList();
    private boolean j;
    /* access modifiers changed from: private */
    public SharedPreferences k;

    private class a extends AsyncTask<Object, Void, C0247j> {
        private a() {
        }

        /* synthetic */ a(ScanResultFrame scanResultFrame, L l) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(C0247j jVar) {
            boolean unused = ScanResultFrame.this.f2820d = false;
            if (jVar == null) {
                ScanResultFrame.this.h.addAll(ScanResultFrame.this.i);
                return;
            }
            if (jVar.d()) {
                ScanResultFrame.this.k.edit().putBoolean("initSucess", true).commit();
            }
            C0247j unused2 = ScanResultFrame.this.f = jVar;
            List<C0244g> b2 = jVar.b();
            if (b2.isEmpty()) {
                boolean unused3 = ScanResultFrame.this.e = true;
                return;
            }
            ScanResultFrame.this.a(b2, false);
            ScanResultFrame.this.i.addAll(b2);
            ScanResultFrame.this.h.addAll(b2);
        }

        /* access modifiers changed from: protected */
        public C0247j doInBackground(Object... objArr) {
            try {
                return C0247j.a(new JSONObject(C0247j.a((Map<String, String>) objArr[0])), true);
            } catch (Exception e) {
                Log.e("CleanResultFrame", "msg", e);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = ScanResultFrame.this.f2820d = true;
        }
    }

    public ScanResultFrame(Context context) {
        super(context);
    }

    public ScanResultFrame(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: private */
    public void a(List<C0244g> list, boolean z) {
        if (Build.IS_INTERNATIONAL_BUILD && list != null) {
            int i2 = 0;
            for (C0244g next : list) {
                if (next instanceof C0243f) {
                    i2++;
                    C0243f fVar = (C0243f) next;
                    if (!fVar.q()) {
                        int i3 = i2 % 2;
                        C0243f a2 = C0251n.a((JSONObject) null, "", fVar.n());
                        if (a2.q()) {
                            fVar.c(a2);
                            fVar.c(z);
                        } else {
                            Log.d("CleanResultFrame", "international ad hide");
                        }
                    }
                }
            }
        }
    }

    private void c() {
        if (!this.f2820d && !this.e) {
            C0247j jVar = this.g;
            if (jVar != null) {
                a(jVar.b(), false);
                this.f = jVar;
                this.i.addAll(jVar.b());
                this.h.addAll(jVar.b());
                return;
            }
            C0247j jVar2 = this.f;
            if (jVar2 == null || "******************".equals(jVar2.a())) {
                this.f = C0250m.a();
                this.i.addAll(this.f.b());
            }
            HashMap hashMap = new HashMap();
            this.k = this.f2817a.getSharedPreferences("data_config", 0);
            if (!this.k.getBoolean("initSucess", false)) {
                hashMap.put("init", o.f2310b);
            }
            new a(this, (L) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{hashMap});
        }
    }

    public void a() {
        this.f2819c = (AutoPasteListView) findViewById(R.id.list_view);
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.f2819c.setOverScrollMode(2);
        }
        this.f2819c.setAlignItem(0);
        this.f2819c.setMarginTopPixel((int) getResources().getDimension(R.dimen.antivirus_action_bar_height));
        this.f2819c.setAdapter(this.h);
        this.f2819c.setOnItemClickListener(this);
        this.f2819c.setOnScrollListener(this);
        this.f2819c.setOnScrollPercentChangeListener(new L(this));
        if (Build.IS_INTERNATIONAL_BUILD) {
            b.b.g.a.a().a(this);
        }
        if (this.i.isEmpty()) {
            c();
        }
    }

    public void a(Context context, C0247j jVar, t tVar) {
        this.g = jVar;
        this.f2817a = context;
        this.h = tVar;
    }

    public void b() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            b.b.g.a.a().c(this);
            C0247j jVar = this.f;
            if (jVar != null && jVar.b() != null) {
                for (C0244g next : this.f.b()) {
                    if (next instanceof C0243f) {
                        Object k2 = ((C0243f) next).k();
                        C0251n.a(k2);
                        b.b.g.a.a().b(k2);
                    }
                }
            }
        }
    }

    public List<C0244g> getModels() {
        return this.f.b();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j2) {
        C0238a aVar = (C0238a) adapterView.getItemAtPosition(i2);
        if (aVar != null && M.f2816a[aVar.getBaseCardType().ordinal()] == 1) {
            ((C0244g) aVar).onClick(view);
        }
    }

    public void onScroll(AbsListView absListView, int i2, int i3, int i4) {
        if (i2 > 0) {
            this.j = true;
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i2) {
    }

    public void setEventHandler(b bVar) {
        this.f2818b = bVar;
    }
}
