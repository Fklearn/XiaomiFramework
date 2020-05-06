package com.miui.antivirus.whitelist;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import com.miui.antivirus.whitelist.j;
import com.miui.common.stickydecoration.f;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miuix.recyclerview.widget.RecyclerView;

public class WhiteListActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<Boolean>, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private boolean f3011a = false;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public String f3012b = "key_show_toast";
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public List<h> f3013c = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Button f3014d;
    private TextView e;
    private RecyclerView f;
    private RecyclerView.f g;
    private b h;
    /* access modifiers changed from: private */
    public j i;
    private c j;
    private a k = new a(this, (a) null);

    private class a extends b.b.c.i.b {
        private a() {
        }

        /* synthetic */ a(WhiteListActivity whiteListActivity, a aVar) {
            this();
        }

        private void a() {
            if (!WhiteListActivity.this.f3013c.isEmpty()) {
                Log.i("removeWhiteList", "begin---------");
                ArrayList<i> arrayList = new ArrayList<>();
                for (h hVar : WhiteListActivity.this.f3013c) {
                    for (i next : hVar.f3041b) {
                        if (next.e()) {
                            arrayList.add(next);
                        }
                    }
                }
                ArrayList arrayList2 = new ArrayList();
                for (i iVar : arrayList) {
                    if (iVar.e()) {
                        arrayList2.add(iVar.a());
                    }
                }
                String[] strArr = new String[arrayList2.size()];
                int i = 0;
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    strArr[i] = (String) it.next();
                    i++;
                }
                WhiteListActivity.this.i.a(strArr);
            }
        }

        private void b() {
            Bundle bundle = new Bundle();
            bundle.putBoolean(WhiteListActivity.this.f3012b, true);
            WhiteListActivity.this.getLoaderManager().restartLoader(100, bundle, WhiteListActivity.this);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1021) {
                a();
            } else if (i == 1022) {
                b();
            }
        }
    }

    private class b extends RecyclerView.a<a> {

        /* renamed from: a  reason: collision with root package name */
        private LayoutInflater f3016a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Set<i> f3017b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public List<h> f3018c;

        /* renamed from: d  reason: collision with root package name */
        private List<i> f3019d;

        private class a extends RecyclerView.u {
            /* access modifiers changed from: private */

            /* renamed from: a  reason: collision with root package name */
            public TextView f3020a;
            /* access modifiers changed from: private */

            /* renamed from: b  reason: collision with root package name */
            public TextView f3021b;
            /* access modifiers changed from: private */

            /* renamed from: c  reason: collision with root package name */
            public CheckBox f3022c;
            /* access modifiers changed from: private */

            /* renamed from: d  reason: collision with root package name */
            public ImageView f3023d;
            /* access modifiers changed from: private */
            public View itemView;

            private a(View view) {
                super(view);
                this.itemView = view;
                this.f3023d = (ImageView) view.findViewById(R.id.icon);
                this.f3020a = (TextView) view.findViewById(R.id.title);
                this.f3021b = (TextView) view.findViewById(R.id.summary);
                this.f3022c = (CheckBox) view.findViewById(R.id.checkbox);
            }

            /* synthetic */ a(b bVar, View view, a aVar) {
                this(view);
            }
        }

        private b(Context context) {
            this.f3017b = new HashSet();
            this.f3018c = new ArrayList();
            this.f3019d = new ArrayList();
            this.f3016a = LayoutInflater.from(context);
        }

        /* synthetic */ b(WhiteListActivity whiteListActivity, Context context, a aVar) {
            this(context);
        }

        private void a(boolean z) {
            com.miui.common.persistence.b.b("key_first_enter_virus_whitelist", z);
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antivirus.whitelist.WhiteListActivity] */
        private void b() {
            new AlertDialog.Builder(WhiteListActivity.this).setTitle(R.string.long_click_dialog_title).setMessage(R.string.long_click_dialog_summary).setPositiveButton(R.string.button_text_let_me_select, new c(this)).show();
        }

        private boolean c() {
            return com.miui.common.persistence.b.a("key_first_enter_virus_whitelist", true);
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull a aVar, int i) {
            String str;
            StringBuilder sb;
            i iVar = this.f3019d.get(i);
            int i2 = b.f3030a[iVar.b().ordinal()];
            if (i2 != 1) {
                if (i2 == 2) {
                    aVar.f3021b.setText(R.string.hints_virus_apk_list_item_summary);
                    sb = new StringBuilder();
                    sb.append("apk_icon://");
                    str = iVar.a();
                }
                aVar.f3020a.setText(iVar.d());
                aVar.f3022c.setTag(iVar);
                aVar.f3022c.setChecked(iVar.e());
                aVar.f3022c.setOnCheckedChangeListener(new d(this, iVar));
                aVar.itemView.setOnClickListener(new e(this, aVar));
            }
            aVar.f3021b.setText(R.string.hints_virus_app_list_item_summary);
            sb = new StringBuilder();
            sb.append("pkg_icon://");
            str = iVar.c();
            sb.append(str);
            r.a(sb.toString(), aVar.f3023d, r.f);
            aVar.f3020a.setText(iVar.d());
            aVar.f3022c.setTag(iVar);
            aVar.f3022c.setChecked(iVar.e());
            aVar.f3022c.setOnCheckedChangeListener(new d(this, iVar));
            aVar.itemView.setOnClickListener(new e(this, aVar));
        }

        public void a(List<h> list) {
            this.f3018c.clear();
            this.f3018c.addAll(list);
            this.f3017b.clear();
            this.f3019d.clear();
            for (int i = 0; i < list.size(); i++) {
                this.f3019d.addAll(list.get(i).f3041b);
            }
            WhiteListActivity.this.f3014d.setVisibility(!this.f3018c.isEmpty() ? 0 : 8);
            if (c()) {
                if (!this.f3018c.isEmpty()) {
                    b();
                }
                a(false);
            }
        }

        public int getItemCount() {
            return this.f3019d.size();
        }

        @NonNull
        public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new a(this, this.f3016a.inflate(R.layout.v_white_list_item_view, viewGroup, false), (a) null);
        }
    }

    private class c extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private b.b.c.i.b f3024a;

        private c(b.b.c.i.b bVar) {
            super(bVar);
            this.f3024a = bVar;
        }

        /* synthetic */ c(WhiteListActivity whiteListActivity, b.b.c.i.b bVar, a aVar) {
            this(bVar);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            this.f3024a.sendEmptyMessage(1022);
        }
    }

    private static class d extends b.b.c.i.a<Boolean> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<WhiteListActivity> f3026b;

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.whitelist.WhiteListActivity, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        d(com.miui.antivirus.whitelist.WhiteListActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f3026b = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.whitelist.WhiteListActivity.d.<init>(com.miui.antivirus.whitelist.WhiteListActivity):void");
        }

        public Boolean loadInBackground() {
            WhiteListActivity whiteListActivity = (WhiteListActivity) this.f3026b.get();
            if (whiteListActivity != null) {
                int color = whiteListActivity.getResources().getColor(R.color.high_light_green);
                List<j.b> a2 = whiteListActivity.i.a();
                if (!a2.isEmpty()) {
                    String quantityString = whiteListActivity.getResources().getQuantityString(R.plurals.white_list_risk_app_header, a2.size(), new Object[]{Integer.valueOf(a2.size())});
                    g gVar = new g();
                    gVar.a(false);
                    gVar.a((CharSequence) whiteListActivity.a(quantityString, color, String.valueOf(a2.size())));
                    gVar.a(k.RISK_APP);
                    for (j.b next : a2) {
                        List list = null;
                        for (h hVar : whiteListActivity.f3013c) {
                            if (hVar.f3040a == gVar) {
                                list = hVar.f3041b;
                            }
                        }
                        if (list == null) {
                            list = new ArrayList();
                            whiteListActivity.f3013c.add(new h(gVar, list));
                        }
                        list.add(i.a(next));
                    }
                }
                WhiteListActivity whiteListActivity2 = (WhiteListActivity) this.f3026b.get();
                if (whiteListActivity2 != null) {
                    List<j.c> b2 = whiteListActivity2.i.b();
                    if (!b2.isEmpty()) {
                        String quantityString2 = whiteListActivity2.getResources().getQuantityString(R.plurals.white_list_trojan_header, b2.size(), new Object[]{Integer.valueOf(b2.size())});
                        g gVar2 = new g();
                        gVar2.a(false);
                        gVar2.a((CharSequence) whiteListActivity2.a(quantityString2, color, String.valueOf(b2.size())));
                        gVar2.a(k.TROJAN);
                        for (j.c next2 : b2) {
                            List list2 = null;
                            for (h hVar2 : whiteListActivity2.f3013c) {
                                if (hVar2.f3040a == gVar2) {
                                    list2 = hVar2.f3041b;
                                }
                            }
                            if (list2 == null) {
                                list2 = new ArrayList();
                                whiteListActivity2.f3013c.add(new h(gVar2, list2));
                            }
                            list2.add(i.a(next2));
                        }
                    }
                    return null;
                }
            }
            return (Boolean) super.loadInBackground();
        }
    }

    private void l() {
        this.f.b(this.g);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < this.h.f3018c.size(); i3++) {
            for (int i4 = 0; i4 < ((h) this.h.f3018c.get(i3)).f3041b.size(); i4++) {
                hashMap.put(Integer.valueOf(i4 + i2), ((h) this.h.f3018c.get(i3)).f3040a.a().toString());
            }
            i2 += ((h) this.h.f3018c.get(i3)).f3041b.size();
        }
        this.g = f.a.a((com.miui.common.stickydecoration.b.c) new a(this, hashMap)).a();
        this.f.a(this.g);
    }

    private void m() {
        int i2;
        TextView textView;
        if (this.f3013c.size() == 0) {
            textView = this.e;
            i2 = 0;
        } else {
            textView = this.e;
            i2 = 8;
        }
        textView.setVisibility(i2);
        this.h.a(this.f3013c);
        this.h.notifyDataSetChanged();
        l();
    }

    private void n() {
        try {
            getContentResolver().registerContentObserver(f.f3036a, true, this.j);
        } catch (Exception e2) {
            Log.e("WhiteListActivity", e2.toString());
        }
    }

    public SpannableString a(String str, int i2, String... strArr) {
        SpannableString spannableString = new SpannableString(str);
        try {
            for (String str2 : strArr) {
                int indexOf = str.indexOf(str2);
                spannableString.setSpan(new ForegroundColorSpan(i2), indexOf, str2.length() + indexOf, 34);
            }
        } catch (Exception e2) {
            Log.e("WhiteListActivity", "msg", e2);
        }
        return spannableString;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antivirus.whitelist.WhiteListActivity] */
    /* renamed from: a */
    public void onLoadFinished(Loader<Boolean> loader, Boolean bool) {
        this.f3014d.setEnabled(false);
        m();
        if (this.f3011a) {
            Toast.makeText(this, R.string.toast_removed_from_virus_white_list, 0).show();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.v_white_list_cleanup_btn) {
            this.k.sendEmptyMessage(1021);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, android.view.View$OnClickListener, com.miui.antivirus.whitelist.WhiteListActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.v_activity_white_list);
        this.i = j.a(getApplicationContext());
        this.f3014d = (Button) findViewById(R.id.v_white_list_cleanup_btn);
        this.f3014d.setOnClickListener(this);
        this.e = (TextView) findViewById(R.id.v_empty_view);
        this.f = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.v_white_list);
        this.f.setLayoutManager(new LinearLayoutManager(this));
        this.h = new b(this, this, (a) null);
        this.f.setAdapter(this.h);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(this.f3012b, false);
        getLoaderManager().initLoader(100, bundle2, this);
        this.j = new c(this, this.k, (a) null);
        n();
    }

    public Loader<Boolean> onCreateLoader(int i2, Bundle bundle) {
        this.f3013c.clear();
        this.f3011a = bundle.getBoolean(this.f3012b);
        return new d(this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        WhiteListActivity.super.onDestroy();
        try {
            getLoaderManager().destroyLoader(100);
            getContentResolver().unregisterContentObserver(this.j);
        } catch (Exception e2) {
            Log.e("WhiteListActivity", e2.toString());
        }
    }

    public void onLoaderReset(Loader<Boolean> loader) {
    }
}
