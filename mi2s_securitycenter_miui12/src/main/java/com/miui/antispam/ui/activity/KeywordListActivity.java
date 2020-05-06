package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.app.Activity;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.BatchOperation;
import miui.provider.ExtraTelephony;
import miui.view.EditActionMode;

public class KeywordListActivity extends r implements LoaderManager.LoaderCallbacks<Cursor> {

    /* renamed from: d  reason: collision with root package name */
    private RecyclerViewExt f2546d;
    private TextView e;
    /* access modifiers changed from: private */
    public b f;
    private c g;
    private a h;
    /* access modifiers changed from: private */
    public BatchOperation i;
    /* access modifiers changed from: private */
    public Toast j;
    /* access modifiers changed from: private */
    public long k;
    /* access modifiers changed from: private */
    public String l;
    private MenuItem m;
    private int n;
    private boolean o;
    /* access modifiers changed from: private */
    public InputMethodManager p;
    /* access modifiers changed from: private */
    public HashSet<String> q = new HashSet<>();

    private class a extends Handler {
        private a() {
        }

        /* synthetic */ a(KeywordListActivity keywordListActivity, D d2) {
            this();
        }

        public void handleMessage(Message message) {
            String string = message.getData().getString("keyword");
            if (KeywordListActivity.this.j != null) {
                KeywordListActivity.this.j.cancel();
            }
            KeywordListActivity keywordListActivity = KeywordListActivity.this;
            Toast unused = keywordListActivity.j = Toast.makeText(keywordListActivity.getApplicationContext(), KeywordListActivity.this.getString(R.string.toast_keyword_exist, new Object[]{string}), 0);
            KeywordListActivity.this.j.show();
        }
    }

    private static class b extends RecyclerViewExt.c<C0037b> {
        protected List<Object> f;

        private static class a {

            /* renamed from: a  reason: collision with root package name */
            int f2548a;

            /* renamed from: b  reason: collision with root package name */
            String f2549b;

            private a(int i, String str) {
                this.f2548a = i;
                this.f2549b = str;
            }

            /* synthetic */ a(int i, String str, D d2) {
                this(i, str);
            }
        }

        /* renamed from: com.miui.antispam.ui.activity.KeywordListActivity$b$b  reason: collision with other inner class name */
        private static class C0037b extends RecyclerView.u {

            /* renamed from: a  reason: collision with root package name */
            public final TextView f2550a;

            /* renamed from: b  reason: collision with root package name */
            public final CheckBox f2551b;

            private C0037b(@NonNull View view) {
                super(view);
                this.f2550a = (TextView) view.findViewById(R.id.data);
                this.f2551b = (CheckBox) view.findViewById(16908289);
            }

            /* synthetic */ C0037b(View view, D d2) {
                this(view);
            }
        }

        private b() {
            this.f = new ArrayList();
        }

        /* synthetic */ b(D d2) {
            this();
        }

        public Object a(int i) {
            return this.f.get(i);
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull C0037b bVar, int i) {
            super.onBindViewHolder(bVar, i);
            bVar.f2550a.setText(((a) this.f.get(i)).f2549b);
            bVar.f2551b.setVisibility(this.e ? 0 : 8);
            bVar.itemView.setOnClickListener(new I(this, bVar, i));
            bVar.f2551b.setChecked(b(i));
        }

        public int getItemCount() {
            return this.f.size();
        }

        public long[] i() {
            SparseBooleanArray g = g();
            if (g.size() == 0) {
                return null;
            }
            long[] jArr = new long[g.size()];
            for (int i = 0; i < jArr.length; i++) {
                jArr[i] = (long) ((a) a(g.keyAt(i))).f2548a;
            }
            return jArr;
        }

        @NonNull
        public C0037b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new C0037b(LayoutInflater.from(d()).inflate(R.layout.fw_keyword_listitem, viewGroup, false), (D) null);
        }

        public void setData(List<Object> list) {
            this.f.clear();
            if (list != null) {
                this.f.addAll(list);
                notifyDataSetChanged();
            }
        }
    }

    private class c implements RecyclerViewExt.d {
        private c() {
        }

        /* synthetic */ c(KeywordListActivity keywordListActivity, D d2) {
            this();
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
        private void a(ActionMode actionMode) {
            new AlertDialog.Builder(KeywordListActivity.this).setTitle(R.string.dlg_delete_keyword).setPositiveButton(R.string.dlg_clear_current_ok, new J(this, actionMode, KeywordListActivity.this.f.i(), KeywordListActivity.this.f.g())).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }

        /* access modifiers changed from: private */
        public void a(long[] jArr, SparseBooleanArray sparseBooleanArray) {
            new K(this, sparseBooleanArray, jArr).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        /* JADX WARNING: type inference failed for: r5v1, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
        public void a(ActionMode actionMode, int i, boolean z) {
            int i2;
            EditActionMode editActionMode;
            KeywordListActivity.this.f.a((Context) KeywordListActivity.this, actionMode);
            if (!r.f2610a) {
                if (KeywordListActivity.this.f.f() == KeywordListActivity.this.f.getItemCount()) {
                    editActionMode = (EditActionMode) actionMode;
                    i2 = KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light;
                } else {
                    editActionMode = (EditActionMode) actionMode;
                    i2 = KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light;
                }
                editActionMode.setButton(16908314, (CharSequence) null, i2);
            }
        }

        /* JADX WARNING: type inference failed for: r1v4, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 16908313:
                    actionMode.finish();
                    break;
                case 16908314:
                    KeywordListActivity.this.f.a(!KeywordListActivity.this.f.h());
                    KeywordListActivity.this.f.a((Context) KeywordListActivity.this, actionMode);
                    if (!r.f2610a) {
                        ((EditActionMode) actionMode).setButton(16908314, (CharSequence) null, !KeywordListActivity.this.f.h() ? KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light : KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light);
                        break;
                    }
                    break;
                case R.id.edit_mode_delete /*2131296738*/:
                    a(actionMode);
                    break;
            }
            return true;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            KeywordListActivity.this.f.b();
            KeywordListActivity.this.getMenuInflater().inflate(R.menu.list_view_edit_mode_menu, menu);
            menu.findItem(R.id.edit_mode_white).setVisible(false);
            if (r.f2610a) {
                return true;
            }
            EditActionMode editActionMode = (EditActionMode) actionMode;
            editActionMode.setButton(16908313, (CharSequence) null, KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_cancel_dark : miui.R.drawable.action_mode_title_button_cancel_light);
            editActionMode.setButton(16908314, (CharSequence) null, KeywordListActivity.this.b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light);
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            KeywordListActivity.this.f.c();
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void a(ArrayList<String> arrayList) {
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (this.q.contains(next)) {
                Message obtainMessage = this.h.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("keyword", next);
                obtainMessage.setData(bundle);
                obtainMessage.sendToTarget();
            } else {
                ContentProviderOperation.Builder newInsert = ContentProviderOperation.newInsert(ExtraTelephony.Keyword.CONTENT_URI);
                newInsert.withValue(DataSchemeDataSource.SCHEME_DATA, next);
                newInsert.withValue("type", Integer.valueOf(this.o ? 1 : 4));
                newInsert.withValue(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, Integer.valueOf(this.n));
                this.i.add(newInsert.build());
                if (this.i.size() > 100) {
                    this.i.execute();
                }
            }
        }
        if (this.i.size() > 0) {
            this.i.execute();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
    private void c() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.fw_input_dialog, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(R.id.edit_text);
        new AlertDialog.Builder(this).setView(inflate).setTitle(R.string.dlg_add_keyword).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.dlg_keyword_ok, new G(this, editText)).show();
        new Handler().postDelayed(new H(this, editText), 200);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
    private void d() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.fw_input_dialog, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(R.id.edit_text);
        editText.setText(this.l);
        new AlertDialog.Builder(this).setView(inflate).setTitle(R.string.dlg_edit_keyword).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.dlg_keyword_ok, new E(this, editText)).show();
        new Handler().postDelayed(new F(this, editText), 200);
    }

    public List<Object> a(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new b.a(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA)), (D) null));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int i2;
        RecyclerViewExt recyclerViewExt;
        if (cursor != null) {
            this.q.clear();
            while (cursor.moveToNext()) {
                try {
                    this.q.add(cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA)));
                } catch (Exception e2) {
                    Log.e("KeywordListActivity", "Cursor err when caching keywords: ", e2);
                }
            }
            this.f.setData(a(cursor));
            if (this.f.getItemCount() == 0) {
                recyclerViewExt = this.f2546d;
                i2 = 8;
            } else {
                recyclerViewExt = this.f2546d;
                i2 = 0;
            }
            recyclerViewExt.setVisibility(i2);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antispam.ui.activity.KeywordListActivity] */
    public boolean onContextItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 3) {
            d();
            return true;
        } else if (itemId != 4) {
            return true;
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.dlg_delete_keyword).setPositiveButton(R.string.dlg_clear_current_ok, new D(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
            return true;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, android.app.LoaderManager$LoaderCallbacks, com.miui.antispam.ui.activity.r, miui.app.Activity, com.miui.antispam.ui.activity.KeywordListActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        if (r.f2610a) {
            setTheme(2131821040);
        }
        super.onCreate(bundle);
        setContentView(R.layout.fw_keyword_list);
        this.p = (InputMethodManager) getSystemService("input_method");
        this.n = getIntent().getIntExtra("key_sim_id", 1);
        this.o = getIntent().getBooleanExtra("is_black", true);
        setTitle(this.o ? R.string.st_title_keywords_blacklist : R.string.st_title_keywords_whitelist);
        this.f2546d = (RecyclerViewExt) findViewById(16908298);
        this.e = (TextView) findViewById(R.id.text1);
        this.e.setText(this.o ? R.string.st_filter_keywords_setting_summary1 : R.string.st_filter_keywords_setting_summary3);
        this.g = new c(this, (D) null);
        this.f = new b((D) null);
        this.f.a((Activity) this, (RecyclerViewExt.d) this.g);
        this.f2546d.setLayoutManager(new LinearLayoutManager(this));
        this.f2546d.setAdapter(this.f);
        registerForContextMenu(this.f2546d);
        getLoaderManager().initLoader(0, (Bundle) null, this);
        this.h = new a(this, (D) null);
        this.i = new BatchOperation(getContentResolver(), "antispam");
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        b.a aVar = (b.a) this.f.a(((RecyclerViewExt.e) contextMenuInfo).f2646a);
        this.k = (long) aVar.f2548a;
        this.l = aVar.f2549b;
        contextMenu.setHeaderTitle(this.l);
        contextMenu.add(0, 3, 0, getString(R.string.menu_edit));
        contextMenu.add(0, 4, 0, getString(R.string.menu_remove));
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.Loader<android.database.Cursor> onCreateLoader(int r8, android.os.Bundle r9) {
        /*
            r7 = this;
            android.content.CursorLoader r8 = new android.content.CursorLoader
            android.net.Uri r2 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            r9 = 2
            java.lang.String[] r5 = new java.lang.String[r9]
            boolean r9 = r7.o
            r0 = 1
            if (r9 == 0) goto L_0x000e
            r9 = r0
            goto L_0x000f
        L_0x000e:
            r9 = 4
        L_0x000f:
            java.lang.String r9 = java.lang.String.valueOf(r9)
            r1 = 0
            r5[r1] = r9
            int r9 = r7.n
            java.lang.String r9 = java.lang.String.valueOf(r9)
            r5[r0] = r9
            r6 = 0
            r3 = 0
            java.lang.String r4 = "type = ? AND sim_id = ? "
            r0 = r8
            r1 = r7
            r0.<init>(r1, r2, r3, r4, r5, r6)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.KeywordListActivity.onCreateLoader(int, android.os.Bundle):android.content.Loader");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.m = menu.add(0, 0, 0, R.string.menu_add).setIcon(b() ? miui.R.drawable.action_button_new_dark : miui.R.drawable.action_button_new_light);
        this.m.setShowAsAction(2);
        return true;
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.f.setData((List<Object>) null);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 0) {
            return KeywordListActivity.super.onOptionsItemSelected(menuItem);
        }
        c();
        return true;
    }
}
