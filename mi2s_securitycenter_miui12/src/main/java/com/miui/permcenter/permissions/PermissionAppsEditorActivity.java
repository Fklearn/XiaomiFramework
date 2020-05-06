package com.miui.permcenter.permissions;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidxc.recyclerview.widget.LinearLayoutManager;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.permcenter.compact.PermissionManagerCompat;
import com.miui.permcenter.n;
import com.miui.permcenter.permissions.o;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import miui.app.Activity;
import miui.app.AlertDialog;
import miuix.recyclerview.widget.RecyclerView;

public class PermissionAppsEditorActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<u>, o.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public long f6226a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public a f6227b;

    /* renamed from: c  reason: collision with root package name */
    private RecyclerView f6228c;

    /* renamed from: d  reason: collision with root package name */
    private View f6229d;
    private String e;
    private String f;
    /* access modifiers changed from: private */
    public ArrayList<com.miui.permcenter.a> g;
    private int h;
    private d i;
    private b.b.c.i.a<u> j;
    private c k;

    public static class a extends RecyclerView.a<e> {

        /* renamed from: a  reason: collision with root package name */
        private boolean f6230a = true;

        /* renamed from: b  reason: collision with root package name */
        private long f6231b;

        /* renamed from: c  reason: collision with root package name */
        private Context f6232c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public ArrayList<com.miui.permcenter.a> f6233d = new ArrayList<>();
        /* access modifiers changed from: private */
        public o.a e;

        public a(Context context, long j) {
            this.f6231b = j;
            this.f6232c = context;
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull e eVar, int i) {
            float f;
            ImageView imageView;
            ImageView imageView2;
            int i2;
            ImageView imageView3;
            int i3;
            ImageView imageView4;
            int i4;
            r.a("pkg_icon://" + this.f6233d.get(i).e(), eVar.f6240b, r.f);
            if (this.f6230a) {
                imageView = eVar.f6240b;
                f = 1.0f;
            } else {
                imageView = eVar.f6240b;
                f = 0.5f;
            }
            imageView.setAlpha(f);
            eVar.itemView.setOnClickListener(new y(this, i));
            eVar.f6239a.setText(x.j(this.f6232c, this.f6233d.get(i).e()));
            eVar.f6239a.setEnabled(this.f6230a);
            eVar.itemView.setEnabled(this.f6230a);
            int intValue = this.f6233d.get(i).f().get(Long.valueOf(this.f6231b)).intValue();
            int i5 = 0;
            if (intValue == 1) {
                if (this.f6230a) {
                    imageView2 = eVar.f6241c;
                    i2 = R.drawable.icon_action_reject;
                } else {
                    imageView2 = eVar.f6241c;
                    i2 = R.drawable.icon_action_reject_disable;
                }
                imageView2.setImageResource(i2);
                i5 = R.string.permission_action_reject;
            } else if (intValue == 2) {
                if (this.f6230a) {
                    imageView3 = eVar.f6241c;
                    i3 = R.drawable.icon_action_prompt;
                } else {
                    imageView3 = eVar.f6241c;
                    i3 = R.drawable.icon_action_prompt_disable;
                }
                imageView3.setImageResource(i3);
                i5 = R.string.permission_action_prompt;
            } else if (intValue == 3) {
                if (this.f6230a) {
                    imageView4 = eVar.f6241c;
                    i4 = R.drawable.icon_action_accept;
                } else {
                    imageView4 = eVar.f6241c;
                    i4 = R.drawable.icon_action_accept_disable;
                }
                imageView4.setImageResource(i4);
                i5 = R.string.permission_action_accept;
            } else if (intValue == 6) {
                eVar.f6241c.setImageResource(R.drawable.icon_action_foreground);
                i5 = R.string.permission_action_foreground;
            } else if (intValue != 7) {
                eVar.f6241c.setImageDrawable((Drawable) null);
            } else {
                eVar.f6241c.setImageResource(this.f6230a ? R.drawable.icon_action_virtual : R.drawable.icon_action_virtual_disable);
                i5 = R.string.permission_action_virtual;
            }
            if (i5 != 0) {
                eVar.f6241c.setContentDescription(this.f6232c.getString(i5));
            }
        }

        public void a(o.a aVar) {
            this.e = aVar;
        }

        public void a(ArrayList<com.miui.permcenter.a> arrayList) {
            this.f6233d.clear();
            this.f6233d.addAll(arrayList);
            notifyDataSetChanged();
        }

        public void a(boolean z) {
            this.f6230a = z;
        }

        public int getItemCount() {
            return this.f6233d.size();
        }

        @NonNull
        public e onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new e(LayoutInflater.from(this.f6232c).inflate(R.layout.pm_permission_apps_list_item_view, viewGroup, false));
        }
    }

    static class b extends b.b.c.i.a<u> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<PermissionAppsEditorActivity> f6234b;

        public b(PermissionAppsEditorActivity permissionAppsEditorActivity) {
            super(permissionAppsEditorActivity.getApplicationContext());
            this.f6234b = new WeakReference<>(permissionAppsEditorActivity);
        }

        public u loadInBackground() {
            Activity activity = (PermissionAppsEditorActivity) this.f6234b.get();
            if (isLoadInBackgroundCanceled() || activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return null;
            }
            u uVar = new u();
            uVar.f6296a = n.b(activity.getApplicationContext());
            ArrayList<com.miui.permcenter.a> a2 = n.a(activity.getApplicationContext(), activity.f6226a);
            if (!com.miui.permcenter.privacymanager.behaviorrecord.o.b((Context) activity) && activity.f6226a == 32 && Build.VERSION.SDK_INT >= 29) {
                ArrayList<com.miui.permcenter.a> a3 = n.a(activity.getApplicationContext(), (long) PermissionManager.PERM_ID_BACKGROUND_LOCATION);
                Iterator<com.miui.permcenter.a> it = a2.iterator();
                while (it.hasNext()) {
                    com.miui.permcenter.a next = it.next();
                    Iterator<com.miui.permcenter.a> it2 = a3.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        com.miui.permcenter.a next2 = it2.next();
                        if (next.e().equals(next2.e())) {
                            next.f().put(32L, Integer.valueOf(n.a(next.f().get(32L).intValue(), next2.f().get(Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION)).intValue())));
                            break;
                        }
                    }
                }
            }
            Collections.sort(a2, new v(activity.f6226a));
            uVar.f6297b = a2;
            return uVar;
        }
    }

    private static class c extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PermissionAppsEditorActivity> f6235a;

        c(PermissionAppsEditorActivity permissionAppsEditorActivity) {
            this.f6235a = new WeakReference<>(permissionAppsEditorActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Activity activity = (PermissionAppsEditorActivity) this.f6235a.get();
            if (!isCancelled() && activity != null && !activity.isFinishing() && !activity.isDestroyed() && activity.g != null) {
                Iterator it = activity.g.iterator();
                while (it.hasNext()) {
                    com.miui.permcenter.a aVar = (com.miui.permcenter.a) it.next();
                    aVar.b(com.miui.permcenter.privacymanager.behaviorrecord.o.a((Context) activity, aVar.e()));
                }
            }
            return null;
        }
    }

    static class d extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PermissionAppsEditorActivity> f6236a;

        /* renamed from: b  reason: collision with root package name */
        private ArrayList<String> f6237b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f6238c;

        public d(PermissionAppsEditorActivity permissionAppsEditorActivity, ArrayList<String> arrayList, boolean z) {
            this.f6236a = new WeakReference<>(permissionAppsEditorActivity);
            this.f6237b = arrayList;
            this.f6238c = z;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Activity activity = (PermissionAppsEditorActivity) this.f6236a.get();
            if (!isCancelled() && activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                String[] strArr = (String[]) this.f6237b.toArray(new String[0]);
                int i = this.f6238c ? 1 : 2;
                PermissionManager instance = PermissionManager.getInstance(activity.getApplicationContext());
                if (com.miui.permcenter.privacymanager.behaviorrecord.o.b((Context) activity) || Build.VERSION.SDK_INT < 29 || activity.f6226a != 32) {
                    PermissionManagerCompat.setApplicationPermissionWithVirtual(instance, activity.f6226a, i, 2, strArr);
                } else {
                    n.a(instance, i, strArr);
                }
            }
            return null;
        }
    }

    private static class e extends RecyclerView.u {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public TextView f6239a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public ImageView f6240b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public ImageView f6241c;

        public e(@NonNull View view) {
            super(view);
            this.f6239a = (TextView) view.findViewById(R.id.title);
            this.f6240b = (ImageView) view.findViewById(R.id.icon);
            this.f6241c = (ImageView) view.findViewById(R.id.action);
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        int i2;
        Long l;
        ArrayList arrayList = new ArrayList();
        Iterator<com.miui.permcenter.a> it = this.g.iterator();
        while (it.hasNext()) {
            com.miui.permcenter.a next = it.next();
            HashMap<Long, Integer> f2 = next.f();
            Integer num = f2.get(Long.valueOf(this.f6226a));
            if (num != null) {
                if (z) {
                    if (num.intValue() == 3 || num.intValue() == 2 || num.intValue() == 6 || num.intValue() == 7) {
                        arrayList.add(next.e());
                        l = Long.valueOf(this.f6226a);
                        i2 = 1;
                    }
                } else if (num.intValue() == 3 || num.intValue() == 1 || num.intValue() == 6 || num.intValue() == 7) {
                    arrayList.add(next.e());
                    l = Long.valueOf(this.f6226a);
                    i2 = 2;
                }
                f2.put(l, i2);
            }
        }
        this.f6227b.notifyDataSetChanged();
        if (!arrayList.isEmpty()) {
            this.i = new d(this, arrayList, z);
            this.i.execute(new Void[0]);
        }
    }

    private void b(boolean z) {
        Activity activity;
        ArrayList<com.miui.permcenter.a> arrayList = this.g;
        if (arrayList != null && arrayList.size() != 0 && (activity = (PermissionAppsEditorActivity) new WeakReference(this).get()) != null && !activity.isFinishing() && !activity.isDestroyed()) {
            new AlertDialog.Builder(activity).setTitle(z ? R.string.reject_all : R.string.prompt_all).setMessage(z ? R.string.confirm_reject_all_permission : R.string.confirm_prompt_all_permission).setPositiveButton(R.string.ok, new w(this, z)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        }
    }

    public void a(int i2, View view, com.miui.permcenter.a aVar) {
        Integer num = aVar.f().get(Long.valueOf(this.f6226a));
        Activity activity = (PermissionAppsEditorActivity) new WeakReference(this).get();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            n.a(activity, aVar.e(), this.f6226a, this.e, num.intValue(), new x(this, aVar), (this.h & 16) != 0, false, aVar.d(), aVar.a(this.f6226a), (this.h & 64) != 0);
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<u> loader, u uVar) {
        this.g = uVar.f6297b;
        ArrayList<com.miui.permcenter.a> arrayList = this.g;
        if (!(arrayList == null || arrayList.size() == 0)) {
            this.f6229d.setVisibility(8);
        }
        this.f6227b.a(uVar.f6296a);
        this.k = new c(this);
        this.k.execute(new Void[0]);
        this.f6227b.a(this.g);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, com.miui.permcenter.permissions.PermissionAppsEditorActivity, android.content.Context, android.app.LoaderManager$LoaderCallbacks, com.miui.permcenter.permissions.o$a, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_permission_apps);
        this.f6228c = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.app_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.j(1);
        this.f6228c.setLayoutManager(linearLayoutManager);
        this.f6229d = findViewById(R.id.empty_view);
        Intent intent = getIntent();
        this.f6226a = intent.getLongExtra("extra_permission_id", -1);
        if (this.f6226a == -1) {
            finish();
            return;
        }
        this.e = intent.getStringExtra("extra_permission_name");
        this.f = intent.getStringExtra("extra_permission_desc");
        this.h = intent.getIntExtra("extra_permission_flags", 0);
        setTitle(this.e);
        getActionBar().setTitle(this.e);
        this.f6227b = new a(this, this.f6226a);
        this.f6227b.a((o.a) this);
        this.f6228c.setAdapter(this.f6227b);
        getLoaderManager().initLoader(111, (Bundle) null, this);
    }

    public Loader<u> onCreateLoader(int i2, Bundle bundle) {
        this.j = new b(this);
        return this.j;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.perm_app_option, menu);
        MenuItem findItem = menu.findItem(R.id.prompt_all);
        if ((this.h & 16) == 0) {
            return true;
        }
        findItem.setVisible(false);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PermissionAppsEditorActivity.super.onDestroy();
        b.b.o.g.d.a("PermissionAppsEditorActivity", (Object) (InputMethodManager) getApplicationContext().getSystemService("input_method"), "windowDismissed", (Class<?>[]) new Class[]{IBinder.class}, getWindow().getDecorView().getWindowToken());
        d dVar = this.i;
        if (dVar != null) {
            dVar.cancel(true);
        }
        b.b.c.i.a<u> aVar = this.j;
        if (aVar != null) {
            aVar.cancelLoad();
        }
        c cVar = this.k;
        if (cVar != null) {
            cVar.cancel(true);
        }
    }

    public void onLoaderReset(Loader<u> loader) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.prompt_all) {
            b(false);
            return true;
        } else if (itemId != R.id.reject_all) {
            return PermissionAppsEditorActivity.super.onOptionsItemSelected(menuItem);
        } else {
            b(true);
            return true;
        }
    }
}
